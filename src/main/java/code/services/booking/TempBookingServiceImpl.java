package code.services.booking;

import code.model.dto.booking.TempBookingBeforePaymentDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TempBookingServiceImpl implements TempBookingService {

    private static final String KEY_PREFIX = "TEMP_BOOKING: ";
    private static final long TTL = 2;

    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;

    @Override
    public void save(TempBookingBeforePaymentDTO tempBookingBeforePaymentDTO) {
        String key = KEY_PREFIX + tempBookingBeforePaymentDTO.getTempBookingId();
        redisTemplate.opsForValue().set(key, tempBookingBeforePaymentDTO, TTL, TimeUnit.MINUTES);
    }

    @Override
    public TempBookingBeforePaymentDTO get(String tempBookingId) {
        String key = KEY_PREFIX + tempBookingId;
        return (TempBookingBeforePaymentDTO) redisTemplate.opsForValue().get(key);
    }

    @Override
    public void delete(String tempBookingId) {
        redisTemplate.delete(KEY_PREFIX + tempBookingId);
    }
}
