# 📓 NotesVault

A full-featured desktop notes manager built with **JavaFX 21**, **PostgreSQL** (via Docker), **SHA-256** password hashing, and **Maven**.

---

## ✨ Features

| Feature | Detail |
|---|---|
| **Authentication** | Login & Register with SHA-256 hashed passwords |
| **4 Screens** | Login → Register → Dashboard → Note Editor |
| **Full CRUD** | Create, read, update, and delete notes |
| **Pin Notes** | Pin important notes to always show at the top |
| **Categories** | General, Work, Personal, Ideas, Study, To-Do |
| **Live Search** | Filter notes by title or content in real time |
| **Character Counter** | Live character count while typing |
| **Docker Database** | PostgreSQL 16 via Docker Compose — no local install needed |
| **Environment Config** | Database credentials loaded from `.env` file |
| **Dark Theme** | Custom dark UI via CSS |

---

## 🛠 Tech Stack

- **Java 21**
- **JavaFX 21** — UI framework
- **PostgreSQL 16** — database (via Docker)
- **JDBC** — database connectivity
- **SHA-256** — password hashing (built-in `java.security`)
- **Maven** — build tool
- **Docker Compose** — container management

---

## 📋 Prerequisites

Before running the app, make sure you have:

- [Java 21](https://adoptium.net/) (Eclipse Temurin recommended)
- [Maven 3.9+](https://maven.apache.org/)
- [Docker Desktop](https://www.docker.com/products/docker-desktop)
- [IntelliJ IDEA](https://www.jetbrains.com/idea/) (recommended)

---

## 🚀 Quick Start

### 1. Clone the repository

```bash
git clone <your-repo-url>
cd notes-saver
```

### 2. Create the `.env` file

Copy the example file and fill in your credentials:

```powershell
cp .env.example .env
```

Your `.env` should look like this:

```env
DB_URL=jdbc:postgresql://localhost:5432/notes-saver
DB_USER=notes-saver_user
DB_PASS=notes-saver_pass
```

### 3. Start the database

Open Docker Desktop and wait until it says **Engine running**, then:

```powershell
cd docker
docker-compose up -d
```

Verify the container is running:

```powershell
docker ps
```

You should see `notesvault_db` with status `Up`.

### 4. Run the app

**Option A — IntelliJ:**
Click the green ▶ **Run** button on `MainApp.java`.

**Option B — Maven (terminal):**
```powershell
mvn clean javafx:run
```

---

## 🗂 Project Structure

```
notes-saver/
├── .env                                        # DB credentials (not committed)
├── .env.example                                # Credentials template
├── pom.xml                                     # Maven build config
│
├── docker/
│   ├── docker-compose.yml                      # PostgreSQL 16 service
│   └── init.sql                                # Auto-applied DB schema
│
└── src/main/
    ├── java/fred/was/here/notessaver/
    │   ├── MainApp.java                        # Entry point
    │   ├── module-info.java
    │   ├── controller/
    │   │   ├── LoginController.java
    │   │   ├── RegisterController.java
    │   │   ├── DashboardController.java
    │   │   └── NoteEditorController.java
    │   ├── dao/
    │   │   ├── UserDAO.java                    # Register & login
    │   │   └── NoteDAO.java                    # CRUD, search, pin
    │   ├── model/
    │   │   ├── User.java
    │   │   └── Note.java
    │   └── util/
    │       ├── DatabaseUtil.java               # JDBC + .env loader
    │       ├── PasswordUtil.java               # SHA-256 hashing
    │       └── SceneManager.java               # Scene switching
    │
    └── resources/fred/was/here/notessaver/
        ├── login.fxml
        ├── register.fxml
        ├── dashboard.fxml
        ├── note_editor.fxml
        └── styles.css
```

---

## 🗄 Database Schema

```sql
-- Users table
CREATE TABLE users (
    id         SERIAL PRIMARY KEY,
    username   VARCHAR(50)  NOT NULL UNIQUE,
    email      VARCHAR(100) NOT NULL UNIQUE,
    password   VARCHAR(255) NOT NULL,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW()
);

-- Notes table
CREATE TABLE notes (
    id         SERIAL PRIMARY KEY,
    user_id    INTEGER      NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    title      VARCHAR(255) NOT NULL,
    content    TEXT         NOT NULL DEFAULT '',
    category   VARCHAR(50)  NOT NULL DEFAULT 'General',
    pinned     BOOLEAN      NOT NULL DEFAULT FALSE,
    created_at TIMESTAMP    NOT NULL DEFAULT NOW(),
    updated_at TIMESTAMP    NOT NULL DEFAULT NOW()
);
```

---

## ⚙️ Configuration

All database settings are read from the `.env` file at startup. Edit it to change credentials or port:

```env
DB_URL=jdbc:postgresql://localhost:5432/notes-saver
DB_USER=notes-saver_user
DB_PASS=notes-saver_pass
```

These must match the values in `docker/docker-compose.yml`:

```yaml
environment:
  POSTGRES_DB: notes-saver
  POSTGRES_USER: notes-saver_user
  POSTGRES_PASSWORD: notes-saver_pass
```

---

## 🔑 Security

- Passwords are **never stored in plaintext** — SHA-256 hashing is applied before saving.
- All database queries use **PreparedStatement** — no SQL injection possible.
- Each user can only access **their own notes** — all queries filter by `user_id`.
- The `.env` file keeps credentials **out of source code**.

---

## 🗃 Viewing the Database

**Option A — Docker Desktop:**
1. Open Docker Desktop → Containers → `notesvault_db`
2. Click the **Exec** tab
3. Run: `psql -U notes-saver_user -d notes-saver`
4. Then query as needed

**Option B — Terminal:**
```powershell
docker exec -it notesvault_db psql -U notes-saver_user -d notes-saver
```

Useful commands inside psql:
```sql
\dt                  -- list all tables
SELECT * FROM users; -- view all users
SELECT * FROM notes; -- view all notes
\q                   -- exit
```

---

## 🛑 Stopping the Database

```powershell
cd docker
docker-compose down        # stop, keep data
docker-compose down -v     # stop and delete all data
```

---

## 📝 Notes

> The database schema is applied automatically via `docker/init.sql` on the **first** container start. If tables are missing, reset the volume with `docker-compose down -v` then `docker-compose up -d`.
