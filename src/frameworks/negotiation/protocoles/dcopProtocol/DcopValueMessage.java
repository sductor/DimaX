package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.HashSet;
import java.util.Set;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;

public class DcopValueMessage<State> extends Message{
	final State myState;
	int remainingHops;

	final AgentIdentifier variable;

	public DcopValueMessage(
			int remainingHops,
			AgentIdentifier variable,
			State myState) {
		super();
		this.remainingHops = remainingHops;
		this.variable = variable;
		this.myState = myState;
	}

	public AgentIdentifier getVariable() {
		return variable;
	}

	public boolean mustBeForwarded() {
		return remainingHops>=0;
	}

	public void decreaseHops(){
		remainingHops--;
	}
	
	State getMyState(){
		return myState;
	}	
	
	public DcopValueMessage<State> clone(){
		DcopValueMessage<State> dcopValueMessage = new DcopValueMessage<State>(new Integer(remainingHops), variable, myState);
		return dcopValueMessage;
	}
}	