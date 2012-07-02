package dimaxx.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.LinkedList;

import negotiation.negotiationframework.SimpleNegotiatingAgent;
import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.PostStepComposant;
import dima.introspectionbasedagents.annotations.PreStepComposant;
import dima.introspectionbasedagents.annotations.ProactivityFinalisation;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.annotations.ResumeActivity;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.annotations.Transient;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;

public abstract class ObservingSelfService
extends BasicCommunicatingCompetence<SimpleNegotiatingAgent<?, ?,?>>{
	private static final long serialVersionUID = 496384107474313690L;


	ActivityLog l = new ActivityLog();
	public static final String observationLog = "log key for self observing service agents";

	public ObservingSelfService() {
		super();
	}


	//
	//
	//

	protected abstract ExperimentationResults generateMyResults();

	@ProactivityInitialisation
	public void firstStateInit(){
		this.l.add(this.generateMyResults());
	}

	@PostStepComposant(ticker=ObservingGlobalService._state_snapshot_frequency)
	public void notifyMyState(){
		this.l.add(this.generateMyResults());
	}

	//
	//
	//

	boolean simulationEnded=false;
	@PreStepComposant(ticker=ExperimentationParameters._maxSimulationTime)
	@Transient
	boolean endSimulation(){
		assert !simulationEnded;
		this.logMonologue("this is the end my friend",ObservingSelfService.observationLog);
		simulationEnded=true;
		
		//send notifs
		this.l.getResults().getLast().setLastInfo();
		this.notify(this.l);
		this.getMyAgent().sendNotificationNow();

		//end activity
		getMyAgent().setActive(false);
		return true;
	}

	@MessageHandler
	public void simulationEndORder(final SimulationEndedMessage s){
		logMonologue("recieving end simulation order",ObservingSelfService.observationLog);
		assert simulationEnded;
		getMyAgent().setAlive(false);
	}
	
	@PreStepComposant(ticker=ExperimentationParameters._maxSimulationTime+75000)
	public void killForced(){
		logWarning("kill forced");
		assert simulationEnded;
		getMyAgent().setAlive(false);
		
	}
	
	@ResumeActivity
	//allow to continue to receive messages
	public void tryToResumeActivity(){
		if (getMyAgent().hasAppliStarted()) logMonologue("resuming", ObservingSelfService.observationLog);
		final Collection<AbstractMessage> messages = new ArrayList<AbstractMessage>();
		while (getMyAgent().getMailBox().hasMail()){
			final AbstractMessage m = getMyAgent().getMailBox().readMail();
			if (m instanceof SimulationEndedMessage) {
				logMonologue("recieving end simulation order in resuming",ObservingSelfService.observationLog);
				simulationEndORder((SimulationEndedMessage)m);
			} else {
//				logMonologue("ignoring "+m+" in resuming",ObservingSelfService.observationLog);
				messages.add(m);
			}
		}
		for (final AbstractMessage m : messages) {
			getMyAgent().getMailBox().writeMail(m);
		}
		getMyAgent().wwait(1000);
	}

	//
	// Public class
	//

	public class ActivityLog extends Message {
		private static final long serialVersionUID = -1828186933572497512L;

		LinkedList<ExperimentationResults> results =
				new LinkedList<ExperimentationResults>();

		public LinkedList<ExperimentationResults> getResults() {
			return this.results;
		}

		public void add(final ExperimentationResults generateMyResults) {
			this.results.add(generateMyResults);
		}
	}
}




//
//	@StepComposant(ticker=StaticParameters._simulationTime)
//	@Transient
//	public boolean endSimulation(){
//		this.logMonologue("this is the end my friend");
//		final Double lastActionTime = new Double(new Date().getTime() - this.lastAction.getTime());
//		this.notify(new AgentEndProtocolObs(
//				this.getIdentifier(),
//				this.protoTime.getRepresentativeElement(),
//				Math.max(this.getMyCurrentState().getMyMemCharge(), this.getMyCurrentState().getMyProcCharge()),
//				this.getMyCurrentState().getMyStateStatus(),
//				lastActionTime,
//				this.getMyCurrentState().getMyReplicas().size(),
//				this.getMyCurrentState().getMyReliability(),
//				this.getMyCurrentState().getMyDisponibility(),
//				this.getMyCurrentState().getMyCriticity(),
//				this.iAMDead));
//		this.notify(new AgentInfo());
//		this.observer.autoSendOfNotifications();
//		this.setAlive(false);
//		return true;
//	}
//
//	@StepComposant(ticker=StaticParameters._simulationTime)
//	@Transient
//	public boolean endSimulation(){
//		//		if (new Date().getTime() - StepTickersParameters.creation.getTime()>NegotiationSimulationParameters._simulationTime){
//		this.notify(new HostInfo());
//		this.notify(new HostEnd());
//		this.observer.autoSendOfNotifications();
//		this.setAlive(false);
//		return true;
//		//		} else
//		//			return false;
//	}

//
//@StepComposant(ticker=StaticParameters._state_snapshot_frequency)
//public void notifyMyStateToGlobalServiceLeRetour(){
//	this.notify(new HostInfo());
//	this.mustDeclareFaulty=false;
//}
//
//public void notifyMyStateToGlobalServiceLeRetour(){
//	//		logMonologue(getMyCurrentState().getMyReliability()+" "+getMyCurrentState().getMyReplicas().isEmpty());
//	this.notify(new AgentInfo(),Laborantin.simulationResultStateObservationKey);
//}
//
//
//@MessageHandler
//@NotificationEnvelope
//public void systemInfoUpdate(final NotificationMessage<SystemInformationMessage> m) {
//	((CandidatureReplicaCore) this.myCore).beNotified(m.getNotification());
//	//		if (m.getSender().toString().equals("#HOST_MANAGER##simu_0#HostManager_0:77")&&m.getReceiver().toString().equals("#simu_0#DomainAgent_4"))
//	//			logMonologue(
//	//					"i've received "+m.getNotification()
//	//					+"\n myRelia="+getMyCurrentState().getMyReliability()+"("+getMyCurrentState().getMyStateStatus()+")"
//	//					+"\n aggregated="+((ReplicaCore) this.myCore).getFirstReliabilityTercile()+" , "+((ReplicaCore) this.myCore).getLastReliabilityTercile());
//
//	//		if (getMyCurrentState().getMyStateStatus().equals(AgentStateStatus.Wastefull))
//	//			logMonologue("I've becomed wastefull =o");
//	//		logMonologue("firstTercile="+((ReplicaCore) myCore).getFirstReliabilityTercile()
//	//				+"\n lastTercile="+((ReplicaCore) myCore).getLastReliabilityTercile()
//	//				+"\n my relia+status="+getMyCurrentState().getMyReliability()+" "+getMyCurrentState().getMyStateStatus());
//}
//


//
//Date protocolInitiationTime = null;
//AverageDoubleAggregator protoTime = new AverageDoubleAggregator();
//public Date lastAction = new Date();
//public boolean iAMDead=false;
//private boolean iveSarted=false;
//
//@Override
//public void initiateNegotiation(){
//	super.initiateNegotiation();
//	this.protocolInitiationTime = new Date();
//}
//
//@Override
//public void execute(final ReplicationCandidature c){
//	super.execute(c);
//	//		this.observe(c.getResource(), SystemInformationMessage.class);
//	if (this.protocolInitiationTime!=null){
//		this.protoTime.add( new Double(new Date().getTime() - this.protocolInitiationTime.getTime()));
//		this.protocolInitiationTime = null;
//	}
//	this.lastAction = new Date();
//	this.iveSarted=true;
//	this.iAMDead=false;
//
//}
//
///*
// *
// */
//
//@StepComposant(ticker=StaticParameters._quantileInfoFrequency)
//public void notifyMyStateToGlobalService(){
//	//		logMonologue(getMyCurrentState().getMyReliability()+" "+getMyCurrentState().getMyReplicas().isEmpty());
//	this.notify(new AgentInfo());
//}