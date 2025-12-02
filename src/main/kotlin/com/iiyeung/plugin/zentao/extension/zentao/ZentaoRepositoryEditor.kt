package com.iiyeung.plugin.zentao.extension.zentao

import com.iiyeung.plugin.zentao.ZentaoBundle
import com.iiyeung.plugin.zentao.extension.zentao.model.ZentaoProduct
import com.intellij.openapi.progress.ProgressIndicator
import com.intellij.openapi.project.Project
import com.intellij.openapi.util.NlsContexts
import com.intellij.tasks.config.BaseRepositoryEditor
import com.intellij.tasks.impl.TaskUiUtil
import com.intellij.util.Consumer
import com.intellij.util.ui.FormBuilder
import java.net.URI
import javax.swing.JComboBox
import javax.swing.JComponent
import javax.swing.event.DocumentEvent
import javax.swing.event.DocumentListener


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

    private fun setComponentError(component: JComponent, message: String?) {
        component.putClientProperty("JComponent.outline", if (message.isNullOrBlank()) null else "error")
        component.toolTipText = message
    }

    private fun isValidHttpUrl(text: String?): Boolean {
        if (text.isNullOrBlank()) return false
        val trimmed = text.trim()
        if (!(trimmed.startsWith("http://") || trimmed.startsWith("https://"))) return false
        return try {
            val uri = URI(trimmed)
            !uri.host.isNullOrBlank()
        } catch (_: Exception) {
            false
        }
    }

    private fun installValidation() {
        // Validate URL format
        val urlDoc = myURLText.document
        urlDoc.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = validateAll()
            override fun removeUpdate(e: DocumentEvent) = validateAll()
            override fun changedUpdate(e: DocumentEvent) = validateAll()
        })

        // Validate required username
        myUserNameText.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = validateAll()
            override fun removeUpdate(e: DocumentEvent) = validateAll()
            override fun changedUpdate(e: DocumentEvent) = validateAll()
        })

        // Validate required password
        myPasswordText.document.addDocumentListener(object : DocumentListener {
            override fun insertUpdate(e: DocumentEvent) = validateAll()
            override fun removeUpdate(e: DocumentEvent) = validateAll()
            override fun changedUpdate(e: DocumentEvent) = validateAll()
        })

        // Initial validation
        validateAll()
    }

    private fun validateAll() {
        val urlText = myURLText.text
        val userText = myUserNameText.text
        val pwdText = String(myPasswordText.password)

        val urlError = if (isValidHttpUrl(urlText)) null else ZentaoBundle.message("editor.validation.invalid.url")
        val userError = if (userText.isNullOrBlank()) ZentaoBundle.message("editor.validation.username.empty") else null
        val pwdError = if (pwdText.isBlank()) ZentaoBundle.message("editor.validation.password.empty") else null

        setComponentError(myURLText, urlError)
        setComponentError(myUserNameText, userError)
        setComponentError(myPasswordText, pwdError)
    }

    override fun createCustomPanel(): JComponent? {
        myUsernameLabel.isVisible = true
        myUserNameText.isVisible = true
        myUsernameLabel.text = "Username"
        myPasswordLabel.text = "Password"
        val panel = FormBuilder.createFormBuilder().panel
        // Install simple live validation for base URL and required fields
        installValidation()
        return panel
    }
}