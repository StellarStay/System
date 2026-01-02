package code.services.s3;

import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

public interface S3Service {

    // Tạo service để upload file lên AWS S3
        // Upload file lên S3
        // file là file cần upload
        // folder là thư mục trong bucket S3 để lưu file vào (ex: "room/")
        // Trả về URL của file đã upload
    String uploadFile(MultipartFile file, String folder) throws IOException;

    // Xóa file khỏi S3
        // fileUrl là URL của file cần xóa
    void deleteFile(String fileUrl);


    // Tạo URL có thời hạn để truy cập file trong S3 và thường dùng cho việc tải các file riêng tư, ví dụ như ảnh đại diện người dùng
        // fileKey là khóa của file trong S3 (ex: "room/filename.jpg")
        // expireMinutes là thời gian hết hạn của URL (tính bằng phút)
        // Trả về URL có thể truy cập các file riêng tư trong S3 trong khoảng thời gian nhất định
    String preSignUrl(String fileKey, int expireMinutes);


}
