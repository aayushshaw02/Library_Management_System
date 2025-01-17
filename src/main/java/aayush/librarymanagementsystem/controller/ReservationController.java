package aayush.librarymanagementsystem.controller;

import aayush.librarymanagementsystem.dto.Account;
import aayush.librarymanagementsystem.dto.BookReservation;
import aayush.librarymanagementsystem.service.ReservationService;
import aayush.librarymanagementsystem.service.UserService;
import org.springframework.security.access.annotation.Secured;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.security.Principal;
import java.util.Optional;

@Controller
@RequestMapping("/reservations")
public class ReservationController {
    private final ReservationService reservationService;
    private final UserService userService;

    public ReservationController(ReservationService reservationService, UserService userService) {
        this.reservationService = reservationService;
        this.userService = userService;
    }

    @Secured("ROLE_USER")
    @PostMapping("/{bookId}")
    public String reserveBook(@PathVariable long bookId, Principal principal) {
        Account account = userService.getAccountByEmail(principal.getName()).orElse(null);

        if (account == null) {
            return "error";
        }

        boolean reserved = reservationService.addReservation(bookId, account.getId());

        if (reserved) {
            return "redirect:/library/books/" + bookId;
        }

        String error = "Book is already reserved";
        return "redirect:/library/books/" + bookId + "?error=" + error;
    }

    @Secured("ROLE_LIBRARIAN")
    @PostMapping("/renew/{id}")
    public String renewReservation(@PathVariable("id") long reservationId) {
        BookReservation reservation = reservationService.renewReservation(reservationId).orElse(null);

        if (reservation == null) {
            return "error";
        }

        return "redirect:/users/" + reservation.getAccount().getId();
    }

    @Secured("ROLE_LIBRARIAN")
    @DeleteMapping("/delete/{id}")
    public String deleteReservation(@PathVariable("id") long reservationId) {
        Optional<BookReservation> reservation = reservationService.deleteReservationById(reservationId);
        return reservation.map(bookReservation ->
                        "redirect:/users/" + bookReservation.getAccount().getId())
                .orElse("error");
    }
}
