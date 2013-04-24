package org.movshovich.msaver;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

public class SyncMacher {

	private static final String REQ_PATH = "/sync.json";
	
	private String ipAddr;
	private Context context;

	public SyncMacher(String ipAddr, Context context) {
		this.ipAddr = ipAddr;
		this.context = context;
	}

	public void doSync() {
		if (ipAddr == null || ipAddr.isEmpty()) {
			Toast.makeText(context, "Please fill in IP address of server"
					, Toast.LENGTH_SHORT).show();
		}
		JSONObject json = new JSONObject();
		DefaultHttpClient http = new DefaultHttpClient();
		HttpPost post = new HttpPost(ipAddr + REQ_PATH);
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
			return;
		} 
	}

	private void processResponse(HttpResponse resp) {
		// TODO Auto-generated method stub
		
	}

}
