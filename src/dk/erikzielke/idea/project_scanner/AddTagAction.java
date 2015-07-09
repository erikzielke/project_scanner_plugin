package dk.erikzielke.idea.project_scanner;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.components.impl.stores.StorageData;
import com.intellij.openapi.components.impl.stores.StorageUtil;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.BufferExposingByteArrayOutputStream;
import com.intellij.util.LineSeparator;
import com.intellij.util.xmlb.XmlSerializer;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.settings.tags.ProjectScannerProjectComponent;
import dk.erikzielke.idea.project_scanner.settings.tags.ProjectScannerTags;
import org.jdom.Element;
import org.jdom.JDOMException;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Map;

public class AddTagAction extends AnAction {
    public AddTagAction() {
        super("Add Tag...", "Adds a tag to the selected project", null);
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        String tag = Messages.showInputDialog(getEventProject(e), "Write the tag you want to add", "Add Tag To", null);
        ScannedProject data = e.getData(ProjectScannerToolWindow.SELECTED_PROJECT);
        if (data != null) {
            ArrayList<String> tags = data.getTags();
            if (!tags.contains(tag)) {
                tags.add(tag);
                ProjectScannerTags projectScannerTags = new ProjectScannerTags();
                projectScannerTags.setTags(tags);
                Element serialize = XmlSerializer.serialize(projectScannerTags);
                try {
                    Element project = new Element("project");
                    Element component = new Element("component");
                    component.setAttribute("name", "ProjectScannerTags");
                    project.addContent(component);
                    Element child = serialize.getChildren().get(0);
                    child.detach();
                    component.addContent(child);
                    Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
                    for (Project openProject : openProjects) {
                        if (openProject.getBasePath().equals(data.getLocation())) {
                            ProjectScannerProjectComponent component1 = openProject.getComponent(ProjectScannerProjectComponent.class);
                            if (component1 != null) {
                                component1.setState(projectScannerTags);
                            }
                        }
                    }
                    File tagFile = new File(data.getLocation() + "/ProjectScannerTags.xml");
                    BufferExposingByteArrayOutputStream bufferExposingByteArrayOutputStream = StorageUtil.writeToBytes(project, LineSeparator.getSystemLineSeparator().getSeparatorString());
                    StorageUtil.writeFile(tagFile, this, null, bufferExposingByteArrayOutputStream, LineSeparator.getSystemLineSeparator());
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public static class MyStorageData extends StorageData {

        private final Element serialize;

        public MyStorageData(Element serialize) {
            super("project");
            this.serialize = serialize;
        }

        @Nullable
        @Override
        public Element save(@NotNull Map<String, Element> newLiveStates) {
            return super.save(newLiveStates);
        }

        @Nullable
        @Override
        public Element getState(@NotNull String name) {
            if ("ProjectScannerTags".equals(name)) {
                return serialize;
            }
            return super.getState(name);
        }
    }
}
