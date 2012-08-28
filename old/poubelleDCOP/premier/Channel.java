package negotiation.dcopframework.daj;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;

import darx.DarxCommInterface;
import darx.DarxException;
import darx.RemoteTask;
import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.kernel.DimaXCommunicationComponent;

public class Channel implements InChannel, OutChannel  {

	// sender and receiver node and message selector
	private AgentIdentifier sender;
	private AgentIdentifier receiver;
	private LinkedList<Message> mailBox;
	

	protected  DarxCommInterface comm;
    protected  DiCOPmaXNodeTask task;
	
	
	
	
	
	@Override
	public void send(Message msg) {

		RemoteTask remote = null;
		try {
			remote = this.task.findTask(receiver.toString());
		}
		catch(final DarxException e) {
			System.out.println("Getting " + receiver + " from nameserver failed : " + e);
			return;
		}

		if(remote != null)
			this.comm.sendAsyncMessage(remote, (Serializable) msg);
		else
			throw new RuntimeException(this+" Echec de l'envoi du message"+msg);
		
	}

	@Override
	public Message receive() {
		return mailBox.pop();
	}

	@Override
	public Message receive(int n) {
		//Toujours utilisé avec 1!!!!
		return mailBox.pop();
	}
	
	// --------------------------------------------------------------------------
	// return vector of messages in channel
	// --------------------------------------------------------------------------
	public Message[] getMessages() {
		return (Message[]) mailBox.toArray();
	}
}
