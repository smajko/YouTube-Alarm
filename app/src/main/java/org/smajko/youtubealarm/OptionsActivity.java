package org.smajko.youtubealarm;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.view.View;

import static org.smajko.youtubealarm.R.id.secondPicker;
import static org.smajko.youtubealarm.R.id.yt_text;

public class OptionsActivity extends Activity {
    //data structures that hold the data retrieved from user's input in options
    NumberPicker picker;
    EditText editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_options);

        //define number picker and edit text to set seconds/youtube link
        editor = (EditText) findViewById(yt_text);
        picker = (NumberPicker) findViewById(secondPicker);
        picker.setMinValue(0);
        picker.setMaxValue(20);
    }

    public void applySettings(View view){
        int seconds = picker.getValue();
        String link = editor.getText().toString();

        //return settings that user sets to activity that called it (MainActivity)
        Intent resultIntent = new Intent();
        resultIntent.putExtra("seconds",seconds);
        resultIntent.putExtra("link",link);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    }
}
