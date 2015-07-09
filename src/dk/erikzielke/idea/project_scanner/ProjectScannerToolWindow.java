package dk.erikzielke.idea.project_scanner;

import com.intellij.ide.actions.RevealFileAction;
import com.intellij.openapi.actionSystem.*;
import com.intellij.openapi.actionSystem.ex.CheckboxAction;
import com.intellij.openapi.actionSystem.ex.CustomComponentAction;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.SimpleToolWindowPanel;
import com.intellij.openapi.util.text.StringUtil;
import com.intellij.openapi.vfs.LocalFileSystem;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.ui.*;
import com.intellij.ui.components.JBList;
import dk.erikzielke.idea.project_scanner.model.ScannedProject;
import dk.erikzielke.idea.project_scanner.render.MyColoredListCellRenderer;
import dk.erikzielke.idea.project_scanner.render.tag.FilterModel;
import dk.erikzielke.idea.project_scanner.render.tag.TagPopupComponent;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerResult;
import dk.erikzielke.idea.project_scanner.settings.ProjectScannerStateApplicationComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.awt.*;
import java.io.File;
import java.util.*;
import java.util.List;

public class ProjectScannerToolWindow extends SimpleToolWindowPanel {

    public static DataKey<DefaultListModel<ScannedProject>> SCANNED_PROJECTS = DataKey.create("scannedProject");
    public static DataKey<ScannedProject> SELECTED_PROJECT = DataKey.create("selectedProject");
    private final DefaultListModel<ScannedProject> model;
    JList list;
    FilterModel<java.util.List<String>> filterModel;


    public ProjectScannerToolWindow(boolean vertical) {
        super(vertical, true);
        list = new JBList();
        ListSpeedSearch listSpeedSearch = new ListSpeedSearch(list);
        model = new DefaultListModel<>();
        list.setModel(model);

        list.addMouseListener(new PopupHandler() {
            @Override
            public void invokePopup(Component comp, int x, int y) {
                popupInvoked(comp, x, y);
            }
        });
        ProjectScannerStateApplicationComponent instance = ProjectScannerStateApplicationComponent.getInstance();
        ProjectScannerResult state = instance.getState();
        HashSet<String> filters = new HashSet<>();
        if (state != null) {
            java.util.List<ScannedProject> scannedProjects = state.getScannedProjects();
            for (ScannedProject scannedProject : scannedProjects) {
                model.addElement(scannedProject);
                ArrayList<String> tags = scannedProject.getTags();
                for (String tag : tags) {
                    filters.add(tag);
                }
            }
        }
        filterModel = new FilterModel<>();
        ArrayList<String> potentialTags = new ArrayList<>(filters);
        Collections.sort(potentialTags);
        filterModel.setPotentialTags(potentialTags);
        setToolbar(createToolbar());


        filterModel.addSetFilterListener(new Runnable() {
            @Override
            public void run() {
                ApplicationManager.getApplication().invokeLater(
                        new Runnable() {
                            @Override
                            public void run() {
                                model.clear();
                                ProjectScannerStateApplicationComponent instance = ProjectScannerStateApplicationComponent.getInstance();
                                ProjectScannerResult state = instance.getState();
                                if (state != null) {
                                    java.util.List<ScannedProject> scannedProjects = state.getScannedProjects();
                                    for (ScannedProject scannedProject : scannedProjects) {
                                        if (filterModel.getFilter() == null || containsAny(scannedProject.getTags(), filterModel.getFilter())) {
                                            model.addElement(scannedProject);
                                        }
                                    }
                                }
                            }
                        }
                );
            }
        });
        SimpleTextAttributes simpleTextAttributes = new SimpleTextAttributes(Font.BOLD, JBColor.BLACK);
        list.setCellRenderer(new MyColoredListCellRenderer(simpleTextAttributes));

        setContent(ScrollPaneFactory.createScrollPane(list));
    }

    private boolean containsAny(ArrayList<String> tags, List<String> filter) {
        HashSet<String> strings = new HashSet<>(tags);
        strings.retainAll(filter);
        return !strings.isEmpty();
    }

    private void popupInvoked(final Component comp, final int x, final int y) {
        final ScannedProject project = (ScannedProject) list.getSelectedValue();
        if (project != null) {
            final DefaultActionGroup group = new DefaultActionGroup();
            group.add(new OpenProjectInNewWindow(list));
            group.add(new RevealFileAction());
            group.add(new CopyPathAction());
            final ActionPopupMenu popupMenu = ActionManager.getInstance().createActionPopupMenu("projectScannerPopup", group);
            popupMenu.getComponent().show(comp, x, y);
        }
    }


    @Nullable
    @Override
    public Object getData(String dataId) {
        if (SCANNED_PROJECTS.is(dataId)) {
            return model;
        } else if (SELECTED_PROJECT.is(dataId)) {
            return list.getSelectedValue();
        } else if (CommonDataKeys.VIRTUAL_FILE.is(dataId)) {
            ScannedProject selectedValue = (ScannedProject) list.getSelectedValue();
            if (selectedValue != null) {
                VirtualFile fileByIoFile = LocalFileSystem.getInstance().findFileByIoFile(new File(selectedValue.getLocation()));
                return fileByIoFile;
            } else {
                return null;
            }
        }
        return super.getData(dataId);
    }

    private JComponent createToolbar() {
        final DefaultActionGroup group = new DefaultActionGroup();
        group.add(new TagAction(filterModel));
        OpenProjectInNewWindow action = new OpenProjectInNewWindow(list);
        group.add(new ProjectScanAction());
        group.add(action);

        final ActionToolbar actionToolBar = ActionManager.getInstance().createActionToolbar("projectScannerToolbar", group, true);

        final JPanel buttonsPanel = new JPanel(new BorderLayout());
        buttonsPanel.add(actionToolBar.getComponent(), BorderLayout.CENTER);
        return buttonsPanel;
    }

    public void refresh() {
        list.invalidate();
    }

    private static class TagAction extends DumbAwareAction implements CustomComponentAction {
        private FilterModel<java.util.List<String>> filterModel;

        public TagAction(FilterModel<java.util.List<String>> filterModel) {

            this.filterModel = filterModel;
        }

        @Override
        public void actionPerformed(AnActionEvent e) {

        }

        @Override
        public JComponent createCustomComponent(Presentation presentation) {

            return new TagPopupComponent<List<String>>("Tags", filterModel) {

                @NotNull
                @Override
                protected String getText(@NotNull List<String> values) {
                    return StringUtil.join(values, "|");
                }

                @Nullable
                @Override
                protected String getToolTip(@NotNull List<String> o) {
                    return null;
                }

                @Override
                protected ActionGroup createActionGroup() {
                    DefaultActionGroup defaultActionGroup = new DefaultActionGroup();
                    defaultActionGroup.add(new AllAction());
                    List<String> strings = filterModel.getPotentialTags();
                    for (String string : strings) {
                        defaultActionGroup.add(new ElementAction(string));
                    }

                    return defaultActionGroup;
                }
            }.initUi();
        }

        private class ElementAction extends CheckboxAction {
            private final String string;

            public ElementAction(String string) {
                super(string);
                this.string = string;
            }

            @Override
            public boolean isSelected(AnActionEvent e) {
                if (filterModel.getFilter() != null) {
                    return filterModel.getFilter().contains(string);
                }
                return false;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                if (filterModel.getFilter() != null && filterModel.getFilter().contains(string)) {
                    filterModel.getFilter().remove(string);
                    if (filterModel.getFilter().isEmpty()) {
                        filterModel.setFilter(null);
                    }
                } else {
                    if (filterModel.getFilter() == null) {
                        ArrayList<String> filter = new ArrayList<>();
                        filter.add(string);
                        filterModel.setFilter(filter);
                    } else {
                        List<String> filter = filterModel.getFilter();
                        filter.add(string);
                        filterModel.setFilter(filter);
                    }
                }
            }
        }

        private class AllAction extends CheckboxAction {
            public AllAction() {
                super("All");
            }


            @Override
            public boolean isSelected(AnActionEvent e) {
                return filterModel.getFilter() == null;
            }

            @Override
            public void setSelected(AnActionEvent e, boolean state) {
                if (state) {
                    filterModel.setFilter(null);
                }
            }
        }
    }
}
