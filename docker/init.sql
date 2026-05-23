-- NotesvVault Database Schema

CREATE TABLE IF NOT EXISTS users (
    id          SERIAL PRIMARY KEY,
    username    VARCHAR(50)  NOT NULL UNIQUE,
    email       VARCHAR(100) NOT NULL UNIQUE,
    password    VARCHAR(255) NOT NULL,      -- BCrypt hash
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW()
    );

CREATE TABLE IF NOT EXISTS notes (
    id          SERIAL PRIMARY KEY,
    user_id     INTEGER      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title       VARCHAR(255) NOT NULL,
    content     TEXT         NOT NULL DEFAULT '',
    category    VARCHAR(50)  NOT NULL DEFAULT 'General',
    pinned      BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at  TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at  TIMESTAMP    NOT NULL DEFAULT NOW()
    );

-- Auto-update updated_at on notes changes
CREATE OR REPLACE FUNCTION update_updated_at()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = NOW();
RETURN NEW;
END;
$$ LANGUAGE plpgsql;

CREATE TRIGGER notes_updated_at
    BEFORE UPDATE ON notes
    FOR EACH ROW EXECUTE FUNCTION update_updated_at();

-- Indexes
CREATE INDEX IF NOT EXISTS idx_notes_user_id ON notes(user_id);
CREATE INDEX IF NOT EXISTS idx_notes_category ON notes(category);