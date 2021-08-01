package nachos.proj1.models.states;

import nachos.proj1.interfaces.IState;
import nachos.proj1.models.User;

public class ChatState implements IState {

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
		if(input == 1) user.setState(new PrivateChatMenuState());
		else if(input == 2) user.setState(new PublicChatState());
	}

	@Override
	public void changeState(User user, String input) {
		
	}

}
