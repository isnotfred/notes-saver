package fred.was.here.notessaver.dao;

import fred.was.here.notessaver.model.Note;
import fred.was.here.notessaver.util.DatabaseUtil;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Data-access object for the notes table.
 */
public class NoteDAO {

    private static final String SELECT_COLS =
            "id, user_id, title, content, category, pinned, created_at, updated_at";

    /** Create a new note and return it with its generated id & timestamps. */
    public Note create(Note note) {
        String sql = """
            INSERT INTO notes (user_id, title, content, category, pinned)
            VALUES (?, ?, ?, ?, ?)
            RETURNING %s
            """.formatted(SELECT_COLS);

        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, note.getUserId());
            ps.setString(2, note.getTitle());
            ps.setString(3, note.getContent());
            ps.setString(4, note.getCategory());
            ps.setBoolean(5, note.isPinned());

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            throw new RuntimeException("create note failed", e);
        }
        throw new RuntimeException("create note: no row returned");
    }

    /** Return all notes belonging to a user, pinned first, then newest first. */
    public List<Note> findByUser(int userId) {
        String sql = """
            SELECT %s FROM notes
            WHERE user_id = ?
            ORDER BY pinned DESC, updated_at DESC
            """.formatted(SELECT_COLS);

        List<Note> notes = new ArrayList<>();
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findByUser failed", e);
        }
        return notes;
    }

    /** Search notes by keyword (title or content, case-insensitive). */
    public List<Note> search(int userId, String keyword) {
        String sql = """
            SELECT %s FROM notes
            WHERE user_id = ?
              AND (LOWER(title) LIKE LOWER(?) OR LOWER(content) LIKE LOWER(?))
            ORDER BY pinned DESC, updated_at DESC
            """.formatted(SELECT_COLS);

        String pattern = "%%" + keyword + "%%";
        List<Note> notes = new ArrayList<>();
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, userId);
            ps.setString(2, pattern);
            ps.setString(3, pattern);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) notes.add(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("search failed", e);
        }
        return notes;
    }

    /** Fetch a single note by id. */
    public Optional<Note> findById(int id) {
        String sql = "SELECT %s FROM notes WHERE id = ?".formatted(SELECT_COLS);
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(map(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("findById failed", e);
        }
        return Optional.empty();
    }

    /** Update title, content, category and pinned flag. */
    public void update(Note note) {
        String sql = """
            UPDATE notes
            SET title = ?, content = ?, category = ?, pinned = ?
            WHERE id = ? AND user_id = ?
            """;
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setString(1, note.getTitle());
            ps.setString(2, note.getContent());
            ps.setString(3, note.getCategory());
            ps.setBoolean(4, note.isPinned());
            ps.setInt(5, note.getId());
            ps.setInt(6, note.getUserId());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("update note failed", e);
        }
    }

    /** Delete a note by id (only if it belongs to the given user). */
    public void delete(int noteId, int userId) {
        String sql = "DELETE FROM notes WHERE id = ? AND user_id = ?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setInt(1, noteId);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("delete note failed", e);
        }
    }

    /** Toggle the pinned flag. */
    public void togglePin(int noteId, int userId, boolean pinned) {
        String sql = "UPDATE notes SET pinned = ? WHERE id = ? AND user_id = ?";
        try (Connection c = DatabaseUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {

            ps.setBoolean(1, pinned);
            ps.setInt(2, noteId);
            ps.setInt(3, userId);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("togglePin failed", e);
        }
    }

    // ── helpers ────────────────────────────────────────────────────────────

    private Note map(ResultSet rs) throws SQLException {
        Note n = new Note();
        n.setId(rs.getInt("id"));
        n.setUserId(rs.getInt("user_id"));
        n.setTitle(rs.getString("title"));
        n.setContent(rs.getString("content"));
        n.setCategory(rs.getString("category"));
        n.setPinned(rs.getBoolean("pinned"));
        n.setCreatedAt(rs.getTimestamp("created_at").toLocalDateTime());
        n.setUpdatedAt(rs.getTimestamp("updated_at").toLocalDateTime());
        return n;
    }
}