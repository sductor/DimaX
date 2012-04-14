package negotiation.faulttolerance.collaborativecandidature;

import java.util.HashSet;
import java.util.Random;

import negotiation.faulttolerance.experimentation.Replica;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationResultAgent;
import negotiation.faulttolerance.faulsimulation.FaultEvent;
import negotiation.faulttolerance.faulsimulation.FaultObservationService;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ContractTrunk;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.protocoles.collaborative.AgentInformedSelectionCore;
import negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import negotiation.negotiationframework.rationality.AltruistRationalCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.ObservingSelfService;

public class CollaborativeReplica extends Replica {
	private static final long serialVersionUID = 4986143017976368579L;


	public CollaborativeReplica(
			final AgentIdentifier id,
			final ReplicaState myState,
			final String socialWelfare,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id, myState,
				new AltruistRationalCore(new ReplicationSocialOptimisation(socialWelfare),new InformedCandidatureRationality(new ReplicaCore(),true)),
				new AgentInformedSelectionCore(),
				new CollaborativeCandidatureProposer(),
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