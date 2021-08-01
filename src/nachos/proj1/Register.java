package nachos.proj1;

public class Register {

	private static MyConsole console = MyConsole.getInstance();
	private static MyFileSystem myFileSystem = MyFileSystem.getInstance();
	
	public Register() {
		
	}
	
	public static void doRegister() {
		String username,password;
		console.print("Input username: ");
		username = console.scan();
		console.print("Input password: ");
		password = console.scan();
		String saveUserFormat = username + MyFileSystem.DELIMITER + password;
		
		myFileSystem.appendFile(MyFileSystem.USER_FILE_NAME, saveUserFormat);
	}

}
