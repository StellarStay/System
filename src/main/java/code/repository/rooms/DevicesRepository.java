package code.repository.rooms;

import code.model.entity.rooms.DevicesEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DevicesRepository extends JpaRepository<DevicesEntity, String> {
}
