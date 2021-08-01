package nachos.proj1;

import nachos.proj1.models.Meeting;
import nachos.proj1.models.User;
import nachos.proj1.models.states.HostStartMenuState;
import nachos.proj1.models.states.ParticipantStartMenuState;
import nachos.proj1.utils.Util;
import nachos.proj1.utils.Validator;

public class MainSystem {

	private MyConsole console = MyConsole.getInstance();
	private User currentUser = null;
	private MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	private MyFileSystem myFileSystem = MyFileSystem.getInstance();
	private boolean isInvited = false;
	private Meeting currentInvitedMeeting = new Meeting();
	
	public MainSystem() {
		authMenu();
	}
	
	public void authMenu() {
		int option;
		do {
			console.println("1. Register");
			console.println("2. Login");
			console.println("3. Exit");
			console.print(">> ");
			option = console.scanInt();
			switch(option) {
			case 1:
				Register.doRegister();
				break;
			case 2:
				currentUser = Login.doLogin();
				break;
			}
		}while(option != 3 && currentUser == null);
		mainMenu();
	}
	
	private void initialize() {
		myFileSystem.loadOnlineUsersData();
		String onlineUserFormat = currentUser.getUsername() + MyFileSystem.DELIMITER +
				currentUser.getCurrentNetworkAddress();
		if(Validator.isNotContainsByName(myFileSystem.getOnlineUsersData(), currentUser.getUsername())) {
			myFileSystem.appendFile(MyFileSystem.ONLINE_USERS_FILE_NAME, onlineUserFormat);
			System.out.println("true");
		}
	}
	
	public void mainMenu() {
		int option = 0;
		initialize();
		do {
			console.println("1. Create a Meeting");
			console.println("2. Join a Meeting");
			console.println("3. View Meeting Recording");
			console.println("4. View Meeting Request");
			console.println("5. Exit");
			console.print(">> ");
			option = console.scanInt();
			switch (option) {
			case 1:
				createNewMeeting();
				break;
			case 2:
				joinMeeting();
				break;
			case 4:
				viewMeetingRequest();
				break;
			case 5:
				
				break;
			}
		} while (option != 5);
	}

	private void viewMeetingRequest() {
		int input = 0;
		do {
			Util.printDynamicList(myNetworkLink.getRequests().size(), myNetworkLink.getRequests());
			console.print(">> ");
			input = console.scanInt();
		} while (input < 1 || input > myNetworkLink.getRequests().size());
		isInvited = true;
		String meetingLink = myNetworkLink.getRequests().get(input-1).getMeetingLinks();
		currentInvitedMeeting = Validator.isValidMeetingIdentifier(meetingLink);
		joinMeeting();
	}

	private void joinMeeting() {
		String input = "";
		String inputMenu;
		Meeting currentMeeting;
		String meetingPassword;
		while (true) {
			if(!isInvited) {
				do {
					console.print("Please input Meeting ID or Meeting Links: ");
					input = console.scan();
					currentMeeting = Validator.isValidMeetingIdentifier(input);
				} while (currentMeeting == null);
				if (!input.startsWith("https:")) {
					console.print("Please input password for the Meeting: ");
					meetingPassword = console.scan();
					if(!Validator.isPasswordValid(input, meetingPassword)) return;
				}
			}
			else currentMeeting = currentInvitedMeeting;
			currentUser.setState(new ParticipantStartMenuState());
			myNetworkLink.initialize();
			String joinMeetingFormat = "join" + MyNetworkLink.DELIMITER + 
					myNetworkLink.getNetworkAddress()+ MyNetworkLink.DELIMITER + 
					currentUser.getUsername() + MyNetworkLink.DELIMITER +
					currentMeeting.getMeetingID();
			
			String participantMeetingFormat = currentUser.getUsername() +
					MyFileSystem.DELIMITER + currentUser.getCurrentNetworkAddress();
			
			System.out.println(currentMeeting.getMeetingID());
			myFileSystem.appendFile("participant"+currentMeeting.getMeetingID()+MyFileSystem.FILE_EXTENSION,
					participantMeetingFormat);

			myNetworkLink.setCurrentUser(currentUser);
			myNetworkLink.send(currentMeeting.getHostAddress(), joinMeetingFormat);
			
			Util.currentMeetingID = currentMeeting.getMeetingID();
			Util.currentMeetingLink = "https://www."+Util.currentMeetingID+".com";
			myNetworkLink.setParticipant(true);
			while (myNetworkLink.isParticipant()) {
				myNetworkLink.liveStreaming();
				System.out.println("START");
				String userState = currentUser.getState().getClass().getSimpleName();
				inputMenu = console.scan();
				if (Validator.isNumeric(inputMenu)) {
					int inputINT = Integer.parseInt(inputMenu);
					if(userState.equals("InviteOtherPeopleState")) {
						if(inputINT > 0) {
							String inviteParticipantFormat = "invite" + MyNetworkLink.DELIMITER +
									Util.currentMeetingID + MyNetworkLink.DELIMITER + 
									currentUser.getUsername();
							
							int destinationNetworkAddress = myFileSystem.getOnlineUsersDataNotInMeeting().get(inputINT-1).getCurrentNetworkAddress();
							myNetworkLink.send(destinationNetworkAddress
									,inviteParticipantFormat);
						}
					}
					else if(userState.equals("ParticipantStartMenuState")) {
						if(inputINT == 3) {
							currentMeeting = Validator.isValidMeetingIdentifier(currentMeeting.getMeetingID());
						}
					}
					else if(userState.equals("PrivateChatMenuState")) {
						Util.destinationPrivateMessageAddress = inputINT;
					}
					currentUser.getState().getInputFromUser(currentUser, inputINT);
			
				} else {
					if (userState.equals("PublicChatState")) {
						String string = "chat" + MyNetworkLink.DELIMITER + inputMenu + MyNetworkLink.DELIMITER
								+ currentUser.getUsername();
						myNetworkLink.send(currentMeeting.getHostAddress(), string);
					}
					else if(userState.equals("PrivateChatState")) {
						String privateChatFormat = "private" + MyNetworkLink.DELIMITER + inputMenu +
								MyNetworkLink.DELIMITER + currentUser.getUsername();
						myNetworkLink.send(Util.destinationPrivateMessageAddress, privateChatFormat);
					}
				}
				if(currentUser.getState().getClass().getSimpleName().equals("ExitMeetingState")) {
					String leaveMeetingFormat = "leave" + MyNetworkLink.DELIMITER + myNetworkLink.getNetworkAddress()
					+ MyNetworkLink.DELIMITER + currentUser.getUsername();
					myNetworkLink.send(currentMeeting.getHostAddress(), leaveMeetingFormat);
					break;
				}
			}
		}
	}

	private void createNewMeeting() {
		String privateMeeting;
		String inputMenu;
		console.print("Create a private meeting [Yes|No]: ");
		privateMeeting = console.scan();
		if(privateMeeting.equals("Yes")) {
			String meetingPassword;
			console.print("Please input Meeting Password: ");
			meetingPassword = console.scan();
			
			Meeting meeting = 
					new Meeting(meetingPassword, currentUser.getUsername(), 
							privateMeeting, myNetworkLink.getNetworkAddress());
			
			String saveMeetingFormat = meeting.getMeetingLink() + MyFileSystem.DELIMITER + 
					meeting.getPassword() + MyFileSystem.DELIMITER + meeting.getHostUsername() +
					MyFileSystem.DELIMITER + meeting.getIsPrivate() + MyFileSystem.DELIMITER +
					meeting.getHostAddress() + MyFileSystem.DELIMITER +
					meeting.isPrivateMessage() + MyFileSystem.DELIMITER +
					meeting.isRecording();
			
			myFileSystem.appendFile(MyFileSystem.MEETING_FILE_NAME, meeting.getMeetingID());
			myFileSystem.writeFile(meeting.getMeetingID() + MyFileSystem.FILE_EXTENSION, 
					saveMeetingFormat);
			
			currentUser.setState(new HostStartMenuState());
			myNetworkLink.setCurrentUser(currentUser);
			myNetworkLink.setHost(true);
			
			String participantMeetingFormat = currentUser.getUsername() +
					MyFileSystem.DELIMITER + currentUser.getCurrentNetworkAddress();
			
			myFileSystem.appendFile("participant"+meeting.getMeetingID()+MyFileSystem.FILE_EXTENSION,
					participantMeetingFormat);
			
			Util.currentMeetingID = meeting.getMeetingID();
			Util.currentMeetingLink = "https://www."+Util.currentMeetingID+".com";
			
			while (true) {
				console.println(meeting.getMeetingLink());
				myNetworkLink.liveStreaming();
				inputMenu = console.scan();
				if(Validator.isNumeric(inputMenu)) {
					int input = Integer.parseInt(inputMenu);
					currentUser.getState().getInputFromUser(currentUser, input);
				}
			}
		}
	}
}