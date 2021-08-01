package nachos.proj1.utils;

import java.util.Vector;

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
	public Util() {
		
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
			String currentRequest = requests.get(x).getMeetingLinks() + " : " + 
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
	
	public static void printDynamicList(int totalParticipant, Vector<Record> records, 
		Vector<User> users, User currentUser) {
		int limit = 25;
		int max = 0;
		String viewer = "Total Participant: "+totalParticipant;
		String informationMeeting;
		System.out.println("PRINT DYNAMIC LIST");
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
				String currentUserString= (x+1) + ". "+users.get(x).getUsername();
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
		System.out.println("END DYNAMIC LIST");
	}

}
