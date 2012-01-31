package dima.basicagentcomponents;

/**
 * The Agent Adress is an Abstract Class referencing a Communication Behavior.
 *
 * Creation date: (27/04/00 11:34:08)
 * @author: Zahia Guessoum
 */

import dima.basiccommunicationcomponents.Message;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

public class AgentAddress extends AbstractAgentAddress {
	/**
	 *
	 */
	private static final long serialVersionUID = 1261042594034411855L;
	public BasicCommunicatingAgent agentBehavior;
	/**
	 * BasicAgent constructor comment.
	 */
	public AgentAddress() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (22/09/00 15:16:46)
	 * @param com Gdima.behaviors.communication.CommunicationBehavior
	 */
	public AgentAddress(final BasicCommunicatingAgent com)
	{this.agentBehavior = com;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (27/04/00 11:37:10)
	 * @return Gdima.competences.communication.AgentIdentifier
	 */
	@Override
	public  AgentIdentifier getId()
	{return this.agentBehavior.getIdentifier();  }
	/**
	 * Insert the method's description here.
	 * Creation date: (28/04/00 16:09:42)
	 * @param m Gdima.competences.communication.AbstractMessage
	 */
	public void receive(final Message m) {
		this.agentBehavior.receive(m);
	}

	public Object processMessage(final Message m) {
		return this.agentBehavior.processMessage(m);
	}
	/**
	 * @param am
	 */



}
