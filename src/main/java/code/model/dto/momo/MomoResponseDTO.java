package code.model.dto.momo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class MomoResponseDTO {
    private String partnerCode;   // Mã đối tác (MoMo)
    private String orderId;       // Mã đơn hàng
    private String requestId;     // Mã yêu cầu
    private Long amount;          // Số tiền thanh toán
    private Long responseTime;    // Thời gian phản hồi từ MoMo
    private String message;       // Thông điệp (nếu có lỗi)
    private Integer resultCode;   // Mã kết quả (0: thành công, khác 0: thất bại)
    private String payUrl;        // URL thanh toán để người dùng redirect
    private String deeplink;      // Link deep link cho MoMo app
    private String qrCodeUrl;     // QR Code URL (nếu có)
}
