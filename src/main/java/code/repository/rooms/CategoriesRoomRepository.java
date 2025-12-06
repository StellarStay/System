package code.repository.rooms;

import code.model.entity.rooms.CategoriesRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoriesRoomRepository extends JpaRepository<CategoriesRoomEntity,String> {
}
