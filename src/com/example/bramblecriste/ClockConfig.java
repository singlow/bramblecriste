package com.example.bramblecriste;

public class ClockConfig {
	public long initialTimePlayer1;
	public long initialTimePlayer2;
	public long turnIncrement;
	
	public ClockConfig(long initialTimePlayer1, long initialTimePlayer2, long turnIncrement) {
		this.initialTimePlayer1 = initialTimePlayer1;
		this.initialTimePlayer2 = initialTimePlayer2;
		this.turnIncrement = turnIncrement;
	}
}