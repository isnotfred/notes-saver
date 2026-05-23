package fred.was.here.notessaver.model;

import java.time.LocalDateTime;

public class User {
    private int id;
    private String username;
    private String email;
    private String passwordHash;
    private LocalDateTime createdAt;

    public User() {}

    public User(int id, String username, String email, LocalDateTime createdAt) {
        this.id = id;
        this.username = username;
        this.email = email;
        this.createdAt = createdAt;
    }

    // Getters & setters
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public String getUsername()               { return username; }
    public void setUsername(String username)  { this.username = username; }

    public String getEmail()                  { return email; }
    public void setEmail(String email)        { this.email = email; }

    public String getPasswordHash()           { return passwordHash; }
    public void setPasswordHash(String h)     { this.passwordHash = h; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime d) { this.createdAt = d; }

    @Override
    public String toString() { return username; }
}