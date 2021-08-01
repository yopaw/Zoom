package nachos.proj1.models.states;

import nachos.proj1.interfaces.IState;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;

public class ParticipantStartMenuState implements IState {

	public ParticipantStartMenuState() {
		
	}

	@Override
	public void changeState(User user, int input) {
		if(input == 1) user.setState(new InviteOtherPeopleState());
		else if(input == 4) user.setState(new ChatState());
		else if(input == 5) user.setState(new ExitMeetingState());
	}

	@Override
	public void printStateMenu(User user) {
		String raiseHandMenu = "";
		if(Util.isRaisedHand) raiseHandMenu = "Lower ";
		else raiseHandMenu = "Raise ";
		raiseHandMenu += "Hand";
		System.out.println("1. Invite Other People");
		System.out.println("2. "+raiseHandMenu);
		System.out.println("3. Record");
		System.out.println("4. Chat");
		System.out.println("5. Exit");
		System.out.print(">> ");
	}

	@Override
	public void getInputFromUser(User user, int input) {
		switch(input) {
		case 1:
			changeState(user, input);
			break;
		case 2:
			Util.isRaisedHand = !Util.isRaisedHand;
			break;
		case 3:
			
			break;
		case 4:
			changeState(user, input);
			break;
		case 5:
			changeState(user, input);
			break;
		}		
	}

	@Override
	public void getInputFromUser(User user, String input) {
		
	}

	@Override
	public void changeState(User user, String input) {
		
	}

}
