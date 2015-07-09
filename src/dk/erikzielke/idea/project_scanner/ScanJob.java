package dk.erikzielke.idea.project_scanner;

import com.intellij.openapi.util.JDOMUtil;
import com.intellij.util.xmlb.XmlSerializer;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerResult;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerStateApplicationComponent;
import dk.erikzielke.idea.project_scanner.settings.tags.ProjectScannerTags;
import org.apache.commons.lang.SystemUtils;
import org.jdom.Element;
import org.jdom.JDOMException;

import java.io.*;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.SimpleFileVisitor;
import java.util.ArrayList;
import java.util.List;

public class ScanJob {

    public static void main(String[] args) {
        ScanJob scanJob = new ScanJob();
        List<ScannedProject> scannedProjects = scanJob.execute("C:\\Users\\Erik");
        System.out.println(scannedProjects);
    }

    public List<ScannedProject> execute(String... roots) {
        ArrayList<ScannedProject> scannedProjectList = new ArrayList<>();
        for (String root : roots) {
            File file = new File(root);
            boolean javaVersionAtLeast = SystemUtils.isJavaVersionAtLeast(1.7f);
            if (javaVersionAtLeast) {
                scan7(file, scannedProjectList);
            } else {
                scan(file, scannedProjectList);
            }
        }
        System.out.println(scannedProjectList);
        ProjectScannerStateApplicationComponent instance = ProjectScannerStateApplicationComponent.getInstance();
        ProjectScannerResult state = new ProjectScannerResult();
        state.setScannedProjects(scannedProjectList);
        instance.setState(state);
        return scannedProjectList;
    }

    private void scan7(File file, ArrayList<ScannedProject> scannedProjectList) {
        try {
            if (file.isDirectory()) {
                Path path = file.toPath();
                Files.walkFileTree(path, new SimpleFileVisitor<Path>() {
                    @Override
                    public FileVisitResult visitFileFailed(Path file, IOException exc) throws IOException {
                        return FileVisitResult.CONTINUE;
                    }

                    @Override
                    public FileVisitResult postVisitDirectory(Path dir, IOException exc) throws IOException {
                        Path fileName = dir.getFileName();
                        if (fileName.toString().equals(".idea")) {
                            createScannedProject(dir.getParent().toAbsolutePath().toString(), scannedProjectList, dir.toFile());
                        }

                        return super.postVisitDirectory(dir, exc);

                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void scan(File file, List<ScannedProject> scannedProjectList) {
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null) {
                for (File contentOfDir : files) {
                    if (contentOfDir.isDirectory() && contentOfDir.getName().equals(".idea")) {
                        createScannedProject(file.getAbsolutePath(), scannedProjectList, contentOfDir);
                    } else if (contentOfDir.isDirectory()) {
                        scan(contentOfDir, scannedProjectList);
                    }
                }
            }
        }
    }

    private void createScannedProject(String absolutePath, List<ScannedProject> scannedProjectList, File contentOfDir) {
        File nameFile = new File(contentOfDir, ".name");
        ScannedProject scannedProject = new ScannedProject();

        if (nameFile.exists()) {
            try {
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(nameFile)));
                String name = bufferedReader.readLine();
                scannedProject.setName(name);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

        scannedProject.setLocation(absolutePath);
        File tagFile = new File(contentOfDir, "ProjectScannerTags.xml");
        if (tagFile.exists()) {
            ProjectScannerTags bean = new ProjectScannerTags();
            try {
                List<Element> children = JDOMUtil.load(tagFile).getChildren();
                if (children != null && !children.isEmpty()) {
                    XmlSerializer.deserializeInto(bean, children.get(0));
                }
                scannedProject.setTags(bean.getTags());
            } catch (JDOMException | IOException e) {
                e.printStackTrace();
            }
        }

        scannedProjectList.add(scannedProject);
    }
}
