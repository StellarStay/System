package code.services.booking_contact;

import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.booking_contact.BookingContactRequestDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.booking_contact.BookingContactEntity;
import code.repository.booking_contact.BookingContactRepository;
import code.services.booking.TempBookingService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class BookingContacServiceImpl implements BookingContactService {

    @Autowired
    private TempBookingService tempBookingService;
    @Autowired
    private BookingContactRepository bookingContactRepository;

    int max_length_payment_id = 8;
    private String generateBookingContactId() {
        String bContactId;
        do{
            bContactId = RandomId.generateRoomId(max_length_payment_id);
        }while (bookingContactRepository.existsById(bContactId));
        return bContactId;
    }

    @Override
    public BookingContactEntity insertBookingContact(BookingEntity bookingEntity, BookingContactRequestDTO bookingContactRequestDTO) {
        BookingContactEntity bookingContactEntity = new BookingContactEntity();
        bookingContactEntity.setId(generateBookingContactId());
        bookingContactEntity.setBooking(bookingEntity);

        if (bookingEntity.getUser() != null){
            bookingContactEntity.setFirstName(bookingEntity.getUser().getFirstName());
            bookingContactEntity.setLastName(bookingEntity.getUser().getLastName());
            bookingContactEntity.setEmail(bookingEntity.getUser().getEmail());
            bookingContactEntity.setPhoneNumber(bookingEntity.getUser().getPhone());
        }
        else{
            if (bookingContactRequestDTO == null) {
                throw new RuntimeException("Guest contact is required");
            }
            bookingContactEntity.setFirstName(bookingContactRequestDTO.getFirstName());
            bookingContactEntity.setLastName(bookingContactRequestDTO.getLastName());
            bookingContactEntity.setEmail(bookingContactRequestDTO.getEmail());
            bookingContactEntity.setPhoneNumber(bookingContactRequestDTO.getPhoneNumber());
        }

        return bookingContactEntity;

    }
}
