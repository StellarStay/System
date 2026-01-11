package code.model.dto.rooms.res;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoomResponseDTO {
    @Id
    private String roomId;
    private String roomName;
    private String title;
    private String description;
    private String address;
    private BigDecimal price_per_night;
    private int max_guests;
    private String status; // PENDING, APPROVED, REJECTED, BLOCKED
//    private CategoriesRoomEntity category;
    private String categoryName;
//    private UserEntity owner; // owner có nghĩa là chủ phòng
    private String ownerName;
}
