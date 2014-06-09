package net.mariyana.mp3alarmclock.view.fragment;

import java.io.File;

import net.mariyana.mp3alarmclock.model.Alarm;
import android.os.Bundle;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

public class EditAlarmFragment extends AlarmFragment {

	private Alarm alarmForEdit;
	private File tempRecordingFile;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setRetainInstance(true);

		tempRecordingFile = new File(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/mp3alarm.3gp");
		if (tempRecordingFile.exists())
			tempRecordingFile.delete();
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {

		View rootView = super.onCreateView(inflater, container,
				savedInstanceState);
		updateAlarmValues(getArguments().getInt("alarmPosition"));

		return rootView;
	}

	private void updateAlarmValues(int position) {

		alarmForEdit = getMainActivity().getAlarmsManager().getAlarmByPosition(
				position);

		int hour = (Integer.parseInt((alarmForEdit.getTime()).substring(0, 2)));
		int minute = Integer.parseInt((alarmForEdit.getTime()).substring(3, 5));

		timePicker.setCurrentHour(hour);
		timePicker.setCurrentMinute(minute);

		mo.setChecked(alarmForEdit.getMo());
		tu.setChecked(alarmForEdit.getTu());
		we.setChecked(alarmForEdit.getWe());
		th.setChecked(alarmForEdit.getTh());
		fr.setChecked(alarmForEdit.getFr());
		sa.setChecked(alarmForEdit.getSa());
		su.setChecked(alarmForEdit.getSu());

		alarmNameET.setText(alarmForEdit.getName());
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

				alarmForEdit.setTime(timeString);
				alarmForEdit.setMo(mo.isChecked());
				alarmForEdit.setTu(tu.isChecked());
				alarmForEdit.setWe(we.isChecked());
				alarmForEdit.setTh(th.isChecked());
				alarmForEdit.setFr(fr.isChecked());
				alarmForEdit.setSa(sa.isChecked());
				alarmForEdit.setSu(su.isChecked());
				alarmForEdit.setName(nameString);
				if (tempMusicFilePath != null) {
					alarmForEdit.setMusicPath(tempMusicFilePath);
				} else if (tempRecording) {
					alarmForEdit.setMusicPath("");
				}
				alarmForEdit.setActive(true);

				if (!getMainActivity().getAlarmsManager()
						.checkForDuplicateAlarm(timeString,
								alarmForEdit.getId())) {

					getMainActivity().getAlarmsManager().setAlarm(alarmForEdit,
							alarmForEdit.getActive());

					EditAlarmFragment.this.refreshAndClose();
				}
			}
		};
	}
}
