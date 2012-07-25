package dima.introspectionbasedagents.services.communicating.xmppcommunication;

import org.jivesoftware.smack.Connection;
import org.jivesoftware.smack.packet.Packet;
import org.jivesoftware.smack.packet.Presence;
import org.jivesoftware.smackx.pubsub.PresenceState;
import org.w3c.rdf.model.SetModel;

public class GTalkCommunicationCompetence extends JabberCommunicationCompetence{


	public boolean invisible;
	final private String loginname;
	private String mode;
	private String status;

	//
	// Constructors
	//

	public GTalkCommunicationCompetence(String loginname) {
		super();
		this.loginname = loginname;
	}

	//
	// Accessors
	//

	public void setInvisible(boolean invisible) {
		this.invisible = invisible;
	}

	public void setMode(String mode) {
		this.mode = mode;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	//
	// Method
	//

	@Override
	public boolean connect(String[] args) {
		boolean b = super.connect(new String[]{"talk.google.com", "5222", "gmail.com",loginname+"@gmail.com","false"});
		return b;
	}



	public void setStatusToServer(boolean available, Presence.Mode mode, String status) {
		setMode(mode.toString());
		setStatus(status);
		setInvisible(!available);
		connection.sendPacket(getClientRequestForStatusList());
		connection.sendPacket(updateStatus());
		connection.sendPacket(sendPresence());
	}

	/*
	 * 
	 */

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

	private Packet updateStatus(){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";	
				output  = "<iq type='set' to='"+loginname+"@gmail.com' id='"+Packet.nextID()+"'>";
				output += "<query xmlns='google:shared-status' version='2'>";
				output += "<status>"+status+"</status>";
				output += "<show>"+mode+"</show>";
				output += "<invisible value='"+invisible+"'/>";
				output += "</query>";
				output += "</iq>";
				return output;
			}
		};
	}

	private Packet sendPresence(){
		return new Packet() {

			@Override
			public String toXML() {
				String output = "";	
				output  = "<presence>";
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

	//
	// Main
	//

	public static void main(String[] args) throws Exception{
		final GTalkCommunicationCompetence com = new GTalkCommunicationCompetence("coolhibou");
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
//		Connection.DEBUG_ENABLED = true;
		boolean ok =com.connect(null);
		System.out.println("connected? "+ok);
		com.setStatusToServer(false, Presence.Mode.available, "maha!");

		boolean isRunning = true;
		while (isRunning) {
			Thread.sleep(50);
		}

	}
}
