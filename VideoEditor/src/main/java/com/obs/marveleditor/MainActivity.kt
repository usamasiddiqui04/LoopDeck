/*
 *
 *  Created by Optisol on Aug 2019.
 *  Copyright © 2019 Optisol Business Solutions pvt ltd. All rights reserved.
 *
 */

package com.obs.marveleditor

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity

import com.facebook.drawee.backends.pipeline.Fresco
import com.obs.marveleditor.fragments.OptiMasterProcessorFragment

class MainActivity : AppCompatActivity() {

    private val sharedPrefFile = "videoPath"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        Fresco.initialize(this)
        setContentView(R.layout.activity_main_video_editor)

        val VideoPath: String = intent.getStringExtra("videoPath")
        val sharedPreferences: SharedPreferences = this.getSharedPreferences(sharedPrefFile,
            Context.MODE_PRIVATE)

        val editor:SharedPreferences.Editor =  sharedPreferences.edit()
        editor.putString("videoPath",VideoPath)
        editor.apply()
        editor.commit()

        val fragmentTransaction = supportFragmentManager.beginTransaction()
        fragmentTransaction.replace(R.id.frame_container1, OptiMasterProcessorFragment()).commit()
    }
}
