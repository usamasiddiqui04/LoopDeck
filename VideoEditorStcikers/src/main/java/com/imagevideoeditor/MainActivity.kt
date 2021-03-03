package com.imagevideoeditor

import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.imagevideoeditor.Utils.CameraUtils
import com.imagevideoeditor.Utils.CameraUtils.OnCameraResult
import com.kbeanie.multipicker.api.CameraVideoPicker
import com.kbeanie.multipicker.api.entity.ChosenImage
import com.kbeanie.multipicker.api.entity.ChosenVideo
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity(), OnCameraResult {
    private var cameraUtils: CameraUtils? = null
    private val cameraVideoPicker: CameraVideoPicker? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        cameraUtils = CameraUtils(this, this)
        btnPhoto!!.setOnClickListener {
            //                cameraUtils.openCameraGallery();
        }
        btnVideo!!.setOnClickListener { cameraUtils!!.alertVideoSelcetion() }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        cameraUtils!!.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onSuccess(images: List<ChosenImage>) {
        if (images != null && images.size > 0) {
            val i = Intent(this@MainActivity, PreviewPhotoActivity::class.java)
            i.putExtra("DATA", images[0].originalPath)
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i)
        }
    }

    override fun onError(error: String) {}
    public override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        cameraUtils!!.onActivityResult(requestCode, resultCode, data)
    }

    override fun onVideoSuccess(list: List<ChosenVideo>) {
        if (list != null && list.size > 0) {
            val i = Intent(this@MainActivity, PreviewVideoActivity::class.java)
            i.putExtra("DATA", list[0].originalPath)
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i)
        }
    }
}