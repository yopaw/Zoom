package nachos.proj1.models.states;

import nachos.proj1.MyNetworkLink;
import nachos.proj1.interfaces.IState;
import nachos.proj1.models.User;

public class PrivateChatState implements IState {

	private MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	
	public PrivateChatState() {
		
	}

	@Override
	public void getInputFromUser(User user, int input) {
		
	}

	@Override
	public void getInputFromUser(User user, String input) {
		changeState(user, input);
	}

	@Override
	public void printStateMenu(User user) {
		System.out.print("Chat (input exit to leave the chat menu) : ");
	}

	@Override
	public void changeState(User user, int input) {
		
	}

	@Override
	public void changeState(User user, String input) {
		if(input.equals("exit")) {
			if(myNetworkLink.isHost()) user.setState(new HostStartMenuState());
			else if(myNetworkLink.isParticipant()) user.setState(new ParticipantStartMenuState());
		}
	}
}
