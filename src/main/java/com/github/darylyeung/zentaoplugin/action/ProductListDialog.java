package com.github.darylyeung.zentaoplugin.action;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.DialogWrapper;
import com.intellij.tasks.Task;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-09 10:50:37
 */
public class ProductListDialog extends DialogWrapper {
    private JTable table1;
    private JPanel myPanel;

    public ProductListDialog(Project project, Task task) {
        super(project, false);
    }

    @Override
    protected @Nullable JComponent createCenterPanel() {
        return myPanel;
    }
}
