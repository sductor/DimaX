package negotiation.faulttolerance.collaborativecandidature;

import java.util.HashSet;
import java.util.Random;

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

public class CollaborativeReplica
extends SimpleNegotiatingAgent<ReplicationSpecification, ReplicaState, InformedCandidature<ReplicationCandidature,ReplicationSpecification>> {
	private static final long serialVersionUID = 4986143017976368579L;

	//
	// Fields
	//

	private final boolean dynamicCrticity;

	//	public boolean replicate = true;

	@Competence
	ObservingSelfService mySelfObservationService = new ObservingSelfService() {

		/**
		 *
		 */
		private static final long serialVersionUID = 6123670961531677514L;

		@Override
		protected ExperimentationResults generateMyResults() {
			ReplicationResultAgent myInfo;
			myInfo = new ReplicationResultAgent(
					CollaborativeReplica.this.getMyCurrentState(),
					CollaborativeReplica.this.getCreationTime());
			return myInfo;
		}
	};

	@Competence
	FaultObservationService<ReplicationSpecification, ReplicaState, ReplicationCandidature> myFaultAwareService =
	new FaultObservationService<ReplicationSpecification, ReplicaState, ReplicationCandidature>() {

		/**
		 *
		 */
		private static final long serialVersionUID = 186751301573671600L;

		@Override
		protected void resetMyState() {
			CollaborativeReplica.this.setNewState(
					new ReplicaState(this.getIdentifier(),
							CollaborativeReplica.this.getMyCurrentState().getMyCriticity(),
							CollaborativeReplica.this.getMyCurrentState().getMyProcCharge(),
							CollaborativeReplica.this.getMyCurrentState().getMyMemCharge(),
							new HashSet<HostState>(),this.getMyAgent().nextStateCounter));
		}

		@Override
		protected void resetMyUptime() {
			assert 1<0:"Replica.this.getMyCurrentState().resetUptime()";
		}

		@Override
		public void faultObservation(final FaultEvent m) {// final
			// NotificationMessage<FaultEvent>
			// m) {
			if (CollaborativeReplica.this.isAlive()) {
				super.faultObservation(m);
				if (CollaborativeReplica.this.getMyCurrentState().getMyReplicas().isEmpty()) {
					this.logMonologue("this is the end my friend",LogService.onBoth);
					CollaborativeReplica.this.mySelfObservationService.endSimulation();
				}
			}
		}
	};

	//
	// Constructor
	//

	public CollaborativeReplica(
			final AgentIdentifier id,
			final Double criticity,	final Double procCharge,final Double memCharge,
			final String socialWelfare,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id, null,
				new AltruistRationalCore(
						new ReplicationSocialOptimisation(socialWelfare),
						new InformedCandidatureRationality(new ReplicaCore(),true)),
						new AgentInformedSelectionCore(),
						new CollaborativeCandidatureProposer(),
						new SimpleObservationService(),
						new OneDeciderCommunicationProtocol( new ContractTrunk(), false));
		this.myStateType = ReplicaState.class;
		this.dynamicCrticity=dynamicCriticity;
		this.setNewState(new ReplicaState(id, criticity, procCharge, memCharge,new HashSet<HostState>(),-1));
		this.getMyProtocol().getContracts().setMyAgent(this);
	}

	//
	// Accessors
	//

	@StepComposant(ticker=ReplicationExperimentationParameters._criticity_update_frequency)
	public void updateMyCriticity() {
		if (this.dynamicCrticity){
			final Random r = new Random();
			if (r.nextDouble() <= ReplicationExperimentationParameters._criticityVariationProba) {// On
				// met a jour
				final int signe = r.nextBoolean() ? 1 : -1;
				final Double newCriticity = Math
						.min(1.,
								Math.max(
										ReplicationExperimentationParameters._criticityMin,
										this.getMyCurrentState().getMyCriticity()
										+ signe
										* r.nextDouble()
										* ReplicationExperimentationParameters._criticityVariationAmplitude));
				this.logWarning("Updating my criticity", LogService.onNone);
				this.setNewState(
						new ReplicaState(
								this.getIdentifier(),
								newCriticity, this.getMyCurrentState().getMyProcCharge(),
								this.getMyCurrentState().getMyMemCharge(), this
								.getMyCurrentState().getMyReplicas(),this.nextStateCounter));
			}
		}
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