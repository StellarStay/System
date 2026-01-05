CREATE TABLE payment_methods (
                                 payment_method_id VARCHAR(50) PRIMARY KEY,
                                 payment_method_name VARCHAR(100) NOT NULL,
                                 description VARCHAR(255)
);

CREATE TABLE bookings (
                          booking_id VARCHAR(50) PRIMARY KEY,
                          plan_check_in_time TIMESTAMP NOT NULL,
                          plan_check_out_time TIMESTAMP NOT NULL,
                          actual_check_in_time TIMESTAMP,
                          actual_check_out_time TIMESTAMP,
                          status VARCHAR(50) NOT NULL,
                          total_price DECIMAL(15,2) NOT NULL,
                          user_booking_id VARCHAR(50),
                          room_id VARCHAR(50) NOT NULL,

                          CONSTRAINT fk_booking_user
                              FOREIGN KEY (user_booking_id) REFERENCES users(user_id),
                          CONSTRAINT fk_booking_room
                              FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE payments (
                          payment_id VARCHAR(50) PRIMARY KEY,
                          payment_status VARCHAR(50) NOT NULL,
                          paid_at TIMESTAMP,
                          total_price DECIMAL(15,2) NOT NULL,
                          booking_id VARCHAR(50) NOT NULL,
                          payment_method_id VARCHAR(50) NOT NULL,

                          CONSTRAINT fk_payment_booking
                              FOREIGN KEY (booking_id) REFERENCES bookings(booking_id),
                          CONSTRAINT fk_payment_method
                              FOREIGN KEY (payment_method_id) REFERENCES payment_methods(payment_method_id)
);

CREATE TABLE booking_contact (
                                 id VARCHAR(50) PRIMARY KEY,
                                 booking_id VARCHAR(50) NOT NULL,
                                 first_name VARCHAR(100) NOT NULL,
                                 last_name VARCHAR(100) NOT NULL,
                                 email VARCHAR(100) NOT NULL,
                                 phone_number VARCHAR(20) NOT NULL,
                                 created_at TIMESTAMP NOT NULL,

                                 CONSTRAINT fk_booking_contact
                                     FOREIGN KEY (booking_id) REFERENCES bookings(booking_id)
);
