package dima.introspectionbasedagents.services.communicating.xmppcommunication;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smack.util.StringUtils;

import dima.introspectionbasedagents.services.loggingactivity.LogMonologue;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public class GTalkCommunicationCompetence extends JabberCommunicationCompetence{

	final private String loginname;
	private Collection<String> friends=new HashSet<String>();
	File log;
	//
	// Constructors
	//

	public GTalkCommunicationCompetence(String loginname) {
		super();
		this.loginname = loginname;
	}


	//
	// Method
	//

	@Override
	public boolean connect(String[] args) {
		boolean b = super.connect(new String[]{"talk.google.com", "5222", "gmail.com",loginname+"@gmail.com","false"});
		friends.add(receiveHiddenFromUSer("new friend?"));
//		friends.add(receiveFromUSer("new friend?"));
		log = new File("/local/ductors/__Messages.log");
		handlePresenceChangement(connection.getRoster().getPresence(friends.iterator().next()));
		return b;
	}

	@Override
	public void setStatusToServer(boolean available, Presence.Mode mode, String status) {
		connection.sendPacket(getClientRequestForStatusList());
		connection.sendPacket(getClientRequest4updateStatus(available, mode, status));
		connection.sendPacket(getClientRequest4sendPresence(mode));
	}

	@Override
	public void handlePresenceChangement(Presence presence) {
		if (friends.contains(StringUtils.parseBareAddress(presence.getFrom()))){
			String text = "on "+new Date()+" new presence : "+presence.getFrom()+" : "+presence;
//			System.out.println(text);
			LogService.logOnFile(log, text, false, false);
		}
	}

	/*
	 * Google specific 
	 */

	public void setBlocked(String user){
		connection.sendPacket(getClientRequest4setTAttribute(user,"B"));
	}

	public void setNeverShow(String user){
		connection.sendPacket(getClientRequest4setTAttribute(user,"H"));
	}

	public void setAlwaysShow(String user){
		connection.sendPacket(getClientRequest4setTAttribute(user,"P"));
	}

	public void setAuto(String user){
		connection.sendPacket(getClientRequest4setNoTAttribute(user));
	}
	
	public void requestForExtendedFileAttribute(){
		connection.sendPacket( getClientRequestForExtendedFileAttribute());
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
				output += "<iq type='get' to='"+loginname+"@gmail.com' id='"+Packet.nextID()+"' >";
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
				output += "<iq type='set' to='"+loginname+"@gmail.com' id='"+Packet.nextID()+"'>";
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
				output += "<iq type='get' from='"+loginname+"@gmail.com'  id='"+Packet.nextID()+"'>";
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

	public static void main(String[] args) throws Exception{
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
		boolean ok =com.connect(null);
		System.out.println("connected? "+ok);
		com.setStatusToServer(false, Presence.Mode.xa, "");
//		com.setBlocked("coolhibou@gmail.com");
//		com.setAuto("coolhibou@gmail.com");

		boolean isRunning = true;
		while (isRunning) {
			Thread.sleep(50);
		}
	}
}
