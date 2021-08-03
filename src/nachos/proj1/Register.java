package nachos.proj1;

import java.util.Vector;

import nachos.proj1.models.User;
import nachos.proj1.utils.Util;
import nachos.proj1.utils.Validator;

public class Register {

	private static MyConsole console = MyConsole.getInstance();
	private static MyFileSystem myFileSystem = MyFileSystem.getInstance();
	
	public Register() {
		
	}
	
	public static void doRegister() {
		String username,password;
		Vector<User> users;
		do {
			Util.clearScreen(30);
			users = myFileSystem.getAllUsersData();
			console.print("Input username: ");
			username = console.scan();
			console.print("Input password: ");
			password = console.scan();
		} while (!Validator.isNotContainsByName(users, username) || 
				!Validator.isValidRegisterFormat(username, password));
		String saveUserFormat = username + MyFileSystem.DELIMITER + password;
		
		myFileSystem.appendFile(MyFileSystem.USER_FILE_NAME, saveUserFormat);
	}

}
