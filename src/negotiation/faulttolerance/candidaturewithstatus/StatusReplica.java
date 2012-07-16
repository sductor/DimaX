package negotiation.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import negotiation.faulttolerance.experimentation.Replica;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;

public class StatusReplica extends Replica {


	//
	// Competences
	//
	
	public final StatusObservationCompetence soc;
	
	
	// 
	// Constructor
	//	
	

	public StatusReplica(AgentIdentifier id, ReplicaState myState,
			RationalCore myRationality, SelectionCore participantCore,
			ProposerCore proposerCore, ObservationService myInformation,
			AbstractCommunicationProtocol protocol, boolean dynamicCriticity,
			AgentIdentifier myLaborantin)
			throws CompetenceException {
		super(id, myState, myRationality, participantCore, proposerCore, myInformation,
				protocol, dynamicCriticity);
		soc = new StatusObservationCompetence(this,myLaborantin);
	}

	public StatusReplica(AgentIdentifier id, ReplicaState myState,
			RationalCore myRationality, SelectionCore participantCore,
			ProposerCore proposerCore, ObservationService myInformation,
			AbstractCommunicationProtocol protocol, boolean dynamicCriticity,
			int numbertoContact)
			throws CompetenceException {
		super(id, myState, myRationality, participantCore, proposerCore, myInformation,
				protocol, dynamicCriticity);
		soc = new StatusObservationCompetence(this,numbertoContact);
	}
	
	//
	// Accessors
	//

	public void setNewState(final ReplicaState s) {
		super.setNewState(s);
		if (soc!=null) soc.diffuse(s);
	}
	
	//
	// Behavior
	//


	@StepComposant()
	@Transient
	public boolean initialynotifyMyState4Status() {
		soc.diffuse(getMyCurrentState());
		return true;
	}
}
