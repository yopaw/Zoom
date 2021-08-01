package nachos.proj1;

import java.util.Vector;

import nachos.machine.Machine;
import nachos.machine.MalformedPacketException;
import nachos.machine.NetworkLink;
import nachos.machine.Packet;
import nachos.proj1.models.Record;
import nachos.proj1.models.Request;
import nachos.proj1.models.User;
import nachos.proj1.utils.Util;
import nachos.threads.KThread;
import nachos.threads.Semaphore;

public class MyNetworkLink {
	
	private static MyNetworkLink instance = null;
	private NetworkLink networkLink = Machine.networkLink();
	private Semaphore networkLinkSemaphore = new Semaphore(0);
	private Vector<User> listParticipant = new Vector<>();
	private Vector<Record> records = new Vector<>();
	private Vector<Request> requests = new Vector<>();
	private User currentUser = new User();
	
	public static final char DELIMITER = '#';
	private int totalParticipant = 0; 
	private String purpose;
	private String participantMessage;
	private String participantUsername;
	private boolean isHost = false;
	private boolean isParticipant = false;
	private String recordContent;
	public static String publicMessage = "";
	
	public void initialize() {
		listParticipant.clear();
		records.clear();
		isHost = false;
		isParticipant = false;
		recordContent = "";
		participantUsername = "";
		participantMessage = "";
		purpose = "";
		totalParticipant = 0;
	}
	
	
	public MyNetworkLink() {
		Runnable receiveInterruptHandler = new Runnable() {
			
			@Override
			public void run() {
				String []contents = receive().split(DELIMITER+"");
				String currentTime = String.valueOf(MyTimer.time/20000);
				purpose = contents[0];
				participantMessage = contents[1];
				participantUsername = contents[2];
				if(isHost) {
					if(purpose.equals("join")) {
						int participantId = Integer.parseInt(participantMessage);
						String meetingIdentifier = contents[3];
						if(meetingIdentifier.equals(Util.currentMeetingID) ||
								meetingIdentifier.equals(Util.currentMeetingLink)) {
							recordContent = participantUsername+" joined the stream !";
							records.add(new Record(participantUsername, 
									recordContent, 
									currentTime));
							totalParticipant++;
							listParticipant.add(new User(participantUsername, "", participantId));
							sendRequestToParticipant();
						}
					}
					else if(purpose.equals("chat")) {
						recordContent = participantMessage;
						records.add(new Record(participantUsername,
								recordContent,
								currentTime));
						sendRequestToParticipant();
					}
					else if(purpose.equals("raise")) {
						recordContent = participantUsername + 
								"raised his / her hand";
						records.add(new Record(participantUsername, 
								recordContent,
								currentTime));
						sendRequestToParticipant();
					}
					else if(purpose.equals("leave")) {
						recordContent = participantUsername +
								"leave the meeting";
						records.add(new Record(participantUsername,
								recordContent,
								currentTime));
						removeParticipantByName(participantUsername);
						totalParticipant--;
						sendRequestToParticipant();
					}
					else if(purpose.equals("private")) {
						recordContent = participantMessage;
						records.add(new Record(participantUsername + " (private)",
								recordContent,
								currentTime));
					}
					liveStreaming();
				}
				else {
					if(purpose.equals("chat")) {
						records.add(new Record(participantUsername, participantMessage,
								currentTime));
						liveStreaming();
					}
					else if(purpose.equals("update")) {
						isParticipant = true;
						int tempTotalParticipant = totalParticipant;
						totalParticipant = Integer.parseInt(participantMessage);
						if(participantUsername.equals(currentUser.getUsername())) 
							recordContent = participantUsername+"(You) joined the stream !";
						else {
							if(tempTotalParticipant > totalParticipant)
								recordContent = participantUsername+" leave the stream !";
							else 
								recordContent = participantUsername+" joined the stream !";
						}
						records.add(new Record(participantUsername, recordContent, currentTime));
						liveStreaming();
					}
					else if(purpose.equals("raise")) {
						recordContent = participantUsername + 
								"raised his / her hand";
						records.add(new Record(participantUsername, 
								recordContent,
								currentTime));
						liveStreaming();
					}
					else if(purpose.equals("invite")) {
						requests.add(new Request(participantMessage, participantUsername));
					}
					else if(purpose.equals("private")) {
						recordContent = participantMessage;
						records.add(new Record(participantUsername + " (private)",
								recordContent,
								currentTime));
						liveStreaming();
					}
				}
			}
		};
		
		Runnable sendInterruptHandler = new Runnable() {
			@Override
			public void run() {
				networkLinkSemaphore.V();
			}
		};
		
		networkLink.setInterruptHandlers(receiveInterruptHandler, sendInterruptHandler);
	}
	
	public static MyNetworkLink getInstance() {
		return instance == null ? instance = new MyNetworkLink() : instance;
	}
	
	public int getNetworkAddress() {
		return networkLink.getLinkAddress();
	}

	public void send(int destinationAddress, String string) {
		Packet packet = null;
		try {
			packet = new Packet(destinationAddress, getNetworkAddress(), string.getBytes());
		} catch (MalformedPacketException e) {
			
		}
		networkLink.send(packet);
		networkLinkSemaphore.P();
	}
	
	public void sendRequestToParticipant() {
		KThread thread = new KThread(new Runnable() {
			@Override
			public void run() {
				String userState = currentUser.getState().getClass().getSimpleName();
				if(userState.equals("ExitMeetingState")) {
					for(int i = 0; i < listParticipant.size(); i++) {
						String string = "leave" + DELIMITER + "" + DELIMITER + currentUser.getUsername();
						send(listParticipant.get(i).getCurrentNetworkAddress(), string);
					}
				}
				else if(userState.equals("PublicChatState")) {
					for(int i = 0; i < listParticipant.size(); i++) {
						String string = "chat" + MyNetworkLink.DELIMITER + publicMessage + MyNetworkLink.DELIMITER
								+ currentUser.getUsername();
						send(listParticipant.get(i).getCurrentNetworkAddress(), string);
					}
				}
				else if(purpose.equals("join") || purpose.equals("leave")) {
					for(int i = 0; i < listParticipant.size(); i++) {
						String string = "update" + DELIMITER + totalParticipant + 
								DELIMITER + participantUsername;
						send(listParticipant.get(i).getCurrentNetworkAddress(), string);
					}
				}
				else if(purpose.equals("chat")) {
					for(int i = 0; i < listParticipant.size(); i++) {
						String string = "chat" + DELIMITER + participantMessage + 
								DELIMITER + participantUsername;
						send(listParticipant.get(i).getCurrentNetworkAddress(), string);
					}
				}
				else if(purpose.equals("raise")) {
					for(int i = 0; i < listParticipant.size(); i++) {
						String string = "raise" + DELIMITER +  
								recordContent + DELIMITER + participantUsername;
						send(listParticipant.get(i).getCurrentNetworkAddress(), string);
					}
				}
			}
		});
		thread.fork();
	}
	
	public void liveStreaming() {
		Util.clearScreen(30);
		Util.printDynamicList(totalParticipant, records, null, currentUser);
		System.out.println(currentUser.getState().getClass().getSimpleName());
		currentUser.getState().printStateMenu(currentUser);
	}
	
	private void removeParticipantByName(final String PARTICIPANT_NAME) {
		for (User user : listParticipant) {
			if(user.getUsername().equals(PARTICIPANT_NAME)) {
				listParticipant.remove(user);
				break;
			}
		}
	}
	
	public Vector<Record> getRecords() {
		return records;
	}


	public void setRecords(Vector<Record> records) {
		this.records = records;
	}


	public String receive() {
		return new String(networkLink.receive().contents);
	}
	
	public Vector<Request> getRequests() {
		return requests;
	}

	public void setRequests(Vector<Request> requests) {
		this.requests = requests;
	}

	public Vector<User> getListParticipant() {
		return listParticipant;
	}

	public void setListParticipant(Vector<User> listParticipant) {
		this.listParticipant = listParticipant;
	}

	public User getCurrentUser() {
		return currentUser;
	}

	public void setCurrentUser(User currentUser) {
		this.currentUser = currentUser;
	}
	
	public boolean isHost() {
		return isHost;
	}

	public void setHost(boolean isHost) {
		this.isHost = isHost;
	}


	public boolean isParticipant() {
		return isParticipant;
	}

	public void setParticipant(boolean isParticipant) {
		this.isParticipant = isParticipant;
	}
	
}
