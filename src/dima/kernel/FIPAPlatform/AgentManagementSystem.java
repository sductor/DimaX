package dima.kernel.FIPAPlatform;

/**
 * Insert the type's description here.
 * Creation date: (28/04/02 13:04:14)
 * @author: Tarek JARRAYA
 */

import java.util.HashMap;

import dima.basicagentcomponents.AgentAddress;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.Message;





public class AgentManagementSystem extends dima.kernel.communicatingAgent.BasicCommunicatingAgent
{
	/**
	 *
	 */
	private static final long serialVersionUID = -5589071064597300072L;
	public static AgentManagementSystem DIMAams=null;
	public AgentManagementSystem() {
		super(new AgentName("DIMAams"));
		DIMAams =this;
	}
	public AgentManagementSystem(final AgentIdentifier newId) {
		super(newId);
		DIMAams =this;
	}
	public AgentManagementSystem(final AgentIdentifier newId, final HashMap al) {
		super(newId);
		DIMAams =this;
	}
	public AgentManagementSystem(final HashMap al) {
		super(new AgentName("DIMAams"));
		DIMAams =this;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/07/2002 13:12:28)
	 */
	public synchronized void forwardMessage(final Message m) {
		final String a = m.getReceiver().toString();
		AgentAddress ad;
		if ( this.aquaintances.containsKey(a))
		{ad = (AgentAddress) this.aquaintances.get(a);
		ad.receive(m);}
		else System.out.println(
				"From AMS : Message lost! \n"+m
				+"\n sender : "+m.getSender()
				+"\n receiver -------> "+m.getReceiver() 
				+ " address does not exit\n  ams known adress are ->"+this.aquaintances);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/07/2002 11:00:42)
	 * @return Gdima.kernel.aFIPAPlatform.AgentManagementSystem
	 */
	public static AgentManagementSystem getDIMAams() {
		initAMS();
		return DIMAams;
	}

	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 12:07:21)
	 */
	@Override
	public void processNextMessage() {
		final Message m=(Message)this.getMessage();
		this.forwardMessage(m);
	}
	public void register(final AgentAddress client) {
		this.addAquaintance(client);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/07/2002 11:00:42)
	 * @param newDIMAams Gdima.kernel.aFIPAPlatform.AgentManagementSystem
	 */
	static void setDIMAams(final AgentManagementSystem newDIMAams) {
		DIMAams = newDIMAams;
	}

	@Override
	public boolean isActive() {
		return true;
	}

	@Override
	public void step()
	{
		while (this.isActive())
			this.readAllMessages();
		//wwait(200);
	}
	public void unregister(final AgentIdentifier name) {
		this.aquaintances.remove(name);
	}

	public static void main(final String args[])
	{final AgentManagementSystem ams = new AgentManagementSystem();
	ams.activate();}

	public static void initAMS()
	{
		if (DIMAams==null){
			final AgentManagementSystem ams = new AgentManagementSystem();
			ams.activate();
		}
	}

}
