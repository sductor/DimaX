package vieux.dcop;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.ontologies.Protocol;

public class DcopProtocol<Value, Constraint>
extends Protocol<DcopNodeAgent<Value, Constraint>>{

	public enum OptType {tDistance, kSize};
	public final int qualityValue;

	/*
	 * 
	 */
	
	public void init(){
		sendMessage(getMyAgent().getNeighbors(), new ValueMessage(getMyAgent(),qualityValue+1));
		sendMessage(getMyAgent().getNeighbors(), new ConstraintMessage(getMyAgent(),qualityValue));
	}

	/*
	 * 
	 */

	@MessageHandler
	public void receiveValueInfo(ValueMessage m){
		getMyAgent().updateLocalView(m);
		if (m.remainingHops>0)
			sendMessage(getMyAgent().getNeighbors(), new ValueMessage(m,m.remainingHops-1));
	}
	
	@MessageHandler
	public void receiveConstraintInfo(ConstraintMessage m){
		getMyAgent().updateConstraint(m);
		if (m.remainingHops>0)
			sendMessage(getMyAgent().getNeighbors(), new ConstraintMessage(m,m.remainingHops-1));
	}
	
	/*
	 * 
	 */
	
	
	
	public class ValueMessage extends Message {

		public final AgentIdentifier originalSenderId;
		public final Value originalSenderValue;
		public final int remainingHops;


		public ValueMessage(DcopNodeAgent<Value, Constraint> myAgent,int k) {
			originalSenderId = getMyAgent().getIdentifier();
			originalSenderValue=myAgent.getValue();
			remainingHops=k;
		}


		public ValueMessage(ValueMessage m, int k) {
			originalSenderId=m.originalSenderId;
			originalSenderValue=m.originalSenderValue;
			remainingHops=k;
		}
	}
	public class ConstraintMessage extends Message {

		public final AgentIdentifier originalSenderId;
		public final Constraint originalSenderConstraint;
		public final int remainingHops;


		public ConstraintMessage(DcopNodeAgent<Value, Constraint> myAgent,int k) {
			originalSenderId = getMyAgent().getIdentifier();
			originalSenderConstraint=myAgent.getConstraint();
			remainingHops=k;
		}


		public ConstraintMessage(ConstraintMessage m, int k) {
			originalSenderId=m.originalSenderId;
			originalSenderConstraint=m.originalSenderConstraint;
			remainingHops=k;
		}
	}
	public class LocKMessage extends Message{
		
	}
}
