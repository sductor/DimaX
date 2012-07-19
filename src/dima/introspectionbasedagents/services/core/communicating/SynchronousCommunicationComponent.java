package dima.introspectionbasedagents.services.core.communicating;

import dima.basiccommunicationcomponents.Message;
import dima.basicinterfaces.DimaComponentInterface;

public interface SynchronousCommunicationComponent extends DimaComponentInterface{

	
	boolean isConnected(String[] args);

	boolean connect(String[] args);

	boolean disconnect(String[] args);
	

	/*
	 * 
	 */
	
	Message sendSynchronousMessage(Message a);

	
}
