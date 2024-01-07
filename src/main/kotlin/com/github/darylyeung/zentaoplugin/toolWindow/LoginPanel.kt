package com.github.darylyeung.zentaoplugin.toolWindow

import com.beust.klaxon.Klaxon
import com.github.darylyeung.zentaoplugin.common.Constant
import com.github.darylyeung.zentaoplugin.model.TokenResponse
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.options.BoundConfigurable
import com.intellij.openapi.ui.DialogPanel
import com.intellij.ui.dsl.builder.bindText
import com.intellij.ui.dsl.builder.panel
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-06 11:49:29
 */
class LoginPanel(displayName: String, helpTopic: String?) :
    BoundConfigurable(displayName, helpTopic) {

    private var serverUrl = ""
    private var account = ""
    private var password = ""

    override fun createPanel(): DialogPanel {
        return panel {
            val serverUrlRow = row("Server Url") {
                textField().bindText(::serverUrl)
                label("example: http://zentao.com")
            }
            val accountRow = row("Username") {
                textField().bindText(::account)
            }
            val pwdRow = row("Password") {
                passwordField().bindText(::password)
            }

            row {
                button("Submit") {
                    thisLogger().info("serverUrl: ${serverUrl.trim()},username: ${account.trim()},password: ${password.trim()}")
                    //  get token
                    if (loginSuccessful(serverUrl.trim(), account.trim(), password.trim())) {
                        thisLogger().info("login successful")
//                        toolWindow.contentManager.removeAllContents(true)
//                        val listPanel = ProductListPanel().createListPanel(toolWindow)
//                        val content = ContentFactory.getInstance().createContent(listPanel, null, false)
//                        toolWindow.contentManager.addContent(content)
                    }
                }
            }
        }
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
}

