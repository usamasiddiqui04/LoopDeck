package com.xorbix.loopdeck.ui.onboardingscreen

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.xorbix.loopdeck.R
import com.xorbix.loopdeck.ui.login.WebviewLoginActivity
import kotlinx.android.synthetic.main.activity_on_boarding_screen.*

class OnBoardingScreenActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_on_boarding_screen)

        floatingactionbutton.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    WebviewLoginActivity::class.java
                )
            )
        }
    }
}