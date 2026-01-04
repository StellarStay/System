package code.model.dto.rooms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfRoomResponseDTO {
    private String deviceName;
    private boolean status;
}
