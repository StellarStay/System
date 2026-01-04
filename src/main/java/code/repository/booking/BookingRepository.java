package code.repository.booking;

import code.model.entity.booking.BookingEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BookingRepository extends JpaRepository<BookingEntity, String> {
    // RoomEntity primary key field is `roomId`, so the derived query must traverse room.
    List<BookingEntity> findByRoom_RoomId(String roomId);
    List<BookingEntity> findByUser_UserId(String userId);
}
