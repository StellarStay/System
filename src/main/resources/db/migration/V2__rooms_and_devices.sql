CREATE TABLE categories_room (
                                  category_id VARCHAR(50) PRIMARY KEY,
                                  category_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE rooms (
                       room_id VARCHAR(50) PRIMARY KEY,
                       room_name VARCHAR(255) NOT NULL,
                       title VARCHAR(255) NOT NULL,
                       description TEXT NOT NULL,
                       address VARCHAR(255) NOT NULL,
                       price_per_night DECIMAL(15,2) NOT NULL,
                       max_guests INT NOT NULL,
                       status VARCHAR(50) NOT NULL,
                       created_at TIMESTAMP NOT NULL,
                       category_id VARCHAR(50) NOT NULL,
                       owner_id VARCHAR(50) NOT NULL,

                       CONSTRAINT fk_room_category
                           FOREIGN KEY (category_id) REFERENCES categories_room(category_id),
                       CONSTRAINT fk_room_owner
                           FOREIGN KEY (owner_id) REFERENCES users(user_id)
);

CREATE TABLE image_rooms (
                             image_id VARCHAR(50) PRIMARY KEY,
                             image_url TEXT NOT NULL,
                             created_at TIMESTAMP NOT NULL,
                             room_id VARCHAR(50) NOT NULL,

                             CONSTRAINT fk_image_room
                                 FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE devices (
                         device_id VARCHAR(50) PRIMARY KEY,
                         device_name VARCHAR(100) NOT NULL,
                         device_type VARCHAR(100) NOT NULL,
                         brand VARCHAR(100) NOT NULL
);

CREATE TABLE device_of_room (
                                device_of_room_id VARCHAR(50) PRIMARY KEY,
                                status BOOLEAN NOT NULL,
                                created_date TIMESTAMP NOT NULL,
                                device_id VARCHAR(50) NOT NULL,
                                room_id VARCHAR(50) NOT NULL,

                                CONSTRAINT fk_dor_device
                                    FOREIGN KEY (device_id) REFERENCES devices(device_id),
                                CONSTRAINT fk_dor_room
                                    FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
