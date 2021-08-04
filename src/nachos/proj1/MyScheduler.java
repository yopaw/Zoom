package nachos.proj1;

import nachos.threads.Scheduler;
import nachos.threads.ThreadQueue;

public class MyScheduler extends Scheduler{

	public MyScheduler() {
		
	}

	@Override
	public ThreadQueue newThreadQueue(boolean transferPriority) {
		// TODO Auto-generated method stub
		return new MyThreadQueue();
	}

}
