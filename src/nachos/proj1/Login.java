package nachos.proj1;

import java.util.Vector;

import nachos.proj1.models.User;
import nachos.proj1.utils.Util;
import nachos.proj1.utils.Validator;

public class Login {
	
	private static MyConsole console = MyConsole.getInstance();
	private static MyFileSystem myFileSystem = MyFileSystem.getInstance();
	private static MyNetworkLink myNetworkLink = MyNetworkLink.getInstance();
	private static final String FILENAME = "users.txt";
	
	public Login() {
		
	}
	
	public static User doLogin() {
		String username,password;
		do {
			console.print("Input username: ");
			username = console.scan();
			console.print("Input password: ");
			password = console.scan();
		} while (!Validator.isValidRegisterFormat(username, password));
		String output = myFileSystem.readFile(FILENAME);
		String[] users = output.trim().split("\n");
		
		for (String string : users) {
			String[] currUser = string.split(MyFileSystem.DELIMITER);
			if(currUser.length > 1) {
				String currUsername = currUser[0];
				String currPassword = currUser[1];
				if(currUsername.equals(username) && currPassword.equals(password)) {
					myFileSystem.loadOnlineUsersData();
					User user = new User(username, password, myNetworkLink.getNetworkAddress());
					Vector<User> onlineUsers = myFileSystem.getOnlineUsersData();
					if(Validator.isNotContainsByName(onlineUsers, user.getUsername()))
					return user;
					else {
						System.out.println("Please logout your account first");
						console.scan();
						return null;
					}
				}
			}
		}
		System.out.println("There are no matching accounts");
		return null;
	}
	
}
