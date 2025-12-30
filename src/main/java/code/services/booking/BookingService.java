package code.services.booking;

import code.model.dto.booking.GuestBookingRequestDTO;
import code.model.dto.booking.UserBookingRequestDTO;
import code.model.dto.booking.BookingResponseDTO;
import code.model.dto.rooms.RoomResponseDTO;
import code.model.entity.booking.BookingEntity;

import java.time.LocalDateTime;
import java.util.List;

public interface BookingService {
    String prepareGuestBooking(GuestBookingRequestDTO guestBookingRequestDTO);
    String prepareUserBooking(UserBookingRequestDTO bookingRequestDTO, String userId);

    BookingEntity insertBookingFromTemp(String tempBookingId);


    boolean updateActualCheckInTime(String bookingId, UserBookingRequestDTO bookingRequestDTO);
    boolean updateActualCheckOutTime(String bookingId, UserBookingRequestDTO bookingRequestDTO);

    boolean cancelBooking(String bookingId);


    List<BookingResponseDTO> getAllBookings();
    BookingEntity getBookingById(String bookingId);
    BookingResponseDTO getBookingResponseById(String bookingId);

    boolean checkRoomAvailability(String roomId, LocalDateTime checkInTime, LocalDateTime checkOutTime);


}
