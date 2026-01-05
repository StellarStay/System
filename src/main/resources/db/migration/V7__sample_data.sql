-- =====================================================
-- V7: SAMPLE DATA FOR DEVELOPMENT
-- =====================================================

-- ===== CATEGORIES (Loại phòng) =====
INSERT INTO categories_room (category_id, category_name) VALUES
    ('hotel', 'Hotel'),
    ('apartment', 'Apartment'),
    ('resort', 'Resort'),
    ('villa', 'Villa'),
    ('homestay', 'Homestay'),
    ('hostel', 'Hostel'),
    ('guesthouse', 'Guest House'),
    ('motel', 'Motel');

-- ===== DEVICES (Thiết bị/Tiện nghi phòng) =====
INSERT INTO devices (device_id, device_name, device_type, brand) VALUES
    -- Điện tử & Giải trí
    ('wifi', 'WiFi', 'Internet', 'Standard'),
    ('tv', 'Smart TV', 'Entertainment', 'Samsung'),
    ('cable-tv', 'Cable TV', 'Entertainment', 'Standard'),
    ('speaker', 'Bluetooth Speaker', 'Entertainment', 'JBL'),

    -- Điều hòa & Làm mát
    ('ac', 'Air Conditioner', 'Climate Control', 'Daikin'),
    ('fan', 'Ceiling Fan', 'Climate Control', 'Panasonic'),
    ('heater', 'Heater', 'Climate Control', 'Standard'),

    -- Nhà bếp
    ('kitchen', 'Full Kitchen', 'Kitchen', 'Standard'),
    ('fridge', 'Refrigerator', 'Kitchen', 'LG'),
    ('microwave', 'Microwave', 'Kitchen', 'Sharp'),
    ('stove', 'Gas Stove', 'Kitchen', 'Standard'),
    ('kettle', 'Electric Kettle', 'Kitchen', 'Philips'),
    ('cookware', 'Cookware Set', 'Kitchen', 'Standard'),

    -- Giặt ủi
    ('washing', 'Washing Machine', 'Laundry', 'Electrolux'),
    ('dryer', 'Dryer', 'Laundry', 'Electrolux'),
    ('iron', 'Iron & Board', 'Laundry', 'Philips'),

    -- Ngoại thất & Tiện ích
    ('pool', 'Swimming Pool', 'Outdoor', 'Standard'),
    ('gym', 'Gym/Fitness', 'Facilities', 'Standard'),
    ('parking', 'Free Parking', 'Facilities', 'Standard'),
    ('elevator', 'Elevator', 'Facilities', 'Standard'),
    ('balcony', 'Balcony/Terrace', 'Outdoor', 'Standard'),
    ('garden', 'Garden', 'Outdoor', 'Standard'),

    -- An ninh & An toàn
    ('security', '24/7 Security', 'Safety', 'Standard'),
    ('cctv', 'CCTV', 'Safety', 'Hikvision'),
    ('safe', 'Safe Box', 'Safety', 'Standard'),
    ('fire-alarm', 'Fire Alarm', 'Safety', 'Standard'),

    -- Phòng tắm
    ('hot-water', 'Hot Water', 'Bathroom', 'Standard'),
    ('bathtub', 'Bathtub', 'Bathroom', 'Standard'),
    ('hairdryer', 'Hair Dryer', 'Bathroom', 'Panasonic'),

    -- Khác
    ('pet', 'Pet Allowed', 'Policy', 'Standard'),
    ('workspace', 'Dedicated Workspace', 'Workspace', 'Standard'),
    ('bbq', 'BBQ Grill', 'Outdoor', 'Standard');

-- ===== SAMPLE USERS (Owner & Regular Users) =====
INSERT INTO users (user_id, id_card, first_name, last_name, date_of_birth, phone, email, password, gender, status, created_at, role_id) VALUES
    -- Owner accounts (password: owner123)
    ('owner-001', '001234567890', 'Nguyen Van', 'Anh', '1985-05-15', '0901234567', 'owner1@gmail.com', '$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO', true, true, CURRENT_TIMESTAMP, 'owner'),
    ('owner-002', '002234567890', 'Tran Thi', 'Binh', '1988-08-20', '0912345678', 'owner2@gmail.com', '$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO', false, true, CURRENT_TIMESTAMP, 'owner'),
    ('owner-003', '003234567890', 'Le Van', 'Cuong', '1990-03-10', '0923456789', 'owner3@gmail.com', '$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO', true, true, CURRENT_TIMESTAMP, 'owner'),

    -- Regular users (password: user123)
    ('user-001', '101234567890', 'Pham Thi', 'Dao', '1995-12-25', '0934567890', 'user1@gmail.com', '$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO', false, true, CURRENT_TIMESTAMP, 'user'),
    ('user-002', '102234567890', 'Hoang Van', 'En', '1998-07-18', '0945678901', 'user2@gmail.com', '$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO', true, true, CURRENT_TIMESTAMP, 'user'),
    ('user-003', '103234567890', 'Vo Thi', 'Phuong', '2000-01-05', '0956789012', 'user3@gmail.com', '$2a$10$xQ8mP5eqY3zN8b7T1h9OQudYzqXRjGQKh5TlYqF9bQxN3Y5eqX8zO', false, true, CURRENT_TIMESTAMP, 'user');

-- ===== SAMPLE ROOMS =====
INSERT INTO rooms (room_id, room_name, title, description, address, price_per_night, max_guests, status, created_at, category_id, owner_id) VALUES
    -- Owner 1's rooms (Hanoi)
    ('room-001', 'Luxury Suite Downtown', 'Spacious Suite in Heart of Hanoi',
     'Beautiful suite with modern amenities in the city center. Walking distance to Hoan Kiem Lake and Old Quarter. Perfect for business travelers and tourists.',
     'Floor 15, Lotte Center, 54 Lieu Giai, Ba Dinh, Hanoi', 2500000, 2, 'APPROVED', CURRENT_TIMESTAMP, 'hotel', 'owner-001'),

    ('room-002', 'Cozy West Lake Apartment', 'Modern 2BR Apartment with Lake View',
     'Fully furnished 2-bedroom apartment overlooking West Lake. Equipped kitchen, living room, and balcony. Quiet neighborhood, close to restaurants and cafes.',
     '123 Yen Phu, Tay Ho, Hanoi', 1800000, 4, 'APPROVED', CURRENT_TIMESTAMP, 'apartment', 'owner-001'),

    ('room-003', 'Budget Hostel Bed', 'Shared Dorm - Old Quarter',
     'Clean and safe hostel bed in shared dorm room. Perfect for backpackers. Common kitchen, lounge area, and free walking tours.',
     '45 Hang Bac, Hoan Kiem, Hanoi', 150000, 1, 'APPROVED', CURRENT_TIMESTAMP, 'hostel', 'owner-001'),

    -- Owner 2's rooms (Da Nang)
    ('room-004', 'Beachfront Villa', 'Luxury 4BR Villa with Private Pool',
     'Stunning beachfront villa with private pool, garden, and direct beach access. 4 bedrooms, modern kitchen, BBQ area. Ideal for families and groups.',
     '123 Vo Nguyen Giap, Son Tra, Da Nang', 8500000, 8, 'APPROVED', CURRENT_TIMESTAMP, 'villa', 'owner-002'),

    ('room-005', 'My Khe Beach Resort Room', 'Deluxe Ocean View Room',
     'Resort room with breathtaking ocean views. Access to resort facilities: infinity pool, spa, restaurant, and private beach.',
     'My Khe Beach Resort, Ngu Hanh Son, Da Nang', 3200000, 2, 'APPROVED', CURRENT_TIMESTAMP, 'resort', 'owner-002'),

    ('room-006', 'Cozy Homestay Han River', 'Traditional Vietnamese Homestay',
     'Experience local culture in this family-run homestay by Han River. Home-cooked meals available. Friendly hosts who speak English.',
     '78 Bach Dang, Hai Chau, Da Nang', 450000, 3, 'APPROVED', CURRENT_TIMESTAMP, 'homestay', 'owner-002'),

    -- Owner 3's rooms (Ho Chi Minh)
    ('room-007', 'District 1 Luxury Apartment', 'Premium 3BR Apartment - City Center',
     'High-end apartment in the heart of Saigon. Landmark 81 view, gym, pool, and 24/7 security. Close to shopping malls and restaurants.',
     'Vinhomes Central Park, Binh Thanh, Ho Chi Minh', 4500000, 6, 'APPROVED', CURRENT_TIMESTAMP, 'apartment', 'owner-003'),

    ('room-008', 'Boutique Hotel Room', 'Deluxe Room - Ben Thanh Market Area',
     'Stylish boutique hotel room near Ben Thanh Market. Includes breakfast, airport shuttle, and rooftop bar access.',
     '234 Le Thanh Ton, District 1, Ho Chi Minh', 1900000, 2, 'APPROVED', CURRENT_TIMESTAMP, 'hotel', 'owner-003'),

    ('room-009', 'Budget Motel', 'Clean & Affordable Room - Pham Ngu Lao',
     'Simple, clean room in backpacker area. AC, hot water, free WiFi. Great value for budget travelers.',
     '56 Bui Vien, District 1, Ho Chi Minh', 350000, 2, 'APPROVED', CURRENT_TIMESTAMP, 'motel', 'owner-003'),

    -- Pending room (waiting approval)
    ('room-010', 'New Hanoi Apartment', 'Brand New Studio - Cau Giay District',
     'Just renovated studio apartment. Modern furniture, full amenities. Near universities and tech parks.',
     '789 Tran Duy Hung, Cau Giay, Hanoi', 1200000, 2, 'PENDING', CURRENT_TIMESTAMP, 'apartment', 'owner-001');

-- ===== SAMPLE ROOM IMAGES =====
INSERT INTO image_rooms (image_id, image_url, created_at, room_id) VALUES
    -- Room 001 images
    ('img-001-1', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-001/main.jpg', CURRENT_TIMESTAMP, 'room-001'),
    ('img-001-2', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-001/bedroom.jpg', CURRENT_TIMESTAMP, 'room-001'),
    ('img-001-3', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-001/view.jpg', CURRENT_TIMESTAMP, 'room-001'),

    -- Room 002 images
    ('img-002-1', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-002/main.jpg', CURRENT_TIMESTAMP, 'room-002'),
    ('img-002-2', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-002/kitchen.jpg', CURRENT_TIMESTAMP, 'room-002'),

    -- Room 004 images (villa)
    ('img-004-1', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-004/main.jpg', CURRENT_TIMESTAMP, 'room-004'),
    ('img-004-2', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-004/pool.jpg', CURRENT_TIMESTAMP, 'room-004'),
    ('img-004-3', 'https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/room-004/beach.jpg', CURRENT_TIMESTAMP, 'room-004');

-- ===== DEVICE OF ROOM (Link devices to rooms) =====
INSERT INTO device_of_room (device_of_room_id, status, created_date, device_id, room_id) VALUES
    -- Room 001 (Luxury Suite) - Full amenities
    ('dor-001-1', true, CURRENT_TIMESTAMP, 'wifi', 'room-001'),
    ('dor-001-2', true, CURRENT_TIMESTAMP, 'ac', 'room-001'),
    ('dor-001-3', true, CURRENT_TIMESTAMP, 'tv', 'room-001'),
    ('dor-001-4', true, CURRENT_TIMESTAMP, 'fridge', 'room-001'),
    ('dor-001-5', true, CURRENT_TIMESTAMP, 'safe', 'room-001'),
    ('dor-001-6', true, CURRENT_TIMESTAMP, 'hot-water', 'room-001'),
    ('dor-001-7', true, CURRENT_TIMESTAMP, 'elevator', 'room-001'),

    -- Room 002 (Apartment) - Kitchen & Living
    ('dor-002-1', true, CURRENT_TIMESTAMP, 'wifi', 'room-002'),
    ('dor-002-2', true, CURRENT_TIMESTAMP, 'ac', 'room-002'),
    ('dor-002-3', true, CURRENT_TIMESTAMP, 'tv', 'room-002'),
    ('dor-002-4', true, CURRENT_TIMESTAMP, 'kitchen', 'room-002'),
    ('dor-002-5', true, CURRENT_TIMESTAMP, 'fridge', 'room-002'),
    ('dor-002-6', true, CURRENT_TIMESTAMP, 'washing', 'room-002'),
    ('dor-002-7', true, CURRENT_TIMESTAMP, 'balcony', 'room-002'),
    ('dor-002-8', true, CURRENT_TIMESTAMP, 'parking', 'room-002'),

    -- Room 003 (Hostel) - Basic
    ('dor-003-1', true, CURRENT_TIMESTAMP, 'wifi', 'room-003'),
    ('dor-003-2', true, CURRENT_TIMESTAMP, 'fan', 'room-003'),
    ('dor-003-3', true, CURRENT_TIMESTAMP, 'security', 'room-003'),

    -- Room 004 (Villa) - Luxury everything
    ('dor-004-1', true, CURRENT_TIMESTAMP, 'wifi', 'room-004'),
    ('dor-004-2', true, CURRENT_TIMESTAMP, 'ac', 'room-004'),
    ('dor-004-3', true, CURRENT_TIMESTAMP, 'tv', 'room-004'),
    ('dor-004-4', true, CURRENT_TIMESTAMP, 'kitchen', 'room-004'),
    ('dor-004-5', true, CURRENT_TIMESTAMP, 'pool', 'room-004'),
    ('dor-004-6', true, CURRENT_TIMESTAMP, 'bbq', 'room-004'),
    ('dor-004-7', true, CURRENT_TIMESTAMP, 'garden', 'room-004'),
    ('dor-004-8', true, CURRENT_TIMESTAMP, 'parking', 'room-004'),
    ('dor-004-9', true, CURRENT_TIMESTAMP, 'security', 'room-004'),

    -- Room 007 (Premium Apartment)
    ('dor-007-1', true, CURRENT_TIMESTAMP, 'wifi', 'room-007'),
    ('dor-007-2', true, CURRENT_TIMESTAMP, 'ac', 'room-007'),
    ('dor-007-3', true, CURRENT_TIMESTAMP, 'tv', 'room-007'),
    ('dor-007-4', true, CURRENT_TIMESTAMP, 'kitchen', 'room-007'),
    ('dor-007-5', true, CURRENT_TIMESTAMP, 'gym', 'room-007'),
    ('dor-007-6', true, CURRENT_TIMESTAMP, 'pool', 'room-007'),
    ('dor-007-7', true, CURRENT_TIMESTAMP, 'elevator', 'room-007'),
    ('dor-007-8', true, CURRENT_TIMESTAMP, 'security', 'room-007'),
    ('dor-007-9', true, CURRENT_TIMESTAMP, 'parking', 'room-007');

-- ===== SAMPLE BOOKINGS =====
INSERT INTO bookings (booking_id, plan_check_in_time, plan_check_out_time, actual_check_in_time, actual_check_out_time, status, total_price, user_booking_id, room_id) VALUES
    -- Completed booking
    ('booking-001', '2025-12-20 14:00:00', '2025-12-25 12:00:00', '2025-12-20 14:30:00', '2025-12-25 11:00:00', 'COMPLETED', 12500000, 'user-001', 'room-001'),

    -- Confirmed upcoming booking
    ('booking-002', '2026-02-01 15:00:00', '2026-02-05 11:00:00', NULL, NULL, 'CONFIRMED', 7200000, 'user-002', 'room-002'),

    -- Current booking (checked in)
    ('booking-003', '2026-01-03 14:00:00', '2026-01-10 12:00:00', '2026-01-03 14:15:00', NULL, 'CONFIRMED', 59500000, 'user-003', 'room-004'),

    -- Pending booking
    ('booking-004', '2026-01-20 16:00:00', '2026-01-22 10:00:00', NULL, NULL, 'PENDING', 3800000, 'user-001', 'room-008'),

    -- Cancelled booking
    ('booking-005', '2025-12-15 14:00:00', '2025-12-18 12:00:00', NULL, NULL, 'CANCELLED', 5400000, 'user-002', 'room-007');

-- ===== BOOKING CONTACTS =====
INSERT INTO booking_contact (id, booking_id, first_name, last_name, email, phone_number, created_at) VALUES
    ('contact-001', 'booking-001', 'Pham Thi', 'Dao', 'user1@gmail.com', '0934567890', '2025-12-15 10:30:00'),
    ('contact-002', 'booking-002', 'Hoang Van', 'En', 'user2@gmail.com', '0945678901', '2026-01-02 09:15:00'),
    ('contact-003', 'booking-003', 'Vo Thi', 'Phuong', 'user3@gmail.com', '0956789012', '2025-12-28 16:20:00'),
    ('contact-004', 'booking-004', 'Pham Thi', 'Dao', 'user1@gmail.com', '0934567890', '2026-01-04 11:00:00'),
    ('contact-005', 'booking-005', 'Hoang Van', 'En', 'user2@gmail.com', '0945678901', '2025-12-10 14:45:00');

-- ===== SAMPLE PAYMENTS =====
INSERT INTO payments (payment_id, payment_status, paid_at, total_price, booking_id, payment_method_id) VALUES
    ('payment-001', 'Success', '2025-12-15 10:35:00', 12500000, 'booking-001', 'momo'),
    ('payment-002', 'Success', '2026-01-02 09:20:00', 7200000, 'booking-002', 'momo'),
    ('payment-003', 'Success', '2025-12-28 16:25:00', 59500000, 'booking-003', 'cash'),
    ('payment-004', 'Pending', NULL, 3800000, 'booking-004', 'momo'),
    ('payment-005', 'Failed', NULL, 5400000, 'booking-005', 'momo');

-- ===== SAMPLE REVIEWS =====
INSERT INTO reviews (review_id, stars, comment, reply, created_at, user_rated_id, user_replied_id, room_id) VALUES
    ('review-001', 5.0, 'Absolutely amazing suite! The view was spectacular and the service was top-notch. Will definitely come back!',
     'Thank you so much for your kind words! We look forward to hosting you again.',
     '2025-12-26 10:00:00', 'user-001', 'owner-001', 'room-001'),

    ('review-002', 4.5, 'Great apartment with beautiful lake view. Very clean and well-equipped kitchen. Only minor issue was parking was a bit tight.',
     'Thank you for your feedback! We are working on improving the parking situation.',
     '2025-12-28 15:30:00', 'user-002', 'owner-001', 'room-002'),

    ('review-003', 5.0, 'Dream villa! Perfect for our family vacation. Kids loved the pool and beach access. Highly recommend!',
     NULL,
     '2026-01-04 09:00:00', 'user-003', NULL, 'room-004'),

    ('review-004', 3.5, 'Good location but room was smaller than expected. WiFi was excellent though.',
     'Thank you for your review. We have updated our room description to be more accurate about the size.',
     '2025-11-15 11:00:00', 'user-001', 'owner-003', 'room-008');

-- ===== SAMPLE NOTIFICATIONS =====
INSERT INTO notifications (notification_id, title, message, created_at, user_receiver_id, type_id) VALUES
    ('notif-001', 'Booking Confirmed', 'Your booking for Luxury Suite Downtown has been confirmed. Booking ID: booking-001',
     '2025-12-15 10:30:00', 'user-001', 'booking'),

    ('notif-002', 'Payment Successful', 'Your payment of 12,500,000 VND has been processed successfully.',
     '2025-12-15 10:35:00', 'user-001', 'payment'),

    ('notif-003', 'New Booking Request', 'You have a new booking request for Cozy West Lake Apartment.',
     '2026-01-02 09:15:00', 'owner-001', 'booking'),

    ('notif-004', 'New Review', 'You received a 5-star review for Luxury Suite Downtown!',
     '2025-12-26 10:00:00', 'owner-001', 'review'),

    ('notif-005', 'System Maintenance', 'Scheduled system maintenance on Jan 10, 2026 from 2AM to 4AM.',
     '2026-01-03 10:00:00', 'user-001', 'system');

-- ===== SAMPLE CHAT MESSAGES =====
INSERT INTO chat_messages (message_id, message_content, created_at, sender_id, receiver_id, room_id) VALUES
    ('chat-001', 'Hi! I am interested in booking your suite. Is it available for next week?',
     '2025-12-14 10:00:00', 'user-001', 'owner-001', 'room-001'),

    ('chat-002', 'Yes, it is available! What dates are you looking for?',
     '2025-12-14 10:15:00', 'owner-001', 'user-001', 'room-001'),

    ('chat-003', 'December 20-25. Also, do you offer airport pickup?',
     '2025-12-14 10:20:00', 'user-001', 'owner-001', 'room-001'),

    ('chat-004', 'Yes, we can arrange airport pickup for an additional fee. Let me send you the details.',
     '2025-12-14 10:25:00', 'owner-001', 'user-001', 'room-001'),

    ('chat-005', 'Hello! Does your villa have a BBQ grill? Planning a family gathering.',
     '2025-12-20 14:00:00', 'user-003', 'owner-002', 'room-004'),

    ('chat-006', 'Yes! We have a BBQ grill and all necessary equipment. Feel free to use it!',
     '2025-12-20 14:10:00', 'owner-002', 'user-003', 'room-004');

