package dk.erikzielke.idea.project_scanner.settings.tags;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.DialogBuilder;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerSettings;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ProjectScannerTagsForm extends JPanel {
    private final DefaultListModel<String> model;
    private final JBList list;

    public ProjectScannerTagsForm() {
        setLayout(new BorderLayout());
        model = new DefaultListModel<>();
        list = new JBList();
        list.setModel(model);
        add(ToolbarDecorator.createDecorator(list)
                .setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        String tag = Messages.showInputDialog(ProjectScannerTagsForm.this, "Enter tag to add", "Add Tag", null);
                        model.addElement(tag);
                    }
                })
                .setMoveUpAction(null)
                .setMoveDownAction(null)
                .createPanel(), BorderLayout.CENTER);
    }

    public boolean isModified(ProjectScannerTags state) {
        if (model.size() != state.getTags().size()) {
            return true;
        }
        for (int i = 0; i < state.getTags().size(); i++) {
            String newRoot = model.getElementAt(i);
            String oldRoot = state.getTags().get(i);
            if (!oldRoot.equals(newRoot)) {
                return true;
            }
        }
        return false;
    }

    public void getData(ProjectScannerTags state) {
        ArrayList<String> roots = new ArrayList<>();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            roots.add(model.getElementAt(i));
        }
        state.setTags(roots);
    }

    public void setData(ProjectScannerTags data) {
        model.clear();
        for (String root : data.getTags()) {
            model.addElement(root);
        }
    }
}
