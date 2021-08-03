package nachos.proj1;

import java.util.Vector;

import nachos.proj1.models.Meeting;
import nachos.proj1.models.Record;
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
	private MyTimer myTimer = MyTimer.getInstance();
	private Util util = Util.getInstance();
	
	public MainSystem() {
		authMenu();
	}
	
	public void authMenu() {
		int option;
		do {
			Util.clearScreen(30);
			console.println("Zoom Poom Boom");
			console.println("=================");
			console.println("1. Register");
			console.println("2. Login");
			console.println("3. Exit");
			console.print(">> ");
			option = console.scanInt();
			Util.clearScreen(30);
			switch(option) {
			case 1:
				Register.doRegister();
				break;
			case 2:
				currentUser = Login.doLogin();
				if(currentUser != null) mainMenu();
				break;
			case 3:
				printLogo();
				break;
			}
		}while(option != 3 && currentUser == null);
	}
	
	private void initialize() {
		myFileSystem.loadOnlineUsersData();
		Util.currentUsername = currentUser.getUsername();
		String onlineUserFormat = currentUser.getUsername() + MyFileSystem.DELIMITER +
				currentUser.getCurrentNetworkAddress();
		if(Validator.isNotContainsByName(myFileSystem.getOnlineUsersData(), currentUser.getUsername())) {
			myFileSystem.appendFile(MyFileSystem.ONLINE_USERS_FILE_NAME, onlineUserFormat);
		}
	}
	
	public void mainMenu() {
		int option = 0;
		initialize();
		do {
			Util.clearScreen(30);
			console.println("Zoom Poom Boom");
			console.println("=================");
			console.println("1. Create a Meeting");
			console.println("2. Join a Meeting");
			console.println("3. View Meeting Recording");
			console.println("4. View Meeting Request");
			console.println("5. Exit");
			console.print(">> ");
			option = console.scanInt();
			Util.clearScreen(30);
			switch (option) {
			case 1:
				createNewMeeting();
				break;
			case 2:
				joinMeeting();
				break;
			case 3:
				viewMeetingRecording();
				break;
			case 4:
				viewMeetingRequest();
				break;
			case 5:
				myFileSystem.overwriteOnlineUsersData(currentUser.getUsername());
				printLogo();
				break;
			}
		} while (option != 5);
	}

	private void viewMeetingRecording() {
		Vector<String> meetingID = myFileSystem.getMeetingsIDUserData(currentUser.getUsername());
		Util.printDynamicList(meetingID);
		int input = 0;
		do {
			console.print(">> ");
			input = console.scanInt();
		} while (input < 1 || input > meetingID.size());
		Vector<Record> records = myFileSystem.getMeetingRecordData(meetingID.get(input-1), currentUser.getUsername());
		util.playRecord(records, meetingID.get(input-1));
		console.print("Press Enter to Continue...");
		console.scan();
	}

	private void viewMeetingRequest() {
		int input = 0;
		if(myNetworkLink.getRequests().isEmpty()) {
			console.println("Sorry you dont have any invitation");
			console.scan();
		}
		else {
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
	}

	private void joinMeeting() {
		String input = "";
		String inputMenu;
		Meeting currentMeeting;
		String meetingPassword;
		while (true) {
			if (!isInvited) {
				do {
					console.print("Please input Meeting ID or Meeting Links: ");
					input = console.scan();
					currentMeeting = Validator.isValidMeetingIdentifier(input);
				} while (currentMeeting == null);
				if (!input.startsWith("https:") && !currentMeeting.getPassword().isEmpty()) {
					console.print("Please input password for the Meeting: ");
					meetingPassword = console.scan();
					if (!Validator.isPasswordValid(input, meetingPassword))
						return;
					else
						break;
				}
				else break;
			} else {
				currentMeeting = currentInvitedMeeting;
				break;
			}
		}
		
		MyTimer.time = 0;
		Util.currentMeetingID = currentMeeting.getMeetingID();
		Util.currentMeetingLink = "https://www." + Util.currentMeetingID + ".com";

		currentUser.setState(new ParticipantStartMenuState());

		myNetworkLink.initialize();

		String joinMeetingFormat = "join" + MyNetworkLink.DELIMITER + myNetworkLink.getNetworkAddress()
				+ MyNetworkLink.DELIMITER + currentUser.getUsername() + MyNetworkLink.DELIMITER
				+ currentMeeting.getMeetingID();

		String participantMeetingFormat = currentUser.getUsername() + MyFileSystem.DELIMITER
				+ currentUser.getCurrentNetworkAddress();

		System.out.println(currentMeeting.getMeetingID());
		myFileSystem.appendFile("participant" + currentMeeting.getMeetingID() + MyFileSystem.FILE_EXTENSION,
				participantMeetingFormat);

		myNetworkLink.setCurrentUser(currentUser);
		myNetworkLink.send(currentMeeting.getHostAddress(), joinMeetingFormat);

		myNetworkLink.setParticipant(true);

		while (true) {
			myNetworkLink.liveStreaming();
			String userState = currentUser.getState().getClass().getSimpleName();
			inputMenu = console.scan();
			if (Validator.isNumeric(inputMenu)) {
				int inputINT = Integer.parseInt(inputMenu);
				if (userState.equals("InviteOtherPeopleState")) {
					if (inputINT > 0) {
						String inviteParticipantFormat = "invite" + MyNetworkLink.DELIMITER + Util.currentMeetingID
								+ MyNetworkLink.DELIMITER + currentUser.getUsername();

						int destinationNetworkAddress = myFileSystem.getOnlineUsersDataNotInMeeting().get(inputINT - 1)
								.getCurrentNetworkAddress();
						myNetworkLink.send(destinationNetworkAddress, inviteParticipantFormat);
					}
				} else if (userState.equals("PrivateChatMenuState")) {
					Util.destinationPrivateMessageAddress = inputINT;
				} else if (userState.equals("ParticipantStartMenuState")) {
					if (inputINT == 2) {
						String raiseHandFormat;
						if (!Util.isRaisedHand)
							raiseHandFormat = "raise";
						else
							raiseHandFormat = "lower";
						raiseHandFormat += MyNetworkLink.DELIMITER + "raise" + MyNetworkLink.DELIMITER
								+ currentUser.getUsername();
						myNetworkLink.send(currentMeeting.getHostAddress(), raiseHandFormat);
					}
				}
				currentUser.getState().getInputFromUser(currentUser, inputINT);
			} else {
				if (userState.equals("PublicChatState")) {
					if (!inputMenu.equals("exit")) {
						String string = "chat" + MyNetworkLink.DELIMITER + inputMenu + MyNetworkLink.DELIMITER
								+ currentUser.getUsername();
						myNetworkLink.send(currentMeeting.getHostAddress(), string);
					}
				} else if (userState.equals("PrivateChatState")) {
					if (!inputMenu.equals("exit")) {
						if(MyFileSystem.getListParticipantMeeting().
								get(Util.destinationPrivateMessageAddress-1).
								getCurrentNetworkAddress() == currentUser.getCurrentNetworkAddress()) {
							System.out.println();
							System.out.println("You cannot chat to your own self");
						}
						else {
							String privateChatFormat = "private" + MyNetworkLink.DELIMITER + inputMenu
									+ MyNetworkLink.DELIMITER + currentUser.getUsername();
							myNetworkLink.getRecords().add(
									new Record(currentUser.getUsername() + " (private)", inputMenu, MyTimer.time / 20000));
							myNetworkLink.send(MyFileSystem.getListParticipantMeeting()
									.get(Util.destinationPrivateMessageAddress - 1).getCurrentNetworkAddress(),
									privateChatFormat);
						}
					}
				}
				currentUser.getState().getInputFromUser(currentUser, inputMenu);
			}
			if (currentUser.getState().getClass().getSimpleName().equals("ExitMeetingState")) {
				if (Validator.isNumeric(inputMenu)) {
					int inputINT = Integer.parseInt(inputMenu);
					if (inputINT == 5) {
						if(Util.isRecording) {
							myNetworkLink.getRecords().add(new Record(currentUser.getUsername(),
									"You left the meeting", MyTimer.getTime()));
						}
						String leaveMeetingFormat = "leave" + MyNetworkLink.DELIMITER
								+ myNetworkLink.getNetworkAddress() + MyNetworkLink.DELIMITER
								+ currentUser.getUsername();
						myNetworkLink.send(currentMeeting.getHostAddress(), leaveMeetingFormat);
						myFileSystem.overwriteParticipantUsersData(currentUser.getUsername());
					}
				}
				else {
					myNetworkLink.getRecords().add(new Record(currentUser.getUsername(), 
							"The meeting has ended",
							MyTimer.getTime()));
				}
				break;
			}
		}
		if(Util.canRecord) util.addCurrentRecording();
		util.initialize();
	}

	private void createNewMeeting() {
		String privateMeeting;
		String inputMenu;
		String meetingPassword = "";
		do {
			console.print("Create a private meeting [Yes|No]: ");
			privateMeeting = console.scan();
		} while (!privateMeeting.equals("Yes") && !privateMeeting.equals("No"));
		if(privateMeeting.equals("Yes")) {
			do {
				console.print("Please input Meeting Password: ");
				meetingPassword = console.scan();
			} while (meetingPassword.trim().isEmpty());
		}
		
		MyTimer.time = 0;
		myNetworkLink.initialize();
		
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
		
		
		Util.currentMeetingID = meeting.getMeetingID();
		Util.currentMeetingLink = "https://www."+Util.currentMeetingID+".com";
		currentUser.setState(new HostStartMenuState());
		myNetworkLink.setCurrentUser(currentUser);
		myNetworkLink.setHost(true);
		
		String participantMeetingFormat = currentUser.getUsername() +
				MyFileSystem.DELIMITER + currentUser.getCurrentNetworkAddress();
		
		myFileSystem.appendFile("participant"+meeting.getMeetingID()+MyFileSystem.FILE_EXTENSION,
				participantMeetingFormat);
		
		while (true) {
			console.println(meeting.getMeetingLink());
			myNetworkLink.liveStreaming();
			inputMenu = console.scan();
			String userState = currentUser.getState().getClass().getSimpleName();
			if(!userState.equals("ExitMeetingState")) {
				if(Validator.isNumeric(inputMenu)) {
					int input = Integer.parseInt(inputMenu);
					if (userState.equals("InviteOtherPeopleState")) {
						if (input > 0) {
							String inviteParticipantFormat = "invite" + MyNetworkLink.DELIMITER + Util.currentMeetingID
									+ MyNetworkLink.DELIMITER + currentUser.getUsername();

							int destinationNetworkAddress = myFileSystem.getOnlineUsersDataNotInMeeting().get(input - 1)
									.getCurrentNetworkAddress();
							myNetworkLink.send(destinationNetworkAddress, inviteParticipantFormat);
						}
					} else if (userState.equals("PrivateChatMenuState")) {
						Util.destinationPrivateMessageAddress = input;
					}
					else if (userState.equals("HostStartMenuState")) {
						if (input == 4) {
							MyNetworkLink.isHostRaisedHand = true;
							myNetworkLink.sendRequestToParticipant();
						}
					}
					currentUser.getState().getInputFromUser(currentUser, input);
				}
				else {
					if (userState.equals("PublicChatState")) {
						if(!inputMenu.equals("exit")) {
							MyNetworkLink.publicMessage = inputMenu;
							myNetworkLink.getRecords().add(new Record(currentUser.getUsername(), 
									inputMenu, MyTimer.time/20000));
							myNetworkLink.sendRequestToParticipant();
						}
					}
					else if(userState.equals("PrivateChatState")) {
						if(!inputMenu.equals("exit")) {
							if(Util.destinationPrivateMessageAddress-1 == currentUser.getCurrentNetworkAddress()) {
								System.out.println("You cant message yourself!");	
							}
							String privateChatFormat = "private" + MyNetworkLink.DELIMITER + inputMenu +
									MyNetworkLink.DELIMITER + currentUser.getUsername();
							myNetworkLink.getRecords().add(new Record(currentUser.getUsername() + " (private)",
									inputMenu,
									MyTimer.time/20000));
							myNetworkLink.send(MyFileSystem.getListParticipantMeeting().get(Util.destinationPrivateMessageAddress-1).getCurrentNetworkAddress(), 
									privateChatFormat);
						}
					}
					currentUser.getState().getInputFromUser(currentUser, inputMenu);
				}
			}
			if(currentUser.getState().getClass().getSimpleName().equals("ExitMeetingState")) {
				myNetworkLink.getRecords().add(new Record(currentUser.getUsername(), "The meeting has ended",
						MyTimer.getTime()));
				myNetworkLink.sendRequestToParticipant();
				break;
			}
		}
		util.addCurrentRecording();
		util.initialize();
	}
	private void printLogo() {
		String logo19_1[] = {
				"\n",
				"\n",
				"\n",
				"                       ###############: .------- :###############    ",
				"                     `###############- -::::::::-`-###############`  ",
				"                    `########. ```````-::######:-` `-.``` .########` ",
				"                   .########` -::::::::/######-   -/:::::-``########.",
				"                   /####### `-:::########/-    `-######:::-` #######/",
				"                    :#####  -:::######-`   `-###########:::-` #####: ",
				"                     -####. `-::####````-###############/:-` .####-  ",
				"                      .####: `-::#.`-#:.####-`` .######/:-` :####.   ",
				"                       `####- .::/#####  #````#` /####/::. -####`    ",
				"                        `###`.::#######/ `.:####--:.`.##::.`###`     ",
				"                          :`.:::##############:.````:###/::-`:       ",
				"                            -:::/#########:.`   .:######:::-         ",
				"                             .:::/#####-`   .:########/:::.          ",
				"                              .------`  `:######::-------.           ",
				"                               `::- ``.::######::- `::::`            ",
				"                                /####- .::::::::. .#####             ",
				"                                 -####: `......` :####/              ",
				"                                  .#####////////#####-               ",
				"                                   `################.                ",
				"                                     /#############`                 ",
				"                                      -###########                   ",
				"\n"
		};
		
		for(int i=0; i<25; i++){
			System.out.println(logo19_1[i]);
			try {
				Thread.sleep(100);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		String motto = "\"Always Try New Things, Overcome All Problems\" - Bluejack 19-1\0";	
		System.out.print("             ");
		for(int i=0; motto.charAt(i) != '\0'; i++){
			System.out.print(motto.charAt(i));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		System.out.println();
		
		String maker = "                                           By : PB 19-1 \0";
		for(int i=0; maker.charAt(i) != '\0'; i++){
			System.out.print(maker.charAt(i));
			try {
				Thread.sleep(50);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
