package com.imagevideoeditor;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.imagevideoeditor.Utils.CameraUtils;
import com.kbeanie.multipicker.api.CameraVideoPicker;
import com.kbeanie.multipicker.api.entity.ChosenImage;
import com.kbeanie.multipicker.api.entity.ChosenVideo;

import java.util.List;

public class MainActivity extends AppCompatActivity implements CameraUtils.OnCameraResult {

    private CameraUtils cameraUtils;
    private CameraVideoPicker cameraVideoPicker;
    Button btnPhoto , btnVideo ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_video_text);
        cameraUtils = new CameraUtils(this, this);

        btnVideo = findViewById(R.id.btnVideo);
        btnPhoto = findViewById(R.id.btnPhoto);

        btnPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
//                cameraUtils.openCameraGallery();
            }
        });
        btnVideo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cameraUtils.alertVideoSelcetion();
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        cameraUtils.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    @Override
    public void onSuccess(List<ChosenImage> images) {
        if (images != null && images.size() > 0) {
            Intent i = new Intent(MainActivity.this, PreviewPhotoActivity.class);
            i.putExtra("DATA", images.get(0).getOriginalPath());
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i);

        }
    }

    @Override
    public void onError(String error) {

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        cameraUtils.onActivityResult(requestCode, resultCode, data);

    }


    @Override
    public void onVideoSuccess(List<ChosenVideo> list) {
        if (list != null && list.size() > 0) {
            Intent i = new Intent(MainActivity.this, PreviewVideoActivity.class);
            i.putExtra("DATA", list.get(0).getOriginalPath());
            //binding.ivProfilePic.setImageURI(Uri.fromFile(selectedImageFile));
            startActivity(i);

        }
    }
}

