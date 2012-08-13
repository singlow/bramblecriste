package com.iakob.bramblecriste;

import java.text.DecimalFormat;
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
import android.util.TypedValue;
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

	public static String COLOR_THEME = "color_theme";
    
    public static String VIBRATE_ON_PRESS = "vibrate_on_press";
    private Vibrator vibe;
    private boolean vibrateOnPress = false;
    private int pauseCount = 0;
    
    private int themeId;

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
		try {
			themeId = Integer.parseInt(prefs.getString(COLOR_THEME, Integer.toString(R.style.Bramblecriste)));
		} catch (NumberFormatException e) {
			themeId = R.style.Bramblecriste;
		}
		setTheme(themeId);
		
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

        final TypedValue playerActiveColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.player_active_color, playerActiveColor, true);
        final TypedValue playerDefaultColor = new TypedValue();
        getTheme().resolveAttribute(R.attr.player_default_color, playerDefaultColor, true);
        
        player1Button.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					handler.removeCallbacks(updateTimeDisplayTask);
			        handler.postDelayed(updateTimeDisplayTask, 20);
					pauseCount = 0;
					if (vibrateOnPress) vibe.vibrate(50);
					clock.startPlayer2();
					player2Button.setEnabled(true);
					player2Button.setBackgroundColor(playerActiveColor.data);
					player1Button.setEnabled(false);
					player1Button.setBackgroundColor(playerDefaultColor.data);
				}
				return true;
			}
		});
        player2Button.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					handler.removeCallbacks(updateTimeDisplayTask);
			        handler.postDelayed(updateTimeDisplayTask, 20);
					pauseCount = 0;
					if (vibrateOnPress) vibe.vibrate(50);
					clock.startPlayer1();
					player2Button.setEnabled(false);
					player2Button.setBackgroundColor(playerDefaultColor.data);
					player1Button.setEnabled(true);
					player1Button.setBackgroundColor(playerActiveColor.data);
				}
				return true;	
			}
		});
        pauseButton.setOnTouchListener(new View.OnTouchListener() {
			@Override
			public boolean onTouch(View v, MotionEvent e) {
				if (e.getAction() == MotionEvent.ACTION_DOWN) {
					handler.removeCallbacks(updateTimeDisplayTask);
					
					if (vibrateOnPress) vibe.vibrate(50);
					
					clock.pause();
					
					player2Button.setEnabled(true);
					player2Button.setBackgroundColor(playerDefaultColor.data);
					player1Button.setEnabled(true);
					player1Button.setBackgroundColor(playerDefaultColor.data);
					
					pauseCount++;
					if (pauseCount >= 5) {
						clock.reset();
						pauseCount = 0;
					}
				}
				
				updateTimeDisplay();
				
				return true;
			}
		});
        
        handler = new Handler();
    }
    
    
    @Override
    public void onPause() {
    	super.onPause();
    	clock.pause();
    	handler.removeCallbacks(updateTimeDisplayTask);
    	Editor editor = prefs.edit();
       	editor.putLong(PLAYER_1_REMAINING, clock.getPlayer1Remaining());
       	editor.putLong(PLAYER_2_REMAINING, clock.getPlayer2Remaining());
       	editor.commit();
    }
    
    @Override
    public void onResume() {
		super.onResume();
		
		if (themeId != Integer.parseInt(prefs.getString(COLOR_THEME, Integer.toString(R.style.Bramblecriste)))) {
			reload();
		}
		
		config.initialTimePlayer1 = Long.parseLong(prefs.getString(PLAYER_1_INITIAL, INITIAL_DEFAULT));
		config.initialTimePlayer2 = Long.parseLong(prefs.getString(PLAYER_2_INITIAL, INITIAL_DEFAULT));
		clock.setPlayer1Remaining(prefs.getLong(PLAYER_1_REMAINING, config.initialTimePlayer1));
		clock.setPlayer2Remaining(prefs.getLong(PLAYER_2_REMAINING, config.initialTimePlayer2));
		handler.postDelayed(updateTimeDisplayTask, 10);
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
    
    private Runnable updateTimeDisplayTask = new Runnable() {
		@Override
		public void run() {
			updateTimeDisplay();
	        handler.postDelayed(updateTimeDisplayTask, 20);
		}
	};
	
	private void updateTimeDisplay() {
        player1Button.setText(formatTime(clock.getPlayer1Remaining()));
        player2Button.setText(formatTime(clock.getPlayer2Remaining()));
	}
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_timer, menu);
        return true;
    }

	@Override
	protected void onDestroy() {
		handler.removeCallbacks(updateTimeDisplayTask);
		super.onDestroy();
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
    private final static DecimalFormat timerFormatLow = new DecimalFormat("0.0");
    private String formatTime(long time) {
    	if (Math.abs(time) < 1000*60) {
    		return timerFormatLow.format((double) time / 1000);
    	} else {
        	String prefix = time >=0 ? "" : "-";
    		return prefix + timerFormatHigh.format(new Date(Math.abs(time)));
    	}
    } 

    public void reload() {
        Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();

        overridePendingTransition(0, 0);
        startActivity(intent);
    }
}
