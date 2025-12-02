package code.model.entity.users;

import code.model.entity.notification.ChatMessageEntity;
import code.model.entity.notification.NotificationEntity;
import code.model.entity.rooms.RoomEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "users")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserEntity {
    @Id
    @Column(name = "user_id", nullable = false)
    private String userId;
    @Column(name = "id_card", nullable = false, unique = true)
    private String idCard;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "date_of_birth", nullable = false)
    private LocalDateTime dateOfBirth;
    @Column(name = "phone", nullable = false)
    private String phone;
    @Column(name = "email", nullable = false, unique = true)
    private String email;
    @Column(name = "password", nullable = false)
    private String password;
    @Column(name = "gender", nullable = false)
    private boolean gender;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "status", nullable = false)
    private boolean status;

    @ManyToOne
    @JoinColumn(name = "role_id", nullable = false)
    private RoleEntity role;

    @OneToMany(mappedBy = "owner")
    private List<RoomEntity> rooms;

    @OneToMany(mappedBy = "sender")
    private List<ChatMessageEntity> chatMessages;

    @OneToMany(mappedBy = "receiver")
    private List<ChatMessageEntity> receivedMessages;

    @OneToMany(mappedBy = "userReceiver")
    private List<NotificationEntity> notifications;



}
