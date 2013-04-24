package org.movshovich.msaver;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class MSaverPreferenceActivity extends PreferenceActivity {

	public static final String KEY_PREF_SERVER_ADDR = "server_addr";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}

}
