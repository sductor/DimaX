package frameworks.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;



import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.MessageHandler;
import dima.introspectionbasedagents.annotations.ProactivityInitialisation;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.modules.aggregator.AbstractMinMaxAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightAverageDoubleAggregation;
import dima.introspectionbasedagents.services.BasicCommunicatingCompetence;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.observingagent.NotificationMessage;
import dima.introspectionbasedagents.services.observingagent.NotificationEnvelopeClass.NotificationEnvelope;
import frameworks.experimentation.ObservingSelfService.ActivityLog;

public abstract class ObservingGlobalService<Agent extends Laborantin>
extends BasicCommunicatingCompetence<Agent>{
	private static final long serialVersionUID = -2893635425783775245L;

	//
	// Fields
	//

	public final ExperimentationParameters rep;
	private final HashSet<ExperimentationResults> finalStates = new HashSet<ExperimentationResults>();


	final Collection<AgentIdentifier> allAgent=new ArrayList<AgentIdentifier>();
	final Collection<AgentIdentifier> remainingAgent=new ArrayList<AgentIdentifier>();
	//	final Collection<AgentIdentifier> remainingHost=new ArrayList<AgentIdentifier>();


	public ObservingGlobalService(
			final ExperimentationParameters rep) {
		super();
		this.rep = rep;
	}

	//
	// Constants
	//

	public ExperimentationParameters getSimulationParameters() {
		return this.rep;
	}

	public static final long _state_snapshot_frequency = ExperimentationParameters._maxSimulationTime / 60;

	//
	// Abstract
	//

	public abstract void initiate();

	protected void setObservation() {
		for (final CompetentComponent ag : this.getMyAgent().getAgents()) {
			ag.addObserver(this.getIdentifier(), ActivityLog.class);
		}
	}

	//	protected abstract void updateHostInfo(ExperimentationResults notification);
	//
	//	protected abstract void updateAgentInfo(ExperimentationResults notification);
	protected abstract void updateInfo(ExperimentationResults notification);



	protected boolean allAgentDied(){
		for (final AgentIdentifier myAgent : this.allAgent){
			if (this.getMyAgent().api.getAllAgents().contains(myAgent)) {
				return false;
			}
		}
		return true;
	}


	/**
	 *
	 * @return true when the simulation is ended
	 * must ensure every agent is destroyed!!!
	 */
	public boolean simulationHasEnded(){
		if (this.allAgentDied()){
			this.logMonologue("ALL AGENTS DIED!!!",LogService.onBoth);
			this.logWarning("ALL AGENTS DIED!!!",LogService.onBoth);
			return true;
		}else if (this.getActiveAgents().size()==0){
			if (!this.endRequestSended){
				this.logMonologue("Every agent has finished!!...killing....",LogService.onBoth);
				for (final AgentIdentifier r : this.getAllAgents()) {
					this.sendMessage(r, new SimulationEndedMessage(this));
					//					this.logWarning("Every agent has finished!!...killing... sending to "+r,LogService.onBoth);
				}
				this.endRequestSended=true;
			}
			return false;
		}else if (this.getMyAgent().getUptime()>this.timeBeforeForcingSimulationEnd()){
			//			if (!endRequestSended){
			if (!this.shouldHAveEndedActivated) {
				this.logWarning("FORCING END REQUEST!!!! I SHOULD HAVE ALREADY END!!!!(rem ag, rem host)="+this.getActiveAgents(),LogService.onBoth);
				this.shouldHAveEndedActivated=true;
				this.endRequestSended=true;
				for (final AgentIdentifier r : this.getAllAgents()) {
					this.sendMessage(r, new SimulationEndedMessage(this));
					//					this.logWarning("Every agent has finished!!...killing... sending to "+r,LogService.onBoth);
				}
			} else {
				//WAITING!!!
				//				for (final AgentIdentifier r : this.getActiveAgents()) {
				//					this.sendMessage(r, new SimulationEndedMessage(this));
				//				}
				//				}
				//				this.endRequestSended=true;
			}
			//			this.logWarning("FORCING END REQUEST!!!! remaining agents are "+this.getActiveAgents(),LogService.onBoth);
			return false;//waiting 5 more minutes
		}else if (this.getMyAgent().getUptime()>this.timeBeforeKillingSimulation()){
			this.getMyAgent().getApi().kill(this.getActiveAgents());
			this.logWarning("ENDING FORCED!!!! i should have end!!!!(rem ag, rem host)="+this.getActiveAgents(),LogService.onBoth);
			return true;
		} else {
			return false;
		}
	}

	protected long timeBeforeForcingSimulationEnd() {
		return ExperimentationParameters._maxSimulationTime+300000;//+10min/*+1min*///300000){//+5min
	}
	protected long timeBeforeKillingSimulation() {
		return ExperimentationParameters._maxSimulationTime+600000;//+5min/*+2min*///600000){//+10min
	}



	boolean endRequestSended= false;
	boolean shouldHAveEndedActivated=false ;
	protected abstract void writeResult();

	//
	// Methods
	//


	public Collection<AgentIdentifier> getActiveAgents() {
		return this.remainingAgent;
	}


	public Collection<AgentIdentifier> getAllAgents() {
		return this.allAgent;
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
			this.allAgent.add(id);
			this.remainingAgent.add(id);
			//			}
		}
	}


	@MessageHandler
	@NotificationEnvelope
	public final void receiveResult(final NotificationMessage<ActivityLog> l){

		final LinkedList<ExperimentationResults> results =
				new LinkedList(l.getNotification().getResults());

		assert this.verification(results);

		if (!results.isEmpty()){
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

			this.finalStates.add(results.getLast());
		}

	}

	public void setAgentHasEnded(final AgentIdentifier id){
		//		getMyAgent().logMonologue("agent is dead "+id, LogService.onBoth);
		assert this.remainingAgent.contains(id);
		this.remainingAgent.remove(id);
	}

	public HashSet<ExperimentationResults> getFinalStates() {
		return this.finalStates;
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
		return i
				* ObservingGlobalService._state_snapshot_frequency;
	}

	//	public long getMaxSimulationTime() {
	//		return _maxSimulationTime;
	//	}

	//
	// Writing Primitives
	//

	/**
	 * Give the agregated values of a parameter
	 */
	public static <Aggregation extends AbstractMinMaxAggregation<Double> & AbstractCompensativeAggregation<Double>>String getQuantilePointObs(
			final String entry,
			final Aggregation variable, final double significatifPercent, final int totalNumber){
		String result =entry+" min;\t "
				//				+entry+" firstTercile;\t "
				//				+entry+"  mediane;\t  "
				//				+entry+" lastTercile;\t "
				+entry+"  max ;\t "
				+entry+" prod ;\t "
				+entry+" mean ;\t percent of agent aggregated=\n";
		if (variable.getWeightOfAggregatedElements()> significatifPercent*totalNumber) {
			result += variable.getMinElement()+";\t " +
					//					variable.getQuantile(1,3)+";\t " +
					//					variable.getMediane()+";\t " +
					//					variable.getQuantile(2,3)+";\t " +
					variable.getMaxElement()+";\t " +
					variable.getProd()+";\t " +
					variable.getRepresentativeElement()+";\t " +
					"("+variable.getWeightOfAggregatedElements()/totalNumber+")\n";
		} else {
			result += "--;\t-;\t-;\t-;\t-;\t-  ("+variable.getWeightOfAggregatedElements()/totalNumber+")\n";
		}

		return result;
	}

	/**
	 * Give the agregated values of a parameter through the time
	 * @param entry
	 * @param variable
	 * @param significatifPercent
	 * @param totalNumber
	 * @return
	 */
	public static <Aggregation extends AbstractMinMaxAggregation<Double> & AbstractCompensativeAggregation<Double>> String getQuantileTimeEvolutionObs( final String entry,
			final Aggregation[] variable, final double significatifPercent, final int totalNumber){
		String result ="t (seconds);\t "+
				entry+" min;\t "
				//				+entry+" firstTercile;\t "
				//				+entry+"  mediane;\t  "
				//				+entry+" lastTercile;\t "
				+entry+" mean  ;\t "
				+entry+" max ;\t "
				+entry+" prod ;\t percent of agent aggregated=\n";
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++){
			result += ObservingGlobalService.geTime(i)/1000.+" ;\t ";
			if (variable[i].getWeightOfAggregatedElements()>significatifPercent*totalNumber) {
				result +=
						variable[i].getMinElement()+";\t " +
								//								variable[i].getQuantile(1,3)+";\t " +
								//								variable[i].getMediane()+";\t " +
								//								variable[i].getQuantile(2,3)+";\t " +
								variable[i].getRepresentativeElement()+";\t " +
								variable[i].getMaxElement()+";\t" +
								variable[i].getProd()+";\t  (" +
								variable[i].getWeightOfAggregatedElements()/totalNumber+")\n";
			} else {
				result += "-;\t-;\t-;\t-;\t-;\t-;\t  ("+variable[i].getWeightOfAggregatedElements()/totalNumber+")\n";
			}
		}
		return result;
	}

	public static  String getMeanTimeEvolutionObs(final String entry, final LightAverageDoubleAggregation[] variable,
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
		if (!results.isEmpty()){
			final ExperimentationResults resultType= results.getLast();
			assert !this.alreadyReceived.contains(resultType.getId()):"argh! already received"+resultType.getId();
			this.alreadyReceived.add(resultType.getId());
			assert results.size()<=ObservingGlobalService.getNumberOfTimePoints():"arg : "+results.size()+"\n"+results;
			//		String result ="Agent has sended results \n"+resultType.getId()+"   "+_state_snapshot_frequency+" :";
			//		for (final ExperimentationResults r : results){
			//			result+=r.getUptime()+"  "+getTimeStep(r)+"\n";
			//		}
			//		getMyAgent().logMonologue(result, LogService.onBoth);
		}
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