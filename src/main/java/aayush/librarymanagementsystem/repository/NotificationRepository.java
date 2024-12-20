package aayush.librarymanagementsystem.repository;

import aayush.librarymanagementsystem.entity.NotificationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<NotificationEntity, Long>{
    List<NotificationEntity> findByAccount_Email(String recipientEmail);
}
