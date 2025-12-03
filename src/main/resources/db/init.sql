CREATE TABLE IF NOT EXISTS media (
    id SERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    media_type VARCHAR(50),
    release_year INTEGER,
    genres TEXT[],
    age_restriction INTEGER,
    rating DOUBLE PRECISION
);
