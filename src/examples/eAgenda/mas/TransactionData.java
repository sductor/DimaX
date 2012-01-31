package examples.eAgenda.mas;

import java.io.Serializable;

import dima.basicagentcomponents.AgentIdentifier;
import examples.eAgenda.data.ActivityList;
import examples.eAgenda.data.Meeting;


/** Data structure that gather all information related to a transaction (easiest to use and retrieve) */
public class TransactionData implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = -3397188121718261820L;
	public TransactionID TID, parentTID;
	public Meeting goal;
	public ActivityList activities;
	public AgentIdentifier initiator;
	public Integer identifierWithinTransaction;
	public boolean necessary;

	public TransactionData(final TransactionID id, final Meeting m, final TransactionID parentID, final ActivityList act, final AgentIdentifier whoSaidIt, final Integer identifier, final Boolean necessaryToTheMeeting) {
		this.TID = id;
		this.goal = m;
		this.parentTID = parentID;
		this.activities = act;
		this.initiator = whoSaidIt;
		this.identifierWithinTransaction = identifier;
		this.necessary = necessaryToTheMeeting.booleanValue();
	}

	public TransactionID getTransactionID(){
		return this.TID;
	}

	public Meeting getMeeting(){
		return this.goal;
	}


}
