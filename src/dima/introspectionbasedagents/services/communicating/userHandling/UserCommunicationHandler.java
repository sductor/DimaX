package dima.introspectionbasedagents.services.communicating.userHandling;

import java.io.IOException;
import java.util.Scanner;

import dima.introspectionbasedagents.services.communicating.execution.SystemCommunicationService;

public class UserCommunicationHandler extends SystemCommunicationService{


	/**
	 * 
	 */
	private static final long serialVersionUID = -6785482451235166705L;
	public boolean useGui=false;




	public void communicateWithUSerWithGui(final boolean useGui) {
		this.useGui = useGui;
	}


	public void sendToUser(final String text){
		if (this.useGui){
			try {
				//				this.execute("zenity  --info --text="+text);
				//				this.execute("kdialog --passivepopup '"+text+"' &");
				this.execute("notify-send "+text);

			} catch (final ErrorOnProcessExecutionException e) {
				e.printStackTrace();
			}
		} else {
			System.out.println(text);
		}
	}


	public String receiveFromUSer(final String askingText){
		if (this.useGui){
			try {
				return this.execute("zenity  --entry ");
			} catch (final ErrorOnProcessExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			System.out.println(askingText+" : ");
			return new Scanner(System.in).nextLine();
		}
	}

	//Should not be stored in a variable or the variable should be volatile
	public String receiveHiddenFromUSer(final String askingText){
		if (this.useGui){
			try {
				return this.execute("zenity --info "+askingText+" --password ");
			} catch (final ErrorOnProcessExecutionException e) {
				e.printStackTrace();
				return null;
			}
		} else {
			try {
				return String.valueOf(PasswordField.getPassword(System.in, askingText+" : "));
			} catch (final IOException e) {
				e.printStackTrace();
				return null;
			}
		}
	}
}
