package dimaxx.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;

import negotiation.faulttolerance.experimentation.ReplicationResultAgent;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.services.BasicAgentCommunicatingCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.shells.BasicCompetentAgent;
import dimaxx.experimentation.ObservingSelfService.ActivityLog;
import dimaxx.tools.aggregator.HeavyAggregation;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightAverageDoubleAggregation;

public abstract class ObservingGlobalService<Agent extends Laborantin>
extends BasicAgentCommunicatingCompetence<Agent>{
	private static final long serialVersionUID = -2893635425783775245L;

	//
	// Fields
	//

	protected HashSet<ReplicationResultAgent> finalStates = new HashSet();

	final Collection<AgentIdentifier> remainingAgent=new ArrayList<AgentIdentifier>();
	//	final Collection<AgentIdentifier> remainingHost=new ArrayList<AgentIdentifier>();

	//
	// Constants
	//

	public static final long _state_snapshot_frequency = ExperimentationParameters._maxSimulationTime / 2;

	//
	// Abstract
	//

	public abstract void initiate();

	protected void setObservation() {
		for (final BasicCompetentAgent ag : this.getMyAgent().getAgents()) {
			ag.addObserver(this.getIdentifier(), ActivityLog.class);
		}
	}

	//	protected abstract void updateHostInfo(ExperimentationResults notification);
	//
	//	protected abstract void updateAgentInfo(ExperimentationResults notification);
	protected abstract void updateInfo(ExperimentationResults notification);


	/**
	 *
	 * @return true when the simulation is ended
	 * must ensure every agent is destroyed!!!
	 */
	protected abstract boolean simulationHasEnded();

	protected abstract void writeResult();

	//
	// Methods
	//


	public Collection<AgentIdentifier> getAliveAgents() {
		return this.remainingAgent;
	}



	//
	// Behavior
	//

	@ProactivityInitialisation
	public final void initiateAgents(){
		for (final AgentIdentifier id : this.getMyAgent().agents.keySet()) {
			//			if (id instanceof ResourceIdentifier) {
			//				this.remainingHost.add(id);
			//			} else {
			this.remainingAgent.add(id);
			//			}
		}
	}


	@MessageHandler
	@NotificationEnvelope
	public final void receiveResult(final NotificationMessage<ActivityLog> l){

		final LinkedList<ExperimentationResults> results =
				l.getNotification().getResults();

		assert this.verification(results);

		for (final ExperimentationResults r : results) {
			this.updateInfo(r);

			if (r.isLastInfo()){
				this.setAgentHasEnded(r.getId());

				this.logMonologue(r.getId()
						+" has finished!, " +
						"\n * remaining agents "+this.remainingAgent.size()
						//					+"\n * remaining hosts "+remainingHost.size()
						,LogService.onFile);
			}
		}

		if (results.getLast() instanceof ReplicationResultAgent) {
			this.finalStates.add((ReplicationResultAgent) results.getLast());
		}

	}

	public void setAgentHasEnded(final AgentIdentifier id){
//		getMyAgent().logMonologue("agent is dead "+id, LogService.onBoth);
		this.remainingAgent.remove(id);
	}

	//
	// Time Primitives
	//

	public static int getNumberOfTimePoints() {
		return (int) (ExperimentationParameters._maxSimulationTime / ObservingGlobalService._state_snapshot_frequency) +1;//le +1 est l'état initial
	}

	public static int getTimeStep(final ExperimentationResults ag) {
		return Math
				.max(0,
						(int) (ag.getUptime() / ObservingGlobalService._state_snapshot_frequency) - 1);
	}

	public static Long geTime(final int i) {
		return (i)
				* ObservingGlobalService._state_snapshot_frequency;
	}

	public long getMaxSimulationTime() {
		return ExperimentationParameters._maxSimulationTime;
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
		for (final Double d :  agent_values) {
			variable.add(d);
		}
		if (!variable.isEmpty() && variable.getWeightOfAggregatedElements()>(int) (significatifPercent*totalNumber)) {
			result += variable.getMinElement()+";\t " +
					variable.getQuantile(1,3)+";\t " +
					variable.getMediane()+";\t " +
					variable.getQuantile(2,3)+";\t " +
					variable.getMaxElement()+";\t " +
					//							variable.getSum()+";\t " +
					variable.getRepresentativeElement()+";\t " +
					variable.getWeightOfAggregatedElements()/totalNumber+"\n";
		} else {
			result += "-;\t-;\t-;\t-;\t-;\t-  ("+variable.getWeightOfAggregatedElements()/totalNumber+")\n";
		}

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
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++){
			result += ObservingGlobalService.geTime(i)/1000.+" ;\t ";
			if (variable[i].getWeightOfAggregatedElements()>significatifPercent*totalNumber) {
				result +=
						variable[i].getMinElement()+";\t " +
								variable[i].getQuantile(1,3)+";\t " +
								variable[i].getMediane()+";\t " +
								variable[i].getQuantile(2,3)+";\t " +
								variable[i].getMaxElement()+";\t " +
								//						variable[i].getSum()+";\t " +
								variable[i].getRepresentativeElement()+";\t (" +
								variable[i].getWeightOfAggregatedElements()/totalNumber+")\n";
			} else {
				result += "-;\t-;\t-;\t-;\t-;\t-;\t  ("+variable[i].getWeightOfAggregatedElements()/totalNumber+")\n";
			}
		}
		return result;
	}

	public static  String getMeanTimeEvolutionObs(final ExperimentationParameters p, final String entry, final LightAverageDoubleAggregation[] variable,
			final double significatifPercent, final int totalNumber){
		String result = "t (seconds);\t "+entry+" ;\t percent of agent aggregated=\n";
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++){
			result += ObservingGlobalService.geTime(i)/1000.+" ;\t ";
			if (variable[i].getNumberOfAggregatedElements()>significatifPercent*totalNumber) {
				result+=variable[i].getRepresentativeElement()+";\t (" +
						(double) variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			} else {
				result += "-;\t ("+(double)variable[i].getNumberOfAggregatedElements()/(double)  totalNumber+")\n";
			}
		}
		return result;
	}

	public static  Double getPercent(final int value, final int total){
		return (double) value/(double) total*100;
	}

	//
	// Assertion Primitives
	//

	//VERIFICATION
	HashSet<AgentIdentifier> alreadyReceived=null;
	ArrayList<HashSet<AgentIdentifier>> received=null;
	public boolean verification(final LinkedList<ExperimentationResults> results ){
		if (this.alreadyReceived==null){ //firstTime
			this.alreadyReceived= new HashSet<AgentIdentifier>();
			this.received = new ArrayList<HashSet<AgentIdentifier>>();
			for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
				this.received.add(new HashSet<AgentIdentifier>());
			}
		}

		//		assert !received.get(i).contains(ag.getId());
		//		received.get(i).add(ag.getId());

		final ExperimentationResults resultType= results.getLast();
		assert (!this.alreadyReceived.contains(resultType.getId())):"argh! already received"+resultType.getId();
		this.alreadyReceived.add(resultType.getId());
		assert (results.size()<=ObservingGlobalService.getNumberOfTimePoints()):"arg : "+results.size();
//		String result ="Agent has sended results \n"+resultType.getId()+"   "+_state_snapshot_frequency+" :";
//		for (final ExperimentationResults r : results){
//			result+=r.getUptime()+"  "+getTimeStep(r)+"\n";
//		}
//		getMyAgent().logMonologue(result, LogService.onBoth);
		return true;
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