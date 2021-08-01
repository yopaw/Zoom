package nachos.proj1;

import nachos.machine.Machine;
import nachos.machine.Timer;

public class MyTimer {

	private Timer timer = Machine.timer(); 
	private static MyTimer instance = null;
	public static int time = 0;
	
	private MyTimer() {
		timer.setInterruptHandler(new Runnable() {
			@Override
			public void run() {
				time++;
			}
		});
	}
	
	public static int getTime() {
		return time / 20000;
	}
	
	public static MyTimer getInstance() {
		return instance  == null ? instance = new MyTimer() : instance;
	}

}
