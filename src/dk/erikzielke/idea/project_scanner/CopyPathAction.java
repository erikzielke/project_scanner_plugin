package dk.erikzielke.idea.project_scanner;

import com.intellij.ide.IdeBundle;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.actionSystem.Presentation;
import com.intellij.openapi.ide.CopyPasteManager;
import com.intellij.openapi.project.DumbAware;
import com.intellij.openapi.vfs.VirtualFile;

import java.awt.datatransfer.StringSelection;

public class CopyPathAction extends AnAction {
    public CopyPathAction() {
        super(IdeBundle.message("action.copy.path",null,null));
    }

    @Override
    public void actionPerformed(AnActionEvent e) {
        VirtualFile file = CommonDataKeys.VIRTUAL_FILE.getData(e.getDataContext());
        if (file != null) {
            CopyPasteManager.getInstance().setContents(new StringSelection(file.getPresentableUrl()));
        }
    }


}
