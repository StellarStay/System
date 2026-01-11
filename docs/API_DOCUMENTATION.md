# 📚 API Documentation - Booking Room System

> **Base URL:** `http://localhost:8080`  
> **Last Updated:** January 11, 2026

---

## 🔐 Authentication & Authorization

### Roles trong hệ thống:
- **ADMIN** - Quản trị viên (full quyền)
- **OWNER** - Chủ phòng (quản lý phòng của mình)
- **USER** - Người dùng thường (đặt phòng)

### Token Flow:
1. Login → Nhận `accessToken` + `refreshToken` qua Cookie
2. Token tự động gửi qua Cookie (hoặc Header: `Authorization: Bearer <token>`)
3. Khi hết hạn → Dùng `/api/auth/refresh` để lấy token mới

---

## 📋 API Quick Reference

### 🔑 1. Authentication APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/auth/register/fill_information` | ❌ Public | Đăng ký - Bước 1: Điền thông tin |
| POST | `/api/auth/register/verify-otp` | ❌ Public | Đăng ký - Bước 2: Xác thực OTP |
| POST | `/api/auth/login` | ❌ Public | Đăng nhập |
| POST | `/api/auth/refresh` | ❌ Public | Refresh token khi hết hạn |
| POST | `/api/auth/logout` | ✅ Authenticated | Đăng xuất |
| GET | `/api/auth/me` | ✅ Authenticated | Lấy thông tin user hiện tại |

**Request Body Examples:**

**Register (Step 1):**
```json
{
  "idCard": "123456789012",
  "firstName": "Nguyen",
  "lastName": "Van A",
  "dateOfBirth": "1995-05-15",
  "phone": "0912345678",
  "email": "user@example.com",
  "password": "password123",
  "gender": true,
  "roleId": "user"
}
```

**Login:**
```json
{
  "email": "admin@gmail.com",
  "password": "admin123"
}
```

---

### 👥 2. User Management APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/users/create_user` | 🔒 ADMIN | Tạo user mới |
| GET | `/api/users/get_user/{userId}` | ✅ Authenticated | Lấy thông tin user theo ID |
| GET | `/api/users/get_all_users` | 🔒 ADMIN | Lấy danh sách tất cả users |
| PUT | `/api/users/update/user_user` | ✅ Authenticated | User tự update thông tin mình |
| PUT | `/api/users/update/admin_user` | ✅ Authenticated | Admin update user (cần check role) |
| PUT | `/api/users/change_password` | ✅ Authenticated | Đổi mật khẩu |

**Note:** `update/user_user` lấy `userId` từ token, không cần truyền PathVariable

---

### 🏠 3. Room Management APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/rooms/insertRoom` | 🔒 ADMIN, OWNER | Tạo phòng mới (multipart/form-data) |
| POST | `/api/rooms/updateRoom/{roomId}` | 🔒 ADMIN, OWNER | Cập nhật thông tin phòng |
| GET | `/api/rooms/get/getAllRooms` | ❌ Public | Lấy danh sách tất cả phòng |
| GET | `/api/rooms/get/getByRoomId/{roomId}` | ❌ Public | Lấy chi tiết 1 phòng |
| GET | `/api/rooms/get/getByMaxGuests?maxGuests={n}` | ❌ Public | Tìm phòng theo số khách |
| GET | `/api/rooms/get/getByAddress?address={text}` | ❌ Public | Tìm phòng theo địa chỉ |
| GET | `/api/rooms/get/getByCategory/{categoryId}` | ❌ Public | Lấy phòng theo loại |
| GET | `/api/rooms/get/getByPricePerNight?minPrice={}&maxPrice={}` | ❌ Public | Tìm phòng theo khoảng giá |
| GET | `/api/rooms/get/getByDateAvailability?planCheckInDate={}&planCheckOutDate={}` | ❌ Public | Tìm phòng trống theo ngày |

**insertRoom Form Data:**
- `roomName`, `title`, `description`, `address`
- `pricePerNight` (BigDecimal)
- `maxGuests` (int)
- `status` (PENDING/APPROVED/REJECTED/BLOCKED)
- `categoryId`, `ownerId`
- `images` (MultipartFile[])

---

### 📂 4. Category APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/categories/create_category` | 🔒 ADMIN | Tạo loại phòng mới |
| PUT | `/api/categories/update_category/{cateRoomId}` | 🔒 OWNER ⚠️ | Cập nhật loại phòng |
| DELETE | `/api/categories/delete_category/{cateRoomId}` | 🔒 ADMIN | Xóa loại phòng |
| GET | `/api/categories/get_category/{cateRoomId}` | 🔒 ADMIN | Lấy 1 loại phòng |
| GET | `/api/categories/get_all_categories` | ❌ Public | Lấy tất cả loại phòng |

⚠️ **Warning:** `update_category` hiện tại cho OWNER, nên đổi thành ADMIN

---

### 🔧 5. Device APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/devices/insert_device` | ❌ Public ⚠️ | Thêm thiết bị mới |
| PUT | `/api/devices/update_device/{deviceId}` | ❌ Public ⚠️ | Cập nhật thiết bị |
| DELETE | `/api/devices/delete_device/{deviceId}` | ❌ Public ⚠️ | Xóa thiết bị |
| GET | `/api/devices/get_all_devices` | ❌ Public | Lấy tất cả thiết bị |
| GET | `/api/devices/get_device_by_id/{deviceId}` | ❌ Public | Lấy 1 thiết bị |

⚠️ **Warning:** Tất cả CUD operations đang Public, nên thêm `@PreAuthorize("hasRole('ADMIN')")`

---

### 🛏️ 6. Device of Room APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/rooms/devices/insert_device_to_room` | 🔒 OWNER | Thêm thiết bị vào phòng |
| PUT | `/api/rooms/devices/update_device_of_room/{deviceOfRoomId}` | 🔒 OWNER | Cập nhật trạng thái thiết bị |
| DELETE | `/api/rooms/devices/remove_device_from_room/{deviceOfRoomId}` | 🔒 OWNER | Xóa thiết bị khỏi phòng |
| GET | `/api/rooms/devices/get_all_devices_of_room/{roomId}` | ❌ Public | Lấy thiết bị của phòng |

---

### 📅 7. Booking APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/booking/filling_booking_information_for_user?userId={id}` | 🔒 USER | Tạo booking (user đã login) |
| POST | `/api/booking/filling_booking_information_for_guest` | ❌ Public | Tạo booking (guest) |
| GET | `/api/booking/get_all_booking_by_user_id/{userId}` | 🔒 USER | Lấy booking của 1 user |
| GET | `/api/booking/get_detail_booking/{bookingId}` | ❌ Public ⚠️ | Lấy chi tiết booking |

⚠️ **Warning:** `get_detail_booking` nên check ownership

**Request Body (for_user):**
```json
{
  "roomId": "room-001",
  "planCheckInTime": "2026-02-01T14:00:00",
  "planCheckOutTime": "2026-02-05T12:00:00",
  "totalPrice": 10000000
}
```

**Request Body (for_guest):** + thêm `contactInfo` object

---

### 💳 8. Payment APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/payment_method/choose_method` | ❌ Public | Chọn payment method |
| POST | `/api/payment/create_payment` | ❌ Public | Tạo payment (MoMo/Cash) |
| POST | `/api/payment/ipn-handler` | ❌ Public | Webhook từ MoMo |
| GET | `/api/payment_method/get_all_payment_methods` | ❌ Public | Lấy danh sách payment methods |

**choose_method Request:**
```json
{
  "tempBookingId": "tempBookingId-abc-xyz",
  "paymentMethodId": "momo"
}
```

**create_payment Request:**
```json
{
  "bookingId": "booking-001",
  "paymentMethodId": "momo",
  "totalPrice": 10000000
}
```

---

### 📁 9. S3 File Upload APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| POST | `/api/s3/upload/room-image` | ❌ Public ⚠️ | Upload ảnh phòng lên S3 |
| DELETE | `/api/s3/delete?fileUrl={url}` | ❌ Public ⚠️ | Xóa file khỏi S3 |
| GET | `/api/s3/presigned-url?fileKey={key}&expirationMinutes={n}` | ❌ Public | Tạo URL tạm để download |

⚠️ **Warning:** Upload/Delete nên chỉ cho OWNER/ADMIN

**Upload Form Data:**
- `file` (MultipartFile) - Max 5MB, chỉ ảnh

---

### 🎭 10. Role Management APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| PUT | `/api/roles/update_role/{roleId}` | 🔒 ADMIN | Cập nhật role |
| DELETE | `/api/roles/delete_role/{roleId}` | 🔒 ADMIN | Xóa role |
| GET | `/api/roles/get_role_by_id/{roleId}` | 🔒 ADMIN | Lấy 1 role |
| GET | `/api/roles/get_all_roles` | 🔒 ADMIN | Lấy tất cả roles |

---

### 📞 11. Booking Contact APIs

| Method | Endpoint | Phân Quyền | Mô Tả |
|--------|----------|-----------|-------|
| GET | `/api/booking_contacts/get_by_booking_id/{bookingId}` | ❌ Public ⚠️ | Lấy contact của booking |

⚠️ **Warning:** Nên check ownership

---

## 🔄 Booking Flow

### ✅ User đã đăng nhập:
```
1. Browse phòng: GET /api/rooms/get/getAllRooms
2. Xem chi tiết: GET /api/rooms/get/getByRoomId/{roomId}
3. Tạo booking: POST /api/booking/filling_booking_information_for_user
   → Response: tempBookingId
4. Chọn payment: POST /api/payment_method/choose_method
5. Thanh toán: POST /api/payment/create_payment
   → MoMo: redirect đến payUrl
   → Cash: hoàn tất
6. MoMo callback: POST /api/payment/ipn-handler (tự động)
7. Kiểm tra: GET /api/booking/get_detail_booking/{bookingId}
```

### ✅ Guest (không đăng nhập):
```
Giống flow trên, nhưng:
- Bước 3: POST /api/booking/filling_booking_information_for_guest
- Phải điền contactInfo (firstName, lastName, email, phone)
```

---

## ⚠️ Security Issues Cần Fix

### 🔴 Critical:
1. **Device APIs** - Tất cả CUD operations đang Public
2. **S3 Upload/Delete** - Public, bất kỳ ai cũng upload được
3. **Booking Detail** - Không check ownership, ai cũng xem được
4. **Booking Contact** - Không check ownership

### 🟡 Medium:
1. **Category Update** - Đang cho OWNER, nên đổi ADMIN
2. **get_user/{userId}** - Nên chỉ cho xem profile mình hoặc ADMIN

### 💡 Recommended Fixes:
```java
// Device APIs
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> insertDevice(...)

// S3 Upload
@PreAuthorize("hasAnyRole('OWNER', 'ADMIN')")
public ResponseEntity<?> uploadImage(...)

// Booking Detail
@PreAuthorize("hasRole('ADMIN') or @bookingService.isOwner(#bookingId, authentication.principal)")
public ResponseEntity<?> getBookingDetail(...)
```

---

## 📊 Sample Test Accounts

| Email | Password | Role |
|-------|----------|------|
| admin@gmail.com | admin123 | ADMIN |
| owner1@gmail.com | owner123 | OWNER |
| user1@gmail.com | user123 | USER |

---

## 📌 Common Status Codes

| Code | Meaning | When |
|------|---------|------|
| 200 | OK | Request thành công |
| 400 | Bad Request | Input không hợp lệ |
| 401 | Unauthorized | Chưa login / Token hết hạn |
| 403 | Forbidden | Không đủ quyền |
| 404 | Not Found | Resource không tồn tại |
| 409 | Conflict | Email đã tồn tại, trùng data |
| 500 | Internal Server Error | Lỗi server |

---

## 🎯 Notes

- **Token được lưu trong Cookie** (ACCESS_TOKEN, REFRESH_TOKEN)
- **Swagger tự động gửi Cookie**, không cần manual setup
- **Postman**: Cần copy cookie sau login hoặc dùng Bearer token
- **Format DateTime**: `yyyy-MM-ddTHH:mm:ss` (ISO 8601)
- **File upload**: Max 5MB, chỉ ảnh
- **Payment Methods**: `cash`, `momo`
- **Booking Status**: PENDING, CONFIRMED, COMPLETED, CANCELLED
- **Room Status**: PENDING, APPROVED, REJECTED, BLOCKED

---

**📅 Last Updated:** January 11, 2026  
**🔖 Version:** 2.0.0 - Simplified (API + Permissions only)
