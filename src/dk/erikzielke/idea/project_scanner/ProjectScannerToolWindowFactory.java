package dk.erikzielke.idea.project_scanner;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.openapi.wm.ToolWindowFactory;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentManager;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;

public class ProjectScannerToolWindowFactory implements ToolWindowFactory {

    @Override
    public void createToolWindowContent(@NotNull Project project, @NotNull ToolWindow toolWindow) {
        ContentManager contentManager = toolWindow.getContentManager();
        ProjectScannerToolWindow projectScannerToolWindow = new ProjectScannerToolWindow(true);
        ScannerProjectManagerListener scannerProjectManagerListener = new ScannerProjectManagerListener(projectScannerToolWindow);
        ProjectManager.getInstance().addProjectManagerListener(scannerProjectManagerListener, project);
        Content content = contentManager.getFactory().createContent(projectScannerToolWindow, null, false);
        contentManager.addContent(content);
    }
}
