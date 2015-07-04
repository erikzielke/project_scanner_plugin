package dk.erikzielke.idea.project_scanner.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.*;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;


@State(name = "ProjectScannerResult", storages = {@Storage(id = "ProjectScannerResult", file = "$APP_CONFIG$/ProjectScannerResult.xml")})
public class ProjectScannerStateApplicationComponent  implements ApplicationComponent, PersistentStateComponent<ProjectScannerResult> {

    private ProjectScannerResult state;

    public static ProjectScannerStateApplicationComponent getInstance() {
        return ServiceManager.getService(ProjectScannerStateApplicationComponent.class);
    }

    @Nullable
    @Override
    public ProjectScannerResult getState() {
        return state;
    }

    public void setState(ProjectScannerResult state) {
        this.state = state;

    }

    @Override
    public void loadState(ProjectScannerResult state) {
        this.state = state;
    }

    @Override
    public void initComponent() {

    }

    @Override
    public void disposeComponent() {

    }

    @NotNull
    @Override
    public String getComponentName() {
        return "ProjectScannerResult";
    }
}
