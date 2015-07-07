package dk.erikzielke.idea.project_scanner.settings.tags;

import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.ProjectComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import com.intellij.openapi.project.Project;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerApplicationComponent;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerResult;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerStateApplicationComponent;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.ArrayList;


@State(name = "ProjectScannerTags", storages = {@Storage(id = "ProjectScannerTags", file = "$PROJECT_CONFIG_DIR$/ProjectScannerTags.xml")})

public class ProjectScannerProjectComponent implements ProjectComponent, Configurable, PersistentStateComponent<ProjectScannerTags> {
    private ProjectScannerTags tags;
    private ProjectScannerTagsForm projectScannerTagsForm;
    private Project project;

    public ProjectScannerProjectComponent(Project project) {
        this.project = project;
    }

    public void initComponent() {
        if (tags == null) {
            tags = new ProjectScannerTags();
        }
    }

    public void disposeComponent() {
    }

    @NotNull
    public String getComponentName() {
        return "ProjectScannerProjectComponent";
    }

    public void projectOpened() {
        // called when project is opened
    }

    public void projectClosed() {
        // called when project is being closed
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Project Scanner Tags";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }

    @Nullable
    @Override
    public ProjectScannerTags getState() {
        return tags;
    }

    @Override
    public void loadState(ProjectScannerTags state) {
        this.tags = state;
    }

    @Nullable
    @Override
    public JComponent createComponent() {
        if (projectScannerTagsForm == null) {

            projectScannerTagsForm = new ProjectScannerTagsForm();
        }
        return projectScannerTagsForm;
    }

    @Override
    public boolean isModified() {
        return projectScannerTagsForm.isModified(tags);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (projectScannerTagsForm != null) {
            projectScannerTagsForm.getData(tags);
            ProjectScannerResult projectScannerResult = ProjectScannerStateApplicationComponent.getInstance().getState();
            if (projectScannerResult != null) {
                ArrayList<ScannedProject> scannedProjects = projectScannerResult.getScannedProjects();
                ScannedProject currentProject = null;
                for (ScannedProject scannedProject : scannedProjects) {
                    if (scannedProject.getLocation().equals(project.getBasePath())) {
                        currentProject = scannedProject;
                        break;
                    }
                }
                if (currentProject != null) {
                    currentProject.setTags(tags.getTags());
                }
            }
        }
    }

    @Override
    public void reset() {
        if (projectScannerTagsForm != null) {
            projectScannerTagsForm.setData(tags);
        }
    }
    @Override
    public void disposeUIResources() {
        projectScannerTagsForm = null;
    }
}
