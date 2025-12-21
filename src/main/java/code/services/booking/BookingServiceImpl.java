package code.services.booking;

import code.model.dto.booking.BookingRequestDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import code.repository.booking.BookingRepository;
import code.services.rooms.RoomsService;
import code.services.users.UserService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomsService roomsService;

    int max_length_booking_id = 10;
    private String generateBookingId() {
        String bookingId;
        do {
            bookingId = RandomId.generateRoomId(max_length_booking_id);
        }while (bookingRepository.existsById(bookingId));
        return bookingId;
    }

    @Override
    public boolean insertBooking(BookingRequestDTO bookingRequestDTO) {
        // Khi insert một booking thì trước đó phải kiểm tra trạng thái phòng dựa trong lịch booking xem có bị đè lên không --> nếu có thì không cho insert
        // Kiểm tra user(đã có tài khoản) và phòng có tồn tại hay không
        // Tính được giá tiền dựa trên ngày đặt phòng

        RoomEntity roomBooked = roomsService.getRoomById(bookingRequestDTO.getRoomId());
        UserEntity userBook = userService.getUser(bookingRequestDTO.getUserId());

        if (userBook == null ||  roomBooked == null) {
            return false;
        }

        boolean isRoomAvailable = checkRoomAvailability(bookingRequestDTO.getRoomId(), bookingRequestDTO.getCheckInTime(), bookingRequestDTO.getCheckOutTime());
        if (!isRoomAvailable) {
            return false;
        }
        // Tính số đêm
        int nightForBook = Math.toIntExact(ChronoUnit.DAYS.between(bookingRequestDTO.getCheckInTime(), bookingRequestDTO.getCheckOutTime()));
        // Tính tiền = tiền/đêm * số đêm
        BigDecimal priceForBook = (BigDecimal.valueOf(nightForBook).multiply(roomBooked.getPrice_per_night()));
        // Tính giá cho booking
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setBookingId(generateBookingId());
        bookingEntity.setRoom(roomBooked);
        bookingEntity.setUser(userBook);
        bookingEntity.setCheckInTime(bookingRequestDTO.getCheckInTime());
        bookingEntity.setCheckOutTime(bookingRequestDTO.getCheckOutTime());
        bookingEntity.setStatus("PENDING");
        bookingEntity.setTotalPrice(priceForBook);

        bookingRepository.save(bookingEntity);
        return true;
    }

    @Override
    public boolean updateBooking(String bookingId, BookingRequestDTO bookingRequestDTO) {
        // Phần update này chỉ dành cho Host/Admin và nó chỉ được update khi nào mà khách hàng đã book và đã chuyển tiền thành công thì mới được update từ PENDING --> CONFIRMED

        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        RoomEntity roomBooked = roomsService.getRoomById(bookingRequestDTO.getRoomId());
        UserEntity userBook = userService.getUser(bookingRequestDTO.getUserId());
        if (bookingEntity == null) {
            return false;
        }
        bookingEntity.setStatus(bookingRequestDTO.getStatus());
        bookingEntity.setCheckInTime(bookingRequestDTO.getCheckInTime());
        bookingEntity.setCheckOutTime(bookingRequestDTO.getCheckOutTime());
        bookingEntity.setStatus(bookingRequestDTO.getStatus());
        bookingEntity.setRoom(roomBooked);
        bookingEntity.setUser(userBook);
        bookingRepository.save(bookingEntity);
        return true;
    }

    @Override
    public boolean cancelBooking(String bookingId) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            return false;
        }
        bookingEntity.setStatus("CANCELLED");
        bookingRepository.save(bookingEntity);
        return true;
    }

    @Override
    public List<BookingEntity> getAllBookings() {
        return bookingRepository.findAll();
    }

    @Override
    public BookingEntity getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public boolean checkRoomAvailability(String roomId, LocalDateTime checkInTime, LocalDateTime checkOutTime) {
        // Muốn check thì đầu tiên phải lấy phòng ở trong toàn bộ booking ra
        // Sau đó dựa vào tất cả trong booking đối với phòng đó để kiểm tra xem có bị trùng thời gian không ?
        // Nếu trùng thời gian nhưng trạng thái là CANCELLED thì vẫn được duyệt qua và cho đặt
        // Ngược lại nếu trùng thời gian và trạng thái là PENDING hoặc CONFIRMED thì không cho đặt
        // Chỉ duy nhất trạng thái CANCELLED(có thể trùng thời gian) hoặc không trùng thời gian mới được phép đặt
        RoomEntity roomToCheck = roomsService.getRoomById(roomId);
        if (roomToCheck == null) {
            return false;
        }
        List<BookingEntity> bookingsOfRoom = bookingRepository.findByRoom_RoomId(roomId);
        for (BookingEntity booking : bookingsOfRoom) {
            boolean checkOverLapping =  (checkInTime.isAfter(booking.getCheckInTime()) && checkInTime.isBefore(booking.getCheckOutTime()))  ||
                    (checkOutTime.isAfter(booking.getCheckInTime()) && checkOutTime.isBefore(booking.getCheckOutTime())) ||
                    (checkInTime.isBefore(booking.getCheckInTime()) && checkOutTime.isAfter(booking.getCheckOutTime()));

            if(booking.getStatus().equals("CANCELLED") || booking.getStatus().equals("COMPLETED")) {
                continue;
            }

            else if (checkOverLapping && (booking.getStatus().equals("PENDING") || booking.getStatus().equals("CONFIRMED"))) {
                return false;
            }
        }
        return true;
    }
}

// Có một trường hợp khi khách booking, nó sẽ ghi nhận vào trong booking với trạng thái PENDING
// Khi khách hoàn tất thông tin nhưng không thanh toán hoặc thoát ra khỏi trang web thì trạng thái sẽ phải tự động chuyển về CANCELLED sau khoảng 2'
