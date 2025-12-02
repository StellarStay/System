package code.model.entity.notification;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "notification_types")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificationTypeEntity {
    @Id
    @Column(name = "type_id", nullable = false)
    private String typeId;
    @Column(name = "type_name", nullable = false)
    private String typeName;

    @OneToMany(mappedBy = "notificationType")
    private List<NotificationEntity> notifications;
}
