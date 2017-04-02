package org.smajko.youtubealarm;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.media.RingtoneManager;
import android.os.Bundle;
import android.app.Activity;
import android.content.Intent;
import android.view.View;
import android.text.format.DateUtils;

public class AlarmActivity extends Activity {
    int seconds;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_alarm);

        seconds = getIntent().getIntExtra("seconds",0);
        MainActivity.ringtone = RingtoneManager.getRingtone(AlarmActivity.this, MainActivity.alarmUri);
        MainActivity.ringtone.play();
    }

    //closes the snooze/dismiss screen and finishes the activity
    public void dismissClicked(View view){
        MainActivity.ringtone.stop();
        finish();
    }

    //instantiates alarm manager to play alarm after a fixed amount of time, which passes to alarm receiver and back to this activity to replay the alarm
    public void snoozeClicked(View view){
        MainActivity.ringtone.stop();
        AlarmManager snoozeManager = (AlarmManager)getSystemService(Context.ALARM_SERVICE);
        Intent myIntent = new Intent(this, AlarmReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
        long triggerTime = System.currentTimeMillis() + seconds * DateUtils.SECOND_IN_MILLIS;
        snoozeManager.set(AlarmManager.RTC_WAKEUP, triggerTime, pendingIntent);
        finish();
    }
}
