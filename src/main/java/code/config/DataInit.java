//// java
//package code.config;
//
//import code.model.entity.booking.BookingEntity;
//import code.model.entity.chatting.ChatMessageEntity;
//import code.model.entity.notification.NotificationEntity;
//import code.model.entity.notification.NotificationTypeEntity;
//import code.model.entity.payments.PaymentEntity;
//import code.model.entity.payments.PaymentMethodEntity;
//import code.model.entity.rating.ReviewsEntity;
//import code.model.entity.rooms.CategoriesRoomEntity;
//import code.model.entity.rooms.ImageRoomEntity;
//import code.model.entity.rooms.RoomEntity;
//import code.model.entity.rooms.DevicesEntity;
//import code.model.entity.rooms.DeviceOfRoomEntity;
//import code.model.entity.users.RoleEntity;
//import code.model.entity.users.UserEntity;
//import code.repository.booking.BookingRepository;
//import code.repository.chatting.ChatMessageRepository;
//import code.repository.notification.NotificationRepository;
//import code.repository.notification.NotificationTypeRepository;
//import code.repository.payments.PaymentMethodRepository;
//import code.repository.payments.PaymentRepository;
//import code.repository.rating.ReviewsRepository;
//import code.repository.rooms.CategoriesRoomRepository;
//import code.repository.rooms.ImageRoomRepository;
//import code.repository.rooms.RoomRepository;
//import code.repository.rooms.DevicesRepository;
//import code.repository.rooms.DeviceOfRoomRepository;
//import code.repository.users.RoleRepository;
//import code.repository.users.UserRepository;
//import code.util.RandomId;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.CommandLineRunner;
//import org.springframework.stereotype.Component;
//
//import java.math.BigDecimal;
//import java.time.LocalDate;
//import java.time.LocalDateTime;
//
//@Component
//public class DataInit implements CommandLineRunner {
//    @Autowired
//    private UserRepository userRepository;
//    @Autowired
//    private RoleRepository roleRepository;
//    @Autowired
//    private BookingRepository bookingRepository;
//    @Autowired
//    private RoomRepository roomRepository;
//    @Autowired
//    private CategoriesRoomRepository categoriesRoomRepository;
//    @Autowired
//    private ImageRoomRepository imageRoomRepository;
//    @Autowired
//    private PaymentMethodRepository paymentMethodRepository;
//    @Autowired
//    private PaymentRepository paymentRepository;
//    @Autowired
//    private NotificationRepository notificationRepository;
//    @Autowired
//    private NotificationTypeRepository notificationTypeRepository;
//    @Autowired
//    private ReviewsRepository reviewsRepository;
//    @Autowired
//    private ChatMessageRepository chatMessageRepository;
//    @Autowired
//    private DevicesRepository devicesRepository;
//    @Autowired
//    private DeviceOfRoomRepository deviceOfRoomRepository;
//
//
//    @Override
//    public void run(String... args) throws Exception {
//        // Kiểm tra nếu đã có dữ liệu thì không init lại
//        if (roleRepository.count() > 0 || userRepository.count() > 0 || bookingRepository.count() > 0) {
//            System.out.println("⚠️ Data already exists. Skipping initialization.");
//            return;
//        }
//
//        System.out.println("🚀 Starting data initialization...");
//
//        // 1. Tạo Roles
//        RoleEntity adminRole = new RoleEntity();
//        adminRole.setRoleId("admin");
//        adminRole.setRoleName("ADMIN");
//        adminRole.setDescription("Administrator with full access");
//        roleRepository.save(adminRole);
//
//        RoleEntity ownerRole = new RoleEntity();
//        ownerRole.setRoleId("owner");
//        ownerRole.setRoleName("OWNER");
//        ownerRole.setDescription("Room owner who can manage their rooms");
//        roleRepository.save(ownerRole);
//
//        RoleEntity userRole = new RoleEntity();
//        userRole.setRoleId("user");
//        userRole.setRoleName("USER");
//        userRole.setDescription("Regular user who can book rooms");
//        roleRepository.save(userRole);
//
//        // 2. Tạo Users
//        UserEntity admin = new UserEntity();
//        admin.setUserId(RandomId.generateRoomId(10));
//        admin.setIdCard("123456789");
//        admin.setFirstName("Admin");
//        admin.setLastName("System");
//        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
//        admin.setPhone("0901234567");
//        admin.setEmail("admin@bookingroom.com");
//        admin.setPassword("$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO"); // password: admin123
//        admin.setGender(true);
//        admin.setCreatedAt(LocalDateTime.now());
//        admin.setStatus(true);
//        admin.setRole(adminRole);
//        userRepository.save(admin);
//
//        UserEntity owner1 = new UserEntity();
//        owner1.setUserId(RandomId.generateRoomId(10));
//        owner1.setIdCard("987654321");
//        owner1.setFirstName("Nguyen");
//        owner1.setLastName("Van A");
//        owner1.setDateOfBirth(LocalDate.of(1985, 5, 15));
//        owner1.setPhone("0912345678");
//        owner1.setEmail("owner1@gmail.com");
//        owner1.setPassword("$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO"); // password: owner123
//        owner1.setGender(true);
//        owner1.setCreatedAt(LocalDateTime.now());
//        owner1.setStatus(true);
//        owner1.setRole(ownerRole);
//        userRepository.save(owner1);
//
//        UserEntity owner2 = new UserEntity();
//        owner2.setUserId(RandomId.generateRoomId(10));
//        owner2.setIdCard("456789123");
//        owner2.setFirstName("Tran");
//        owner2.setLastName("Thi B");
//        owner2.setDateOfBirth(LocalDate.of(1988, 8, 20));
//        owner2.setPhone("0923456789");
//        owner2.setEmail("owner2@gmail.com");
//        owner2.setPassword("$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO");
//        owner2.setGender(false);
//        owner2.setCreatedAt(LocalDateTime.now());
//        owner2.setStatus(true);
//        owner2.setRole(ownerRole);
//        userRepository.save(owner2);
//
//        UserEntity user1 = new UserEntity();
//        user1.setUserId(RandomId.generateRoomId(10));
//        user1.setIdCard("147258369");
//        user1.setFirstName("Le");
//        user1.setLastName("Van C");
//        user1.setDateOfBirth(LocalDate.of(1995, 3, 10));
//        user1.setPhone("0934567890");
//        user1.setEmail("user1@gmail.com");
//        user1.setPassword("$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO"); // password: user123
//        user1.setGender(true);
//        user1.setCreatedAt(LocalDateTime.now());
//        user1.setStatus(true);
//        user1.setRole(userRole);
//        userRepository.save(user1);
//
//        UserEntity user2 = new UserEntity();
//        user2.setUserId(RandomId.generateRoomId(10));
//        user2.setIdCard("369258147");
//        user2.setFirstName("Pham");
//        user2.setLastName("Thi D");
//        user2.setDateOfBirth(LocalDate.of(1998, 12, 25));
//        user2.setPhone("0945678901");
//        user2.setEmail("user2@gmail.com");
//        user2.setPassword("$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO");
//        user2.setGender(false);
//        user2.setCreatedAt(LocalDateTime.now());
//        user2.setStatus(true);
//        user2.setRole(userRole);
//        userRepository.save(user2);
//
//        // 3. Tạo Categories Room
//        CategoriesRoomEntity hotelCategory = new CategoriesRoomEntity();
//        hotelCategory.setCategoryId(RandomId.generateRoomId(10));
//        hotelCategory.setCategoryName("Hotel");
//        categoriesRoomRepository.save(hotelCategory);
//
//        CategoriesRoomEntity apartmentCategory = new CategoriesRoomEntity();
//        apartmentCategory.setCategoryId(RandomId.generateRoomId(10));
//        apartmentCategory.setCategoryName("Apartment");
//        categoriesRoomRepository.save(apartmentCategory);
//
//        CategoriesRoomEntity villaCategory = new CategoriesRoomEntity();
//        villaCategory.setCategoryId(RandomId.generateRoomId(10));
//        villaCategory.setCategoryName("Villa");
//        categoriesRoomRepository.save(villaCategory);
//
//        CategoriesRoomEntity homestayCategory = new CategoriesRoomEntity();
//        homestayCategory.setCategoryId(RandomId.generateRoomId(10));
//        homestayCategory.setCategoryName("Homestay");
//        categoriesRoomRepository.save(homestayCategory);
//
//        // 4. Tạo Rooms
//        RoomEntity room1 = new RoomEntity();
//        room1.setRoomId(RandomId.generateRoomId(10));
//        room1.setRoomName("Deluxe Ocean View");
//        room1.setTitle("Luxury Deluxe Room with Ocean View");
//        room1.setDescription("Beautiful deluxe room with stunning ocean view, includes king-size bed, private balcony, and modern amenities.");
//        room1.setAddress("123 Beach Road, Da Nang");
//        room1.setPrice_per_night(new BigDecimal("1500000"));
//        room1.setMax_guests(2);
//        room1.setCreatedAt(LocalDateTime.now());
//        room1.setStatus("APPROVED");
//        room1.setCategory(hotelCategory);
//        room1.setOwner(owner1);
//        roomRepository.save(room1);
//
//        RoomEntity room2 = new RoomEntity();
//        room2.setRoomId(RandomId.generateRoomId(10));
//        room2.setRoomName("Modern City Apartment");
//        room2.setTitle("Spacious 2-Bedroom Apartment in City Center");
//        room2.setDescription("Modern apartment with 2 bedrooms, fully equipped kitchen, living room, and great city views.");
//        room2.setAddress("456 Le Loi Street, Ho Chi Minh City");
//        room2.setPrice_per_night(new BigDecimal("2000000"));
//        room2.setMax_guests(4);
//        room2.setCreatedAt(LocalDateTime.now());
//        room2.setStatus("APPROVED");
//        room2.setCategory(apartmentCategory);
//        room2.setOwner(owner1);
//        roomRepository.save(room2);
//
//        RoomEntity room3 = new RoomEntity();
//        room3.setRoomId(RandomId.generateRoomId(10));
//        room3.setRoomName("Luxury Villa with Pool");
//        room3.setTitle("5-Bedroom Villa with Private Pool and Garden");
//        room3.setDescription("Stunning luxury villa featuring 5 bedrooms, private swimming pool, tropical garden, BBQ area, and mountain views.");
//        room3.setAddress("789 Mountain View, Dalat");
//        room3.setPrice_per_night(new BigDecimal("5000000"));
//        room3.setMax_guests(10);
//        room3.setCreatedAt(LocalDateTime.now());
//        room3.setStatus("APPROVED");
//        room3.setCategory(villaCategory);
//        room3.setOwner(owner2);
//        roomRepository.save(room3);
//
//        RoomEntity room4 = new RoomEntity();
//        room4.setRoomId(RandomId.generateRoomId(10));
//        room4.setRoomName("Cozy Homestay");
//        room4.setTitle("Traditional Vietnamese Homestay Experience");
//        room4.setDescription("Authentic Vietnamese homestay with friendly hosts, home-cooked meals, and cultural experiences.");
//        room4.setAddress("321 Rural Road, Hoi An");
//        room4.setPrice_per_night(new BigDecimal("800000"));
//        room4.setMax_guests(3);
//        room4.setCreatedAt(LocalDateTime.now());
//        room4.setStatus("APPROVED");
//        room4.setCategory(homestayCategory);
//        room4.setOwner(owner2);
//        roomRepository.save(room4);
//
//        RoomEntity room5 = new RoomEntity();
//        room5.setRoomId(RandomId.generateRoomId(10));
//        room5.setRoomName("Budget Hotel Room");
//        room5.setTitle("Affordable Room Near Airport");
//        room5.setDescription("Clean and comfortable budget room, perfect for short stays. Close to airport and public transport.");
//        room5.setAddress("555 Airport Road, Hanoi");
//        room5.setPrice_per_night(new BigDecimal("500000"));
//        room5.setMax_guests(2);
//        room5.setCreatedAt(LocalDateTime.now());
//        room5.setStatus("PENDING");
//        room5.setCategory(hotelCategory);
//        room5.setOwner(owner1);
//        roomRepository.save(room5);
//
//        // 5. Tạo Image Rooms
//        ImageRoomEntity img1_1 = new ImageRoomEntity();
//        img1_1.setImageId(RandomId.generateRoomId(10));
//        img1_1.setImageUrl("https://images.unsplash.com/photo-1566073771259-6a8506099945");
//        img1_1.setCreatedAt(LocalDateTime.now());
//        img1_1.setRoom(room1);
//        imageRoomRepository.save(img1_1);
//
//        ImageRoomEntity img1_2 = new ImageRoomEntity();
//        img1_2.setImageId(RandomId.generateRoomId(10));
//        img1_2.setImageUrl("https://images.unsplash.com/photo-1582719478250-c89cae4dc85b");
//        img1_2.setCreatedAt(LocalDateTime.now());
//        img1_2.setRoom(room1);
//        imageRoomRepository.save(img1_2);
//
//        ImageRoomEntity img2_1 = new ImageRoomEntity();
//        img2_1.setImageId(RandomId.generateRoomId(10));
//        img2_1.setImageUrl("https://images.unsplash.com/photo-1522708323590-d24dbb6b0267");
//        img2_1.setCreatedAt(LocalDateTime.now());
//        img2_1.setRoom(room2);
//        imageRoomRepository.save(img2_1);
//
//        ImageRoomEntity img3_1 = new ImageRoomEntity();
//        img3_1.setImageId(RandomId.generateRoomId(10));
//        img3_1.setImageUrl("https://images.unsplash.com/photo-1613490493576-7fde63acd811");
//        img3_1.setCreatedAt(LocalDateTime.now());
//        img3_1.setRoom(room3);
//        imageRoomRepository.save(img3_1);
//
//        ImageRoomEntity img4_1 = new ImageRoomEntity();
//        img4_1.setImageId(RandomId.generateRoomId(10));
//        img4_1.setImageUrl("https://images.unsplash.com/photo-1571896349842-33c89424de2d");
//        img4_1.setCreatedAt(LocalDateTime.now());
//        img4_1.setRoom(room4);
//        imageRoomRepository.save(img4_1);
//
//        // 5.a Tạo Devices
//        DevicesEntity tv = new DevicesEntity();
//        tv.setDeviceId(RandomId.generateRoomId(10));
//        tv.setDeviceName("Smart TV");
//        tv.setDeviceType("Entertainment");
//        tv.setBrand("Samsung");
//        devicesRepository.save(tv);
//
//        DevicesEntity ac = new DevicesEntity();
//        ac.setDeviceId(RandomId.generateRoomId(10));
//        ac.setDeviceName("Air Conditioner");
//        ac.setDeviceType("Climate");
//        ac.setBrand("Daikin");
//        devicesRepository.save(ac);
//
//        DevicesEntity fridge = new DevicesEntity();
//        fridge.setDeviceId(RandomId.generateRoomId(10));
//        fridge.setDeviceName("Refrigerator");
//        fridge.setDeviceType("Appliance");
//        fridge.setBrand("Panasonic");
//        devicesRepository.save(fridge);
//
//        DevicesEntity wifi = new DevicesEntity();
//        wifi.setDeviceId(RandomId.generateRoomId(10));
//        wifi.setDeviceName("WiFi Router");
//        wifi.setDeviceType("Network");
//        wifi.setBrand("TP-Link");
//        devicesRepository.save(wifi);
//
//        DevicesEntity kettle = new DevicesEntity();
//        kettle.setDeviceId(RandomId.generateRoomId(10));
//        kettle.setDeviceName("Electric Kettle");
//        kettle.setDeviceType("Appliance");
//        kettle.setBrand("Philips");
//        devicesRepository.save(kettle);
//
//        // 5.b Link Devices to Rooms (DeviceOfRoom)
//        DeviceOfRoomEntity d1 = new DeviceOfRoomEntity();
//        d1.setDeviceOfRoomId(RandomId.generateRoomId(10));
//        d1.setStatus(true);
//        d1.setCreatedDate(LocalDateTime.now());
//        d1.setDevice(tv);
//        d1.setRoom(room1);
//        deviceOfRoomRepository.save(d1);
//
//        DeviceOfRoomEntity d2 = new DeviceOfRoomEntity();
//        d2.setDeviceOfRoomId(RandomId.generateRoomId(10));
//        d2.setStatus(true);
//        d2.setCreatedDate(LocalDateTime.now());
//        d2.setDevice(ac);
//        d2.setRoom(room1);
//        deviceOfRoomRepository.save(d2);
//
//        DeviceOfRoomEntity d3 = new DeviceOfRoomEntity();
//        d3.setDeviceOfRoomId(RandomId.generateRoomId(10));
//        d3.setStatus(true);
//        d3.setCreatedDate(LocalDateTime.now());
//        d3.setDevice(wifi);
//        d3.setRoom(room2);
//        deviceOfRoomRepository.save(d3);
//
//        DeviceOfRoomEntity d4 = new DeviceOfRoomEntity();
//        d4.setDeviceOfRoomId(RandomId.generateRoomId(10));
//        d4.setStatus(true);
//        d4.setCreatedDate(LocalDateTime.now());
//        d4.setDevice(fridge);
//        d4.setRoom(room3);
//        deviceOfRoomRepository.save(d4);
//
//        DeviceOfRoomEntity d5 = new DeviceOfRoomEntity();
//        d5.setDeviceOfRoomId(RandomId.generateRoomId(10));
//        d5.setStatus(false);
//        d5.setCreatedDate(LocalDateTime.now());
//        d5.setDevice(kettle);
//        d5.setRoom(room4);
//        deviceOfRoomRepository.save(d5);
//
//        // 6. Tạo Payment Methods
//        PaymentMethodEntity cashMethod = new PaymentMethodEntity();
//        cashMethod.setPaymentMethodId(RandomId.generateRoomId(10));
//        cashMethod.setPaymentMethodName("Cash");
//        cashMethod.setDescription("Pay with cash at the property");
//        paymentMethodRepository.save(cashMethod);
//
//        PaymentMethodEntity creditCardMethod = new PaymentMethodEntity();
//        creditCardMethod.setPaymentMethodId(RandomId.generateRoomId(10));
//        creditCardMethod.setPaymentMethodName("Credit Card");
//        creditCardMethod.setDescription("Pay with credit/debit card");
//        paymentMethodRepository.save(creditCardMethod);
//
//        PaymentMethodEntity momoMethod = new PaymentMethodEntity();
//        momoMethod.setPaymentMethodId(RandomId.generateRoomId(10));
//        momoMethod.setPaymentMethodName("MoMo");
//        momoMethod.setDescription("Pay with MoMo e-wallet");
//        paymentMethodRepository.save(momoMethod);
//
//        PaymentMethodEntity bankTransferMethod = new PaymentMethodEntity();
//        bankTransferMethod.setPaymentMethodId(RandomId.generateRoomId(10));
//        bankTransferMethod.setPaymentMethodName("Bank Transfer");
//        bankTransferMethod.setDescription("Direct bank transfer");
//        paymentMethodRepository.save(bankTransferMethod);
//
//        // 7. Tạo Bookings
//        // Booking 1: Đã confirm, chưa check-in (trong tương lai)
//        BookingEntity booking1 = new BookingEntity();
//        booking1.setBookingId(RandomId.generateRoomId(10));
//        booking1.setPlanCheckInTime(LocalDateTime.now().plusDays(5));
//        booking1.setPlanCheckOutTime(LocalDateTime.now().plusDays(8));
//        booking1.setActualCheckInTime(null); // Chưa check-in
//        booking1.setActualCheckOutTime(null); // Chưa check-out
//        booking1.setStatus("CONFIRM");
//        booking1.setTotalPrice(new BigDecimal("4500000"));
//        booking1.setUser(user1);
//        booking1.setRoom(room1);
//        bookingRepository.save(booking1);
//
//        // Booking 2: Đã confirm, chưa check-in (trong tương lai)
//        BookingEntity booking2 = new BookingEntity();
//        booking2.setBookingId(RandomId.generateRoomId(10));
//        booking2.setPlanCheckInTime(LocalDateTime.now().plusDays(10));
//        booking2.setPlanCheckOutTime(LocalDateTime.now().plusDays(15));
//        booking2.setActualCheckInTime(null); // Chưa check-in
//        booking2.setActualCheckOutTime(null); // Chưa check-out
//        booking2.setStatus("CONFIRM");
//        booking2.setTotalPrice(new BigDecimal("10000000"));
//        booking2.setUser(user2);
//        booking2.setRoom(room2);
//        bookingRepository.save(booking2);
//
//        // Booking 3: Pending, chưa thanh toán
//        BookingEntity booking3 = new BookingEntity();
//        booking3.setBookingId(RandomId.generateRoomId(10));
//        booking3.setPlanCheckInTime(LocalDateTime.now().plusDays(3));
//        booking3.setPlanCheckOutTime(LocalDateTime.now().plusDays(5));
//        booking3.setActualCheckInTime(null);
//        booking3.setActualCheckOutTime(null);
//        booking3.setStatus("PENDING");
//        booking3.setTotalPrice(new BigDecimal("10000000"));
//        booking3.setUser(user1);
//        booking3.setRoom(room3);
//        bookingRepository.save(booking3);
//
//        // Booking 4: Đã hủy (trong quá khứ)
//        BookingEntity booking4 = new BookingEntity();
//        booking4.setBookingId(RandomId.generateRoomId(10));
//        booking4.setPlanCheckInTime(LocalDateTime.now().minusDays(10));
//        booking4.setPlanCheckOutTime(LocalDateTime.now().minusDays(8));
//        booking4.setActualCheckInTime(null); // Đã hủy nên không có actual
//        booking4.setActualCheckOutTime(null);
//        booking4.setStatus("CANCELLED");
//        booking4.setTotalPrice(new BigDecimal("1600000"));
//        booking4.setUser(user2);
//        booking4.setRoom(room4);
//        bookingRepository.save(booking4);
//
//        // 8. Tạo Payments
//        PaymentEntity payment1 = new PaymentEntity();
//        payment1.setPaymentId(RandomId.generateRoomId(10));
//        payment1.setPaymentStatus("Success");
//        payment1.setPaidAt(LocalDateTime.now());
//        payment1.setTotalPrice(new BigDecimal("4500000"));
//        payment1.setBooking(booking1);
//        payment1.setPaymentMethod(creditCardMethod);
//        paymentRepository.save(payment1);
//
//        PaymentEntity payment2 = new PaymentEntity();
//        payment2.setPaymentId(RandomId.generateRoomId(10));
//        payment2.setPaymentStatus("Success");
//        payment2.setPaidAt(LocalDateTime.now());
//        payment2.setTotalPrice(new BigDecimal("10000000"));
//        payment2.setBooking(booking2);
//        payment2.setPaymentMethod(momoMethod);
//        paymentRepository.save(payment2);
//
//        PaymentEntity payment3 = new PaymentEntity();
//        payment3.setPaymentId(RandomId.generateRoomId(10));
//        payment3.setPaymentStatus("Pending");
//        payment3.setPaidAt(null);
//        payment3.setTotalPrice(new BigDecimal("10000000"));
//        payment3.setBooking(booking3);
//        payment3.setPaymentMethod(bankTransferMethod);
//        paymentRepository.save(payment3);
//
//        PaymentEntity payment4 = new PaymentEntity();
//        payment4.setPaymentId(RandomId.generateRoomId(10));
//        payment4.setPaymentStatus("Failed");
//        payment4.setPaidAt(LocalDateTime.now().minusDays(10));
//        payment4.setTotalPrice(new BigDecimal("1600000"));
//        payment4.setBooking(booking4);
//        payment4.setPaymentMethod(cashMethod);
//        paymentRepository.save(payment4);
//
//        // 9. Tạo Notification Types
//        NotificationTypeEntity bookingNotif = new NotificationTypeEntity();
//        bookingNotif.setTypeId(RandomId.generateRoomId(10));
//        bookingNotif.setTypeName("Booking");
//        notificationTypeRepository.save(bookingNotif);
//
//        NotificationTypeEntity paymentNotif = new NotificationTypeEntity();
//        paymentNotif.setTypeId(RandomId.generateRoomId(10));
//        paymentNotif.setTypeName("Payment");
//        notificationTypeRepository.save(paymentNotif);
//
//        NotificationTypeEntity systemNotif = new NotificationTypeEntity();
//        systemNotif.setTypeId(RandomId.generateRoomId(10));
//        systemNotif.setTypeName("System");
//        notificationTypeRepository.save(systemNotif);
//
//        NotificationTypeEntity reviewNotif = new NotificationTypeEntity();
//        reviewNotif.setTypeId(RandomId.generateRoomId(10));
//        reviewNotif.setTypeName("Review");
//        notificationTypeRepository.save(reviewNotif);
//
//        // 10. Tạo Notifications
//        NotificationEntity notif1 = new NotificationEntity();
//        notif1.setNotificationId(RandomId.generateRoomId(10));
//        notif1.setTitle("Booking Confirmed");
//        notif1.setMessage("Your booking for Deluxe Ocean View has been confirmed!");
//        notif1.setCreatedAt(LocalDateTime.now());
//        notif1.setUserReceiver(user1);
//        notif1.setNotificationType(bookingNotif);
//        notificationRepository.save(notif1);
//
//        NotificationEntity notif2 = new NotificationEntity();
//        notif2.setNotificationId(RandomId.generateRoomId(10));
//        notif2.setTitle("Payment Successful");
//        notif2.setMessage("Payment of 4,500,000 VND has been processed successfully.");
//        notif2.setCreatedAt(LocalDateTime.now());
//        notif2.setUserReceiver(user1);
//        notif2.setNotificationType(paymentNotif);
//        notificationRepository.save(notif2);
//
//        NotificationEntity notif3 = new NotificationEntity();
//        notif3.setNotificationId(RandomId.generateRoomId(10));
//        notif3.setTitle("New Booking Request");
//        notif3.setMessage("You have a new booking request for your Modern City Apartment.");
//        notif3.setCreatedAt(LocalDateTime.now());
//        notif3.setUserReceiver(owner1);
//        notif3.setNotificationType(bookingNotif);
//        notificationRepository.save(notif3);
//
//        NotificationEntity notif4 = new NotificationEntity();
//        notif4.setNotificationId(RandomId.generateRoomId(10));
//        notif4.setTitle("Welcome to Booking Room");
//        notif4.setMessage("Thank you for joining our platform. Start exploring amazing rooms now!");
//        notif4.setCreatedAt(LocalDateTime.now());
//        notif4.setUserReceiver(user2);
//        notif4.setNotificationType(systemNotif);
//        notificationRepository.save(notif4);
//
//        // 11. Tạo Reviews
//        ReviewsEntity review1 = new ReviewsEntity();
//        review1.setReviewId(RandomId.generateRoomId(10));
//        review1.setStars(5.0);
//        review1.setComment("Amazing room with beautiful ocean view! Very clean and comfortable. Highly recommended!");
//        review1.setReply("Thank you for your wonderful review! We're glad you enjoyed your stay.");
//        review1.setCreatedAt(LocalDateTime.now().minusDays(2));
//        review1.setUserRated(user1);
//        review1.setUserReplied(owner1);
//        review1.setRoom(room1);
//        reviewsRepository.save(review1);
//
//        ReviewsEntity review2 = new ReviewsEntity();
//        review2.setReviewId(RandomId.generateRoomId(10));
//        review2.setStars(4.5);
//        review2.setComment("Great apartment in the city center. Very convenient location and modern facilities.");
//        review2.setReply(null);
//        review2.setCreatedAt(LocalDateTime.now().minusDays(5));
//        review2.setUserRated(user2);
//        review2.setUserReplied(null);
//        review2.setRoom(room2);
//        reviewsRepository.save(review2);
//
//        ReviewsEntity review3 = new ReviewsEntity();
//        review3.setReviewId(RandomId.generateRoomId(10));
//        review3.setStars(4.0);
//        review3.setComment("Nice villa with great pool. Perfect for family vacation. A bit far from city but worth it.");
//        review3.setReply("Thank you! We're happy you and your family had a great time.");
//        review3.setCreatedAt(LocalDateTime.now().minusDays(7));
//        review3.setUserRated(user1);
//        review3.setUserReplied(owner2);
//        review3.setRoom(room3);
//        reviewsRepository.save(review3);
//
//        ReviewsEntity review4 = new ReviewsEntity();
//        review4.setReviewId(RandomId.generateRoomId(10));
//        review4.setStars(5.0);
//        review4.setComment("Authentic homestay experience! The hosts were very friendly and the food was delicious.");
//        review4.setReply(null);
//        review4.setCreatedAt(LocalDateTime.now().minusDays(3));
//        review4.setUserRated(user2);
//        review4.setUserReplied(null);
//        review4.setRoom(room4);
//        reviewsRepository.save(review4);
//
//        // 12. Tạo Chat Messages
//        ChatMessageEntity chat1 = new ChatMessageEntity();
//        chat1.setMessageId(RandomId.generateRoomId(10));
//        chat1.setMessageContent("Hi, I'm interested in booking your Deluxe Ocean View room. Is it available from Dec 15-18?");
//        chat1.setCreatedAt(LocalDateTime.now().minusDays(7));
//        chat1.setSender(user1);
//        chat1.setReceiver(owner1);
//        chat1.setRoom(room1);
//        chatMessageRepository.save(chat1);
//
//        ChatMessageEntity chat2 = new ChatMessageEntity();
//        chat2.setMessageId(RandomId.generateRoomId(10));
//        chat2.setMessageContent("Yes, the room is available for those dates! Would you like to proceed with the booking?");
//        chat2.setCreatedAt(LocalDateTime.now().minusDays(7).plusHours(1));
//        chat2.setSender(owner1);
//        chat2.setReceiver(user1);
//        chat2.setRoom(room1);
//        chatMessageRepository.save(chat2);
//
//        ChatMessageEntity chat3 = new ChatMessageEntity();
//        chat3.setMessageId(RandomId.generateRoomId(10));
//        chat3.setMessageContent("Great! Yes, I'd like to book it. Does the price include breakfast?");
//        chat3.setCreatedAt(LocalDateTime.now().minusDays(7).plusHours(2));
//        chat3.setSender(user1);
//        chat3.setReceiver(owner1);
//        chat3.setRoom(room1);
//        chatMessageRepository.save(chat3);
//
//        ChatMessageEntity chat4 = new ChatMessageEntity();
//        chat4.setMessageId(RandomId.generateRoomId(10));
//        chat4.setMessageContent("Hello, can I bring pets to the villa?");
//        chat4.setCreatedAt(LocalDateTime.now().minusDays(4));
//        chat4.setSender(user2);
//        chat4.setReceiver(owner2);
//        chat4.setRoom(room3);
//        chatMessageRepository.save(chat4);
//
//        ChatMessageEntity chat5 = new ChatMessageEntity();
//        chat5.setMessageId(RandomId.generateRoomId(10));
//        chat5.setMessageContent("Unfortunately, we don't allow pets at this property. Sorry for the inconvenience.");
//        chat5.setCreatedAt(LocalDateTime.now().minusDays(4).plusHours(3));
//        chat5.setSender(owner2);
//        chat5.setReceiver(user2);
//        chat5.setRoom(room3);
//        chatMessageRepository.save(chat5);
//
//        ChatMessageEntity chat6 = new ChatMessageEntity();
//        chat6.setMessageId(RandomId.generateRoomId(10));
//        chat6.setMessageContent("Do you offer airport pickup service?");
//        chat6.setCreatedAt(LocalDateTime.now().minusDays(2));
//        chat6.setSender(user1);
//        chat6.setReceiver(owner1);
//        chat6.setRoom(room2);
//        chatMessageRepository.save(chat6);
//
//        System.out.println("✅ Data initialization completed successfully!");
//        System.out.println("📊 Summary:");
//        System.out.println("   - Roles: 3");
//        System.out.println("   - Users: 5 (1 Admin, 2 Owners, 2 Users)");
//        System.out.println("   - Categories: 4");
//        System.out.println("   - Rooms: 5");
//        System.out.println("   - Images: 5");
//        System.out.println("   - Devices: 5");
//        System.out.println("   - Device assignments: 5");
//        System.out.println("   - Payment Methods: 4");
//        System.out.println("   - Bookings: 4");
//        System.out.println("   - Payments: 4");
//        System.out.println("   - Notification Types: 4");
//        System.out.println("   - Notifications: 4");
//        System.out.println("   - Reviews: 4");
//        System.out.println("   - Chat Messages: 6");
//    }
//}
//
