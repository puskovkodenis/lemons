package com.lemons.fruit.service;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.util.JsonReader;
import android.util.Log;
import android.util.SparseArray;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;

public class GameSetupService implements Handler.Callback {
	final private Handler mHandler;
	final private int MSG_FETCH_HIGH_SCORES = 1;
	private int nextRequestId = 0;
	private SparseArray<HighScoreCallback> mRequestMap = new SparseArray<>();

	public GameSetupService() {
		HandlerThread handlerThread = new HandlerThread("Game Setup Service");
		handlerThread.start();
		mHandler = new Handler(handlerThread.getLooper(), this);
	}

	public interface HighScoreCallback {
		void onHighScore(int highScore);
	}


	public void fetchHighScore(HighScoreCallback callback) {
		final int requestId = nextRequestId++;
		mRequestMap.put(requestId, callback);
		Message message = mHandler.obtainMessage(MSG_FETCH_HIGH_SCORES, requestId, 0);
		mHandler.sendMessage(message);
	}


	private void dispatchHighScoreResult(final int requestId, final int highScore) {
		Handler uiHandler = new Handler(Looper.getMainLooper());
		uiHandler.post(new Runnable() {
			@Override
			public void run() {
				HighScoreCallback callback = mRequestMap.get(requestId);
				if (callback != null) {
					callback.onHighScore(highScore);
				}
				mRequestMap.remove(requestId);
			}
		});
	}


	private int downloadHighScore() {
		String myurl = "";
		InputStream is = null;

		try {
			URL url = new URL(myurl);
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setReadTimeout(10000 /* milliseconds */);
			conn.setConnectTimeout(15000 /* milliseconds */);
			conn.setRequestMethod("GET");
			conn.setDoInput(true);
			// Starts the query
			conn.connect();
			int response = conn.getResponseCode();
			Log.d("GameSetupService", "The response is: " + response);
			is = conn.getInputStream();

			// Convert the InputStream into a string
			return readIt(is);

			// Makes sure that the InputStream is closed after the app is
			// finished using it.
		} catch(Throwable t) {
			return 100;
		} finally {
			if (is != null) {
				try {
					is.close();
				} catch (Throwable t) {
					Log.e("GameSetupService", "Error cleaning up in finally");
				}
			}
		}

	}

	private int readIt(InputStream stream) throws IOException {
		Reader reader = new InputStreamReader(stream, "UTF-8");
		JsonReader jsonReader = new JsonReader(reader);
		jsonReader.beginObject();
		int result = 100;
		while (jsonReader.hasNext()) {
			String name = jsonReader.nextName();
			if (name.equals("high_score")) {
				result = jsonReader.nextInt();
			}
		}
		jsonReader.close();
		return result;

	}


	@Override
	public boolean handleMessage(Message message) {
		switch (message.what) {
			case MSG_FETCH_HIGH_SCORES:
				final int highScore = downloadHighScore();
				dispatchHighScoreResult(message.arg1, highScore);
				return true;
		}
		return false;
	}
}
