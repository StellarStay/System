package code.model.entity.rooms;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "categories_room")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class CategoriesRoomEntity {
    @Id
    @Column(name = "category_id", nullable = false)
    private String categoryId;
    @Column(name = "category_name", nullable = false, unique = true)
    private String categoryName;

    @OneToMany(mappedBy = "category")
    private List<RoomEntity> rooms;
}
