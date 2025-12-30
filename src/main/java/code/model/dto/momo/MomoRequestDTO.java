package code.model.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MomoRequestDTO {
    private String partnerCode;   // Mã đối tác (MoMo)
    private String requestType;   // Thường là "captureMoMo" hoặc "createOrder"
    private String ipnUrl;       // URL nhận callback từ MoMo (IPN)
    private String redirectUrl;  // URL trả về sau khi thanh toán
    private String orderId;      // Mã đơn hàng (từ TempBookingId)
    private Long amount;         // Số tiền (đơn vị là đồng)
    private String orderInfo;    // Thông tin về đơn hàng
    private String requestId;    // ID yêu cầu (đảm bảo duy nhất)
    private String extraData;    // Dữ liệu bổ sung (có thể là JSON chuỗi)
    private String signature;    // Chữ ký bảo mật (tạo từ các tham số trên)
    private String lang = "en";  // Ngôn ngữ (mặc định là "en")
}
