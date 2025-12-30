package code.controller.booking;

import code.model.dto.booking.GuestBookingRequestDTO;
import code.model.dto.booking.UserBookingRequestDTO;
import code.services.booking.BookingService;
import code.services.payments.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking")
public class BookingController {
    @Autowired
    private BookingService bookingService;
    @Autowired
    private PaymentService paymentService;

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

}

