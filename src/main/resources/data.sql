-- Insert users
INSERT INTO users (username, email, password) VALUES
('admin', 'admin@happytravel.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2'),
('user', 'user@happytravel.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2'),
('john_doe', 'john@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2'),
('jane_smith', 'jane@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2'),
('mike_wilson', 'mike@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2'),
('sarah_jones', 'sarah@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2'),
('david_brown', 'david@example.com', '$2a$10$HsMF2wIVlZAelTWGNHD/r.lbHJemKWx0.HEfqHKHF91CR8R3fDjX2');

-- Insert user roles
INSERT INTO user_roles (user_id, roles) VALUES
(1, 'ADMIN'),
(2, 'USER'),
(3, 'USER'),
(4, 'USER'),
(5, 'USER'),
(6, 'USER'),
(7, 'USER');
