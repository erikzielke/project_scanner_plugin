package dk.erikzielke.idea.project_scanner.render.tag;

import com.intellij.icons.AllIcons;
import com.intellij.ide.DataManager;
import com.intellij.openapi.actionSystem.ActionGroup;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.project.DumbAwareAction;
import com.intellij.openapi.ui.popup.JBPopupFactory;
import com.intellij.openapi.ui.popup.ListPopup;
import com.intellij.ui.ClickListener;
import com.intellij.ui.RoundedLineBorder;
import com.intellij.util.ui.UIUtil;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import javax.swing.border.Border;
import java.awt.event.*;

public abstract class TagPopupComponent<Filter> extends JPanel {

    protected static final String ALL = "All";
    private static final int GAP_BEFORE_ARROW = 3;
    private static final int BORDER_SIZE = 2;
    private static final Border INNER_MARGIN_BORDER = BorderFactory.createEmptyBorder(2, 2, 2, 2);
    private static final Border FOCUSED_BORDER = createFocusedBorder();
    private static final Border UNFOCUSED_BORDER = createUnfocusedBorder();
    @NotNull
    protected final FilterModel<Filter> myFilterModel;


    @NotNull
    private final String myName;
    private JLabel myNameLabel;
    private JLabel myValueLabel;

    protected TagPopupComponent(@NotNull String name, @NotNull FilterModel<Filter> filterModel) {
        myName = name;
        myFilterModel = filterModel;
    }

    private static Border createFocusedBorder() {
        return BorderFactory.createCompoundBorder(new RoundedLineBorder(UIUtil.getHeaderActiveColor(), 10, BORDER_SIZE), INNER_MARGIN_BORDER);
    }

    private static Border createUnfocusedBorder() {
        return BorderFactory
                .createCompoundBorder(BorderFactory.createEmptyBorder(BORDER_SIZE, BORDER_SIZE, BORDER_SIZE, BORDER_SIZE), INNER_MARGIN_BORDER);
    }

    public JComponent initUi() {
        myNameLabel = new JLabel(myName + ": ");
        myValueLabel = new JLabel() {
            @Override
            public String getText() {
                return getCurrentText();
            }
        };
        setDefaultForeground();
        setFocusable(true);
        setBorder(UNFOCUSED_BORDER);

        setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
        add(myNameLabel);
        add(myValueLabel);
        add(Box.createHorizontalStrut(GAP_BEFORE_ARROW));
        add(new JLabel(AllIcons.Ide.Statusbar_arrows));

        installChangeListener(new Runnable() {
            @Override
            public void run() {
                myValueLabel.revalidate();
                myValueLabel.repaint();
            }
        });
        showPopupMenuOnClick();
        showPopupMenuFromKeyboard();
        indicateHovering();
        indicateFocusing();
        return this;
    }

    public String getCurrentText() {
        Filter filter = myFilterModel.getFilter();
        return filter == null ? ALL : getText(filter);
    }

    public void installChangeListener(@NotNull Runnable onChange) {
        myFilterModel.addSetFilterListener(onChange);
    }

    @NotNull
    protected abstract String getText(@NotNull Filter filter);

    @Nullable
    protected abstract String getToolTip(@NotNull Filter filter);

    @Override
    public String getToolTipText() {
        Filter filter = myFilterModel.getFilter();
        return filter == null ? null : getToolTip(filter);
    }

    /**
     * Returns the special action that indicates that no filtering is selected in this component.
     */
    @NotNull
    protected AnAction createAllAction() {
        return new AllAction();
    }

    /**
     * Create popup actions available under this filter.
     */
    protected abstract ActionGroup createActionGroup();

    private void indicateFocusing() {
        addFocusListener(new FocusAdapter() {
            @Override
            public void focusGained(@NotNull FocusEvent e) {
                setBorder(FOCUSED_BORDER);
            }

            @Override
            public void focusLost(@NotNull FocusEvent e) {
                setBorder(UNFOCUSED_BORDER);
            }
        });
    }

    private void showPopupMenuFromKeyboard() {
        addKeyListener(new KeyAdapter() {
            @Override
            public void keyPressed(@NotNull KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER || e.getKeyCode() == KeyEvent.VK_DOWN) {
                    showPopupMenu();
                }
            }
        });
    }

    private void showPopupMenuOnClick() {
        new ClickListener() {
            @Override
            public boolean onClick(@NotNull MouseEvent event, int clickCount) {
                showPopupMenu();
                return true;
            }
        }.installOn(this);
    }

    private void indicateHovering() {
        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseEntered(@NotNull MouseEvent e) {
                setOnHoverForeground();
            }

            @Override
            public void mouseExited(@NotNull MouseEvent e) {
                setDefaultForeground();
            }
        });
    }

    private void setDefaultForeground() {
        myNameLabel.setForeground(UIUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getInactiveTextColor());
        myValueLabel.setForeground(UIUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getInactiveTextColor().darker().darker());
    }

    private void setOnHoverForeground() {
        myNameLabel.setForeground(UIUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getTextAreaForeground());
        myValueLabel.setForeground(UIUtil.isUnderDarcula() ? UIUtil.getLabelForeground() : UIUtil.getTextFieldForeground());
    }

    private void showPopupMenu() {
        ListPopup popup = JBPopupFactory.getInstance()
                .createActionGroupPopup(null, createActionGroup(), DataManager.getInstance().getDataContext(this),
                        JBPopupFactory.ActionSelectionAid.SPEEDSEARCH, false);
        popup.showUnderneathOf(this);
    }

    private class AllAction extends DumbAwareAction {

        AllAction() {
            super(ALL);
        }

        @Override
        public void actionPerformed(@NotNull AnActionEvent e) {
            myFilterModel.setFilter(null);
        }
    }
}
