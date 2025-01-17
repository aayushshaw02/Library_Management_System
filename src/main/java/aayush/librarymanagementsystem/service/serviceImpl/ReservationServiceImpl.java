package aayush.librarymanagementsystem.service.serviceImpl;

import aayush.librarymanagementsystem.applicationEvent.events.NotificationEvent;
import aayush.librarymanagementsystem.dto.Account;
import aayush.librarymanagementsystem.dto.Book;
import aayush.librarymanagementsystem.dto.BookReservation;
import aayush.librarymanagementsystem.dto.ReservationStatus;
import aayush.librarymanagementsystem.entity.AccountEntity;
import aayush.librarymanagementsystem.entity.BookEntity;
import aayush.librarymanagementsystem.entity.BookReservationEntity;
import aayush.librarymanagementsystem.property.ConfigurationProperty;
import aayush.librarymanagementsystem.repository.BookReservationRepository;
import aayush.librarymanagementsystem.service.LibraryService;
import aayush.librarymanagementsystem.service.ReservationService;
import aayush.librarymanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
public class ReservationServiceImpl implements ReservationService {

    private ConfigurationProperty props;
    private BookReservationRepository reservationRepository;
    private UserService userService;
    private LibraryService libraryService;
    private ApplicationEventPublisher publisher;

    @Override
    public List<BookReservation> getReservationsByAccountId(long accountId) {
        return reservationRepository.findByAccountId(accountId).stream().map(BookReservationEntity::toDto).toList();
    }

    @Override
    @Transactional
    public void deleteReservationsByAccountId(long accountId) {
        List<BookReservation> reservations = getReservationsByAccountId(accountId);
        reservations.forEach(
                reservation -> libraryService.returnBook(reservation.getBook().getId())
        );
        reservationRepository.deleteByAccountId(accountId);
    }

    @Override
    @Transactional
    public void deleteReservationByBookIdAndAccountId(long bookId, long accountId) {
        libraryService.returnBook(bookId);
        reservationRepository.deleteByBookIdAndAccountId(bookId,accountId);
    }

    @Override
    @Transactional
    public Optional<BookReservation> deleteReservationById(long reservationId) {
        Optional<BookReservationEntity> reservation = reservationRepository.findById(reservationId);
        if (reservation.isPresent()) {
            libraryService.returnBook(reservation.get().getBook().getId());
            reservationRepository.deleteById(reservationId);

            // Send notification
            sendNotification(
                    "Book reservation cancelled",
                    "You have successfully cancelled the reservation for the book " + reservation.get().getBook().getTitle(),
                    reservation.get().getAccount().toDto());

            return Optional.of(reservation.get().toDto());
        }

        return Optional.empty();
    }

    @Override
    @Transactional
    public boolean addReservation(long bookId, long accountId) {
        if (reservationRepository.existsByAccount_IdAndBook_Id(accountId, bookId)) {
            // Reservation already exists
            return false;
        }

        Account account = userService.getAccountById(accountId).orElse(null);
        Book book = libraryService.reserveBook(bookId).orElse(null);

        if (account == null || book == null) {
            return false;
        }

        BookReservationEntity reservation = new BookReservationEntity();

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        reservation.setStatus(ReservationStatus.RESERVED);
        reservation.setReservationDate(calendar.getTime());

        // Add 7 days to the current date
        calendar.add(Calendar.DATE, props.getMaxDaysToReturnBook());
        reservation.setDueDate(calendar.getTime());

        reservation.setBook(BookEntity.fromDto(book));
        reservation.setAccount(AccountEntity.fromDto(account));

        reservationRepository.save(reservation);

        // Send notification
        sendNotification(
                "Book reservation successful",
                "You have successfully reserved the book " + book.getTitle(),
                account);

        return true;
    }

    @Override
    public void returnBook(long bookId, long accountId) {
        BookReservationEntity reservation = reservationRepository.findByBookIdAndAccountId(bookId, accountId).orElse(null);

        if (reservation == null) {
            throw new IllegalArgumentException("Reservation not found");
        }

        libraryService.returnBook(bookId);

        reservationRepository.deleteByBookIdAndAccountId(bookId, accountId);

        // Send notification
        sendNotification(
                "Book returned",
                "You have successfully returned the book " + reservation.getBook().getTitle(),
                reservation.getAccount().toDto());
    }

    @Override
    public Optional<BookReservation> updateReservationStatus(long bookId, long accountId, ReservationStatus status) {
        BookReservationEntity reservation = reservationRepository.findByBookIdAndAccountId(bookId, accountId).orElse(null);

        if (reservation == null) {
            return Optional.empty();
        }

        reservation.setStatus(status);
        reservationRepository.save(reservation);

        return Optional.of(reservation.toDto());
    }

    @Override
    public Optional<BookReservation> updateReservationStatus(long reservationId, ReservationStatus status) {
        BookReservationEntity reservation = reservationRepository.findById(reservationId).orElse(null);

        if (reservation == null) {
            return Optional.empty();
        }

        reservation.setStatus(status);
        reservationRepository.save(reservation);

        return Optional.of(reservation.toDto());
    }

    @Override
    public List<BookReservation> getOverdueReservations() {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        return reservationRepository.getOverdueBookReservations(calendar.getTime())
                .stream().map(BookReservationEntity::toDto).toList();
    }

    @Override
    public Optional<BookReservation> renewReservation(long reservationId) {
        BookReservationEntity reservation = reservationRepository.findById(reservationId).orElse(null);

        if (reservation == null || reservation.getStatus().equals(ReservationStatus.RESERVED)) {
            return Optional.empty();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(reservation.getDueDate());
        calendar.add(Calendar.DATE, props.getMaxDaysToReturnBook());
        reservation.setDueDate(calendar.getTime());

        reservation.setStatus(ReservationStatus.RESERVED);

        reservationRepository.save(reservation);

        // Send notification
        sendNotification(
                "Book renewal successful",
                "You have successfully renewed the book " + reservation.getBook().getTitle(),
                reservation.getAccount().toDto());

        return Optional.of(reservation.toDto());
    }

    private void sendNotification(String subject, String content, Account account) {
        publisher.publishEvent(new NotificationEvent(account, subject, content));
    }
}
