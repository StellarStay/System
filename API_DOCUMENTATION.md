# 📚 API Documentation - Booking Room System

> **Base URL:** `http://localhost:8080`  
> **Last Updated:** January 5, 2026

---

## 🔐 Authentication & Authorization

### Roles trong hệ thống:
- **ADMIN** - Quản trị viên (full quyền)
- **OWNER** - Chủ phòng (quản lý phòng của mình)
- **USER** - Người dùng thường (đặt phòng)

### Token Flow:
1. Login → Nhận `accessToken` (15 phút) + `refreshToken` (7 ngày)
2. Gửi `accessToken` trong Header: `Authorization: Bearer <token>`
3. Khi hết hạn → Dùng `/api/auth/refresh` để lấy token mới

---

## 📑 Table of Contents

1. [Authentication APIs](#1-authentication-apis)
2. [User Management APIs](#2-user-management-apis)
3. [Room Management APIs](#3-room-management-apis)
4. [Category APIs](#4-category-apis)
5. [Device APIs](#5-device-apis)
6. [Booking APIs](#6-booking-apis)
7. [Payment APIs](#7-payment-apis)
8. [S3 File Upload APIs](#8-s3-file-upload-apis)
9. [Role Management APIs](#9-role-management-apis)
10. [Booking Contact APIs](#10-booking-contact-apis)

---

## 1. Authentication APIs

### 📌 POST `/api/auth/register/fill_information`
**Mô tả:** Đăng ký tài khoản mới (bước 1 - điền thông tin)

**Phân quyền:** ❌ Public (không cần đăng nhập)

**Request Body:**
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

**Response:**
```json
{
  "verificationToken": "abc-xyz-token",
  "message": "OTP sent to email"
}
```

---

### 📌 POST `/api/auth/register/verify-otp`
**Mô tả:** Xác thực OTP (bước 2 - hoàn tất đăng ký)

**Phân quyền:** ❌ Public

**Request Body:**
```json
{
  "verificationToken": "abc-xyz-token",
  "otp": "123456"
}
```

**Response:**
```json
{
  "message": "Registration completed successfully"
}
```

---

### 📌 POST `/api/auth/login`
**Mô tả:** Đăng nhập vào hệ thống

**Phân quyền:** ❌ Public

**Request Body:**
```json
{
  "email": "admin@gmail.com",
  "password": "admin123"
}
```

**Response:**
```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

**Note:** Token tự động được set vào Cookie (ACCESS_TOKEN, REFRESH_TOKEN)

---

### 📌 POST `/api/auth/refresh`
**Mô tả:** Làm mới access token khi hết hạn

**Phân quyền:** ❌ Public

**Request Body:**
```json
{
  "refreshToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
}
```

**Response:**
```json
{
  "accessToken": "new-access-token...",
  "refreshToken": "new-refresh-token...",
  "tokenType": "Bearer",
  "expiresIn": 900
}
```

---

### 📌 POST `/api/auth/logout`
**Mô tả:** Đăng xuất khỏi hệ thống

**Phân quyền:** ✅ Authenticated (bất kỳ user đã đăng nhập)

**Request:** Không cần body (lấy user từ token)

**Response:**
```json
"Logged out successfully"
```

**Note:** Xóa token khỏi Cookie và Redis

---

### 📌 GET `/api/auth/me`
**Mô tả:** Lấy thông tin user hiện tại

**Phân quyền:** ✅ Authenticated

**Request:** Không cần params (lấy từ token)

**Response:**
```json
{
  "userId": "abc-123",
  "idCard": "123456789012",
  "firstName": "Nguyen",
  "lastName": "Van A",
  "dateOfBirth": "1995-05-15",
  "phone": "0912345678",
  "email": "user@example.com",
  "gender": true,
  "status": true,
  "createdAt": "2026-01-01T10:00:00",
  "roleId": "user"
}
```

---

## 2. User Management APIs

### 📌 POST `/api/users/create_user`
**Mô tả:** Tạo user mới (chỉ ADMIN)

**Phân quyền:** 🔒 ADMIN only

**Request Body:**
```json
{
  "idCard": "123456789012",
  "firstName": "Tran",
  "lastName": "Thi B",
  "dateOfBirth": "1998-08-20",
  "phone": "0923456789",
  "email": "newuser@example.com",
  "password": "password123",
  "gender": false,
  "roleId": "user"
}
```

**Response:**
```
"User created successfully"
```

---

### 📌 GET `/api/users/get_user/{userId}`
**Mô tả:** Lấy thông tin user theo ID

**Phân quyền:** ✅ Authenticated

**Path Variable:**
- `userId` (String) - ID của user

**Response:**
```json
{
  "userId": "abc-123",
  "idCard": "123456789012",
  "firstName": "Nguyen",
  "lastName": "Van A",
  "dateOfBirth": "1995-05-15",
  "phone": "0912345678",
  "email": "user@example.com",
  "gender": true,
  "status": true,
  "createdAt": "2026-01-01T10:00:00",
  "roleId": "user"
}
```

---

### 📌 GET `/api/users/get_all_users`
**Mô tả:** Lấy danh sách tất cả users

**Phân quyền:** 🔒 ADMIN only

**Response:**
```json
[
  {
    "userId": "user-001",
    "firstName": "Nguyen",
    "lastName": "Van A",
    "email": "user1@gmail.com",
    "roleId": "user",
    ...
  },
  {
    "userId": "owner-001",
    "firstName": "Tran",
    "lastName": "Thi B",
    "email": "owner1@gmail.com",
    "roleId": "owner",
    ...
  }
]
```

---

## 3. Room Management APIs

### 📌 POST `/api/rooms/insertRoom`
**Mô tả:** Tạo phòng mới (upload ảnh kèm theo)

**Phân quyền:** 🔒 ADMIN, OWNER

**Content-Type:** `multipart/form-data`

**Request (Form Data):**
- `roomName` (String) - Tên phòng
- `title` (String) - Tiêu đề
- `description` (String) - Mô tả chi tiết
- `address` (String) - Địa chỉ
- `pricePerNight` (BigDecimal) - Giá/đêm (VD: 1500000)
- `maxGuests` (int) - Số khách tối đa
- `status` (String) - PENDING/APPROVED/REJECTED/BLOCKED
- `categoryId` (String) - ID loại phòng
- `ownerId` (String) - ID chủ phòng
- `images` (MultipartFile[]) - Danh sách ảnh phòng

**Response:**
```
"Room inserted successfully"
```

---

### 📌 POST `/api/rooms/updateRoom/{roomId}`
**Mô tả:** Cập nhật thông tin phòng

**Phân quyền:** 🔒 ADMIN, OWNER

**Path Variable:**
- `roomId` (String) - ID phòng cần update

**Request Body:**
```json
{
  "roomName": "Luxury Suite Updated",
  "title": "New Title",
  "description": "Updated description",
  "address": "New Address",
  "pricePerNight": 2000000,
  "maxGuests": 4,
  "status": "APPROVED",
  "categoryId": "hotel"
}
```

**Response:**
```
"Room updated successfully"
```

---

### 📌 GET `/api/rooms/get/getAllRooms`
**Mô tả:** Lấy danh sách tất cả phòng

**Phân quyền:** ❌ Public

**Response:**
```json
[
  {
    "roomId": "room-001",
    "roomName": "Luxury Suite Downtown",
    "title": "Spacious Suite in Heart of Hanoi",
    "description": "Beautiful suite with modern amenities...",
    "address": "Floor 15, Lotte Center, 54 Lieu Giai, Ba Dinh, Hanoi",
    "pricePerNight": 2500000,
    "maxGuests": 2,
    "status": "APPROVED",
    "createdAt": "2026-01-01T10:00:00",
    "categoryId": "hotel",
    "categoryName": "Hotel",
    "ownerId": "owner-001",
    "ownerName": "Nguyen Van Anh",
    "images": [
      {
        "imageId": "img-001-1",
        "imageUrl": "https://s3.amazonaws.com/rooms/room-001/main.jpg",
        "createdAt": "2026-01-01T10:05:00"
      }
    ],
    "devices": [
      {
        "deviceId": "wifi",
        "deviceName": "WiFi",
        "status": true
      }
    ]
  }
]
```

---

### 📌 GET `/api/rooms/get/getByRoomId/{roomId}`
**Mô tả:** Lấy chi tiết 1 phòng theo ID

**Phân quyền:** ❌ Public

**Path Variable:**
- `roomId` (String) - ID phòng

**Response:** Giống như item trong `getAllRooms`

---

### 📌 GET `/api/rooms/get/getByMaxGuests`
**Mô tả:** Tìm phòng theo số lượng khách

**Phân quyền:** ❌ Public

**Query Params:**
- `maxGuests` (int) - Số khách tối thiểu (VD: 4)

**Example:** `/api/rooms/get/getByMaxGuests?maxGuests=4`

**Response:** Array of RoomResponseDTO

---

### 📌 GET `/api/rooms/get/getByAddress`
**Mô tả:** Tìm phòng theo địa chỉ (tìm kiếm gần đúng)

**Phân quyền:** ❌ Public

**Query Params:**
- `address` (String) - Địa chỉ tìm kiếm (VD: "Hanoi", "Da Nang")

**Example:** `/api/rooms/get/getByAddress?address=Hanoi`

**Response:** Array of RoomResponseDTO

---

### 📌 GET `/api/rooms/get/getByCategory/{categoryId}`
**Mô tả:** Lấy phòng theo loại

**Phân quyền:** ❌ Public

**Path Variable:**
- `categoryId` (String) - ID loại phòng (hotel, apartment, villa...)

**Response:** Array of RoomResponseDTO

---

### 📌 GET `/api/rooms/get/getByPricePerNight`
**Mô tả:** Tìm phòng theo khoảng giá

**Phân quyền:** ❌ Public

**Query Params:**
- `minPrice` (BigDecimal) - Giá tối thiểu (VD: 500000)
- `maxPrice` (BigDecimal) - Giá tối đa (VD: 3000000)

**Example:** `/api/rooms/get/getByPricePerNight?minPrice=500000&maxPrice=3000000`

**Response:** Array of RoomResponseDTO

---

### 📌 GET `/api/rooms/get/getByDateAvailability`
**Mô tả:** Tìm phòng trống theo ngày (chưa có booking)

**Phân quyền:** ❌ Public

**Query Params:**
- `planCheckInDate` (LocalDateTime) - Ngày check-in dự kiến
- `planCheckOutDate` (LocalDateTime) - Ngày check-out dự kiến

**Format:** `yyyy-MM-ddTHH:mm:ss` (ISO 8601)

**Example:**
```
/api/rooms/get/getByDateAvailability?planCheckInDate=2026-02-01T14:00:00&planCheckOutDate=2026-02-05T12:00:00
```

**Response:** Array of RoomResponseDTO (phòng không bị trùng booking)

---

## 4. Category APIs

### 📌 POST `/api/categories/create_category`
**Mô tả:** Tạo loại phòng mới

**Phân quyền:** 🔒 ADMIN only

**Request Body:**
```json
{
  "categoryName": "Resort"
}
```

**Response:**
```
"Category created successfully"
```

---

### 📌 PUT `/api/categories/update_category/{cateRoomId}`
**Mô tả:** Cập nhật loại phòng

**Phân quyền:** 🔒 OWNER (có thể cần sửa thành ADMIN)

**Path Variable:**
- `cateRoomId` (String) - ID loại phòng

**Request Body:**
```json
{
  "categoryName": "Luxury Resort"
}
```

**Response:**
```
"Category updated successfully"
```

---

### 📌 DELETE `/api/categories/delete_category/{cateRoomId}`
**Mô tả:** Xóa loại phòng

**Phân quyền:** 🔒 ADMIN only

**Path Variable:**
- `cateRoomId` (String)

**Response:**
```
"Category deleted successfully"
```

---

### 📌 GET `/api/categories/get_category/{cateRoomId}`
**Mô tả:** Lấy thông tin 1 loại phòng

**Phân quyền:** 🔒 ADMIN only

**Path Variable:**
- `cateRoomId` (String)

**Response:**
```json
{
  "categoryId": "hotel",
  "categoryName": "Hotel"
}
```

---

### 📌 GET `/api/categories/get_all_categories`
**Mô tả:** Lấy tất cả loại phòng

**Phân quyền:** ❌ Public

**Response:**
```json
[
  {
    "categoryId": "hotel",
    "categoryName": "Hotel"
  },
  {
    "categoryId": "apartment",
    "categoryName": "Apartment"
  },
  {
    "categoryId": "villa",
    "categoryName": "Villa"
  }
]
```

---

## 5. Device APIs

### 📌 POST `/api/devices/insert_device`
**Mô tả:** Thêm thiết bị/tiện nghi mới

**Phân quyền:** ❌ Public (nên thêm ADMIN)

**Request Body:**
```json
{
  "deviceName": "Smart TV",
  "deviceType": "Entertainment",
  "brand": "Samsung"
}
```

**Response:** `true` / `false`

---

### 📌 PUT `/api/devices/update_device/{deviceId}`
**Mô tả:** Cập nhật thông tin thiết bị

**Phân quyền:** ❌ Public (nên thêm ADMIN)

**Path Variable:**
- `deviceId` (String)

**Request Body:**
```json
{
  "deviceName": "Ultra Smart TV",
  "deviceType": "Entertainment",
  "brand": "LG"
}
```

**Response:** `true` / `false`

---

### 📌 DELETE `/api/devices/delete_device/{deviceId}`
**Mô tả:** Xóa thiết bị

**Phân quyền:** ❌ Public (nên thêm ADMIN)

**Path Variable:**
- `deviceId` (String)

**Response:** `true`

---

### 📌 GET `/api/devices/get_all_devices`
**Mô tả:** Lấy tất cả thiết bị

**Phân quyền:** ❌ Public

**Response:**
```json
[
  {
    "deviceId": "wifi",
    "deviceName": "WiFi",
    "deviceType": "Internet",
    "brand": "Standard"
  },
  {
    "deviceId": "ac",
    "deviceName": "Air Conditioner",
    "deviceType": "Climate Control",
    "brand": "Daikin"
  }
]
```

---

### 📌 GET `/api/devices/get_device_by_id/{deviceId}`
**Mô tả:** Lấy chi tiết 1 thiết bị

**Phân quyền:** ❌ Public

**Path Variable:**
- `deviceId` (String)

**Response:**
```json
{
  "deviceId": "wifi",
  "deviceName": "WiFi",
  "deviceType": "Internet",
  "brand": "Standard"
}
```

---

## 6. Booking APIs

### 📌 POST `/api/booking/filling_booking_information_for_user`
**Mô tả:** Tạo booking cho user đã đăng nhập

**Phân quyền:** 🔒 USER only

**Request Body:**
```json
{
  "roomId": "room-001",
  "planCheckInTime": "2026-02-01T14:00:00",
  "planCheckOutTime": "2026-02-05T12:00:00",
  "totalPrice": 10000000
}
```

**Query Param:**
- `userId` (String) - ID của user đang booking

**Response:**
```
"tempBookingId-abc-xyz"
```

**Note:** Trả về `tempBookingId` để dùng cho bước chọn payment method

---

### 📌 POST `/api/booking/filling_booking_information_for_guest`
**Mô tả:** Tạo booking cho khách (không cần đăng nhập)

**Phân quyền:** ❌ Public

**Request Body:**
```json
{
  "roomId": "room-001",
  "planCheckInTime": "2026-02-01T14:00:00",
  "planCheckOutTime": "2026-02-05T12:00:00",
  "totalPrice": 10000000,
  "contactInfo": {
    "firstName": "Nguyen",
    "lastName": "Van A",
    "email": "guest@example.com",
    "phoneNumber": "0912345678"
  }
}
```

**Response:**
```
"tempBookingId-xyz-123"
```

---

### 📌 GET `/api/booking/get_all_booking_by_user_id/{userId}`
**Mô tả:** Lấy tất cả booking của 1 user

**Phân quyền:** 🔒 USER only

**Path Variable:**
- `userId` (String)

**Response:**
```json
[
  {
    "bookingId": "booking-001",
    "planCheckInTime": "2026-02-01T14:00:00",
    "planCheckOutTime": "2026-02-05T12:00:00",
    "actualCheckInTime": null,
    "actualCheckOutTime": null,
    "status": "CONFIRMED",
    "totalPrice": 10000000,
    "roomId": "room-001",
    "roomName": "Luxury Suite",
    "userId": "user-001"
  }
]
```

---

### 📌 GET `/api/booking/get_detail_booking/{bookingId}`
**Mô tả:** Lấy chi tiết 1 booking

**Phân quyền:** ❌ Public (nên thêm check ownership)

**Path Variable:**
- `bookingId` (String)

**Response:**
```json
{
  "bookingId": "booking-001",
  "planCheckInTime": "2026-02-01T14:00:00",
  "planCheckOutTime": "2026-02-05T12:00:00",
  "actualCheckInTime": null,
  "actualCheckOutTime": null,
  "status": "CONFIRMED",
  "totalPrice": 10000000,
  "roomId": "room-001",
  "roomName": "Luxury Suite Downtown",
  "userId": "user-001",
  "userName": "Nguyen Van A",
  "payment": {
    "paymentId": "payment-001",
    "paymentStatus": "Success",
    "paymentMethodId": "momo"
  },
  "contact": {
    "firstName": "Nguyen",
    "lastName": "Van A",
    "email": "user@example.com",
    "phoneNumber": "0912345678"
  }
}
```

---

## 7. Payment APIs

### 📌 POST `/api/payment_method/choose_method`
**Mô tả:** Chọn phương thức thanh toán (sau khi tạo booking)

**Phân quyền:** ❌ Public

**Request Body:**
```json
{
  "tempBookingId": "tempBookingId-abc-xyz",
  "paymentMethodId": "momo"
}
```

**Response:**
```
"Payment method selected successfully"
```

---

### 📌 POST `/api/payment/create_payment`
**Mô tả:** Tạo payment (nếu chọn MoMo → trả về URL thanh toán)

**Phân quyền:** ❌ Public

**Request Body:**
```json
{
  "bookingId": "booking-001",
  "paymentMethodId": "momo",
  "totalPrice": 10000000
}
```

**Response (MoMo):**
```json
{
  "payUrl": "https://test-payment.momo.vn/v2/gateway/pay?t=...",
  "qrCodeUrl": "https://chart.googleapis.com/chart?chs=300x300&cht=qr&chl=...",
  "deeplink": "momo://app?action=pay&...",
  "deeplinkMiniApp": "momo://miniapp?...",
  "orderId": "order-123",
  "requestId": "req-456",
  "amount": 10000000,
  "resultCode": 0,
  "message": "Successful"
}
```

**Response (Cash):**
```json
{
  "message": "Payment will be made at property",
  "paymentId": "payment-001"
}
```

---

### 📌 POST `/api/payment/ipn-handler`
**Mô tả:** Webhook từ MoMo (xử lý kết quả thanh toán)

**Phân quyền:** ❌ Public (MoMo gọi)

**Request Body:** MoMo IPN payload (tự động gửi)

**Response:**
```
"IPN processed successfully"
```

**Note:** API này do MoMo gọi, không phải client

---

## 8. S3 File Upload APIs

### 📌 POST `/api/s3/upload/room-image`
**Mô tả:** Upload ảnh phòng lên AWS S3

**Phân quyền:** ❌ Public (nên thêm OWNER/ADMIN)

**Content-Type:** `multipart/form-data`

**Request (Form Data):**
- `file` (MultipartFile) - File ảnh (max 5MB, chỉ ảnh)

**Response:**
```json
{
  "fileUrl": "https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/abc-xyz.jpg",
  "fileName": "room-photo.jpg",
  "fileSize": 2048576,
  "message": "Upload successful"
}
```

**Validation:**
- File không empty
- Content-Type phải là `image/*`
- Size <= 5MB

---

### 📌 DELETE `/api/s3/delete`
**Mô tả:** Xóa file khỏi S3

**Phân quyền:** ❌ Public (nên thêm OWNER/ADMIN)

**Query Param:**
- `fileUrl` (String) - URL đầy đủ của file cần xóa

**Example:**
```
/api/s3/delete?fileUrl=https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/abc.jpg
```

**Response:**
```
"File deleted successfully"
```

---

### 📌 GET `/api/s3/presigned-url`
**Mô tả:** Tạo URL tạm thời để download file private

**Phân quyền:** ❌ Public

**Query Params:**
- `fileKey` (String) - Key của file (VD: `rooms/abc.jpg`)
- `expirationMinutes` (int, optional) - Thời gian hết hạn (default: 60 phút)

**Example:**
```
/api/s3/presigned-url?fileKey=rooms/abc.jpg&expirationMinutes=120
```

**Response:**
```
"https://stellarstay-room-images.s3.amazonaws.com/rooms/abc.jpg?X-Amz-Algorithm=..."
```

---

## 9. Role Management APIs

### 📌 PUT `/api/roles/update_role/{roleId}`
**Mô tả:** Cập nhật role

**Phân quyền:** 🔒 ADMIN only

**Path Variable:**
- `roleId` (String) - ID của role

**Request Body:**
```json
{
  "roleName": "SUPER_ADMIN",
  "description": "Super administrator with all permissions"
}
```

**Response:** `true` / `false`

---

### 📌 DELETE `/api/roles/delete_role/{roleId}`
**Mô tả:** Xóa role

**Phân quyền:** 🔒 ADMIN only

**Path Variable:**
- `roleId` (String)

**Response:** `true` / `false`

---

### 📌 GET `/api/roles/get_role_by_id/{roleId}`
**Mô tả:** Lấy thông tin 1 role

**Phân quyền:** 🔒 ADMIN only

**Path Variable:**
- `roleId` (String)

**Response:**
```json
{
  "roleId": "admin",
  "roleName": "ADMIN",
  "description": "Administrator with full access"
}
```

---

### 📌 GET `/api/roles/get_all_roles`
**Mô tả:** Lấy tất cả roles

**Phân quyền:** 🔒 ADMIN only

**Response:**
```json
[
  {
    "roleId": "admin",
    "roleName": "ADMIN",
    "description": "Administrator with full access"
  },
  {
    "roleId": "owner",
    "roleName": "OWNER",
    "description": "Room owner"
  },
  {
    "roleId": "user",
    "roleName": "USER",
    "description": "Regular user"
  }
]
```

---

## 10. Booking Contact APIs

### 📌 GET `/api/booking_contacts/get_by_booking_id/{bookingId}`
**Mô tả:** Lấy thông tin liên hệ của booking

**Phân quyền:** ❌ Public (nên check ownership)

**Path Variable:**
- `bookingId` (String)

**Response:**
```json
{
  "id": "contact-001",
  "bookingId": "booking-001",
  "firstName": "Nguyen",
  "lastName": "Van A",
  "email": "user@example.com",
  "phoneNumber": "0912345678",
  "createdAt": "2026-01-01T10:00:00"
}
```

---

## 11. Device of Room APIs

### 📌 POST `/api/rooms/devices/insert_device_to_room`
**Mô tả:** Thêm thiết bị vào phòng

**Phân quyền:** 🔒 OWNER only

**Request Body:**
```json
{
  "deviceId": "wifi",
  "roomId": "room-001",
  "status": true
}
```

**Response:** `true` / `false`

---

### 📌 PUT `/api/rooms/devices/update_device_of_room/{deviceOfRoomId}`
**Mô tả:** Cập nhật trạng thái thiết bị trong phòng

**Phân quyền:** 🔒 OWNER only

**Path Variable:**
- `deviceOfRoomId` (String)

**Request Body:**
```json
{
  "status": false
}
```

**Response:** `true` / `false`

---

### 📌 DELETE `/api/rooms/devices/remove_device_from_room/{deviceOfRoomId}`
**Mô tả:** Xóa thiết bị khỏi phòng

**Phân quyền:** 🔒 OWNER only

**Path Variable:**
- `deviceOfRoomId` (String)

**Response:** `true` / `false`

---

### 📌 GET `/api/rooms/devices/get_all_devices_of_room/{roomId}`
**Mô tả:** Lấy tất cả thiết bị của 1 phòng

**Phân quyền:** ❌ Public

**Path Variable:**
- `roomId` (String)

**Response:**
```json
[
  {
    "deviceOfRoomId": "dor-001-1",
    "deviceId": "wifi",
    "deviceName": "WiFi",
    "deviceType": "Internet",
    "brand": "Standard",
    "status": true,
    "createdDate": "2026-01-01T10:00:00"
  },
  {
    "deviceOfRoomId": "dor-001-2",
    "deviceId": "ac",
    "deviceName": "Air Conditioner",
    "deviceType": "Climate Control",
    "brand": "Daikin",
    "status": true,
    "createdDate": "2026-01-01T10:00:00"
  }
]
```

---

## 📊 Booking Flow (User Journey)

### ✅ Flow 1: User Đã Đăng Nhập

```
1. GET /api/rooms/get/getAllRooms (Browse phòng)
2. GET /api/rooms/get/getByRoomId/{roomId} (Xem chi tiết)
3. POST /api/booking/filling_booking_information_for_user (Tạo booking)
   → Response: tempBookingId
4. POST /api/payment_method/choose_method (Chọn payment method)
5. POST /api/payment/create_payment (Thanh toán)
   → Nếu MoMo: Redirect đến payUrl
   → Nếu Cash: Hoàn tất
6. MoMo callback → POST /api/payment/ipn-handler (tự động)
7. GET /api/booking/get_detail_booking/{bookingId} (Kiểm tra)
```

### ✅ Flow 2: Guest (Không Đăng Nhập)

```
1. GET /api/rooms/get/getAllRooms
2. GET /api/rooms/get/getByRoomId/{roomId}
3. POST /api/booking/filling_booking_information_for_guest
   → Cần điền: contactInfo (firstName, lastName, email, phone)
4. POST /api/payment_method/choose_method
5. POST /api/payment/create_payment
6. MoMo callback → POST /api/payment/ipn-handler
```

---

## 🔐 Security Notes

### 🚨 APIs Cần Bổ Sung Phân Quyền:

1. **Device APIs** - Hiện tại Public, nên thêm `@PreAuthorize("hasRole('ADMIN')")`
2. **S3 Upload/Delete** - Nên chỉ cho OWNER/ADMIN
3. **Booking Detail** - Nên check ownership (chỉ user/owner liên quan mới xem được)
4. **Category Update** - Hiện tại là OWNER, nên đổi thành ADMIN

### 🛡️ Best Practices:

- Luôn gửi token trong Header: `Authorization: Bearer <token>`
- Token hết hạn (403) → Gọi `/api/auth/refresh` để lấy token mới
- Validate input trước khi gửi API
- Handle errors properly (400, 401, 403, 404, 500)

---

## 📌 Common Status Codes

| Code | Meaning | Example |
|------|---------|---------|
| 200 | OK | Request thành công |
| 400 | Bad Request | Input không hợp lệ |
| 401 | Unauthorized | Chưa đăng nhập / Token hết hạn |
| 403 | Forbidden | Không có quyền truy cập |
| 404 | Not Found | Resource không tồn tại |
| 500 | Internal Server Error | Lỗi server |

---

## 🎯 Quick Reference

### Sample Users (From V7 Migration):

| Email | Password | Role |
|-------|----------|------|
| admin@gmail.com | admin123 | ADMIN |
| owner1@gmail.com | owner123 | OWNER |
| owner2@gmail.com | owner123 | OWNER |
| owner3@gmail.com | owner123 | OWNER |
| user1@gmail.com | user123 | USER |
| user2@gmail.com | user123 | USER |
| user3@gmail.com | user123 | USER |

### Payment Methods:

- `cash` - Cash (Pay at property)
- `momo` - MoMo E-wallet

### Booking Statuses:

- `PENDING` - Chờ xác nhận
- `CONFIRMED` - Đã xác nhận
- `COMPLETED` - Đã hoàn thành
- `CANCELLED` - Đã hủy

### Room Statuses:

- `PENDING` - Chờ duyệt
- `APPROVED` - Đã duyệt
- `REJECTED` - Bị từ chối
- `BLOCKED` - Bị khóa

---

**📅 Last Updated:** January 5, 2026  
**🔖 Version:** 1.0.0  
**👨‍💻 Generated by:** AI Assistant

