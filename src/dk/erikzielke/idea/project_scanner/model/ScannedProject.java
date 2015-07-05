package dk.erikzielke.idea.project_scanner.model;

import java.io.Serializable;
import java.util.ArrayList;

public class ScannedProject implements Serializable {
    private String name;
    private String location;
    private ArrayList<String> tags;

    public ScannedProject() {
        tags = new ArrayList<>();
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public ArrayList<String> getTags() {
        return tags;
    }

    public void setTags(ArrayList<String> tags) {
        this.tags = tags;
    }

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return location;
    }
}
