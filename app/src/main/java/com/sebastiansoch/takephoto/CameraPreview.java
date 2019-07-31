package com.sebastiansoch.takephoto;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

public class CameraPreview extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera_preview);
    }

    public void closeCameraPreview(View view) {
        finish();
    }
}
