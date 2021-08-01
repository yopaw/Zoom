package nachos.proj1.interfaces;

import nachos.proj1.models.User;

public interface IState {
	public void getInputFromUser(User user, int input);
	public void getInputFromUser(User user, String input);
	public void printStateMenu(User user);
	public void changeState(User user,int input);
	public void changeState(User user,String input);
}
