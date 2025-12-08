package code.repository.booking;

import code.model.entity.booking.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, String> {
    List<BookingEntity> findByRoomId(String roomId);
}
