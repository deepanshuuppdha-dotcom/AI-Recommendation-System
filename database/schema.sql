-- AI Recommendation System: MySQL 8 schema + sample data
CREATE DATABASE IF NOT EXISTS recsys_db CHARACTER SET utf8mb4;
USE recsys_db;

DROP TABLE IF EXISTS recommendations_log;
DROP TABLE IF EXISTS interactions;
DROP TABLE IF EXISTS user_preferences;
DROP TABLE IF EXISTS system_settings;
DROP TABLE IF EXISTS items;
DROP TABLE IF EXISTS users;

CREATE TABLE users (
  id            INT AUTO_INCREMENT PRIMARY KEY,
  name          VARCHAR(80)  NOT NULL,
  email         VARCHAR(120) NOT NULL UNIQUE,
  password_hash VARCHAR(200) NOT NULL,
  role          ENUM('ADMIN','USER') NOT NULL DEFAULT 'USER',
  created_at    TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE items (
  id          INT AUTO_INCREMENT PRIMARY KEY,
  title       VARCHAR(120) NOT NULL,
  category    VARCHAR(40)  NOT NULL,
  tags        VARCHAR(200) NOT NULL DEFAULT '',
  description VARCHAR(255) NOT NULL DEFAULT '',
  popularity  INT NOT NULL DEFAULT 0
);

CREATE TABLE user_preferences (
  user_id  INT NOT NULL,
  category VARCHAR(40) NOT NULL,
  weight   INT NOT NULL DEFAULT 0,          -- 0 (not interested) .. 5 (love it)
  PRIMARY KEY (user_id, category),
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE
);

CREATE TABLE interactions (
  id         INT AUTO_INCREMENT PRIMARY KEY,
  user_id    INT NOT NULL,
  item_id    INT NOT NULL,
  type       ENUM('VIEW','CLICK','LIKE','PURCHASE') NOT NULL,
  created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

CREATE TABLE recommendations_log (
  id        INT AUTO_INCREMENT PRIMARY KEY,
  user_id   INT NOT NULL,
  item_id   INT NOT NULL,
  score     DOUBLE NOT NULL,
  shown_at  TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
  clicked   BOOLEAN NOT NULL DEFAULT FALSE,
  converted BOOLEAN NOT NULL DEFAULT FALSE,
  FOREIGN KEY (user_id) REFERENCES users(id) ON DELETE CASCADE,
  FOREIGN KEY (item_id) REFERENCES items(id) ON DELETE CASCADE
);

CREATE TABLE system_settings (
  param_name  VARCHAR(60) PRIMARY KEY,
  param_value VARCHAR(60) NOT NULL
);

INSERT INTO system_settings (param_name, param_value) VALUES
  ('weight_preference', '0.5'),
  ('weight_behavior',   '0.3'),
  ('weight_popularity', '0.2'),
  ('top_n',             '6'),
  ('strategy',          'HYBRID');

INSERT INTO items (title, category, tags, description, popularity) VALUES
('Noise-Cancelling Headphones', 'Electronics', 'audio,wireless,travel',  'Over-ear headphones with 30-hour battery and active noise cancelling.', 88),
('Smartwatch Fit Series',       'Electronics', 'wearable,health,tech',   'Tracks heart rate, sleep and workouts with a week-long battery.',      82),
('Mechanical Keyboard K2',      'Electronics', 'typing,gaming,desk',     'Hot-swappable tactile switches in a compact 75% layout.',               67),
('Power Bank 20000 mAh',        'Electronics', 'charging,travel,mobile', 'Fast-charging power bank that keeps two phones going for days.',        91),
('4K Action Camera',            'Electronics', 'video,adventure,outdoor','Waterproof 4K camera with image stabilisation.',                         58),
('Smart LED Desk Lamp',         'Electronics', 'desk,lighting,study',    'Adjustable colour temperature with a built-in study timer.',            49),

('Atomic Habits',               'Books', 'habits,self-help,productivity','Small daily changes that compound into remarkable results.',            95),
('Deep Work',                   'Books', 'focus,productivity,career',    'Rules for focused success in a distracted world.',                      78),
('The Alchemist',               'Books', 'fiction,adventure,classic',    'A shepherd boy travels in search of his personal legend.',              85),
('Clean Code',                  'Books', 'programming,software,craft',   'A handbook of agile software craftsmanship.',                           72),
('Sapiens',                     'Books', 'history,science,non-fiction',  'A brief history of humankind from the Stone Age to today.',             89),
('Wings of Fire',               'Books', 'biography,science,india',      'The autobiography of Dr. A.P.J. Abdul Kalam.',                          80),

('Inception',                   'Movies', 'sci-fi,thriller,mind-bending','A thief who steals secrets through dreams takes on one last job.',      92),
('3 Idiots',                    'Movies', 'comedy,college,drama',        'Three engineering students challenge a rigid education system.',        94),
('Interstellar',                'Movies', 'sci-fi,space,drama',          'Explorers travel through a wormhole to save humanity.',                 90),
('The Dark Knight',             'Movies', 'action,crime,thriller',       'Batman faces the Joker in a battle for Gotham.',                        93),
('Dangal',                      'Movies', 'sports,drama,india',          'A former wrestler trains his daughters to become champions.',           86),
('Spirited Away',               'Movies', 'animation,fantasy,family',    'A girl wanders into a world ruled by spirits.',                         81),

('Yoga Mat Pro',                'Fitness', 'yoga,home,wellness',         'Non-slip 6 mm mat for yoga, stretching and floor workouts.',            64),
('Adjustable Dumbbells',        'Fitness', 'strength,home,gym',          'Swap from 2 to 24 kg with a single dial.',                              70),
('Resistance Band Set',         'Fitness', 'strength,travel,rehab',      'Five bands with different resistance levels and a carry pouch.',        75),
('Skipping Rope',               'Fitness', 'cardio,outdoor,budget',      'Speed rope with ball bearings for smooth cardio.',                      60),
('Foam Roller',                 'Fitness', 'recovery,massage,gym',       'Firm roller for muscle recovery after training.',                       55),
('Protein Shaker Bottle',       'Fitness', 'nutrition,gym,daily',        'Leak-proof bottle with a mixing ball.',                                 52),

('Masala Chai Sampler',         'Food', 'tea,india,gift',                'Four regional chai blends with whole spices.',                          77),
('Single-Origin Coffee Beans',  'Food', 'coffee,brew,morning',           'Medium roast beans from a single estate in Coorg.',                     83),
('Dark Chocolate Box',          'Food', 'chocolate,gift,sweet',          'Assorted 70% cocoa chocolates in a gift box.',                          74),
('Mixed Nuts Pack',             'Food', 'snack,healthy,protein',         'Roasted almonds, cashews and walnuts, lightly salted.',                 68),
('Green Tea Collection',        'Food', 'tea,wellness,herbal',           'Twenty-five bags across five flavours.',                                59),
('Millet Cookies',              'Food', 'snack,healthy,india',           'Baked ragi and jowar cookies with jaggery.',                            62);

-- Demo users (admin@recsys.com / user@recsys.com) are created automatically with salted hashes
-- the first time the web app starts (see AppContextListener).
