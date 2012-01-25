package negotiation.faulttolerance.negotiatingagent;

import negotiation.experimentationframework.ExperimentationResults;
import negotiation.experimentationframework.ObservingSelfService;
import negotiation.faulttolerance.experimentation.ReplicationResultHost;
import negotiation.faulttolerance.faulsimulation.FaultObservationService;
import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import negotiation.negotiationframework.interaction.consensualnegotiation.AbstractProposerCore;
import negotiation.negotiationframework.interaction.selectioncores.AbstractSelectionCore;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.core.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.library.information.ObservationService;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;
import dima.introspectionbasedagents.services.library.information.SimpleObservationService;

public class NegotiatingHost
extends	SimpleNegotiatingAgent<ReplicationSpecification, HostState, ReplicationCandidature>
{
	private static final long serialVersionUID = -8478683967125467116L;

	//
	// Fields
	//

	@Competence
	ObservingSelfService mySelfObservationService = new ObservingSelfService() {

		/**
		 *
		 */
		private static final long serialVersionUID = -6008018665463786541L;

		@Override
		protected ExperimentationResults generateMyResults() {
			return new ReplicationResultHost(
					NegotiatingHost.this.getMyCurrentState(),
					NegotiatingHost.this.getCreationTime());
		}
	};

	@Competence
	public
	FaultObservationService myFaultAwareService = new FaultObservationService() {

		/**
		 *
		 */
		private static final long serialVersionUID = -5530153574167669156L;

		@Override
		protected void resetMyState() {
			NegotiatingHost.this.setNewState(new HostState((ResourceIdentifier) this.getIdentifier(),
					NegotiatingHost.this.getMyCurrentState().getLambda()));
			this.resetMyUptime();
		}

		@Override
		protected void resetMyUptime() {
			NegotiatingHost.this.getMyCurrentState().resetUptime();
		}

	};

	//
	// Constructor
	//


	public NegotiatingHost(
			final ResourceIdentifier id,
			final double lambda,
			final HostCore myRationality,
			final AbstractSelectionCore<ReplicationSpecification, HostState, ReplicationCandidature> participantCore,
			final AbstractProposerCore<
			SimpleNegotiatingAgent<ReplicationSpecification, HostState, ReplicationCandidature>,
			ReplicationSpecification, HostState, ReplicationCandidature> 	proposerCore,
			final ObservationService myInformation,
			final HostDisponibilityComputer myDispoInfo)
					throws CompetenceException {
		super(id, new HostState(id, lambda), myRationality, participantCore, proposerCore, myInformation);
	}

	//
	// Accessors
	//

	public boolean isFaulty() {
		return this.getMyCurrentState().isFaulty();
	}

	//
	// Behavior
	//

	//	@MessageHandler
	//	@NotificationEnvelope
	//	public void updateAgentInfo(final NotificationMessage<ReplicaState> r) {
	//		this.getMyInformation().add(r.getNotification());
	//	}

	@MessageHandler
	@NotificationEnvelope(SimpleObservationService.informationObservationKey)
	public <Info extends Information> void receiveInformation(
			final NotificationMessage<Information> o) {
		//		logMonologue("yophoi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
		//		if (o.getNotification() instanceof ReplicaState && getMyCurrentState().Ihost(((ReplicaState)o.getNotification()).getMyAgentIdentifier())){
		//			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
		//			HostState h = new HostState(getMyCurrentState(),
		//					(ReplicaState) o.getNotification(), getMyCurrentState().getCreationTime());
		//			//on remet ce nouveau state dans h
		//			h = new HostState(h, (ReplicaState) o.getNotification(), getMyCurrentState().getCreationTime());
		//			setNewState(h);
		//		}
	}

	//	public Double getReliability(final AgentIdentifier id)
	//			throws NoInformationAvailableException {
	//		return getMyInformation()
	//				.getInformation(ReplicaState.class, id).getMyReliability();
	//	}
	//
	//	public Double getMemCharge(final AgentIdentifier id)
	//			throws NoInformationAvailableException {
	//		return getMyInformation()
	//				.getInformation(ReplicaState.class, id).getMyMemCharge();
	//	}
	//
	//	public Double getProcCharge(final AgentIdentifier id)
	//			throws NoInformationAvailableException {
	//		return getMyInformation()
	//				.getInformation(ReplicaState.class, id).getMyProcCharge();
	//	}


	// @Override
	// protected EndInfo getMyEndNotif() {
	// return new HostEndInfo(getMyCurrentState(),getCreationTime());
	// }

	//
	// Subclass
	//


}

// @Override
// public void reset() {
// this.resetUptime();
// this.myReplicatedAgents.clear();
// this.memCurrentCharge=0.;
// this.procCurrentCharge=0.;
//
// }

// //Take all fields
// public HostState(final ResourceIdentifier myAgent,
// double lambda,
// final Double procChargeMax,
// final Double memChargeMax) {
// this(myAgent,
// new HashMap<AgentIdentifier, Double>(), lambda,
// procChargeMax, 0.,
// memChargeMax, 0., false);
// }

// /*
// * Global Information Service
// */
//
// @StepComposant(ticker=NegotiationParameters._quantileInfoFrequency)
// public void informSystemState(){
// ((HostCore) this.myCore).notifyTercile();
// }
//
// @MessageHandler
// @NotificationEnvelope
// public void receiveState(final NotificationMessage<AgentInfo> n){
// ((HostCore) this.myCore).receiveAgentInfo(n.getNotification());
// }

// @MessageHandler
// @NotificationEnvelope
// public void faultObservation(final NotificationMessage<FaultEvent> m) {
// Collection<ReplicaState> temp = new ArrayList<ReplicaState>();
// temp.addAll(((HostCore) myCore).systemState);
// ((HostCore) myCore).systemState.clear();
//
// for (ReplicaState r : temp)
// ((HostCore) myCore).systemState.add(r.update(m.getNotification().getHost()));
//
// }
// @StepComposant(ticker=1000)
// @Transient
// public boolean start(){
// appliHasStarted=true;
// return true;
// }

//
//
//
// public class HostInfo implements Comparable<HostInfo>, Serializable{
//
// /**
// *
// */
// private static final long serialVersionUID = 5816609996414146967L;
// Double myCharge;
// Double myDispo;
// ResourceIdentifier myId;
// public boolean faulty;
//
// public HostInfo(){
// this.myCharge= NegotiatingHost.this.getMyCurrentState().getMyCharge();
// this.myDispo=NegotiatingHost.this.getMyCurrentState().getDisponibility();
// this.myId=NegotiatingHost.this.getMyCurrentState().getMyAgentIdentifier();
// this.faulty=NegotiatingHost.this.mustDeclareFaulty;
// }
//
//
// public double getMyCharge() {
// return this.myCharge;
// }
//
// public double getDisponibility() {
// return this.myDispo;
// }
//
// public ResourceIdentifier getMyAgentIdentifier() {
// return this.myId;
// }
//
//
// @Override
// public int compareTo(final HostInfo that) {
// return this.myDispo.compareTo(that.myDispo);
// }
//
// @Override
// public String toString(){
// return
// "\nHOST="+this.myId+
// "\n * charge : "+this.myCharge+
// "\n * dispo : "+this.myDispo;
// }
// }
// @StepComposant(ticker=NegotiationParameters._host_maxFaultfrequency)
// public void testFault(){
// if (LocalFipaScheduler.step>=30 && LocalFipaScheduler.step<40
// && !iMFaulty
// // && getIdentifier().equals(new ResourceIdentifier("HostManager_1", 77))
// ){
// this.iMFaulty=true;
// logMonologue("I'm faulty");
// setNewState(
// ((HostCore) myCore).new HostState(getIdentifier(),
// getMyCurrentState().procChargeMax,getMyCurrentState().memChargeMax,
// getMyCurrentState().lambdaFault, getMyCurrentState().kFault,
// getMyCurrentState().lambdaRepair, getMyCurrentState().kRepair));
// this.notify(new FaultEvent());
// }
// if (LocalFipaScheduler.step>=40
// && iMFaulty
// // && getIdentifier().equals(new ResourceIdentifier("HostManager_1", 77))
// ){
// this.iMFaulty=false;
// logMonologue("I'm repaired");
// setNewState(
// ((HostCore) myCore).new HostState(getIdentifier(),
// getMyCurrentState().procChargeMax,getMyCurrentState().memChargeMax,
// getMyCurrentState().lambdaFault, getMyCurrentState().kFault,
// getMyCurrentState().lambdaRepair, getMyCurrentState().kRepair));
// this.notify(new RepairEvent());
//
// }
// }