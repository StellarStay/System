package code.services.booking;

import code.model.dto.booking.BookingRequestDTO;
import code.model.entity.booking.BookingEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    boolean insertBooking(BookingRequestDTO bookingRequestDTO);
    boolean updateBooking(String bookingId, BookingRequestDTO bookingRequestDTO);
    boolean cancelBooking(String bookingId);
    List<BookingEntity> getAllBookings();
    BookingEntity getBookingById(String bookingId);

    boolean checkRoomAvailability(String roomId, LocalDateTime checkInTime, LocalDateTime checkOutTime);


}
