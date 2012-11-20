package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;

public class DcopConstraintsMessage<State>  extends DcopValueMessage<State> {
	/**
	 * 
	 */
	private static final long serialVersionUID = -1793960846408747376L;
	final Collection<AgentIdentifier> myAcquaintances;



	public DcopConstraintsMessage(final int remainingHops,
			final AgentIdentifier variable,
			final State myState,
			final Collection<AgentIdentifier> myAcquaintances) {
		super(remainingHops,variable,myState);
		this.myAcquaintances = myAcquaintances;
	}


	Collection<AgentIdentifier> getMyAcquaintances(){
		return this.myAcquaintances;
	}

	@Override
	public DcopConstraintsMessage<State> clone(){
		final DcopConstraintsMessage<State> dcopValueMessage = new DcopConstraintsMessage<State>(new Integer(this.remainingHops), this.variable, this.myState, this.myAcquaintances);
		//		dcopValueMessage.alreadySend=new HashSet<AgentIdentifier>(alreadySend);
		return dcopValueMessage;
	}
}
