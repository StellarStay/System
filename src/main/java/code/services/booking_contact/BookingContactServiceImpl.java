package code.services.booking_contact;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.booking_contact.BookingContactRequestDTO;
import code.model.dto.booking_contact.BookingContactResponseDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.booking_contact.BookingContactEntity;
import code.repository.booking_contact.BookingContactRepository;
import code.services.booking.BookingService;
import code.services.booking.TempBookingService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class BookingContactServiceImpl implements BookingContactService {

    @Autowired
    private BookingContactRepository bookingContactRepository;
    @Autowired
    private BookingService bookingService;


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
        if (bookingEntity == null || bookingEntity.getBookingId() == null) {
            throw new BadRequestException("Invalid booking entity request");
        }
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
                throw new BadRequestException("Invalid booking contact request");
            }
            bookingContactEntity.setFirstName(bookingContactRequestDTO.getFirstName());
            bookingContactEntity.setLastName(bookingContactRequestDTO.getLastName());
            bookingContactEntity.setEmail(bookingContactRequestDTO.getEmail());
            bookingContactEntity.setPhoneNumber(bookingContactRequestDTO.getPhoneNumber());
        }
        bookingContactEntity.setCreatedAt(LocalDateTime.now());
        bookingContactRepository.save(bookingContactEntity);

        return bookingContactEntity;

    }

    @Override
    public BookingContactResponseDTO getBookingContactResponseDTOByBookingId(String bookingId) {
        if (bookingId == null){
            throw new BadRequestException("Invalid booking id request");
        }
        BookingContactEntity bookingContactEntity = bookingContactRepository.findByBooking_BookingId(bookingId);
        if (bookingContactEntity == null) {
            throw new ResourceNotFoundException("Booking contact not found for booking");
        } else {
            BookingContactResponseDTO bookingContactResponseDTO = new BookingContactResponseDTO();
            bookingContactResponseDTO.setBookingId(bookingContactEntity.getBooking().getBookingId());
            bookingContactResponseDTO.setFirstName(bookingContactEntity.getFirstName());
            bookingContactResponseDTO.setLastName(bookingContactEntity.getLastName());
            bookingContactResponseDTO.setEmail(bookingContactEntity.getEmail());
            bookingContactResponseDTO.setPhoneNumber(bookingContactEntity.getPhoneNumber());
            bookingContactResponseDTO.setCreatedAt(bookingContactEntity.getCreatedAt());
            return bookingContactResponseDTO;
        }

    }
}
