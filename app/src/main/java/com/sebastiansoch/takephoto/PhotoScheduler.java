package com.sebastiansoch.takephoto;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;

public class PhotoScheduler extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo_scheduler);

        Spinner timeTypeSpinner = findViewById(R.id.timeType_spinner);
        ArrayAdapter<CharSequence> timeTypeAdapter = ArrayAdapter.createFromResource(this, R.array.time_type, android.R.layout.simple_spinner_item);
        timeTypeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeTypeSpinner.setAdapter(timeTypeAdapter);
    }

    public void saveSchedule(View view) {
        EditText period = findViewById(R.id.timePeriod_editText);
        int interval = -1;
        try {
          interval = Integer.parseInt(period.getEditableText().toString());
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        Schedule schedule = new Schedule(interval, TimeType.SECONDS);
        Intent intent = new Intent();
        intent.putExtra("PhotoSchedule", schedule);
        setResult(RESULT_OK, intent);
        finish();
    }
}
