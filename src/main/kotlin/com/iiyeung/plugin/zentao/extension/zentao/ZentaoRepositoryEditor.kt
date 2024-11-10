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
import javax.swing.JComboBox
import javax.swing.JComponent


/**
 * @author iiYeung
 * @version v1.0
 * @date 2024-01-19 18:28:41
 */
class ZentaoRepositoryEditor : BaseRepositoryEditor<ZentaoRepository> {

    constructor(project: Project?, repository: ZentaoRepository, changeListener: Consumer<in ZentaoRepository>?) : super(
        project,
        repository,
        changeListener
    ) {
        //  custom
    }

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
        super.apply()
    }

    override fun createCustomPanel(): JComponent? {
        myUsernameLabel.isVisible = true
        myUserNameText.isVisible = true
        myUsernameLabel.text = TaskBundle.message("label.username")
        myPasswordLabel.text = TaskBundle.message("label.password")
        return FormBuilder.createFormBuilder()
            .getPanel()
    }
}