package code.model.dto.rooms.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class DeviceOfRoomRequestDTO {
    private String roomId;
    private List<String> deviceIds; // Thay đổi từ String sang List<String>
    private boolean status;
}
