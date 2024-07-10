package com.iiyeung.plugin.zentao.extension.zentao

import com.iiyeung.plugin.zentao.ZentaoBundle
import com.iiyeung.plugin.zentao.extension.zentao.model.ZentaoProduct
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.tasks.TaskBundle
import com.intellij.tasks.config.BaseRepositoryEditor
import com.intellij.tasks.impl.TaskUiUtil
import com.intellij.util.Consumer
import com.intellij.util.ui.FormBuilder
import javax.swing.JCheckBox
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.SwingConstants


/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-19 18:28:41
 */
class ZentaoRepositoryEditor(
    project: Project?,
    repository: ZentaoRepository?,
    changeListener: Consumer<in ZentaoRepository>?
) : BaseRepositoryEditor<ZentaoRepository>(
    project,
    repository,
    changeListener,
) {
    private lateinit var myUseBearerTokenAuthenticationCheckBox: JCheckBox

    inner class FetchProjectsTask(
        project: Project?,
        title: @NlsContexts.ProgressTitle String,
        comboBox: JComboBox<ZentaoProduct>
    ) : TaskUiUtil.ComboBoxUpdater<ZentaoProduct>(
        project,
        ZentaoBundle.message("progress.title.downloading.zentao.products"),
        comboBox
    ) {
        override fun getSelectedItem(): ZentaoProduct? {
            return myRepository.getCurrentProduct()
        }

        override fun fetch(indicator: ProgressIndicator): List<ZentaoProduct> {
            return myRepository.fetchProducts()
        }
    }

    override fun apply() {
        myRepository.setUseBearerTokenAuthentication(myUseBearerTokenAuthenticationCheckBox.isSelected)
        super.apply()
        adjustSettingsForServerProperties()
    }

    override fun createCustomPanel(): JComponent? {
        myUseBearerTokenAuthenticationCheckBox = JCheckBox(ZentaoBundle.message("use.personal.access.token"))
        myUseBearerTokenAuthenticationCheckBox.isSelected = myRepository.isUseBearerTokenAuthentication()
        myUseBearerTokenAuthenticationCheckBox.addActionListener { useBearerTokenChanged() }
        myUseBearerTokenAuthenticationCheckBox.horizontalAlignment = SwingConstants.TRAILING
        adjustSettingsForServerProperties()
        return FormBuilder.createFormBuilder()
            .addComponentToRightColumn(myUseBearerTokenAuthenticationCheckBox)
            .getPanel()
    }

    private fun adjustSettingsForServerProperties() {
        if (myUseBearerTokenAuthenticationCheckBox.isSelected()) {
            myUsernameLabel.isVisible = false
            myUserNameText.isVisible = false
            myPasswordLabel.text = TaskBundle.message("label.api.token")
            myUseBearerTokenAuthenticationCheckBox.isVisible = true
        } else {
            myUsernameLabel.isVisible = true
            myUserNameText.isVisible = true
            myUsernameLabel.text = TaskBundle.message("label.username")
            myPasswordLabel.text = TaskBundle.message("label.password")
            myUseBearerTokenAuthenticationCheckBox.isVisible = true
        }
    }

    private fun useBearerTokenChanged() {
        myRepository.setUseBearerTokenAuthentication(myUseBearerTokenAuthenticationCheckBox.isSelected)
        adjustSettingsForServerProperties()
    }
}