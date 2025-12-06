package code.repository.rooms;

import code.model.entity.rooms.ImageRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageRoomRepository extends JpaRepository<ImageRoomEntity,String> {
}
