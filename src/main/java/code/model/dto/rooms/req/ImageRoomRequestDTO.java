package code.model.dto.rooms.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ImageRoomRequestDTO {
    private String imageUrl;
    private String roomId;
}
