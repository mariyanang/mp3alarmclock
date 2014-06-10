package net.mariyana.mp3alarmclock.view.activity;

import net.mariyana.mp3alarmclock.R;
import net.mariyana.mp3alarmclock.controller.AlarmsManager;
import net.mariyana.mp3alarmclock.model.Alarm;
import net.mariyana.mp3alarmclock.view.MediaPlayerService;
import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

public class RingingAlarmActivity extends Activity {
	private Window window;
	private AlarmsManager alarmsManger;
	public static final int SNOOZING_NOTIFICATION_ID = 222;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		window = this.getWindow();
		window.addFlags(WindowManager.LayoutParams.FLAG_DISMISS_KEYGUARD);
		window.addFlags(WindowManager.LayoutParams.FLAG_SHOW_WHEN_LOCKED);
		window.addFlags(WindowManager.LayoutParams.FLAG_TURN_SCREEN_ON);
		window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
		window.requestFeature(Window.FEATURE_ACTION_BAR);
		getActionBar().hide();

		setContentView(R.layout.ringing_alarm_activity);

		long alarmId = getIntent().getLongExtra(Alarm.INTENT_ID, -1);
		alarmsManger = new AlarmsManager(this);
		final Alarm alarm = alarmsManger.getAlarmById(alarmId);

		TextView alarmTime = (TextView) findViewById(R.id.alarm_time);
		alarmTime.setText(alarm.getTime());

		TextView alarmName = (TextView) findViewById(R.id.alarm_name);
		alarmName.setText(alarm.getName());

		Button snoozeBtn = (Button) findViewById(R.id.btn_snooze_alarm);
		snoozeBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						MediaPlayerService.class);
				intent.putExtra(Alarm.INTENT_ID, alarm.getId());
				stopService(intent);

				alarmsManger.setAlarmSnooze(alarm.getId() + 1000);
				finish();

				final Intent restartRingingActivityIntent = new Intent(
						getApplicationContext(), RingingAlarmActivity.class);
				restartRingingActivityIntent
						.addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
				restartRingingActivityIntent.putExtra(Alarm.INTENT_ID,
						alarm.getId());
				PendingIntent restartRingingActivityPenInt = PendingIntent
						.getActivity(getApplicationContext(),
								(int) System.currentTimeMillis(),
								restartRingingActivityIntent, 0);

				Notification.Builder notiBuilder = new Notification.Builder(
						RingingAlarmActivity.this)
						.setTicker("Snoozing for 5 more minutes")
						.setSmallIcon(R.drawable.ic_action_onoff_pressed)
						.setContentIntent(restartRingingActivityPenInt)
						.setContentTitle("mp3 alarm")
						.setContentText("Snoozing for 5 more minutes")
						.setOngoing(true);

				NotificationManager notiMng = (NotificationManager) RingingAlarmActivity.this
						.getSystemService(Context.NOTIFICATION_SERVICE);

				notiMng.cancel(MediaPlayerService.RINGING_NOTIFICATION_ID);
				notiMng.cancel(SNOOZING_NOTIFICATION_ID);

				notiMng.notify(SNOOZING_NOTIFICATION_ID, notiBuilder.build());

			}
		});

		Button stopBtn = (Button) findViewById(R.id.btn_stop_alarm);
		stopBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				Intent intent = new Intent(getApplicationContext(),
						MediaPlayerService.class);
				stopService(intent);

				alarmsManger.cancelAlarm(alarm.getId() + 1000);

				if (alarm.getMo() || alarm.getTu() || alarm.getWe()
						|| alarm.getTh() || alarm.getFr() || alarm.getSa()
						|| alarm.getSu()) {
					alarmsManger.setAlarm(alarm, true, false);
				} else {
					alarmsManger.setAlarm(alarm, false, false);
				}

				NotificationManager notiMng = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
				notiMng.cancel(SNOOZING_NOTIFICATION_ID);
				notiMng.cancel(MediaPlayerService.RINGING_NOTIFICATION_ID);
				finish();
			}
		});

		Intent intent = new Intent(getApplicationContext(),
				MediaPlayerService.class);
		intent.putExtra(MediaPlayerService.START_PLAY, true);
		intent.putExtra(Alarm.INTENT_ID, alarmId);
		startService(intent);
	}

	@Override
	public void onBackPressed() {
		super.onBackPressed();
		finish();
	}

	@Override
	protected void onResume() {
		super.onResume();
		alarmsManger = new AlarmsManager(this);
	}

	@Override
	protected void onPause() {
		super.onPause();
		alarmsManger.close();
		finish();
	}
}
