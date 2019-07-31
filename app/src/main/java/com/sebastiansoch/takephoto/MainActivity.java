package com.sebastiansoch.takephoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final int PHOTO_SCHEDULER_REQ_CODE = 1;
    private Schedule schedule;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void openPhotoScheduler(View view) {
        startActivityForResult(new Intent(this, PhotoScheduler.class), PHOTO_SCHEDULER_REQ_CODE);
    }

    public void openCameraPreview(View view) {
        startActivity(new Intent(this, CameraPreview.class));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PHOTO_SCHEDULER_REQ_CODE:
                if (resultCode == RESULT_OK) {
                    schedule = (Schedule) data.getSerializableExtra("PhotoSchedule");
                }
        }

        Toast.makeText(this, "Schedule: " + schedule.getPeriod(), Toast.LENGTH_SHORT).show();
    }
}
