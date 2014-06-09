package net.mariyana.mp3alarmclock.controller;

import java.io.IOException;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;

public class MusicManager {

	private MediaRecorder mRecorder;
	private MediaPlayer mPlayer;

	public void playStart(String musicFilePath) {
		if (musicFilePath == null) {
			return;
		}
		mPlayer = new MediaPlayer();
		try {
			mPlayer.setDataSource(musicFilePath);
			mPlayer.prepare();
			mPlayer.start();
			mPlayer.setLooping(true);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public void playStop() {
		if (mPlayer != null) {
			mPlayer.stop();
			mPlayer.reset();
			mPlayer.release();
		}

		mPlayer = null;
	}

	public boolean isPlaying() {
		return mPlayer != null;
	}

	public void recordStart() {
		mRecorder = new MediaRecorder();
		mRecorder.setAudioSource(MediaRecorder.AudioSource.DEFAULT);
		mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
		mRecorder.setOutputFile(Environment.getExternalStorageDirectory()
				.getAbsolutePath() + "/mp3alarm.3gp");
		mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
		try {
			mRecorder.prepare();
		} catch (IOException e) {
			e.printStackTrace();
		}

		mRecorder.start();
	}

	public void recordStop() {
		if (mRecorder != null) {
			mRecorder.stop();
			mRecorder.reset();
			mRecorder.release();
		}
		mRecorder = null;
	}

	public boolean isRecording() {
		return mRecorder != null;
	}

	public void close() {
		recordStop();
		playStop();
	}
}
