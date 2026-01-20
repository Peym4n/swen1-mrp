
-- USERS TABLE
-- Handles registration, login credentials, and profile data.
CREATE TABLE IF NOT EXISTS users (
    id SERIAL PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE, -- 
    password VARCHAR(255) NOT NULL,       -- 
    email VARCHAR(100),                   -- From OpenAPI UserProfileUpdate
    favorite_genre VARCHAR(50),           -- From OpenAPI UserProfileUpdate
    token VARCHAR(255),                   -- Auth token
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- MEDIA TABLE
-- Stores the core content (Movies, Series, Games).
CREATE TABLE IF NOT EXISTS media (
    id SERIAL PRIMARY KEY,
    title VARCHAR(150) NOT NULL,          -- 
    description TEXT,                     -- 
    media_type VARCHAR(20) NOT NULL,      -- (movie, series, game)
    release_year INT NOT NULL,            -- 
    age_restriction INT NOT NULL,         -- 
    creator_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    average_rating DECIMAL(3, 2) DEFAULT 0.0, -- (Calculated field)
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- Media Genres Table
CREATE TABLE IF NOT EXISTS media_genres (
    media_id INT REFERENCES media(id) ON DELETE CASCADE,
    genre VARCHAR(50) NOT NULL,
    PRIMARY KEY (media_id, genre)
);

-- Ratings Table
-- Links Users and Media with a score.
CREATE TABLE IF NOT EXISTS ratings (
    id SERIAL PRIMARY KEY,
    user_id INT NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    media_id INT NOT NULL REFERENCES media(id) ON DELETE CASCADE,
    stars INT NOT NULL CHECK (stars BETWEEN 1 AND 5),
    comment TEXT,
    is_confirmed BOOLEAN DEFAULT FALSE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    UNIQUE (user_id, media_id)
);

-- Rating Likes Table
-- Tracks which users liked which ratings.
CREATE TABLE IF NOT EXISTS rating_likes (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    rating_id INT REFERENCES ratings(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, rating_id) -- Enforces 1 like per user per rating
);

-- Favorites Table
-- Tracks user favorite lists.
CREATE TABLE IF NOT EXISTS favorites (
    user_id INT REFERENCES users(id) ON DELETE CASCADE,
    media_id INT REFERENCES media(id) ON DELETE CASCADE,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    PRIMARY KEY (user_id, media_id)
);