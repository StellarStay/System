# 📚 HƯỚNG DẪN SỬ DỤNG HỆ THỐNG XỬ LÝ LỖI THỐNG NHẤT

> **Mục đích:** Tạo ra một hệ thống xử lý lỗi chuẩn cho toàn bộ project, giúp Backend dễ code và Frontend dễ xử lý response.

---

## 🎯 Vấn Đề Cần Giải Quyết

### ❌ Trước đây code như thế này:

**Backend trả lỗi lung tung, không thống nhất:**
```java
// Service 1 trả về boolean
public boolean createUser(UserDTO dto) {
    if (emailExists) {
        return false; // ❌ FE không biết lỗi gì
    }
}

// Service 2 throw RuntimeException
public void login(LoginDTO dto) {
    if (!validPassword) {
        throw new RuntimeException("Invalid credentials"); // ❌ Không có status code
    }
}

// Controller bắt exception bừa bãi
@PostMapping("/register")
public ResponseEntity<?> register(@RequestBody UserDTO dto) {
    try {
        boolean result = service.createUser(dto);
        return ResponseEntity.ok("success");
    } catch (Exception e) {
        return ResponseEntity.badRequest().body(Map.of("error", "failed")); // ❌ Mất thông tin lỗi
    }
}
```

**Kết quả:** FE nhận được response lộn xộn:
- Lúc thì `{"error": "failed"}`
- Lúc thì `{"message": "Email exists"}`
- Lúc thì chỉ có `false`
- Không có status code rõ ràng
- Không biết lỗi ở đâu

### ✅ Bây giờ với hệ thống mới:

**Backend chỉ cần throw exception:**
```java
public void createUser(UserDTO dto) {
    if (emailExists) {
        throw new ConflictException("Email already exists in database");
        // ✅ Rõ ràng, dễ hiểu, tự động trả về status 409
    }
}
```

**FE luôn nhận được response thống nhất:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists in database",
  "path": "/api/auth/register/fill_information",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

---

## 🏗️ Kiến Trúc Hệ Thống

```
┌─────────────────────────────────────────────────────────────┐
│                      Controller Layer                       │
│  @PostMapping("/register")                                  │
│  public ResponseEntity<?> register(@RequestBody UserDTO)    │
│      ➜ Không cần try-catch                                  │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                      Service Layer                          │
│  if (emailExists)                                           │
│      throw new ConflictException("Email already exists")    │
│      ➜ Throw exception khi có lỗi                           │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼ (Exception được throw)
┌─────────────────────────────────────────────────────────────┐
│              GlobalExceptionHandler (Tự động bắt)           │
│  @RestControllerAdvice                                      │
│  @ExceptionHandler(ConflictException.class)                 │
│      ➜ Tự động bắt exception và format response            │
└────────────────────────┬────────────────────────────────────┘
                         │
                         ▼
┌─────────────────────────────────────────────────────────────┐
│                    Response to Frontend                     │
│  {                                                          │
│    "status": 409,                                           │
│    "error": "Conflict",                                     │
│    "message": "Email already exists in database",           │
│    "path": "/api/auth/register/fill_information",          │
│    "timestamp": "2026-01-10T10:30:45.123"                   │
│  }                                                          │
└─────────────────────────────────────────────────────────────┘
```

---

## 📦 Các Thành Phần Của Hệ Thống

### 1️⃣ **ErrorResponse.java** - DTO Chuẩn Cho Response Lỗi

**File:** `code/model/dto/error/ErrorResponse.java`

**Mục đích:** Định nghĩa format thống nhất cho TẤT CẢ response lỗi trong project.

**Cấu trúc:**
```java
{
    "status": 409,                                    // HTTP status code
    "error": "Conflict",                              // Tên loại lỗi
    "message": "Email already exists in database",    // Chi tiết lỗi (hiển thị cho user)
    "path": "/api/auth/register/fill_information",   // API endpoint bị lỗi
    "timestamp": "2026-01-10T10:30:45.123"           // Thời gian xảy ra lỗi
}
```

**Lợi ích:**
- ✅ FE chỉ cần parse 1 format duy nhất
- ✅ Có đủ thông tin để debug (`path`, `timestamp`)
- ✅ Có `message` để hiển thị cho user
- ✅ Có `status` để xử lý logic (401 → redirect login, 403 → show warning...)

---

### 2️⃣ **Custom Exception Classes** - Các Loại Lỗi Cụ Thể

**Mục đích:** Mỗi loại lỗi có 1 exception riêng, giúp code rõ ràng và dễ maintain.

---

#### 🔴 **BadRequestException** → HTTP 400

**Khi nào dùng:**
- Dữ liệu đầu vào không hợp lệ
- Validation failed
- Format sai
- Thiếu dữ liệu bắt buộc

**Ví dụ thực tế:**

```java
// ❌ OTP không hợp lệ
if (otp == null || otp.length() != 6) {
    throw new BadRequestException("OTP must be exactly 6 digits");
}

// ❌ Mật khẩu cũ sai
if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
    throw new BadRequestException("Old password is incorrect");
}

// ❌ Mật khẩu mới phải khác mật khẩu cũ
if (oldPassword.equals(newPassword)) {
    throw new BadRequestException("New password must be different from old password");
}

// ❌ Ngày bắt đầu phải trước ngày kết thúc
if (startDate.isAfter(endDate)) {
    throw new BadRequestException("Start date must be before end date");
}
```

**Response FE nhận được:**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "OTP must be exactly 6 digits",
  "path": "/api/auth/verify-otp",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý như nào:**
```javascript
if (response.status === 400) {
    // Hiển thị message cho user
    showErrorToast(response.data.message); // "OTP must be exactly 6 digits"
}
```

---

#### 🔴 **UnauthorizedException** → HTTP 401

**Khi nào dùng:**
- Chưa đăng nhập
- Token hết hạn
- Token không hợp lệ
- Session expired

**Ví dụ thực tế:**

```java
// ❌ Token hết hạn
if (jwtService.isExpired(claims)) {
    throw new UnauthorizedException("Token has expired. Please login again");
}

// ❌ Refresh token không tồn tại
String storedJti = refreshStore.getJti(userId);
if (storedJti == null) {
    throw new UnauthorizedException("Session expired. Please login again");
}

// ❌ Token bị thu hồi
if (!storedJti.equals(jti)) {
    throw new UnauthorizedException("Token has been revoked. Please login again");
}
```

**Response FE nhận được:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Token has expired. Please login again",
  "path": "/api/rooms",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý như nào:**
```javascript
if (response.status === 401) {
    // Xóa token và redirect về login
    localStorage.removeItem('token');
    router.push('/login');
    showErrorToast("Your session has expired. Please login again");
}
```

---

#### 🔴 **InvalidCredentialsException** → HTTP 401

**Khi nào dùng:**
- Sai email/password khi login
- Thông tin đăng nhập không đúng

**Ví dụ thực tế:**

```java
public AuthResponseDTO login(LoginRequestDTO req) {
    UserEntity user = userRepository.findByEmail(req.getEmail());
    
    // ❌ User không tồn tại HOẶC password sai
    if (user == null || !encoder.matches(req.getPassword(), user.getPassword())) {
        // Không nên nói rõ là "email không tồn tại" hay "password sai" 
        // → Bảo mật, tránh hacker biết email có tồn tại không
        throw new InvalidCredentialsException("Email or password is incorrect");
    }
    
    // ... tạo token
}
```

**Response FE nhận được:**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Email or password is incorrect",
  "path": "/api/auth/login",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý như nào:**
```javascript
if (response.status === 401 && response.data.message.includes("Email or password")) {
    // Hiển thị lỗi dưới form login
    setError("email", { message: "Email or password is incorrect" });
}
```

**Lưu ý bảo mật:** Không nên tách message thành "Email not found" hay "Password incorrect" → Hacker sẽ biết email nào đã đăng ký.

---

#### 🔴 **ForbiddenException** → HTTP 403

**Khi nào dùng:**
- User đã login nhưng không có quyền truy cập
- Không đủ role/permission

**Ví dụ thực tế:**

```java
// ❌ Chỉ ADMIN mới được xóa user
@DeleteMapping("/users/{userId}")
@PreAuthorize("hasRole('ADMIN')")
public ResponseEntity<?> deleteUser(@PathVariable String userId, Authentication auth) {
    String currentUserId = auth.getPrincipal();
    UserEntity currentUser = userService.getUser(currentUserId);
    
    if (!"admin".equals(currentUser.getRole().getRoleName())) {
        throw new ForbiddenException("Only administrators can delete users");
    }
    
    userService.deleteUser(userId);
    return ResponseEntity.ok("User deleted successfully");
}

// ❌ User chỉ được sửa thông tin của chính mình
@PutMapping("/users/{userId}")
public ResponseEntity<?> updateUser(@PathVariable String userId, Authentication auth) {
    String currentUserId = auth.getPrincipal();
    
    if (!currentUserId.equals(userId)) {
        throw new ForbiddenException("You can only update your own profile");
    }
    
    // ... update logic
}

// ❌ Owner không được book phòng của chính mình
public void createBooking(BookingDTO dto, String currentUserId) {
    RoomEntity room = roomRepository.findById(dto.getRoomId())
        .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    
    if (room.getOwner().getUserId().equals(currentUserId)) {
        throw new ForbiddenException("You cannot book your own room");
    }
    
    // ... booking logic
}
```

**Response FE nhận được:**
```json
{
  "status": 403,
  "error": "Forbidden",
  "message": "Only administrators can delete users",
  "path": "/api/users/ABC123",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý như nào:**
```javascript
if (response.status === 403) {
    // Hiển thị warning và ẩn chức năng
    showWarningModal(response.data.message);
    hideDeleteButton(); // Ẩn nút delete nếu không có quyền
}
```

---

#### 🔴 **ResourceNotFoundException** → HTTP 404

**Khi nào dùng:**
- Không tìm thấy user/room/booking theo ID
- Entity không tồn tại trong database

**Ví dụ thực tế:**

```java
// ❌ User không tồn tại
UserEntity user = userRepository.findById(userId)
    .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

// ❌ Room không tồn tại
RoomEntity room = roomRepository.findById(roomId)
    .orElseThrow(() -> new ResourceNotFoundException("Room not found with id: " + roomId));

// ❌ Booking không tồn tại
BookingEntity booking = bookingRepository.findById(bookingId)
    .orElseThrow(() -> new ResourceNotFoundException("Booking not found with id: " + bookingId));

// ❌ Role không tồn tại
RoleEntity role = roleRepository.findById(roleId)
    .orElseThrow(() -> new ResourceNotFoundException("Role '" + roleId + "' not found"));

// ❌ Payment method không tồn tại
PaymentMethodEntity payment = paymentRepository.findById(paymentId)
    .orElseThrow(() -> new ResourceNotFoundException("Payment method not found"));
```

**Response FE nhận được:**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "Room not found with id: ROOM123",
  "path": "/api/rooms/ROOM123",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý như nào:**
```javascript
if (response.status === 404) {
    // Redirect về trang danh sách hoặc hiển thị "not found"
    showErrorToast("The room you're looking for doesn't exist");
    router.push('/rooms');
}
```

---

#### 🔴 **ConflictException** → HTTP 409

**Khi nào dùng:**
- Dữ liệu bị trùng lặp (email, phone, idCard...)
- Xung đột logic nghiệp vụ (booking trùng giờ, phòng đã đầy...)

**Ví dụ thực tế:**

```java
// ❌ Email đã tồn tại trong database
if (userRepository.existsByEmail(email)) {
    throw new ConflictException("Email already exists in database");
}

// ❌ Email đang chờ verify trong Redis
String tempUser = redisTemplate.get("user:temp:" + email);
if (tempUser != null) {
    throw new ConflictException("Email is already registered and pending verification. Please check your email or wait 5 minutes");
}

// ❌ ID Card đã được đăng ký
if (userRepository.existsByIdCard(idCard)) {
    throw new ConflictException("This ID card is already registered");
}

// ❌ Phòng đã được book trong khung giờ này
boolean isBooked = bookingRepository.existsByRoomIdAndTimeRange(roomId, startTime, endTime);
if (isBooked) {
    throw new ConflictException("This room is already booked from " + startTime + " to " + endTime);
}

// ❌ Phòng đã hết slot
if (room.getCurrentBookings() >= room.getMaxCapacity()) {
    throw new ConflictException("This room is fully booked. Please choose another time or room");
}

// ❌ User đã có booking chưa hoàn thành
boolean hasPendingBooking = bookingRepository.existsByUserIdAndStatus(userId, "PENDING");
if (hasPendingBooking) {
    throw new ConflictException("You have a pending booking. Please complete or cancel it before creating a new one");
}
```

**Response FE nhận được:**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists in database",
  "path": "/api/auth/register/fill_information",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý như nào:**
```javascript
if (response.status === 409) {
    if (response.data.message.includes("Email already exists")) {
        // Hiển thị lỗi dưới input email
        setError("email", { message: "This email is already registered. Please use another email or login." });
    } else if (response.data.message.includes("room is already booked")) {
        // Hiển thị lỗi và suggest time slot khác
        showErrorModal({
            title: "Room Not Available",
            message: response.data.message,
            action: "View Available Time Slots"
        });
    }
}
```

---

### 3️⃣ **GlobalExceptionHandler** - Bộ Não Xử Lý Lỗi

**File:** `code/exception/GlobalExceptionHandler.java`

**Mục đích:** Tự động bắt TẤT CẢ exception trong project và chuyển thành ErrorResponse chuẩn.

**Cách hoạt động:**

```java
@RestControllerAdvice  // ← Áp dụng cho toàn bộ project
public class GlobalExceptionHandler {
    
    // Bắt ConflictException → Trả về 409
    @ExceptionHandler(ConflictException.class)
    public ResponseEntity<ErrorResponse> handleConflictException(
            ConflictException ex, HttpServletRequest request) {
        
        ErrorResponse errorResponse = new ErrorResponse(
                409,                    // status code
                "Conflict",             // error type
                ex.getMessage(),        // message từ exception
                request.getRequestURI() // API path
        );
        
        return ResponseEntity.status(409).body(errorResponse);
    }
    
    // Bắt ResourceNotFoundException → Trả về 404
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleResourceNotFoundException(...) {
        // Tương tự
    }
    
    // Bắt tất cả exception khác → Trả về 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleGlobalException(...) {
        // Bắt lỗi không mong muốn
    }
}
```

**Lợi ích:**
- ✅ Controller KHÔNG cần try-catch nữa
- ✅ Tự động format response theo chuẩn
- ✅ Một chỗ quản lý tất cả lỗi → Dễ maintain
- ✅ Thêm/sửa cách xử lý lỗi chỉ cần sửa 1 file này

---

## 🚀 Cách Sử Dụng Thực Tế

### ✏️ **Case 1: Đăng Ký User**

#### **Service Layer:**
```java
@Service
public class RegisterServiceImpl implements RegisterService {
    
    @Override
    public Map<String, String> saveTempUser(UserRequestDTO userRequestDTO) throws MessagingException {
        
        // Bước 1: Kiểm tra email đã tồn tại trong DB chưa
        if (userService.isEmailExists(userRequestDTO.getEmail())) {
            throw new ConflictException("Email already exists in database");
            // ← GlobalExceptionHandler tự động bắt và trả về 409
        }

        // Bước 2: Kiểm tra email đang pending verify trong Redis chưa
        String existingTempUser = stringRedisTemplate.opsForValue()
            .get("user:temp:" + userRequestDTO.getEmail());
        
        if (existingTempUser != null) {
            throw new ConflictException("Email is already registered and pending verification. Please check your email or wait 5 minutes");
            // ← GlobalExceptionHandler tự động bắt và trả về 409
        }

        // Bước 3: Lưu temp user vào Redis và gửi OTP
        // ... logic lưu Redis và gửi email
        
        return Map.of(
            "message", "Registration successful! Please check your email for the OTP.",
            "verificationToken", verificationToken
        );
    }
}
```

#### **Controller Layer:**
```java
@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {
    
    @PostMapping("/fill_information")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO userRequestDTO) throws MessagingException {
        
        // KHÔNG cần try-catch nữa! 
        // GlobalExceptionHandler sẽ tự động bắt exception từ service
        Map<String, String> result = registerService.saveTempUser(userRequestDTO);
        return ResponseEntity.ok(result);
    }
}
```

#### **Kịch bản 1: Email đã tồn tại**

**Request:**
```json
POST /api/auth/register/fill_information
{
  "email": "khanhlinhdsg@gmail.com",
  "password": "Khanh@2005",
  "firstName": "Linh",
  "lastName": "Nguyen"
}
```

**Response (409 Conflict):**
```json
{
  "status": 409,
  "error": "Conflict",
  "message": "Email already exists in database",
  "path": "/api/auth/register/fill_information",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý:**
```javascript
try {
    const response = await axios.post('/api/auth/register/fill_information', data);
    // Success
    showSuccessToast("Please check your email for OTP");
    router.push('/verify-otp');
} catch (error) {
    if (error.response.status === 409) {
        // Hiển thị lỗi dưới input email
        setFormError("email", error.response.data.message);
    }
}
```

---

#### **Kịch bản 2: Verify OTP**

**Service Layer:**
```java
@Override
public String verifyOtp(String verifyToken, String otp) {
    
    // Bước 1: Lấy email từ verification token
    String email = (String) redisTemplate.opsForValue().get("verificationToken:" + verifyToken);
    if (email == null) {
        throw new BadRequestException("Verification token has expired or is invalid");
        // ← Trả về 400
    }

    // Bước 2: Lấy OTP từ Redis
    String storedOtp = (String) redisTemplate.opsForValue().get("otp:" + email);
    if (storedOtp == null) {
        throw new BadRequestException("OTP has expired. Please request a new one");
        // ← Trả về 400
    }

    // Bước 3: So sánh OTP
    if (!storedOtp.equals(otp)) {
        throw new BadRequestException("Invalid OTP. Please try again");
        // ← Trả về 400
    }

    // Bước 4: Lưu user vào DB và xóa Redis
    String tempUserJson = stringRedisTemplate.opsForValue().get("user:temp:" + email);
    UserRequestDTO registeredUser = deserializeUser(tempUserJson);
    userService.insertUser(registeredUser);
    
    // Xóa temp data
    stringRedisTemplate.delete("user:temp:" + email);
    redisTemplate.delete("otp:" + email);
    redisTemplate.delete("verificationToken:" + verifyToken);
    
    return "OTP verified successfully! Your registration is complete.";
}
```

**Request với OTP sai:**
```json
POST /api/auth/register/verify-otp
{
  "verificationToken": "abc123xyz",
  "otp": "999999"
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Invalid OTP. Please try again",
  "path": "/api/auth/register/verify-otp",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

---

### ✏️ **Case 2: Login**

#### **Service Layer:**
```java
@Service
public class AuthService {
    
    public AuthResponseDTO login(LoginRequestDTO req) {
        
        // Bước 1: Tìm user theo email
        UserEntity user = userRepository.findByEmail(req.getEmail());
        
        // Bước 2: Kiểm tra user tồn tại và password đúng
        if (user == null || !encoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Email or password is incorrect");
            // ← GlobalExceptionHandler bắt và trả về 401
            // ← Không nói rõ là email hay password sai → Bảo mật
        }

        // Bước 3: Tạo token và trả về
        String accessToken = jwtService.generateAccessToken(
            user.getUserId(), 
            user.getEmail(), 
            user.getRole().getRoleName()
        );
        
        JwTService.RefreshTokenBundle refreshBundle = jwtService.generateRefreshToken(user.getUserId());
        refreshStore.save(user.getUserId(), refreshBundle.jti());
        
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccessToken(accessToken);
        res.setRefreshToken(refreshBundle.token());
        return res;
    }
}
```

#### **Request với sai password:**
```json
POST /api/auth/login
{
  "email": "khanhlinhdsg@gmail.com",
  "password": "WrongPassword123"
}
```

**Response (401 Unauthorized):**
```json
{
  "status": 401,
  "error": "Unauthorized",
  "message": "Email or password is incorrect",
  "path": "/api/auth/login",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

**FE xử lý:**
```javascript
try {
    const response = await axios.post('/api/auth/login', credentials);
    // Success
    localStorage.setItem('accessToken', response.data.accessToken);
    router.push('/dashboard');
} catch (error) {
    if (error.response.status === 401) {
        // Hiển thị lỗi
        showErrorToast(error.response.data.message); // "Email or password is incorrect"
    }
}
```

---

### ✏️ **Case 3: Update User**

#### **Service Layer:**
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public boolean updateUserForUser(String userId, UserRequestDTO userRequestDTO) {
        
        // Bước 1: Tìm user
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
            // ← Nếu không tìm thấy → Tự động throw exception → Trả về 404
        
        // Bước 2: Kiểm tra role 'user' có tồn tại không
        RoleEntity role = roleRepository.findById("user")
            .orElseThrow(() -> new ResourceNotFoundException("Role 'user' not found"));
            // ← Nếu không tìm thấy role → Trả về 404
        
        // Bước 3: Update user
        user.setFirstName(userRequestDTO.getFirstName());
        user.setLastName(userRequestDTO.getLastName());
        user.setPhone(userRequestDTO.getPhone());
        user.setRole(role);
        
        userRepository.save(user);
        return true;
    }
}
```

#### **Request với userId không tồn tại:**
```json
PUT /api/users/NOTEXIST123
{
  "firstName": "Updated",
  "lastName": "Name"
}
```

**Response (404 Not Found):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: NOTEXIST123",
  "path": "/api/users/NOTEXIST123",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

---

### ✏️ **Case 4: Change Password**

#### **Service Layer:**
```java
@Service
public class UserServiceImpl implements UserService {
    
    @Override
    public boolean changePassword(String userId, RequestChangePasswordDTO requestDTO) {
        
        // Bước 1: Tìm user
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // Bước 2: Kiểm tra mật khẩu cũ
        if (!passwordEncoder.matches(requestDTO.oldPassword(), user.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
            // ← Trả về 400
        }
        
        // Bước 3: Kiểm tra mật khẩu mới phải khác mật khẩu cũ
        if (requestDTO.oldPassword().equals(requestDTO.newPassword())) {
            throw new BadRequestException("New password must be different from old password");
            // ← Trả về 400
        }
        
        // Bước 4: Kiểm tra confirm password
        if (!requestDTO.newPassword().equals(requestDTO.rewriteNewPassword())) {
            throw new BadRequestException("New password and confirm password do not match");
            // ← Trả về 400
        }
        
        // Bước 5: Lưu password mới
        String newPasswordEncoded = passwordEncoder.encode(requestDTO.newPassword());
        user.setPassword(newPasswordEncoded);
        userRepository.save(user);
        
        return true;
    }
}
```

#### **Request với mật khẩu cũ sai:**
```json
PUT /api/users/ABC123/change-password
{
  "oldPassword": "WrongOldPassword",
  "newPassword": "NewPassword@123",
  "rewriteNewPassword": "NewPassword@123"
}
```

**Response (400 Bad Request):**
```json
{
  "status": 400,
  "error": "Bad Request",
  "message": "Old password is incorrect",
  "path": "/api/users/ABC123/change-password",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

---

## 📊 Bảng Tổng Hợp HTTP Status Code

| Status | Exception | Khi Nào Dùng | Message Ví Dụ |
|--------|-----------|--------------|----------------|
| **400** | `BadRequestException` | Dữ liệu đầu vào không hợp lệ, validation failed | "OTP must be exactly 6 digits" |
| **401** | `UnauthorizedException` | Token hết hạn, session expired | "Token has expired. Please login again" |
| **401** | `InvalidCredentialsException` | Sai email/password khi login | "Email or password is incorrect" |
| **403** | `ForbiddenException` | Không có quyền truy cập | "Only administrators can delete users" |
| **404** | `ResourceNotFoundException` | Không tìm thấy resource | "User not found with id: ABC123" |
| **409** | `ConflictException` | Dữ liệu trùng lặp, xung đột logic | "Email already exists in database" |
| **500** | `RuntimeException` | Lỗi server không mong muốn | "An unexpected error occurred" |

---

## ✅ Checklist Khi Code Mới

### **Trong Service:**
- [ ] Thay `return false` bằng `throw new ResourceNotFoundException(...)`
- [ ] Thay `throw new RuntimeException(...)` bằng exception cụ thể
- [ ] Dùng `.orElseThrow()` thay vì `.orElse(null)` + if check
- [ ] Message lỗi phải rõ ràng, hữu ích cho user
- [ ] Chọn đúng exception: 400/401/403/404/409

### **Trong Controller:**
- [ ] Không cần try-catch (để GlobalExceptionHandler xử lý)
- [ ] Chỉ throw exception khi có MessagingException hoặc checked exception

### **Test API:**
- [ ] Kiểm tra response có đúng format không
- [ ] Kiểm tra status code có đúng không
- [ ] Kiểm tra message có rõ ràng không

---

## 🎓 Ví Dụ Migration Code Cũ → Code Mới

### ❌ **Trước đây (Code cũ):**

```java
@Service
public class UserServiceImpl {
    public boolean updateUser(String userId, UserDTO dto) {
        UserEntity user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return false; // ❌ FE không biết lỗi gì
        }
        
        RoleEntity role = roleRepository.findById(dto.getRoleId()).orElse(null);
        if (role == null) {
            return false; // ❌ FE không biết lỗi gì
        }
        
        user.setFirstName(dto.getFirstName());
        user.setRole(role);
        userRepository.save(user);
        return true;
    }
}

@RestController
public class UserController {
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDTO dto) {
        try {
            boolean result = userService.updateUser(userId, dto);
            if (!result) {
                return ResponseEntity.badRequest().body(Map.of("error", "Update failed"));
                // ❌ Không biết tại sao failed
            }
            return ResponseEntity.ok("Updated successfully");
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", e.getMessage()));
            // ❌ Mất thông tin, không chuẩn
        }
    }
}
```

### ✅ **Bây giờ (Code mới):**

```java
@Service
public class UserServiceImpl {
    public boolean updateUser(String userId, UserDTO dto) {
        // ✅ Tự động throw exception nếu không tìm thấy
        UserEntity user = userRepository.findById(userId)
            .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
        
        // ✅ Tự động throw exception nếu không tìm thấy
        RoleEntity role = roleRepository.findById(dto.getRoleId())
            .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + dto.getRoleId()));
        
        user.setFirstName(dto.getFirstName());
        user.setRole(role);
        userRepository.save(user);
        return true;
    }
}

@RestController
public class UserController {
    @PutMapping("/users/{userId}")
    public ResponseEntity<?> updateUser(@PathVariable String userId, @RequestBody UserDTO dto) {
        // ✅ Không cần try-catch, GlobalExceptionHandler tự xử lý
        userService.updateUser(userId, dto);
        return ResponseEntity.ok(Map.of("message", "User updated successfully"));
    }
}
```

**Response khi lỗi (tự động):**
```json
{
  "status": 404,
  "error": "Not Found",
  "message": "User not found with id: ABC123",
  "path": "/api/users/ABC123",
  "timestamp": "2026-01-10T10:30:45.123"
}
```

---

## 🔥 Lợi Ích Của Hệ Thống Mới

### **Cho Backend Developer:**
| Trước | Sau |
|-------|-----|
| Phải viết try-catch ở mọi nơi | Không cần try-catch |
| Phải tự tạo response lỗi | Tự động format bởi GlobalExceptionHandler |
| Mỗi nơi trả lỗi khác nhau | Thống nhất toàn project |
| Khó maintain khi có nhiều API | Sửa 1 chỗ (GlobalExceptionHandler) → Áp dụng toàn project |
| Code dài, khó đọc | Code ngắn gọn, rõ ràng |

### **Cho Frontend Developer:**
| Trước | Sau |
|-------|-----|
| Phải parse nhiều format khác nhau | Chỉ cần parse 1 format duy nhất |
| Không biết status code | Luôn có `status` rõ ràng |
| Message lỗi không rõ | Message luôn có ý nghĩa |
| Khó debug | Có `path`, `timestamp` để tracking |
| Phải xử lý nhiều case | Chỉ cần xử lý theo `status` |

### **Cho Team:**
- ✅ Chuẩn hóa cách xử lý lỗi → Giảm bug
- ✅ Onboarding dễ dàng → Đọc doc này là hiểu
- ✅ Review code nhanh hơn → Follow cùng 1 pattern
- ✅ Maintain dễ hơn → Biết lỗi nằm ở đâu

---

## 📁 Danh Sách File Trong Hệ Thống

### **Exception Classes:**
```
code/exception/
├── GlobalExceptionHandler.java         ← Bộ não xử lý lỗi
├── BadRequestException.java            ← 400 exception
├── UnauthorizedException.java          ← 401 exception
├── InvalidCredentialsException.java    ← 401 exception (login)
├── ForbiddenException.java             ← 403 exception
├── ResourceNotFoundException.java      ← 404 exception
├── ConflictException.java              ← 409 exception
└── EmailAlreadyExistsException.java    ← Extends ConflictException
```

### **DTO Classes:**
```
code/model/dto/error/
└── ErrorResponse.java                  ← Response format chuẩn
```

### **Service Classes (Đã update):**
```
code/services/
├── users/UserServiceImpl.java          ← Dùng ResourceNotFoundException, BadRequestException
├── registers/RegisterServiceImpl.java  ← Dùng ConflictException, BadRequestException
└── token/AuthService.java              ← Dùng InvalidCredentialsException, UnauthorizedException
```

### **Controller Classes (Đã update):**
```
code/controller/
└── users/RegisterController.java       ← Không còn try-catch
```

---

## 💡 Tips & Best Practices

### 1. **Message phải rõ ràng và hữu ích**

❌ **Không tốt:**
```java
throw new BadRequestException("Invalid input");
throw new ResourceNotFoundException("Not found");
```

✅ **Tốt:**
```java
throw new BadRequestException("OTP must be exactly 6 digits");
throw new ResourceNotFoundException("User not found with id: " + userId);
```

### 2. **Không leak thông tin nhạy cảm**

❌ **Không tốt (Lộ thông tin database):**
```java
throw new BadRequestException("SQL Error: Table 'users' doesn't exist");
throw new RuntimeException("Connection refused to localhost:5432");
```

✅ **Tốt:**
```java
throw new BadRequestException("Unable to process your request");
throw new RuntimeException("Service temporarily unavailable");
```

### 3. **Bảo mật khi login**

❌ **Không tốt (Hacker biết email có tồn tại không):**
```java
if (user == null) {
    throw new ResourceNotFoundException("Email not found");
}
if (!passwordMatches) {
    throw new BadRequestException("Password incorrect");
}
```

✅ **Tốt (Không tiết lộ thông tin):**
```java
if (user == null || !passwordMatches) {
    throw new InvalidCredentialsException("Email or password is incorrect");
}
```

### 4. **Throw exception sớm (Fail Fast)**

❌ **Không tốt:**
```java
public void createBooking(BookingDTO dto) {
    RoomEntity room = roomRepository.findById(dto.getRoomId()).orElse(null);
    UserEntity user = userRepository.findById(dto.getUserId()).orElse(null);
    
    if (room == null || user == null) {
        throw new ResourceNotFoundException("Invalid data");
    }
    // ... many lines of code
}
```

✅ **Tốt:**
```java
public void createBooking(BookingDTO dto) {
    // Validate ngay từ đầu
    RoomEntity room = roomRepository.findById(dto.getRoomId())
        .orElseThrow(() -> new ResourceNotFoundException("Room not found"));
    
    UserEntity user = userRepository.findById(dto.getUserId())
        .orElseThrow(() -> new ResourceNotFoundException("User not found"));
    
    // ... business logic
}
```

### 5. **Cần try-catch khi nào?**

✅ **Cần try-catch:**
- Khi gọi external service (API bên ngoài, email service...)
- Khi xử lý file I/O
- Khi có checked exception (MessagingException, IOException...)

```java
public void sendOtpEmail(String email, String otp) {
    try {
        emailService.sendEmail(email, "Your OTP", otp);
    } catch (MessagingException e) {
        throw new RuntimeException("Failed to send email. Please try again later");
        // Không expose chi tiết lỗi SMTP
    }
}
```

❌ **KHÔNG cần try-catch:**
- Trong Controller (để GlobalExceptionHandler xử lý)
- Khi throw custom exception
- Khi validate dữ liệu

---

## 🆘 Troubleshooting

### **Vấn đề 1: Exception không được bắt bởi GlobalExceptionHandler**

**Nguyên nhân:** Controller đang dùng try-catch bắt hết exception.

**Giải pháp:** Xóa try-catch trong Controller, để exception throw ra ngoài.

### **Vấn đề 2: Response không đúng format**

**Nguyên nhân:** Controller đang tự tạo response thay vì throw exception.

**Giải pháp:** Throw exception thay vì return ResponseEntity với body lỗi.

### **Vấn đề 3: Status code không đúng**

**Nguyên nhân:** Dùng sai loại exception.

**Giải pháp:** Xem lại bảng mapping và chọn đúng exception.

---

## 📞 Support

Nếu cần thêm exception mới hoặc có thắc mắc, hãy:
1. Xác định HTTP status code phù hợp (400/401/403/404/409/500)
2. Tạo class mới extend từ exception tương ứng
3. Thêm handler vào GlobalExceptionHandler nếu cần custom logic

---

**🎉 Chúc bạn code vui vẻ với hệ thống xử lý lỗi mới!**
