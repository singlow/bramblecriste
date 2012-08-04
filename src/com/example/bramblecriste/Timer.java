package com.example.bramblecriste;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Handler;
import android.os.Vibrator;
import android.view.Menu;
import android.view.MenuItem;
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
    
	private static long INITIAL_DEFAULT = 25*60*1000;
    private static String INCREMENT = "turnIncrement";
    private static long INCREMENT_DEFAULT = 0;
    
    private static String PLAYER_1_INITIAL = "player1Initial";
    private static String PLAYER_2_INITIAL = "player2Initial";
    private static String PLAYER_1_REMAINING = "player1Remaining";
    private static String PLAYER_2_REMAINING = "player2Remaining";
    
    private Vibrator vibe;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, 
                                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        
        vibe = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE) ;
    
    	prefs = this.getPreferences(MODE_PRIVATE);
		config = new ClockConfig(
				prefs.getLong(PLAYER_1_INITIAL, INITIAL_DEFAULT),
				prefs.getLong(PLAYER_2_INITIAL, INITIAL_DEFAULT),
				prefs.getLong(INCREMENT, INCREMENT_DEFAULT));
		clock = new Clock(config);

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
        
        player1Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(50);
				clock.startPlayer2();
				player2Button.setEnabled(true);
				player2Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_active));
				pauseButton.setEnabled(true);
				player1Button.setEnabled(false);
				player1Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
			}
		});
        player2Button.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(50);
				clock.startPlayer1();
				player2Button.setEnabled(false);
				player2Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
				pauseButton.setEnabled(true);
				player1Button.setEnabled(true);
				player1Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_active));
			}
		});
        pauseButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				vibe.vibrate(50);
				clock.pause();
				player2Button.setEnabled(true);
				player2Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
				pauseButton.setEnabled(false);
				player1Button.setEnabled(true);
				player1Button.setBackgroundDrawable(getResources().getDrawable(R.color.player_button_color));
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
		clock.setPlayer1Remaining(prefs.getLong(PLAYER_1_REMAINING, 25*60*1000));
		clock.setPlayer2Remaining(prefs.getLong(PLAYER_2_REMAINING, 25*60*1000));
		handler.postDelayed(updateTime, 10);
		player1Button.setEnabled(true);
		player2Button.setEnabled(true);
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
