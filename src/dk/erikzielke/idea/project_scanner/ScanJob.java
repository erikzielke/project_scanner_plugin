package dk.erikzielke.idea.project_scanner;

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.xmlb.XmlSerializer;
import com.intellij.util.xmlb.XmlSerializerUtil;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerApplicationComponent;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerResult;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerSettings;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerStateApplicationComponent;
import dk.erikzielke.idea.project_scanner.settings.tags.ProjectScannerTags;
import org.jdom.JDOMException;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class ScanJob {

    public static void main(String[] args) {
        ScanJob scanJob = new ScanJob();
        List<ScannedProject> scannedProjects = scanJob.execute("C:\\Users\\Erik\\IdeaProjects");
        System.out.println(scannedProjects);
    }

    public List<ScannedProject> execute(String... roots) {
        ArrayList<ScannedProject> scannedProjectList = new ArrayList<>();
        for (String root : roots) {
            System.out.println("Scanning root " + root + "...");
            File file = new File(root);
            scan(file, scannedProjectList);
        }
        ProjectScannerStateApplicationComponent instance = ProjectScannerStateApplicationComponent.getInstance();
        ProjectScannerResult state = new ProjectScannerResult();
        state.setScannedProjects(scannedProjectList);
        instance.setState(state);
        return scannedProjectList;
    }

    private void scan(File file, List<ScannedProject> scannedProjectList) {
        if (file.isDirectory()) {
            System.out.println("Scanning " + file.getAbsolutePath() + "...");
            File[] files = file.listFiles();
            if (files != null) {
                for (File contentOfDir : files) {
                    if (contentOfDir.isDirectory() && contentOfDir.getName().equals(".idea")) {
                        System.out.println("Found .idea project in " + file.getAbsolutePath());
                        File nameFile = new File(contentOfDir, ".name");
                        ScannedProject scannedProject = new ScannedProject();
                        try {
                            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(nameFile)));
                            String name = bufferedReader.readLine();
                            scannedProject.setName(name);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }

                        scannedProject.setLocation(file.getAbsolutePath());
                        File tagFile = new File(contentOfDir, "ProjectScannerTags.xml");
                        if (tagFile.exists()) {
                            ProjectScannerTags bean = new ProjectScannerTags();
                            try {
                                XmlSerializer.deserializeInto(bean, JDOMUtil.load(tagFile).getChildren().get(0));
                                scannedProject.setTags(bean.getTags());
                            } catch (JDOMException | IOException e) {
                                e.printStackTrace();
                            }
                        }

                        scannedProjectList.add(scannedProject);


                    } else if (contentOfDir.isDirectory()) {
                        scan(contentOfDir, scannedProjectList);
                    }
                }
            }
        }
    }
}
