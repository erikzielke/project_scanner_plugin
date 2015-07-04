package dk.erikzielke.idea.project_scanner.settings;

import com.intellij.openapi.fileChooser.FileChooserDescriptor;
import com.intellij.openapi.fileChooser.FileChooserDialog;
import com.intellij.openapi.fileChooser.FileChooserFactory;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.AnActionButton;
import com.intellij.ui.AnActionButtonRunnable;
import com.intellij.ui.ToolbarDecorator;
import com.intellij.ui.components.JBList;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class ProjectScannerSettingsForm extends JPanel {
    JBList list = new JBList();
    private final DefaultListModel<Object> model;

    public ProjectScannerSettingsForm() {
        setLayout(new BorderLayout());
        model = new DefaultListModel<>();
        list.setModel(model);
        add(ToolbarDecorator.createDecorator(list)
                .setAddAction(new AnActionButtonRunnable() {
                    @Override
                    public void run(AnActionButton anActionButton) {
                        Project defaultProject = ProjectManager.getInstance().getDefaultProject();
                        FileChooserDialog fileChooser = FileChooserFactory.getInstance().createFileChooser(new FileChooserDescriptor(false, true, false, false, false, false), defaultProject, ProjectScannerSettingsForm.this);
                        VirtualFile[] files = fileChooser.choose(defaultProject);
                        if (files.length == 1) {
                            VirtualFile rootToAdd = files[0];
                            model.addElement(rootToAdd.getPath());
                        }
                    }
                })
                .setMoveUpAction(null)
                .setMoveDownAction(null)
                .createPanel(), BorderLayout.CENTER);
    }

    public boolean isModified(ProjectScannerSettings state) {
        if (model.size() != state.getRoots().size()) {
            return true;
        }
        for (int i = 0; i < state.getRoots().size(); i++) {
            String newRoot = (String) model.getElementAt(i);
            String oldRoot = state.getRoots().get(i);
            if (!oldRoot.equals(newRoot)) {
                return true;
            }
        }
        return false;
    }

    public void getData(ProjectScannerSettings state) {
        ArrayList<String> roots = new ArrayList<>();
        int size = model.getSize();
        for (int i = 0; i < size; i++) {
            roots.add((String)model.getElementAt(i));
        }
        state.setRoots(roots);
    }

    public void setData(ProjectScannerSettings data) {
        model.clear();
        for (String root : data.getRoots()) {
            model.addElement(root);
        }
    }


}
