package com.example.bramblecriste;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.app.Activity;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

public class Settings extends Activity implements OnItemSelectedListener {
	
	Spinner player1spinner;
	Spinner player2spinner;
	SharedPreferences prefs;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        player1spinner = (Spinner) findViewById(R.id.player1minutes);
        player2spinner = (Spinner) findViewById(R.id.player2minutes);
        Integer[] minuteArray = new Integer[60];
        for (int i = 0; i < 60; i++) {
        	minuteArray[i] = i+1;
        }
        ArrayAdapter<Integer> adapter = new ArrayAdapter<Integer>(this, android.R.layout.simple_spinner_dropdown_item, minuteArray);
        player1spinner.setAdapter(adapter);
        player2spinner.setAdapter(adapter);
        

    	prefs = this.getSharedPreferences(Timer.PREFERENCES, Activity.MODE_PRIVATE);
		player1spinner.setSelection((int) prefs.getLong(Timer.PLAYER_1_INITIAL, Timer.INITIAL_DEFAULT)/(60*1000)-1);
		player2spinner.setSelection((int) prefs.getLong(Timer.PLAYER_2_INITIAL, Timer.INITIAL_DEFAULT)/(60*1000)-1);

		player1spinner.setOnItemSelectedListener(this);
		player2spinner.setOnItemSelectedListener(this);
	
    }

	@Override
	public void onItemSelected(AdapterView<?> parent, View view, int position,
			long id) {
		Editor editor = prefs.edit();
		if (view.getParent().equals(player1spinner)) {
			editor.putLong(Timer.PLAYER_1_INITIAL, (parent.getItemIdAtPosition(position)+1)*60*1000);
		}
		if (view.getParent().equals(player2spinner)) {
			editor.putLong(Timer.PLAYER_2_INITIAL, (parent.getItemIdAtPosition(position)+1)*60*1000);
		}
		editor.commit();
	}

	@Override
	public void onNothingSelected(AdapterView<?> parent) {

		
	}


}