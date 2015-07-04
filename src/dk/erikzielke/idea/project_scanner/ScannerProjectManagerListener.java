package dk.erikzielke.idea.project_scanner;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManagerListener;

class ScannerProjectManagerListener implements ProjectManagerListener {
    private ProjectScannerToolWindow projectScannerToolWindow;

    public ScannerProjectManagerListener(ProjectScannerToolWindow projectScannerToolWindow) {

        this.projectScannerToolWindow = projectScannerToolWindow;
    }

    @Override
    public void projectOpened(Project project) {
        projectScannerToolWindow.refresh();
    }

    @Override
    public boolean canCloseProject(Project project) {
        return true;
    }

    @Override
    public void projectClosed(Project project) {
        projectScannerToolWindow.refresh();
    }

    @Override
    public void projectClosing(Project project) {

    }
}
