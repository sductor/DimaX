package dima.introspectionbasedagents.services.communicating.xmppcommunication;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManager;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.PacketCollector;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.PrivacyList;
import org.jivesoftware.smack.PrivacyListManager;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.NotFilter;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.IQ;
import org.jivesoftware.smack.packet.IQ.Type;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.packet.PrivacyItem;
import org.jivesoftware.smack.packet.PrivacyItem.PrivacyRule;
import org.jivesoftware.smack.provider.IQProvider;
import org.jivesoftware.smack.util.StringUtils;
import org.xmlpull.v1.XmlPullParser;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.acquaintance.AcquaintancesHandler;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;
import dima.introspectionbasedagents.services.communicating.AsynchronousCommunicationComponent;
import dima.introspectionbasedagents.services.communicating.execution.SystemCommunicationService;

public class JabberCommunicationCompetence 
extends SystemCommunicationService 
implements AsynchronousCommunicationComponent, AcquaintancesHandler{

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

		public AgentIdentifier getSender(){
			return new AgentName(encapsulatedMessage.getFrom());
		}

		public String getContent(){
			return encapsulatedMessage.getBody(encapsulatedMessage.getLanguage());
		}
	}

	Map<AgentIdentifier,Presence> acquaintances;
	XMPPConnection connection;
	String me;

	HashMap<String, String> chatThreads = new HashMap<String, String>();
	MessageListener myMessageListener = new MessageListener(){

		@Override
		public void processMessage(Chat chat,
				org.jivesoftware.smack.packet.Message arg1) {
			receive(new JabberMessage(arg1));
		}
	};

	@Override
	public boolean isConnected(String[] args) {
		return connection.isConnected();
	}

	@Override
	public boolean connect(String[] args) {
		// Create the configuration for this new connection
		//		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		ConnectionConfiguration config = new ConnectionConfiguration(args[0], new Integer(args[1]), args[2]);
		config.setCompressionEnabled(true);
//		SASLAuthentication.supportSASLMechanism("PLAIN", 0);
		config.setSASLAuthenticationEnabled(true);
		config.setSendPresence(new Boolean(args[4]));
		connection = new XMPPConnection(config);


		try {
			// Connect to the server
			connection.connect();	
			// Log into the server
			String pass = this.execute("zenity  --password ");
			connection.login(args[3], pass, "smackThat");

			RosterListener rl = new RosterListener() {

				public void entriesAdded(Collection<String> addresses) {
					handleNewAcquaintances(addresses);
					for (String user : addresses){
						acquaintances.put(new AgentName(user), connection.getRoster().getPresence(user));
					}
				}

				public void entriesDeleted(Collection<String> addresses) {
					handleRemovedAcquaintances(addresses);
					for (String user : addresses){
						acquaintances.remove(new AgentName(user));
					}
				}

				public void entriesUpdated(Collection<String> addresses) {
									handleUpdatedAcquaintances(addresses);			
					for (String user : addresses){
						acquaintances.put(new AgentName(user), connection.getRoster().getPresence(user));
					}
				}



				public void presenceChanged(Presence presence) {
					handlePresenceChangement(presence);
				}



			};
			connection.getRoster().addRosterListener(rl);

			connection.getChatManager().addChatListener(new ChatManagerListener() {

				@Override
				public void chatCreated(Chat chat, boolean createdLocally) {
					chat.addMessageListener(myMessageListener);

				}
			});
			me = connection.getUser();

		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		} catch (ErrorOnProcessExecutionException e) {
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

	//
	// Sending
	//	

	@Override
	public void sendMessage(AgentIdentifier id, AbstractMessageInterface a) {
		Chat chat;
		if (chatThreads.containsKey(id.toString()))
			chat = connection.getChatManager().getThreadChat(chatThreads.get(id.toString()));
		else
			chat = connection.getChatManager().createChat(id.toString(), myMessageListener);
		try {
			chat.sendMessage(a.getContent().toString());
		} catch (XMPPException e) {}
	}

	@Override
	public void receive(AbstractMessageInterface a) {
		try {
			this.execute("zenity  --text "+"From : "+a.getSender()+" : "+a.getContent().toString());
		} catch (ErrorOnProcessExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("From : "+a.getSender()+" : "+a.getContent().toString());

	}

	//
	// Acquaintances
	//

	@Override
	public Collection<AgentIdentifier> getAcquaintances() {
		return acquaintances.keySet();
	}

	@Override
	public boolean addAcquaintance(AgentIdentifier id) {
		try {
			connection.getRoster().createEntry(id.toString(), id.toString(), null);
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean removeAcquaintance(AgentIdentifier id) {
		try {
			connection.getRoster().removeEntry(connection.getRoster().getEntry(id.toString()));
		} catch (XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	/*
	 * 
	 */
	
	public void handleNewAcquaintances(Collection<String> addresses) {
		System.out.println("new entries added "+addresses);
	}
	
	public void handleRemovedAcquaintances(Collection<String> addresses) {
		System.out.println("new entries reùoved "+addresses);
	}

	public void handleUpdatedAcquaintances(Collection<String> addresses) {
		System.out.println("new entries updated "+addresses);
	}
	
	public void handlePresenceChangement(Presence presence) {
		System.out.println("on "+new Date()+" new presence : "+presence.getFrom()+" : "+presence);
	}

	//
	// Stauts management
	//

	public void setStatusToServer(boolean available, Presence.Mode mode,String status) {
		setStatusTo(null, available,mode,status);
	}

	public void setStatusToEveryone(boolean available,Presence.Mode mode,  String status){
		Collection<RosterEntry> entries = connection.getRoster().getEntries();
		for (RosterEntry entry : entries)  {
			setStatusTo(entry.getUser(), available,mode,status);
		}
	}

	protected void setStatusTo(String user, boolean available, Presence.Mode mode, String status) {
		//		System.out.println("setting unavaliable to "+user);

		Presence.Type type = available? Presence.Type.available: Presence.Type.unavailable;
		Presence presence = new Presence(type);

		presence.setStatus(status);
		presence.setMode(mode);
		if (user!=null){
			presence.setTo(user);
		}
		
		connection.sendPacket(presence);
	}

	//
	// Main
	//
	
	public static void main(String[] args) throws Exception{
		final JabberCommunicationCompetence com = new JabberCommunicationCompetence();
		Runtime.getRuntime().addShutdownHook(
				new Thread(){	
					@Override
					public void run() {
						System.out.println("disconnected!!!");
						System.out.flush();
						com.disconnect(null);
					};
				}
				);
		Connection.DEBUG_ENABLED = true;
		boolean ok =com.connect(new String[]{"talk.google.com", "5222", "gmail.com","coolhibou","true"});
		System.out.println("connected? "+ok);

		boolean isRunning = true;
		while (isRunning) {
			Thread.sleep(50);
		}

	}
}

//com.setGlobalInvisible(true);
//			setPrivacy("invisible", getPrivacyForEveryone());
//			setStatus(true, "yo");
//		com.setGlobalInvisible(true);
//com.setStatus(true, "maha");
//com.setStatusToEveryone(true);
//		com.observeChange();

//		System.out.println("!!!"+com.me+" "
//				+com.connection.getRoster().getPresence(com.me)+" "
//				+com.connection.getRoster().getPresence(com.me).getStatus());
//		com.setStatus(false, "yoo");
//		System.out.println("!!!"+com.me+" "
//				+com.connection.getRoster().getPresence(com.me)+" "
//				+com.connection.getRoster().getPresence(com.me).getStatus());

//		Message a = new Message();
//		a.setContent("yohohohoho!!!!!! =D je suis un agent de sylvain ");
//		//		AgentIdentifier receiver = new AgentName("violeta.serrano.garcia@gmail.com");
//		AgentIdentifier receiver = new AgentName("ductor.sylvain@gmail.com");
////		com.sendMessage(receiver, a);
//		receiver=new AgentName("coolhibou@gmail.com");
////		com.sendMessage(receiver, a);
//<iq type='set' to='romeo@gmail.com/orchard' id='ss-2'>
//<query xmlns='google:shared-status' version='2'>
//<status>yo</status>
//<show>default</show>
//<status-list show='default'></status-list>
//<status-list show='dnd'></status-list>
//<invisible value='true'/>
//</query>
//</iq>
//<iq type='set' to='romeo@gmail.com/orchard' id='ss-2'>
//<query xmlns='google:shared-status' version='2'>
//  <status>Juliet's here</status>
//  <show>default</show>
//  <status-list show='default'>
//      <status>Juliet's here</status>
//      <status>Pining away</status>
//      <status>Wherefor indeed</status>
//      <status>Thinking about the sun</status>
//  </status-list>
//  <status-list show='dnd'>
//      <status>Chilling with Mercutio</status>
//      <status>Visiting the monk</status>
//  </status-list>
//  <invisible value='false'/>
//</query>
//</iq>

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





