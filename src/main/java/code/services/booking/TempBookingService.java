package code.services.booking;

import code.model.dto.booking.TempBookingBeforePaymentDTO;

public interface TempBookingService {
    public void save(TempBookingBeforePaymentDTO tempBookingBeforePaymentDTO);
    public TempBookingBeforePaymentDTO get(String tempBookingId);
    public void delete(String tempBookingId);
}
