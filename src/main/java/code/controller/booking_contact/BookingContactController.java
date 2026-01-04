package code.controller.booking_contact;

import code.model.dto.booking_contact.BookingContactResponseDTO;
import code.services.booking_contact.BookingContactService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/booking_contacts")
public class BookingContactController {
    @Autowired
    private BookingContactService bookingContactService;

    @GetMapping("/get_by_booking_id/{bookingId}")
    public ResponseEntity<BookingContactResponseDTO> getByBookingId(@PathVariable("bookingId") String bookingId){
        BookingContactResponseDTO bookingContactResponseDTO = bookingContactService.getBookingContactResponseDTOByBookingId(bookingId);
        if (bookingContactResponseDTO != null) {
            return ResponseEntity.ok(bookingContactResponseDTO);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}
