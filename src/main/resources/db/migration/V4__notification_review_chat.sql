CREATE TABLE notification_types (
                                    type_id VARCHAR(50) PRIMARY KEY,
                                    type_name VARCHAR(100) NOT NULL UNIQUE
);

CREATE TABLE notifications (
                               notification_id VARCHAR(50) PRIMARY KEY,
                               title VARCHAR(255) NOT NULL,
                               message TEXT NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               user_receiver_id VARCHAR(50) NOT NULL,
                               type_id VARCHAR(50) NOT NULL,

                               CONSTRAINT fk_notification_user
                                   FOREIGN KEY (user_receiver_id) REFERENCES users(user_id),
                               CONSTRAINT fk_notification_type
                                   FOREIGN KEY (type_id) REFERENCES notification_types(type_id)
);

CREATE TABLE reviews (
                         review_id VARCHAR(50) PRIMARY KEY,
                         stars DOUBLE PRECISION NOT NULL,
                         comment TEXT NOT NULL,
                         reply TEXT,
                         created_at TIMESTAMP NOT NULL,
                         user_rated_id VARCHAR(50),
                         user_replied_id VARCHAR(50),
                         room_id VARCHAR(50),

                         CONSTRAINT fk_review_user_rated
                             FOREIGN KEY (user_rated_id) REFERENCES users(user_id),
                         CONSTRAINT fk_review_user_replied
                             FOREIGN KEY (user_replied_id) REFERENCES users(user_id),
                         CONSTRAINT fk_review_room
                             FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);

CREATE TABLE chat_messages (
                               message_id VARCHAR(50) PRIMARY KEY,
                               message_content TEXT NOT NULL,
                               created_at TIMESTAMP NOT NULL,
                               sender_id VARCHAR(50) NOT NULL,
                               receiver_id VARCHAR(50) NOT NULL,
                               room_id VARCHAR(50) NOT NULL,

                               CONSTRAINT fk_chat_sender
                                   FOREIGN KEY (sender_id) REFERENCES users(user_id),
                               CONSTRAINT fk_chat_receiver
                                   FOREIGN KEY (receiver_id) REFERENCES users(user_id),
                               CONSTRAINT fk_chat_room
                                   FOREIGN KEY (room_id) REFERENCES rooms(room_id)
);
