package dk.erikzielke.idea.project_scanner;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonShortcuts;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.util.InvalidDataException;
import com.intellij.openapi.wm.impl.ProjectWindowAction;
import com.intellij.openapi.wm.impl.WindowDressing;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import org.jdom.JDOMException;

import javax.swing.*;
import java.io.IOException;

public class OpenProjectInNewWindow extends AnAction {
    public OpenProjectInNewWindow(JComponent list) {
        super("Open In New Window", "Opens the selected project in a new window", AllIcons.Icons.Ide.NextStep);
        registerCustomShortcutSet(CommonShortcuts.ALT_ENTER, list);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        ScannedProject scannedProject = e.getData(ProjectScannerToolWindow.SELECTED_PROJECT);
        if (scannedProject != null) {
            for (Project openProject : openProjects) {
                if (scannedProject.getLocation() != null && scannedProject.getLocation().equals(openProject.getBasePath())) {
                    AnAction[] children = WindowDressing.getWindowActionGroup().getChildren(null);
                    for (AnAction child : children) {
                        if (child instanceof ProjectWindowAction) {
                            ProjectWindowAction projectWindowAction = (ProjectWindowAction) child;
                            if(projectWindowAction.getProjectLocation().equals(openProject.getBasePath())) {
                                projectWindowAction.actionPerformed(e);
                                return;
                            };
                        }
                    }

                }
            }

            try {
                ProjectManager.getInstance().loadAndOpenProject(scannedProject.getLocation());
            } catch (IOException e1) {
                e1.printStackTrace();
            } catch (JDOMException e1) {
                e1.printStackTrace();
            } catch (InvalidDataException e1) {
                e1.printStackTrace();
            }
        }
    }
}
