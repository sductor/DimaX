package dima.basiccommunicationcomponents;

import dima.basicagentcomponents.AgentAddress;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.AbstractMessageInterface;
import dima.basicinterfaces.CommunicationComponentInterface;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;
/**
 * Insert the type's description here.
 * Creation date: (25/04/01 09:39:51)
 * @author: Zahia Guessoum
 */
public class CommunicationComponent extends AgentAddress implements CommunicationComponentInterface{
	/**
	 *
	 */
	private static final long serialVersionUID = -3715842740895934919L;
	/**
	 * CommunicationComponent constructor comment.
	 */
	public CommunicationComponent() {
		super();
	}
	/**
	 * CommunicationComponent constructor comment.
	 */
	public CommunicationComponent(final BasicCommunicatingAgent a) {
		super();
		this.agentBehavior = a;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/04/01 09:41:06)
	 */
	public void sendMessage(final AgentAddress ad,final Message am) {
		ad.receive(am);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/04/01 09:41:06)
	 */
	public void sendMessage(final Message m) {
		AgentManagementSystem.DIMAams.receive(m);
	}

	public Object sendSynchMessage(final AgentAddress ad,final Message am) {
		return ad.processMessage(am);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (25/04/01 09:41:06)
	 */
	public Object sendSynchMessage(final Message m) {
		//AgentManagementSystem.DIMAams.receive(m);
		return null;
	}

	public AgentIdentifier getMessageReceiver(Message m){
		return m.getReceiver();
	}
	
	@Override
	public void sendMessage(final AgentIdentifier id, final AbstractMessageInterface m) {
		m.setReceiver(id);
		m.setSender(this.agentBehavior.getIdentifier());
		this.sendMessage((Message) m);
	}
}
