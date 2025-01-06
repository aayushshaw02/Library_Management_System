package aayush.librarymanagementsystem.service;

import aayush.librarymanagementsystem.dto.Notification;

import java.util.List;

public interface NotificationService {
    void sendNotification(String accountEmail, String subject, String content);

    List<Notification> getNotifications(String accountEmail);
}
