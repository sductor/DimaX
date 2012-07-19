package frameworks.negotiation.faulttolerance.experimentation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;


import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.core.deployment.server.HostIdentifier;
import dima.introspectionbasedagents.services.core.launch.APIAgent.APILauncherModule;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;
import dima.introspectionbasedagents.services.modules.distribution.NormalLaw.DispersionSymbolicValue;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.experimentation.IfailedException;
import frameworks.experimentation.Laborantin;
import frameworks.experimentation.Laborantin.NotEnoughMachinesException;
import frameworks.negotiation.faulttolerance.candidaturewithstatus.StatusHost;
import frameworks.negotiation.faulttolerance.candidaturewithstatus.StatusReplica;
import frameworks.negotiation.faulttolerance.collaborativecandidature.CollaborativeHost;
import frameworks.negotiation.faulttolerance.collaborativecandidature.CollaborativeReplica;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostCore;
import frameworks.negotiation.faulttolerance.negotiatingagent.HostState;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicaCore;
import frameworks.negotiation.faulttolerance.negotiatingagent.ReplicationHostAllocationSolver;
import frameworks.negotiation.negotiationframework.NegotiationParameters;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.negotiationframework.rationality.RationalAgent;
import frameworks.negotiation.negotiationframework.rationality.SimpleRationalAgent;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;
import frameworks.negotiation.negotiationframework.selection.GreedySelectionModule;
import frameworks.negotiation.negotiationframework.selection.OptimalSelectionModule;
import frameworks.negotiation.negotiationframework.selection.SimpleSelectionCore;
import frameworks.negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;

public class ReplicationExperimentationParameters extends
ExperimentationParameters<ReplicationLaborantin> implements Comparable {
	private static final long serialVersionUID = -7191963637040889163L;

	//	final AgentIdentifier experimentatorId;


	/**
	 * Instance
	 */

	ReplicationInstanceGraph rig;

	/***
	 * Variables
	 */

	public int nbAgents;
	public int nbHosts;

	public String _usedProtocol;
	public String _agentSelection;
	public String _hostSelection;
	public SocialChoiceType _socialWelfare;

	public int agentAccessiblePerHost;

	public Double hostFaultProbabilityMean;
	public  DispersionSymbolicValue hostDisponibilityDispersion;

	public  Double agentLoadMean;
	public DispersionSymbolicValue agentLoadDispersion;

	public Double hostCapacityMean;
	public DispersionSymbolicValue hostCapacityDispersion;

	public Double agentCriticityMean;
	public DispersionSymbolicValue agentCriticityDispersion;

	public Boolean dynamicCriticity;
	public Double host_maxSimultaneousFailure;

	/***
	 * Constantes
	 */

	public static final int startingNbHosts = 8;
	public static int startingNbAgents =15;

	//		public static final int startingNbHosts = 5;
	//		public static int startingNbAgents =10;


	public  int simultaneousCandidature = 100;

	public  int simultaneousAcceptation = 50;
	public  int	opinionDiffusionDegree = 50;

	public  long maxComputingTime = 120000;//2 min

	public final boolean completGraph = true;
	public static final boolean multiDim=true;

	private  boolean withOptimal = false;
	private final int maxOptimal = 50;

	/* FAULTS
	 *
	 * * lambda haut => weibull bas weibull bas => eventOccur haut lambda = prob
	 * de panne disp = 1 - lambda (useStaticDispo = true) disp = weibull
	 * (useStaticDispo = false)
	 *
	 *
	 * **
	 *
	 * k bas => eventOccur tot et pour tout le monde
	 */

	public enum DisponibilityComputationType {Static, Weibull, Poisson}
	public static final DisponibilityComputationType choosenType = DisponibilityComputationType.Static;//Poisson;//

	public static final long _host_maxFaultfrequency = 500;//10 * ReplicationExperimentationProtocol._timeToCollect;// 2*_simulationTime;//
	public static final long _timeScale = 10 * ReplicationExperimentationParameters._host_maxFaultfrequency;
	public static final double _lambdaRepair = 1;

	//	WEIBULL : OLD
	public static final double _kValue = 7;
	public static final double _kRepair = .001;
	public static final double _theta = 0;// _host_maxFaultfrequency;//0.2;


	//
	// System Dynamicity 4 status
	//

	/*
	 * Criticité
	 */

	public static final double _criticityMin = 0.1;
	public static final double _criticityVariationProba = 20. / 100.;// 20%
	public static final double _criticityVariationAmplitude = 30. / 100.;// 10%
	public static final long _criticity_update_frequency = 4*NegotiationParameters._timeToCollect;// (long)

	// public static final double _dispoMax = 0.7;
	// public static final double _dispoVariationProba = 0./100.;
	// public static final double _dispoVariationAmplitude = 10./100.;
	// public static final long _dispo_update_frequency =2*_quantileInfoFrequency;

	//
	// Constructor
	//


	ReplicationExperimentationParameters(final String resultPath,
			final AgentIdentifier experimentatorId,
			final int nbAgents, final int nbHosts, final double k,
			final Double hostFaultProbabilityMean,
			final DispersionSymbolicValue hostFaultProbabilityDispersion,
			final Double agentLoadMean,
			final DispersionSymbolicValue agentLoadDispersion,
			final Double hostCapacityMean,
			final DispersionSymbolicValue hostcapacityDispersion,
			final Double agentCriticityMean,
			final DispersionSymbolicValue agentCriticityDispersion,
			final String usedProtocol,
			final SocialChoiceType socialWelfare,
			final String agentSelection,
			final String hostSelection,
			final boolean dynamicCriticty,
			final Double host_maxSimultaneousFailurePercent) {
		super(experimentatorId, resultPath);
		this.nbAgents = nbAgents;
		this.nbHosts= nbHosts;
		this.setkAccessible(k);
		//		assert this.agentAccessiblePerHost>0;
		this.hostFaultProbabilityMean = hostFaultProbabilityMean;
		this.hostDisponibilityDispersion=hostFaultProbabilityDispersion;
		this.agentLoadMean = agentLoadMean;
		this.agentLoadDispersion=agentLoadDispersion;
		this.agentCriticityMean=agentCriticityMean;
		this.agentCriticityDispersion=agentCriticityDispersion;
		this.hostCapacityMean=hostCapacityMean;//41.5;//0.30*nbAgents;//hostCapacityMean;
		this.hostCapacityDispersion=hostcapacityDispersion;
		this._usedProtocol = usedProtocol;
		this._socialWelfare=socialWelfare;
		this._agentSelection = this.setAgentSelection(agentSelection);
		this._hostSelection=hostSelection;
		this.dynamicCriticity = dynamicCriticty;
		this.setMaxSimultFailure(host_maxSimultaneousFailurePercent);
		//		startingNbAgents =(int)((startingNbHosts * hostCapacityMean)/agentLoadMean);
		this.withOptimal=this.withOptimal&&nbAgents*nbHosts<this.maxOptimal;
		this.simultaneousCandidature = Math.min(nbHosts,this.simultaneousCandidature);
		//		simultaneousAcceptation = (int) Math.min(nbAgents,Math.max(simultaneousAcceptation,(int)((double)startingNbAgents)/((double)startingNbHosts)+1));
	}

	public String setAgentSelection(final String agentSelection){
		return this._usedProtocol.equals(NegotiationParameters.key4mirrorProto)?NegotiationParameters.key4greedySelect:agentSelection;
	}

	@Override
	public boolean equals(final Object o){
		if (o instanceof ReplicationExperimentationParameters){
			final ReplicationExperimentationParameters that = (ReplicationExperimentationParameters) o;

			return super.equals(that) && this.getRealkAccessible()==that.getRealkAccessible() &&
					this.hostFaultProbabilityMean.equals( that.hostFaultProbabilityMean) &&
					this.agentLoadMean.equals(that.agentLoadMean) &&
					this.agentLoadDispersion.equals(that.agentLoadDispersion) &&
					this.hostCapacityMean.equals(that.hostCapacityMean) &&
					this.hostCapacityDispersion.equals(that.hostCapacityDispersion) &&
					this._usedProtocol.equals(that._usedProtocol) &&
					this._socialWelfare.equals(that._socialWelfare) &&
					this._agentSelection.equals(that._agentSelection) &&
					this._hostSelection.equals(that._hostSelection);
		} else {
			return false;
		}
	}

	@Override
	public int hashCode(){
		return
				2*this.getRealkAccessible().hashCode()
				+4*this.hostFaultProbabilityMean.hashCode()
				+8*this.agentLoadMean.hashCode()
				+16*this._usedProtocol.hashCode()
				+32*this._socialWelfare.hashCode()
				+64*this._agentSelection.hashCode()
				+128*this._hostSelection.hashCode()
				+256*this.hostDisponibilityDispersion.hashCode()
				+512*this.agentLoadDispersion.hashCode()
				+1024*this.agentCriticityMean.hashCode()
				+2048*this.agentCriticityDispersion.hashCode()
				+4096*this.dynamicCriticity.hashCode()
				+8192*this.host_maxSimultaneousFailure.hashCode()
				+16000*this.hostCapacityMean.hashCode()
				+32000*this.hostCapacityDispersion.hashCode();
	}

	@Override
	public ReplicationExperimentationParameters clone(){
		return new ReplicationExperimentationParameters(
				ReplicationExperimentationParameters.getProtocolId(),
				this.experimentatorId,
				this.nbAgents,
				this.nbHosts,
				this.getRealkAccessible(),
				this.hostFaultProbabilityMean,
				this.hostDisponibilityDispersion,
				this.agentLoadMean,
				this.agentLoadDispersion,
				this.hostCapacityMean,
				this.hostCapacityDispersion,
				this.agentCriticityMean,
				this.agentCriticityDispersion,
				this._usedProtocol,
				this._socialWelfare,
				this._agentSelection,
				this._hostSelection,
				this.dynamicCriticity,
				this.getRealMaxSimultFailure());

	}

	//
	// Accessors
	//

	public Collection<AgentIdentifier> getReplicasIdentifier() {
		return this.rig.getAgentsIdentifier();
	}

	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return this.rig.getHostsIdentifier();
	}
	//	//	// final NormalLaw numberOfKnownHosts = new NormalLaw(this.p.kAccessible,
	//	//	// 0);
	//	private int getNumberOfKnownHosts() {
	//		// return numberOfKnownHosts.nextValue();
	//		return kAccessible;
	//	}

	public void setkAccessible(final double k) {
		this.agentAccessiblePerHost =(int) (k * this.nbAgents);
	}

	public Double getRealkAccessible() {
		return (double)this.agentAccessiblePerHost/
				(double)this.nbAgents;
	}

	public void setMaxSimultFailure(final Double host_maxSimultaneousFailurePercent){
		this.host_maxSimultaneousFailure = this.agentAccessiblePerHost*host_maxSimultaneousFailurePercent;

	}

	public Double getRealMaxSimultFailure(){
		return this.host_maxSimultaneousFailure/this.agentAccessiblePerHost;
	}

	//
	// Methods
	//

	@Override
	public final void initiateParameters() throws IfailedException{
		this.rig = new ReplicationInstanceGraph(this.getMyAgent(),this);

		this.rig.initiateAgents(this);

		ReplicationOptimalSolver ros=null;

		this.logMonologue("Agents & Hosts:\n"+this.rig.getAgentStates()+"\n"+this.rig.getHostsStates(), LogService.onFile);

		int count = 5;
		boolean iFailed=false;
		do {
			try{
				iFailed=false;
				this.rig.setVoisinage();
				if (this.withOptimal) {
					ros = new ReplicationOptimalSolver(this.getMyAgent());
				}
				this.rig.initialRep();
			} catch (final IfailedException e) {
				iFailed=true;
				//				this.logWarning("I'v faileeeeeddddddddddddd RETRYINNNGGGGG "+count+e, LogService.onBoth);
				count--;
				if (count==0) {
					throw e;
				}
			}
		}while(iFailed && count > 0);

		if (this.withOptimal){
			this.logMonologue("beggining optimal computation &&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&", LogService.onBoth);
			ros.solve();
			this.logMonologue("&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&&& ending optimal computation ", LogService.onBoth);
		}

		String initialisationStatus = "Neighborhoog... :\n";
		if (this.completGraph) {
			initialisationStatus+="using a complete accesiblity graph";
		} else {
			for (final AgentIdentifier r : this.rig.getAgentsIdentifier()){
				initialisationStatus+=r+"  has acces to  "+this.rig.getAccessibleHost(r)+"\n";
			}
		}
		initialisationStatus += "\n Initializing allocation... :\n";
		for (final HostState h : this.rig.getHostsStates()){
			initialisationStatus+=h.getMyAgentIdentifier()+"  has allocated  "+h.getMyResourceIdentifiers()+"\n";
		}
		this.logMonologue(initialisationStatus, LogService.onFile);
	}


	//
	// Protocol
	//





	/*
	 * Instanciation
	 */

	@Override
	protected Collection<RationalAgent> instanciateAgents()throws CompetenceException {
		//		System.out.println(this.getMyAgent()+" "+this.getMyAgent().
		//				myStatusObserver);
		//		assert !this._usedProtocol
		//		.equals(NegotiationParameters.key4CentralisedstatusProto) ||
		//		this.getMyAgent().
		//		myStatusObserver.iObserveStatus():
		//			this._usedProtocol
		//			.equals(NegotiationParameters.key4CentralisedstatusProto)+" "+this.getMyAgent().myStatusObserver.iObserveStatus();

		//		this.logMonologue("Initializing agents... ",LogService.onBoth);
		final Map<AgentIdentifier,RationalAgent> result = new HashMap<AgentIdentifier, RationalAgent>();

		/*
		 * Agent instanciation
		 */

		for (final AgentIdentifier replicaId : this.getReplicasIdentifier()) {

			final SimpleRationalAgent rep;
			if (this._usedProtocol
					.equals(NegotiationParameters.key4mirrorProto)) { //Collaborative

				rep = new CollaborativeReplica(
						replicaId,
						this.rig.getAgentState(replicaId),
						this._socialWelfare,
						this.simultaneousCandidature,
						this.dynamicCriticity);

			}else if (this._usedProtocol
					.equals(NegotiationParameters.key4CentralisedstatusProto)){
				rep = new StatusReplica(
						replicaId,
						this.rig.getAgentState(replicaId),
						this.getSelectionCore(this._agentSelection),
						this.simultaneousCandidature,
						this.dynamicCriticity,
						this.getMyAgentIdentifier());
			}else  if (this._usedProtocol.equals(NegotiationParameters.key4statusProto)){ //Status

				rep = new StatusReplica(
						replicaId,
						this.rig.getAgentState(replicaId),
						this.getSelectionCore(this._agentSelection),
						this.simultaneousCandidature,
						this.dynamicCriticity,
						this.opinionDiffusionDegree);


			} else {
				throw new RuntimeException("impossible : usedProtocol = "+this._usedProtocol);
			}

			//Ajout des acquaintances
			rep.getMyInformation().addAll(this.rig.getAccessibleHost(replicaId));

			//gestion des état initiaux
			for (final AgentIdentifier host : rep.getMyCurrentState().getMyResourceIdentifiers()){
				if (rep.getMyCore().iMemorizeMyRessourceState()) {
					rep.getMyInformation().add(this.rig.getHostState((ResourceIdentifier)host));
				}
				if (rep.getMyCore().iObserveMyRessourceChanges()) {
					rep.addObserver(host,
							SimpleRationalAgent.stateChangementObservation);
				}
			}
			result.put(rep.getId(),rep);
		}

		/*
		 * Host instanciation
		 */

		for (final ResourceIdentifier hostId : this.getHostsIdentifier()) {

			final RationalAgent hostAg;
			if (this._usedProtocol
					.equals(NegotiationParameters.key4mirrorProto)) {
				hostAg = new CollaborativeHost(
						hostId,
						this.rig.getHostState(hostId),
						this._socialWelfare,
						this.simultaneousAcceptation,
						this.getSelectionType(this._hostSelection),
						this.maxComputingTime);

			}else if (this._usedProtocol
					.equals(NegotiationParameters.key4CentralisedstatusProto)){ //Status
				hostAg = new StatusHost(
						hostId,
						this.rig.getHostState(hostId),
						this.getSelectionCore(this._hostSelection),
						this._socialWelfare,
						this.getMyAgentIdentifier());

			} else if (this._usedProtocol
					.equals(NegotiationParameters.key4statusProto)) {
				hostAg = new StatusHost(
						hostId,
						this.rig.getHostState(hostId),
						this.getSelectionCore(this._hostSelection),
						this._socialWelfare,
						this.opinionDiffusionDegree);
			}else {
				throw new RuntimeException("impossible : usedProtocol = "+this._usedProtocol);
			}

			//pas d'acquaintance pour les ressources

			//gestion des état initiaux
			for (final AgentIdentifier ag : hostAg.getMyCurrentState().getMyResourceIdentifiers()){
				if (((HostCore)hostAg.getMyCore()).iMemorizeMyRessourceState()) {
					hostAg.getMyInformation().add(this.rig.getAgentState(ag));
				}
				if (((HostCore)hostAg.getMyCore()).iObserveMyRessourceChanges()) {
					hostAg.addObserver(ag,
							SimpleRationalAgent.stateChangementObservation);
				}
			}

			result.put(hostAg.getIdentifier(),hostAg);
			//			getMyAgent().myInformationService.add(hostAg.getMyCurrentState());
		}

		/*
		 *
		 */

		this.logMonologue("Initializing agents done!:\n" + this.getMyAgent().myInformationService.show(HostState.class),LogService.onFile);
		return result.values();
	}

	private SelectionCore getSelectionCore(final String selection){

		if (selection
				.equals(NegotiationParameters.key4greedySelect)) {
			return new SimpleSelectionCore(
					true, false, new GreedySelectionModule(GreedySelectionType.Greedy));
		} else if (selection
				.equals(NegotiationParameters.key4rouletteWheelSelect)) {
			return new SimpleSelectionCore(
					true, false, new GreedySelectionModule(GreedySelectionType.RooletteWheel));
		} else if (selection
				.equals(NegotiationParameters.key4randomSelect)) {
			return new SimpleSelectionCore(
					true, false, new GreedySelectionModule(GreedySelectionType.Random));
		} else if (selection
				.equals(NegotiationParameters.key4OptSelect)) {
			return new SimpleSelectionCore(
					true, false, new OptimalSelectionModule(new ReplicationHostAllocationSolver(_socialWelfare), true, maxComputingTime));
		}else if (selection
				.equals(NegotiationParameters.key4BetterSelect)) {
			return new SimpleSelectionCore(
					true, false, new OptimalSelectionModule(new ReplicationHostAllocationSolver(_socialWelfare), false, maxComputingTime));
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : selection = "+ selection);
		}
	}
	private GreedySelectionType getSelectionType(final String selection){

		if (selection
				.equals(NegotiationParameters.key4greedySelect)) {
			return GreedySelectionType.Greedy;
		} else if (selection
				.equals(NegotiationParameters.key4rouletteWheelSelect)) {
			return GreedySelectionType.RooletteWheel;
		} else if (selection
				.equals(NegotiationParameters.key4OptSelect)) {
			throw new RuntimeException(
					"todo!!! "+ selection);
			//				select = new AllocationSelectionCore<ReplicationSpecification, ReplicaState, ReplicationCandidature>(true, false);
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : selection = "+ selection);
		}
	}

	//
	// Protocole
	//

	//
	//  Génération de simulation
	// /////////////////////////////////

	//
	// Set of values
	//

	static List<String> protos = Arrays.asList(new String[]{
			NegotiationParameters.key4mirrorProto,
			NegotiationParameters.key4CentralisedstatusProto
			//			,NegotiationParameters.key4statusProto
	});
	static List<SocialChoiceType> welfare = Arrays.asList(new SocialChoiceType[]{SocialChoiceType.Utility, SocialChoiceType.Leximin,SocialChoiceType.Nash});//
	static List<String> select = Arrays.asList(new String[]{
			NegotiationParameters.key4greedySelect,
			NegotiationParameters.key4randomSelect,
			NegotiationParameters.key4rouletteWheelSelect});//,key4AllocSelect
	static List<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
			DispersionSymbolicValue.Nul,
			DispersionSymbolicValue.Moyen,
			DispersionSymbolicValue.Max});
	static List<Double> doubleParameters = Arrays.asList(new Double[]{
			0.1,
			0.5,
			1.});
	static List<Double> doubleParameters4 = Arrays.asList(new Double[]{
			0.3,
			0.6,
			1.});
	static List<Double> doubleParameters5 = Arrays.asList(new Double[]{
			0.1,
			0.33
			,0.66
			//			,1.
	});
	static List<Double> doubleParameters2 = Arrays.asList(new Double[]{
			0.,
			0.5,
			1.});
	static List<Double> doubleParameters3 = Arrays.asList(new Double[]{
			0.,
			0.25,
			0.5,
			0.75,
			1.});
	static List<Double> doubleParameters6 = Arrays.asList(new Double[]{
			//			0.01,
			0.1,
			//			0.25,
			0.5,
			//			0.75,
			//			1.
	});
	//pref TODO : Non imple chez l'agent!!
	//	Collection<String> agentPref = Arrays.asList(new String[]{
	//			ReplicationExperimentationProtocol.key4agentKey_Relia,
	//			ReplicationExperimentationProtocol.key4agentKey_loadNRelia});
	//	static final String key4agentKey_Relia="onlyRelia";
	//	static final String key4agentKey_loadNRelia="firstLoadSecondRelia";

	//
	// Variation configuration
	//

	public static final int iterationNumber=10;

	static boolean varyProtocol=false;
	static boolean  varyOptimizers=false;

	static boolean varyAgents=false;
	static boolean varyHosts=false;

	static boolean varyAccessibleHost=false;

	static boolean varyAgentSelection=false;
	static boolean varyHostSelection=false;

	static boolean varyHostDispo=false;
	static boolean varyHostFaultDispersion=false;

	static boolean varyAgentLoad=false;
	static boolean varyAgentLoadDispersion=false;

	static boolean varyHostCapacity=true;
	static boolean varyHostCapacityDispersion=false;

	static boolean varyAgentCriticity=false;
	static boolean varyAgentCriticityDispersion=false;

	static boolean varyFault=false;
	static int dynamicCriticityKey=-1; //-1 never dynamics, 1 always dynamics, 0 both


	//
	// Default values
	//

	static ReplicationExperimentationParameters getDefaultParameters() {
		return new ReplicationExperimentationParameters(
				ReplicationExperimentationParameters.getProtocolId(),
				new AgentName("ziReplExp"),
				ReplicationExperimentationParameters.startingNbAgents,
				ReplicationExperimentationParameters.startingNbHosts,
				1.,//ReplicationExperimentationParameters.doubleParameters.get(2),//kaccessible
				0.7,//dispo mean
				DispersionSymbolicValue.Moyen,//dispo dispersion
				0.5,//ReplicationExperimentationProtocol.doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Moyen,//load dispersion
				(double)ReplicationExperimentationParameters.startingNbAgents/(double)ReplicationExperimentationParameters.startingNbHosts,//ReplicationExperimentationParameters.doubleParameters.get(1),//capacity mean2.5,//
				DispersionSymbolicValue.Faible,//capcity dispersion
				ReplicationExperimentationParameters.doubleParameters.get(1),//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				NegotiationParameters.key4CentralisedstatusProto,//NegotiationParameters.key4statusProto,//NegotiationParameters.key4mirrorProto,//
				SocialChoiceType.Utility,
				NegotiationParameters.key4greedySelect,//NegotiationParameters.key4rouletteWheelSelect,//
				NegotiationParameters.key4greedySelect,//NegotiationParameters.key4rouletteWheelSelect,//
				false,
				ReplicationExperimentationParameters.doubleParameters2.get(0));
	}

	private static String getProtocolId() {
		return ExperimentationParameters._maxSimulationTime / 1000
				+ "secs"
				+ (ReplicationExperimentationParameters.varyAgentSelection==true?"varyAgentSelection":"")
				+ (ReplicationExperimentationParameters.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationParameters.varyProtocol?"varyProtocol":"")
				+ (ReplicationExperimentationParameters.varyHostDispo?"varyHostDispo":"")
				+ (ReplicationExperimentationParameters.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationParameters.varyOptimizers?"varyOptimizers":"")
				+ (ReplicationExperimentationParameters.varyAccessibleHost?"varyAccessibleHost":"")
				+ (ReplicationExperimentationParameters.varyAgentLoad?"varyAgentLoad":"")
				+ (ReplicationExperimentationParameters.varyHostCapacity?"varyHostCapacity":"");
	}

	@Override
	public LinkedList<ExperimentationParameters<ReplicationLaborantin>> generateSimulation() {
		//		final String usedProtocol, agentSelection, hostSelection;
		//		f.mkdirs();
		new File(LogService.getMyPath()+"result_"+ReplicationExperimentationParameters.getProtocolId()+"/").mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch =
				new HashSet<ReplicationExperimentationParameters>();
		simuToLaunch.add(ReplicationExperimentationParameters.getDefaultParameters());
		if (ReplicationExperimentationParameters.varyAgents) {
			simuToLaunch = this.varyAgents(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyHosts) {
			simuToLaunch = this.varyHosts(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyAccessibleHost) {
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyHostDispo) {
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyHostFaultDispersion) {
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyAgentLoad) {
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyAgentLoadDispersion) {
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyHostCapacity) {
			simuToLaunch = this.varyHostCapacity(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyHostCapacityDispersion) {
			simuToLaunch = this.varyHostCapacityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyAgentCriticity) {
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyAgentCriticityDispersion) {
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyAgentSelection) {
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyHostSelection) {
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyOptimizers) {
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyProtocol) {
			simuToLaunch = this.varyProtocol(simuToLaunch);
		}
		if (ReplicationExperimentationParameters.varyFault) {
			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);
		}

		simuToLaunch = this.varyDynamicCriticity(simuToLaunch);

		final Comparator<ExperimentationParameters<ReplicationLaborantin>> comp =
				new Comparator<ExperimentationParameters<ReplicationLaborantin>>() {

			@Override
			public int compare(final ExperimentationParameters<ReplicationLaborantin> o1,
					final ExperimentationParameters<ReplicationLaborantin> o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		final LinkedList<ExperimentationParameters<ReplicationLaborantin>> simus =
				new LinkedList<ExperimentationParameters<ReplicationLaborantin>>();
		for (final ReplicationExperimentationParameters p : simuToLaunch) {
			if (this.isValid(p)){
				simus.add(p);
			}else{
				//				this.logWarning("ABORTED !!! "+(p.nbHosts*p.agentAccessiblePerHost<p.nbAgents)+" "+(p.agentAccessiblePerHost<=0)+" \n"+p, LogService.onBoth);
			}
		}
		Collections.sort(simus,comp);
		return simus;
	}


	private boolean isValid(final ReplicationExperimentationParameters p) {
		if (p.nbHosts*p.agentAccessiblePerHost<this.nbAgents || p.agentAccessiblePerHost<=0) {
			return false;
		}

		return true;
	}

	@Override
	public Laborantin createLaborantin(final APILauncherModule api)
			throws CompetenceException, IfailedException,
			NotEnoughMachinesException {
		final ReplicationLaborantin l = new ReplicationLaborantin(this, api);
		this.setMyAgent(l);
		return l;
	}


	/*
	 *Distribution
	 */

	final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgent(final HostIdentifier id) {
		return new Integer((int) this.nbSimuPerMAchine*
				//				(nbAgents + nbHosts)+1);
				(ReplicationExperimentationParameters.startingNbAgents + ReplicationExperimentationParameters.startingNbHosts)+1);
		//		return new Integer(10);
	}


	/*
	 *
	 */


	//
	// Primitive
	//



	private Collection<ReplicationExperimentationParameters> varyProtocol(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationParameters.protos){
				final ReplicationExperimentationParameters n =  p.clone();
				n._usedProtocol=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationParameters.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n.setAgentSelection(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : ReplicationExperimentationParameters.select){
				final ReplicationExperimentationParameters n =  p.clone();
				n._hostSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final SocialChoiceType v : ReplicationExperimentationParameters.welfare){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgents(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters6){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=(int)(v*ReplicationExperimentationParameters.startingNbAgents);
				//				n.nbAgents=(int)((v  * n.nbHosts * n.hostCapacityMean)/n.agentLoadMean);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgents2(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			final List<Integer> nbAgentsList = Arrays.asList(new Integer[]{10,20,40,80,100,150,200,350,500,750,1000,2000,5000});
			for (final Integer v : nbAgentsList){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=v;
				result.add(n);
			}

			//			for (final Double v : ReplicationExperimentationParameters.doubleParameters6){
			//				final ReplicationExperimentationParameters n =  p.clone();
			//				n.nbAgents=(int)(v*ReplicationExperimentationParameters.startingNbAgents);
			////				n.nbAgents=(int)((v  * n.nbHosts * n.hostCapacityMean)/n.agentLoadMean);
			//				result.add(n);
			//			}

			//
			//
			//				final ReplicationExperimentationParameters n1 =  p.clone();
			//				n1.nbAgents=50;
			//				result.add(n1);
			//				final ReplicationExperimentationParameters n2 =  p.clone();
			//				n2.nbAgents=80;
			//				result.add(n2);
			//				final ReplicationExperimentationParameters n3 =  p.clone();
			//				n3.nbAgents=150;
			//				result.add(n3);
			//				final ReplicationExperimentationParameters n4 =  p.clone();
			//				n4.nbAgents=300;
			//				result.add(n4);


		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHosts(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters6){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbHosts=(int)(v*ReplicationExperimentationParameters.startingNbHosts);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters4){
				final ReplicationExperimentationParameters n =  p.clone();
				n.setkAccessible(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostDispo(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters5){
				final ReplicationExperimentationParameters n =  p.clone();
				n.hostFaultProbabilityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostFaultDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationParameters.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostDisponibilityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoad(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters5){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoadDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationParameters.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	//	private Collection<ReplicationExperimentationParameters> varyHostCapacity2(final Collection<ReplicationExperimentationParameters> exps){
	//		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
	//		for (final ReplicationExperimentationParameters p : exps) {
	//			for (final Double v : ReplicationExperimentationParameters.doubleParameters){
	//				final ReplicationExperimentationParameters n = p.clone();
	//				n.hostCapacityMean=v;
	//				result.add(n);
	//			}
	//		}
	//		return result;
	//	}

	private Collection<ReplicationExperimentationParameters> varyHostCapacity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		final List<Double> capacitiesList = Arrays.asList(new Double[]{0.10,0.30,0.60,1.});
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : capacitiesList){
				//variant resources capacity
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityMean=v*p.nbAgents;
				if (n.hostCapacityMean>n.nbAgents/n.nbHosts){
					result.add(n);
				}
				//fixed resources capacity
				final ReplicationExperimentationParameters n2 = p.clone();
				n2.hostCapacityMean=(double)ReplicationExperimentationParameters.startingNbAgents/(double)ReplicationExperimentationParameters.startingNbHosts;
				if (n2.hostCapacityMean>n2.nbAgents/n2.nbHosts){
					result.add(n2);
				}
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostCapacityDispersion(
			final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationParameters.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityMean=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : ReplicationExperimentationParameters.dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyMaxSimultFailure(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : ReplicationExperimentationParameters.doubleParameters2){
				final ReplicationExperimentationParameters n = p.clone();
				n.setMaxSimultFailure(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			final Collection<ReplicationExperimentationParameters> exps) {
		assert ReplicationExperimentationParameters.dynamicCriticityKey>=-1 && ReplicationExperimentationParameters.dynamicCriticityKey<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			if (ReplicationExperimentationParameters.dynamicCriticityKey==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (ReplicationExperimentationParameters.dynamicCriticityKey==1){
				p.dynamicCriticity=true;
				result.add(p);
			} else {
				final ReplicationExperimentationParameters n = p.clone();
				n.dynamicCriticity=!p.dynamicCriticity;
				result.add(n);
			}
		}
		return result;
	}

	@Override
	public int compareTo(final Object o) {
		final ReplicationExperimentationParameters that = (ReplicationExperimentationParameters) o;
		final double fixedResources=
				(double)ReplicationExperimentationParameters.startingNbAgents/
				(double)ReplicationExperimentationParameters.startingNbHosts;
		final boolean thisIsFixed=this.hostCapacityMean==fixedResources;
		final boolean thatIsFixed=that.hostCapacityMean==fixedResources;
		final double thisHostCapacityPercent=this.hostCapacityMean/this.nbAgents;
		final double thatHostCapacityPercent=that.hostCapacityMean/that.nbAgents;

		if  (thisIsFixed && thatIsFixed) {
			return this.nbAgents-that.nbAgents;
		} else if (thisIsFixed && !thatIsFixed){
			return -1;
		} else if (thatIsFixed && !thisIsFixed){
			return 1;
		} else {//!thisIsFixed && !thatIsFixed
			if (thisHostCapacityPercent!=thatHostCapacityPercent){
				return (int) (thisHostCapacityPercent-thatHostCapacityPercent);
			} else {
				return this.nbAgents-that.nbAgents;
			}
		}
	}
}



//			for (final AgentIdentifier h : rep.getMyCurrentState().getMyResourceIdentifiers()){
//				rep.addObserver(h,
//						SimpleObservationService.informationObservationKey);
//				rep.getMyInformation().add(this.rig.getHostState((ResourceIdentifier) h));
//			}
//}else if (){ //Status
//
//rep = new StatusReplica(
//		replicaId,
//		this.rig.getAgentState(replicaId),
//		this.getCore(true, this._usedProtocol, this._socialWelfare),
//		this.getSelectionCore(this._agentSelection),
//		this.getProposerCore(true, this._usedProtocol),
//		this.getInformationService(true, this._usedProtocol),
//		new ReverseCFPProtocol(),
//		this.dynamicCriticity,
//		opinionDiffusionDegree);

//			for (final AgentIdentifier h : rep.getMyCurrentState().getMyResourceIdentifiers()){
//				rep.addObserver(h,
//						SimpleObservationService.informationObservationKey);
//				rep.getMyInformation().add(this.rig.getHostState((ResourceIdentifier) h));
//			}
//
////
//// Creation de laborantin
//// /////////////////////////////////
//
//@Override
//public ReplicationLaborantin createNewLaborantin(
//		final ExperimentationParameters para, final APILauncherModule api)
//				throws NotEnoughMachinesException, CompetenceException, IfailedException {
//	ReplicationLaborantin l = null;
//	final ReplicationExperimentationParameters p = (ReplicationExperimentationParameters) para;
//	//		boolean erreur = true;
//	//		while (erreur)
//	//			try {
//	l = new ReplicationLaborantin(p, api,this.getMaxNumberOfAgentPerMachine(null));
//	//				erreur = false;
//	//			} catch (final IfailedException e) {
//	//				LogService.writeException(
//	//						"retrying to launch simu " + p.getName()
//	//						+ " failure caused by : ", e.e);
//	//				erreur = true;
//	//			}
//
//	return l;
//}

//public String getDescription(){
//	return super.getDescription()+" simus of "
//			+ExperimentationProtocol._simulationTime/1000.+
//			"secs  on "+this.getApi().getAvalaibleHosts().size()+" machine";//+ReplicationExperimentationProtocol.nbSimuPerMAchine+" simu per machine"
//}

//	public final static String simulationResultStateObservationKey = "observe the state!";

// return "Simulation with parameters :\n"
// +"nbrAgents="+this.nbAgents
// +"; nbrHosts="+this.nbHosts
// +"; k="+this.kAccessible
// +"; agent load="+this.agentLoadMean
// +"; host faults="+this.hostFaultProbabilityMean
// +" nbrRepPerHostMoyen="+this.kAccessible/(this.nbAgents*this.agentLoadMean.getNumericValue());
// }