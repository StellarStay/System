package code.model.dto.rooms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfRoomRequestDTO {
    private String roomId;
    private String deviceId;
    private boolean status;
}
