# 🚨 FIX LỖI: User not authorized to perform s3:PutObject

## ❌ LỖI BẠN ĐANG GẶP:

```
software.amazon.awssdk.services.s3.model.S3Exception: 
User: arn:aws:iam::737230811967:user/stellarstay-s3-user 
is not authorized to perform: s3:PutObject on resource: 
"arn:aws:s3:::stellarstay-room-images/rooms/8963eecf-5490-41c7-af8f-018e9317f30a.jpg" 
because no identity-based policy allows the s3:PutObject action 
(Service: S3, Status Code: 403)
```

---

## 🔍 NGUYÊN NHÂN:

IAM User `stellarstay-s3-user` **KHÔNG có quyền upload file** (PutObject) lên S3 bucket `stellarstay-room-images`.

---

## ✅ GIẢI PHÁP NHANH - 5 PHÚT FIX XONG:

### **BƯỚC 1: Đăng nhập AWS Console**

1. Mở browser: https://console.aws.amazon.com/
2. Đăng nhập với Root user account của bạn
3. Vào trang chủ AWS Console

---

### **BƯỚC 2: Vào IAM Service**

1. Ở thanh **Search** phía trên, gõ: **`IAM`**
2. Click vào **"IAM"** (Identity and Access Management)

---

### **BƯỚC 3: Kiểm tra User Permissions**

1. Click **"Users"** (menu bên trái)
2. Click vào user: **`stellarstay-s3-user`**
3. Click tab **"Permissions"** (phía trên)

**Kiểm tra:**
- Nếu **KHÔNG thấy** policy `AmazonS3FullAccess` → Thiếu quyền! (làm tiếp BƯỚC 4)
- Nếu **CÓ** `AmazonS3FullAccess` → Làm BƯỚC 5 (có thể bucket bị restrict)

---

### **BƯỚC 4: Gắn Policy cho User (QUAN TRỌNG!)**

1. Click nút **"Add permissions"** (màu xanh)
2. Chọn: **"Attach policies directly"**
3. Ở ô **Search**, gõ: **`S3`**
4. Tìm và **tick ✅** vào policy: **`AmazonS3FullAccess`**
   
   ```
   Policy name: AmazonS3FullAccess
   Type: AWS managed - job function
   Description: Provides full access to all buckets via the AWS Management Console
   ```

5. Click **"Next"** (góc dưới bên phải)
6. Click **"Add permissions"**

**✅ Thành công!** Hiện thông báo: 
```
Successfully added permissions to stellarstay-s3-user
```

---

### **BƯỚC 5: Verify Policy đã được gắn**

1. Quay lại tab **"Permissions"** của user
2. Trong phần **"Permissions policies"**, bạn sẽ thấy:

```
Policy name                  Type
AmazonS3FullAccess          AWS managed policy
```

✅ **OK! User giờ có quyền upload rồi!**

---

### **BƯỚC 6: Test lại trong Swagger**

1. **KHÔNG CẦN RESTART APP** (credentials không đổi, chỉ quyền trên AWS thay đổi)
2. Mở Swagger: http://localhost:8080/swagger-ui/index.html
3. Tìm section: **"S3 File Management"**
4. Click: **POST `/api/s3/upload/room-image`**
5. Click **"Try it out"**
6. Click nút **"Choose File"** → Chọn ảnh
7. Click **"Execute"**

**Kết quả:**

**✅ Thành công (200 OK):**
```json
{
  "fileUrl": "https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/abc-123.jpg",
  "fileName": "room1.jpg",
  "fileSize": 245678,
  "message": "Upload successful"
}
```

**🎉 XONG! Giờ upload được rồi!**

---

## 🛡️ (OPTIONAL) GIẢI PHÁP AN TOÀN HƠN - PRODUCTION:

Thay vì dùng `AmazonS3FullAccess` (cho phép làm MỌI THỨ với S3), tạo **custom policy** chỉ cho phép upload/delete:

### **Tạo Custom Policy:**

1. **IAM → Policies → "Create policy"**
2. Click tab **"JSON"**
3. Xóa hết, paste đoạn này:

```json
{
  "Version": "2012-10-17",
  "Statement": [
    {
      "Sid": "StellarStayS3Access",
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

4. Click **"Next"**
5. **Policy name:** `StellarStay-S3-Limited`
6. **Description:** `Allow upload/delete only for stellarstay-room-images bucket`
7. Click **"Create policy"**

### **Detach AmazonS3FullAccess, Attach Custom Policy:**

1. **Users → stellarstay-s3-user → Permissions**
2. **Detach** (xóa) `AmazonS3FullAccess`:
   - Tick ✅ vào `AmazonS3FullAccess`
   - Click **"Remove" → "Remove policy"**
3. **Attach** custom policy:
   - Click **"Add permissions" → "Attach policies directly"**
   - Search: `StellarStay-S3-Limited`
   - Tick ✅ → Click **"Add permissions"**

✅ Giờ user **CHỈ** có quyền upload/delete trong bucket `stellarstay-room-images`, không làm gì được với bucket khác → An toàn hơn!

---

## 📊 SO SÁNH 2 CÁCH:

| Phương pháp | Ưu điểm | Nhược điểm | Khuyên dùng |
|------------|---------|------------|-------------|
| **AmazonS3FullAccess** | ✅ Nhanh<br>✅ Đơn giản | ❌ Cho phép làm MỌI THỨ với S3<br>❌ Kém bảo mật | Development/Testing |
| **Custom Policy** | ✅ An toàn<br>✅ Chỉ cho phép upload/delete bucket này | ❌ Phải tạo policy thủ công | Production |

---

## 🎯 CHECKLIST:

- [ ] Đăng nhập AWS Console
- [ ] Vào IAM → Users → stellarstay-s3-user
- [ ] Kiểm tra tab Permissions
- [ ] Attach policy `AmazonS3FullAccess` (hoặc custom policy)
- [ ] Verify policy đã được gắn
- [ ] Test upload lại trong Swagger
- [ ] Upload thành công → ✅ DONE!

---

## 🆘 NẾU VẪN LỖI 403 SAU KHI LÀM HẾT TRÊN:

### **Check 1: Bucket Policy có DENY không?**

1. S3 Console: https://s3.console.aws.amazon.com/s3/buckets
2. Click bucket: `stellarstay-room-images`
3. Tab **"Permissions"** → Scroll xuống **"Bucket policy"**
4. Nếu thấy `"Effect": "Deny"` → **XÓA** policy đó
5. Hoặc paste policy này (chỉ allow public read):

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

6. Save changes

---

### **Check 2: Access Key còn active không?**

1. IAM → Users → stellarstay-s3-user
2. Tab **"Security credentials"**
3. Scroll **"Access keys"**
4. Kiểm tra:
   - Status: **Active** ✅
   - Nếu **Inactive** → Click **"Actions" → "Activate"**
   - Nếu không thấy key nào → Tạo mới (xem S3_INTEGRATION_GUIDE.md BƯỚC 2.3)

---

### **Check 3: Region có đúng không?**

**application.properties:**
```properties
aws.s3.region=ap-southeast-1  # ← Phải match với region của bucket!
```

Kiểm tra region bucket:
- S3 Console → Click bucket → Xem **"AWS Region"**
- Ví dụ: `Asia Pacific (Singapore) ap-southeast-1`
- Update `application.properties` cho match
- Restart app

---

## ✅ KẾT LUẬN:

**99% trường hợp lỗi này do thiếu policy `AmazonS3FullAccess`!**

Làm theo BƯỚC 1-6 ở trên là fix được ngay! 🚀

---

**Created: January 1, 2026**
**Author: GitHub Copilot** 🤖

