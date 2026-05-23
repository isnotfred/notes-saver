package fred.was.here.notessaver.dao;

import fred.was.here.notessaver.model.User;
import fred.was.here.notessaver.util.DatabaseUtil;
import fred.was.here.notessaver.util.PasswordUtil;

import java.sql.*;
import java.util.Optional;

/**
 * Data-access object for the users table.
 */
public class UserDAO {

    /**
     * Register a new user. Hashes the password before storing.
     *
     * @return the newly created User, or empty if the username/email is taken
     */
    public Optional<User> register(String username, String email, String plainPassword) {
        String sql = "INSERT INTO users (username, email, password) VALUES (?, ?, ?) RETURNING id, created_at";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            ps.setString(2, email);
            ps.setString(3, PasswordUtil.hash(plainPassword));

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    User user = new User(
                            rs.getInt("id"),
                            username,
                            email,
                            rs.getTimestamp("created_at").toLocalDateTime()
                    );
                    return Optional.of(user);
                }
            }
        } catch (SQLException e) {
            // Unique violation (23505) → username or email already exists
            if ("23505".equals(e.getSQLState())) return Optional.empty();
            throw new RuntimeException("register failed", e);
        }
        return Optional.empty();
    }

    /**
     * Authenticate a user by username + plain-text password.
     *
     * @return the User if credentials match, otherwise empty
     */
    public Optional<User> login(String username, String plainPassword) {
        String sql = "SELECT id, username, email, password, created_at FROM users WHERE username = ?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, username);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String storedHash = rs.getString("password");
                    if (PasswordUtil.verify(plainPassword, storedHash)) {
                        User user = new User(
                                rs.getInt("id"),
                                rs.getString("username"),
                                rs.getString("email"),
                                rs.getTimestamp("created_at").toLocalDateTime()
                        );
                        return Optional.of(user);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("login failed", e);
        }
        return Optional.empty();
    }
}
