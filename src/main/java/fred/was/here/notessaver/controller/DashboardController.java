package fred.was.here.notessaver.controller;

import fred.was.here.notessaver.dao.NoteDAO;
import fred.was.here.notessaver.model.Note;
import fred.was.here.notessaver.model.User;
import fred.was.here.notessaver.util.SceneManager;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.util.Callback;

import java.util.List;
import java.util.Optional;

public class DashboardController {

    @FXML private Label         greetingLabel;
    @FXML private TextField     searchField;
    @FXML private ListView<Note> noteListView;
    @FXML private Button        newNoteBtn;
    @FXML private Button        logoutBtn;
    @FXML private Label         statusLabel;

    private final NoteDAO noteDAO = new NoteDAO();
    private User currentUser;
    private final ObservableList<Note> notes = FXCollections.observableArrayList();

    /** Called by SceneManager after switching to this scene. */
    public void initUser(User user) {
        this.currentUser = user;
        greetingLabel.setText("Hello, " + user.getUsername() + " 👋");
        loadNotes();
    }

    @FXML
    private void initialize() {
        noteListView.setItems(notes);
        noteListView.setCellFactory(buildCellFactory());

        // Open note on double-click
        noteListView.setOnMouseClicked(e -> {
            if (e.getClickCount() == 2) openSelectedNote();
        });

        // Live search
        searchField.textProperty().addListener((obs, old, val) -> {
            if (val == null || val.isBlank()) loadNotes();
            else searchNotes(val.strip());
        });
    }

    private void loadNotes() {
        List<Note> list = noteDAO.findByUser(currentUser.getId());
        notes.setAll(list);
        updateStatus();
    }

    private void searchNotes(String keyword) {
        List<Note> list = noteDAO.search(currentUser.getId(), keyword);
        notes.setAll(list);
        updateStatus();
    }

    private void updateStatus() {
        int count = notes.size();
        statusLabel.setText(count + " note" + (count == 1 ? "" : "s"));
    }

    @FXML
    private void handleNewNote() {
        NoteEditorController editor =
                SceneManager.switchScene("note_editor.fxml", "New Note");
        editor.initNew(currentUser, this::loadNotesAfterReturn);
    }

    private void openSelectedNote() {
        Note selected = noteListView.getSelectionModel().getSelectedItem();
        if (selected == null) return;
        NoteEditorController editor =
                SceneManager.switchScene("note_editor.fxml", "Edit Note");
        editor.initEdit(currentUser, selected, this::loadNotesAfterReturn);
    }

    private void loadNotesAfterReturn() {
        // Re-enter dashboard (called by editor on save/cancel)
        DashboardController dashboard =
                SceneManager.switchScene("dashboard.fxml", "Dashboard");
        dashboard.initUser(currentUser);
    }

    @FXML
    private void handleLogout() {
        SceneManager.switchScene("login.fxml", "Login");
    }

    // ── Custom cell factory with pin & delete buttons ───────────────────────

    private Callback<ListView<Note>, ListCell<Note>> buildCellFactory() {
        return lv -> new ListCell<>() {
            private final Label titleLabel   = new Label();
            private final Label previewLabel = new Label();
            private final Label metaLabel    = new Label();
            private final Button pinBtn      = new Button();
            private final Button deleteBtn   = new Button("🗑");
            private final VBox text          = new VBox(2, titleLabel, previewLabel, metaLabel);
            private final HBox root          = new HBox(10, text, pinBtn, deleteBtn);

            {
                HBox.setHgrow(text, Priority.ALWAYS);
                titleLabel.getStyleClass().add("note-title");
                previewLabel.getStyleClass().add("note-preview");
                metaLabel.getStyleClass().add("note-meta");
                pinBtn.getStyleClass().add("icon-btn");
                deleteBtn.getStyleClass().add("icon-btn");

                pinBtn.setOnAction(e -> {
                    Note n = getItem();
                    if (n != null) {
                        noteDAO.togglePin(n.getId(), currentUser.getId(), !n.isPinned());
                        loadNotes();
                    }
                });

                deleteBtn.setOnAction(e -> {
                    Note n = getItem();
                    if (n == null) return;
                    Alert confirm = new Alert(Alert.AlertType.CONFIRMATION,
                            "Delete \"" + n.getTitle() + "\"?", ButtonType.YES, ButtonType.CANCEL);
                    confirm.setHeaderText(null);
                    Optional<ButtonType> result = confirm.showAndWait();
                    if (result.isPresent() && result.get() == ButtonType.YES) {
                        noteDAO.delete(n.getId(), currentUser.getId());
                        loadNotes();
                    }
                });
            }

            @Override
            protected void updateItem(Note note, boolean empty) {
                super.updateItem(note, empty);
                if (empty || note == null) {
                    setGraphic(null);
                } else {
                    titleLabel.setText((note.isPinned() ? "📌 " : "") + note.getTitle());
                    previewLabel.setText(note.getPreview());
                    metaLabel.setText(note.getCategory() + "  •  " + note.getFormattedUpdatedAt());
                    pinBtn.setText(note.isPinned() ? "Unpin" : "📌");
                    root.getStyleClass().removeAll("pinned-note");
                    if (note.isPinned()) root.getStyleClass().add("pinned-note");
                    setGraphic(root);
                }
            }
        };
    }
}