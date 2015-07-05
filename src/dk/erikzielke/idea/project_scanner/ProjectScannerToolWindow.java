package dk.erikzielke.idea.project_scanner;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.render.MyColoredListCellRenderer;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerResult;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerStateApplicationComponent;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;

public class ProjectScannerToolWindow extends SimpleToolWindowPanel {

    public static DataKey<DefaultListModel<ScannedProject>> SCANNED_PROJECTS = DataKey.create("scannedProject");
    public static DataKey<ScannedProject> SELECTED_PROJECT = DataKey.create("selectedProject");
    private final DefaultListModel<ScannedProject> model;
    JList list;


    public ProjectScannerToolWindow(boolean vertical) {
        super(vertical, true);
        list = new JBList();
        ListSpeedSearch listSpeedSearch = new ListSpeedSearch(list);
        setToolbar(createToolbar());

        model = new DefaultListModel<>();
        list.setModel(model);

        list.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(Component comp, int x, int y) {
                popupInvoked(comp, x, y);
            }
        });
        ProjectScannerStateApplicationComponent instance = ProjectScannerStateApplicationComponent.getInstance();
        ProjectScannerResult state = instance.getState();
        if (state != null) {
            java.util.List<ScannedProject> scannedProjects = state.getScannedProjects();
            for (ScannedProject scannedProject : scannedProjects) {
                model.addElement(scannedProject);
            }
        }
        SimpleTextAttributes simpleTextAttributes = new SimpleTextAttributes(Font.BOLD, JBColor.BLACK);
        list.setCellRenderer(new MyColoredListCellRenderer(simpleTextAttributes));

        setContent(ScrollPaneFactory.createScrollPane(list));
    }

    private void popupInvoked(final Component comp, final int x, final int y) {
        final ScannedProject project = (ScannedProject) list.getSelectedValue();
        if (project != null) {
            final DefaultActionGroup group = new DefaultActionGroup();
            group.add(new OpenProjectInNewWindow(list));
            group.add(new RevealFileAction());
            group.add(new CopyPathAction());
            final ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("projectScannerPopup", group);
            popupMenu.getComponent().show(comp, x, y);
        }
    }


    @Nullable
    @Override
    public Object getData(String dataId) {
        if (SCANNED_PROJECTS.is(dataId)) {
            return model;
        } else if (SELECTED_PROJECT.is(dataId)) {
            return list.getSelectedValue();
        } else if (CommonDataKeys.VIRTUAL_FILE.is(dataId)) {
            ScannedProject selectedValue = (ScannedProject) list.getSelectedValue();
            if (selectedValue != null) {
                VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(selectedValue.getLocation()));
                return fileByIoFile;
            } else {
                return null;
            }
        }
        return super.getData(dataId);
    }

    private JComponent createToolbar() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new ProjectScanAction());
        OpenProjectInNewWindow action = new OpenProjectInNewWindow(list);
        group.add(action);
        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("projectScannerToolbar", group, true);
        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        return buttonsPanel;
    }

    public void refresh() {
        list.invalidate();
    }

}
