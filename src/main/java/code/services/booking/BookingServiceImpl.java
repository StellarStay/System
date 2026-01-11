package code.services.booking;

import code.exception.BadRequestException;
import code.exception.ConflictException;
import code.exception.ResourceNotFoundException;
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
        if (guestBookingRequestDTO == null) {
            throw new BadRequestException("Guest Booking Request is null");
        }
        RoomEntity roomEntity = roomsService.getRoomById(guestBookingRequestDTO.getRoomId());
        if (roomEntity == null) {
            throw new ResourceNotFoundException("Room is not exist");
        }
        boolean checkAvailable = checkRoomAvailability(guestBookingRequestDTO.getRoomId(),
                guestBookingRequestDTO.getPlanCheckInTime(), guestBookingRequestDTO.getPlanCheckOutTime());
        if (!checkAvailable) {
            throw new ResourceNotFoundException("Room is not available in selected time");
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
        tempBooking.setBookingContactRequestDTO(guestBookingRequestDTO.getBookingContactRequestDTO());
        tempBooking.setPaymentMethodId(null);
        tempBookingService.save(tempBooking);
        return "Success: " + tempBooking.getTempBookingId();
    }

    @Override
    public String prepareUserBooking(UserBookingRequestDTO bookingRequestDTO, String userId) {
        if (bookingRequestDTO == null || userId == null) {
            throw new BadRequestException("User Booking Request or User Id is null");
        }
        RoomEntity roomEntity = roomsService.getRoomById(bookingRequestDTO.getRoomId());
        UserEntity userEntity = userService.getUser(userId);
        if (roomEntity == null) {
            throw new ResourceNotFoundException("Room is not exist");
        }
        if (userEntity == null) {
            throw new ResourceNotFoundException("User is not exist");
        }
        boolean checkAvailable = checkRoomAvailability(bookingRequestDTO.getRoomId(),
                bookingRequestDTO.getPlanCheckInTime(), bookingRequestDTO.getPlanCheckOutTime());
        if (!checkAvailable) {
            throw new ResourceNotFoundException("Room is not available in selected time");
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
        tempBooking.setPaymentMethodId(null);
        tempBookingService.save(tempBooking);
        return "Success: " + tempBooking.getTempBookingId();
    }


    @Override
    public BookingEntity insertBookingFromTemp(String tempBookingId) {
        if (tempBookingId == null) {
            throw new BadRequestException("TempBooking Id is null");
        }
        System.out.println("  📝 insertBookingFromTemp called with tempBookingId: " + tempBookingId);

        TempBookingBeforePaymentDTO tempBooking = tempBookingService.get(tempBookingId);
        if (tempBooking == null) {
            System.out.println("  ❌ ERROR: TempBooking not found in insertBookingFromTemp");
            throw new ResourceNotFoundException("TempBooking is not exist");
        }
        System.out.println("  ✅ TempBooking retrieved successfully");

        System.out.println("  📝 Fetching room: " + tempBooking.getRoomId());
        RoomEntity roomEntity = roomsService.getRoomById(tempBooking.getRoomId());
        if (roomEntity == null) {
            System.out.println("  ❌ ERROR: Room not found: " + tempBooking.getRoomId());
            throw new ResourceNotFoundException("Room is not exist");
        }
        System.out.println("  ✅ Room found: " + roomEntity.getRoomId());

        // Xử lý user: nếu userId null (guest) thì user sẽ là null
        UserEntity userFromTemp = null;
        if (tempBooking.getUserId() != null && !tempBooking.getUserId().isEmpty()) {
            System.out.println("  📝 Fetching user: " + tempBooking.getUserId());
            userFromTemp = userService.getUser(tempBooking.getUserId());
            if (userFromTemp != null) {
                System.out.println("  ✅ User found: " + userFromTemp.getUserId());
            } else {
                System.out.println("  ⚠️ User not found, but continuing as guest");
            }
        } else {
            System.out.println("  📝 Guest booking (no userId)");
        }

        System.out.println("  📝 Creating BookingEntity...");
        BookingEntity bookingEntity = new BookingEntity();
        bookingEntity.setBookingId(tempBooking.getTempBookingId());
        bookingEntity.setRoom(roomEntity);
        bookingEntity.setUser(userFromTemp); // userFromTemp có thể là null nếu là guest booking
        bookingEntity.setPlanCheckInTime(tempBooking.getPlanCheckInTime());
        bookingEntity.setPlanCheckOutTime(tempBooking.getPlanCheckOutTime());
        bookingEntity.setStatus("PENDING");
        bookingEntity.setTotalPrice(tempBooking.getTotalPrice());

        System.out.println("  📝 Saving BookingEntity to database...");
        bookingRepository.save(bookingEntity);
        System.out.println("  ✅ BookingEntity saved successfully");

        return bookingEntity;
    }

    @Override
    public boolean updateActualCheckInTime(String bookingId, UserBookingRequestDTO userBookingRequestDTO) {
        // Phần update này chỉ dành cho Host/Admin và nó chỉ được update khi nào mà khách hàng đến nơi và check in
        if (bookingId == null || userBookingRequestDTO == null) {
            throw new BadRequestException("Booking Id or User Booking Request is null");
        }
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            throw new ResourceNotFoundException("Booking is not found");
        }
        bookingEntity.setStatus("CONFIRMED");
        bookingEntity.setActualCheckInTime(LocalDateTime.now());
        bookingRepository.save(bookingEntity);
        return true;
    }

    @Override
    public boolean updateActualCheckOutTime(String bookingId, UserBookingRequestDTO userBookingRequestDTO) {
        // Phần update này chỉ dành cho Host/Admin và nó chỉ được update khi nào mà khách hàng đến nơi và check out

        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            throw new ResourceNotFoundException("Booking is not found");
        }
        bookingEntity.setStatus("COMPLETED");
        bookingEntity.setActualCheckOutTime(LocalDateTime.now());
        bookingRepository.save(bookingEntity);
        return true;
    }

    @Override
    public boolean cancelBooking(String bookingId) {
        if (bookingId == null) {
            throw new BadRequestException("Booking Id is null");
        }
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            throw new ResourceNotFoundException("Booking is not found");
        }
        bookingEntity.setStatus("CANCELLED");
        bookingRepository.save(bookingEntity);
        return true;
    }
//
    @Override
    public List<BookingResponseDTO> getAllBookings() {
        List<BookingEntity> bookingEntityList = bookingRepository.findAll();
        if (bookingEntityList.isEmpty()) {
            throw new ResourceNotFoundException("Booking list is not found");
        }
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
        if (bookingId == null) {
            throw new BadRequestException("Booking Id is null");
        }
        return bookingRepository.findById(bookingId).orElse(null);
    }

    @Override
    public BookingResponseDTO getBookingResponseById(String bookingId) {
        if (bookingId == null) {
            throw new BadRequestException("Booking Id is null");
        }
        BookingEntity bookingEntity = bookingRepository.findById(bookingId).orElse(null);
        if (bookingEntity == null) {
            throw new ResourceNotFoundException("Booking is not found");
        }
        else {
            BookingResponseDTO bookingResponseDTO = new BookingResponseDTO();
            bookingResponseDTO.setBookingId(bookingEntity.getBookingId());
            bookingResponseDTO.setPlanCheckInTime(bookingEntity.getPlanCheckInTime());
            bookingResponseDTO.setPlanCheckOutTime(bookingEntity.getPlanCheckOutTime());
            bookingResponseDTO.setActualCheckInTime(bookingEntity.getActualCheckInTime());
            bookingResponseDTO.setActualCheckOutTime(bookingEntity.getActualCheckOutTime());
            bookingResponseDTO.setStatus(bookingEntity.getStatus());
            bookingResponseDTO.setTotalPrice(bookingEntity.getTotalPrice());
            if (bookingEntity.getUser() == null){
                bookingResponseDTO.setUserId(null);
            }
            else {
                bookingResponseDTO.setUserId(bookingEntity.getUser().getUserId());
            }
            bookingResponseDTO.setRoomId(bookingEntity.getRoom().getRoomId());
            return bookingResponseDTO;
        }
    }

    @Override
    public List<BookingResponseDTO> getBookingsByUserId(String userId) {
        if (userId == null) {
            throw new BadRequestException("User Id is null");
        }
        List<BookingEntity> bookingEntityList = bookingRepository.findByUser_UserId(userId);
        if (bookingEntityList.isEmpty()) {
            throw new ResourceNotFoundException("Can't find user from booking list");
        }
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
    // Đây là hàm check xem phòng được chỉ định có trống vào khoảng thời gian muốn đặt hay không
    public boolean checkRoomAvailability(String roomId, LocalDateTime planCheckInTime, LocalDateTime planCheckOutTime) {
        // Muốn check thì đầu tiên phải lấy phòng ở trong toàn bộ booking ra
        // Sau đó dựa vào tất cả trong booking đối với phòng đó để kiểm tra xem có bị trùng thời gian không ?
        // Nếu trùng thời gian nhưng trạng thái là CANCELLED thì vẫn được duyệt qua và cho đặt
        // Ngược lại nếu trùng thời gian và trạng thái là PENDING hoặc CONFIRMED thì không cho đặt
        // Chỉ duy nhất trạng thái CANCELLED(có thể trùng thời gian) hoặc không trùng thời gian mới được phép đặt
        if (roomId == null || planCheckInTime == null || planCheckOutTime == null) {
            throw new BadRequestException("Invalid input for checking room availability");
        }
        RoomEntity roomToCheck = roomsService.getRoomById(roomId);
        if (roomToCheck == null) {
            throw new ResourceNotFoundException("Room is not exist");
        }
        List<BookingEntity> bookingsOfRoom = bookingRepository.findByRoom_RoomId(roomId);
        for (BookingEntity booking : bookingsOfRoom) {
            boolean checkOverLapping =
                    // Điều kiện check planCheckInTime bị lọt vào trong khoảng thời gian của booking
                    (planCheckInTime.isAfter(booking.getPlanCheckInTime()) && planCheckInTime.isBefore(booking.getPlanCheckOutTime()))  ||
                    // Điều kiện check planCheckOutTime bị lọt vào trong khoảng thời gian của booking
                    (planCheckOutTime.isAfter(booking.getPlanCheckInTime()) && planCheckOutTime.isBefore(booking.getPlanCheckOutTime())) ||
                    // Điều kiện check planCheckInTime và planCheckOutTime bao trùm lấy khoảng thời gian của booking
                    (planCheckInTime.isBefore(booking.getPlanCheckInTime()) && planCheckOutTime.isAfter(booking.getPlanCheckOutTime())) ||
                    // Điều kiện check planCheckInTime hoặc planCheckOutTime trùng với thời gian của booking
                    (planCheckInTime.isEqual(booking.getPlanCheckInTime()) || planCheckOutTime.isEqual(booking.getPlanCheckOutTime()));

            if(booking.getStatus().equals("CANCELLED") || booking.getStatus().equals("COMPLETED")) {
                continue;
            }

            else if (checkOverLapping && (booking.getStatus().equals("PENDING") || booking.getStatus().equals("CONFIRMED"))) {
                throw new ConflictException("Room is not available in selected time");
            }
        }
        return true;
    }
}
