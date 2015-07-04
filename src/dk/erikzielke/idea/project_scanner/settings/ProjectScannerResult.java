package dk.erikzielke.idea.project_scanner.settings;

import dk.erikzielke.idea.project_scanner.model.ScannedProject;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProjectScannerResult {
    private ArrayList<ScannedProject> scannedProjects = new ArrayList<>();


    public ArrayList<ScannedProject> getScannedProjects() {
        return scannedProjects;
    }

    public void setScannedProjects(ArrayList<ScannedProject> scannedProjects) {
        this.scannedProjects = scannedProjects;
    }
}
