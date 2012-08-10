package com.iakob.bramblecriste;

public class Clock {
	
	private static int TURN_PLAYER1 = 1;
	private static int TURN_PLAYER2 = 2;
	private static int TURN_PAUSED = 0;
	
	private Player player1;
	private Player player2;
	
	private ClockConfig config;
	
	public Clock(ClockConfig config) {
		this.config = config;
		player1 = new Player(this.config.initialTimePlayer1);
		player2 = new Player(this.config.initialTimePlayer2);
	}
	
	public void reset() {
		pause();
		player1.setRemaining(config.initialTimePlayer1);
		player2.setRemaining(config.initialTimePlayer2);
	}
	
	public void pause() {
		player1.stop();
		player2.stop();
	}
	
	public void startPlayer1() {
		player2.stop();
		player1.start();
	}
	
	public void startPlayer2() {
		player1.stop();
		player2.start();
	}

	public long getPlayer1Remaining() {
		return player1.getRemaining();
	}
	
	public long getPlayer2Remaining() {
		return player2.getRemaining();
	}

	public void setPlayer1Remaining(long time) {
		player1.setRemaining(time);
	}
	
	public void setPlayer2Remaining(long time) {
		player2.setRemaining(time);
	}
	
	public int getTurn() {
		if (player1.isRunning()) {
			return TURN_PLAYER1;
		}
		if (player2.isRunning()) {
			return TURN_PLAYER2;
		}
		return TURN_PAUSED;
	}
		
	private class Player {
		private boolean running = false;
		private long remaining;
		private long turnStart = 0;
		
		public Player(long initialtime) {
			remaining = initialtime;
		}
		
		public void setRemaining(long time) {
			remaining = time;
		}
		
		public void stop() {
			if (running) {
				running = false;
				remaining -= System.currentTimeMillis() - turnStart;
			}
		}
		
		public void start() {
			if (!running) {
				running = true;
				turnStart = System.currentTimeMillis();
			}
		}
		
		public long getRemaining() {
			if (running) {
				return remaining - (System.currentTimeMillis() - turnStart);
			}
			return remaining;
		}
		
		public boolean isRunning() {
			return running;
		}
	}
}
