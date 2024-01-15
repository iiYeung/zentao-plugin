package com.github.darylyeung.zentaoplugin.action;

import com.github.darylyeung.zentaoplugin.toolWindow.ProductListPanel;
import com.intellij.openapi.ui.DialogPanel;
import com.intellij.openapi.wm.ToolWindow;
import com.intellij.ui.content.Content;
import com.intellij.ui.content.ContentFactory;

import javax.swing.*;

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-09 11:13:36
 */
public class LoginPanel {
    private JButton Submit;
    private JTextField serverUrl;
    private JTextField account;
    private JPasswordField password;

    public DialogPanel createPanel(ToolWindow toolWindow) {
        DialogPanel dialogPanel = new DialogPanel();
        dialogPanel.add(serverUrl);
        dialogPanel.add(account);
        dialogPanel.add(password);
        dialogPanel.add(Submit);

        Submit.addActionListener(e -> {
            String serverUrlText = serverUrl.getText();
            String accountText = account.getText();
            String passwordText = password.getText();
//            if (new LoginPanel().loginSuccessful(serverUrlText, accountText, passwordText)) {
//                toolWindow.getContentManager().removeAllContents(true);
//                JPanel listPanel = new ProductListPanel().createListPanel(toolWindow);
//                Content content = ContentFactory.getInstance().createContent(listPanel, "", false);
//                toolWindow.getContentManager().addContent(content);
//            }
        });
        return dialogPanel;
    }
}
