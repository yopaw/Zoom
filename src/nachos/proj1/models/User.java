package nachos.proj1.models;

import java.util.Vector;

import nachos.proj1.interfaces.IState;

public class User {
	
	private String username;
	private String password;
	private Vector<Meeting> videos = new Vector<>();
	private int currentNetworkAddress;
	private IState state;
	
	public Vector<Meeting> getVideos() {
		return videos;
	}

	public void setVideos(Vector<Meeting> videos) {
		this.videos = videos;
	}

	public int getCurrentNetworkAddress() {
		return currentNetworkAddress;
	}

	public void setCurrentNetworkAddress(int currentNetworkAddress) {
		this.currentNetworkAddress = currentNetworkAddress;
	}

	public IState getState() {
		return state;
	}

	public void setState(IState state) {
		this.state = state;
	}

	public User() {
		
	}

	public User(String username, int currentNetworkAddress) {
		super();
		this.username = username;
		this.currentNetworkAddress = currentNetworkAddress;
	}
	
	public User(String username, String password, int currentNetworkAddress) {
		super();
		this.username = username;
		this.password = password;
		this.currentNetworkAddress = currentNetworkAddress;
	}

	public String getUsername() {
		return username;
	}



	public void setUsername(String username) {
		this.username = username;
	}



	public String getPassword() {
		return password;
	}



	public void setPassword(String password) {
		this.password = password;
	}

}
