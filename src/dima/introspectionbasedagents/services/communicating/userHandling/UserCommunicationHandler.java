package dima.introspectionbasedagents.services.communicating.userHandling;

import java.io.IOException;
import java.util.Scanner;

import dima.introspectionbasedagents.services.communicating.execution.SystemCommunicationService;

public class UserCommunicationHandler extends SystemCommunicationService{
	
	
	public boolean useGui=false;
	
	


	public void communicateWithUSerWithGui(boolean useGui) {
		this.useGui = useGui;
	}


	public void sendToUser(String text){
		if (useGui){
			try {
//				this.execute("zenity  --info --text="+text);
//				this.execute("kdialog --passivepopup '"+text+"' &");
				this.execute("notify-send "+text);

			} catch (ErrorOnProcessExecutionException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(text);
		}
	}
	

	public String receiveFromUSer(String askingText){
		if (useGui){
			try {
				return this.execute("zenity  --entry ");
			} catch (ErrorOnProcessExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println(askingText+" : ");
			return new Scanner(System.in).nextLine();
		}
	}
	
	//Should not be stored in a variable or the variable should be volatile
	public String receiveHiddenFromUSer(String askingText){
		if (useGui){
			try {
				return this.execute("zenity --info "+askingText+" --password ");
			} catch (ErrorOnProcessExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				return String.valueOf(PasswordField.getPassword(System.in, askingText+" : "));
			} catch (IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
