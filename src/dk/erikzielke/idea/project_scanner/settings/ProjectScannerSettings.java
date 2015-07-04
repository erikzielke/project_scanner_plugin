package dk.erikzielke.idea.project_scanner.settings;

import java.util.ArrayList;
import java.util.List;

public class ProjectScannerSettings {
    private ArrayList<String> roots = new ArrayList<>();

    public ArrayList<String> getRoots() {
        return roots;
    }

    public void setRoots(ArrayList<String> roots) {
        this.roots = roots;
    }
}
