package com.github.darylyeung.zentaoplugin.toolWindow

import com.beust.klaxon.Klaxon
import com.github.darylyeung.zentaoplugin.common.Constant
import com.intellij.ide.util.PropertiesComponent
import com.intellij.openapi.diagnostic.thisLogger
import com.intellij.openapi.wm.ToolWindow
import com.intellij.ui.content.ContentFactory
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import java.awt.GridBagConstraints
import java.awt.GridBagLayout
import javax.swing.JButton
import javax.swing.JLabel
import javax.swing.JPanel
import javax.swing.JTextField

/**
 * @author Yeung
 * @version v1.0
 * @date 2024-01-06 11:49:29
 */
class LoginPanel {
    fun createLoginPanel(toolWindow: ToolWindow): JPanel {

        val panel = JPanel()
        panel.layout = GridBagLayout()

        val constraints = GridBagConstraints()

        val serverUrlLabel = JLabel("Server Url")
        val serverUrlTextField = JTextField(200)

        val usernameLabel = JLabel("username")
        val usernameTextField = JTextField(100)

        val passwordLabel = JLabel("password")
        val passwordTextField = JTextField(100)

        val submitButton = JButton("Submit")
        submitButton.addActionListener {
            val serverUrl = serverUrlTextField.text
            val username = usernameTextField.text
            val password = passwordTextField.text

            thisLogger().info("serverUrl: $serverUrl,username: $username,password: $password")
            //  get token
            if (loginSuccessful(serverUrl, username, password)) {
                toolWindow.contentManager.removeAllContents(true)
                val listPanel = ProductListPanel().createListPanel(toolWindow)
                val content = ContentFactory.getInstance().createContent(listPanel, null, false)
                toolWindow.contentManager.addContent(content)
            }

        }
        constraints.anchor = GridBagConstraints.WEST

        constraints.gridx = 0
        constraints.gridy = 0
        panel.add(serverUrlLabel, constraints)

        constraints.gridx = 1
        constraints.gridy = 0
        panel.add(serverUrlTextField)

        constraints.gridx = 0
        constraints.gridy = 1
        panel.add(usernameLabel)

        constraints.gridx = 1
        constraints.gridy = 1
        panel.add(usernameTextField)

        constraints.gridx = 0
        constraints.gridy = 2
        panel.add(passwordLabel)

        constraints.gridx = 1
        constraints.gridy = 2
        panel.add(passwordTextField)

        constraints.gridwidth = 2
        constraints.gridx = 0
        constraints.gridy = 3
        panel.add(submitButton)
        return panel
    }


    private fun loginSuccessful(serverUrl: String, username: String, password: String): Boolean {
        val client = OkHttpClient()
        val json = "{\"account\":\"$username\",\"password\":\"$password\"}"
        val body = json.toRequestBody("application/json; charset=utf-8".toMediaTypeOrNull())
        PropertiesComponent.getInstance().setValue(Constant.SERVER_URL_KEY.value, serverUrl)
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

data class TokenResponse(val token: String)