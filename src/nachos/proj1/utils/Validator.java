package nachos.proj1.utils;

import java.util.Vector;

import nachos.proj1.MyFileSystem;
import nachos.proj1.models.Meeting;
import nachos.proj1.models.User;

public class Validator {

	private static MyFileSystem myFileSystem = MyFileSystem.getInstance();
	public Validator() {
		// TODO Auto-generated constructor stub
	}
	
	public static boolean isNotContainsByName(final Vector<User> users, final String NAME) {
		for (User user : users) {
			if(user.getUsername().equals(NAME)) {
				return false;
			}
		}
		return true;
	}
	
	public static boolean isNotContainsById(final Vector<String> meetingsID, final String ID) {
		for (String string : meetingsID) {
			if(ID.equals(string)) {
				return false;
			}
		}
		return true;
	}
	
	public static Meeting isValidMeetingIdentifier(final String MEETING_IDENTIFIER) {
		String fileContent = myFileSystem.readFile(MyFileSystem.MEETING_FILE_NAME);
		String[] listOfMeeting = fileContent.split(MyFileSystem.MULTIPLE_DELIMITER);
		for (String currentMeeting : listOfMeeting) {
			String currentMeetingFileName = currentMeeting + MyFileSystem.FILE_EXTENSION;
			String[] meetingData = myFileSystem.readFile(currentMeetingFileName).split(MyFileSystem.DELIMITER);
			if(meetingData.length > 1) {
				String meetingLink = meetingData[0];
				String meetingPassword = meetingData[1];
				String hostUsername = meetingData[2];
				String isPrivate = meetingData[3];
				int hostAddress = Integer.parseInt(meetingData[4]);
				boolean privateMessage = Boolean.parseBoolean(meetingData[5]);
				boolean recordMeeting = Boolean.parseBoolean(meetingData[6]);
				
				if (meetingLink.equals(MEETING_IDENTIFIER)) {
					return new Meeting(currentMeeting, meetingPassword, meetingLink,
							hostUsername, isPrivate, hostAddress, privateMessage, recordMeeting);
				}
			}
		}
		
		fileContent = myFileSystem.readFile(MEETING_IDENTIFIER + MyFileSystem.FILE_EXTENSION);
		String[] meetingData = fileContent.split(MyFileSystem.DELIMITER);
		
		if(fileContent.isEmpty()) return null;
		else {
			String meetingLink = meetingData[0];
			String meetingPassword = meetingData[1];
			String hostUsername = meetingData[2];
			String isPrivate = meetingData[3];
			int hostAddress = Integer.parseInt(meetingData[4]);
			
			boolean privateMessage = Boolean.parseBoolean(meetingData[5]);
			boolean recordMeeting = Boolean.parseBoolean(meetingData[6]);
			
			return new Meeting(MEETING_IDENTIFIER, meetingPassword, meetingLink,
					hostUsername, isPrivate, hostAddress, privateMessage, recordMeeting);
		}
				
	}
	
	public static boolean isPasswordValid(final String MEETING_IDENTIFIER, final String PASSWORD) {
		String fileContent = myFileSystem.readFile(MEETING_IDENTIFIER + MyFileSystem.FILE_EXTENSION);
		String[] contents = fileContent.split(MyFileSystem.DELIMITER);
		String meetingPassword = contents[1];
		if(meetingPassword.equals(PASSWORD)) return true;
		return false;
 	}
	
	public static boolean isNumeric(final String STRING) {
		try {
			Integer.parseInt(STRING);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}
}
