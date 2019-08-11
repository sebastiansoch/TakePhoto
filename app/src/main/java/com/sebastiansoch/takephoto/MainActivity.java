package com.sebastiansoch.takephoto;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.sebastiansoch.takephoto.info.CameraSettings;
import com.sebastiansoch.takephoto.info.Schedule;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_SCHEDULER_REQ_CODE = 1;
    private static final int CAMERA_PREVIEW_REQ_CODE = 2;
    private static final int WRITE_EXTERNAL_STORAGE_REQ_CODE = 3;

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
        checkWriteStoragePermission();
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

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == WRITE_EXTERNAL_STORAGE_REQ_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                try {
                    createPhotoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Toast.makeText(this, "Permission successfully granted!", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, "App needs to save video to run", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void checkWriteStoragePermission() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
                try {
                    createPhotoFileName();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            } else {
                if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    Toast.makeText(this, "App needs to be able to save photo", Toast.LENGTH_SHORT).show();
                }

                requestPermissions(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, WRITE_EXTERNAL_STORAGE_REQ_CODE);
            }
        } else {
            try {
                createPhotoFileName();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void createPhotoFileName() throws IOException {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String prefix = "TakePhoto_" + timestamp + "_";

        File file = File.createTempFile(prefix, ".jpg", Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES));
    }

}
