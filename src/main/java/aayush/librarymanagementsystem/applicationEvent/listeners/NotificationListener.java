package aayush.librarymanagementsystem.applicationEvent.listeners;

import aayush.librarymanagementsystem.applicationEvent.events.NotificationEvent;
import aayush.librarymanagementsystem.service.NotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NotificationListener {
    private final NotificationService notificationService;

    @Async
    @EventListener
//    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT)
    public void onNotificationSendEvent(NotificationEvent event) {
        // Send email
        notificationService.sendNotification(event.getAccount().getEmail(), event.getSubject(), event.getContent());
        log.info("Notification sent to {}", event.getAccount().getEmail());
        log.info("Subject: {}", event.getSubject());
    }
}