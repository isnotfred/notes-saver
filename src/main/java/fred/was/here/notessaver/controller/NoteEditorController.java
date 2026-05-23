package fred.was.here.notessaver.controller;

import fred.was.here.notessaver.dao.NoteDAO;
import fred.was.here.notessaver.model.Note;
import fred.was.here.notessaver.model.User;
import fred.was.here.notessaver.util.SceneManager;
import javafx.fxml.FXML;
import javafx.scene.control.*;

/**
 * Handles both NEW note creation and EDIT of an existing note.
 * The calling screen passes a Runnable that is invoked on save or cancel
 * to navigate back.
 */
public class NoteEditorController {

    @FXML private TextField     titleField;
    @FXML private TextArea      contentArea;
    @FXML private ComboBox<String> categoryCombo;
    @FXML private CheckBox      pinnedCheck;
    @FXML private Button        saveBtn;
    @FXML private Label         charCountLabel;
    @FXML private Label         errorLabel;

    private final NoteDAO noteDAO = new NoteDAO();
    private User currentUser;
    private Note editingNote;     // null when creating new
    private Runnable onDone;

    private static final String[] CATEGORIES =
            {"General", "Work", "Personal", "Ideas", "Study", "To-Do"};

    @FXML
    private void initialize() {
        categoryCombo.getItems().addAll(CATEGORIES);
        categoryCombo.setValue("General");

        // Live character counter
        contentArea.textProperty().addListener((obs, old, val) ->
                charCountLabel.setText(val.length() + " chars")
        );
    }

    /** Call when creating a brand-new note. */
    public void initNew(User user, Runnable onDone) {
        this.currentUser = user;
        this.onDone = onDone;
        saveBtn.setText("Save Note");
    }

    /** Call when editing an existing note. */
    public void initEdit(User user, Note note, Runnable onDone) {
        this.currentUser = user;
        this.editingNote = note;
        this.onDone = onDone;

        titleField.setText(note.getTitle());
        contentArea.setText(note.getContent());
        categoryCombo.setValue(note.getCategory());
        pinnedCheck.setSelected(note.isPinned());
        saveBtn.setText("Update Note");
    }

    @FXML
    private void handleSave() {
        String title   = titleField.getText().strip();
        String content = contentArea.getText();
        String cat     = categoryCombo.getValue();
        boolean pinned = pinnedCheck.isSelected();

        errorLabel.setText("");
        if (title.isEmpty()) {
            errorLabel.setText("Title cannot be empty.");
            return;
        }

        if (editingNote == null) {
            // CREATE
            Note note = new Note(currentUser.getId(), title, content, cat);
            note.setPinned(pinned);
            noteDAO.create(note);
        } else {
            // UPDATE
            editingNote.setTitle(title);
            editingNote.setContent(content);
            editingNote.setCategory(cat);
            editingNote.setPinned(pinned);
            noteDAO.update(editingNote);
        }

        onDone.run();
    }

    @FXML
    private void handleCancel() {
        onDone.run();
    }
}