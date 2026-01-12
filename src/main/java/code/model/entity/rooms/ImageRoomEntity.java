package code.model.entity.rooms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "image_rooms")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageRoomEntity {
    @Id
    @Column(name = "image_id", nullable = false)
    private String imageId;
    @Column(name = "image_url", nullable = false)
    private String imageUrl;
    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;
    @Column(name = "is_thumbnail", nullable = false)
    private Boolean isThumbnail = false;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;
}
