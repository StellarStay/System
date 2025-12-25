package code.services.booking;

import code.model.dto.booking.GuestBookingRequestDTO;
import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.booking.UserBookingRequestDTO;
import code.model.dto.booking.BookingResponseDTO;
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
import java.util.ArrayList;
import java.util.List;

@Service
public class BookingServiceImpl implements BookingService {
    @Autowired
    private BookingRepository bookingRepository;
    @Autowired
    private UserService userService;
    @Autowired
    private RoomsService roomsService;
    @Autowired
    private TempBookingService tempBookingService;

    int max_length_booking_id = 10;
    private String generateBookingId() {
        String bookingId;
        do {
            bookingId = RandomId.generateRoomId(max_length_booking_id);
        }while (bookingRepository.existsById(bookingId));
        return bookingId;
    }

    @Override
    public String prepareGuestBooking(GuestBookingRequestDTO guestBookingRequestDTO) {
        RoomEntity roomEntity = roomsService.getRoomById(guestBookingRequestDTO.getRoomId());
        if (roomEntity == null) {
            return "Room is not exist";
        }
        boolean checkAvailable = checkRoomAvailability(guestBookingRequestDTO.getRoomId(),
                guestBookingRequestDTO.getPlanCheckInTime(), guestBookingRequestDTO.getPlanCheckOutTime());
        if (!checkAvailable) {
            return "Room is not available in selected time";
        }
        int nightForBook = Math.toIntExact(ChronoUnit.DAYS.between(guestBookingRequestDTO.getPlanCheckInTime(), guestBookingRequestDTO.getPlanCheckOutTime()));
        BigDecimal priceForBook = (BigDecimal.valueOf(nightForBook).multiply(roomEntity.getPrice_per_night()));
        TempBookingBeforePaymentDTO tempBooking = new TempBookingBeforePaymentDTO();
        tempBooking.setTempBookingId(generateBookingId());
        tempBooking.setRoomId(guestBookingRequestDTO.getRoomId());
        tempBooking.setUserId(null); // Guest không có userId
        tempBooking.setPlanCheckInTime(guestBookingRequestDTO.getPlanCheckInTime());
        tempBooking.setPlanCheckOutTime(guestBookingRequestDTO.getPlanCheckOutTime());
        tempBooking.setTotalPrice(priceForBook);
        tempBookingService.save(tempBooking);
        return "Success: " + tempBooking.getTempBookingId();
    }

    @Override
    public String prepareUserBooking(UserBookingRequestDTO bookingRequestDTO, String userId) {
        RoomEntity roomEntity = roomsService.getRoomById(bookingRequestDTO.getRoomId());
        UserEntity userEntity = userService.getUser(userId);
        if (roomEntity == null) {
            return "Room is not exist";
        }
        if (userEntity == null) {
            return "User is not exist";
        }
        boolean checkAvailable = checkRoomAvailability(bookingRequestDTO.getRoomId(),
                bookingRequestDTO.getPlanCheckInTime(), bookingRequestDTO.getPlanCheckOutTime());
        if (!checkAvailable) {
            return "Room is not available in selected time";
        }
        int nightForBook = Math.toIntExact(ChronoUnit.DAYS.between(bookingRequestDTO.getPlanCheckInTime(), bookingRequestDTO.getPlanCheckOutTime()));
        BigDecimal priceForBook = (BigDecimal.valueOf(nightForBook).multiply(roomEntity.getPrice_per_night()));
        TempBookingBeforePaymentDTO tempBooking = new TempBookingBeforePaymentDTO();
        tempBooking.setTempBookingId(generateBookingId());
        tempBooking.setRoomId(bookingRequestDTO.getRoomId());
        tempBooking.setUserId(userId);
        tempBooking.setPlanCheckInTime(bookingRequestDTO.getPlanCheckInTime());
        tempBooking.setPlanCheckOutTime(bookingRequestDTO.getPlanCheckOutTime());
        tempBooking.setTotalPrice(priceForBook);

        tempBookingService.save(tempBooking);
        return "Success: " + tempBooking.getTempBookingId();
    }


    @Override
    public BookingEntity insertBookingFromTemp(String tempBookingId) {
        TempBookingBeforePaymentDTO tempBooking = tempBookingService.get(tempBookingId);
        if (tempBooking == null) {
            throw new RuntimeException("TempBooking not found");
        }

        RoomEntity roomEntity = roomsService.getRoomById(tempBooking.getRoomId());
        if (roomEntity == null) {
            throw new RuntimeException("Room is not exist");
        }

        UserEntity userFromTemp = userService.getUser(tempBooking.getUserId());

        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setBookingId(tempBooking.getTempBookingId());
        bookingEntity.setRoom(roomEntity);
        bookingEntity.setUser(userFromTemp); // userFromTemp có thể là null nếu như user không tồn tại
        bookingEntity.setPlanCheckInTime(tempBooking.getPlanCheckInTime());
        bookingEntity.setPlanCheckOutTime(tempBooking.getPlanCheckOutTime());
        bookingEntity.setStatus("PENDING");
        bookingEntity.setTotalPrice(tempBooking.getTotalPrice());

        bookingRepository.save(bookingEntity);

        return bookingEntity;
    }

    @Override
    public boolean updateCheckInTime(String bookingId, UserBookingRequestDTO userBookingRequestDTO) {
        // Phần update này chỉ dành cho Host/Admin và nó chỉ được update khi nào mà khách hàng đến nơi và check in

        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            return false;
        }
        bookingEntity.setStatus("CONFIRMED");
        bookingEntity.setActualCheckInTime(LocalDateTime.now());
        bookingRepository.save(bookingEntity);
        return true;
    }

    @Override
    public boolean updateCheckOutTime(String bookingId, UserBookingRequestDTO userBookingRequestDTO) {
        // Phần update này chỉ dành cho Host/Admin và nó chỉ được update khi nào mà khách hàng đến nơi và check out

        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            return false;
        }
        bookingEntity.setStatus("COMPLETED");
        bookingEntity.setActualCheckOutTime(LocalDateTime.now());
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
//
    @Override
    public List<BookingResponseDTO> getAllBookings() {
        List<BookingEntity> bookingEntityList = bookingRepository.findAll();
        List<BookingResponseDTO> bookingResponseDTOList = new ArrayList<>();
        for(BookingEntity bookingEntity : bookingEntityList) {
            BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
            bookingResponseDTO.setBookingId(bookingEntity.getBookingId());

            bookingResponseDTO.setPlanCheckInTime(bookingEntity.getPlanCheckInTime());
            bookingResponseDTO.setPlanCheckOutTime(bookingEntity.getPlanCheckOutTime());
            bookingResponseDTO.setActualCheckInTime(bookingEntity.getActualCheckInTime());
            bookingResponseDTO.setActualCheckOutTime(bookingEntity.getActualCheckOutTime());

            bookingResponseDTO.setStatus(bookingEntity.getStatus());
            bookingResponseDTO.setTotalPrice(bookingEntity.getTotalPrice());
            bookingResponseDTO.setUserId(bookingEntity.getUser().getUserId());
            bookingResponseDTO.setRoomId(bookingEntity.getRoom().getRoomId());
            bookingResponseDTOList.add(bookingResponseDTO);
        }
        return bookingResponseDTOList;
    }

    @Override
    public BookingEntity getBookingById(String bookingId) {
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public BookingResponseDTO getBookingResponseById(String bookingId) {
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            return null;
        } else {
            BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
            bookingResponseDTO.setBookingId(bookingEntity.getBookingId());
            bookingResponseDTO.setPlanCheckInTime(bookingEntity.getPlanCheckInTime());
            bookingResponseDTO.setPlanCheckOutTime(bookingEntity.getPlanCheckOutTime());
            bookingResponseDTO.setActualCheckInTime(bookingEntity.getActualCheckInTime());
            bookingResponseDTO.setActualCheckOutTime(bookingEntity.getActualCheckOutTime());
            bookingResponseDTO.setStatus(bookingEntity.getStatus());
            bookingResponseDTO.setTotalPrice(bookingEntity.getTotalPrice());
            bookingResponseDTO.setUserId(bookingEntity.getUser().getUserId());
            bookingResponseDTO.setRoomId(bookingEntity.getRoom().getRoomId());
            return bookingResponseDTO;
        }
    }


    @Override
    public boolean checkRoomAvailability(String roomId, LocalDateTime planCheckInTime, LocalDateTime planCheckOutTime) {
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
            boolean checkOverLapping =  (planCheckInTime.isAfter(booking.getPlanCheckInTime()) && planCheckInTime.isBefore(booking.getPlanCheckOutTime()))  ||
                    (planCheckOutTime.isAfter(booking.getPlanCheckInTime()) && planCheckOutTime.isBefore(booking.getPlanCheckOutTime())) ||
                    (planCheckInTime.isBefore(booking.getPlanCheckInTime()) && planCheckOutTime.isAfter(booking.getPlanCheckOutTime()));

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

