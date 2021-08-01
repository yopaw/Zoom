package nachos.proj1.models;

public class Chat {

	private String username;
	private String content;
	
	
	public Chat(String username, String content) {
		this.username = username;
		this.content = content;
	}



	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}



	public String getContent() {
		return content;
	}



	public void setContent(String content) {
		this.content = content;
	}



	public Chat() {
		// TODO Auto-generated constructor stub
	}

}
