package com.xorbics.loopdeck.ui.main


import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.StrictMode
import androidx.appcompat.app.AppCompatActivity
import com.xorbics.loopdeck.R
import com.xorbics.loopdeck.ui.collection.CollectionActivity
import com.xorbics.loopdeck.ui.login.WebviewLoginActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (Build.VERSION.SDK_INT > 9) {
            val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
            StrictMode.setThreadPolicy(policy)
        }
        initViews()
    }

    private fun initViews() {

        btn_recents.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    CollectionActivity::class.java
                )
            )
        }

        btn_login.setOnClickListener {
            startActivity(
                Intent(
                    this@MainActivity,
                    WebviewLoginActivity::class.java
                )
            )
        }
    }


}