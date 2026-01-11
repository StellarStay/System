package code.model.dto.rooms.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfRoomResponseDTO {
    private String deviceName;
    private boolean status;
}
