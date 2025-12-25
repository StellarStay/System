package code.services.booking_contact;

import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.booking_contact.BookingContactRequestDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.booking_contact.BookingContactEntity;

public interface BookingContactService {
    public BookingContactEntity insertBookingContact(BookingEntity bookingEntity, BookingContactRequestDTO bookingContactRequestDTO);
}
