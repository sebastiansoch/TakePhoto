package com.sebastiansoch.takephoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sebastiansoch.takephoto.info.CameraSettings;
import com.sebastiansoch.takephoto.info.Schedule;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_SCHEDULER_REQ_CODE = 1;
    private static final int CAMERA_PREVIEW_REQ_CODE = 2;

    private Schedule schedule;
    private CameraSettings cameraSettings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openPhotoScheduler(View view) {
        startActivityForResult(new Intent(this, PhotoScheduler.class), PHOTO_SCHEDULER_REQ_CODE);
    }

    public void openCameraPreview(View view) {
        startActivityForResult(new Intent(this, CameraPreview.class), CAMERA_PREVIEW_REQ_CODE);
    }

    public void executeSchedular(View view) {
        if (cameraSettings != null) {
            Intent intent = new Intent(this, SavePhoto.class);
            intent.putExtra("CameraSettings", cameraSettings);
            startActivity(intent);
        } else {
            Toast.makeText(this, "Open camera preview to init settings", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_SCHEDULER_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    schedule = (Schedule) data.getSerializableExtra("PhotoSchedule");
                }
            case CAMERA_PREVIEW_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    cameraSettings = (CameraSettings) data.getSerializableExtra("CameraSettings");
                }
        }

        if (schedule != null) {
            Toast.makeText(this, "Schedule: " + schedule.getPeriod(), Toast.LENGTH_SHORT).show();
        }
    }
}
