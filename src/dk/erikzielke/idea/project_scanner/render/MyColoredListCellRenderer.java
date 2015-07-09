package dk.erikzielke.idea.project_scanner.render;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.ui.ColoredListCellRenderer;
import com.intellij.ui.JBColor;
import com.intellij.ui.SimpleTextAttributes;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerColorApplicationComponent;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerColors;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.awt.*;
import java.util.*;

public class MyColoredListCellRenderer extends ColoredListCellRenderer {
    private final SimpleTextAttributes simpleTextAttributes;
    private ArrayList<String> tags;
    private RefPainter myRefPainter;
    private ScannedProject scannedProject;
    private boolean open;

    public MyColoredListCellRenderer(SimpleTextAttributes simpleTextAttributes) {
        this.simpleTextAttributes = simpleTextAttributes;
        myRefPainter = new RefPainter();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (tags != null) {
            Font font = getFont();
            if (open) {
                font = font.deriveFont(Font.BOLD);
            }
            FontMetrics fontMetrics = getFontMetrics(font);

            int stringWidth = fontMetrics.stringWidth(scannedProject.toString());
            myRefPainter.drawLabels((Graphics2D) g, collectLabelsForRefs(), stringWidth + 6);
        }
    }

    @NotNull
    private Map<String, Color> collectLabelsForRefs() {
        if (tags.isEmpty()) {
            return Collections.emptyMap();
        }
        Map<String, Color> colorMap = new TreeMap<>();
        ProjectScannerColorApplicationComponent instance = ProjectScannerColorApplicationComponent.getInstance();
        for (String tag : tags) {
            if (tag != null) {
                ProjectScannerColors state = instance.getState();
                JBColor color = JBColor.WHITE;
                if (state != null && state.getColorMap().get(tag) != null) {
                    String tagColorString = state.getColorMap().get(tag);
                    int i = Integer.parseInt(tagColorString);
                    color = new JBColor(i, i);
                } else {
                    double hue = Math.random();
                    int rgb = Color.HSBtoRGB((float) hue, 0.5f, 0.9f);
                    color = new JBColor(rgb, rgb);
                    state.getColorMap().put(tag, String.valueOf(rgb));
                }
                colorMap.put(tag, color);
            }
        }
        return colorMap;
    }


    @Override
    protected void customizeCellRenderer(JList list, Object value, int index, boolean selected, boolean hasFocus) {
        Project[] openProjects = ProjectManager.getInstance().getOpenProjects();
        open = false;
        scannedProject = (ScannedProject) value;
        for (Project openProject : openProjects) {
            if (scannedProject.getLocation().equals(openProject.getBasePath())) {
                open = true;
                break;
            }
        }

        tags = scannedProject.getTags();
        Collections.sort(tags);
        String name = scannedProject.getName();
        if (name == null) {
            name = scannedProject.getLocation();
        }
        if (open) {
            append(name, simpleTextAttributes);
        } else {
            append(name);
        }
    }

}
