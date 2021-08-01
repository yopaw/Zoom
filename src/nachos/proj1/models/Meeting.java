package nachos.proj1.models;

import java.util.Random;

public class Meeting {
	
	private String meetingID = "";
	private String password;
	private String meetingLink;
	private String hostUsername;
	private String isPrivate;
	private int hostAddress;
	private boolean isPrivateMessage = false;
	private boolean isRecording = false;
	
	public Meeting() {
		
	}
	
	public boolean isPrivateMessage() {
		return isPrivateMessage;
	}

	public void setPrivateMessage(boolean isPrivateMessage) {
		this.isPrivateMessage = isPrivateMessage;
	}

	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}
	
	public Meeting(String meetingID, String password, String meetingLink, String hostUsername, String isPrivate,
			int hostAddress, boolean isPrivateMessage, boolean isRecording) {
		super();
		this.meetingID = meetingID;
		this.password = password;
		this.meetingLink = meetingLink;
		this.hostUsername = hostUsername;
		this.isPrivate = isPrivate;
		this.hostAddress = hostAddress;
		this.isPrivateMessage = isPrivateMessage;
		this.isRecording = isRecording;
	}

	public Meeting(String meetingID, String password, String meetingLink, String hostUsername, String isPrivate,
			int hostAddress) {
		super();
		this.meetingID = meetingID;
		this.password = password;
		this.meetingLink = meetingLink;
		this.hostUsername = hostUsername;
		this.isPrivate = isPrivate;
		this.hostAddress = hostAddress;
	}

	public Meeting(String password, String hostUsername, String isPrivate, int hostAddress) {
		super();
		this.hostUsername = hostUsername;
		this.password = password;
		this.isPrivate = isPrivate;
		this.hostAddress = hostAddress;
		generateMeetingID();
		generateLinkMeeting();
	}
	
	public int getHostAddress() {
		return hostAddress;
	}

	public void setHostAddress(int hostAddress) {
		this.hostAddress = hostAddress;
	}

	public void generateLinkMeeting() {
		 this.meetingLink = "https://www."+meetingID+".com";
	}
	
	public String getMeetingID() {
		return meetingID;
	}
	
	public void setMeetingID(String meetingID) {
		this.meetingID = meetingID;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getMeetingLink() {
		return meetingLink;
	}

	public void setMeetingLink(String meetingLink) {
		this.meetingLink = meetingLink;
	}

	public String getHostUsername() {
		return hostUsername;
	}

	public void setHostUsername(String hostUsername) {
		this.hostUsername = hostUsername;
	}

	public String getIsPrivate() {
		return isPrivate;
	}

	public void setIsPrivate(String isPrivate) {
		this.isPrivate = isPrivate;
	}
	
	public void generateMeetingID(){
		Random random = new Random();
		for(int i = 0; i < 6; i++) {
			int randomChance = random.nextInt(2);
			if(randomChance == 0) {
				int randomNumber = random.nextInt(10);
				meetingID += randomNumber;
			}
			else {
				char randomCharacter = (char) (random.nextInt(26) + 65);
				System.out.println(randomCharacter);
				meetingID += randomCharacter;
			}
		}
	}
}
