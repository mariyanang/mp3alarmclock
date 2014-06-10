package net.mariyana.mp3alarmclock.view;

import java.util.List;

import net.mariyana.mp3alarmclock.R;
import net.mariyana.mp3alarmclock.model.Alarm;
import net.mariyana.mp3alarmclock.view.activity.MainActivity;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class AlarmsListArrayAdapter extends ArrayAdapter<Alarm> {

	private MainActivity mainActivity;

	public AlarmsListArrayAdapter(Context context, int resource,
			List<Alarm> alarms, MainActivity mainActivity) {
		super(context, resource, alarms);
		this.mainActivity = mainActivity;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		RelativeLayout template = null;

		template = (RelativeLayout) inflater.inflate(
				R.layout.alarm_listview_row, template, true);

		final Alarm alarm = getItem(position);

		TextView alarmTime = (TextView) template.findViewById(R.id.alarm_time);
		alarmTime.setText(alarm.getTime());

		final CheckBox mo = (CheckBox) template.findViewById(R.id.monday);
		mo.setChecked(alarm.getMo());
		mo.setEnabled(false);
		final CheckBox tu = (CheckBox) template.findViewById(R.id.tuesday);
		tu.setChecked(alarm.getTu());
		tu.setEnabled(false);
		final CheckBox we = (CheckBox) template.findViewById(R.id.wednesday);
		we.setChecked(alarm.getWe());
		we.setEnabled(false);
		final CheckBox th = (CheckBox) template.findViewById(R.id.thursday);
		th.setChecked(alarm.getTh());
		th.setEnabled(false);
		final CheckBox fr = (CheckBox) template.findViewById(R.id.friday);
		fr.setChecked(alarm.getFr());
		fr.setEnabled(false);
		final CheckBox sa = (CheckBox) template.findViewById(R.id.saturday);
		sa.setChecked(alarm.getSa());
		sa.setEnabled(false);
		final CheckBox su = (CheckBox) template.findViewById(R.id.sunday);
		su.setChecked(alarm.getSu());
		su.setEnabled(false);

		TextView alarmName = (TextView) template.findViewById(R.id.alarm_name);
		alarmName.setText(alarm.getName());

		TextView musicPath = (TextView) template.findViewById(R.id.alarm_music);
		if (alarm.getMusicPath().length() == 0) {
			musicPath.setText("recording " + alarm.getId());
		} else {
			musicPath.setText(Uri.parse(alarm.getMusicPath())
					.getLastPathSegment());
		}

		CheckBox onOffAlarmCheckbox = (CheckBox) template
				.findViewById(R.id.btn_on_off);
		onOffAlarmCheckbox.setChecked(alarm.getActive());
		onOffAlarmCheckbox
				.setOnCheckedChangeListener(new OnCheckedChangeListener() {

					@Override
					public void onCheckedChanged(CompoundButton buttonView,
							boolean isChecked) {

						mainActivity.getAlarmsManager().setAlarm(alarm,
								isChecked);
					}
				});

		Button deleteAlarmBtn = (Button) template
				.findViewById(R.id.btn_delete_alarm);
		deleteAlarmBtn.setTag(position);
		deleteAlarmBtn.setOnClickListener(new OnClickListener() {

			@Override
			public void onClick(View v) {

				mainActivity.getAlarmsManager()
						.cancelAlarm(alarm.getId(), true);
				mainActivity.refreshListAdapter();

			}
		});
		return template;

	}
}
