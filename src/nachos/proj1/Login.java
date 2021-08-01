package nachos.proj1;

import nachos.proj1.models.User;

public class Login {
	
	private static MyConsole console = MyConsole.getInstance();
	private static MyFileSystem myFileSystem = MyFileSystem.getInstance();
	private static MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	private static final String FILENAME = "users.txt";
	
	public Login() {
		
	}
	
	public static User doLogin() {
		String username,password;
		console.print("Input username: ");
		username = console.scan();
		console.print("Input password: ");
		password = console.scan();
		
		String output = myFileSystem.readFile(FILENAME);
		String[] users = output.trim().split("\n");
		
		for (String string : users) {
			String[] currUser = string.split(MyFileSystem.DELIMITER);
			if(currUser.length > 1) {
				String currUsername = currUser[0];
				String currPassword = currUser[1];
				System.out.println(currPassword+" "+currUsername);
				if(currUsername.equals(username) && currPassword.equals(password)) {
					User user = new User(username, password, myNetworkLink.getNetworkAddress());
					return user;
				}
			}
		}
		return null;
	}
	
}
