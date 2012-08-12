package com.iakob.bramblecriste;

import java.lang.reflect.Field;

import com.iakob.bramblecriste.R;

import android.content.res.Resources.Theme;
import android.os.Bundle;
import android.preference.ListPreference;
import android.preference.PreferenceActivity;

public class Preferences extends PreferenceActivity {

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.preferences);
		try {
			ListPreference themeSelector = (ListPreference) this
					.findPreference(Timer.COLOR_THEME);
			Field[] themes = R.style.class.getFields();
			String[] entries = new String[themes.length];
			String[] values = new String[themes.length];
			for (int i = 0; i < themes.length; i++) {
				entries[i] = themes[i].getName().replace('_', ' ').replace("Bramblecriste ", "");
				values[i] = Integer.toString(themes[i].getInt(null));
			}
			themeSelector.setEntries(entries);
			themeSelector.setEntryValues(values);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
	}
}
