package code.model.entity.rooms;

import code.model.entity.booking.BookingEntity;
import code.model.entity.notification.ChatMessageEntity;
import code.model.entity.users.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomEntity {
    @Id
    @Column(name = "room_id", nullable = false)
    private String roomId;
    @Column(name = "room_name", nullable = false)
    private String roomName;
    @Column(name = "title", nullable = false)
    private String title;
    @Column(name = "description", nullable = false)
    private String description;
    @Column(name = "address", nullable = false)
    private String address;
    @Column(name = "price_per_night", nullable = false)
    private BigDecimal price_per_night;
    @Column(name = "max_guests", nullable = false)
    private int max_guests;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "status", nullable = false)
    private String status; // PENDING, APPROVED, REJECTED, BLOCKED

    @ManyToOne
    @JoinColumn(name = "category_id", nullable = false)
    private CategoriesRoomEntity category;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private UserEntity owner; // owner có nghĩa là chủ phòng

    @OneToMany(mappedBy = "room")
    private List<ImageRoomEntity> imageRooms;

    @OneToMany(mappedBy = "room")
    private List<BookingEntity> bookings;

    @OneToMany
    private List<ChatMessageEntity> chatMessages;

}
