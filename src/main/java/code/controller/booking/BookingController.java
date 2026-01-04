package code.controller.booking;

import code.model.dto.booking.BookingResponseDTO;
import code.model.dto.booking.GuestBookingRequestDTO;
import code.model.dto.booking.UserBookingRequestDTO;
import code.services.booking.BookingService;
import code.services.payments.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private PaymentService paymentService;

    @PreAuthorize("hasRole('USER')")
    @PostMapping("/filling_booking_information_for_user")
    public ResponseEntity<String> createUserBooking(@RequestBody UserBookingRequestDTO userBookingRequestDTO, String userId) {
        if (userBookingRequestDTO == null || userId == null || userId.isEmpty()) {
            return ResponseEntity.badRequest().body("Invalid request data");
        }
        return ResponseEntity.ok(bookingService.prepareUserBooking(userBookingRequestDTO, userId));
    }
    @PostMapping("/filling_booking_information_for_guest")
    public ResponseEntity<String> createGuestBooking(@RequestBody GuestBookingRequestDTO guestBookingRequestDTO) {
        if (guestBookingRequestDTO == null) {
            return ResponseEntity.badRequest().body("Invalid request data");
        }
        return ResponseEntity.ok(bookingService.prepareGuestBooking(guestBookingRequestDTO));
    }

    @PreAuthorize("hasRole('USER')")
    @GetMapping("/get_all_booking_by_user_id/{userId}")
    public ResponseEntity<List<BookingResponseDTO>> getBookingsByUserId(@PathVariable("userId") String userId) {
        return ResponseEntity.ok(bookingService.getBookingsByUserId(userId));
    }


    @GetMapping("/get_detail_booking/{bookingId}")
    public ResponseEntity<BookingResponseDTO> getBookingById(@PathVariable("bookingId") String bookingId) {
        BookingResponseDTO bookingResponseDTO = bookingService.getBookingResponseById(bookingId);
        if (bookingResponseDTO != null) {
            return ResponseEntity.ok(bookingResponseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }


}

