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
INSERT INTO cards (created_at, name, specific_type, mana_total_cost, text_rules, power, endurance, loyalty, collection, card_number, artist, edition, image_url, quantity, user_id) VALUES
(NOW(), 'Lightning Bolt', 'Instant', 1, 'Lightning Bolt deals 3 damage to any target.', 0, 0, 0, 'Core Set 2021', 137, 'Christopher Rush', 'M21', 'https://example.com/lightning_bolt.jpg', 4, 3), -- John Doe
(NOW(), 'Forest', 'Basic Land', 0, 'Add G.', 0, 0, 0, 'Zendikar Rising', 277, 'Adam Paquette', 'ZNR', 'https://example.com/forest.jpg', 10, 3), -- John Doe
(NOW(), 'Grizzly Bears', 'Creature - Bear', 2, '', 2, 2, 0, 'Dominaria United', 170, 'Carl Critchlow', 'DMU', 'https://example.com/grizzly_bears.jpg', 2, 4), -- Jane Smith
(NOW(), 'Counterspell', 'Instant', 2, 'Counter target spell.', 0, 0, 0, 'Modern Horizons 2', 43, 'Mark Zug', 'MH2', 'https://example.com/counterspell.jpg', 3, 5), -- Mike Wilson
(NOW(), 'Plains', 'Basic Land', 0, 'Add W.', 0, 0, 0, 'Innistrad: Midnight Hunt', 269, 'Alayna Danner', 'MID', 'https://example.com/plains.jpg', 8, 6), -- Sarah Jones
(NOW(), 'Goblin Guide', 'Creature - Goblin Scout', 1, 'Haste. Whenever Goblin Guide attacks, defending player reveals the top card of their library. If it''s a land card, that player puts it into their hand.', 2, 2, 0, 'Zendikar', 145, 'Brad Rigney', 'ZEN', 'https://example.com/goblin_guide.jpg', 1, 7), -- David Brown
(NOW(), 'Shock', 'Instant', 1, 'Shock deals 2 damage to any target.', 0, 0, 0, 'Core Set 2020', 156, 'Zoltan Boros', 'M20', 'https://example.com/shock.jpg', 4, 3),
(NOW(), 'Island', 'Basic Land', 0, 'Add U.', 0, 0, 0, 'Core Set 2020', 265, 'Jonas De Ro', 'M20', 'https://example.com/island.jpg', 10, 4),
(NOW(), 'Serra Angel', 'Creature - Angel', 5, 'Flying, vigilance.', 4, 4, 0, 'Core Set 2021', 34, 'Magali Villeneuve', 'M21', 'https://example.com/serra_angel.jpg', 2, 5),
(NOW(), 'Swamp', 'Basic Land', 0, 'Add B.', 0, 0, 0, 'Core Set 2020', 267, 'Adam Paquette', 'M20', 'https://example.com/swamp.jpg', 12, 6),
(NOW(), 'Mountain', 'Basic Land', 0, 'Add R.', 0, 0, 0, 'Core Set 2020', 266, 'John Avon', 'M20', 'https://example.com/mountain.jpg', 12, 7);

-- Insert types for all cards
INSERT INTO type_card (card_id, types) VALUES
(1, 'INSTANT'), (2, 'BASIC_LAND'), (3, 'CREATURE'), (4, 'INSTANT'), (5, 'BASIC_LAND'), (6, 'CREATURE'),
(7, 'INSTANT'), (8, 'BASIC_LAND'), (9, 'CREATURE'), (10, 'BASIC_LAND'), (11, 'BASIC_LAND');

-- Insert mana colors
INSERT INTO color_card (card_id, mana_colors) VALUES
(1, 'RED'), (2, 'GREEN'), (3, 'GREEN'), (4, 'BLUE'), (5, 'WHITE'), (6, 'RED'),
(7, 'RED'), (8, 'BLUE'), (9, 'WHITE'), (10, 'BLACK'), (11, 'RED');

-- Insert legalities
INSERT INTO legality_card (card_id, legality_format) VALUES
(1, 'COMMANDER'), (2, 'STANDARD'), (3, 'MODERN'), (4, 'COMMANDER'),
(5, 'STANDARD'), (6, 'LEGACY_VINTAGE'), (7, 'STANDARD'),
(8, 'STANDARD'), (9, 'MODERN'), (10, 'STANDARD'), (11, 'STANDARD');

-- Insert decks
INSERT INTO decks (created_at, is_public, type, max_cards, deck_name, user_id) VALUES
(NOW(), TRUE, 'STANDARD', 60, 'Red Aggro', 3),
(NOW(), TRUE, 'STANDARD', 60, 'Green Stompy', 3),
(NOW(), FALSE, 'MODERN', 60, 'Bear Force One', 4),
(NOW(), TRUE, 'COMMANDER', 100, 'Blue Control', 4),
(NOW(), TRUE, 'STANDARD', 60, 'Counter Burn', 5),
(NOW(), FALSE, 'MODERN', 60, 'Angel Glory', 5),
(NOW(), TRUE, 'STANDARD', 60, 'White Weenie', 6),
(NOW(), FALSE, 'STANDARD', 60, 'Swamp Lords', 6),
(NOW(), TRUE, 'STANDARD', 60, 'Goblin Rush', 7),
(NOW(), FALSE, 'COMMANDER', 100, 'Mountain Fury', 7),
(NOW(), TRUE, 'STANDARD', 60, 'Control Deck', 1),
(NOW(), TRUE, 'STANDARD', 60, 'Balanced Deck', 2);

-- Insert deck_card relationships
INSERT INTO deck_cards (id_deck, id_card, quantity) VALUES
(1, 1, 4), (1, 7, 4), (1, 11, 20),
(2, 2, 20), (2, 3, 4),
(3, 3, 4), (3, 2, 10), (3, 8, 8),
(4, 4, 4), (4, 8, 20),
(5, 4, 4), (5, 1, 4), (5, 7, 4),
(6, 9, 2), (6, 4, 4), (6, 8, 12),
(7, 5, 20), (7, 9, 4),
(8, 10, 20), (8, 5, 4),
(9, 6, 1), (9, 7, 4), (9, 11, 20),
(10, 11, 30), (10, 6, 1),
(11, 4, 4), (11, 8, 20),
(12, 2, 10), (12, 5, 10), (12, 10, 10);

INSERT INTO deck_likes (user_id, deck_id) VALUES
(6, 5),
(2, 5),
(4, 5),
(7, 5);