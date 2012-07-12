package negotiation.faulttolerance.collaborativecandidature;

import negotiation.faulttolerance.experimentation.Replica;
import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import negotiation.negotiationframework.protocoles.collaborative.AgentInformedSelectionCore;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidature;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import negotiation.negotiationframework.rationality.AltruistRationalCore;
import negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;

public class CollaborativeReplica extends Replica<InformedCandidature<ReplicationCandidature>> {
	private static final long serialVersionUID = 4986143017976368579L;


	public CollaborativeReplica(
			final AgentIdentifier id,
			final ReplicaState myState,
			final SocialChoiceType socialWelfare,
			final int simulateanousKCadidature,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id, myState,
				new AltruistRationalCore(new ReplicationSocialOptimisation(socialWelfare),new InformedCandidatureRationality(new ReplicaCore(false,false),true)),
				new AgentInformedSelectionCore(),
				new CollaborativeCandidatureProposer(simulateanousKCadidature),
				new SimpleObservationService(),
				new OneDeciderCommunicationProtocol(false),dynamicCriticity);
	}

}



//	public boolean IReplicate() {
//		return this.replicate;
//	}
//
//	public void setIReplicate(final boolean replicate) {
//		this.replicate = replicate;
//	}
//
//	@StepComposant()
//	@Transient
//	public boolean setReplication() {
//		if (this.getMyInformation().getKnownAgents().isEmpty())
//			this.replicate = false;
//
//		// logMonologue("agents i know : "+this.getKnownAgents());
//		// if (IReplicate())
//		// logMonologue("yeeeeeeeeeeaaaaaaaaaaaahhhhhhhhhhhhh      iii replicatre!!!!!!!!!!!!!!!!!!!!!!"+((CandidatureReplicaCoreWithStatus)myCore).getMyStatus());
//
//		return true;
//	}

//	@Override
//	public void setNewState(final ReplicaState s) {
//		super.setNewState(s);
//	}