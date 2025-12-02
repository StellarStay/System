package code.model.entity.notification;

import code.model.entity.users.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "notifications")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationEntity {
    @Id
    @Column(name = "notification_id", nullable = false)
    private String notificationId;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "message", nullable = false)
    private String message;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "user_receiver_id", nullable = false)
    private UserEntity userReceiver;

    @ManyToOne
    @JoinColumn(name = "type_id", nullable = false)
    private NotificationTypeEntity notificationType;
}
