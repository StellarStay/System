package code.model.entity.rooms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name = "device_of_room")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfRoomEntity {
    @Id
    @Column(name = "device_of_room_id", nullable = false)
    private String deviceOfRoomId;
    @Column(name = "status", nullable = false)
    private boolean status;
    @Column(name = "created_date", nullable = false)
    private LocalDateTime createdDate;


    @ManyToOne
    @JoinColumn(name = "device_id", nullable = false)
    private DevicesEntity device;
    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;
}
