package net.mariyana.mp3alarmclock.controller;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import net.mariyana.mp3alarmclock.model.Alarm;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

/**
 * This class is responsible for creating the database.
 */

public class AlarmsDbHelper extends SQLiteOpenHelper {

	Context context;

	public static final String DATABASE_NAME = "mp3alarm.db";
	public static final int DATABASE_VERSION = 2;
	public static final String TABLE_NAME = "alarms";

	public static final String FIELD_TIME = "time";
	public static final String FIELD_MO = "mo";
	public static final String FIELD_TU = "tu";
	public static final String FIELD_WE = "we";
	public static final String FIELD_TH = "th";
	public static final String FIELD_FR = "fr";
	public static final String FIELD_SA = "sa";
	public static final String FIELD_SU = "su";
	public static final String FIELD_NAME = "name";
	public static final String FIELD_MUSIC_PATH = "music_path";
	public static final String FIELD_ACTIVATION_STATE = "activation_state";

	private static final String TEXT_TYPE = " TEXT";

	private static final String SQL_CREATE_TABLE = "CREATE TABLE " + TABLE_NAME
			+ " (" + Alarm._ID + " INTEGER PRIMARY KEY," + FIELD_TIME
			+ TEXT_TYPE + ", " + FIELD_MO + TEXT_TYPE + ", " + FIELD_TU
			+ TEXT_TYPE + ", " + FIELD_WE + TEXT_TYPE + ", " + FIELD_TH
			+ TEXT_TYPE + ", " + FIELD_FR + TEXT_TYPE + ", " + FIELD_SA
			+ TEXT_TYPE + ", " + FIELD_SU + TEXT_TYPE + ", " + FIELD_NAME
			+ TEXT_TYPE + ", " + FIELD_MUSIC_PATH + TEXT_TYPE + ", "
			+ FIELD_ACTIVATION_STATE + TEXT_TYPE + " )";

	private static final String SQL_DELETE_ENTRIES = "DROP TABLE IF EXISTS "
			+ TABLE_NAME;

	private SQLiteDatabase database;

	private String[] projection = { Alarm._ID, FIELD_TIME, FIELD_MO, FIELD_TU,
			FIELD_WE, FIELD_TH, FIELD_FR, FIELD_SA, FIELD_SU, FIELD_NAME,
			FIELD_MUSIC_PATH, FIELD_ACTIVATION_STATE };

	public AlarmsDbHelper(Context context) {
		super(context, DATABASE_NAME, null, DATABASE_VERSION);
		database = this.getWritableDatabase();
	}

	@Override
	public void onCreate(SQLiteDatabase db) {
		db.execSQL(SQL_CREATE_TABLE);
	}

	@Override
	public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		db.execSQL(SQL_DELETE_ENTRIES);
		onCreate(db);
	}

	@Override
	public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
		onUpgrade(db, oldVersion, newVersion);
	}

	public Alarm createAlarm(String time, boolean mo, boolean tu, boolean we,
			boolean th, boolean fr, boolean sa, boolean su, String name,
			String musicPath) {

		ContentValues values = new ContentValues();
		values.put(FIELD_TIME, time);
		values.put(FIELD_MO, mo);
		values.put(FIELD_TU, tu);
		values.put(FIELD_WE, we);
		values.put(FIELD_TH, th);
		values.put(FIELD_FR, fr);
		values.put(FIELD_SA, sa);
		values.put(FIELD_SU, su);
		values.put(FIELD_NAME, name);
		values.put(FIELD_MUSIC_PATH, musicPath);
		values.put(FIELD_ACTIVATION_STATE, true);

		long insertRowId = database.insert(TABLE_NAME, null, values);

		File sdcard = Environment.getExternalStorageDirectory();
		File from = new File(sdcard, "mp3alarm.3gp");
		if (from.exists()) {
			File to = new File(sdcard, "mp3alarm_" + insertRowId + ".3gp");
			from.renameTo(to);
		}

		return new Alarm(insertRowId, time, mo, tu, we, th, fr, sa, su, name,
				musicPath, true);
	}

	public void updateAlarm(Alarm alarm) {
		updateAlarm(alarm.getId(), alarm.getTime(), alarm.getMo(),
				alarm.getTu(), alarm.getWe(), alarm.getTh(), alarm.getFr(),
				alarm.getSa(), alarm.getSu(), alarm.getName(),
				alarm.getMusicPath(), alarm.getActive());
	}

	public void updateAlarm(long alarmId, String time, boolean mo, boolean tu,
			boolean we, boolean th, boolean fr, boolean sa, boolean su,
			String name, String musicPath, boolean active) {

		ContentValues values = new ContentValues();
		values.put(FIELD_TIME, time);
		values.put(FIELD_MO, mo);
		values.put(FIELD_TU, tu);
		values.put(FIELD_WE, we);
		values.put(FIELD_TH, th);
		values.put(FIELD_FR, fr);
		values.put(FIELD_SA, sa);
		values.put(FIELD_SU, su);
		values.put(FIELD_NAME, name);
		values.put(FIELD_MUSIC_PATH, musicPath);
		values.put(FIELD_ACTIVATION_STATE, active);

		database.update(TABLE_NAME, values, Alarm._ID + "=" + alarmId, null);

		File sdcard = Environment.getExternalStorageDirectory();
		File from = new File(sdcard, "mp3alarm.3gp");
		if (from.exists()) {
			File to = new File(sdcard, "mp3alarm_" + alarmId + ".3gp");
			from.renameTo(to);
		}
	}

	public void deleteAlarm(Alarm alarm) {
		deleteAlarm(alarm.getId());
	}

	public void deleteAlarm(long id) {

		database.delete(TABLE_NAME, Alarm._ID + "=" + id, null);

		File sdcard = Environment.getExternalStorageDirectory();
		File to = new File(sdcard, "mp3alarm_" + id + ".3gp");
		to.delete();
	}

	public List<Alarm> getAllAlarms() {

		List<Alarm> alarms = new ArrayList<Alarm>();
		Cursor cursor = database.query(TABLE_NAME, projection, null, null,
				null, null, null);

		cursor.moveToFirst();
		while (!cursor.isAfterLast()) {
			Alarm alarm = cursorToAlarm(cursor);
			alarms.add(alarm);
			cursor.moveToNext();
		}
		cursor.close();

		return alarms;
	}

	public Cursor getAllAlarmsCursor() {
		return database.query(TABLE_NAME, projection, null, null, null, null,
				null);
	}

	public Alarm getAlarmByPosition(int position) {
		List<Alarm> alarms = getAllAlarms();
		if (position <= alarms.size())
			return alarms.get(position);

		return null;
	}

	public Alarm getAlarmById(long alarmId) {

		for (Alarm alarm : getAllAlarms()) {
			long id = alarm.getId();
			if (id == alarmId) {
				return alarm;
			}
		}

		return null;
	}

	private Alarm cursorToAlarm(Cursor cursor) {
		Alarm alarm = new Alarm();
		alarm.setId(Long.parseLong(cursor.getString(0)));
		alarm.setTime(cursor.getString(1));
		alarm.setMo(cursor.getString(2).equals("1"));
		alarm.setTu(cursor.getString(3).equals("1"));
		alarm.setWe(cursor.getString(4).equals("1"));
		alarm.setTh(cursor.getString(5).equals("1"));
		alarm.setFr(cursor.getString(6).equals("1"));
		alarm.setSa(cursor.getString(7).equals("1"));
		alarm.setSu(cursor.getString(8).equals("1"));
		alarm.setName(cursor.getString(9));
		alarm.setMusicPath(cursor.getString(10));
		alarm.setActive(cursor.getString(11).equals("1"));
		return alarm;
	}

}
