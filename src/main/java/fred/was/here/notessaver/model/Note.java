package fred.was.here.notessaver.model;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Note {
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("MMM d, yyyy HH:mm");

    private int id;
    private int userId;
    private String title;
    private String content;
    private String category;
    private boolean pinned;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    public Note() {}

    public Note(int userId, String title, String content, String category) {
        this.userId   = userId;
        this.title    = title;
        this.content  = content;
        this.category = category;
    }

    // Getters & setters
    public int getId()                        { return id; }
    public void setId(int id)                 { this.id = id; }

    public int getUserId()                    { return userId; }
    public void setUserId(int uid)            { this.userId = uid; }

    public String getTitle()                  { return title; }
    public void setTitle(String t)            { this.title = t; }

    public String getContent()                { return content; }
    public void setContent(String c)          { this.content = c; }

    public String getCategory()               { return category; }
    public void setCategory(String cat)       { this.category = cat; }

    public boolean isPinned()                 { return pinned; }
    public void setPinned(boolean p)          { this.pinned = p; }

    public LocalDateTime getCreatedAt()       { return createdAt; }
    public void setCreatedAt(LocalDateTime d) { this.createdAt = d; }

    public LocalDateTime getUpdatedAt()       { return updatedAt; }
    public void setUpdatedAt(LocalDateTime d) { this.updatedAt = d; }

    /** Formatted timestamp shown in the UI. */
    public String getFormattedUpdatedAt() {
        return updatedAt != null ? updatedAt.format(FMT) : "";
    }

    /** Short preview of the content (first 80 chars). */
    public String getPreview() {
        if (content == null || content.isBlank()) return "(empty)";
        String stripped = content.strip().replace("\n", " ");
        return stripped.length() > 80 ? stripped.substring(0, 80) + "…" : stripped;
    }

    @Override
    public String toString() { return title; }
}