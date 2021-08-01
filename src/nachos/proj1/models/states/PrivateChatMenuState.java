package nachos.proj1.models.states;

import nachos.proj1.MyFileSystem;
import nachos.proj1.interfaces.IState;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;

public class PrivateChatMenuState implements IState {

	private MyFileSystem myFileSystem = MyFileSystem.getInstance();
	
	public PrivateChatMenuState() {
		
	}

	@Override
	public void getInputFromUser(User user, int input) {
		changeState(user, input);
	}

	@Override
	public void getInputFromUser(User user, String input) {
		
	}

	@Override
	public void printStateMenu(User user) {
		myFileSystem.loadParticipantMeetingData();
		Util.printDynamicList(MyFileSystem.getListParticipantMeeting().size(), null, MyFileSystem.getListParticipantMeeting(), user);
		System.out.println(">> (input -1 to leave private message menu) ");
	}

	@Override
	public void changeState(User user, int input) {
		user.setState(new PrivateChatState());
	}

	@Override
	public void changeState(User user, String input) {
		
	}

}
