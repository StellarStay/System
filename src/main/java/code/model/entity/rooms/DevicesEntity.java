package code.model.entity.rooms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "devices")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DevicesEntity {
    @Id
    @Column(name = "device_id", nullable = false)
    private String deviceId;
    @Column(name = "device_name", nullable = false)
    private String deviceName;
    @Column(name = "device_type", nullable = false)
    private String deviceType;
    @Column(name = "brand", nullable = false)
    private String brand;

    @OneToMany(mappedBy = "device")
    private List<DeviceOfRoomEntity> devicesInRoom;
}
