package aayush.librarymanagementsystem.applicationEvent.events;


import aayush.librarymanagementsystem.dto.Account;
import lombok.Data;

@Data
public class NotificationEvent {
    private final Account account;
    private final String subject;
    private final String content;
}
