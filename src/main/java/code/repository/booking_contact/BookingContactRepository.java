package code.repository.booking_contact;

import code.model.entity.booking_contact.BookingContactEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BookingContactRepository extends JpaRepository<BookingContactEntity, String> {
    BookingContactEntity findByBooking_BookingId(String bookingId);
}
