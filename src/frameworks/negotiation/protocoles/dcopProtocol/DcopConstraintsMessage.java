package frameworks.negotiation.protocoles.dcopProtocol;

import java.util.Collection;
import java.util.HashSet;

import dima.basicagentcomponents.AgentIdentifier;

public class DcopConstraintsMessage<State>  extends DcopValueMessage<State> {
	final Collection<AgentIdentifier> myAcquaintances;		



	public DcopConstraintsMessage(int remainingHops,
			AgentIdentifier variable,
			State myState,
			Collection<AgentIdentifier> myAcquaintances) {
		super(remainingHops,variable,myState);
		this.myAcquaintances = myAcquaintances;
	}


	Collection<AgentIdentifier> getMyAcquaintances(){
		return myAcquaintances;
	}		
	
	public DcopConstraintsMessage<State> clone(){
		DcopConstraintsMessage<State> dcopValueMessage = new DcopConstraintsMessage<State>(new Integer(remainingHops), variable, myState, myAcquaintances);
//		dcopValueMessage.alreadySend=new HashSet<AgentIdentifier>(alreadySend);
		return dcopValueMessage;
	}
}
