package nachos.proj1.models;

public class Request {
	
	private String meetingLinks;
	private String senderUsername;
	
	public Request(String meetingLinks, String senderUsername) {
		this.meetingLinks = meetingLinks;
		this.senderUsername = senderUsername;
	}

	public Request() {
		// TODO Auto-generated constructor stub
	}

	public String getMeetingLinks() {
		return meetingLinks;
	}

	public void setMeetingLinks(String meetingLinks) {
		this.meetingLinks = meetingLinks;
	}

	public String getSenderUsername() {
		return senderUsername;
	}

	public void setSenderUsername(String senderUsername) {
		this.senderUsername = senderUsername;
	}
	
}
