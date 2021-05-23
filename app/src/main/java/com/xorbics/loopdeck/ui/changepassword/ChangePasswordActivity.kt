package com.xorbics.loopdeck.ui.changepassword

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xorbics.loopdeck.R
import kotlinx.android.synthetic.main.fragment_playlist.*

class ChangePasswordActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_change_password)

        toolbar.setNavigationIcon(R.drawable.ic_back_black)
        toolbar.setNavigationOnClickListener { onBackPressed() }
    }
}