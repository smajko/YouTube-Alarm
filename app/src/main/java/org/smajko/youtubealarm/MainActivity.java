/*
		Main activity of app displaying ui elements on the primary screen
		Sends a broadcast to receiver when alarmManager time matches current time
 */

package org.smajko.youtubealarm;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.media.Ringtone;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.ToggleButton;
import android.widget.CheckBox;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import java.util.Calendar;

public class MainActivity extends Activity {

	AlarmManager alarmManager;

	private PendingIntent pendingIntent;
	private TimePicker alarmTimePicker;

	//ringtone initialized to control in othe ractivities
	public static Uri alarmUri;
	public static Ringtone ringtone;

	//stream mode option
	boolean stream = false;
	CheckBox streamCheckBox;

	//network connection checker
	public static ConnectivityManager cmanager;
	public static NetworkInfo networkInfoObj;

	//default values
	int snoozeSeconds = 10;
	String ytLink = "https://www.youtube.com/watch?v=ttcboE1GrNg";

	@Override
	public void onStart() {
		super.onStart();
	}

	//declare ui elements and alarm manager
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);

		alarmTimePicker = (TimePicker) findViewById(R.id.alarmTimePicker);
		alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
		streamCheckBox = (CheckBox) findViewById(R.id.checkbox_stream);

	}

	//onclick event for options button to open OptionsActivity
	public void openOptions(View view){
		Intent optionsIntent = new Intent(MainActivity.this, OptionsActivity.class);
		startActivityForResult(optionsIntent,1);
	}

	//retrieves data from called activities such as options to apply the changes
	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		switch(requestCode) {
			//requestCode for options
			case (1) : {
				if (resultCode == Activity.RESULT_OK) {
					int tempSeconds = data.getIntExtra("seconds",0);
					String temp = data.getStringExtra("link");
					//ignore empty options, use last used options
					if (temp.length() > 1)
						ytLink = temp;
					if (tempSeconds > 0)
						snoozeSeconds = tempSeconds;
				}
				break;
			}
		}
	}

	public void onToggleClicked(View view) {
		if (((ToggleButton)view).isChecked()) {

			//instantiate calendar to call alarm on time match
			Calendar calendar = Calendar.getInstance();
			calendar.set(Calendar.HOUR_OF_DAY, alarmTimePicker.getCurrentHour());
			calendar.set(Calendar.MINUTE, alarmTimePicker.getCurrentMinute());

			//if time is set earlier than current (e.g set for 6:59pm, currently 7pm), do not play until next clock cycle/day
			if(calendar.before(Calendar.getInstance())) {
				calendar.add(Calendar.DATE, 1);
			}

			//cancel any currently pending intents if toggle button is toggled on
			Intent myIntent = new Intent(MainActivity.this, AlarmReceiver.class);
			PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT).cancel();

			//add parameters to pass to activities that play the alarm
			myIntent.putExtra("stream",stream);
			myIntent.putExtra("seconds",snoozeSeconds);
			myIntent.putExtra("link",ytLink);

			//create pending intent to broadcast to activity that plays the alarm
			pendingIntent = PendingIntent.getBroadcast(MainActivity.this, 0, myIntent, PendingIntent.FLAG_UPDATE_CURRENT);
			if(Build.VERSION.SDK_INT < 23){
				if(Build.VERSION.SDK_INT >= 19)
					alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
				else
					alarmManager.set(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			}
			else {
				alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);
			}
		} else {
			//cancel any pending intent on toggle off
			alarmManager.cancel(pendingIntent);
		}
	}

	//toggle stream mode on and off via checkbox
	public void onCheckStream(View view){
		if (((CheckBox)view).isChecked()) {
			stream = true;
		} else {
			stream = false;
		}
	}
}
