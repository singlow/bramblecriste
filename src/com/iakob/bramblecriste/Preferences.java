package com.iakob.bramblecriste;

import com.iakob.bramblecriste.R;

import android.os.Bundle;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
    }
    
}
