package nachos.proj1.utils;

import java.util.Vector;

import nachos.proj1.MyFileSystem;
import nachos.proj1.MyNetworkLink;
import nachos.proj1.models.Record;
import nachos.proj1.models.Request;
import nachos.proj1.models.User;

public class Util {

	public static String currentMeetingID;
	public static String currentMeetingLink;
	public static boolean isRaisedHand = false;
	public static boolean isRecording = false;
	public static boolean isPrivateMessage = false;
	public static int destinationPrivateMessageAddress = 0;
	public static Vector<Integer> startRecordingTimes = new Vector<>();
	public static Vector<Integer> endRecordingTimes = new Vector<>();
	private MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	private MyFileSystem myFileSystem = MyFileSystem.getInstance();
	public static Vector<Record> currentRecording = new Vector<>();
	public static String currentUsername;
	private static Util instance = null;
	public static boolean canRecord = false;
	
	public void initialize() {
		currentMeetingID = "";
		currentMeetingLink = "";
		isRaisedHand = false;
		isRecording = false;
		isPrivateMessage = false;
		destinationPrivateMessageAddress = 0;
		startRecordingTimes.clear();
		endRecordingTimes.clear();
	}
	
	public void addCurrentRecording() {
		int index = 0;
		currentRecording.clear();
		for(int i = 0; i < myNetworkLink.getRecords().size() ; i++) {
			int startTime = 0;
			int endTime = 0;
			Record currentRecord = myNetworkLink.getRecords().get(i);
			int recordTime = currentRecord.getTime();
			
			if(index < startRecordingTimes.size()) 
				startTime = startRecordingTimes.get(index);
			if(index < endRecordingTimes.size())
				endTime = endRecordingTimes.get(index);
			
			if(myNetworkLink.isHost()) {
				currentRecording.add(currentRecord);
			}
			else {
				if(endTime != 0) {
					if(recordTime >= startTime && recordTime <= endTime) {
						currentRecording.add(currentRecord);
					}
				}
				else {
					if(recordTime >= startTime) {
						currentRecording.add(currentRecord);
					}
				}
			}
		}
		myFileSystem.appendFile(currentUsername+"records"+MyFileSystem.FILE_EXTENSION, currentMeetingID);
		for (Record record : currentRecording) {
			String recordFormat = record.getUsername() + MyFileSystem.DELIMITER +
				record.getContent() + MyFileSystem.DELIMITER +
				record.getTime();
			myFileSystem.appendFile(currentUsername+"record"+
				currentMeetingID+MyFileSystem.FILE_EXTENSION, 
					recordFormat);
		}
	}
	
	private String changeTimeToString(final int TIME) {
		if(TIME < 10) return "0"+String.valueOf(TIME);
		return String.valueOf(TIME);
	}
	
	public void playRecord(Vector<Record> records, String meetingId) {
		int limit = 25;
		int max = 0;
		String viewer = "Recording Meeting: "+meetingId;
		String informationMeeting;
		
		int currentTime = (int) (records.lastElement().getTime());
		int seconds = (int) (currentTime % 60);
		int hours = (int) (currentTime / 60);
		int minutes = hours % 60;
		
		hours /= 60;
		
		informationMeeting =  changeTimeToString(hours) + ":"
				+ changeTimeToString(minutes) + ":" + changeTimeToString(seconds);
	
		Vector<String> vecDuration = new Vector<>();
		
		for (Record record: records) {
			int secondsDuration = (int) (record.getTime() % 60);
			int hoursDuration = (int) (record.getTime() / 60);
			int minutesDuration = hoursDuration % 60;
			hoursDuration /= 60;
			String duration = changeTimeToString(hoursDuration)+ ":" + changeTimeToString(minutesDuration)+ ":"
					+ changeTimeToString(secondsDuration);
			vecDuration.add(duration);
			String currentRecords = record.getUsername() + " : " + record.getContent();
			if (max < currentRecords.length())
				max = currentRecords.length();
		}
		int time = 0;
		while (true) {
			time++;
			int secondsDuration = (int) (time % 60);
			int hoursDuration = (int) (time / 60);
			int minutesDuration = hoursDuration % 60;
			hoursDuration /= 60;
			String duration = changeTimeToString(hoursDuration)+ ":" + 
			changeTimeToString(minutesDuration)+ ":"
					+ changeTimeToString(secondsDuration);
			clearScreen(30);
			System.out.println();
			System.out.println(duration + " / " + informationMeeting);
			for (int i = 0; i < max + limit; i++) {
				System.out.print("=");
			}
			System.out.println();
			for (int i = 0; i < max + limit; i++) {
				if (i == 0 || i == max + limit - 1)
					System.out.print("|");
				else if (i == 1)
					System.out.print(".");
				else if (i == max + limit - viewer.length())
					System.out.print(viewer);
				else if (i < max + limit - viewer.length())
					System.out.print(" ");
			}
			for (int x = 0; x < records.size(); x++) {
				Record currentRecord = records.get(x);
				int difference = currentRecord.getTime();
				
				if (difference <= time) {
					if (x == 0) {
						System.out.println();
						for (int i = 0; i < max + limit; i++)
							System.out.print("-");
						System.out.println();
					}
					String recordFormat; 
					if(x < records.size()-1 ) recordFormat = currentRecord.getUsername()+": "+currentRecord.getContent().trim();
					else recordFormat = currentRecord.getContent();
					if (x == 0) {
						for (int i = 0; i < max + limit; i++) {
							if (i == 0 || i == max + limit - 1)
								System.out.print("|");
							else if (i == 1)
								System.out.print(".");
							else if (i == recordFormat.length())
								System.out.print(recordFormat);
							else if (i == max + limit - vecDuration.get(x).length() - 1)
								System.out.print(vecDuration.get(x));
							else if (i > recordFormat.length() && i < max + limit - vecDuration.get(x).length())
								System.out.print(" ");
						}
					} else {
						System.out.print("\n");
						for (int i = 0; i < max + limit; i++) {
							if (i == 0 || i == max + limit - 1)
								System.out.print("|");
							else if (i == 1)
								System.out.print(".");
							else if (i == recordFormat.length())
								System.out.print(recordFormat);
							else if (i == max + limit - vecDuration.get(x).length() - 1)
								System.out.print(vecDuration.get(x));
							else if (i > recordFormat.length() && i < max + limit - vecDuration.get(x).length())
								System.out.print(" ");
						}
					}
				}
			}
			System.out.print("\n");
			for (int i = 0; i < max + limit; i++) {
				System.out.print("=");
			}
			if (duration.equals(informationMeeting))
				break;
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
	
	
	
	private Util() {
		
	}
	
	public static Util getInstance() {
		return instance == null ? instance = new Util() : instance;
	}
	
	public static void clearScreen(int row) {
		for(int i = 0; i < row; i++) System.out.println();
	}
	
	public static void printDynamicList(int totalParticipant, Vector<Request> requests) {
		int limit = 25;
		int max = 0;
		String viewer = "Total Participant: "+totalParticipant;
		String informationMeeting;
	
		for (Request request : requests) {
			String currentRequest = request.getMeetingLinks() + " : " + request.getSenderUsername();
			if (max < currentRequest.length())
				max = currentRequest.length();
		}
		informationMeeting = " °LIVE";
	
		for(int i = 0 ; i < max+limit ; i++) {
			System.out.print("=");
		}
		System.out.println();
		for(int i = 0 ; i < max+limit ; i++) {
			if(i == 0 || i == max + limit-1) System.out.print("|");
			else if(i == 1) System.out.print(".");
			else if(i == max + limit - informationMeeting.length()) System.out.print(informationMeeting);
			else if(i < max + limit - informationMeeting.length()) System.out.print(" ");
		}
		
		System.out.print("\n");
		
		for(int i = 0 ; i < max+limit ; i++) {
			if(i == 0 || i == max + limit - 1) System.out.print("|");
			else if(i == 1) System.out.print(".");
			else if(i == max + limit - viewer.length()) System.out.print(viewer);
			else if(i < max + limit -  viewer.length()) System.out.print(" ");
		}
		
		for (int x = 0; x < requests.size(); x++) {
			if (x == 0) {
				System.out.println();
				for (int i = 0; i < max + limit; i++)
					System.out.print("-");
				System.out.println();
			}
			String currentRequest = " "+(x+1)+". "+requests.get(x).getMeetingLinks() + " : " + 
			requests.get(x).getSenderUsername();
			if (x == 0) {
				for (int i = 0; i < max + limit; i++) {
					if (i == 0 || i == max + limit - 1)
						System.out.print("|");
					else if (i == 1)
						System.out.print(".");
					else if (i == currentRequest.length())
						System.out.print(currentRequest);
					else if (i > currentRequest.length())
						System.out.print(" ");
				}
			} else {
				System.out.print("\n");
				for (int i = 0; i < max + limit; i++) {
					if (i == 0 || i == max + limit - 1)
						System.out.print("|");
					else if (i == 1)
						System.out.print(".");
					else if (i == currentRequest.length())
						System.out.print(currentRequest);
					else if (i > currentRequest.length())
						System.out.print(" ");
				}
			}
		}
		System.out.print("\n");
		for(int i = 0 ; i < max+limit ; i++) {
			System.out.print("=");
		}
		System.out.print("\n");
	}
	

	public static void printDynamicList(Vector<String> requests) {
		int limit = 25;
		int max = 0;
		String informationMeeting;
	
		for (String request : requests) {
			String currentRequest = request;
			if (max < currentRequest.length())
				max = currentRequest.length();
		}
		informationMeeting = " °LIVE";
	
		for(int i = 0 ; i < max+limit ; i++) {
			System.out.print("=");
		}
		System.out.println();
		for(int i = 0 ; i < max+limit ; i++) {
			if(i == 0 || i == max + limit-1) System.out.print("|");
			else if(i == 1) System.out.print(".");
			else if(i == max + limit - informationMeeting.length()) System.out.print(informationMeeting);
			else if(i < max + limit - informationMeeting.length()) System.out.print(" ");
		}
		
		
		for (int x = 0; x < requests.size(); x++) {
			if (x == 0) {
				System.out.println();
				for (int i = 0; i < max + limit; i++)
					System.out.print("-");
				System.out.println();
			}
			String currentRequest = " "+(x+1)+". "+requests.get(x);
			if (x == 0) {
				for (int i = 0; i < max + limit; i++) {
					if (i == 0 || i == max + limit - 1)
						System.out.print("|");
					else if (i == 1)
						System.out.print(".");
					else if (i == currentRequest.length())
						System.out.print(currentRequest);
					else if (i > currentRequest.length())
						System.out.print(" ");
				}
			} else {
				System.out.print("\n");
				for (int i = 0; i < max + limit; i++) {
					if (i == 0 || i == max + limit - 1)
						System.out.print("|");
					else if (i == 1)
						System.out.print(".");
					else if (i == currentRequest.length())
						System.out.print(currentRequest);
					else if (i > currentRequest.length())
						System.out.print(" ");
				}
			}
		}
		System.out.print("\n");
		for(int i = 0 ; i < max+limit ; i++) {
			System.out.print("=");
		}
		System.out.print("\n");
	}
	
	public static void printDynamicList(int totalParticipant, Vector<Record> records, 
		Vector<User> users, User currentUser) {
		int limit = 25;
		int max = 0;
		String viewer = "Total Participant: "+totalParticipant;
		String informationMeeting;
		if(records == null) {
			int index = 0;
			for (User currUser : users) {
				index++;
				String currUserData = index+". "+currUser.getUsername();
				if(max < currUserData.length()) max = currUserData.length();
			}
			informationMeeting = "Online Users";
		}
		else {
			for (Record record : records) {
				String currentRecord = record.getUsername()+" : "+record.getContent();
				if(max < currentRecord.length()) max = currentRecord.length();
			}
			informationMeeting = " °LIVE";
		}
		
		for(int i = 0 ; i < max+limit ; i++) {
			System.out.print("=");
		}
		
		System.out.println();
		for(int i = 0 ; i < max+limit ; i++) {
			if(i == 0 || i == max + limit-1) System.out.print("|");
			else if(i == 1) System.out.print(".");
			else if(i == max + limit - informationMeeting.length()) System.out.print(informationMeeting);
			else if(i < max + limit - informationMeeting.length()) System.out.print(" ");
		}
		
		System.out.print("\n");
		
		for(int i = 0 ; i < max+limit ; i++) {
			if(i == 0 || i == max + limit - 1) System.out.print("|");
			else if(i == 1) System.out.print(".");
			else if(i == max + limit - viewer.length()) System.out.print(viewer);
			else if(i < max + limit -  viewer.length()) System.out.print(" ");
		}
		
		if(records == null) {
			for (int x = 0 ; x < users.size() ; x ++) {
				if(x == 0 ) {
					System.out.println();
					for(int i = 0 ; i < max+limit ; i++) System.out.print("-");
					System.out.println();
				}
				String currentUserString= " "+(x+1) + ". "+users.get(x).getUsername();
				if(x == 0) {
					for(int i = 0 ; i < max+limit ; i++) {
						if(i == 0 || i == max + limit - 1) System.out.print("|");
						else if(i == 1) System.out.print(".");
						else if(i == currentUserString.length()) System.out.print(currentUserString);
						else if(i > currentUserString.length()) System.out.print(" ");
					}
				}
				else  {
					System.out.print("\n");
					for(int i = 0 ; i < max+limit ; i++) {
						if(i == 0 || i == max + limit - 1) System.out.print("|");
						else if(i == 1) System.out.print(".");
						else if(i == currentUserString.length()) System.out.print(currentUserString);
						else if(i > currentUserString.length()) System.out.print(" ");
					}
				}
			} 
		}
		else {
			for (int x = 0 ; x < records.size() ; x ++) {
				if(x == 0 ) {
					System.out.println();
					for(int i = 0 ; i < max+limit ; i++) System.out.print("-");
					System.out.println();
				}
				String currentRecord = records.get(x).getUsername()+" : "+records.get(x).getContent();
				if(records.get(x).getUsername().equals(currentUser.getUsername())) {
					currentRecord = records.get(x).getUsername()+" (You) : "+records.get(x).getContent();
				}
				if(x == 0) {
					for(int i = 0 ; i < max+limit ; i++) {
						if(i == 0 || i == max + limit - 1) System.out.print("|");
						else if(i == 1) System.out.print(".");
						else if(i == currentRecord.length()) System.out.print(currentRecord);
						else if(i > currentRecord.length()) System.out.print(" ");
					}
				}
				else  {
					System.out.print("\n");
					for(int i = 0 ; i < max+limit ; i++) {
						if(i == 0 || i == max + limit - 1) System.out.print("|");
						else if(i == 1) System.out.print(".");
						else if(i == currentRecord.length()) System.out.print(currentRecord);
						else if(i > currentRecord.length()) System.out.print(" ");
					}
				}
			} 
		}
		System.out.print("\n");
		for(int i = 0 ; i < max+limit ; i++) {
			System.out.print("=");
		}
		System.out.print("\n");
	}

}
