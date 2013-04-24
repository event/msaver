package org.movshovich.msaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class SyncMacher extends AsyncTask<Void, Void, Long> {

	private String ipAddr;
	private Context context;

	public SyncMacher(String ipAddr, Context context) {
		this.ipAddr = ipAddr;
		this.context = context;
	}

	private boolean doSync() {
		if (ipAddr == null || ipAddr.isEmpty()) {
			Toast.makeText(context, "Please fill in IP address of server"
					, Toast.LENGTH_SHORT).show();
		}
		JSONObject json = new JSONObject();
		DefaultHttpClient http = new DefaultHttpClient();
		HttpPost post = new HttpPost(ipAddr);
		post.setHeader("Content-type", "application/json");
		try {
			json.put("username", "mama");
			json.put("password", "mama");
			StringEntity se = new StringEntity(json.toString());
			post.setEntity(se);
			HttpResponse resp = http.execute(post);
			processResponse(resp);
		} catch (Exception e) {
			Toast.makeText(context, "Sync failed"
					, Toast.LENGTH_SHORT).show();
			Log.w("MSaver", e);
			return false;
		} 
		return true;
	}

	private void processResponse(HttpResponse resp) throws IOException, JSONException {
		InputStream content = resp.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(content, "UTF-8"));
		StringBuilder sb = new StringBuilder(128);
		String line = reader.readLine();
		while (line != null) {
			sb.append(line);
			line = reader.readLine();
		}
		JSONObject json = new JSONObject(sb.toString());
		Log.w("MSaver", json.toString(2));
	}

	@Override
	protected Long doInBackground(Void... params) {
		if (doSync()) {
			return 1L;
		} else {
			return 0L;
		}
	}

	@Override
	protected void onPostExecute(Long result) {
		super.onPostExecute(result);
		Toast.makeText(context, "Sync success!"
				, Toast.LENGTH_SHORT).show();
	}

	
}
