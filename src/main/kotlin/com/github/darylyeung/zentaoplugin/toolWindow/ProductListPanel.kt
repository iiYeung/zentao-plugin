package com.github.darylyeung.zentaoplugin.toolWindow

import com.github.darylyeung.zentaoplugin.common.Constant
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import kotlinx.serialization.json.Json
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-06 11:53:10
 */
class ProductListPanel {
    fun createListPanel(toolWindow: ToolWindow): JPanel {
        //  create JPanel
        val panel = JPanel()

        //  send GET request
        val serverUrl = PropertiesComponent.getInstance().getValue(Constant.SERVER_URL_KEY.value)
        val client = OkHttpClient()
        val request = Request.Builder()
            .url("$serverUrl/api.php/v1/products")
            .header("Token", PropertiesComponent.getInstance().getValue(Constant.TOKEN_KEY.value).toString())
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val result = response.body?.string().toString()
            thisLogger().info("request Successful. Response: $result")
            val response = Json.decodeFromString<ApiResponse>(result)
            val columnNames = arrayOf("ID", "Name", "Status")
            val rowData = response.products.map { product -> arrayOf(product.id, product.name, product.status) }
                .toTypedArray()
            val listPanel = DefaultTableModel(rowData, columnNames)
            val table = JTable(listPanel)

            table.addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent?) {
                    super.mouseClicked(e)
                    val row = table.rowAtPoint(e?.point ?: return)
                    val id = table.getValueAt(row, 0).toString().toInt()
                    thisLogger().info("id: $id")

                    toolWindow.contentManager.removeAllContents(true)
                    val listPanel = BugListPanel().createListPanel(id, toolWindow)
                    val content = ContentFactory.getInstance().createContent(listPanel, null, false)
                    toolWindow.contentManager.addContent(content)
                }
            })

            val scrollPane = JScrollPane(table)
            panel.add(scrollPane)
            panel.isVisible = true
            return panel
        } else {
            thisLogger().info("request failed")
            return JPanel()
        }
    }
}

data class ApiResponse(
    val page: Int,
    val total: Int,
    val limit: Int,
    val products: List<Product>
)

data class Product(
    val id: Int,
    val program: Int,
    val name: String,
    val code: String,
    val shadow: Int,
    val bind: String,
    val line: Int,
    val type: String,
    val status: String,
    val subStatus: String,
    val desc: String,
    val PO: UserInfo,
    val QD: UserInfo,
    val RD: UserInfo,
    val feedback: String,
    val ticket: String,
    val acl: String,
    val whitelist: List<Any>,
    val reviewer: String,
    val createdBy: UserInfo,
    val createdDate: String,
    val createdVersion: String,
    val order: Int,
    val vision: String,
    val deleted: String,
    val lineName: String,
    val programName: String,
    val programPM: String,
    val stories: Map<String, Int>,
    val requirements: Map<String, Any>,
    val plans: Int,
    val releases: Int,
    val bugs: Int,
    val unResolved: Int,
    val closedBugs: Int,
    val fixedBugs: Int,
    val thisWeekBugs: Int,
    val assignToNull: Int,
    val progress: Int,
)