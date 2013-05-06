package org.movshovich.msaver;

import java.util.HashMap;
import java.util.Map;

import org.xmlrpc.android.XMLRPCClient;
import org.xmlrpc.android.XMLRPCException;

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
		XMLRPCClient client = new XMLRPCClient("http://www.upcdatabase.com/xmlrpc");
		String text = null;
		try {
			Map<String, String> xmlParams = new HashMap<String, String>();
			xmlParams.put("rpc_key", MainActivity.UPCDB_KEY);
			xmlParams.put("upc", barcodeContent);
			HashMap result = (HashMap) client.call("lookup", xmlParams);
			Log.w("MSaver", result.toString());
			String status = result.get("status").toString();
			if (status != "fail") {
				text = result.get("description").toString();
			}
		} catch (NullPointerException nl) {
			Log.w("MSaver", nl);
		} catch (XMLRPCException e) {
			Log.w("MSaver", e);
		}
		return text;
	}

	@Override
	protected void onPostExecute(String result) {
		super.onPostExecute(result);
		if (result != null) {
			expensesFragment.setProdText(result);
		} else {
			Toast.makeText(context, "Barcode was not recognized", Toast.LENGTH_LONG).show();
		}			
	}
	
}
