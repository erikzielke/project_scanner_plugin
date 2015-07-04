package dk.erikzielke.idea.project_scanner.settings;

import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.ApplicationComponent;
import com.intellij.openapi.components.PersistentStateComponent;
import com.intellij.openapi.components.State;
import com.intellij.openapi.components.Storage;
import com.intellij.openapi.options.Configurable;
import com.intellij.openapi.options.ConfigurationException;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

@State(name = "ProjectScannerSettings", storages = {@Storage(id = "ProjectScannerSettings", file = "$APP_CONFIG$/ProjectScannerSettings.xml")})
public class ProjectScannerApplicationComponent implements Configurable, ApplicationComponent, PersistentStateComponent<ProjectScannerSettings> {
    private ProjectScannerSettings state;
    private ProjectScannerSettingsForm settingsForm;

    public static ProjectScannerApplicationComponent getInstance() {
        return ApplicationManager.getApplication().getComponent(ProjectScannerApplicationComponent.class);
    }

    public void initComponent() {
        if (state == null) {
            state = new ProjectScannerSettings();
        }

    }

    public void disposeComponent() {

    }

    @NotNull
    public String getComponentName() {
        return "Project Scanner";
    }

    @Nls
    @Override
    public String getDisplayName() {
        return "Project Scanner";
    }

    @Nullable
    @Override
    public String getHelpTopic() {
        return null;
    }


    @Nullable
    @Override
    public JComponent createComponent() {
        if (settingsForm == null) {
            settingsForm = new ProjectScannerSettingsForm();
        }
        return settingsForm;
    }

    @Override
    public boolean isModified() {
        return settingsForm.isModified(state);
    }

    @Override
    public void apply() throws ConfigurationException {
        if (settingsForm != null) {
            settingsForm.getData(state);
        }
    }

    @Override
    public void reset() {
        if (settingsForm != null) {
            settingsForm.setData(state);
        }
    }

    @Override
    public void disposeUIResources() {
        settingsForm = null;
    }

    @Nullable
    @Override
    public ProjectScannerSettings getState() {
        return state;
    }

    @Override
    public void loadState(ProjectScannerSettings goToProjectWindowSettings) {
        this.state = goToProjectWindowSettings;
    }

}
