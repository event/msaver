package org.movshovich.msaver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.SQLException;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

public class BarcodeResolver extends AsyncTask<String, Void, String> {

	private ExpensesFragment expensesFragment;
	private Context context;

	public BarcodeResolver(Context context, ExpensesFragment expensesFragment) {
		this.context = context;
		this.expensesFragment = expensesFragment;
	}

	@Override
	protected String doInBackground(String... params) {
		String barcodeContent = params[0];
		Log.w("MSaver", "barcode is " + barcodeContent);
		DefaultHttpClient http = new DefaultHttpClient();
		HttpGet get = new HttpGet(
				"http://eandata.com/feed.php?keycode=01710910518C093A&comp=no"
						+ "&pending=yes&mode=json&find=" + barcodeContent);
		get.setHeader("User-Agent", "Bond007");
		try {
			HttpResponse resp = http.execute(get);
			return getProductName(resp);
		} catch (Exception e) {
			Log.e("MSaver", "Http operations failed", e);
			return null;
		}
	}

	private String getProductName(HttpResponse resp) throws IOException,
			JSONException, SQLException {
		InputStream content = resp.getEntity().getContent();
		BufferedReader reader = new BufferedReader(new InputStreamReader(
				content, "UTF-8"));
		StringBuilder sb = new StringBuilder(128);
		String line = reader.readLine();
		while (line != null) {
			sb.append(line);
			line = reader.readLine();
		}
		String stuff = sb.toString();
		Log.w("MSaver", stuff);
		JSONObject json = new JSONObject(stuff);
		if (json.getJSONObject("status").getInt("code") != 200) {
			return null;
		}
		return json.getJSONObject("product").getString("product");
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result != null) {
			expensesFragment.setProdText(result);
		} else {
			Toast.makeText(context, "Barcode was not recognized",
					Toast.LENGTH_LONG).show();
		}
	}

}
