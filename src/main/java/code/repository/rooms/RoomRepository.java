package code.repository.rooms;

import code.model.entity.rooms.RoomEntity;
import io.lettuce.core.dynamic.annotation.Param;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface RoomRepository extends JpaRepository<RoomEntity,String> {

    @Query("""
    SELECT r FROM RoomEntity r
    WHERE NOT EXISTS (
        SELECT b FROM BookingEntity b
        WHERE b.room.roomId = r.roomId
          AND b.status IN ('CONFIRMED', 'PENDING')
          AND(
            (b.planCheckInTime > :checkIn AND b.planCheckInTime < :checkOut)
            OR (b.planCheckOutTime > :checkIn AND b.planCheckOutTime < :checkOut)
            OR (b.planCheckInTime <= :checkIn AND b.planCheckOutTime >= :checkOut)
          )
    )
    """)
    List<RoomEntity> findAvailableRooms(@Param("checkIn") LocalDateTime checkIn, @Param("checkOut") LocalDateTime checkOut);

}
