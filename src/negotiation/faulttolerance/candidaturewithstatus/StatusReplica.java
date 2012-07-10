package negotiation.faulttolerance.candidaturewithstatus;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import negotiation.faulttolerance.experimentation.Replica;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import negotiation.negotiationframework.rationality.RationalCore;

public class StatusReplica extends Replica {


	//
	// Competences
	//
	
//	public final StatusObservationCompetence soc;
	
	
	// 
	// Constructor
	//	
	

	public StatusReplica(AgentIdentifier id, 
			ReplicaState myState,
			SelectionCore participantCore,
			int simultaneousCandidature,
			boolean dynamicCriticity)
			throws CompetenceException {
		super(id, 
				myState, 
				new CandidatureReplicaCoreWithStatus(), 
				participantCore, 
				new CandidatureReplicaProposerWithStatus(simultaneousCandidature), 
				new SimpleOpinionService(),
				new ReverseCFPProtocol(),
				dynamicCriticity);
//		soc = new StatusObservationCompetence(this,myLaborantin);
	}
//	
//	//
//	// Accessors
//	//
//
//	public void setNewState(final ReplicaState s) {
//		super.setNewState(s);
//		if (soc!=null) soc.diffuse(s);
//	}
//	
//	//
//	// Behavior
//	//
//
//
//	@StepComposant()
//	@Transient
//	public boolean initialynotifyMyState4Status() {
//		soc.diffuse(getMyCurrentState());
//		return true;
//	}
}
