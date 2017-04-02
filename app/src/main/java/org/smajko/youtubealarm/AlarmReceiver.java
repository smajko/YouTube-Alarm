package org.smajko.youtubealarm;

import android.content.Context;
import android.content.Intent;
import android.media.RingtoneManager;
import android.support.v4.content.WakefulBroadcastReceiver;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.widget.Toast;


public class AlarmReceiver extends WakefulBroadcastReceiver {

    @Override
    public void onReceive(final Context context, Intent intent) {
        //retrieve parameters from MainActivity to set snooze seconds, ringtone http link and stream mode boolean
        boolean stream = intent.getBooleanExtra("stream",false);
        String link = intent.getStringExtra("link");
        int seconds = intent.getIntExtra("seconds",0);

        //check current network connection to decide whether to use default ringtone
        MainActivity.cmanager = (ConnectivityManager) context.getSystemService(context.CONNECTIVITY_SERVICE);
        MainActivity.networkInfoObj = MainActivity.cmanager.getActiveNetworkInfo();

        //if phone has a network connection, create intent that parses a youtube link and starts the youtube player
        if (stream && MainActivity.networkInfoObj != null && MainActivity.networkInfoObj.isConnected()) {
            Intent youtubeIntent = new Intent(Intent.ACTION_VIEW,Uri.parse(link));
            youtubeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(youtubeIntent);
        } else {
            //sets the default alarm of phone
            MainActivity.alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_ALARM);
            if (MainActivity.alarmUri == null) {
                //sets alarm as notification sound if alarm not set
                MainActivity.alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION);
                if (MainActivity.alarmUri == null){
                    //sets alarm as ringtone if alarm and notification sounds not set
                    MainActivity.alarmUri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                }
            }
            //MainActivity.ringtone = RingtoneManager.getRingtone(context, MainActivity.alarmUri);

            //play default alarm and set parameters for dismiss/snooze screen, then start the activity
            //MainActivity.ringtone.play();

            Intent alarmIntent = new Intent(context, AlarmActivity.class);
            alarmIntent.putExtra("seconds",seconds);
            alarmIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startWakefulService(context, alarmIntent);
        }
    }
}