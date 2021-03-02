package com.example.loopdeck.ui.login

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.KeyEvent
import android.webkit.CookieManager
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import com.example.loopdeck.R
import com.example.loopdeck.ui.collection.CollectionActivity
import java.net.CookiePolicy

class WebviewLoginActivity : AppCompatActivity() {

    private lateinit var webView: WebView
    private var url = "http://loopdeck-dev-login-app.azurewebsites.net/"

    @SuppressLint("SetJavaScriptEnabled")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_webview_login)

        webView = findViewById(R.id.webview)
        webView.settings.setJavaScriptEnabled(true)

        webView.webViewClient = object : WebViewClient() {
            override fun shouldOverrideUrlLoading(view: WebView?, url: String?): Boolean {
                if (url!!.indexOf("loopdeck-dev-login-app.azurewebsites.net") > -1)
                    return false
                else {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
                    startActivity(intent)

                    return true
                }
            }

            override fun onPageFinished(view: WebView?, url: String?) {
                super.onPageFinished(view, url)

                val cookiename = getCookie(url, "token")

                if(!cookiename.isNullOrBlank()){
                    val intent = Intent(this@WebviewLoginActivity,CollectionActivity::class.java )
                    finish()
                    startActivity(intent)


                }
                Log.d("Webview", "All the cookies in a string:$cookiename")

            }
        }
        webView.loadUrl(url)


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
        val temp = cookies?.split(";".toRegex())?.toTypedArray()

        if(temp != null) {
            for (ar1 in temp) {
                if (ar1.contains(CookieName!!)) {
                    val temp1 = ar1.split("=".toRegex()).toTypedArray()
                    CookieValue = temp1[1]
                    break
                }
            }
        }
        return CookieValue
    }


}