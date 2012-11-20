package frameworks.negotiation.protocoles.dcopProtocol;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;

public class DcopValueMessage<State> extends Message{
	/**
	 * 
	 */
	private static final long serialVersionUID = 3506995441115202346L;
	final State myState;
	int remainingHops;

	final AgentIdentifier variable;

	public DcopValueMessage(
			final int remainingHops,
			final AgentIdentifier variable,
			final State myState) {
		super();
		this.remainingHops = remainingHops;
		this.variable = variable;
		this.myState = myState;
	}

	public AgentIdentifier getVariable() {
		return this.variable;
	}

	public boolean mustBeForwarded() {
		return this.remainingHops>=0;
	}

	public void decreaseHops(){
		this.remainingHops--;
	}

	State getMyState(){
		return this.myState;
	}

	@Override
	public DcopValueMessage<State> clone(){
		final DcopValueMessage<State> dcopValueMessage = new DcopValueMessage<State>(new Integer(this.remainingHops), this.variable, this.myState);
		return dcopValueMessage;
	}
}