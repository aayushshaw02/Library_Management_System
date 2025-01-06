package aayush.librarymanagementsystem.service.serviceImpl;

import aayush.librarymanagementsystem.dto.Notification;
import aayush.librarymanagementsystem.entity.AccountEntity;
import aayush.librarymanagementsystem.entity.NotificationEntity;
import aayush.librarymanagementsystem.repository.NotificationRepository;
import aayush.librarymanagementsystem.service.NotificationService;
import aayush.librarymanagementsystem.service.UserService;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

@Service
@AllArgsConstructor
public class NotificationServiceImpl implements NotificationService {
    @Autowired
    private final NotificationRepository notificationRepository;
    @Autowired
    private final UserService userService;

    @Override
    public void sendNotification(String accountEmail, String subject, String content) {
        var notification = new NotificationEntity(
                null,
                subject,
                content,
                new Date(),
                AccountEntity.fromDto(userService.getAccountByEmail(accountEmail).orElse(null))
        );

        notificationRepository.save(notification);
    }

    @Override
    public List<Notification> getNotifications(String accountEmail) {
        var account = userService.getAccountByEmail(accountEmail).orElse(null);

        if (account != null) {
            return notificationRepository
                    .findByAccount_Email(account.getEmail())
                    .stream().map(NotificationEntity::toDto).toList();
        }

        return List.of();
    }
}
