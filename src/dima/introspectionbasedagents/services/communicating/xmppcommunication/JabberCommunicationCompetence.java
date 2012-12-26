package dima.introspectionbasedagents.services.communicating.xmppcommunication;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.jivesoftware.smack.Chat;
import org.jivesoftware.smack.ChatManagerListener;
import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.ConnectionConfiguration;
import org.jivesoftware.smack.MessageListener;
import org.jivesoftware.smack.RosterEntry;
import org.jivesoftware.smack.RosterListener;
import org.jivesoftware.smack.SASLAuthentication;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.packet.Presence;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.services.acquaintance.AcquaintancesHandler;
import dima.introspectionbasedagents.services.communicating.AbstractMessageInterface;
import dima.introspectionbasedagents.services.communicating.AsynchronousCommunicationComponent;
import dima.introspectionbasedagents.services.communicating.userHandling.UserCommunicationHandler;

public class JabberCommunicationCompetence
extends UserCommunicationHandler
implements AsynchronousCommunicationComponent, AcquaintancesHandler{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6010232479173410332L;

	class JabberMessage extends Message {
		/**
		 * 
		 */
		private static final long serialVersionUID = 5769271549165480502L;
		final org.jivesoftware.smack.packet.Message encapsulatedMessage;

		public JabberMessage(
				final org.jivesoftware.smack.packet.Message encapsulatedMessage) {
			super();
			this.encapsulatedMessage = encapsulatedMessage;
		}

		public org.jivesoftware.smack.packet.Message getEncapsulatedMessage() {
			return this.encapsulatedMessage;
		}

		@Override
		public AgentIdentifier getSender(){
			return new AgentName(this.encapsulatedMessage.getFrom());
		}

		@Override
		public String getContent(){
			return this.encapsulatedMessage.getBody(this.encapsulatedMessage.getLanguage());
		}
	}

	Map<AgentIdentifier,Presence> acquaintances;
	XMPPConnection connection;

	HashMap<String, String> chatThreads = new HashMap<String, String>();
	final MessageListener myMessageListener = new MessageListener(){

		@Override
		public void processMessage(final Chat chat,
				final org.jivesoftware.smack.packet.Message arg1) {
			JabberCommunicationCompetence.this.receive(new JabberMessage(arg1));
		}
	};

	@Override
	public boolean isConnected(final String[] args) {
		return this.connection.isConnected();
	}

	@Override
	public boolean connect(final String[] args) {
		// Create the configuration for this new connection
		//		ConnectionConfiguration config = new ConnectionConfiguration("talk.google.com", 5222, "gmail.com");
		final ConnectionConfiguration config = new ConnectionConfiguration(args[0], new Integer(args[1]), args[2]);
		config.setCompressionEnabled(true);
//				SASLAuthentication.supportSASLMechanism("PLAIN", 0);
//		config.setSASLAuthenticationEnabled(true);
		config.setSendPresence(new Boolean(args[4]));
		this.connection = new XMPPConnection(config);


		try {
			// Connect to the server
			this.connection.connect();
			// Log into the server
			final String pass = this.receiveHiddenFromUSer("Provide password for "+args[3]);
			this.connection.login(args[3], pass, "smackThat");

			this.setRosterListener();

			//			setChatManager();

		} catch (final XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	private void setChatManager() {
		this.connection.getChatManager().addChatListener(new ChatManagerListener() {

			@Override
			public void chatCreated(final Chat chat, final boolean createdLocally) {
				chat.addMessageListener(JabberCommunicationCompetence.this.myMessageListener);

			}
		});
	}

	private void setRosterListener() {
		final RosterListener rl = new RosterListener() {

			@Override
			public void entriesAdded(final Collection<String> addresses) {
				JabberCommunicationCompetence.this.handleNewAcquaintances(addresses);
				for (final String user : addresses){
					JabberCommunicationCompetence.this.acquaintances.put(new AgentName(user), JabberCommunicationCompetence.this.connection.getRoster().getPresence(user));
				}
			}

			@Override
			public void entriesDeleted(final Collection<String> addresses) {
				JabberCommunicationCompetence.this.handleRemovedAcquaintances(addresses);
				for (final String user : addresses){
					JabberCommunicationCompetence.this.acquaintances.remove(new AgentName(user));
				}
			}

			@Override
			public void entriesUpdated(final Collection<String> addresses) {
				JabberCommunicationCompetence.this.handleUpdatedAcquaintances(addresses);
				for (final String user : addresses){
					JabberCommunicationCompetence.this.acquaintances.put(new AgentName(user), JabberCommunicationCompetence.this.connection.getRoster().getPresence(user));
				}
			}



			@Override
			public void presenceChanged(final Presence presence) {
				JabberCommunicationCompetence.this.handlePresenceChangement(presence);
			}



		};
		this.connection.getRoster().addRosterListener(rl);
	}

	@Override
	public boolean disconnect(final String[] args) {
		// Disconnect from the server
		this.connection.disconnect();
		return true;
	}

	//
	// Sending
	//

	@Override
	public void sendMessage(final AgentIdentifier id, final AbstractMessageInterface a) {
		Chat chat;
		if (this.chatThreads.containsKey(id.toString())) {
			chat = this.connection.getChatManager().getThreadChat(this.chatThreads.get(id.toString()));
		} else {
			chat = this.connection.getChatManager().createChat(id.toString(), this.myMessageListener);
		}
		try {
			chat.sendMessage(a.getContent().toString());
		} catch (final XMPPException e) {}
	}

	@Override
	public void receive(final AbstractMessageInterface a) {
		try {
			this.execute("zenity  --text "+"From : "+a.getSender()+" : "+a.getContent().toString());
		} catch (final ErrorOnProcessExecutionException e) {
			e.printStackTrace();
		}
		System.out.println("From : "+a.getSender()+" : "+a.getContent().toString());

	}

	//
	// Acquaintances
	//

	@Override
	public Collection<AgentIdentifier> getAcquaintances() {
		return this.acquaintances.keySet();
	}

	@Override
	public boolean addAcquaintance(final AgentIdentifier id) {
		try {
			this.connection.getRoster().createEntry(id.toString(), id.toString(), null);
		} catch (final XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	@Override
	public boolean removeAcquaintance(final AgentIdentifier id) {
		try {
			this.connection.getRoster().removeEntry(this.connection.getRoster().getEntry(id.toString()));
		} catch (final XMPPException e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/*
	 * 
	 */

	public void handleNewAcquaintances(final Collection<String> addresses) {
		System.out.println("new entries added "+addresses);
	}

	public void handleRemovedAcquaintances(final Collection<String> addresses) {
		System.out.println("new entries reùoved "+addresses);
	}

	public void handleUpdatedAcquaintances(final Collection<String> addresses) {
		System.out.println("new entries updated "+addresses);
	}

	public void handlePresenceChangement(final Presence presence) {
		System.out.println("on "+new Date()+" new presence : "+presence.getFrom()+" : "+presence);
	}

	//
	// Stauts management
	//

	public void setStatusToServer(final boolean available, final Presence.Mode mode,final String status) {
		this.setStatusTo(null, available,mode,status);
	}

	public void setStatusToEveryone(final boolean available,final Presence.Mode mode,  final String status){
		final Collection<RosterEntry> entries = this.connection.getRoster().getEntries();
		for (final RosterEntry entry : entries)  {
			this.setStatusTo(entry.getUser(), available,mode,status);
		}
	}

	protected void setStatusTo(final String user, final boolean available, final Presence.Mode mode, final String status) {
		//		System.out.println("setting unavaliable to "+user);

		final Presence.Type type = available? Presence.Type.available: Presence.Type.unavailable;
		final Presence presence = new Presence(type);

		presence.setStatus(status);
		presence.setMode(mode);
		if (user!=null){
			presence.setTo(user);
		}

		this.connection.sendPacket(presence);
	}

	//
	// Main
	//

	public static void main(final String[] args) throws Exception{
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
		final boolean ok =com.connect(new String[]{"chat.facebook.com","5222","facebook.com","ductor.sylvain","true"});
		//		boolean ok =com.connect(new String[]{"talk.google.com", "5222", "gmail.com","coolhibou","true"});
		System.out.println("connected? "+ok);

		final boolean isRunning = true;
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





