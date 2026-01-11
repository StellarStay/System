package code.model.dto.rooms.res;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceResponseDTO {
    private String deviceName;
    private String deviceType;
    private String brand;
}
