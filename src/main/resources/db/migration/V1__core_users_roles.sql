CREATE TABLE roles (
                       role_id VARCHAR(50) PRIMARY KEY,
                       role_name VARCHAR(50) NOT NULL UNIQUE,
                       description VARCHAR(255)
);

CREATE TABLE users (
                       user_id VARCHAR(50) PRIMARY KEY,
                       id_card VARCHAR(20) NOT NULL UNIQUE,
                       first_name VARCHAR(100) NOT NULL,
                       last_name VARCHAR(100) NOT NULL,
                       date_of_birth DATE NOT NULL,
                       phone VARCHAR(20) NOT NULL,
                       email VARCHAR(100) NOT NULL UNIQUE,
                       password VARCHAR(255) NOT NULL,
                       gender BOOLEAN NOT NULL,
                       status BOOLEAN NOT NULL DEFAULT TRUE,
                       created_at TIMESTAMP NOT NULL,
                       role_id VARCHAR(50) NOT NULL,

                       CONSTRAINT fk_user_role
                           FOREIGN KEY (role_id) REFERENCES roles(role_id)
);
