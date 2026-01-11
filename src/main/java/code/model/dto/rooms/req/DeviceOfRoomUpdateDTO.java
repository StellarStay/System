package code.model.dto.rooms.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfRoomUpdateDTO {
    private String roomId;
    private String deviceId; // Chỉ một device cho update
}

