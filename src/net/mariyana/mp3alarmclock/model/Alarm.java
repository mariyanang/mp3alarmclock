package net.mariyana.mp3alarmclock.model;

import java.io.File;

import android.os.Environment;
import android.provider.BaseColumns;

/**
 * This class contains the data that will be saved in the database and shown in
 * the user interface. POJO - plain java object
 */
public class Alarm implements BaseColumns {

	public static String INTENT_ID = "alarmId";

	private long id;
	private String time;
	private boolean mo;
	private boolean tu;
	private boolean we;
	private boolean th;
	private boolean fr;
	private boolean sa;
	private boolean su;
	private String name;
	private String musicPath;
	private boolean active;

	public Alarm() {

	}

	public Alarm(long id, String time, boolean mo, boolean tu, boolean we,
			boolean th, boolean fr, boolean sa, boolean su, String name,
			String musicPath, boolean active) {
		super();
		this.id = id;
		this.time = time;
		this.mo = mo;
		this.tu = tu;
		this.we = we;
		this.th = th;
		this.fr = fr;
		this.sa = sa;
		this.su = su;
		this.name = name;
		this.musicPath = musicPath;
		this.active = active;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getTime() {
		return time;
	}

	public void setTime(String time) {
		this.time = time;
	}

	// public String getActivationState() {
	// return onoff;
	// }

	// public void setActivationState(String onoff) {
	// this.onoff = onoff;
	// }

	public boolean getMo() {
		return mo;
	}

	public void setMo(boolean mo) {
		this.mo = mo;
	}

	public boolean getTu() {
		return tu;
	}

	public void setTu(boolean tu) {
		this.tu = tu;
	}

	public boolean getWe() {
		return we;
	}

	public void setWe(boolean we) {
		this.we = we;
	}

	public boolean getTh() {
		return th;
	}

	public void setTh(boolean th) {
		this.th = th;
	}

	public boolean getFr() {
		return fr;
	}

	public void setFr(boolean fr) {
		this.fr = fr;
	}

	public boolean getSa() {
		return sa;
	}

	public void setSa(boolean sa) {
		this.sa = sa;
	}

	public boolean getSu() {
		return su;
	}

	public void setSu(boolean su) {
		this.su = su;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getMusicPath() {
		return musicPath;
	}

	public void setMusicPath(String musicPath) {
		this.musicPath = musicPath;
	}

	public boolean getActive() {

		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	@Override
	public String toString() {
		return " id " + id + " time: " + time + " mo: " + mo + " tu: " + tu
				+ " we: " + we + " th: " + th + " fr: " + fr + " sa: " + sa
				+ " su: " + su + " name: " + name + " music_path: " + musicPath
				+ " active: " + active;
	}

	public String getMusicFileName() {

		if (getMusicPath() != null && getMusicPath().length() > 0) {
			return getMusicPath();
		}

		String recordingFileName = Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/mp3alarm_" + getId() + ".3gp";
		if (new File(recordingFileName).exists()) {
			return recordingFileName;
		}

		return null;
	}
}
