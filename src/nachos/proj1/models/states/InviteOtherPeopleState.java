package nachos.proj1.models.states;

import nachos.proj1.MyFileSystem;
import nachos.proj1.MyNetworkLink;
import nachos.proj1.interfaces.IState;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;

public class InviteOtherPeopleState implements IState{

	private MyFileSystem myFileSystem = MyFileSystem.getInstance();
	private MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	
	public InviteOtherPeopleState() {
		
	}

	@Override
	public void changeState(User user, int input) {
		if (myNetworkLink.isHost())
			user.setState(new HostStartMenuState());
		else if (myNetworkLink.isParticipant())
			user.setState(new ParticipantStartMenuState());
	}

	@Override
	public void printStateMenu(User user) {
		Util.printDynamicList(myFileSystem.getOnlineUsersDataNotInMeeting().size(), null, myFileSystem.getOnlineUsersDataNotInMeeting(), user);
		System.out.print(">> (input -1 to leave invite participation menu) ");
	}

	@Override
	public void getInputFromUser(User user, int input) {
		changeState(user, input);
	}

	@Override
	public void getInputFromUser(User user, String input) {
		
	}

	@Override
	public void changeState(User user, String input) {
		
	}

}
