package code.repository.rooms;

import code.model.entity.rooms.DeviceOfRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceOfRoomRepository extends JpaRepository<DeviceOfRoomEntity, String> {
    List<DeviceOfRoomEntity> findByRoom_RoomId(String roomId);
}
