package dima.introspectionbasedagents.services.communicating.xmppcommunication;

import java.io.File;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import dima.introspectionbasedagents.services.loggingactivity.LogService;

public class GTalkCommunicationCompetence extends JabberCommunicationCompetence{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2321334450789569922L;
	final private String loginname;
	private final Collection<String> friends=new HashSet<String>();
	File log;
	//
	// Constructors
	//

	public GTalkCommunicationCompetence(final String loginname) {
		super();
		this.loginname = loginname;
	}


	//
	// Method
	//

	@Override
	public boolean connect(final String[] args) {
		final boolean b = super.connect(new String[]{"talk.google.com", "5222", "gmail.com",this.loginname+"@gmail.com","false"});
		this.friends.add(this.receiveHiddenFromUSer("new friend?"));
		//		friends.add(receiveFromUSer("new friend?"));
		this.log = new File("/local/ductors/__Messages.log");
		LogService.logOnFile(this.log, "\n\n restarting \n\n", false, false);
		this.handlePresenceChangement(this.connection.getRoster().getPresence(this.friends.iterator().next()));
		this.communicateWithUSerWithGui(true);
		return b;
	}

	@Override
	public void setStatusToServer(final boolean available, final Presence.Mode mode, final String status) {
		this.connection.sendPacket(this.getClientRequestForStatusList());
		this.connection.sendPacket(this.getClientRequest4updateStatus(available, mode, status));
		this.connection.sendPacket(this.getClientRequest4sendPresence(mode));
	}

	@Override
	public void handlePresenceChangement(final Presence presence) {
		if (this.friends.contains(StringUtils.parseBareAddress(presence.getFrom()))){
			final String text = "on "+new Date()+" new presence : "+presence.getFrom()+" : "+presence;
			System.out.println(new Date()+" "+presence);
			LogService.logOnFile(this.log, text, false, false);
			//			sendToUser(text);
		}
	}

	/*
	 * Google specific
	 */

	public void setBlocked(final String user){
		this.connection.sendPacket(this.getClientRequest4setTAttribute(user,"B"));
	}

	public void setNeverShow(final String user){
		this.connection.sendPacket(this.getClientRequest4setTAttribute(user,"H"));
	}

	public void setAlwaysShow(final String user){
		this.connection.sendPacket(this.getClientRequest4setTAttribute(user,"P"));
	}

	public void setAuto(final String user){
		this.connection.sendPacket(this.getClientRequest4setNoTAttribute(user));
	}

	public void requestForExtendedFileAttribute(){
		this.connection.sendPacket( this.getClientRequestForExtendedFileAttribute());
		//ajout d'un paquet listener
	}

	//
	// Packet primitives
	//

	@SuppressWarnings("unused")
	private Packet getServiceDiscoveryRequest(){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<iq type='get' to='gmail.com'>";
				output += "<query xmlns='http://jabber.org/protocol/disco#info'/>";
				output += "</iq>";
				return output;
			}
		};
	}

	/*
	 * Shared status
	 * https://developers.google.com/talk/jep_extensions/shared_status?hl=fr
	 * http://community.igniterealtime.org/thread/41274
	 */

	private Packet getClientRequestForStatusList(){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<iq type='get' to='"+GTalkCommunicationCompetence.this.loginname+"@gmail.com' id='"+Packet.nextID()+"' >";
				output += "<query xmlns='google:shared-status' version='2'/>";
				output += "</iq>";
				return output;
			}
		};
	}

	private Packet getClientRequest4updateStatus(final boolean available, final Presence.Mode mode, final String status){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<iq type='set' to='"+GTalkCommunicationCompetence.this.loginname+"@gmail.com' id='"+Packet.nextID()+"'>";
				output += "<query xmlns='google:shared-status' version='2'>";
				output += "<status>"+status+"</status>";
				output += "<show>"+mode+"</show>";
				output += "<invisible value='"+!available+"'/>";
				output += "</query>";
				output += "</iq>";
				return output;
			}
		};
	}

	private Packet getClientRequest4sendPresence(final Presence.Mode mode){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<presence>";
				output += "<show>"+mode+"</show>";
				output += "<status>blabla</status>";
				//				output += "<c xmlns='http://jabber.org/protocol/caps'";
				//				output += " node='http://www.google.com/xmpp/client/caps'";
				//				output += " ver='0.92'/>";
				output += "</presence>";
				return output;
			}
		};
	}

	/*
	 * Extended file attribute
	 * https://developers.google.com/talk/jep_extensions/roster_attributes?hl=fr
	 */

	private Packet getClientRequestForExtendedFileAttribute(){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<iq type='get' from='"+GTalkCommunicationCompetence.this.loginname+"@gmail.com'  id='"+Packet.nextID()+"'>";
				output += "<query xmlns='jabber:iq:roster' xmlns:gr='google:roster' gr:ext='2'/>";
				output += "</iq>";
				return output;
			}
		};
	}

	private Packet getClientRequest4setTAttribute(final String user, final String value){
		assert value.equals("B") || value.equals("P") || value.equals("H");
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<iq type='set' id='"+Packet.nextID()+"'>";
				output += "<query xmlns='jabber:iq:roster' xmlns:gr='google:roster' gr:ext='2'>";
				output += "<item jid='"+user+"' gr:t='"+value+"'/>";
				output += "</query>";
				output += "</iq>";
				return output;
			}
		};
	}
	private Packet getClientRequest4setNoTAttribute(final String user){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";
				output += "<iq type='set' id='"+Packet.nextID()+"'>";
				output += "<query xmlns='jabber:iq:roster' xmlns:gr='google:roster' gr:ext='2'>";
				output += "<item jid='"+user+"' gr:t=''/>";
				output += "</query>";
				output += "</iq>";
				return output;
			}
		};
	}
	//
	// Main
	//

	public static void main(final String[] args) throws Exception{
		final GTalkCommunicationCompetence com = new GTalkCommunicationCompetence("ductor.sylvain");
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
		//				Connection.DEBUG_ENABLED = true;
		final boolean ok =com.connect(null);
		System.out.println("connected? "+ok);
		com.setStatusToServer(false, Presence.Mode.xa, "");
		//		com.setBlocked("coolhibou@gmail.com");
		//		com.setAuto("coolhibou@gmail.com");

		final boolean isRunning = true;
		while (isRunning) {
			Thread.sleep(50);
		}
	}
}
