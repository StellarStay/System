package code.repository.rooms;

import code.model.entity.rooms.DeviceOfRoomEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceOfRoomRepository extends JpaRepository<DeviceOfRoomEntity, String> {
}
