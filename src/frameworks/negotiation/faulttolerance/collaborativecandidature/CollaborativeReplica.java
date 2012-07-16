package frameworks.negotiation.faulttolerance.collaborativecandidature;

import java.io.Serializable;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.information.SimpleObservationService;
import frameworks.negotiation.faulttolerance.Replica;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.AtMostKCandidaturesProposer;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.AgentInformedSelectionCore;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.InformedCandidature;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.InformedCandidatureRationality;
import frameworks.negotiation.negotiationframework.protocoles.collaborative.OneDeciderCommunicationProtocol;
import frameworks.negotiation.negotiationframework.rationality.AltruistRationalCore;
import frameworks.negotiation.negotiationframework.rationality.RationalAgent;
import frameworks.negotiation.negotiationframework.rationality.SimpleRationalAgent;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

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
				new AtMostKCandidaturesProposer(simulateanousKCadidature){
			@Override
			public InformedCandidature<ReplicationCandidature> constructCandidature(
					final ResourceIdentifier id) {
				final InformedCandidature<ReplicationCandidature> c =
						new InformedCandidature<ReplicationCandidature>(new ReplicationCandidature(id,this.getMyAgent().getIdentifier(),true,true));
				//		c.getPossibleContracts().addAll(((CollaborativeAgent)getMyAgent()).getCrt().getPossible(c));
				((RationalAgent<ReplicaState, InformedCandidature<ReplicationCandidature>>) this.getMyAgent()).setMySpecif(c);
				c.setInitialState(((RationalAgent<ReplicaState, InformedCandidature<ReplicationCandidature>>) this.getMyAgent()).getMyCurrentState());
				return c;
			}
		},
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