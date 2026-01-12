package code.repository.rooms;

import code.model.entity.rooms.ImageRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ImageRoomRepository extends JpaRepository<ImageRoomEntity,String> {
    List<ImageRoomEntity> findByRoom_RoomId(String roomId);

    @Query("""
    SELECT img FROM ImageRoomEntity img
    WHERE img.room.roomId = :roomId AND img.isThumbnail = true
    """)
    ImageRoomEntity findThumbnailOfRoom(String roomId);
}
