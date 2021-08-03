package nachos.proj1.models.states;

import nachos.proj1.MyFileSystem;
import nachos.proj1.MyNetworkLink;
import nachos.proj1.interfaces.IState;
import nachos.proj1.models.Meeting;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;
import nachos.proj1.utils.Validator;

public class ChatState implements IState {

	private Meeting currentMeeting = new Meeting();
	private MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	public ChatState() {
		
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
		System.out.println("1. Private Chat");
		System.out.println("2. Public Chat");
		System.out.println("3. Exit");
		System.out.print(">> ");
	}

	@Override
	public void changeState(User user, int input) {
		currentMeeting = Validator.isValidMeetingIdentifier(Util.currentMeetingID);
		if(input == 1) {
			if(currentMeeting.isPrivateMessage()) {
				user.setState(new PrivateChatMenuState());
			}
		}
		else if(input == 2) user.setState(new PublicChatState());
		else if(input == 3) {
			if(myNetworkLink.isHost()) user.setState(new HostStartMenuState());
			else if(myNetworkLink.isParticipant()) user.setState(new ParticipantStartMenuState());
		}
	}

	@Override
	public void changeState(User user, String input) {
		
	}

}
