package net.mariyana.mp3alarmclock.view;

import net.mariyana.mp3alarmclock.controller.AlarmsManager;
import net.mariyana.mp3alarmclock.model.Alarm;
import net.mariyana.mp3alarmclock.view.activity.RingingAlarmActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class PlayAlarmReceiver extends BroadcastReceiver {

	// Notification ID to allow for future updates
	public static final int RINGING_NOTIFICATION_ID1 = 1;

	// Notification Sound (and Vibration on Arrival) autobots_roll_out
	// private Uri soundURI = Uri
	// .parse("android.resource://net.mariyana.mp3alarm/"
	// + R.raw.autobots_roll_out);
	//
	// private long[] mVibratePattern = { 0, 200, 200, 300 };

	@Override
	public void onReceive(Context context, Intent intent) {

		if (Intent.ACTION_BOOT_COMPLETED.equals(intent.getAction())) {
			onReboot(context);
			return;
		}

		Intent startRingingActivityIntent = new Intent(context,
				RingingAlarmActivity.class);

		startRingingActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// startRingingActivityIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
		// | Intent.FLAG_ACTIVITY_CLEAR_TOP
		// | Intent.FLAG_ACTIVITY_SINGLE_TOP);

		long alarmId = intent.getLongExtra(Alarm.INTENT_ID, -1);
		startRingingActivityIntent.putExtra(Alarm.INTENT_ID, alarmId);

		context.startActivity(startRingingActivityIntent);

		// final Intent restartRingingActivityIntent = new Intent(context,
		// RingingAlarmActivity.class);
		// restartRingingActivityIntent
		// .addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
		// restartRingingActivityIntent.putExtra(Alarm.INTENT_ID, alarmId);
		// PendingIntent restartRingingActivityPenInt =
		// PendingIntent.getActivity(
		// context, (int) alarmId, startRingingActivityIntent, 0);
		//
		// Notification.Builder notiBuilder = new Notification.Builder(context)
		// .setTicker(tickerText)
		// .setSmallIcon(R.drawable.ic_action_onoff_pressed)
		// .setOngoing(true)
		// .setContentIntent(restartRingingActivityPenInt)
		// .setContentTitle(contentTitle);
		//
		// NotificationManager notiMng = (NotificationManager) context
		// .getSystemService(Context.NOTIFICATION_SERVICE);
		// notiMng.notify(RINGING_NOTIFICATION_ID1, notiBuilder.build());
	}

	private void onReboot(Context context) {

		// Intent onRebootIntent = new Intent(context, OnRebootActivity.class);
		// onRebootIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
		// context.startActivity(onRebootIntent);

		AlarmsManager alarmsManager = new AlarmsManager(context);

		Log.i("MP3ALARM",
				"OnRebootActivity, recreating " + alarmsManager.getAllAlarms()
						+ " alarms.");

		for (Alarm alarm : alarmsManager.getAllAlarms()) {
			alarmsManager.setAlarm(alarm, true, false);
		}
	}
}
