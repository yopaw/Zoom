package nachos.proj1.models.states;

import nachos.proj1.interfaces.IState;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;

public class HostStartMenuState implements IState {

	private String recordMeetingMenu;
	private String privateMessageMenu;
	private String raiseHandMenu;
	
	public HostStartMenuState() {
	}

	@Override
	public void getInputFromUser(User user, int input) {
		switch (input) {
		case 1:
			changeState(user, input);
			break;
		case 2:
			Util.isRecording = !Util.isRecording;
			break;
		case 3:
			Util.isPrivateMessage = !Util.isPrivateMessage;
			break;
		case 4:
			Util.isRaisedHand = !Util.isRaisedHand;
			break;
		case 5:
			changeState(user, input);
			break;
		case 6:
			changeState(user, input);
			break;
		}
	}

	@Override
	public void getInputFromUser(User user, String input) {
	}

	@Override
	public void printStateMenu(User user) {
		initialize();
		System.out.println("1. Invite Other People");
		System.out.println("2. "+recordMeetingMenu);
		System.out.println("3. "+privateMessageMenu);
		System.out.println("4. "+raiseHandMenu);
		System.out.println("5. Chat");
		System.out.println("6. Exit");
		System.out.print(">> ");
	}
	
	private void initialize() {
		if(Util.isPrivateMessage) privateMessageMenu = "Enable";
		else privateMessageMenu = "Disable";
		privateMessageMenu = "Private Message";
		if(Util.isRecording) recordMeetingMenu = "Enable";
		else recordMeetingMenu = "Disable";
		recordMeetingMenu += "Record Meeting";
		if(Util.isRaisedHand) raiseHandMenu = "Lower";
		else raiseHandMenu = "Raise";
		raiseHandMenu += "Hand";
	}

	@Override
	public void changeState(User user, int input) {
		if(input == 1) user.setState(new InviteOtherPeopleState());
		else if(input == 5) user.setState(new ChatState());
		else if(input == 6) user.setState(new ExitMeetingState());
	}

	@Override
	public void changeState(User user, String input) {
		
	}

}