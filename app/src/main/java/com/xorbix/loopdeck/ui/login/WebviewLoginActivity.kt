package com.xorbix.loopdeck.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.xorbix.loopdeck.R

class WebviewLoginActivity : AppCompatActivity() {


    companion object {

        const val website = "loopdeck-dev-webapp.azurewebsites.net"
        const val base_url = "http://$website"
        const val main_page = "$base_url/mainpage"
    }

    private lateinit var webView: WebView

//    private var url = "https://wedevelop.ca/temp/Login_Updated/login.html"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_login)

        webView = findViewById(R.id.webview)
        webView.settings.javaScriptEnabled = true

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.indexOf(website) > -1)
                    return false
                else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)

                    return true
                }
            }

        }
        webView.loadUrl(main_page)


    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        if (keyCode == KeyEvent.KEYCODE_BACK && webView.canGoBack()) {
            webView.goBack()
            return true
        }
        return super.onKeyDown(keyCode, event)
    }

    fun getCookie(siteName: String?, CookieName: String?): String? {
        var CookieValue: String? = null
        val cookieManager = CookieManager.getInstance()
        val cookies = cookieManager.getCookie(siteName)
        val temp = cookies.split(";".toRegex()).toTypedArray()
        for (ar1 in temp) {
            if (ar1.contains(CookieName!!)) {
                val temp1 = ar1.split("=".toRegex()).toTypedArray()
                CookieValue = temp1[1]
                break
            }
        }
        return CookieValue
    }


}