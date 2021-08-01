package nachos.proj1;

import nachos.machine.Machine;
import nachos.machine.SerialConsole;
import nachos.threads.Semaphore;

public class MyConsole {
	
	private SerialConsole console = Machine.console();
	private static MyConsole instance = null;
	private Semaphore sem = new Semaphore(0);
	private char temp;
	private MyConsole() {
		
		Runnable receiveInterruptHandler = new Runnable() {
			
			@Override
			public void run() {
				temp = (char) console.readByte();
				sem.V();
			}
		};
		
		Runnable sendInterruptHandler = new Runnable() {
			
			@Override
			public void run() {
				sem.V();
			}
		};
		
		console.setInterruptHandlers(receiveInterruptHandler, sendInterruptHandler);
	}
	
	public static MyConsole getInstance() {
		if(instance == null) instance = new MyConsole();
		return instance;
	}
	
	public String scan() {
		String result = "";
		do {
			sem.P();
			if(temp != '\n') result += temp;
		}while(temp != '\n');
		return result;
	}
	
	public int scanInt() {
		int number;
		try {
			number = Integer.parseInt(scan());
		} catch (NumberFormatException e) {
			return -1;
		}
		return number;
	}
	
	public void print(final Object target) {
		String string = target.toString();
		for(int i = 0; i < string.length(); i++) {
			console.writeByte(string.charAt(i));
			sem.P();
		}
	}
	
	public void println(final Object target) {
		print(target+"\n");
	}
}
