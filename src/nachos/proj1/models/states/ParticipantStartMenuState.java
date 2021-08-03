package nachos.proj1.models.states;


import nachos.proj1.MyTimer;
import nachos.proj1.interfaces.IState;
import nachos.proj1.models.Meeting;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;
import nachos.proj1.utils.Validator;

public class ParticipantStartMenuState implements IState {
	private String raiseHandMenu = "";
	private String recordMeetingMenu = "";
	private Meeting currentMeeting = new Meeting();
	public ParticipantStartMenuState() {
		
	}
	
	private void initialize() {
		if(Util.isRaisedHand) raiseHandMenu = "Lower ";
		else raiseHandMenu = "Raise ";
		raiseHandMenu += "Hand";
		if(Util.isRecording) recordMeetingMenu = "Stop ";
		else recordMeetingMenu = "Start ";
		recordMeetingMenu += "Record Meeting";
	}

	@Override
	public void changeState(User user, int input) {
		if(input == 1) user.setState(new InviteOtherPeopleState());
		else if(input == 4) user.setState(new ChatState());
		else if(input == 5) user.setState(new ExitMeetingState());
	}

	@Override
	public void printStateMenu(User user) {
		initialize();
		System.out.println("Participant Menu");
		System.out.println("=====================");
		System.out.println("1. Invite Other People");
		System.out.println("2. "+raiseHandMenu);
		System.out.println("3. "+recordMeetingMenu);
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
			currentMeeting = Validator.isValidMeetingIdentifier(Util.currentMeetingID);
			Util.isRecording = !Util.isRecording;
			if(!currentMeeting.isRecording()) Util.isRecording = false;
			if(Util.isRecording && currentMeeting.isRecording()) {
				Util.startRecordingTimes.add(MyTimer.time/20000);
				Util.canRecord = true;
			}
			else if(!Util.isRecording) {
				if(Util.startRecordingTimes.size() > Util.endRecordingTimes.size())
					Util.endRecordingTimes.add(MyTimer.time/20000);
			}
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
