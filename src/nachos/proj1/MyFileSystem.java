package nachos.proj1;

import java.util.Vector;

import nachos.machine.FileSystem;
import nachos.machine.Machine;
import nachos.machine.OpenFile;
import nachos.proj1.models.Meeting;
import nachos.proj1.models.Record;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;
import nachos.proj1.utils.Validator;

public class MyFileSystem {

	private FileSystem fs = Machine.stubFileSystem();
	private static MyFileSystem instance = null;
	public static final String DELIMITER = "#";
	public static final String MULTIPLE_DELIMITER = "\n";
	public static final String USER_FILE_NAME = "users.txt";
	public static final String FILE_EXTENSION = ".txt";
	public static final String MEETING_FILE_NAME = "meetings.txt";
	public static final String ONLINE_USERS_FILE_NAME = "online.txt";
	private static Vector<User> listOnlineUsers = new Vector<>();
	private static Vector<User> listParticipantMeeting = new Vector<>();
	
	private MyFileSystem() {
		
	}
	
	public static MyFileSystem getInstance() {
		return instance == null ? instance = new MyFileSystem() : instance;
	}
	
	public String readFile(final String FILENAME) {
		OpenFile of = fs.open(FILENAME, false);
		String string = "";
		if(of == null) {
			return string.trim();
		}
		else {
			byte[] data = new byte[9999];
			of.read(data, 0, data.length);
			string = new String(data);
			of.close();
		}
		return string.trim();
	}
	
	public void overwriteFile(final String FILENAME, final String CONTENT) {
		OpenFile of = fs.open(FILENAME, true);
		byte[] data = CONTENT.getBytes();
		of.write(data, 0, data.length);
		of.close();
	}

	public void writeFile(final String FILENAME, final String CONTENT) {
		OpenFile of = fs.open(FILENAME, false);
		if(of == null) of = fs.open(FILENAME, true);
		byte[] data = CONTENT.getBytes();
		of.write(data, 0, data.length);
		of.close();
	}
	
	public void appendFile(final String FILENAME, final String CONTENT) {
		OpenFile of = fs.open(FILENAME, false);
		if(of == null) of = fs.open(FILENAME, true);
		String lastFileContent = readFile(FILENAME);
		if(!lastFileContent.isEmpty())lastFileContent += "\n";
		lastFileContent += CONTENT;
		byte[] data = lastFileContent.getBytes();
		of.write(data, 0, data.length);
		of.close();
	}
	
	public void loadParticipantMeetingData() {
		String participantMeeting = readFile("participant"+Util.currentMeetingID+FILE_EXTENSION);
		String [] participantMeetingData = participantMeeting.split("\n");
		listParticipantMeeting.clear();
		System.out.println(Util.currentMeetingID);
		System.out.println(participantMeetingData.length);
		for (String string : participantMeetingData) {
			String [] currentUserData = string.split(DELIMITER);
			String username = currentUserData[0];
			int userNetworkAddress = Integer.parseInt(currentUserData[1]);
			
			listParticipantMeeting.add(new User(username, userNetworkAddress));
		}
	}
	
	public Vector<User> getOnlineUsersDataNotInMeeting(){
		Vector<User> listOnlineUsersDataNotInMeeting = new Vector<>();
		loadParticipantMeetingData();
		String onlineUsers = readFile(ONLINE_USERS_FILE_NAME);
		String [] onlineUsersData = onlineUsers.split("\n");
		listOnlineUsers.clear();
		for (String string : onlineUsersData) {
			String [] currentUserData = string.split(DELIMITER);
			String username = currentUserData[0];
			int userNetworkAddress = Integer.parseInt(currentUserData[1]);
			
			if(Validator.isNotContainsByName(listParticipantMeeting, username)) {
				listOnlineUsersDataNotInMeeting.add(new User(username, userNetworkAddress));
			}
		}
		return listOnlineUsersDataNotInMeeting;
	}
	
	public void loadOnlineUsersData(){
		String onlineUsers = readFile(ONLINE_USERS_FILE_NAME);
		String [] onlineUsersData = onlineUsers.split("\n");
		listOnlineUsers.clear();
		if(!onlineUsers.isEmpty()) {
			for (String string : onlineUsersData) {
				String [] currentUserData = string.split(DELIMITER);
				String username = currentUserData[0];
				int userNetworkAddress = Integer.parseInt(currentUserData[1]);
				listOnlineUsers.add(new User(username, userNetworkAddress));
			}
		}
	}
	
	public Vector<Record> getMeetingRecordData(final String MEETING_ID, final String USERNAME) {
		Vector<Record> records = new Vector<>();
		String listRecords = readFile(USERNAME+"record"+MEETING_ID+FILE_EXTENSION);
		String[] recordDatas = listRecords.split("\n");
		for (String string : recordDatas) {
			String[] currentRecord = string.split("#");
			records.add(new Record(currentRecord[0],currentRecord[1],Integer.parseInt(currentRecord[2])));
		}
		return records;
	}
	
	public void overwriteOnlineUsersData(final String USERNAME) {
		loadOnlineUsersData();
		for (User user : listOnlineUsers) {
			if(user.getUsername().equals(USERNAME)) {
				listOnlineUsers.remove(user);
				break;
			}
		}
		String overwriteContent = "";
		for (User user : listOnlineUsers) {
			String onlineUserFormat = "";
			if(!user.equals(listOnlineUsers.lastElement())) onlineUserFormat = user.getUsername() + MyFileSystem.DELIMITER + user.getCurrentNetworkAddress()+"\n";
			else onlineUserFormat = user.getUsername() + MyFileSystem.DELIMITER + user.getCurrentNetworkAddress();
			overwriteContent += onlineUserFormat;
		}
		overwriteFile(ONLINE_USERS_FILE_NAME, overwriteContent);
	}
	
	public Vector<String> getMeetingsIDData(){
		Vector<String> meetings = new Vector<>();
		String fileContent = readFile(MyFileSystem.MEETING_FILE_NAME);
		String[] listOfMeeting = fileContent.split(MyFileSystem.MULTIPLE_DELIMITER);
		for (String string : listOfMeeting) {
			meetings.add(string);
		}
		return meetings;
	}
	
	public Vector<String> getMeetingsIDUserData(final String USERNAME){
		Vector<String> meetings = new Vector<>();
		String fileContent = readFile(USERNAME+"records"+FILE_EXTENSION);
		String[] listOfMeeting = fileContent.split(MyFileSystem.MULTIPLE_DELIMITER);
		for (String string : listOfMeeting) {
			meetings.add(string);
		}
		return meetings;
	}
	
	public Vector<User> getOnlineUsersData(){
		return listOnlineUsers;
	}

	public static Vector<User> getListParticipantMeeting() {
		return listParticipantMeeting;
	}

	public static void setListParticipantMeeting(Vector<User> listParticipantMeeting) {
		MyFileSystem.listParticipantMeeting = listParticipantMeeting;
	}
}
