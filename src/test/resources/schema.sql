DROP TABLE IF EXISTS legality_card;
DROP TABLE IF EXISTS color_card;
DROP TABLE IF EXISTS type_card;
DROP TABLE IF EXISTS cards;
DROP TABLE IF EXISTS user_roles;
DROP TABLE IF EXISTS users;


CREATE TABLE IF NOT EXISTS users (
	id BIGINT AUTO_INCREMENT PRIMARY KEY,
	username VARCHAR(255) NOT NULL UNIQUE,
	email VARCHAR(255) NOT NULL UNIQUE,
	password VARCHAR(255) NOT NULL
);

CREATE TABLE IF NOT EXISTS user_roles (
	user_id BIGINT NOT NULL,
	roles VARCHAR(255) NOT NULL,
	PRIMARY KEY (user_id, roles),
	FOREIGN KEY (user_id) REFERENCES users(id)
);

CREATE TABLE IF NOT EXISTS cards (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    created_at DATETIME NOT NULL,
    name VARCHAR(255) NOT NULL,
    specific_type VARCHAR(255),
    mana_total_cost INT NOT NULL,
    text_rules TEXT,
    power INT,
    endurance INT,
    loyalty INT,
    collection VARCHAR(255),
    cart_number INT,
    artist VARCHAR(255),
    edition VARCHAR(255),
    image_url VARCHAR(255),
    quantity INT NOT NULL,
    user_id BIGINT NOT NULL,
    FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS type_card (
    card_id BIGINT NOT NULL,
    types VARCHAR(255) NOT NULL,
    PRIMARY KEY (card_id, types),
    FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS color_card (
    card_id BIGINT NOT NULL,
    mana_colors VARCHAR(255) NOT NULL,
    PRIMARY KEY (card_id, mana_colors),
    FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS legality_card (
    card_id BIGINT NOT NULL,
    legality_format VARCHAR(255) NOT NULL,
    PRIMARY KEY (card_id, legality_format),
    FOREIGN KEY (card_id) REFERENCES cards(id) ON DELETE CASCADE
);
