package negotiation.faulttolerance.experimentation;

import negotiation.faulttolerance.faulsimulation.FaultObservationService;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityFinalisation;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.PatternObserverWithHookservice.EventHookedMethod;
import dimaxx.experimentation.ExperimentationResults;
import dimaxx.experimentation.ObservingSelfService;

public class Host
extends	SimpleNegotiatingAgent<ReplicationSpecification, HostState, ReplicationCandidature>
{
	private static final long serialVersionUID = -8478683967125467116L;

	//
	// Fields
	//
	private long firstModifTime=-2;
	private long lastModifTime=-1;

	@Competence
	ObservingSelfService mySelfObservationService = new ObservingSelfService() {

		/**
		 *
		 */
		private static final long serialVersionUID = -6008018665463786541L;

		@Override
		protected ExperimentationResults generateMyResults() {
			return new ReplicationResultHost(
					Host.this.getMyCurrentState(),firstModifTime,lastModifTime,
					Host.this.getCreationTime(),initialStateNumber);
		}
	};

	@Competence
	public
	FaultObservationService<ReplicationSpecification, HostState, ReplicationCandidature> myFaultAwareService =
	new FaultObservationService<ReplicationSpecification, HostState, ReplicationCandidature>() {

		/**
		 *
		 */
		private static final long serialVersionUID = -5530153574167669156L;

		@Override
		protected void resetMyState() {
			Host.this.setNewState(new HostState((ResourceIdentifier) this.getIdentifier(),
					Host.this.getMyCurrentState().getProcChargeMax(),
					Host.this.getMyCurrentState().getMemChargeMax(),
					Host.this.getMyCurrentState().getLambda(),this.getMyAgent().getMyCurrentState().getStateCounter()+1));
			//			this.resetMyUptime();
		}

		@Override
		protected void resetMyUptime() {

			assert 1<0:"Host.this.getMyCurrentState().resetUptime()";
		}

	};

	//
	// Constructor
	//


	public Host(
			final ResourceIdentifier id,
			final HostState myState,
			final RationalCore myRationality,
			final SelectionCore participantCore,
			final ProposerCore 	proposerCore,
			final ObservationService myInformation,
			final AbstractCommunicationProtocol protocol)
					throws CompetenceException {
		super(id, myState, myRationality, participantCore, proposerCore, myInformation, protocol);
	}


	@EventHookedMethod(HostState.class)
	public void updateStateStatus(HostState h){
		if (firstModifTime==-2){
			assert h.getStateCounter()==initialStateNumber;
			firstModifTime=-1;	
		}else if (firstModifTime==-1){
			assert h.getStateCounter()==initialStateNumber+1;
			firstModifTime=getUptime();		
		}
		
		lastModifTime=getUptime();
	}
	//
	// Behavior
	//

	//	@ProactivityFinalisation
	//	public void thisIsZiEnd(){
	//		this.logMonologue("Ze end : "+this.getMyCurrentState().getMyResourceIdentifiers()+this.getMyCurrentState(), LogService.onBoth);
	////		this.logMonologue("Ze end : "+this.getMyCurrentState(), LogService.onBoth);
	//	}
}




//new HostState(id, hostMaxProc, hostMaxMem, lambda,-1)




//
// Accessors
//

//	public boolean isFaulty() {
//		return this.getMyCurrentState().isFaulty();
//	}

//
// Behavior
//

//	@MessageHandler
//	@NotificationEnvelope
//	public void updateAgentInfo(final NotificationMessage<ReplicaState> r) {
//		this.getMyInformation().add(r.getNotification());
//	}

//	@MessageHandler
//	@NotificationEnvelope(SimpleObservationService.informationObservationKey)
//	public <Info extends Information> void receiveInformation(
//			final NotificationMessage<Information> o) {
//		logMonologue("yophoi!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!");
//		if (o.getNotification() instanceof ReplicaState && getMyCurrentState().Ihost(((ReplicaState)o.getNotification()).getMyAgentIdentifier())){
//			//On supprime le state qu'on vient d'ajouter dans le but de le mettre a jour
//			HostState h = new HostState(getMyCurrentState(),
//					(ReplicaState) o.getNotification(), getMyCurrentState().getCreationTime());
//			//on remet ce nouveau state dans h
//			h = new HostState(h, (ReplicaState) o.getNotification(), getMyCurrentState().getCreationTime());
//			setNewState(h);
//		}
//	}

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


//}

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