package negotiation.faulttolerance.negotiatingagent;

import java.util.Random;

import negotiation.faulttolerance.candidaturewithstatus.CandidatureReplicaCoreWithStatus;
import negotiation.faulttolerance.experimentation.ReplicationExperimentationParameters;
import negotiation.faulttolerance.experimentation.ReplicationResultAgent;
import negotiation.faulttolerance.faulsimulation.FaultEvent;
import negotiation.faulttolerance.faulsimulation.FaultObservationService;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.ObservingSelfService;

public class Replica
extends SimpleNegotiatingAgent<ReplicaState, ReplicationCandidature> {
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
			if (Replica.this.getMyCore() instanceof CandidatureReplicaCoreWithStatus) {
				myInfo = new ReplicationResultAgent(
						Replica.this.getMyCurrentState(),
						Replica.this.getCreationTime(),
						((CandidatureReplicaCoreWithStatus) Replica.this
								.getMyCore()).getMyStatus());
			} else {
				myInfo = new ReplicationResultAgent(
						Replica.this.getMyCurrentState(),
						Replica.this.getCreationTime());
			}
			return myInfo;
		}
	};

	@Competence
	FaultObservationService myFaultAwareService =
	new FaultObservationService() {

		/**
		 *
		 */
		private static final long serialVersionUID = 186751301573671600L;

		@Override
		protected void resetMyState() {
			Replica.this.setNewState(
					new ReplicaState(this.getIdentifier(),
							Replica.this.getMyCurrentState().getMyCriticity(),
							Replica.this.getMyCurrentState().getMyProcCharge(),
							Replica.this.getMyCurrentState().getMyMemCharge(),
							Replica.this.getMyCurrentState().getSocialWelfare(),
							this.getMyAgent().getMyCurrentState().getStateCounter()+1));
		}

		@Override
		protected void resetMyUptime() {
			assert 1<0:"Replica.this.getMyCurrentState().resetUptime()";
		}

		@Override
		public void faultObservation(final FaultEvent m) {// final
			// NotificationMessage<FaultEvent>
			// m) {
			if (Replica.this.isAlive()) {
				super.faultObservation(m);
				if (!Replica.this.getMyCurrentState().isValid()) {
					this.logMonologue("this is the end my friend",LogService.onBoth);
					Replica.this.mySelfObservationService.endSimulation();
				}
			}
		}
	};

	//
	// Constructor
	//



	public Replica(
			final AgentIdentifier id,
			final ReplicaState myState,
			final RationalCore myRationality,
			final SelectionCore participantCore,
			final ProposerCore proposerCore,
			final ObservationService myInformation,
			final AbstractCommunicationProtocol protocol,
			final boolean dynamicCriticity)
					throws CompetenceException {
		super(id, myState, myRationality, participantCore, proposerCore, myInformation, protocol);
		this.myStateType = ReplicaState.class;
		this.dynamicCrticity=dynamicCriticity;
	}


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
								this.getMyCurrentState(),
								newCriticity));
			}
		}
	}
}





	//
	// Accessors
	//

//	public void setNewState(final ReplicaState s) {
//		for (HostState h : s.getMyHosts()){
//			getMyInformation().add(h);
//		}
//		super.setNewState(s);
//	}

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




/*
 *
 */

//this.setNewState(new ReplicaState(id, criticity, procCharge, memCharge,new HashSet<HostState>(),-1));
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

//	@MessageHandler
//	@NotificationEnvelope(SimpleObservationService.informationObservationKey)
//	public <Info extends Information> void receiveInformation(
//			final NotificationMessage<Information> o) {
//		logMonologue("yophoi");
//		if (o.getNotification() instanceof HostState
//				&& getMyCurrentState().getMyResourceIdentifiers().contains(
//						((HostState) o.getNotification()).getMyAgentIdentifier())){
//			ReplicaState r = new ReplicaState(getMyCurrentState(), (HostState) o.getNotification(), getMyCurrentState().getCreationTime());
//			r = new ReplicaState(r, (HostState) o.getNotification(), getMyCurrentState().getCreationTime());
//			setNewState(r);
//		}
//	}

// }
// @Override
// protected EndInfo getMyEndNotif() {
// return new AgentEndInfo(getMyCurrentState(),getCreationTime());
// }



//	public Double getCharge(final ResourceIdentifier r)  {
//		try {
//			return this.getMyInformation().getInformation(HostState.class, r).getMyCharge();
//		} catch (final NoInformationAvailableException e) {
//			throw new RuntimeException("muahahahaha tant pis pour ta gueule!!!!!!!!!!!!!!!!!!!!!!");
//		}
//	}


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