package net.mariyana.mp3alarmclock.view.activity;

import net.mariyana.mp3alarmclock.R;
import net.mariyana.mp3alarmclock.controller.AlarmsManager;
import net.mariyana.mp3alarmclock.view.AlarmsListArrayAdapter;
import net.mariyana.mp3alarmclock.view.fragment.AddAlarmFragment;
import net.mariyana.mp3alarmclock.view.fragment.AlarmsListFragment;
import net.mariyana.mp3alarmclock.view.fragment.EditAlarmFragment;
import android.app.Activity;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListAdapter;

public class MainActivity extends Activity implements
		AlarmsListFragment.OnAlarmSelectedListener {

	public static Intent newAlarmsListFragment(Activity activity) {
		Intent intent = new Intent();
		intent.setClass(activity, AlarmsListFragment.class);
		return intent;
	}

	private AlarmsListArrayAdapter alarmAdapter;
	private AlarmsManager alarmsManager;
	private AlarmsListFragment alarmsListFragment;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.fragment_main);

		initializeAlarmsManager();
		refreshListAdapter();

		if (savedInstanceState == null) {
			if (alarmsListFragment == null) {

				alarmsListFragment = new AlarmsListFragment();

				refreshListAdapter();

				FragmentManager fm = getFragmentManager();
				FragmentTransaction ft = fm.beginTransaction();
				ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
				ft.add(R.id.fragment_container, alarmsListFragment,
						"alarms_list");
				ft.commit();
				fm.executePendingTransactions();

			}
			//
			// if (getIntent().getAction() != null
			// && getIntent().getAction().equals("ringing alarm")) {
			// RingingAlarmFragment ringingAlarmFragment = new
			// RingingAlarmFragment();
			//
			// FragmentManager fm = getFragmentManager();
			// FragmentTransaction ft = fm.beginTransaction();
			//
			// Bundle b = new Bundle();
			// b.putLong(Alarm.INTENT_ID,
			// getIntent().getLongExtra(Alarm.INTENT_ID, -1));
			// ringingAlarmFragment.setArguments(b);
			//
			// ft.replace(R.id.fragment_container, ringingAlarmFragment);
			// ft.commit();
			// fm.executePendingTransactions();
			// }
		}

	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {

		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.main_activity_actions, menu);
		return super.onCreateOptionsMenu(menu);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()) {
		case R.id.action_about:
			Intent intent = new Intent(this, AboutActivity.class);
			startActivity(intent);
		}
		return super.onOptionsItemSelected(item);
	}

	@Override
	protected void onResume() {
		super.onResume();
		initializeAlarmsManager();
		getAlarmsManager().openAlarmsDbHelper();
		refreshListAdapter();
	}

	@Override
	protected void onStop() {
		super.onStop();
		getAlarmsManager().close();
	}

	public AlarmsManager getAlarmsManager() {
		return alarmsManager;
	}

	private void initializeAlarmsManager() {
		if (alarmsManager == null)
			alarmsManager = new AlarmsManager(this);
	}

	public void openAddAlarmFragment() {

		AddAlarmFragment addAlarmFragment = new AddAlarmFragment();

		FragmentManager fm = getFragmentManager();
		FragmentTransaction ft = fm.beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.replace(R.id.fragment_container, addAlarmFragment,
				"addAlarmFragment").addToBackStack("addAlarmFragment").commit();
		getFragmentManager().executePendingTransactions();
	}

	@Override
	public void openEditAlarmFragment(int position) {

		EditAlarmFragment editAlarmFragment = new EditAlarmFragment();

		Bundle b = new Bundle();
		b.putInt("alarmPosition", position);

		editAlarmFragment.setArguments(b);

		FragmentTransaction ft = getFragmentManager().beginTransaction();
		ft.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_OPEN);
		ft.replace(R.id.fragment_container, editAlarmFragment)
				.addToBackStack(null).commit();
		getFragmentManager().executePendingTransactions();
	}

	public void refreshListAdapter() {
		if (alarmAdapter == null) {
			alarmAdapter = new AlarmsListArrayAdapter(this,
					R.layout.alarm_listview_row, alarmsManager.getAllAlarms(),
					this);
		}
		alarmAdapter.clear();
		alarmAdapter.addAll(getAlarmsManager().getAllAlarms());
		alarmAdapter.notifyDataSetChanged();

	}

	// public void onDeleteButtonClickListener(View view) {
	// Long alarmId = (Long) view.getTag();
	// alarmsManager.cancelAlarm(alarmId, true);
	// refreshListAdapter();
	// alarmAdapter.notifyDataSetChanged();
	// alarmsListFragment.getListView().invalidateViews();
	// }

	public ListAdapter getListAdaptor() {
		return alarmAdapter;
	}
}
