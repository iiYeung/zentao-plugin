package com.github.darylyeung.zentaoplugin.toolWindow

import com.beust.klaxon.Klaxon
import com.github.darylyeung.zentaoplugin.common.Constant
import com.github.darylyeung.zentaoplugin.model.TokenResponse
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.jetbrains.annotations.ApiStatus

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-06 11:49:29
 */
class LoginPanel {
    private val model = Model()
    fun createPanel(toolWindow: ToolWindow): DialogPanel {
        lateinit var panel: DialogPanel
         panel = panel {
            row("Server Url") {
                textField().bindText(model::serverUrl)
                label("example: http://zentao.com")
            }
            row("Username") {
                textField().bindText(model::account)
            }
            row("Password") {
                passwordField().bindText(model::password)
            }

            row {
                button("Submit") {
                    panel.apply()
                    thisLogger().info("serverUrl: ${model.serverUrl},username: ${model.account},password: ${model.password}")
                    //  get token
                    if (loginSuccessful(model.serverUrl, model.account, model.password)) {
                        thisLogger().info("login successful")
                        toolWindow.contentManager.removeAllContents(true)
                        val listPanel = ProductListPanel().createListPanel(toolWindow)
                        val content = ContentFactory.getInstance().createContent(listPanel, null, false)
                        toolWindow.contentManager.addContent(content)
                    }
                }
            }
        }
        return panel
    }

    private fun loginSuccessful(serverUrl: String, username: String, password: String): Boolean {
        val client = OkHttpClient()
        val json = "{\"account\":\"$username\",\"password\":\"$password\"}"
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        PropertiesComponent.getInstance().setValue(Constant.SERVER_URL_KEY.value, serverUrl)
        PropertiesComponent.getInstance().setValue(Constant.USER_ACCOUNT.value, username)
        val request = Request.Builder()
            .url("$serverUrl/api.php/v1/tokens")
            .post(body)
            .build()
        val response = client.newCall(request).execute()
        if (response.isSuccessful) {
            val result = response.body?.string().toString()
            thisLogger().info("Login Successful. Response: $result")
            //  resolve token from response
            val tokenResponse = Klaxon().parse<TokenResponse>(result)
            val token = tokenResponse?.token
            thisLogger().info("Login Successful. Token: $token")
            PropertiesComponent.getInstance().setValue(Constant.TOKEN_KEY.value, token)
            return true
        } else {
            val result = response.body?.string().toString()
            println("Login Failed. Response: $result")
            return false
        }
    }

    @ApiStatus.Internal
    internal data class Model(
        var serverUrl: String = "",
        var account: String = "",
        var password: String = ""
    )
}

