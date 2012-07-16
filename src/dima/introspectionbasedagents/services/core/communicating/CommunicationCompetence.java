package dima.introspectionbasedagents.services.core.communicating;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.AbstractMailBox;
import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.DimaComponentInterface;

public interface CommunicationCompetence extends DimaComponentInterface {

	/*
	 * 
	 */
	
	boolean isConnected(String[] args);

	boolean connect(String[] args);

	boolean disconnect(String[] args);
	
	/*
	 * 
	 */
	
	public Collection<AgentIdentifier> getAcquaintances();
	
	public boolean addAcquaintance(AgentIdentifier id);
	
	public boolean removeAcquaintance(AgentIdentifier id);
	
	/*
	 * 
	 */
	
	Message sendSynchronousMessage(Message a);
	
	boolean sendASynchronousMessage(Message a);
	
	void receiveAsynchronousMessage(Message a);
}
