# ✅ ĐÃ CHUYỂN SANG COOKIE-BASED AUTHENTICATION!

## 🎯 NHỮNG GÌ ĐÃ THAY ĐỔI:

### 1. **XÓA Bearer Token Authorization trong Swagger**
- ❌ Xóa `@SecurityScheme` trong OpenApiConfig
- ❌ Xóa tất cả `@SecurityRequirement` 
- ❌ Không còn nút "Authorize" 🔓 trong Swagger UI

### 2. **SỬ DỤNG COOKIE TỰ ĐỘNG**
- ✅ Login → Tự động lưu `ACCESS_TOKEN` vào Cookie
- ✅ Mọi request sau đó → Browser/Swagger tự động gửi Cookie
- ✅ KHÔNG CẦN nhập token thủ công!

---

## 🔧 CƠ CHẾ HOẠT ĐỘNG:

### **LoginController.java:**
```java
@PostMapping("/login")
public ResponseEntity<AuthResponseDTO> login(..., HttpServletResponse response) {
    AuthResponseDTO authResponse = authService.login(request);
    
    // Set ACCESS_TOKEN vào cookie
    Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", authResponse.getAccessToken());
    accessTokenCookie.setHttpOnly(true); // Bảo mật
    accessTokenCookie.setPath("/");
    accessTokenCookie.setMaxAge(15 * 60); // 15 phút
    response.addCookie(accessTokenCookie);
    
    // Set REFRESH_TOKEN vào cookie
    Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", authResponse.getRefreshToken());
    refreshTokenCookie.setHttpOnly(true);
    refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
    response.addCookie(refreshTokenCookie);
    
    return ResponseEntity.ok(authResponse);
}
```

**Kết quả:** Sau khi login, browser sẽ tự động lưu 2 cookies:
- `ACCESS_TOKEN=eyJhbGciOiJIUzI1NiJ9...` (15 phút)
- `REFRESH_TOKEN=eyJhbGciOiJIUzI1NiJ9...` (7 ngày)

---

### **JwTAuthFilter.java:**
```java
String token = null;

// Ưu tiên lấy từ Authorization header (Postman, mobile app)
if (auth != null && auth.startsWith("Bearer ")) {
    token = auth.substring(7);
}
// Nếu không có header, lấy từ Cookie (Swagger, Browser)
else if (request.getCookies() != null) {
    for (Cookie cookie : request.getCookies()) {
        if ("ACCESS_TOKEN".equals(cookie.getName())) {
            token = cookie.getValue();
            break;
        }
    }
}

// Xử lý token...
```

**Kết quả:** JwtAuthFilter tự động:
1. Kiểm tra Authorization header (cho Postman)
2. Nếu không có → Kiểm tra Cookie (cho Swagger/Browser)
3. Parse token → Authenticate

---

### **Logout:**
```java
@PostMapping("/logout")
public ResponseEntity<String> logout(..., HttpServletResponse response) {
    // Xóa cookies
    Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", null);
    accessTokenCookie.setMaxAge(0); // Xóa ngay
    response.addCookie(accessTokenCookie);
    
    // Tương tự với refresh token
}
```

---

## 🚀 CÁCH SỬ DỤNG:

### **Trong Swagger UI:**

#### 1. **Login:**
```
POST /api/auth/login
Body:
{
  "username": "your_username",
  "password": "your_password"
}

Execute → 200 OK
```

**Điều gì xảy ra:**
- ✅ Server trả về accessToken + refreshToken trong response body
- ✅ Server TỰ ĐỘNG set 2 cookies: `ACCESS_TOKEN` và `REFRESH_TOKEN`
- ✅ Browser tự động lưu cookies
- ✅ **KHÔNG CẦN click "Authorize"!**

---

#### 2. **Gọi API cần authentication:**
```
GET /api/auth/me

Execute → 200 OK
{
  "User ID: v6z1kqQf"
}
```

**Điều gì xảy ra:**
- ✅ Browser TỰ ĐỘNG gửi Cookie `ACCESS_TOKEN` trong request
- ✅ JwtAuthFilter đọc token từ Cookie → Authenticate
- ✅ Request thành công!
- ✅ **KHÔNG CẦN làm gì thêm!**

---

#### 3. **Logout:**
```
POST /api/auth/logout

Execute → 200 OK
```

**Điều gì xảy ra:**
- ✅ Server xóa cookies
- ✅ Các request tiếp theo → 403 Forbidden (không còn token)

---

### **Trong Postman (vẫn dùng Bearer được):**

#### Cách 1: Dùng Cookie (giống Swagger)
1. Login → Postman tự động lưu cookie
2. Gọi API khác → Postman tự động gửi cookie

#### Cách 2: Dùng Bearer Token (cũ)
```
Authorization: Bearer eyJhbGciOiJIUzI1NiJ9...
```
→ Vẫn hoạt động bình thường!

**Lý do:** JwtAuthFilter **ƯU TIÊN** Authorization header trước, sau đó mới kiểm tra Cookie.

---

## 📊 SO SÁNH TRƯỚC VÀ SAU:

### ❌ **TRƯỚC (Bearer Token):**
```
1. Login → Copy accessToken
2. Click "Authorize" 🔓
3. Paste token vào
4. Click "Authorize" → Click "Close"
5. Gọi API → OK
6. Token hết hạn → Lặp lại từ bước 1
```

**Vấn đề:**
- Phải thủ công copy/paste token
- Phải click "Authorize" mỗi lần
- Token hết hạn → Phải làm lại

---

### ✅ **SAU (Cookie-based):**
```
1. Login → Execute
2. Gọi bất kỳ API nào → Execute → OK!
```

**Ưu điểm:**
- ✅ TỰ ĐỘNG authenticate sau khi login
- ✅ KHÔNG CẦN copy/paste token
- ✅ KHÔNG CÓ nút "Authorize"
- ✅ Cookie tự động expire sau 15 phút
- ✅ Refresh token tự động lưu 7 ngày

---

## 🔒 BẢO MẬT:

### **HttpOnly Cookie:**
```java
cookie.setHttpOnly(true);
```
- ✅ JavaScript KHÔNG thể đọc cookie (chống XSS)
- ✅ Chỉ gửi qua HTTP/HTTPS
- ✅ Browser tự động quản lý

### **Cookie Expiration:**
- `ACCESS_TOKEN`: 15 phút
- `REFRESH_TOKEN`: 7 ngày

### **Vẫn hỗ trợ Bearer Token:**
- Mobile app, Postman, API clients vẫn dùng `Authorization: Bearer <token>`

---

## 🎯 TEST NGAY:

### 1. **Restart ứng dụng:**
```bash
# Kill process cũ
netstat -ano | findstr :8080
taskkill /PID <PID> /F

# Chạy lại
mvnw spring-boot:run
```

### 2. **Mở Swagger:** `http://localhost:8080/swagger-ui/index.html`

### 3. **Login:**
```
POST /api/auth/login
Body:
{
  "username": "kho20232@gmail.com",
  "password": "your_password"
}
```

### 4. **Kiểm tra Cookie:**
- Mở DevTools (F12) → Tab "Application" → "Cookies" → `http://localhost:8080`
- Sẽ thấy: `ACCESS_TOKEN` và `REFRESH_TOKEN`

### 5. **Gọi /me:**
```
GET /api/auth/me

→ 200 OK: "User ID: xxx"
```

### 6. **Logout:**
```
POST /api/auth/logout

→ 200 OK
→ Cookies bị xóa
→ Gọi /me lại → 403 Forbidden
```

---

## 💡 XỬ LÝ LỖI:

### **Vẫn bị 403 sau khi login?**

1. **Kiểm tra Cookie có được set không:**
   - F12 → Application → Cookies → Xem có `ACCESS_TOKEN` không?

2. **Kiểm tra logs:**
   ```
   DEBUG JwTAuthFilter - Token found in Cookie
   DEBUG JwTAuthFilter - Authenticated user: xxx with role: xxx
   ```

3. **Clear browser cache và cookies:**
   - Ctrl + Shift + Del → Clear all

4. **Kiểm tra CORS:**
   - Swagger chạy trên `localhost:8080` → OK
   - Nếu frontend chạy `localhost:5173` → Cần config CORS cho credentials

---

## 🔄 CORS CHO FRONTEND (nếu cần):

Nếu bạn có frontend chạy trên domain khác (ví dụ `localhost:5173`):

```java
// SecurityConfig.java đã có sẵn:
.cors(cors -> cors.configurationSource(req -> {
    CorsConfiguration c = new CorsConfiguration();
    c.setAllowedOrigins(List.of("http://localhost:5173"));
    c.setAllowedMethods(List.of("GET","POST","PUT","DELETE","OPTIONS"));
    c.setAllowedHeaders(List.of("*"));
    c.setAllowCredentials(true); // ← QUAN TRỌNG: Cho phép gửi cookies!
    return c;
}))
```

---

## ✅ CHECKLIST:

- [x] ✅ Sửa LoginController: Set cookie khi login
- [x] ✅ Sửa LoginController: Xóa cookie khi logout
- [x] ✅ Sửa JwtAuthFilter: Đọc token từ Cookie
- [x] ✅ Xóa @SecurityScheme trong OpenApiConfig
- [x] ✅ Xóa tất cả @SecurityRequirement
- [x] ✅ Update SecurityConfig
- [ ] ⏳ Restart ứng dụng (user cần làm)
- [ ] ⏳ Test Login → /me → Logout (user cần làm)

---

## 🎉 KẾT QUẢ:

**GIỜ ĐÂY:**
1. ✅ Login trong Swagger → Execute
2. ✅ Gọi bất kỳ API nào → Execute → OK!
3. ✅ KHÔNG CẦN "Authorize" hay nhập token!
4. ✅ Browser tự động gửi cookie!
5. ✅ Postman vẫn dùng Bearer token được!

**HOÀN HẢO CHO DEVELOPMENT VỚI SWAGGER!** 🚀

