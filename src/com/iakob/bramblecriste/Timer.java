package com.iakob.bramblecriste;

import java.text.SimpleDateFormat;
import java.util.Date;

import com.iakob.bramblecriste.R;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;

public class Timer extends Activity {
	
	private Clock clock;
	private ClockConfig config;
	private Button player1Button;
	private Button player2Button;
	private Button pauseButton;
	private Handler handler;

	private SharedPreferences prefs;
	
	public static String INITIAL_DEFAULT = "1500000";
	public static String INCREMENT = "turn_increment";
	public static String INCREMENT_DEFAULT = "0";
    
	public static String PLAYER_1_INITIAL = "player_1_initial";
	public static String PLAYER_2_INITIAL = "player_2_initial";
    public static String PLAYER_1_REMAINING = "player_1_remaining";
    public static String PLAYER_2_REMAINING = "player_2_remaining";
    
    public static String VIBRATE_ON_PRESS = "vibrate_on_press";
    private Vibrator vibe;
    private boolean vibrateOnPress = false;
    private int pauseCount = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    
    	prefs = PreferenceManager.getDefaultSharedPreferences(this);
		config = new ClockConfig(
				Long.parseLong(prefs.getString(PLAYER_1_INITIAL, INITIAL_DEFAULT)),
				Long.parseLong(prefs.getString(PLAYER_2_INITIAL, INITIAL_DEFAULT)),
				Long.parseLong(prefs.getString(INCREMENT, INCREMENT_DEFAULT)));
		clock = new Clock(config);
		
		vibrateOnPress = prefs.getBoolean(VIBRATE_ON_PRESS, false);

    	if (savedInstanceState != null) {
    		clock.setPlayer1Remaining(savedInstanceState.getLong(PLAYER_1_REMAINING));
    		clock.setPlayer2Remaining(savedInstanceState.getLong(PLAYER_2_REMAINING));
    	}
        
        setContentView(R.layout.activity_timer);

        player1Button = (Button) findViewById(R.id.playerabutton);
        player2Button = (Button) findViewById(R.id.playerbbutton);
        pauseButton = (Button) findViewById(R.id.pausebutton);

        player1Button.setText(formatTime(clock.getPlayer1Remaining()));
        player2Button.setText(formatTime(clock.getPlayer2Remaining()));
        
        player1Button.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					pauseCount = 0;
					if (vibrateOnPress) vibe.vibrate(50);
					clock.startPlayer2();
					player2Button.setEnabled(true);
					player2Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_active));
					player1Button.setEnabled(false);
					player1Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
				}
				return true;
			}
		});
        player2Button.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					pauseCount = 0;
					if (vibrateOnPress) vibe.vibrate(50);
					clock.startPlayer1();
					player2Button.setEnabled(false);
					player2Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
					player1Button.setEnabled(true);
					player1Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_active));
				}
				return true;	
			}
		});
        pauseButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					if (vibrateOnPress) vibe.vibrate(50);
					clock.pause();
					player2Button.setEnabled(true);
					player2Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
					player1Button.setEnabled(true);
					player1Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
					
					pauseCount++;
					if (pauseCount >= 5) {
						clock.reset();
						pauseCount = 0;
					}
				}
				
				return true;
			}
		});
        
        handler = new Handler();
        handler.postDelayed(updateTime, 10);
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	clock.pause();
    	handler.removeCallbacks(updateTime);
    	Editor editor = prefs.edit();
       	editor.putLong(PLAYER_1_REMAINING, clock.getPlayer1Remaining());
       	editor.putLong(PLAYER_2_REMAINING, clock.getPlayer2Remaining());
       	editor.commit();
    }
    
    @Override
    public void onResume() {
		super.onResume();
		config.initialTimePlayer1 = Long.parseLong(prefs.getString(PLAYER_1_INITIAL, INITIAL_DEFAULT));
		config.initialTimePlayer2 = Long.parseLong(prefs.getString(PLAYER_2_INITIAL, INITIAL_DEFAULT));
		clock.setPlayer1Remaining(prefs.getLong(PLAYER_1_REMAINING, config.initialTimePlayer1));
		clock.setPlayer2Remaining(prefs.getLong(PLAYER_2_REMAINING, config.initialTimePlayer2));
		handler.postDelayed(updateTime, 10);
		player1Button.setEnabled(true);
		player2Button.setEnabled(true);
		vibrateOnPress = prefs.getBoolean(VIBRATE_ON_PRESS, false);
    }
    

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
    	super.onSaveInstanceState(savedInstanceState);
    	savedInstanceState.putLong(PLAYER_1_REMAINING, clock.getPlayer1Remaining());
    	savedInstanceState.putLong(PLAYER_2_REMAINING, clock.getPlayer2Remaining());
    }
    
    @Override
    public void onRestoreInstanceState(Bundle savedInstanceState) {
    	super.onRestoreInstanceState(savedInstanceState);
    	if (savedInstanceState != null) {
    		clock.setPlayer1Remaining(savedInstanceState.getLong(PLAYER_1_REMAINING, 25*60*1000));
    		clock.setPlayer2Remaining(savedInstanceState.getLong(PLAYER_2_REMAINING, 25*60*1000));
    	}
    }
    
    private Runnable updateTime = new Runnable() {
		@Override
		public void run() {
	        player1Button.setText(formatTime(clock.getPlayer1Remaining()));
	        player2Button.setText(formatTime(clock.getPlayer2Remaining()));
	        handler.postDelayed(updateTime, 10);
		}
	};
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_timer, menu);
        return true;
    }

    @Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.menu_reset:
			clock.reset();
			break;
		case R.id.menu_settings:
			Intent settings = new Intent(this, Preferences.class);
			startActivity(settings);
			break;
		}
		return super.onOptionsItemSelected(item);
	}


	private final static SimpleDateFormat timerFormatHigh = new SimpleDateFormat("mm : ss");
    private final static SimpleDateFormat timerFormatLow = new SimpleDateFormat("ss.SSS");
    private String formatTime(long time) {
    	String prefix = time >=0 ? "" : "- ";
    	if (Math.abs(time) < 1000*60) {
    		return prefix + timerFormatLow.format(new Date(Math.abs(time))).substring(0, 4);
    	} else {
    		return prefix + timerFormatHigh.format(new Date(Math.abs(time)));
    	}
    } 
}
