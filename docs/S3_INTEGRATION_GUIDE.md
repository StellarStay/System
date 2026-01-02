# 🚀 HƯỚNG DẪN TÍCH HỢP AWS S3 VÀO SPRING BOOT

## 📋 MỤC LỤC:
1. [Thêm Dependencies](#1-thêm-dependencies)
2. [Cấu hình application.properties](#2-cấu-hình-applicationproperties)
3. [Tạo S3Config](#3-tạo-s3config)
4. [Tạo S3Service](#4-tạo-s3service)
5. [Tạo Controller để Upload/Download](#5-tạo-controller)
6. [Tạo DTO](#6-tạo-dto)
7. [Test với Swagger](#7-test-với-swagger)
8. [Tích hợp vào Room Entity](#8-tích-hợp-vào-room-entity)
9. [Xử lý lỗi thường gặp](#9-xử-lý-lỗi-thường-gặp)
10. [Bảo mật](#10-bảo-mật)
11. [Tối ưu hóa](#11-tối-ưu-hóa)

---

## 1. THÊM DEPENDENCIES

### **pom.xml:**
```xml
<!-- AWS S3 SDK v2 -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3</artifactId>
    <version>2.20.26</version>
</dependency>

<!-- AWS SDK Core -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>auth</artifactId>
    <version>2.20.26</version>
</dependency>

<!-- (Optional) Nếu muốn upload multipart file lớn -->
<dependency>
    <groupId>software.amazon.awssdk</groupId>
    <artifactId>s3-transfer-manager</artifactId>
    <version>2.20.26</version>
</dependency>
```

**Lệnh Maven để update dependencies:**
```bash
mvnw clean install
```

---

## 2. CẤU HÌNH APPLICATION.PROPERTIES

### **2.1. Config BẮT BUỘC (Phải có):**

Mở file **`src/main/resources/application.properties`** và thêm:

```properties
# ============================================
# AWS S3 Configuration (BẮT BUỘC)
# ============================================
# AWS Credentials - Lấy từ IAM User (BƯỚC 2.3)
aws.s3.access-key=YOUR_ACCESS_KEY_ID
aws.s3.secret-key=YOUR_SECRET_ACCESS_KEY

# AWS Region - Chọn region gần nhất (Singapore cho VN)
aws.s3.region=ap-southeast-1

# S3 Bucket name - Tên bucket đã tạo (BƯỚC 1.2)
aws.s3.bucket-name=stellarstay-room-images-khanh2024
```

**Giải thích:**
- `aws.s3.access-key` ✅ **BẮT BUỘC** - Access Key ID từ IAM User
- `aws.s3.secret-key` ✅ **BẮT BUỘC** - Secret Access Key từ IAM User
- `aws.s3.region` ✅ **BẮT BUỘC** - Region của bucket (ap-southeast-1 = Singapore)
- `aws.s3.bucket-name` ✅ **BẮT BUỘC** - Tên bucket (phải match với bucket đã tạo)

---

### **2.2. Config FOLDER (Tùy chọn - Nên có):**

```properties
# ============================================
# S3 Folders Organization (TÙY CHỌN)
# ============================================
# Folder lưu ảnh phòng gốc
aws.s3.folder.rooms=rooms

# Folder lưu ảnh thumbnail (ảnh nhỏ)
aws.s3.folder.thumbnails=thumbnails
```

**Giải thích:**
- `aws.s3.folder.rooms` ⚠️ **TÙY CHỌN** - Tên folder lưu ảnh phòng trong bucket
  - **Có config:** Code dùng value này (`rooms`) → dễ đổi sau
  - **Không config:** Hard-code trong code (`"rooms"`) → cũng OK
  - **Khuyên dùng:** CÓ (linh hoạt hơn)

- `aws.s3.folder.thumbnails` ⚠️ **TÙY CHỌN** - Folder lưu ảnh thumbnail
  - Dùng khi có feature tạo ảnh nhỏ (100x100px) để load nhanh hơn
  - Ví dụ: Upload ảnh 5MB → Tạo thumbnail 50KB → Hiển thị list nhanh

**Lưu ý:** 
- **KHÔNG thêm `/` ở cuối:** `rooms` ✅ | `rooms/` ❌
- Code sẽ tự thêm `/` khi upload

---

### **2.3. Config UPLOAD SIZE (Nên có):**

```properties
# ============================================
# File Upload Configuration (NÊN CÓ)
# ============================================
# Max size cho 1 file
spring.servlet.multipart.max-file-size=10MB

# Max size cho toàn bộ request (nhiều files)
spring.servlet.multipart.max-request-size=10MB

# Enable multipart uploads
spring.servlet.multipart.enabled=true
```

**Giải thích:**
- `spring.servlet.multipart.max-file-size` ✅ **NÊN CÓ**
  - Giới hạn size 1 file upload
  - Mặc định Spring Boot: **1MB** → Quá nhỏ cho ảnh!
  - **10MB** = đủ cho ảnh phòng quality cao
  - Upload file > 10MB → Lỗi: `MaxUploadSizeExceededException`

- `spring.servlet.multipart.max-request-size` ✅ **NÊN CÓ**
  - Giới hạn tổng size tất cả files trong 1 request
  - Ví dụ: Upload 5 ảnh cùng lúc, mỗi ảnh 2MB → Total 10MB ✅
  - Nếu set 10MB mà upload 6 ảnh 2MB → Lỗi!

- `spring.servlet.multipart.enabled` ⚠️ **TÙY CHỌN**
  - Mặc định: `true` (Spring Boot tự enable)
  - Chỉ cần set nếu muốn disable upload: `false`

---

### **2.4. Config ĐẦY ĐỦ (Khuyên dùng):**

```properties
# ============================================
# AWS S3 Configuration
# ============================================
# AWS Credentials (KHÔNG commit lên Git!)
aws.s3.access-key=YOUR_AWS_ACCESS_KEY_ID
aws.s3.secret-key=YOUR_AWS_SECRET_ACCESS_KEY

# AWS Region (Singapore - gần Việt Nam)
aws.s3.region=ap-southeast-1

# S3 Bucket name
aws.s3.bucket-name=stellarstay-room-images-khanh2024

# Folders trong bucket (optional - có thể hard-code trong code)
aws.s3.folder.rooms=rooms
aws.s3.folder.thumbnails=thumbnails

# ============================================
# File Upload Configuration
# ============================================
# Max file size cho 1 file
spring.servlet.multipart.max-file-size=10MB
# Max request size (tổng tất cả files trong 1 request)
spring.servlet.multipart.max-request-size=10MB
# Enable multipart uploads
spring.servlet.multipart.enabled=true
```

---

### **2.5. BẢO MẬT - KHÔNG commit credentials lên Git!**

**⚠️ NGUY HIỂM - ĐỪNG LÀM THẾ NÀY:**
```properties
# ❌ BAD - Commit trực tiếp credentials
aws.s3.access-key=YOUR_AWS_ACCESS_KEY_ID
aws.s3.secret-key=YOUR_AWS_SECRET_ACCESS_KEY
```

**✅ AN TOÀN - Dùng Environment Variables:**

**application.properties:**
```properties
# ✅ GOOD - Dùng biến môi trường
aws.s3.access-key=${AWS_ACCESS_KEY_ID}
aws.s3.secret-key=${AWS_SECRET_ACCESS_KEY}
aws.s3.region=ap-southeast-1
aws.s3.bucket-name=stellarstay-room-images-khanh2024
```

**Set biến môi trường (Windows CMD):**
```cmd
set AWS_ACCESS_KEY_ID=YOUR_AWS_ACCESS_KEY_ID
set AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET_ACCESS_KEY
mvnw spring-boot:run
```

**Hoặc trong IntelliJ IDEA:**
- Run → Edit Configurations
- Environment variables: 
  ```
  AWS_ACCESS_KEY_ID=YOUR_AWS_ACCESS_KEY_ID;AWS_SECRET_ACCESS_KEY=YOUR_AWS_SECRET_ACCESS_KEY
  ```

---

### **2.6. Config KHÔNG CẦN THIẾT:**

```properties
# ❌ KHÔNG CẦN - Chỉ dùng cho S3-compatible services (MinIO, DigitalOcean Spaces)
# aws.s3.endpoint=http://localhost:9000

# ❌ KHÔNG CẦN - Spring Boot tự động detect
# spring.servlet.multipart.file-size-threshold=0

# ❌ KHÔNG CẦN - Mặc định đã OK
# spring.servlet.multipart.location=/tmp
```

---

### **📊 BẢNG TỔNG KẾT CONFIG:**

| Config | Bắt buộc? | Giá trị mẫu | Giải thích |
|--------|-----------|-------------|------------|
| `aws.s3.access-key` | ✅ BẮT BUỘC | `AKIA...` | Access Key từ IAM User |
| `aws.s3.secret-key` | ✅ BẮT BUỘC | `wJal...` | Secret Key từ IAM User |
| `aws.s3.region` | ✅ BẮT BUỘC | `ap-southeast-1` | Region của bucket |
| `aws.s3.bucket-name` | ✅ BẮT BUỘC | `stellarstay-room-images` | Tên bucket |
| `aws.s3.folder.rooms` | ⚠️ TÙY CHỌN | `rooms` | Folder lưu ảnh phòng (nên có) |
| `aws.s3.folder.thumbnails` | ⚠️ TÙY CHỌN | `thumbnails` | Folder lưu thumbnail (nếu cần) |
| `spring.servlet.multipart.max-file-size` | ✅ NÊN CÓ | `10MB` | Giới hạn size 1 file |
| `spring.servlet.multipart.max-request-size` | ✅ NÊN CÓ | `10MB` | Giới hạn size request |
| `spring.servlet.multipart.enabled` | ❌ KHÔNG CẦN | `true` | Mặc định đã `true` |
| `aws.s3.endpoint` | ❌ KHÔNG CẦN | - | Chỉ dùng cho MinIO/DigitalOcean |

---

## 3. TẠO S3CONFIG

### **S3Config.java đã có sẵn trong project:**
```java
package code.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class S3Config {

    @Value("${aws.s3.access-key}")
    private String accessKey;

    @Value("${aws.s3.secret-key}")
    private String secretKey;

    @Value("${aws.s3.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        AwsBasicCredentials credentials = AwsBasicCredentials.create(accessKey, secretKey);
        
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(StaticCredentialsProvider.create(credentials))
                .build();
    }
}
```

**Giải thích:**
- `@Value`: Inject giá trị từ `application.properties`
- `AwsBasicCredentials`: Tạo credentials từ Access Key và Secret Key
- `S3Client.builder()`: Tạo S3 client với region và credentials

---

## 4. TẠO S3SERVICE

### **Tạo interface S3Service:**
```java
package code.services.s3;

import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

public interface S3Service {
    /**
     * Upload file lên S3
     * @param file File cần upload
     * @param folder Thư mục trong bucket (vd: "rooms", "users")
     * @return URL công khai của file
     */
    String uploadFile(MultipartFile file, String folder) throws IOException;
    
    /**
     * Xóa file khỏi S3
     * @param fileUrl URL của file cần xóa
     */
    void deleteFile(String fileUrl);
    
    /**
     * Lấy URL tạm thời (presigned URL) để download file private
     * @param fileKey Key của file trong S3
     * @param expirationMinutes Thời gian hết hạn (phút)
     * @return Presigned URL
     */
    String generatePresignedUrl(String fileKey, int expirationMinutes);
}
```

### **Implement S3ServiceImpl:**
```java
package code.services.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service {

    private final S3Client s3Client;

    @Value("${aws.s3.bucket-name}")
    private String bucketName;

    @Value("${aws.s3.region}")
    private String region;

    public S3ServiceImpl(S3Client s3Client) {
        this.s3Client = s3Client;
    }

    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        // Tạo key (tên file) unique
        String originalFilename = file.getOriginalFilename();
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;

        // Upload lên S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ) // Public file (ai cũng xem được)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Trả về URL công khai
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Extract key từ URL
        // URL: https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/abc.jpg
        // Key: rooms/abc.jpg
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public String generatePresignedUrl(String fileKey, int expirationMinutes) {
        S3Presigner presigner = S3Presigner.create();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expirationMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        String url = presigner.presignGetObject(presignRequest).url().toString();
        presigner.close();
        
        return url;
    }
}
```

**Giải thích:**

#### **uploadFile():**
1. Tạo key unique: `rooms/abc-123-xyz.jpg`
2. Set `contentType` để browser hiểu file là gì (image/jpeg, image/png...)
3. Set `ACL.PUBLIC_READ` để file public (ai cũng xem được)
4. Upload với `s3Client.putObject()`
5. Trả về URL: `https://bucket.s3.region.amazonaws.com/key`

#### **deleteFile():**
1. Extract key từ URL
2. Gọi `s3Client.deleteObject()`

#### **generatePresignedUrl():**
- Tạo URL tạm thời để download file **private**
- URL hết hạn sau `expirationMinutes` phút
- Dùng cho file bảo mật (không public)

---

## 5. TẠO CONTROLLER

### **S3Controller.java:**

```java
package code.controller.s3;

import code.model.dto.s3.FileUploadResponseDTO;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Tag(name = "S3 File Management", description = "Upload/Delete files to AWS S3")
public class S3Controller {

    private final S3Service s3Service;

    @PostMapping("/upload/room-image")
    @Operation(summary = "Upload room image", description = "Upload hình ảnh phòng lên S3")
    public ResponseEntity<FileUploadResponseDTO> uploadRoomImage(
            @RequestParam("file") MultipartFile file) throws IOException {

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type (chỉ cho phép ảnh)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        String fileUrl = s3Service.uploadFile(file, "rooms");

        return ResponseEntity.ok(FileUploadResponseDTO.builder()
                .fileUrl(fileUrl)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .message("Upload successful")
                .build());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete file from S3", description = "Xóa file khỏi S3")
    public ResponseEntity<String> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        s3Service.deleteFile(fileUrl);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "Generate presigned URL", description = "Tạo URL tạm thời để download file private")
    public ResponseEntity<String> getPresignedUrl(
            @RequestParam("fileKey") String fileKey,
            @RequestParam(value = "expirationMinutes", defaultValue = "60") int expirationMinutes) {

        String url = s3Service.generatePresignedUrl(fileKey, expirationMinutes);
        return ResponseEntity.ok(url);
    }
}
```

---

## 6. TẠO DTO

### **FileUploadResponseDTO.java:**
```java
package code.model.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FileUploadResponseDTO {
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String message;
}
```

---

## 7. HƯỚNG DẪN AWS CONSOLE TỪ A-Z (CHO NGƯỜI MỚI)

### **🌟 BƯỚC 0: TẠO TÀI KHOẢN AWS (NẾU CHƯA CÓ)**

#### **0.1. Đăng ký AWS Free Tier:**

1. **Truy cập:** https://aws.amazon.com/free/
2. Click nút **"Create a Free Account"** (màu cam)
3. **Điền thông tin:**
   - **Email address:** Email của bạn (dùng email mới nếu muốn)
   - **AWS account name:** `StellarStay` (hoặc tên bạn muốn)
   - Click **"Verify email address"**

4. **Kiểm tra email:**
   - AWS gửi mã xác nhận (Verification code)
   - Nhập mã vào → Click **"Verify"**

5. **Tạo Root user password:**
   - Nhập password mạnh (ít nhất 8 ký tự, có chữ hoa, số, ký tự đặc biệt)
   - Re-enter password
   - Click **"Continue"**

6. **Điền thông tin liên hệ:**
   - **Account type:** Chọn **"Personal"** (cá nhân)
   - **Full name:** Tên đầy đủ của bạn
   - **Phone number:** Số điện thoại (+84 cho Việt Nam)
   - **Country/Region:** Vietnam
   - **Address, City, State, Postal code:** Điền địa chỉ thật
   - Tick ✅ **"I have read and agree to the terms of the AWS Customer Agreement"**
   - Click **"Continue"**

7. **Thêm thẻ thanh toán:**
   - ⚠️ **AWS yêu cầu thẻ tín dụng/debit để xác minh (nhưng KHÔNG tự động charge)**
   - **Card number:** Số thẻ VISA/Mastercard
   - **Expiration date:** MM/YY
   - **Cardholder's name:** Tên trên thẻ
   - **Billing address:** Chọn "Use contact address"
   - Click **"Verify and Continue"**
   - AWS sẽ **charge tạm 1 USD** (hoặc ~23,000 VND) để verify → hoàn lại sau vài ngày

8. **Xác minh số điện thoại:**
   - Chọn **"Text message (SMS)"** hoặc **"Voice call"**
   - Nhập **Security check code** (4 chữ số trên màn hình)
   - Click **"Send SMS"** (hoặc "Call me now")
   - Nhập mã 4 số nhận được → Click **"Continue"**

9. **Chọn Support plan:**
   - Chọn **"Basic support - Free"** (miễn phí)
   - Click **"Complete sign up"**

10. **🎉 Hoàn tất!**
    - Màn hình hiện: **"Congratulations! Your AWS account is ready"**
    - Click **"Go to the AWS Management Console"**

---

#### **0.2. Đăng nhập AWS Console:**

1. **Truy cập:** https://console.aws.amazon.com/
2. Chọn **"Root user"**
3. Nhập **Root user email address** (email đăng ký lúc nãy)
4. Click **"Next"**
5. Nhập **Password**
6. Click **"Sign in"**
7. **Lần đầu đăng nhập:** AWS có thể yêu cầu xác minh bổ sung (check email/SMS)

---

### **🪣 BƯỚC 1: TẠO S3 BUCKET**

#### **1.1. Vào S3 Service:**

1. Sau khi đăng nhập AWS Console, bạn sẽ thấy trang chủ
2. Ở thanh tìm kiếm **phía trên** (search bar), gõ: **`S3`**
3. Click vào **"S3"** (dưới Services) → Nó có icon hình **bucket màu xanh lá**

   ![Screenshot tìm S3]
   ```
   🔍 Search: S3
   📦 S3 - Scalable Storage in the Cloud
   ```

4. Bạn sẽ vào trang **"Amazon S3"** → Hiện danh sách buckets (đang trống)

---

#### **1.2. Tạo Bucket mới:**

1. Click nút **"Create bucket"** (màu cam, góc phải)

2. **General configuration:**
   - **AWS Region:** Chọn **`Asia Pacific (Singapore) ap-southeast-1`**
     - ⚠️ Chọn region gần Việt Nam để upload/download nhanh hơn
   - **Bucket type:** Để mặc định **"General purpose"**

3. **Bucket name:**
   - Nhập: **`stellarstay-room-images-<tên-bạn>`**
   - ⚠️ **Lưu ý:** Tên bucket phải **unique toàn cầu** (không trùng với ai)
   - Ví dụ: `stellarstay-room-images-khanh2024`
   - **Quy tắc đặt tên:**
     - Chỉ chữ thường (a-z), số (0-9), dấu gạch ngang (-)
     - Từ 3-63 ký tự
     - Không có khoảng trắng, không viết hoa

4. **Object Ownership:**
   - Chọn: **"ACLs enabled"** (click radio button)
   - Tick ✅ **"I acknowledge that ACLs will be restored"**
   - **Object Ownership:** Chọn **"Bucket owner preferred"**

5. **Block Public Access settings for this bucket:**
   - ⚠️ **QUAN TRỌNG:** Để public file ảnh, bạn cần **BỎ CHỌN** hết các ô tick
   - **Bỏ tick ❌** "Block all public access" (click vào ô tick để bỏ)
   - Sau khi bỏ tick, sẽ hiện cảnh báo màu đỏ:
     ```
     ⚠️ Turning off block all public access might result in this bucket 
        and the objects within becoming public
     ```
   - **Tick ✅** vào ô: **"I acknowledge that the current settings might result in this bucket and the objects within becoming public"**

6. **Bucket Versioning:**
   - Để mặc định: **"Disable"** (không cần versioning)

7. **Tags (optional):**
   - Bỏ qua (không cần thêm tags)

8. **Default encryption:**
   - Để mặc định: **"Server-side encryption with Amazon S3 managed keys (SSE-S3)"**
   - Encryption key type: **"Amazon S3 managed keys (SSE-S3)"**

9. **Advanced settings:**
   - **Object Lock:** Để mặc định **"Disable"**

10. **Kéo xuống cuối → Click nút "Create bucket"** (màu cam)

11. **✅ Thành công!**
    - Hiện thông báo xanh lá: **"Successfully created bucket "stellarstay-room-images-khanh2024""**
    - Bạn sẽ thấy bucket mới trong danh sách

---

#### **1.3. Cấu hình Bucket Policy (cho phép public read):**

1. Click vào **tên bucket** vừa tạo (stellarstay-room-images-khanh2024)
2. Click tab **"Permissions"** (thanh menu ngang phía trên)
3. Scroll xuống phần **"Bucket policy"**
4. Click nút **"Edit"**
5. Dán đoạn JSON này vào (thay `stellarstay-room-images-khanh2024` bằng tên bucket của bạn):

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::stellarstay-room-images-khanh2024/*"
    }
  ]
}
```

   **Giải thích:**
   - `"Principal": "*"` → Ai cũng có thể truy cập
   - `"Action": "s3:GetObject"` → Chỉ cho phép đọc (GET), không cho phép upload/delete
   - `"Resource": "arn:aws:s3:::bucket-name/*"` → Áp dụng cho tất cả file trong bucket

6. Click **"Save changes"**
7. ✅ Xong! Giờ mọi file upload lên bucket sẽ public (ai cũng xem được)

---

### **👤 BƯỚC 2: TẠO IAM USER ĐỂ LẤY ACCESS KEY**

#### **2.1. Vào IAM Service:**

1. Click vào **thanh tìm kiếm phía trên**, gõ: **`IAM`**
2. Click vào **"IAM"** (Identity and Access Management)
   ```
   🔍 Search: IAM
   🔐 IAM - Manage access to AWS resources
   ```

3. Bạn sẽ vào trang **IAM Dashboard**

---

#### **2.2. Tạo IAM User mới:**

1. Ở **menu bên trái**, click **"Users"** (mục thứ 3)
2. Click nút **"Create user"** (màu cam, góc phải)

3. **Step 1: Specify user details**
   - **User name:** Nhập **`stellarstay-s3-user`**
   - **Provide user access to the AWS Management Console (optional):**
     - **BỎ TICK ❌** (user này chỉ dùng cho code, không cần login vào console)
   - Click **"Next"**

4. **Step 2: Set permissions**
   - Chọn: **"Attach policies directly"** (click radio button)
   - Ở ô **Search**, gõ: **`S3`**
   - Tìm và **tick ✅** vào policy: **`AmazonS3FullAccess`**
     - Policy này cho phép user làm mọi thứ với S3 (upload, delete, list buckets...)
   - Click **"Next"**

5. **Step 3: Review and create**
   - Kiểm tra lại thông tin:
     ```
     User name: stellarstay-s3-user
     Permissions: AmazonS3FullAccess
     ```
   - Click **"Create user"**

6. **✅ Thành công!**
   - Hiện thông báo: **"User stellarstay-s3-user created successfully"**

---

#### **2.3. Tạo Access Key cho User:**

1. Click vào **tên user** vừa tạo: **`stellarstay-s3-user`**
2. Click tab **"Security credentials"** (thanh menu ngang)
3. Scroll xuống phần **"Access keys"**
4. Click nút **"Create access key"**

5. **Step 1: Access key best practices & alternatives**
   - Chọn: **"Application running outside AWS"** (click radio button)
   - Tick ✅ **"I understand the above recommendation..."**
   - Click **"Next"**

6. **Step 2: Set description tag (optional)**
   - **Description tag value:** Nhập **`StellarStay Backend S3 Access`**
   - Click **"Create access key"**

7. **Step 3: Retrieve access keys**
   - 🎉 **Thành công!** Màn hình hiện:
     ```
     Access key created
     ✅ Access key: AKIAIOSFODNN7EXAMPLE
     ✅ Secret access key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
     ```

   - ⚠️ **CỰC KỲ QUAN TRỌNG:**
     - **LƯU LẠI NGAY** 2 thông tin này vào file text hoặc notepad
     - **Secret access key CHỈ HIỆN 1 LẦN Duy nhất!** Sau khi thoát ra không xem lại được
     - **KHÔNG share cho ai**, không commit lên Git

   - **Cách lưu:**
     - **Option 1:** Click **"Download .csv file"** → Lưu file CSV vào máy
     - **Option 2:** Copy/paste vào Notepad:
       ```
       Access Key ID: AKIAIOSFODNN7EXAMPLE
       Secret Access Key: wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
       ```

8. Click **"Done"**

9. ✅ Xong! Giờ bạn có:
   - ✅ S3 Bucket: `stellarstay-room-images-khanh2024`
   - ✅ Access Key ID
   - ✅ Secret Access Key

---

### **⚙️ BƯỚC 3: CẤU HÌNH APPLICATION.PROPERTIES**

1. Mở file: **`src/main/resources/application.properties`**
2. Thêm đoạn config này vào cuối file:

```properties
# ============================================
# AWS S3 Configuration
# ============================================
aws.s3.access-key=AKIAIOSFODNN7EXAMPLE
aws.s3.secret-key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
aws.s3.region=ap-southeast-1
aws.s3.bucket-name=stellarstay-room-images-khanh2024
```

   **⚠️ Thay thế:**
   - `AKIAIOSFODNN7EXAMPLE` → Access Key ID của bạn
   - `wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY` → Secret Access Key của bạn
   - `stellarstay-room-images-khanh2024` → Tên bucket của bạn

3. **Lưu file** (Ctrl + S)

---

### **🚀 BƯỚC 4: RESTART ỨNG DỤNG**

#### **4.1. Kill process cũ (nếu đang chạy):**

**Windows CMD:**
```cmd
netstat -ano | findstr :8080
```
- Copy PID (số ở cột cuối)
- Kill process:
```cmd
taskkill /PID <PID_NUMBER> /F
```

Ví dụ:
```cmd
C:\> netstat -ano | findstr :8080
  TCP    0.0.0.0:8080           0.0.0.0:0              LISTENING       12345

C:\> taskkill /PID 12345 /F
SUCCESS: The process with PID 12345 has been terminated.
```

---

#### **4.2. Chạy lại ứng dụng:**

```cmd
cd "D:\Khanh's Project\StellarStay\BE\System"
mvnw spring-boot:run
```

**Đợi cho đến khi thấy:**
```
Started BookingRoomProjectApplication in X.XXX seconds
```

---

### **🧪 BƯỚC 5: TEST TRONG SWAGGER**

#### **5.1. Mở Swagger UI:**

1. Mở browser, truy cập: **http://localhost:8080/swagger-ui/index.html**
2. Tìm section: **"S3 File Management"** (kéo xuống)

---

#### **5.2. Test Upload Room Image:**

1. Click vào **POST `/api/s3/upload/room-image`** → Click **"Try it out"**
2. Click nút **"Choose File"** → Chọn 1 ảnh từ máy tính (jpg, png...)
3. Click **"Execute"**
4. **Kết quả:**

**✅ Thành công (200 OK):**
```json
{
  "fileUrl": "https://stellarstay-room-images-khanh2024.s3.ap-southeast-1.amazonaws.com/rooms/abc-123-xyz.jpg",
  "fileName": "room1.jpg",
  "fileSize": 245678,
  "message": "Upload successful"
}
```

**Copy `fileUrl` → Paste vào browser:**
- URL: `https://stellarstay-room-images-khanh2024.s3.ap-southeast-1.amazonaws.com/rooms/abc-123-xyz.jpg`
- Nếu **thấy ảnh hiển thị** → **PERFECT!** ✅🎉

---

**❌ Lỗi thường gặp:**

| Lỗi | Nguyên nhân | Giải pháp |
|-----|-------------|-----------|
| **403 Forbidden (Access Denied)** | Sai Access Key/Secret Key | Kiểm tra lại credentials trong `application.properties` |
| **404 Not Found (Bucket does not exist)** | Sai tên bucket hoặc bucket chưa tạo | Kiểm tra `aws.s3.bucket-name` |
| **403 Forbidden khi mở URL ảnh** | Bucket chặn public access | Làm lại **Bước 1.3** (Bucket Policy) |
| **500 Internal Server Error** | Chưa config S3 trong code | Kiểm tra `S3Config.java`, `S3Service`, `S3Controller` |

---

#### **5.3. Kiểm tra trên AWS Console:**

1. Quay lại **AWS S3 Console**: https://s3.console.aws.amazon.com/s3/buckets
2. Click vào bucket: **stellarstay-room-images-khanh2024**
3. Vào folder: **rooms/**
4. Bạn sẽ thấy file ảnh vừa upload! ✅
5. Click vào file → Click **"Open"** → Ảnh hiển thị trong tab mới

---

#### **5.4. Test Delete File:**

1. Quay lại Swagger
2. Click **DELETE `/api/s3/delete`** → **"Try it out"**
3. **fileUrl:** Paste URL ảnh vừa upload
   ```
   https://stellarstay-room-images-khanh2024.s3.ap-southeast-1.amazonaws.com/rooms/abc-123-xyz.jpg
   ```
4. Click **"Execute"**
5. **Response:** `"File deleted successfully"`
6. Refresh lại bucket trên AWS Console → File đã biến mất! ✅

---

#### **5.5. Test Generate Presigned URL (Advanced):**

**Presigned URL** dùng cho file **private** (không public). Nếu bạn muốn:
- File chỉ xem được khi có link đặc biệt
- Link tự hết hạn sau X phút
- Bảo mật cao hơn

**Cách test:**

1. Click **GET `/api/s3/presigned-url`** → **"Try it out"**
2. **fileKey:** Nhập `rooms/abc-123-xyz.jpg` (tên file trong bucket)
3. **expirationMinutes:** Nhập `60` (link hết hạn sau 60 phút)
4. Click **"Execute"**
5. **Response:**
   ```
   https://stellarstay-room-images-khanh2024.s3.ap-southeast-1.amazonaws.com/rooms/abc-123-xyz.jpg?X-Amz-Algorithm=...&X-Amz-Expires=3600&X-Amz-Signature=...
   ```
6. Copy URL này → Paste vào browser → Ảnh hiển thị
7. Sau 60 phút → URL hết hạn → 403 Forbidden ✅

---

### **📱 BƯỚC 6: MẸO SỬ DỤNG TRÊN AWS CONSOLE**

#### **6.1. Xem danh sách file trong bucket:**

1. Vào S3 Console: https://s3.console.aws.amazon.com/s3/buckets
2. Click vào bucket name
3. Thấy folder `rooms/` → Click vào
4. Danh sách file hiển thị với:
   - **Name:** Tên file
   - **Last modified:** Thời gian upload
   - **Size:** Dung lượng
   - **Storage class:** Standard (mặc định)

---

#### **6.2. Upload file thủ công trên AWS Console:**

1. Vào bucket → Click **"Upload"**
2. Click **"Add files"** → Chọn ảnh từ máy
3. Kéo xuống → **Permissions:**
   - Click **"Grant public-read access"**
   - Tick ✅ **"I understand..."**
4. Click **"Upload"**
5. File upload xong → Click **"Close"**
6. Click vào file → Copy **Object URL** → Paste vào browser → Xem ảnh ✅

---

#### **6.3. Xóa file trên AWS Console:**

1. Vào bucket → Click vào folder `rooms/`
2. **Tick ✅** vào file muốn xóa
3. Click nút **"Delete"** (phía trên)
4. Gõ: **`permanently delete`** vào ô confirm
5. Click **"Delete objects"**
6. ✅ File đã bị xóa

---

#### **6.4. Xem thống kê bucket:**

1. Click vào bucket → Tab **"Metrics"**
2. Thấy biểu đồ:
   - **Number of objects:** Số file trong bucket
   - **Bucket size:** Tổng dung lượng
   - **Requests:** Số lần GET/PUT

---

#### **6.5. Xem chi phí S3:**

1. Click vào **user name** (góc phải trên) → **"Billing and Cost Management"**
2. Click **"Bills"** (menu bên trái)
3. Tìm **"Amazon Simple Storage Service"**
4. Xem chi tiết:
   - **S3 Storage:** Chi phí lưu trữ
   - **S3 Requests:** Chi phí GET/PUT requests
   - **S3 Data Transfer:** Chi phí bandwidth

**Lưu ý:** Tháng đầu **FREE** (Free Tier):
- 5GB storage
- 20,000 GET requests
- 2,000 PUT requests

---

### **🔒 BƯỚC 7: BẢO MẬT NÂNG CAO**

#### **7.1. KHÔNG commit credentials lên Git:**

**BAD ❌:**
```properties
# application.properties
aws.s3.access-key=AKIAIOSFODNN7EXAMPLE  ← Commit lên Git = NGUY HIỂM!
aws.s3.secret-key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

**GOOD ✅:**

**Cách 1: Dùng Environment Variables**

1. Tạo file **`.env`** (thêm vào `.gitignore`):
   ```properties
   AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
   AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   ```

2. **application.properties:**
   ```properties
   aws.s3.access-key=${AWS_ACCESS_KEY_ID}
   aws.s3.secret-key=${AWS_SECRET_ACCESS_KEY}
   aws.s3.region=ap-southeast-1
   aws.s3.bucket-name=stellarstay-room-images-khanh2024
   ```

3. **Set environment variables:**

   **Windows (CMD):**
   ```cmd
   set AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
   set AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
   mvnw spring-boot:run
   ```

   **IntelliJ IDEA:**
   - Run → Edit Configurations
   - Environment variables: `AWS_ACCESS_KEY_ID=...;AWS_SECRET_ACCESS_KEY=...`

---

**Cách 2: Tạo `application-dev.properties` (local) và `application-prod.properties` (production)**

**application.properties:**
```properties
spring.profiles.active=dev
```

**application-dev.properties:** (thêm vào `.gitignore`)
```properties
aws.s3.access-key=AKIAIOSFODNN7EXAMPLE
aws.s3.secret-key=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
aws.s3.region=ap-southeast-1
aws.s3.bucket-name=stellarstay-room-images-khanh2024
```

**.gitignore:**
```
application-dev.properties
application-prod.properties
.env
```

---

#### **7.2. Xóa Access Key nếu bị lộ:**

**Nếu bạn vô tình commit credentials lên Git:**

1. **Xóa ngay Access Key trên AWS:**
   - IAM → Users → stellarstay-s3-user
   - Tab "Security credentials"
   - Tìm Access Key bị lộ → Click **"Actions" → "Delete"**
   - Confirm delete

2. **Tạo Access Key mới:**
   - Click **"Create access key"**
   - Làm lại **Bước 2.3** ở trên

3. **Update credentials trong code**

4. **Delete Git history:**
   ```bash
   git filter-branch --force --index-filter \
     "git rm --cached --ignore-unmatch application.properties" \
     --prune-empty --tag-name-filter cat -- --all
   git push origin --force --all
   ```

---

#### **7.3. Giới hạn quyền IAM User (Production):**

**Thay vì dùng `AmazonS3FullAccess` (quá rộng), tạo policy riêng:**

1. IAM → Policies → **"Create policy"**
2. Click tab **"JSON"**, paste:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:GetObject",
        "s3:DeleteObject"
      ],
      "Resource": "arn:aws:s3:::stellarstay-room-images-khanh2024/*"
    }
  ]
}
```

3. Click **"Next"**
4. **Policy name:** `StellarStay-S3-LimitedAccess`
5. Click **"Create policy"**
6. Quay lại IAM User → **Detach** `AmazonS3FullAccess`
7. **Attach** `StellarStay-S3-LimitedAccess`

**Giải thích:**
- User **CHỈ** có quyền PUT/GET/DELETE file trong bucket `stellarstay-room-images-khanh2024`
- **KHÔNG** có quyền xóa bucket, tạo bucket mới, etc.
- An toàn hơn! ✅

---

### **⚡ BƯỚC 8: TỐI ƯU HÓA CHO PRODUCTION**

#### **8.1. Nén ảnh trước khi upload (giảm chi phí):**

**Thêm dependency:**
```xml
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.19</version>
</dependency>
```

**Cập nhật S3ServiceImpl:**
```java
import net.coobird.thumbnailator.Thumbnails;
import java.io.ByteArrayOutputStream;

@Override
public String uploadFile(MultipartFile file, String folder) throws IOException {
    // Nén ảnh trước khi upload
    ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
    Thumbnails.of(file.getInputStream())
            .size(1920, 1080)  // Resize max 1920x1080
            .outputQuality(0.8) // Chất lượng 80%
            .toOutputStream(outputStream);
    
    byte[] compressedImage = outputStream.toByteArray();
    
    // Upload ảnh đã nén
    String fileName = folder + "/" + UUID.randomUUID() + ".jpg";
    PutObjectRequest putObjectRequest = PutObjectRequest.builder()
            .bucket(bucketName)
            .key(fileName)
            .contentType("image/jpeg")
            .acl(ObjectCannedACL.PUBLIC_READ)
            .build();
    
    s3Client.putObject(putObjectRequest, RequestBody.fromBytes(compressedImage));
    
    return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
}
```

**Kết quả:**
- File 5MB → Nén còn 500KB
- Upload nhanh hơn 10 lần
- Tiết kiệm chi phí storage + bandwidth 🚀

---

#### **8.2. Upload bất đồng bộ (Async):**

**Thêm `@EnableAsync` vào main class:**
```java
@SpringBootApplication
@EnableAsync
public class BookingRoomProjectApplication {
    // ...
}
```

**Cập nhật S3Service:**
```java
import org.springframework.scheduling.annotation.Async;
import java.util.concurrent.CompletableFuture;

@Async
public CompletableFuture<String> uploadFileAsync(MultipartFile file, String folder) throws IOException {
    String fileUrl = uploadFile(file, folder);
    return CompletableFuture.completedFuture(fileUrl);
}
```

**Sử dụng trong Controller:**
```java
@PostMapping("/create-room-async")
public ResponseEntity<String> createRoomAsync(
        @RequestPart("room") RoomDTO roomDTO,
        @RequestPart("image") MultipartFile image) throws Exception {
    
    // Upload ảnh bất đồng bộ (không chờ)
    CompletableFuture<String> futureUrl = s3Service.uploadFileAsync(image, "rooms");
    
    // Tạo room ngay (không đợi upload xong)
    Room room = new Room();
    room.setRoomId(RandomId.generateRandomId());
    // ...set other fields...
    
    roomRepository.save(room);
    
    // Đợi upload xong rồi update imageUrl
    futureUrl.thenAccept(imageUrl -> {
        room.setImageUrl(imageUrl);
        roomRepository.save(room);
    });
    
    return ResponseEntity.ok("Room created! Image uploading...");
}
```

**Kết quả:**
- API response nhanh (không đợi upload xong)
- User experience tốt hơn ✅

---

#### **8.3. Dùng CloudFront CDN (tùy chọn - nâng cao):**

**CloudFront** = CDN của AWS → Load ảnh nhanh hơn, rẻ hơn

**Cách setup:**

1. AWS Console → Tìm **"CloudFront"**
2. Click **"Create distribution"**
3. **Origin domain:** Chọn bucket `stellarstay-room-images-khanh2024.s3.ap-southeast-1.amazonaws.com`
4. **Origin access:** Chọn **"Public"**
5. **Default cache behavior:** Để mặc định
6. Click **"Create distribution"**
7. Đợi 5-10 phút → Status: **Deployed** ✅
8. Copy **Distribution domain name:** `d1234abcd.cloudfront.net`

**Cập nhật S3ServiceImpl:**
```java
@Value("${aws.cloudfront.domain}")
private String cloudfrontDomain;

// Trả về CloudFront URL thay vì S3 URL
return String.format("https://%s/%s", cloudfrontDomain, fileName);
```

**application.properties:**
```properties
aws.cloudfront.domain=d1234abcd.cloudfront.net
```

**Kết quả:**
- URL: `https://d1234abcd.cloudfront.net/rooms/abc.jpg`
- Load ảnh **nhanh hơn 3-5 lần** (cache ở edge locations gần user)
- Chi phí bandwidth **rẻ hơn 20-30%** 💰

---

### **📊 BƯỚC 9: GIÁM SÁT VÀ THEO DÕI**

#### **9.1. Xem log upload/delete:**

**Thêm logging vào S3ServiceImpl:**
```java
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
public class S3ServiceImpl implements S3Service {
    
    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        log.info("Uploading file: {} to folder: {}", file.getOriginalFilename(), folder);
        // ...existing code...
        log.info("File uploaded successfully: {}", fileUrl);
        return fileUrl;
    }
    
    @Override
    public void deleteFile(String fileUrl) {
        log.info("Deleting file: {}", fileUrl);
        // ...existing code...
        log.info("File deleted successfully");
    }
}
```

**Log output:**
```
2026-01-01 10:30:45 INFO  - Uploading file: room1.jpg to folder: rooms
2026-01-01 10:30:47 INFO  - File uploaded successfully: https://...
```

---

#### **9.2. Monitor trên AWS CloudWatch:**

1. AWS Console → **CloudWatch**
2. Click **"Metrics"** → **"S3"**
3. Chọn bucket: `stellarstay-room-images-khanh2024`
4. Xem biểu đồ:
   - **NumberOfObjects:** Số file
   - **BucketSizeBytes:** Dung lượng
   - **AllRequests:** Tổng requests
   - **4xxErrors, 5xxErrors:** Lỗi

---

### **🎯 CHECKLIST HOÀN CHỈNH**

**AWS Setup:**
- [ ] Tạo tài khoản AWS Free Tier
- [ ] Đăng nhập AWS Console thành công
- [ ] Tạo S3 bucket: `stellarstay-room-images-<tên-bạn>`
- [ ] Bỏ "Block all public access"
- [ ] Thêm Bucket Policy (public read)
- [ ] Tạo IAM User: `stellarstay-s3-user`
- [ ] Attach policy: `AmazonS3FullAccess`
- [ ] Tạo Access Key + lưu credentials

**Code Setup:**
- [ ] Thêm AWS SDK dependencies vào `pom.xml`
- [ ] Chạy `mvnw clean install`
- [ ] Kiểm tra `S3Config.java`
- [ ] Tạo `S3Service` interface
- [ ] Tạo `S3ServiceImpl`
- [ ] Tạo `S3Controller`
- [ ] Tạo `FileUploadResponseDTO`
- [ ] Config `application.properties`
- [ ] Thêm vào `.gitignore`

**Testing:**
- [ ] Restart ứng dụng
- [ ] Mở Swagger UI
- [ ] Test upload ảnh
- [ ] Mở URL ảnh trong browser
- [ ] Kiểm tra file trong AWS Console
- [ ] Test delete file
- [ ] Test presigned URL

**Production Ready:**
- [ ] Dùng Environment Variables cho credentials
- [ ] Giới hạn quyền IAM User
- [ ] Thêm logging
- [ ] Nén ảnh trước khi upload
- [ ] Setup CloudFront CDN (optional)

---

### **🆘 TROUBLESHOOTING NÂNG CAO**

---

#### **❌ LỖI 1: User not authorized to perform s3:PutObject**

**Thông báo lỗi đầy đủ:**
```
software.amazon.awssdk.services.s3.model.S3Exception: 
User: arn:aws:iam::737230811967:user/stellarstay-s3-user is not authorized 
to perform: s3:PutObject on resource: "arn:aws:s3:::stellarstay-room-images/rooms/abc.jpg" 
because no identity-based policy allows the s3:PutObject action 
(Service: S3, Status Code: 403, Request ID: ...)
```

**🔍 Nguyên nhân:**
- IAM User `stellarstay-s3-user` **KHÔNG có quyền upload** (PutObject) lên S3
- Có thể bạn quên attach policy `AmazonS3FullAccess` khi tạo user
- Hoặc policy bị detach/xóa sau đó

---

**✅ GIẢI PHÁP 1: Gắn policy `AmazonS3FullAccess` (NHANH NHẤT)**

1. **Đăng nhập AWS Console:** https://console.aws.amazon.com/
2. **Tìm IAM service:** Gõ `IAM` ở thanh search → Click **IAM**
3. **Vào Users:** Click **"Users"** (menu bên trái)
4. **Click vào user:** `stellarstay-s3-user`
5. **Vào tab Permissions:** Click tab **"Permissions"**
6. **Kiểm tra permissions:**
   - Nếu **KHÔNG thấy** `AmazonS3FullAccess` → Chưa có policy!
   - Nếu thấy nhưng vẫn lỗi → Policy bị restrict

7. **Add policy:**
   - Click nút **"Add permissions"** → **"Attach policies directly"**
   - Search: `AmazonS3FullAccess`
   - **Tick ✅** vào policy `AmazonS3FullAccess`
   - Click **"Add permissions"**

8. **Verify:**
   - Quay lại tab Permissions
   - Thấy `AmazonS3FullAccess` trong danh sách ✅

9. **Test lại trong Swagger:**
   - Quay lại Swagger: `http://localhost:8080/swagger-ui/index.html`
   - POST `/api/s3/upload/room-image`
   - Choose file → Execute
   - **200 OK** → ✅ Thành công!

---

**✅ GIẢI PHÁP 2: Tạo Custom Policy (AN TOÀN HƠN - Production)**

Nếu không muốn dùng `AmazonS3FullAccess` (quá rộng), tạo policy riêng:

1. **IAM → Policies → "Create policy"**
2. **Click tab JSON**, paste:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "AllowS3Operations",
      "Effect": "Allow",
      "Action": [
        "s3:PutObject",
        "s3:PutObjectAcl",
        "s3:GetObject",
        "s3:DeleteObject",
        "s3:ListBucket"
      ],
      "Resource": [
        "arn:aws:s3:::stellarstay-room-images",
        "arn:aws:s3:::stellarstay-room-images/*"
      ]
    }
  ]
}
```

3. **Click "Next"**
4. **Policy name:** `StellarStay-S3-UploadDelete`
5. **Click "Create policy"**
6. **Quay lại IAM User:**
   - Users → stellarstay-s3-user
   - Add permissions → Attach policies directly
   - Search: `StellarStay-S3-UploadDelete`
   - Tick ✅ → Add permissions

**Giải thích policy:**
- `s3:PutObject` ✅ Upload file
- `s3:PutObjectAcl` ✅ Set public/private
- `s3:GetObject` ✅ Download file
- `s3:DeleteObject` ✅ Xóa file
- `s3:ListBucket` ✅ List files trong bucket
- `Resource`: Chỉ áp dụng cho bucket `stellarstay-room-images`

---

**✅ GIẢI PHÁP 3: Kiểm tra Bucket Policy (nếu 2 cách trên không work)**

Có thể bucket có policy **DENY** upload:

1. **S3 Console:** https://s3.console.aws.amazon.com/s3/buckets
2. **Click bucket:** `stellarstay-room-images`
3. **Tab "Permissions"**
4. **Scroll xuống "Bucket policy"**
5. **Kiểm tra:** Nếu có dòng `"Effect": "Deny"` → Xóa hoặc sửa thành `"Allow"`
6. **Nếu trống:** Thêm policy này:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::stellarstay-room-images/*"
    }
  ]
}
```

7. **Save changes**

---

**✅ GIẢI PHÁP 4: Tạo lại Access Key (nếu vẫn lỗi)**

Có thể Access Key bị revoke hoặc expire:

1. **IAM → Users → stellarstay-s3-user**
2. **Tab "Security credentials"**
3. **Scroll "Access keys":**
   - Nếu thấy status: **Inactive** → Click **"Actions" → "Activate"**
   - Nếu không thấy key nào → Tạo mới:
     - Click **"Create access key"**
     - Chọn: **"Application running outside AWS"**
     - Tick ✅ "I understand..."
     - Click **"Create access key"**
     - **COPY ngay Access Key + Secret Key** (chỉ hiện 1 lần!)

4. **Cập nhật `application.properties`:**
```properties
aws.s3.access-key=<NEW_ACCESS_KEY_ID>
aws.s3.secret-key=<NEW_SECRET_ACCESS_KEY>
```

5. **Restart ứng dụng**

---

**🎯 CHECKLIST FIX LỖI 403:**

- [ ] Kiểm tra IAM User có policy `AmazonS3FullAccess`
- [ ] Nếu chưa có → Attach policy
- [ ] Verify trong AWS Console: Permissions tab
- [ ] Test lại upload trong Swagger
- [ ] Nếu vẫn lỗi → Kiểm tra Bucket Policy (không có Deny)
- [ ] Nếu vẫn lỗi → Tạo lại Access Key
- [ ] Cập nhật credentials trong `application.properties`
- [ ] Restart app

---

---

## 8. TÍCH HỢP VÀO ROOM ENTITY

### **Cách 1: Lưu 1 ảnh chính**

#### **Room.java:**
```java
@Entity
@Table(name = "rooms")
public class Room {
    @Id
    private String roomId;
    
    // Thêm trường này
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    // ...existing fields...
}
```

#### **RoomController.java:**
```java
@PostMapping("/create-with-image")
public ResponseEntity<RoomDTO> createRoomWithImage(
        @RequestPart("room") RoomRequestDTO roomRequest,
        @RequestPart("image") MultipartFile image) throws IOException {
    
    // Upload image lên S3
    String imageUrl = s3Service.uploadFile(image, "rooms");
    
    // Create room với imageUrl
    Room room = new Room();
    room.setImageUrl(imageUrl);
    // ...set other fields...
    
    roomRepository.save(room);
    
    return ResponseEntity.ok(roomMapper.toDTO(room));
}
```

### **Cách 2: Lưu nhiều ảnh (Gallery)**

#### **Tạo entity RoomImage:**
```java
@Entity
@Table(name = "room_images")
public class RoomImage {
    @Id
    private String imageId;
    
    @ManyToOne
    @JoinColumn(name = "room_id")
    private Room room;
    
    @Column(name = "image_url", length = 500)
    private String imageUrl;
    
    @Column(name = "is_primary")
    private boolean isPrimary; // Ảnh chính
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
}
```

#### **Room.java:**
```java
@Entity
public class Room {
    @Id
    private String roomId;
    
    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<RoomImage> images;
}
```

#### **RoomController.java:**
```java
@PostMapping("/{roomId}/upload-images")
public ResponseEntity<List<String>> uploadRoomImages(
        @PathVariable String roomId,
        @RequestParam("images") List<MultipartFile> images) throws IOException {
    
    Room room = roomRepository.findById(roomId)
            .orElseThrow(() -> new RuntimeException("Room not found"));
    
    List<String> imageUrls = new ArrayList<>();
    
    for (MultipartFile image : images) {
        String imageUrl = s3Service.uploadFile(image, "rooms/" + roomId);
        
        RoomImage roomImage = new RoomImage();
        roomImage.setImageId(RandomId.generateRandomId());
        roomImage.setRoom(room);
        roomImage.setImageUrl(imageUrl);
        roomImage.setCreatedAt(LocalDateTime.now());
        
        roomImageRepository.save(roomImage);
        imageUrls.add(imageUrl);
    }
    
    return ResponseEntity.ok(imageUrls);
}
```

---

## 9. XỬ LÝ LỖI THƯỜNG GẶP

### **Lỗi 1: Access Denied**
```
Status Code: 403
Error: Access Denied
```

**Nguyên nhân:**
- Sai Access Key hoặc Secret Key
- IAM User không có quyền S3

**Giải pháp:**
- Kiểm tra lại credentials trong `application.properties`
- Kiểm tra IAM User có policy `AmazonS3FullAccess`

---

### **Lỗi 2: Bucket does not exist**
```
Status Code: 404
Error: The specified bucket does not exist
```

**Nguyên nhân:**
- Sai tên bucket
- Bucket chưa được tạo

**Giải pháp:**
- Kiểm tra `aws.s3.bucket-name` trong `application.properties`
- Tạo bucket trên AWS Console

---

### **Lỗi 3: File upload thành công nhưng không xem được**
```
Status Code: 403 Forbidden khi mở URL
```

**Nguyên nhân:**
- Bucket chặn public access
- File không được set ACL public

**Giải pháp:**

1. **Bỏ Block Public Access:**
   - AWS Console → S3 → Bucket → "Permissions"
   - Edit "Block public access" → Bỏ hết ✅

2. **Thêm Bucket Policy:**
```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "PublicReadGetObject",
      "Effect": "Allow",
      "Principal": "*",
      "Action": "s3:GetObject",
      "Resource": "arn:aws:s3:::stellarstay-room-images/*"
    }
  ]
}
```
   - Paste vào: Bucket → "Permissions" → "Bucket Policy"

---

### **Lỗi 4: File quá lớn**
```
java.lang.IllegalArgumentException: File size must be less than 5MB
```

**Giải pháp:**
- Compress ảnh trước khi upload
- Hoặc tăng limit trong Controller:
```java
if (file.getSize() > 10 * 1024 * 1024) { // 10MB
```

---

## 10. BẢO MẬT

### **KHÔNG BAO GIỜ commit credentials lên Git!**

#### **Cách 1: Dùng .gitignore**
```
# .gitignore
application.properties
application-prod.properties
```

#### **Cách 2: Dùng Environment Variables**

**application.properties:**
```properties
aws.s3.access-key=${AWS_ACCESS_KEY_ID}
aws.s3.secret-key=${AWS_SECRET_ACCESS_KEY}
```

**Windows (cmd):**
```cmd
set AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE
set AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
mvnw spring-boot:run
```

**IntelliJ IDEA:**
- Run → Edit Configurations → Environment Variables:
```
AWS_ACCESS_KEY_ID=AKIAIOSFODNN7EXAMPLE;AWS_SECRET_ACCESS_KEY=wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY
```

#### **Cách 3: Dùng AWS IAM Role (Production)**
- Deploy lên AWS EC2/ECS → Attach IAM Role
- Không cần credentials trong code
- AWS SDK tự động lấy credentials từ IAM Role

---

## 11. TỐI ƯU HÓA

### **1. Nén ảnh trước khi upload:**
```java
// Dependency
<dependency>
    <groupId>net.coobird</groupId>
    <artifactId>thumbnailator</artifactId>
    <version>0.4.19</version>
</dependency>

// Code
ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
Thumbnails.of(file.getInputStream())
        .size(1920, 1080)
        .outputQuality(0.8)
        .toOutputStream(outputStream);

byte[] compressedImage = outputStream.toByteArray();
```

### **2. Upload bất đồng bộ (Async):**
```java
@Async
public CompletableFuture<String> uploadFileAsync(MultipartFile file, String folder) {
    // ...upload code...
    return CompletableFuture.completedFuture(fileUrl);
}
```

### **3. Dùng CloudFront CDN:**
- Tạo CloudFront distribution trước S3 bucket
- URL: `https://d1234.cloudfront.net/rooms/abc.jpg`
- Load nhanh hơn, rẻ hơn

---

## 📊 SO SÁNH CÁC CÁCH LƯU ẢNH

| Phương pháp | Ưu điểm | Nhược điểm |
|------------|---------|------------|
| **Lưu trong Database (BLOB)** | ✅ Đơn giản, không cần config | ❌ Database phình to<br>❌ Chậm<br>❌ Tốn băng thông server |
| **Lưu trong Server (File System)** | ✅ Đơn giản<br>✅ Nhanh | ❌ Mất khi deploy lại<br>❌ Không scale được<br>❌ Backup khó |
| **AWS S3** | ✅ Không giới hạn dung lượng<br>✅ CDN sẵn có<br>✅ Backup tự động<br>✅ Scale tốt | ❌ Tốn chi phí (nhưng rẻ)<br>❌ Cần config AWS |

**Kết luận:** Dùng **S3** cho production! 🚀

---

## 💰 CHI PHÍ AWS S3

### **Free Tier (12 tháng đầu):**
- ✅ 5GB storage
- ✅ 20,000 GET requests
- ✅ 2,000 PUT requests

### **Sau Free Tier:**
- Storage: **$0.023/GB/tháng** (rẻ!)
- PUT/POST requests: **$0.005/1,000 requests**
- GET requests: **$0.0004/1,000 requests**

**Ví dụ:**
- 100GB ảnh: $2.3/tháng
- 100,000 uploads: $0.5
- 1 triệu downloads: $0.4

**→ TOTAL: ~$3.2/tháng cho 100GB + 1 triệu requests** 💸

---

## ✅ CHECKLIST TÍCH HỢP S3

- [ ] Thêm dependencies vào `pom.xml`
- [ ] Chạy `mvnw clean install`
- [ ] Tạo S3 bucket trên AWS
- [ ] Tạo IAM User + lấy Access Key
- [ ] Config `application.properties`
- [ ] Kiểm tra `S3Config.java` (đã có sẵn)
- [ ] Tạo `S3Service` + `S3ServiceImpl`
- [ ] Tạo `S3Controller`
- [ ] Tạo `FileUploadResponseDTO`
- [ ] Restart ứng dụng
- [ ] Test upload trong Swagger
- [ ] Mở URL ảnh trong browser
- [ ] Test delete
- [ ] Tích hợp vào `Room` entity

---

## 🎯 KẾT QUẢ

**SAU KHI HOÀN THÀNH:**
1. ✅ Upload ảnh phòng lên S3 qua Swagger
2. ✅ Lấy URL công khai: `https://bucket.s3.region.amazonaws.com/rooms/abc.jpg`
3. ✅ Mở ảnh trên browser
4. ✅ Xóa ảnh khi không cần
5. ✅ Lưu URL vào database (Room entity)
6. ✅ Frontend hiển thị ảnh từ S3

**PERFECT!** 🚀📸

---

## 📚 TÀI LIỆU THAM KHẢO

- AWS S3 Documentation: https://docs.aws.amazon.com/s3/
- AWS SDK for Java v2: https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/
- Spring Boot File Upload: https://spring.io/guides/gs/uploading-files/
- S3 Pricing: https://aws.amazon.com/s3/pricing/

---

**Created: January 2026**
**Last Updated: January 2026**
**Author: GitHub Copilot** 🤖

