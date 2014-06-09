package net.mariyana.mp3alarmclock.view.fragment;

import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;

public class AddAlarmFragment extends AlarmFragment {

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);
	}

	@Override
	protected OnClickListener getCreateAlarmButtonClickListener() {
		return new OnClickListener() {

			@Override
			public void onClick(View v) {

				timePicker.clearFocus();
				int hour = timePicker.getCurrentHour();
				int minute = timePicker.getCurrentMinute();
				String timeString = "";

				if (hour <= 9)
					timeString = "0";
				timeString += hour + ":";
				if (minute <= 9)
					timeString += "0";
				timeString += minute;

				String nameString = alarmNameET.getText().toString();

				String musicString = "";
				if (tempMusicFilePath != null) {
					musicString = tempMusicFilePath;
				} else if (tempRecording) {
					musicString = "";
				}

				if (!getMainActivity().getAlarmsManager()
						.checkForDuplicateAlarm(timeString)) {

					getMainActivity().getAlarmsManager().createAlarm(
							timeString, mo.isChecked(), tu.isChecked(),
							we.isChecked(), th.isChecked(), fr.isChecked(),
							sa.isChecked(), su.isChecked(), nameString,
							musicString);

					AddAlarmFragment.this.refreshAndClose();
				}
			}
		};
	}
}
