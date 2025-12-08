package code.model.dto.rooms;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceRequestDTO {
    private String deviceName;
    private String deviceType;
    private String brand;
}
