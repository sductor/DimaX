package dima.introspectionbasedagents.services.library.xmppcommunication;

import java.util.Collection;
import java.util.HashMap;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.core.communicating.CommunicationCompetence;

public class JabberCommunicationCompetence implements CommunicationCompetence{

	class JabberMessage extends Message {
		final org.jivesoftware.smack.packet.Message encapsulatedMessage;

		public JabberMessage(
				org.jivesoftware.smack.packet.Message encapsulatedMessage) {
			super();
			this.encapsulatedMessage = encapsulatedMessage;
		}

		public org.jivesoftware.smack.packet.Message getEncapsulatedMessage() {
			return encapsulatedMessage;
		}

	}

	XMPPConnection connection;
	HashMap<String, String> chatThreads = new HashMap<String, String>();
	MessageListener myListener = new MessageListener(){

		@Override
		public void processMessage(Chat chat,
				org.jivesoftware.smack.packet.Message arg1) {
			receiveAsynchronousMessage(new JabberMessage(arg1));
		}
	};
	Roster roster;

	@Override
	public boolean isConnected(String[] args) {
		return connection.isConnected();
	}

	@Override
	public boolean connect(String[] args) {
		// Create the configuration for this new connection
		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		//		config.setCompressionEnabled(true);
		//		config.setSASLAuthenticationEnabled(true);

		connection = new XMPPConnection(config);
		try {
			// Connect to the server
			connection.connect();
			// Log into the server
			connection.login("ductor.sylvain@gmail.com", "", "");
			connection.getChatManager().addChatListener(new ChatManagerListener() {

				@Override
				public void chatCreated(Chat chat, boolean createdLocally) {
					chat.addMessageListener(myListener);

				}
			});
			roster = connection.getRoster();
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean disconnect(String[] args) {
		// Disconnect from the server
		connection.disconnect();
		return true;
	}

	/*
	 * 
	 */
	

	@Override
	public Collection<AgentIdentifier> getAcquaintances() {
		return null;
	}

	@Override
	public boolean addAcquaintance(AgentIdentifier id) {
		return false;
	}

	@Override
	public boolean removeAcquaintance(AgentIdentifier id) {
		return false;
	}
	
	/*
	 * 
	 */
	
	@Override
	public Message sendSynchronousMessage(Message a) {
		throw new RuntimeException("unavalaible");
	}

	@Override
	public boolean sendASynchronousMessage(Message a) {
		Chat chat;
		if (chatThreads.containsKey(a.getReceiver()))
			chat = connection.getChatManager().getThreadChat(chatThreads.get(a.getReceiver()));
		else
			chat = connection.getChatManager().createChat(a.getReceiver().toString(), myListener);
		try {
			chat.sendMessage(a.getContent().toString());
		} catch (XMPPException e) {
			return false;
		}
		return true;
	}

	@Override
	public void receiveAsynchronousMessage(Message a) {
		System.out.println(a.getContent().toString());

	}

	public static void main(String[] args){
		JabberCommunicationCompetence com = new JabberCommunicationCompetence();
		boolean ok =com.connect(null);
		Collection<RosterEntry> entries = com.roster.getEntries();
		for (RosterEntry entry : entries) {
			System.out.print("entry "+entry+" is "+com.roster.getPresence(entry.getUser()));
			if (com.roster.getPresence(entry.getUser()).isAvailable())
		    System.out.print(entry+"!!!!!!!!!!!!!!!!!!!!!!!!!!!!\n");
			else 
				System.out.print("\n");
		}
		System.out.println(ok);
		Message a = new Message();
		a.setContent("yohohohoho!!!!!! =D c mes agent qui te parlent depuis ma plateforme!!! ++");
		a.setReceiver("ductor.sylvain@gmail.com");
		ok =com.sendASynchronousMessage(a);
		System.out.println(ok+" "+com.chatThreads);
		
	}

}



//1- create connection
//
//public boolean openConnection() {
//    ConnectionConfiguration connectionConfiguration = new ConnectionConfiguration("talk.google.com", 5222, "mail.google.com");
//    this.connection = new XMPPConnection(connectionConfiguration);
//    try {
//        this.connection.connect();
//    } catch (XMPPException e) {
//        // TODO: Send Error Information To Programmer's Email Address
//    }
//    if(this.connection.isConnected()) {
//        this.roster = this.connection.getRoster();
//        this.roster.addRosterListener(new RosterListener() {
//            public void entriesAdded(Collection<String> addresses) {}
//            public void entriesDeleted(Collection<String> addresses) {}
//            public void entriesUpdated(Collection<String> addresses) {}
//            public void presenceChanged(Presence presence) {}
//        });
//        return true;
//    }
//    return false;
//}
//2- login
//
//public boolean login(String jid, String password) {
//    try { 
//        this.connection.login(jid, password, "smack");
//    } catch (XMPPException e) {
//        // TODO: Send Error Information To Programmer's Email Address
//    }
//    if(this.connection.isAuthenticated()) return true;
//    return false;
//}
//3- buddy list
//
//public void buddiesList() {
//    Collection<RosterEntry> rosterEntries = this.roster.getEntries();
//    for(RosterEntry rosterEntry: rosterEntries) {
//        System.out.println(rosterEntry.username() + " === " + this.roster.getPresence(rosterEntry.getUser()));
//    }
//}
//4- implementation
//
//public static void main(String args[]) {
//    IMService imService = new IMService();
//    imService.openConnection();
//    imService.login("google account", "password");
//    imService.buddiesList();
//}





