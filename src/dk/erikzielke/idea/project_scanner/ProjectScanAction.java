package dk.erikzielke.idea.project_scanner;

import com.intellij.icons.AllIcons;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.vcs.changes.RunnableBackgroundableWrapper;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerApplicationComponent;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerSettings;

import javax.swing.*;
import java.util.Collections;
import java.util.List;

public class ProjectScanAction extends AnAction {

    public ProjectScanAction() {
        super("Scan", "Scan for projects", AllIcons.ToolbarDecorator.AddFolder);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {


        ProgressManager.getInstance().run(new RunnableBackgroundableWrapper(e.getProject(), "Scanning for projects...", new Runnable() {
            @Override
            public void run() {

                ProjectScannerApplicationComponent instance = ProjectScannerApplicationComponent.getInstance();
                ProjectScannerSettings state = instance.getState();
                List<String> roots = Collections.emptyList();
                if (state != null) {
                    roots = state.getRoots();
                }

                ScanJob scanJob = new ScanJob();
                List<ScannedProject> scannedProjects = scanJob.execute(roots.toArray(new String[roots.size()]));
                ApplicationManager.getApplication().invokeLater(new Runnable() {
                    @Override
                    public void run() {
                        DefaultListModel<ScannedProject> data = ProjectScannerToolWindow.SCANNED_PROJECTS.getData(e.getDataContext());
                        if (data != null) {
                            data.clear();
                            for (ScannedProject scannedProject : scannedProjects) {
                                data.addElement(scannedProject);
                            }
                        }
                    }
                });

            }
        }));
    }

}
