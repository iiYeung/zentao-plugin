package com.github.darylyeung.zentaoplugin.toolWindow

import com.github.darylyeung.zentaoplugin.action.OpenTaskDialog
import com.github.darylyeung.zentaoplugin.common.Constant
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.tasks.TaskManager
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import okhttp3.OkHttpClient
import okhttp3.Request
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JPopupMenu
import javax.swing.JScrollPane
import javax.swing.JTable
import javax.swing.table.DefaultTableModel

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-06 18:31:37
 */
class BugListPanel {
    fun createListPanel(id: Int, toolWindow: ToolWindow): JPanel {
        // 创建 JPanel
        val panel = JPanel()

        //  send GET request
        val serverUrl = PropertiesComponent.getInstance().getValue(Constant.SERVER_URL_KEY.value)
        val client = OkHttpClient()
        val request = Request.Builder().url("$serverUrl/api.php/v1/products/:$id/bugs")
            .header("Token", PropertiesComponent.getInstance().getValue(Constant.TOKEN_KEY.value).toString()).build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val result = response.body?.string().toString()
            thisLogger().info("request Successful. Response: $result")
            val json = Json { ignoreUnknownKeys = true }
//            val response = Klaxon().parse<BugData>(result)
            val columnNames = arrayOf("ID", "Name")
//            val rowData = response?.bugs?.map { bug -> arrayOf(bug.id, bug.title) }
//                ?.toTypedArray()
            val account = PropertiesComponent.getInstance().getValue(Constant.USER_ACCOUNT.value).toString()
            val rowData = json.parseToJsonElement(result).jsonObject.get("bugs")?.jsonArray?.map { bug ->
//                if (bug.jsonObject.get("assignedTo")?.jsonObject?.get("account").toString().equals(account)) {
//                    arrayOf(
//                        bug.jsonObject.get("id"), bug.jsonObject.get("title").toString()
//                    )
//                }
                arrayOf(
                    bug.jsonObject.get("id"), bug.jsonObject.get("title").toString()
                )
            }?.toTypedArray()
            val listPanel = DefaultTableModel(rowData, columnNames)
            val table = JTable(listPanel)

            val popupMenu = JPopupMenu()
            val detailItem = popupMenu.add("Detail")
            val taskItem = popupMenu.add("Create Task")

            //  TODO detailItem

            //   taskItem
            taskItem.addActionListener {
                val selectedRow = table.selectedRow
                val id = table.getValueAt(selectedRow, 0).toString()
                val title = table.getValueAt(selectedRow, 1).toString()
//                OpenTaskDialog(DefaultProjectFactory.getInstance().defaultProject, LocalTaskImpl(id, title))().show()
                val project = toolWindow.project
                val manager = TaskManager.getManager(project)
                OpenTaskDialog(
                    toolWindow.project,
                    manager.createLocalTask(title)
                ).show()
            }
//            table.addMouseListener(object : MouseAdapter() {
//                override fun mouseClicked(e: MouseEvent?) {
//                    super.mouseClicked(e)
//                    if (e?.clickCount == 2) {
//                        val row = table.rowAtPoint(e.point)
//                        val id = table.getValueAt(row, 0)
//                        thisLogger().info("id: $id")
//
//                    }
//                }
//            })
            table.componentPopupMenu = popupMenu

            table.addMouseListener(object : MouseAdapter() {
                override fun mouseReleased(e: MouseEvent?) {
                    if (e != null) {
                        if (e.button == MouseEvent.BUTTON3) {
                            popupMenu.show(e.component, e.x, e.y)
                        }
                    }
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

data class BugData(
    val page: Int, val total: Int, val limit: Int, val bugs: List<Bug>
)

data class Bug(
    val id: Int,
    val project: Int,
    val product: Int,
    val injection: Int,
    val identify: Int,
    val branch: Int,
    val module: Int,
    val execution: Int,
    val plan: Int,
    val story: Int,
    val storyVersion: Int,
    val task: Int,
    val toTask: Int,
    val toStory: Int,
    val title: String,
    val keywords: String,
    val severity: Int,
    val pri: Int,
    val type: String,
    val os: String,
    val browser: String,
    val hardware: String,
    val found: String,
    val steps: String,
    val status: String,
    val subStatus: String,
    val color: String,
    val confirmed: Int,
    val activatedCount: Int,
    val activatedDate: String?,
    val feedbackBy: String,
    val notifyEmail: String,
    val mailto: List<Any>,
    val openedBy: UserInfo,
    val openedDate: String,
    val openedBuild: String,
    val assignedTo: UserInfo,
    val assignedDate: String,
    val deadline: String,
    val resolvedBy: UserInfo?,
    val resolution: String,
    val resolvedBuild: String,
    val resolvedDate: String?,
    val closedBy: UserInfo?,
    val closedDate: String?,
    val duplicateBug: Int,
    val linkBug: String,
    val case: Int,
    val caseVersion: Int,
    val feedback: Int,
    val result: Int,
    val repo: Int,
    val mr: Int,
    val entry: String,
    val lines: String,
    val v1: String,
    val v2: String,
    val repoType: String,
    val issueKey: String,
    val testtask: Int,
    val lastEditedBy: UserInfo,
    val lastEditedDate: String,
    val deleted: Boolean,
    val priOrder: String,
    val severityOrder: Int,
    val delay: Int,
    val needconfirm: Boolean,
    val statusName: String,
    val productStatus: String
)

data class UserInfo(
    val id: Int, val account: String, val avatar: String, val realname: String
)