package negotiation.faulttolerance.negotiatingagent;

import java.util.Date;
import java.util.HashSet;
import java.util.Random;

import negotiation.experimentationframework.ExperimentationResults;
import negotiation.experimentationframework.SelfObservingService;
import negotiation.faulttolerance.ReplicationCandidature;
import negotiation.faulttolerance.ReplicationSpecification;
import negotiation.faulttolerance.candidaturenegotiation.statusdestruction.CandidatureReplicaCoreWithStatus;
import negotiation.faulttolerance.experimentation.ReplicationAgentResult;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationProtocol;
import negotiation.faulttolerance.experimentation.ReplicationLaborantin;
import negotiation.faulttolerance.faulsimulation.FaultObservationService;
import negotiation.faulttolerance.faulsimulation.FaultEvent;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.agent.RationalCore;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import negotiation.negotiationframework.interaction.selectioncores.AbstractSelectionCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.library.information.NoInformationAvailableException;
import dima.introspectionbasedagents.services.library.information.ObservationService;
import dima.introspectionbasedagents.services.library.information.SimpleObservationService;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;

public class NegotiatingReplica
extends SimpleNegotiatingAgent<ReplicationSpecification, ReplicaState, ReplicationCandidature> {
	private static final long serialVersionUID = 4986143017976368579L;

	//
	// Fields
	//

	public boolean replicate = true;

	@Competence
	SelfObservingService mySelfObservationService = new SelfObservingService() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 6123670961531677514L;

		@Override
		protected ExperimentationResults generateMyResults() {
			ReplicationAgentResult myInfo;
			if (NegotiatingReplica.this.getMyCore() instanceof CandidatureReplicaCoreWithStatus)
				myInfo = new ReplicationAgentResult(
						NegotiatingReplica.this.getMyCurrentState(),
						NegotiatingReplica.this.getCreationTime(),
						((CandidatureReplicaCoreWithStatus) NegotiatingReplica.this
								.getMyCore()).getMyStatus());
			else
				myInfo = new ReplicationAgentResult(
						NegotiatingReplica.this.getMyCurrentState(),
						NegotiatingReplica.this.getCreationTime());
			if (!NegotiatingReplica.this.isAlive())
				myInfo.setiAmDead(true);
			return myInfo;
		}
	};

	@Competence
	FaultObservationService myFaultAwareService = new FaultObservationService() {

		/**
		 * 
		 */
		private static final long serialVersionUID = 186751301573671600L;

		@Override
		protected void resetMyState() {
			NegotiatingReplica.this.setNewState(
					new ReplicaState(getIdentifier(), 
					getMyCurrentState().getMyCriticity(), 
					getMyCurrentState().getMyProcCharge(), 
					getMyCurrentState().getMyMemCharge(),
					new HashSet<HostState>()));
		}

		@Override
		protected void resetMyUptime() {
			NegotiatingReplica.this.getMyCurrentState().resetUptime();
		}

		@Override
		public void faultObservation(final FaultEvent m) {// final
															// NotificationMessage<FaultEvent>
															// m) {
			if (NegotiatingReplica.this.isAlive()) {
				super.faultObservation(m);
				if (NegotiatingReplica.this.getMyCurrentState().getMyReplicas()
						.isEmpty()) {
					this.logMonologue("this is the end my friend");
					NegotiatingReplica.this.mySelfObservationService
							.endSimulation();
				}
			}
		}
	};

	//
	// Constructor
	//

	public NegotiatingReplica(
			final AgentIdentifier id,
			final Date horloge,
			final Double criticity,
			final Double procCharge,
			final Double memCharge,
			final RationalCore<ReplicationSpecification, ReplicaState, ReplicationCandidature> myRationality,
			final AbstractSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature> participantCore,
			final AbstractProposerCore<SimpleNegotiatingAgent<ReplicationSpecification, ReplicaState, ReplicationCandidature>,ReplicationSpecification, ReplicaState, ReplicationCandidature> proposerCore,
			ObservationService myInformation)
			throws CompetenceException {
		super(id, horloge, null, myRationality, participantCore, proposerCore, myInformation);
		myStateType = ReplicaState.class;
		this.setNewState(new ReplicaState(id, criticity, procCharge, memCharge,new HashSet<HostState>()));
	}

	public NegotiatingReplica(
			final AgentIdentifier id,
			final Double criticity,
			final Double procCharge,
			final Double memCharge,
			final RationalCore<ReplicationSpecification, ReplicaState, ReplicationCandidature> myRationality,
			final AbstractSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature> participantCore,
			final AbstractProposerCore<SimpleNegotiatingAgent<ReplicationSpecification, ReplicaState, ReplicationCandidature>,ReplicationSpecification, ReplicaState, ReplicationCandidature> proposerCore,
			ObservationService myInformation)
			throws CompetenceException {
		super(id, null, myRationality, participantCore, proposerCore, myInformation);
		myStateType = ReplicaState.class;
		this.setNewState(new ReplicaState(id, criticity, procCharge, memCharge,new HashSet<HostState>()));
	}

	//
	// Accessors
	//

	public boolean IReplicate() {
		return this.replicate;
	}

	public void setIReplicate(final boolean replicate) {
		this.replicate = replicate;
	}

	@StepComposant()
	@Transient
	public boolean initialynotifyMyState4Status() {
		this.notifyMyReliability4Status();
		return true;
	}

	@StepComposant()
	@Transient
	public boolean setReplication() {
		if (this.getMyInformation().getKnownAgents().isEmpty())
			this.replicate = false;

		// logMonologue("agents i know : "+this.getKnownAgents());
		// if (IReplicate())
		// logMonologue("yeeeeeeeeeeaaaaaaaaaaaahhhhhhhhhhhhh      iii replicatre!!!!!!!!!!!!!!!!!!!!!!"+((CandidatureReplicaCoreWithStatus)myCore).getMyStatus());

		return true;
	}

	@Override
	public void setNewState(ReplicaState s) {
		super.setNewState(s);
	}
	
	@StepComposant(ticker = ReplicationExperimentationParameters._reliabilityObservationFrequency)
	public void notifyMyReliability4Status() {
		// logMonologue("relia send to "+observer.getObserver(ReplicationExperimentationProtocol.reliabilityObservationKey));
		if (this.getMyCore() instanceof CandidatureReplicaCoreWithStatus)
			this.notify(
					this.getMyCurrentState().getMyReliability(),
					ReplicationLaborantin.reliabilityObservationKey);
	}

	// @StepComposant(ticker=StaticParameters._criticity_update_frequency)
	public void updateMyCriticity() {
		final Random r = new Random();
		if (r.nextDouble() <= ReplicationExperimentationParameters._criticityVariationProba) {// On
																							// met
																							// a
																							// jour
			final int signe = r.nextBoolean() ? 1 : -1;
			final Double newCriticity = Math
					.min(1.,
							Math.max(
									ReplicationExperimentationParameters._criticityMin,
									this.getMyCurrentState().getMyCriticity()
											+ signe
											* r.nextDouble()
											* ReplicationExperimentationParameters._criticityVariationAmplitude));
			
			this.setNewState(
					new ReplicaState(
							this.getIdentifier(),
					newCriticity, this.getMyCurrentState().getMyProcCharge(),
					this.getMyCurrentState().getMyMemCharge(), this
							.getMyCurrentState().getMyReplicas()));
		}
	}

	/*
	 *
	 */

//	@Override
//	public ContractTrunk<ReplicationCandidature> select(
//			final ContractTrunk<ReplicationCandidature> cs) {
//
//		if (this.myCore instanceof CandidatureReplicaCoreWithMinInfo)
//			((CandidatureReplicaCoreWithMinInfo) this.myCore)
//					.setMinKnowRelia(cs);
//
//		return super.select(cs);
//	}

//	@Override
//	public void execute(final ReplicationCandidature c) {
//		// notify(new ReliabilityUpdate());
//		ReplicaState previousState = getMyCurrentState();
//		super.execute(c);
////		logMonologue("i have been replicated by "+c.getResource());// : \n previous stae : "+previousState+"\n new state : "+getMyCurrentState());
//	}
	
	@MessageHandler
	@NotificationEnvelope(SimpleObservationService.informationObservationKey)
	public <Info extends Information> void receiveInformation(
			NotificationMessage<Information> o) {
//		logMonologue("yophoi");
//		if (o.getNotification() instanceof HostState 
//				&& getMyCurrentState().getMyReplicaIdentifiers().contains(
//						((HostState) o.getNotification()).getMyAgentIdentifier())){
//			ReplicaState r = new ReplicaState(getMyCurrentState(), (HostState) o.getNotification());
//			r = new ReplicaState(r, (HostState) o.getNotification());
//			setNewState(r);
//		}
	}

	// }
	// @Override
	// protected EndInfo getMyEndNotif() {
	// return new AgentEndInfo(getMyCurrentState(),getCreationTime());
	// }



	public Double getCharge(ResourceIdentifier r)  {
		try {
			return getMyInformation().getInformation(HostState.class, r).getMyCharge();
		} catch (NoInformationAvailableException e) {
			throw new RuntimeException("muahahahaha tant pis pour ta gueule!!!!!!!!!!!!!!!!!!!!!!");
		}
	}
}

/*
 *
 *
 *
 *
 *
 *
 *
 */

//
// public class ReliabilityUpdate implements Serializable{
//
// /**
// *
// */
// private static final long serialVersionUID = -1158730596156778825L;
// private final AgentIdentifier id;
// private final Double relia;
//
//
//
// public ReliabilityUpdate() {
// super();
// this.id = NegotiatingReplica.this.getIdentifier();
// this.relia = NegotiatingReplica.this.getMyCurrentState().getMyReliability();
// if (this.relia==null) throw new RuntimeException("aaaaaaaaaaa");
// }
//
// public Double getRelia() {
// return this.relia;
// }
//
// public AgentIdentifier getIdentifier() {
// return this.id;
// }
//
// @Override
// public String toString(){
// return "identif "+this.id+", relia "+this.relia;
// }
// }

// public AgentStateStatus getMyStateStatus(){
// return CandidatureReplicaCore.this.getStatus(this);
// }

// @Override
// public void reset() {
// this.myReplicas.clear();
// this.resetUptime();
// }

//
// public boolean Iaccept(final ReplicaState s, final ReplicationCandidature c)
// {
// return super.Iaccept(s,c) &&
// (
// (s.getMyReliability()>minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>minKnownReliability)
// ||
// (s.getMyReliability()<minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>s.getMyReliability())
// );
// }
// public boolean Iaccept(final ReplicaState s, final
// Collection<ReplicationCandidature> c) {
// return super.Iaccept(s,c) &&
// (
// (s.getMyReliability()>=minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>minKnownReliability)
// ||
// (s.getMyReliability()<minKnownReliability &&
// getMyResultingState(s,c).getMyReliability()>s.getMyReliability())
// );
// }

// private Double computeDisponibility(
// final Collection<ResourceIdentifier> replicas) {
// // final Collection<Double> hosts = new ArrayList<Double>();
// // for (final ResourceIdentifier r : replicas){
// // try{
// // hosts.add(ReplicaCore.this.specifs.get(r).getDisponibility());
// // } catch (NullPointerException e){
// //
// logMonologue(specifs+"\n --->"+getMyAgent().getMyCurrentState().myReplicas);
// // throw e;
// // }
// // }
//
// return getMyAgent().getDisponibility(replicas);
// }
// @Override
// public ReplicaState clone(){
// final Set<ResourceIdentifier> reps = new HashSet<ResourceIdentifier>();
// reps.addAll(this.getMyReplicas());
// return new ReplicaState(this, reps);
// }

// boolean iveSarted=false;
//
// Behavior
//

/*
 * Notification
 */

/*
 *
 */

/*
 *
 */

// @StepComposant(ticker=1000)
// @Transient
// public boolean start(){
// appliHasStarted=true;
// return true;
// }

/*
 *
 */

/*
 *
 */

//
// /*
// *
// *
// *
// *
// *
// *
// */
//
//
// public class AgentInfo extends GimaObject
// implements Comparable<AgentInfo>{
//
// /**
// *
// */
// private static final long serialVersionUID = -5530129809027522515L;
// public final AgentIdentifier id;
// public final Double reliability;
// public final long uptime;
// public final boolean maxReplication;
// public final Double myProcCharge;
// public final Double myMemCharge;
// public final AgentStateStatus myStatus;
// public final Double criticity;
// public final Boolean iAmDead;
//
//
//
//
// public AgentInfo() {
// super();
// this.id = NegotiatingReplica.this.getIdentifier();
// this.reliability =
// NegotiatingReplica.this.getMyCurrentState().getMyReliability();
// this.uptime = NegotiatingReplica.this.getMyCurrentState().getUptime();
// this.maxReplication =
// NegotiatingReplica.this.getMyCurrentState().getMyReplicas().containsAll(NegotiatingReplica.this.getKnownAgents());
// this.myProcCharge=NegotiatingReplica.this.getMyCurrentState().getMyProcCharge();
// this.myMemCharge=NegotiatingReplica.this.getMyCurrentState().getMyMemCharge();
// this.myStatus =
// NegotiatingReplica.this.getMyCurrentState().getMyStateStatus();
// this.criticity=NegotiatingReplica.this.getMyCurrentState().getMyCriticity();
// this.iAmDead=NegotiatingReplica.this.iAMDead;
// }
//
//
//
// public AgentIdentifier getMyAgentIdentifier() {
// return this.id;
// }
//
//
// @Override
// public int hashCode(){
// return this.getMyAgentIdentifier().hashCode();
// }
//
// @Override
// public boolean equals(final Object o){
// if (o instanceof AgentInfo) {
// final AgentInfo that = (AgentInfo) o;
// return that.getMyAgentIdentifier().equals(this.getMyAgentIdentifier());
// } else
// return false;
// }
//
//
//
// public Double getMyReliability() {
// return this.reliability;
// }
//
//
//
// @Override
// public int compareTo(final AgentInfo that) {
// if (this.maxReplication==true && that.maxReplication==true)
// return 0;
// else if (this.maxReplication==true && that.maxReplication==false)
// return 1;
// else if (this.maxReplication==false && that.maxReplication==true)
// return -1;
// else
// return this.reliability.compareTo(that.reliability);
// }
//
//
//
// public Double getMyProcCharge() {
// return this.myProcCharge;
// }
//
//
//
// public Double getMyMemCharge() {
// return this.myMemCharge;
// }
// }