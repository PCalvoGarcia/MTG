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

-- Insert cards associated with users
INSERT INTO cards (created_at, name, specific_type, mana_total_cost, text_rules, power, endurance, loyalty, collection, cart_number, artist, edition, image_url, quantity, user_id) VALUES
(NOW(), 'Lightning Bolt', 'Instant', 1, 'Lightning Bolt deals 3 damage to any target.', 0, 0, 0, 'Core Set 2021', 137, 'Christopher Rush', 'M21', 'https://example.com/lightning_bolt.jpg', 4, 3), -- John Doe
(NOW(), 'Forest', 'Basic Land', 0, 'Add G.', 0, 0, 0, 'Zendikar Rising', 277, 'Adam Paquette', 'ZNR', 'https://example.com/forest.jpg', 10, 3), -- John Doe
(NOW(), 'Grizzly Bears', 'Creature - Bear', 2, '', 2, 2, 0, 'Dominaria United', 170, 'Carl Critchlow', 'DMU', 'https://example.com/grizzly_bears.jpg', 2, 4), -- Jane Smith
(NOW(), 'Counterspell', 'Instant', 2, 'Counter target spell.', 0, 0, 0, 'Modern Horizons 2', 43, 'Mark Zug', 'MH2', 'https://example.com/counterspell.jpg', 3, 5), -- Mike Wilson
(NOW(), 'Plains', 'Basic Land', 0, 'Add W.', 0, 0, 0, 'Innistrad: Midnight Hunt', 269, 'Alayna Danner', 'MID', 'https://example.com/plains.jpg', 8, 6), -- Sarah Jones
(NOW(), 'Goblin Guide', 'Creature - Goblin Scout', 1, 'Haste. Whenever Goblin Guide attacks, defending player reveals the top card of their library. If it''s a land card, that player puts it into their hand.', 2, 2, 0, 'Zendikar', 145, 'Brad Rigney', 'ZEN', 'https://example.com/goblin_guide.jpg', 1, 7); -- David Brown

-- Insert card types
INSERT INTO type_card (card_id, types) VALUES
(1, 'INSTANT'),
(2, 'LAND'),
(3, 'CREATURE'),
(4, 'INSTANT'),
(5, 'LAND'),
(6, 'CREATURE');

-- Insert mana colors for cards
INSERT INTO color_card (card_id, mana_colors) VALUES
(1, 'RED'),
(2, 'GREEN'),
(3, 'GREEN'),
(4, 'BLUE'),
(5, 'WHITE'),
(6, 'RED');

-- Insert legality formats for cards
INSERT INTO legality_card (card_id, legality_format) VALUES
 (1, 'COMMANDER'),
(2, 'STANDARD'),
 (3, 'MODERN'),
 (4, 'COMMANDER'),
(5, 'STANDARD'),
(6, 'LEGACY_VINTAGE');
