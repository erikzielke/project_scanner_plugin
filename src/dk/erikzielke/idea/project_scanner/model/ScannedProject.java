package dk.erikzielke.idea.project_scanner.model;

import java.io.Serializable;

public class ScannedProject implements Serializable {
    private String name;
    private String location;

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

    @Override
    public String toString() {
        if (name != null) {
            return name;
        }
        return location;
    }
}
