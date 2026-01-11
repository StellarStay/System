package code.model.dto.rooms.req;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomRequestDTO {
    private String roomName;
    private String title;
    private String description;
    private String address;
    private BigDecimal price_per_night;
    private int max_guests;
    private String status; // PENDING, APPROVED, REJECTED, BLOCKED, Nếu là chủ phòng create phòng thì luôn là trạng thái PENDING, chờ admin duyệt rồi mới chuyển qua các trạng thái còn lại
    private String categoriesId;
    private String ownerId; // owner có nghĩa là chủ phòng
}
