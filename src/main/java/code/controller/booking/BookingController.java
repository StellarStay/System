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
    public ResponseEntity<String> createUserBooking(@RequestBody UserBookingRequestDTO userBookingRequestDTO) {
        return ResponseEntity.ok().body("success");
    }
    @PostMapping("/filling_booking_information_for_guest")
    public ResponseEntity<String> createGuestBooking(@RequestBody GuestBookingRequestDTO guestBookingRequestDTO) {
        return ResponseEntity.ok().body("success");
    }

}
