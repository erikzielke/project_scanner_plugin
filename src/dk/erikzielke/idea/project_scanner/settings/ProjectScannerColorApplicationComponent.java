package dk.erikzielke.idea.project_scanner.settings;

import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(name = "ProjectScannerColor", storages = {@Storage(id = "ProjectScannerColor", file = "$APP_CONFIG$/ProjectScannerColor.xml")})
public class ProjectScannerColorApplicationComponent implements ApplicationComponent, PersistentStateComponent<ProjectScannerColors> {

    private ProjectScannerColors state;

    public static ProjectScannerColorApplicationComponent getInstance() {
        return ServiceManager.getService(ProjectScannerColorApplicationComponent.class);
    }

    @Nullable
    @Override
    public ProjectScannerColors getState() {
        return state;
    }

    public void setState(ProjectScannerColors state) {
        this.state = state;
    }

    @Override
    public void loadState(ProjectScannerColors state) {
        this.state = state;
    }

    @Override
    public void initComponent() {
        if (state == null) {
            state = new ProjectScannerColors();
        }
    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ProjectScannerColors";
    }
}
