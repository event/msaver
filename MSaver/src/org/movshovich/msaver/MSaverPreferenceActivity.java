package org.movshovich.msaver;

import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.util.Log;

public class MSaverPreferenceActivity extends PreferenceActivity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preference);
	}

}
