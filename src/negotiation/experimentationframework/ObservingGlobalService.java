package negotiation.experimentationframework;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import negotiation.experimentationframework.ObservingSelfService.ActivityLog;
import negotiation.faulttolerance.experimentation.ReplicationLaborantin;
import negotiation.faulttolerance.experimentation.ReplicationResultAgent;
import negotiation.faulttolerance.negotiatingagent.Host;
import negotiation.faulttolerance.negotiatingagent.Replica;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.services.BasicAgentCompetence;
import dima.introspectionbasedagents.services.UnrespectedCompetenceSyntaxException;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;
import dimaxx.tools.mappedcollections.HashedHashSet;

public abstract class ObservingGlobalService
extends BasicAgentCompetence<Laborantin>{


	/**
	 * 
	 */
	private static final long serialVersionUID = -2893635425783775245L;
	private final ExperimentationParameters p;
	protected HashSet<ReplicationResultAgent> finalStates = new HashSet();

	public ObservingGlobalService(final Laborantin ag, final ExperimentationParameters p)
			throws UnrespectedCompetenceSyntaxException {
		super(ag);
		this.p = p;
	}

	//
	// Accessors
	//

	protected ExperimentationParameters getSimulationParameters() {
		return this.p;
	}

	//
	// Abstract
	//

	public abstract void initiate();

	protected abstract void updateAgentInfo(ExperimentationResults notification);

	protected abstract void updateHostInfo(ExperimentationResults notification);

	protected abstract void writeResult();

	//
	// Methods
	//


	protected void setObservation(){
		//Use to print at the end of the method the observation graph
		final Collection<AgentIdentifier> observedHostResultLog  =
				new ArrayList<AgentIdentifier>();
		final Collection<AgentIdentifier> observedRepResultLog  =
				new ArrayList<AgentIdentifier>();
		final Collection<AgentIdentifier> reliabilityStatusLog  =
				new ArrayList<AgentIdentifier>();
		final HashedHashSet<AgentIdentifier, AgentIdentifier> opinionsLog =
				new HashedHashSet<AgentIdentifier, AgentIdentifier>();
		//Use to print at the end of the method the observation graph

		//Activating status observation
		if (this.p._usedProtocol.equals(ExperimentationProtocol.getKey4centralisedstatusproto())
				|| this.p._usedProtocol.equals(ExperimentationProtocol.getKey4statusproto()))
			this.getMyAgent().myStatusObserver.setActive(true);
		else
			this.getMyAgent().myStatusObserver.setActive(false);


		for (final BasicCompetentAgent ag : this.getMyAgent().agents.values())
			//Observation about agent
			if (ag instanceof Replica){
				//Observation de l'évolution des états de l'agent
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedRepResultLog.add(ag.getIdentifier());

				//
				if (this.p._usedProtocol.equals(ExperimentationProtocol.getKey4centralisedstatusproto())){
					//I aggregate agents reliability
					ag.addObserver(this.getIdentifier(), ObservingStatusService.reliabilityObservationKey);//this.addObserver(ag.getIdentifier(),ObservingStatusService.reliabilityObservationKey);???
					reliabilityStatusLog.add(ag.getIdentifier());
					//I forward my opinion to every agents
					this.addObserver(ag.getIdentifier(), SimpleOpinionService.opinionObservationKey);
					opinionsLog.add(ag.getId(), this.getIdentifier());
				} else if (this.getSimulationParameters()._usedProtocol.equals(ExperimentationProtocol.getKey4statusproto()))
					//This agent observe every agents that it knows
					for (final AgentIdentifier h :	((Replica)ag).getMyInformation().getKnownAgents()){
						this.getMyAgent().getAgent(h).addObserver(ag.getId(), SimpleOpinionService.opinionObservationKey);
						opinionsLog.add(ag.getId(), h);
					}
				else if (this.getSimulationParameters()._usedProtocol.equals(ExperimentationProtocol.getKey4mirrorproto())){
					//no observation
				}
				else throw new RuntimeException("impossible : ");
			}else if (ag instanceof Host){
				//Observation de l'évolution des états de l'hpte
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedHostResultLog.add(ag.getIdentifier());
				// this.myFaultService.addObserver(h.getId(), FaultEvent.class);
				// this.myFaultService.addObserver(h.getId(), RepairEvent.class)
			} else if (ag instanceof ReplicationLaborantin)
				this.logMonologue("C'est moi!!!!!!!!!! =D",LogService.onFile);
			else
				throw new RuntimeException("impossible");

		String mono = "Setting observation :"
				+"\n * I observe results of "+observedHostResultLog
				+"\n * I observe results of      "+observedRepResultLog
				+"\n * I observe reliability of  "+reliabilityStatusLog;
		for (final AgentIdentifier id : opinionsLog.keySet())
			mono += "\n * "+id+" observe opinon of "+opinionsLog.get(id);
				this.logMonologue(mono,LogService.onFile);

	}



	//
	// Behavior
	//


	//		//VERIFICATION
	//		HashSet<AgentIdentifier> alreadyReceived = new HashSet<AgentIdentifier>();
	//		public static ArrayList<HashSet<AgentIdentifier>> received;
	//		static {
	//			received = new ArrayList<HashSet<AgentIdentifier>>();
	//			for (int i = 0; i < ExperimentationParameters.getNumberOfTimePoints(); i++)
	//				received.add(new HashSet<AgentIdentifier>());
	//		}
	//		assert !received.get(i).contains(ag.getId());
	//		received.get(i).add(ag.getId());
	//		//VERIFICATION
	@MessageHandler
	@NotificationEnvelope
	public final void receiveResult(final NotificationMessage<ActivityLog> l){

		final LinkedList<ExperimentationResults> results =
				l.getNotification().getResults();

		//Verfication
		//		ExperimentationResults resultType= results.getLast();
		//		assert (!alreadyReceived.contains(resultType.getId())):"argh! already received"+resultType.getId();
		//		alreadyReceived.add(resultType.getId());
		//		assert (results.size()<=p.getNumberOfTimePoints()):"arg : "+results.size();
		//		System.out.println("\n"+resultType.getId()+"   "+ExperimentationProtocol._state_snapshot_frequency+" :");
		//		for (final ExperimentationResults r : results){
		//			System.out.println(r.getUptime()+"  "+ExperimentationParameters.getTimeStep(r));
		//		}
		//verfication


		for (final ExperimentationResults r : results)
			this.updateInfo(r);

				if (results.getLast() instanceof ReplicationResultAgent)
					this.finalStates.add((ReplicationResultAgent) results.getLast());

	}

	private void updateInfo(final ExperimentationResults r) {
		if (r.isHost())
			this.updateHostInfo(r);
		else
			this.updateAgentInfo(r);

		if (r.isLastInfo()){
			if (r.isHost())
				this.getMyAgent().remainingHost.remove(r.getId());
			else
				this.getMyAgent().remainingAgent.remove(r.getId());

			this.logMonologue(r.getId()
					+" has finished!, " +
					"\n * remaining agents "+this.getMyAgent().remainingAgent.size()+
					"\n * remaining hosts "+this.getMyAgent().remainingHost.size(),LogService.onFile);
		}
	}
	//
	// Writing Primitives
	//

	public static String getQuantilePointObs(
			final String entry,
			final Collection<Double> agent_values, final double significatifPercent, final int totalNumber){
		String result ="t (seconds);\t "+
				entry+" min;\t "
				+entry+" firstTercile;\t "
				+entry+"  mediane;\t  "
				+entry+" lastTercile;\t "
				+entry+"  max ;\t "
				//				+entry+" sum ;\t "
				+entry+" mean ;\t percent of agent aggregated=\n";
		final HeavyAggregation<Double> variable = new HeavyDoubleAggregation();
		for (final Double d :  agent_values)
			variable.add(d);
				if (!variable.isEmpty() && variable.getWeightOfAggregatedElements()>(int) (significatifPercent*totalNumber))
					result += variable.getMinElement()+";\t " +
							variable.getQuantile(1,3)+";\t " +
							variable.getMediane()+";\t " +
							variable.getQuantile(2,3)+";\t " +
							variable.getMaxElement()+";\t " +
							//							variable.getSum()+";\t " +
							variable.getRepresentativeElement()+";\t " +
							variable.getWeightOfAggregatedElements()/totalNumber+"\n";
				else
					result += "-;\t-;\t-;\t-;\t-;\t-  ("+variable.getWeightOfAggregatedElements()/totalNumber+")\n";

				return result;
	}

	public static  String getQuantileTimeEvolutionObs(final ExperimentationParameters p, final String entry,
			final HeavyAggregation<Double>[] variable, final double significatifPercent, final int totalNumber){
		String result ="t (seconds);\t "+
				entry+" min;\t "
				+entry+" firstTercile;\t "
				+entry+"  mediane;\t  "
				+entry+" lastTercile;\t "
				+entry+"  max ;\t "
				//				+entry+" sum ;\t "
				+entry+" mean ;\t percent of agent aggregated=\n";
		for (int i = 0; i < ExperimentationParameters.getNumberOfTimePoints(); i++){
			result += p.geTime(i)/1000.+" ;\t ";
			if (variable[i].getWeightOfAggregatedElements()>significatifPercent*totalNumber)//!variable[i].isEmpty() &&
				result +=
				variable[i].getMinElement()+";\t " +
						variable[i].getQuantile(1,3)+";\t " +
						variable[i].getMediane()+";\t " +
						variable[i].getQuantile(2,3)+";\t " +
						variable[i].getMaxElement()+";\t " +
						//						variable[i].getSum()+";\t " +
						variable[i].getRepresentativeElement()+";\t (" +
						variable[i].getWeightOfAggregatedElements()/totalNumber+")\n";
			else
				result += "-;\t-;\t-;\t-;\t-;\t-;\t  ("+variable[i].getWeightOfAggregatedElements()/totalNumber+")\n";
		}
		return result;
	}

	public static  String getMeanTimeEvolutionObs(final ExperimentationParameters p, final String entry, final LightAverageDoubleAggregation[] variable,
			final double significatifPercent, final int totalNumber){
		String result = "t (seconds);\t "+entry+" ;\t percent of agent aggregated=\n";
		for (int i = 0; i < ExperimentationParameters.getNumberOfTimePoints(); i++){
			result += p.geTime(i)/1000.+" ;\t ";
			if (variable[i].getNumberOfAggregatedElements()>significatifPercent*totalNumber)
				result+=variable[i].getRepresentativeElement()+";\t (" +
						(double) variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			else
				result += "-;\t ("+(double)variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
		}
		return result;
	}

	public static  Double getPercent(final int value, final int total){
		return (double) value/(double) total*100;
	}
}



//@MessageHandler
//@NotificationEnvelope
//public final void receiveReplicaInfo(
//		final NotificationMessage<ReplicationAgentResult> n) {
//	assert 1<0:"not used anymore : replaced by void receiveResult(final NotificationMessage<ActivityLog> l)";
//	this.states.remove(
//			n.getNotification());
//	this.states.add(n.getNotification());
//	this.updateInfo(n.getNotification());
//}
//
//@MessageHandler
//@NotificationEnvelope
//public final void receiveHostInfo(
//		final NotificationMessage<ReplicationHostResult> n) {
//	assert 1<0:"not used anymore : replaced by void receiveResult(final NotificationMessage<ActivityLog> l)";
//	this.updateInfo(n.getNotification());
//
//}
