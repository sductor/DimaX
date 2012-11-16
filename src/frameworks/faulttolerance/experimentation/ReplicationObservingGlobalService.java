package frameworks.faulttolerance.experimentation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.LinkedList;



import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.annotations.StepComposant;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.kernel.CompetentComponent;
import dima.introspectionbasedagents.kernel.LaunchableCompetentComponent;
import dima.introspectionbasedagents.modules.aggregator.LightAverageDoubleAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightWeightedAverageDoubleAggregation;
import dima.introspectionbasedagents.modules.mappedcollections.HashedHashSet;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.support.GimaObject;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.ExperimentationResults;
import frameworks.experimentation.ObservingGlobalService;
import frameworks.experimentation.ObservingSelfService.ActivityLog;
import frameworks.faulttolerance.Host;
import frameworks.faulttolerance.Replica;
import frameworks.faulttolerance.collaborativecandidature.CollaborativeHost;
import frameworks.faulttolerance.collaborativecandidature.CollaborativeReplica;
import frameworks.faulttolerance.negotiatingagent.ReplicaState;
import frameworks.faulttolerance.negotiatingagent.ReplicationSocialOptimisation;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.contracts.ResourceIdentifier;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationObservingGlobalService extends ObservingGlobalService<ReplicationLaborantin>{


	private static final long serialVersionUID = -6071939423880629421L;

	//
	// Fields
	//

	/**
	 *
	 */
	boolean imTheOpt=false;
	Integer optimalTime=null;
	Integer firstoptimaltime=null;
	boolean iObserveStatus;
	/*
	 * Agent
	 */
	/* Quantile */
	LightAverageDoubleAggregation[] agentsExpectedReliabilityEvolution;
	LightAverageDoubleAggregation[] agentsMinReliabilityEvolution;
	/* Mean */
	LightWeightedAverageDoubleAggregation[] criticite;
	/* Disponibility */
	LightAverageDoubleAggregation[] agentsDispoEvolution;
	/* Quantile */
	LightAverageDoubleAggregation[] agentsSaturationEvolution;
	/* Point */
	//	HeavyDoubleAggregation firstReplicationtime;
	LightAverageDoubleAggregation lastReplicationtime;
	LightAverageDoubleAggregation nbOfStateModif;
	LightAverageDoubleAggregation searchTime;
	// Map<AgentIdentifier, Double> firstReplicationtime =
	// new HashMap<AgentIdentifier, Double>();
	// Map<AgentIdentifier, Double> lifeTime =
	// new HashMap<AgentIdentifier, Double>();
	// Map<AgentIdentifier, Double> lastAction =
	// new HashMap<AgentIdentifier, Double>();
	// Map<AgentIdentifier, Double> protocoleExecutiontime =
	// new HashMap<AgentIdentifier, Double>();
	/*
	 * Host
	 */
	/* Quantile */
	LightAverageDoubleAggregation[] hostsChargeEvolution;
	/* Mean */
	LightAverageDoubleAggregation[] faulty;
	//	private int remainingHost=0;

	/*
	 * Status
	 */

	StatusQuantityTrunk[] statusEvolution;
	//	final HeavyDoubleAggregation agentsStatusObservation = new HeavyDoubleAggregation();
	//
	// Constructor
	//


	public ReplicationObservingGlobalService(final ReplicationExperimentationParameters rep) {
		super(rep);

		//Activating status observation
		if (rep._usedProtocol.equals(NegotiationParameters.key4CentralisedstatusProto)
				|| rep._usedProtocol.equals(NegotiationParameters.key4statusProto)) {
			this.iObserveStatus=true;
		} else {
			this.iObserveStatus=false;
		}
	}

	@Override
	public ReplicationExperimentationParameters getSimulationParameters() {
		return (ReplicationExperimentationParameters)super.getSimulationParameters();
	}

	//	@ProactivityInitialisation
	//	public final void initiateHostNumber(){
	//		for (final AgentIdentifier id : this.getMyAgent().getIdentifiers()) {
	//			if (id instanceof ResourceIdentifier) {
	//				this.remainingHost++;
	//			}
	//		}
	//	}



	@Override
	public void initiate() {
		this.agentsExpectedReliabilityEvolution = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.agentsMinReliabilityEvolution = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.agentsDispoEvolution = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.criticite = new LightWeightedAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.hostsChargeEvolution = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.faulty = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		this.agentsSaturationEvolution = new LightAverageDoubleAggregation[ObservingGlobalService.getNumberOfTimePoints()];
		//		firstReplicationtime = new HeavyDoubleAggregation();
		this.lastReplicationtime = new LightAverageDoubleAggregation();
		this.nbOfStateModif = new LightAverageDoubleAggregation();
		this.searchTime = new LightAverageDoubleAggregation();
		for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
			this.hostsChargeEvolution[i] = new LightAverageDoubleAggregation();
			this.agentsSaturationEvolution[i] = new LightAverageDoubleAggregation();
			this.agentsExpectedReliabilityEvolution[i] = new LightAverageDoubleAggregation();
			this.agentsMinReliabilityEvolution[i] = new LightAverageDoubleAggregation();
			this.agentsDispoEvolution[i] = new LightAverageDoubleAggregation();
			this.criticite[i] = new LightWeightedAverageDoubleAggregation();
			this.faulty[i] = new LightAverageDoubleAggregation();
		}


		if (this.iObserveStatus){
			this.statusEvolution =
					new StatusQuantityTrunk[ObservingGlobalService.getNumberOfTimePoints()];
			for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
				this.statusEvolution[i] = new StatusQuantityTrunk();
			}
		}
	}

	@Override
	protected void setObservation(){
		//		assert !this._usedProtocol
		//		.equals(NegotiationParameters.key4CentralisedstatusProto) ||
		//		this.getMyAgent().
		//		myStatusObserver.iObserveStatus():
		//			this._usedProtocol
		//		.equals(NegotiationParameters.key4CentralisedstatusProto)+" "+this.getMyAgent().myStatusObserver.iObserveStatus();

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


		for (final CompetentComponent ag : this.getMyAgent().getAgents()) {
			//Observation about agent
			if (ag instanceof Replica){
				//Observation de l'évolution des états de l'agent
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedRepResultLog.add(ag.getIdentifier());

				//
				//				if (this.getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4CentralisedstatusProto)){
				//					//I aggregate agents reliability
				//					ag.addObserver(this.getIdentifier(), CentralisedObservingStatusService.reliabilityObservationKey);//this.addObserver(ag.getIdentifier(),ObservingStatusService.reliabilityObservationKey);???
				//					reliabilityStatusLog.add(ag.getIdentifier());
				//					//I forward my opinion to every agents
				//					this.addObserver(ag.getIdentifier(), SimpleOpinionService.opinionObservationKey);
				//					opinionsLog.add(ag.getId(), this.getIdentifier());
				//				} else if (this.getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4statusProto)) {
				//					//This agent observe every agents that it knows
				////					for (final AgentIdentifier h :	((Replica)ag).getMyInformation().getKnownAgents()){
				////						this.getMyAgent().getAgent(h).addObserver(ag.getId(), SimpleOpinionService.opinionObservationKey);
				////						opinionsLog.add(ag.getId(), h);
				////					}
				//				} else if (this.getMyAgent().getSimulationParameters()._usedProtocol.equals(NegotiationParameters.key4mirrorProto)){
				//					//do nothing;
				//				} else {
				//					throw new RuntimeException("impossible : ");
				//				}
			}else if (ag instanceof Host){
				//Observation de l'évolution des états de l'hpte
				ag.addObserver(this.getIdentifier(), ActivityLog.class);
				observedHostResultLog.add(ag.getIdentifier());
				// this.myFaultService.addObserver(h.getId(), FaultEvent.class);
				// this.myFaultService.addObserver(h.getId(), RepairEvent.class)
			} else if (ag instanceof ReplicationLaborantin) {
				this.logMonologue("C'est moi!!!!!!!!!! =D",LogService.onFile);
			} else {
				throw new RuntimeException("impossible");
			}
		}

		String mono = "Setting observation :"
				+"\n * I observe results of "+observedHostResultLog
				+"\n * I observe results of      "+observedRepResultLog
				+"\n * I observe reliability of  "+reliabilityStatusLog;
		for (final AgentIdentifier id : opinionsLog.keySet()) {
			mono += "\n * "+id+" observe opinon of "+opinionsLog.get(id);
		}
		this.logMonologue(mono,LogService.onFile);

	}


	
	//
	// Accessors
	//

	@Override
	protected long timeBeforeForcingSimulationEnd() {
		return ExperimentationParameters._maxSimulationTime+this.getSimulationParameters().maxIndividualComputingTime+120000;/*+2min*///300000){//+5min
	}

	@Override
	protected long timeBeforeKillingSimulation() {
		return ExperimentationParameters._maxSimulationTime+this.getSimulationParameters().maxIndividualComputingTime+300000;/*+5min*///600000){//+10min
	}

	//
	// Methods
	//


	@Override
	protected void updateInfo(final ExperimentationResults notification) {
		if (notification instanceof ReplicationResultAgent){
			final ReplicationResultAgent ag = (ReplicationResultAgent) notification;
			this.getMyAgent().getSimulationParameters();
			int i = ObservingGlobalService.getTimeStep(ag);


			this.updateAnAgentValue(ag, i);


			if (ag.isLastInfo()) {
				for (i = ObservingGlobalService.getTimeStep(ag) + 1; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
					this.updateAnAgentValue(ag, i);
				}
			}
		} else if (notification instanceof ReplicationResultHost){
			final ReplicationResultHost h = (ReplicationResultHost) notification;
			int i = ObservingGlobalService.getTimeStep(h);
			this.updateAnHostValue(h, i);


			if (h.isLastInfo()) {
				//				if (h.nbOfModif!=0){
				this.lastReplicationtime.add(new Double(h.getLastModifTime()));
				//					firstReplicationtime.add(new Double(h.getFirstModifTime()));
				//				}
				this.nbOfStateModif.add(new Double(h.nbOfModif));
				for (i = ObservingGlobalService.getTimeStep(h) + 1;
						i < ObservingGlobalService.getNumberOfTimePoints();
						i++) {
					this.updateAnHostValue(h, i);
				}

				this.searchTime.add(h.searchTime);
			}
		} else {
			assert 1<0;
		}
	}private void updateAnAgentValue(final ReplicationResultAgent ag, final int i) {
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.agentsSaturationEvolution[i].add(
					(double)ag.getNumberOfAllocatedResources()/
					(this.getMyAgent().getSimulationParameters().agentAccessiblePerHost));
			this.agentsExpectedReliabilityEvolution[i].add(ag.getReliability(SocialChoiceType.Utility));
			this.agentsMinReliabilityEvolution[i].add(ag.getReliability(SocialChoiceType.Leximin));
			this.agentsDispoEvolution[i].add(ag.getDisponibility());
			this.criticite[i].add(ag.disponibility==0. ? 0. : 1., ag.criticity);

			if (this.iObserveStatus){
				this.statusEvolution[i].incr(ag);
			}
		}
		// firstReplicationtime.put(ag.id, );
		// lifeTime.put(ag.id, );
		// lastAction.put(ag.id, );
		// protocoleExecutiontime.put(ag.id, );
	}private void updateAnHostValue(final ReplicationResultHost h, final int i) {
		this.getMyAgent().getSimulationParameters();
		/**/
		if (i < ObservingGlobalService.getNumberOfTimePoints()) {
			this.hostsChargeEvolution[i].add(h.charge);
			this.faulty[i].add(h.isFaulty ? 0. : 1.);
		}
	}

	@Override
	protected synchronized void writeResult() {
		if (this.imTheOpt) {
			LogService.logOnFile(
					this.getMyAgent().getSimulationParameters().getResultPath(),"First Result : "+this.firstoptimaltime+", OPTIMAL RESULT : "+this.optimalTime,
					true, false);
		}

		LogService.logOnFile(
				this.getMyAgent().getSimulationParameters().getResultPath(),
				"launched :\n--> " + new Date().toString() + "\n "
						+ this.getMyAgent().getSimulationParameters().getSimulationName()
						+ this.getMyAgent().getSimulationParameters() + "\n results are :",
						true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs("reliability",
						this.agentsExpectedReliabilityEvolution, 0.75 * (this.getActiveAgents().size() / this.getMyAgent()
								.getSimulationParameters().nbAgents), this.getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs("reliability",
						this.agentsMinReliabilityEvolution, 0.75 * (this.getActiveAgents().size() / this.getMyAgent()
								.getSimulationParameters().nbAgents), this.getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs("disponibilite",
						this.agentsDispoEvolution, 0.75 * (this.getActiveAgents().size() / this.getMyAgent()
								.getSimulationParameters().nbAgents), this.getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		// Taux de survie = moyenne pond��r�� des (wi, li) | li ��� {0,1} agent
		// mort/vivant
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getMeanTimeEvolutionObs("survie : moyenne ponderee des (wi, mort/vivant)", this.criticite,
						0.75 * (this.getActiveAgents().size() / this.getMyAgent()
								.getSimulationParameters().nbAgents), this.getMyAgent()
								.getSimulationParameters().nbAgents), true,
								false);
		//		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
		//				.getQuantilePointObs("First Replication Time",
		//						firstReplicationtime,
		//						0.75,
		//						this.getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantilePointObs(
						"Time Since Last Action",
						this.lastReplicationtime,
						0.75,
						this.getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantilePointObs(
						"State Modif number",
						this.nbOfStateModif,
						0.75,
						this.getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantilePointObs(
						"Search time",
						this.searchTime,
						0.75,
						this.getMyAgent().getSimulationParameters().nbHosts), true, false);
		//		 Writing.log(this.p.f, getQuantilePointObs("Life Time",
		//		 lifeTime.values(),0.75*p.nbAgents), true, false);
		//		 Writing.log(this.p.f, getQuantilePointObs("Protocol Execution Time",
		//		 protocoleExecutiontime.values(),0.75*p.nbAgents), true, false);
		/**/
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs("charge",
						this.hostsChargeEvolution, 0.75,
						this.getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getQuantileTimeEvolutionObs("agentSaturation",
						this.agentsSaturationEvolution, 0.75,
						this.getMyAgent().getSimulationParameters().nbAgents), true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), ObservingGlobalService
				.getMeanTimeEvolutionObs("percent of hosts that are alive",
						this.faulty, 0.75,
						this.getMyAgent().getSimulationParameters().nbHosts), true, false);
		LogService.logOnFile(this.getMyAgent().getSimulationParameters().getResultPath(), "Optimal? "+this.analyseOptimal(), true, false);

		if (this.iObserveStatus){
			String result =
					"t (seconds in percent);\t lost;\t fragile;\t " +
							"thrifty (empty);\t thrifty;\t thrifty (full);\t wastefull;\t =\n";
			this
			.getSimulationParameters();
			for (int i = 0; i < ObservingGlobalService.getNumberOfTimePoints(); i++) {
				result +=ObservingGlobalService.geTime(i)
						/ 1000.
						+ " ;\t "
						+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentLost,this.getSimulationParameters().nbAgents)
						+ ";\t "
						+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentFragile,this.getSimulationParameters().nbAgents)
						+ ";\t "
						+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentEmpty,this.getSimulationParameters().nbAgents)
						+ ";\t "
						+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentThrifty,this.getSimulationParameters().nbAgents)
						+ ";\t "
						+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentFull,this.getSimulationParameters().nbAgents)
						+ ";\t "
						+ ObservingGlobalService.getPercent(this.statusEvolution[i].nbAgentWastefull,this.getSimulationParameters().nbAgents)
						+ " ("
						+ this.statusEvolution[i].getTotal()
						/ this.getSimulationParameters().nbAgents
						+ ")\n";
			}

			LogService.logOnFile(this.getSimulationParameters()
					.getResultPath(), result, true, false);
		}
		//		this.logWarning(this.getIdentifier()+" OOOOOOOOOKKKKKKKKKKKK?????????\n"+
		//				analyseOptimal()+" for protocol "+getMyAgent().getSimulationParameters()._usedProtocol,
		//				LogService.onBoth);

	}

	//	@Override
	//	public void setAgentHasEnded(final AgentIdentifier id){
	//		super.setAgentHasEnded(id);
	//		if (id instanceof ResourceIdentifier) {
	//			this.remainingHost--;
	//		}
	//	}

	//
	// Primitives
	//

	double min;
	double mean;
	double nash;

	public double getMinWelfare() {
		return this.min;
	}

	public double getUtilWelfare() {
		return this.mean;
	}

	public double getNashWelfare() {
		return this.nash;
	}

	String analyseOptimal(){
		String result="";
		//		assert !this.getFinalStates().isEmpty();

		final LinkedList<ReplicationResultAgent> reliaStates = new LinkedList<ReplicationResultAgent>();
		for (final ExperimentationResults er : this.getFinalStates()){
			if (er instanceof ReplicationResultAgent) {
				reliaStates.add((ReplicationResultAgent)er);
			}
		}

		this.min = Double.POSITIVE_INFINITY;
		double sum=0;
		double criti=0;
		this.nash=1;
		final LinkedList<Double> lex = new LinkedList<Double>();
		for (final ReplicationResultAgent r : reliaStates){
			sum+=ReplicationSocialOptimisation.getReliability(r.getDisponibility(), r.getCriticity(), SocialChoiceType.Utility);
			criti+=r.getCriticity();
			this.nash*=ReplicationSocialOptimisation.getReliability(r.getDisponibility(), r.getCriticity(), SocialChoiceType.Nash);
			this.min = Math.min(this.min, ReplicationSocialOptimisation.getReliability(r.getDisponibility(), r.getCriticity(), SocialChoiceType.Leximin));
			//			lex.addLast(ReplicationSocialOptimisation.getReliability(r.getDisponibility(), r.getCriticity(), SocialChoiceType.Leximin));
			//			Collections.sort(lex);
		}
		this.mean=sum/criti;
		result += //"Leximin solution : "+lex
				"min sol "+this.min+"\n Sum solution "+sum+"\n Mean Solution : "+this.mean+"\n Nash solution "+this.nash;

		//		result+="\n Agent percent of allocated resources : ";
		//		for (ReplicationResultAgent r : reliaStates){
		//			result+= ((double)r.numberOfAllocatedResources/(double)getMyAgent().getSimulationParameters().nbHosts)*100+"%, ";
		//		}


		final Comparator<ReplicationResultAgent> reliaComp = new Comparator<ReplicationResultAgent>() {
			@Override
			public int compare(final ReplicationResultAgent o1,
					final ReplicationResultAgent o2) {
				return o1.disponibility.compareTo(o2.disponibility);
			}
		};
		Collections.sort(reliaStates, reliaComp);
		ReplicationResultAgent prev=null;
		if (!reliaStates.isEmpty()) {
			prev  = reliaStates.removeFirst();
		}
		result+="\n Agents sorted by criticity? ";
		while(!reliaStates.isEmpty()){
			if (prev.getDisponibility()<reliaStates.getFirst().getDisponibility() &&
					prev.criticity>reliaStates.getFirst().criticity) {
				result+="false";
				break;
			}

			prev = reliaStates.removeFirst();
		}
		if (!result.endsWith("false")) {
			result+="true";
		}
		return result;
	}


	//
	// Subclasses
	//

	public class StatusQuantityTrunk extends GimaObject {

		/**
		 *
		 */
		private static final long serialVersionUID = 3740320075407434400L;

		int nbAgentLost = 0;

		int nbAgentFragile = 0;
		int nbAgentThrifty = 0;
		int nbAgentFull = 0;
		int nbAgentWastefull = 0;
		int nbAgentEmpty = 0;

		public void incr(final ReplicationResultAgent s) {
			if (s.getDisponibility()==0) {
				this.nbAgentLost++;
			} else {
				switch (s.getStatus()) {
				case Fragile:
					this.nbAgentFragile++;
					break;
				case Thrifty:
					this.nbAgentThrifty++;
					break;
				case Full:
					this.nbAgentFull++;
					break;
				case Wastefull:
					this.nbAgentWastefull++;
					break;
				case Empty:
					this.nbAgentEmpty++;
					break;
				}
			}
		}

		public double getTotal() {
			return (double) this.nbAgentFragile + this.nbAgentThrifty
					+ this.nbAgentFull + this.nbAgentWastefull
					+ this.nbAgentEmpty+ this.nbAgentLost;
		}
	}
}

//					for (final AgentIdentifier r : this.getAllAgents()) {
//						this.sendMessage(r, new SimulationEndedMessage());
//					}
//					this.endRequestSended=true;	//				else if (this.getAliveAgents().size()==this.remainingHost){
//					this.logMonologue("all agents lost! ending ..",LogService.onBoth);
//					for (final AgentIdentifier r : this.getAliveAgents()) {
//						this.sendMessage(r, new SimulationEndedMessage());
//					}
//					this.endRequestSended=true;
//				}
