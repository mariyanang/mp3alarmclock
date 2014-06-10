package net.mariyana.mp3alarmclock.controller;

import java.io.File;
import java.util.Calendar;
import java.util.List;

import net.mariyana.mp3alarmclock.model.Alarm;
import net.mariyana.mp3alarmclock.view.MediaPlayerService;
import net.mariyana.mp3alarmclock.view.PlayAlarmReceiver;
import net.mariyana.mp3alarmclock.view.activity.RingingAlarmActivity;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.widget.Toast;

public class AlarmsManager {

	AlarmsDbHelper alarmsDbHelper;
	Context context;
	AlarmManager alarmManager;
	private MusicManager player;

	public AlarmsManager(Context context) {
		this.context = context;
		this.alarmsDbHelper = new AlarmsDbHelper(context);
		alarmManager = (AlarmManager) context
				.getSystemService(Activity.ALARM_SERVICE);
		player = new MusicManager();
	}

	public Alarm createAlarm(String time, boolean mo, boolean tu, boolean we,
			boolean th, boolean fr, boolean sa, boolean su, String name,
			String musicPath) {

		Alarm alarm = alarmsDbHelper.createAlarm(time, mo, tu, we, th, fr, sa,
				su, name, musicPath);

		setAlarm(alarm, true);

		return alarm;
	}

	/**
	 * Cancels alarm pending intent and deletes the alarm if second parameter is
	 * used.
	 * 
	 * @param alarmId
	 * @param delete
	 *            if true deletes alarm from DB
	 */
	public void cancelAlarm(long alarmId, boolean delete) {

		cancelAlarm(alarmId);
		cancelAlarm(alarmId + 1000);

		if (delete) {
			alarmsDbHelper.deleteAlarm(alarmId);

			Toast.makeText(context, "Alarm deleted", Toast.LENGTH_SHORT).show();
		}
	}

	public void cancelAlarm(long alarmId) {
		Intent playAlarmIntent = new Intent(context, PlayAlarmReceiver.class);
		playAlarmIntent.putExtra(Alarm.INTENT_ID, alarmId);

		PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
				(int) alarmId, playAlarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);
		alarmManager.cancel(alarmIntent);

		NotificationManager notiMng = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);
		notiMng.cancel(MediaPlayerService.RINGING_NOTIFICATION_ID);
		notiMng.cancel(RingingAlarmActivity.SNOOZING_NOTIFICATION_ID);
	}

	public void setAlarmSnooze(long alarmId) {

		Calendar calendar = Calendar.getInstance();
		calendar.setTimeInMillis(calendar.getTimeInMillis() + 5 * 60 * 1000);

		setAlarm(calendar, alarmId);
	}

	public void setAlarm(Calendar calendar, long alarmId) {

		int pendingIntentRequestCode = (int) alarmId;
		if (alarmId > 1000) {
			alarmId = alarmId - 1000;
		}
		// String musicPath =
		// alarmsDbHelper.getAlarmById(alarmId).getMusicPath();

		Intent playAlarmIntent = new Intent(context, PlayAlarmReceiver.class);
		playAlarmIntent.putExtra(Alarm.INTENT_ID, alarmId);
		// playAlarmIntent.putExtra("musicPath", musicPath);

		PendingIntent alarmIntent = PendingIntent.getBroadcast(context,
				pendingIntentRequestCode, playAlarmIntent,
				PendingIntent.FLAG_UPDATE_CURRENT);

		alarmManager.set(android.app.AlarmManager.RTC_WAKEUP,
				calendar.getTimeInMillis(),
				// System.currentTimeMillis() + 10 * 1000,
				alarmIntent);
	}

	public void setAlarm(int wishDay, Calendar calendar, long alarmId) {
		int daysDiff = getDaysDiff(calendar, wishDay);

		calendar.add(Calendar.DAY_OF_YEAR, daysDiff);

		setAlarm(calendar, alarmId);
	}

	private int getDaysDiff(Calendar wishTime, int wishDay) {

		int todayDayOfWeekIndex = getDayOfWeekIndex(wishTime
				.get(Calendar.DAY_OF_WEEK));
		int wishdayDayOfWeekIndex = getDayOfWeekIndex(wishDay);

		if (todayDayOfWeekIndex > wishdayDayOfWeekIndex) {
			return (7 - todayDayOfWeekIndex) + wishdayDayOfWeekIndex;
		}
		if (wishdayDayOfWeekIndex > todayDayOfWeekIndex) {
			return wishdayDayOfWeekIndex - todayDayOfWeekIndex;
		}
		if (wishdayDayOfWeekIndex == todayDayOfWeekIndex
				&& System.currentTimeMillis() > wishTime.getTimeInMillis()) {

			return 7;
		}
		return 0;
	}

	private int getDayOfWeekIndex(int calendarDayOfWeek) {
		switch (calendarDayOfWeek) {
		case Calendar.MONDAY:
			return 0;
		case Calendar.TUESDAY:
			return 1;
		case Calendar.WEDNESDAY:
			return 2;
		case Calendar.THURSDAY:
			return 3;
		case Calendar.FRIDAY:
			return 4;
		case Calendar.SATURDAY:
			return 5;
		case Calendar.SUNDAY:
			return 6;
		}
		return 0;
	}

	public void setAlarm(Alarm alarm, boolean active) {
		setAlarm(alarm, active, true);
	}

	public void setAlarm(Alarm alarm, boolean active, boolean showToast) {

		alarm.setActive(active);
		alarmsDbHelper.updateAlarm(alarm);

		if (!active) {
			cancelAlarm(alarm.getId());
		} else {
			// Delete previous music recording file for this alarm if it is
			// using user selected song
			File tempRecordingFile = new File(Environment
					.getExternalStorageDirectory().getAbsolutePath()
					+ "/mp3alarm_" + alarm.getId() + ".3gp");
			if (!"".equals(alarm.getMusicPath()) && tempRecordingFile.exists()) {
				tempRecordingFile.delete();
			}

			int hour = (Integer.parseInt((alarm.getTime()).substring(0, 2)));
			int minute = Integer.parseInt((alarm.getTime()).substring(3, 5));

			final Calendar calendar = Calendar.getInstance();
			calendar.setTimeInMillis(System.currentTimeMillis());
			calendar.set(Calendar.HOUR_OF_DAY, hour);
			calendar.set(Calendar.MINUTE, minute);
			calendar.set(Calendar.SECOND, 0);

			boolean toastShown = false;
			if (alarm.getMo()) {
				setAlarm(Calendar.MONDAY, calendar, alarm.getId());

				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (alarm.getTu()) {
				setAlarm(Calendar.TUESDAY, calendar, alarm.getId());

				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (alarm.getWe()) {
				setAlarm(Calendar.WEDNESDAY, calendar, alarm.getId());

				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (alarm.getTh()) {
				setAlarm(Calendar.THURSDAY, calendar, alarm.getId());
				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (alarm.getFr()) {
				setAlarm(Calendar.FRIDAY, calendar, alarm.getId());
				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (alarm.getSa()) {
				setAlarm(Calendar.SATURDAY, calendar, alarm.getId());
				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (alarm.getSu()) {
				setAlarm(Calendar.SUNDAY, calendar, alarm.getId());
				if (!toastShown && showToast) {
					showAlarmToast(calendar);
					toastShown = true;
				}
			}
			if (!alarm.getMo() && !alarm.getTu() && !alarm.getWe()
					&& !alarm.getTh() && !alarm.getFr() && !alarm.getSa()
					&& !alarm.getSu()) {

				if (calendar.getTimeInMillis() < System.currentTimeMillis()) {
					calendar.add(Calendar.DAY_OF_YEAR, 1);
				}

				setAlarm(calendar, alarm.getId());

				if (showToast)
					showAlarmToast(calendar);
			}
		}
	}

	private void showAlarmToast(Calendar calendar) {
		long toastTime = calendar.getTimeInMillis()
				- System.currentTimeMillis();
		int toastSec = (int) (toastTime / 1000l);
		int toastDays = toastSec / 3600 / 24;
		int toastHour = (toastSec - toastDays * 24 * 3600) / 3600;
		int toastMin = toastSec / 60 - toastDays * 24 * 60 - toastHour * 60;

		String toastMessage = "";
		if (toastDays > 0) {
			toastMessage += toastDays + " day" + (toastDays > 1 ? "s" : "")
					+ " ";
		}
		if (toastHour > 0) {
			toastMessage += toastHour + " hour" + (toastHour > 1 ? "s" : "")
					+ " ";
		}
		if (toastMin > 0) {
			toastMessage += toastMin + " minute" + (toastMin > 1 ? "s" : "")
					+ " ";
		}

		toastMessage = "Alarm set for " + toastMessage
				+ (toastMessage.length() == 0 ? "less than a minute " : "")
				+ "from now";
		Toast.makeText(context, toastMessage, Toast.LENGTH_LONG).show();
	}

	public void close() {
		alarmsDbHelper.close();
	}

	public void openAlarmsDbHelper() {
		alarmsDbHelper = new AlarmsDbHelper(context);
	}

	public AlarmsDbHelper getAlarmsDbHelper() {
		return alarmsDbHelper;
	}

	public List<Alarm> getAllAlarms() {
		return alarmsDbHelper.getAllAlarms();
	}

	public Alarm getAlarmByPosition(int position) {
		return alarmsDbHelper.getAlarmByPosition(position);
	}

	public Alarm getAlarmById(long alarmId) {
		return alarmsDbHelper.getAlarmById(alarmId);
	}

	public boolean checkForDuplicateAlarm(String timeString) {
		return checkForDuplicateAlarm(timeString, -1);
	}

	public boolean checkForDuplicateAlarm(String timeString, long alarmId) {
		for (Alarm alarm : getAllAlarms()) {
			if (alarm.getTime().equals(timeString) && alarm.getId() != alarmId) {
				Toast.makeText(context,
						"Alarm for " + timeString + " already exists",
						Toast.LENGTH_LONG).show();
				return true;
			}
		}
		return false;
	}

	public void playMusic(long alarmId) {
		final Alarm alarm = getAlarmById(alarmId);
		player.playStart(alarm.getMusicFileName());
	}

	public void stopMusic() {
		if (player != null) {
			player.close();
		}
	}

	public MusicManager getMusicManager() {
		return player;
	}
}
