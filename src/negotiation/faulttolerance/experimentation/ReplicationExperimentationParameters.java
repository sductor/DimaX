package negotiation.faulttolerance.experimentation;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.jdom.JDOMException;

import negotiation.faulttolerance.candidaturewithstatus.CandidatureReplicaCoreWithStatus;
import negotiation.faulttolerance.candidaturewithstatus.CandidatureReplicaProposerWithStatus;
import negotiation.faulttolerance.candidaturewithstatus.ObservingStatusService;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeHost;
import negotiation.faulttolerance.collaborativecandidature.CollaborativeReplica;
import negotiation.faulttolerance.faulsimulation.FaultTriggeringService;
import negotiation.faulttolerance.faulsimulation.HostDisponibilityComputer;
import negotiation.faulttolerance.negotiatingagent.HostCore;
import negotiation.faulttolerance.negotiatingagent.HostState;
import negotiation.faulttolerance.negotiatingagent.ReplicaState;
import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.faulttolerance.negotiatingagent.ReplicationSpecification;
import negotiation.negotiationframework.NegotiationParameters;
import negotiation.negotiationframework.contracts.InformedCandidature;
import negotiation.negotiationframework.contracts.MatchingCandidature;
import negotiation.negotiationframework.contracts.ResourceIdentifier;
import negotiation.negotiationframework.contracts.AbstractContractTransition.IncompleteContractException;
import negotiation.negotiationframework.protocoles.InactiveProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.protocoles.ReverseCFPProtocol;
import negotiation.negotiationframework.rationality.RationalCore;
import negotiation.negotiationframework.rationality.SimpleRationalAgent;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;
import negotiation.negotiationframework.selection.SimpleSelectionCore;
import negotiation.negotiationframework.selection.GreedySelectionModule.GreedySelectionType;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import dima.introspectionbasedagents.services.information.SimpleObservationService;
import dima.introspectionbasedagents.services.information.SimpleOpinionService;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.services.replication.ReplicationHandler;
import dima.introspectionbasedagents.shells.APIAgent.APILauncherModule;
import dimaxx.experimentation.ExperimentationParameters;
import dimaxx.experimentation.Experimentator;
import dimaxx.experimentation.IfailedException;
import dimaxx.experimentation.Laborantin;
import dimaxx.experimentation.ObservingGlobalService;
import dimaxx.experimentation.Laborantin.NotEnoughMachinesException;
import dimaxx.server.HostIdentifier;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;
import dimaxx.tools.mappedcollections.HashedHashSet;

public class ReplicationExperimentationParameters extends
ExperimentationParameters<ReplicationLaborantin> {
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
	public String _socialWelfare;


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

	public static final int startingNbAgents = 10;
	public static final int startingNbHosts = 4;

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

	public static final long _host_maxFaultfrequency = 500;//10 * ReplicationExperimentationProtocol._timeToCollect;// 2*_simulationTime;//
	public static final long _timeScale = 10 * _host_maxFaultfrequency;
	public static final double _kValue = 7;
	public static final double _lambdaRepair = 1;
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
	public static final long _criticity_update_frequency = 2*NegotiationParameters._timeToCollect;// (long)

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
			final String socialWelfare,
			final String agentSelection,
			final String hostSelection,
			final boolean dynamicCriticty,
			final Double host_maxSimultaneousFailurePercent) {
		super(experimentatorId, resultPath);
		this.nbAgents = nbAgents;
		this.nbHosts= nbHosts;		
		this.setkAccessible(k);
		assert agentAccessiblePerHost>0;
		this.hostFaultProbabilityMean = hostFaultProbabilityMean;
		this.hostDisponibilityDispersion=hostFaultProbabilityDispersion;
		this.agentLoadMean = agentLoadMean;
		this.agentLoadDispersion=agentLoadDispersion;
		this.agentCriticityMean=agentCriticityMean;
		this.agentCriticityDispersion=agentCriticityDispersion;
		this.hostCapacityMean=hostCapacityMean;
		this.hostCapacityDispersion=hostcapacityDispersion;
		this._usedProtocol = usedProtocol;
		this._socialWelfare=socialWelfare;
		this._agentSelection = agentSelection;
		this.set_hostSelection(hostSelection);
		this.dynamicCriticity = dynamicCriticty;
		this.setMaxSimultFailure(host_maxSimultaneousFailurePercent);
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
				this.getResultPath().toString(),
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
		return rig.getAgentsIdentifier();
	}

	public Collection<ResourceIdentifier> getHostsIdentifier() {
		return rig.getHostsIdentifier();
	}
	//	//	// final NormalLaw numberOfKnownHosts = new NormalLaw(this.p.kAccessible,
	//	//	// 0);
	//	private int getNumberOfKnownHosts() {
	//		// return numberOfKnownHosts.nextValue();
	//		return kAccessible;
	//	}

	public void setkAccessible(final double k) {
		this.agentAccessiblePerHost =(int) (k * this.nbHosts);
	}

	public Double getRealkAccessible() {
		return (double)this.agentAccessiblePerHost/
				(double)this.nbHosts;
	}

	public void setMaxSimultFailure(final Double host_maxSimultaneousFailurePercent){
		this.host_maxSimultaneousFailure = this.agentAccessiblePerHost*host_maxSimultaneousFailurePercent;

	}

	public Double getRealMaxSimultFailure(){
		return this.host_maxSimultaneousFailure/this.agentAccessiblePerHost;
	}

	public void set_hostSelection(final String hostSelection) {
		if (this._usedProtocol.equals(NegotiationParameters.key4mirrorProto)) {
			this._hostSelection = NegotiationParameters.key4AllocSelect;
		} else {
			this._hostSelection = hostSelection;
		}
	}

	//
	// Methods
	//

	public final void initiateParameters() throws IfailedException{
		rig = new ReplicationInstanceGraph(getMyAgent(),this);

	}


	//
	// Protocol
	//





	/*
	 * Instanciation
	 */

	@Override
	protected Collection<SimpleRationalAgent> instanciateAgents()throws CompetenceException {
		assert !_usedProtocol
		.equals(NegotiationParameters.key4CentralisedstatusProto) ||getMyAgent().myStatusObserver.iObserveStatus();

		this.logMonologue("Initializing agents... ",LogService.onBoth);
		Map<AgentIdentifier,SimpleRationalAgent> result = new HashMap<AgentIdentifier, SimpleRationalAgent>();

		/*
		 * Agent instanciation
		 */

		for (AgentIdentifier replicaId : getReplicasIdentifier()) {

			final SimpleRationalAgent rep;
			if (_usedProtocol
					.equals(NegotiationParameters.key4mirrorProto)) { //Collaborative

				rep = new CollaborativeReplica(
						replicaId,
						rig.getAgentState(replicaId),
						_socialWelfare,
						dynamicCriticity);

			}else { //Status

				rep = new Replica(
						replicaId, 
						rig.getAgentState(replicaId),
						getCore(true, _usedProtocol, _socialWelfare), 
						getSelectionCore(_agentSelection), 
						getProposerCore(true, _usedProtocol), 
						getInformationService(true, _usedProtocol), 
						new ReverseCFPProtocol(),
						dynamicCriticity);

			}


			rep.getMyInformation().addAll(rig.getAccessibleHost(replicaId));

			for (AgentIdentifier h : rep.getMyCurrentState().getMyResourceIdentifiers()){
				rep.addObserver(h,
						SimpleObservationService.informationObservationKey);
				rep.getMyInformation().add(rig.getHostState((ResourceIdentifier) h));
			}

			result.put(rep.getId(),rep);
			getMyAgent().myInformationService.add(rep.getMyCurrentState());
		}

		/*
		 * Host instanciation
		 */

		for (ResourceIdentifier hostId : getHostsIdentifier()) {

			final SimpleRationalAgent hostAg;
			if (_usedProtocol
					.equals(NegotiationParameters.key4mirrorProto)) {
				hostAg = new CollaborativeHost(
						hostId,
						rig.getHostState(hostId),
						_socialWelfare);
			} else {
				hostAg = new Host(
						hostId,
						rig.getHostState(hostId),
						getCore(false, _usedProtocol, _socialWelfare), 
						getSelectionCore(_hostSelection), 
						getProposerCore(false, _usedProtocol), 
						getInformationService(false, _usedProtocol), 
						new ReverseCFPProtocol());

				for (AgentIdentifier ag : hostAg.getMyCurrentState().getMyResourceIdentifiers()){
					hostAg.addObserver(ag,
							SimpleObservationService.informationObservationKey);

					ReplicationHandler.replicate(ag);

					hostAg.getMyInformation().add(rig.getAgentState(ag));
					this.logMonologue(hostAg + "  ->I have initially replicated "
							+ ag,LogService.onBoth);
				}
			}

			result.put(hostAg.getId(),hostAg);
			getMyAgent().myInformationService.add(hostAg.getMyCurrentState());
		}

		/*
		 * 
		 */		

		this.logMonologue("Initializing agents done!:\n" + getMyAgent().myInformationService.show(HostState.class),LogService.onFile);
		return result.values();
	}
	private RationalCore getCore(boolean agent, String _usedProtocol, String _socialWelfare){
		if (_usedProtocol
				.equals(NegotiationParameters.key4CentralisedstatusProto)){
			return agent?new CandidatureReplicaCoreWithStatus():new HostCore(_socialWelfare);
		}else if (_usedProtocol.equals(NegotiationParameters.key4statusProto)) {
			return agent?new CandidatureReplicaCoreWithStatus():new HostCore(_socialWelfare);
		} else 	if (_usedProtocol
				.equals(NegotiationParameters.key4multiLatProto)) {
			throw new RuntimeException("unimplemented!");
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : _usedProtocol = "
							+ _usedProtocol);
		}
	}
	private ProposerCore getProposerCore(boolean agent, String _usedProtocol){
		if (_usedProtocol
				.equals(NegotiationParameters.key4CentralisedstatusProto)){
			return agent?new CandidatureReplicaProposerWithStatus():new InactiveProposerCore();
		}else if (_usedProtocol.equals(NegotiationParameters.key4statusProto)) {
			return agent?new CandidatureReplicaProposerWithStatus():new InactiveProposerCore();
		} else 	if (_usedProtocol
				.equals(NegotiationParameters.key4multiLatProto)) {
			throw new RuntimeException("unimplemented!");
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : _usedProtocol = "
							+ _usedProtocol);
		}
	}
	private ObservationService getInformationService(boolean agent, String _usedProtocol){
		if (_usedProtocol
				.equals(NegotiationParameters.key4CentralisedstatusProto)){
			return agent?new SimpleOpinionService():new SimpleObservationService();
		}else if (_usedProtocol.equals(NegotiationParameters.key4statusProto)) {
			return agent?new SimpleOpinionService():new SimpleOpinionService();
		} else 	if (_usedProtocol
				.equals(NegotiationParameters.key4multiLatProto)) {
			throw new RuntimeException("unimplemented!");
		} else {
			throw new RuntimeException(
					"Static parameters est mal conf : _usedProtocol = "
							+ _usedProtocol);
		}
	}
	private SelectionCore getSelectionCore(String selection){

		if (selection
				.equals(NegotiationParameters.key4greedySelect)) {
			return new SimpleSelectionCore(
					true, false, GreedySelectionType.Greedy);
		} else if (selection
				.equals(NegotiationParameters.key4rouletteWheelSelect)) {
			return new SimpleSelectionCore(
					true, false, GreedySelectionType.RooletteWheel);
		} else if (selection
				.equals(NegotiationParameters.key4AllocSelect)) {
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
			NegotiationParameters.key4CentralisedstatusProto,
			NegotiationParameters.key4statusProto});
	static List<String> welfare = Arrays.asList(new String[]{
			SocialChoiceFunction.key4leximinSocialWelfare,
			SocialChoiceFunction.key4NashSocialWelfare,
			SocialChoiceFunction.key4UtilitaristSocialWelfare});
	static List<String> select = Arrays.asList(new String[]{
			NegotiationParameters.key4greedySelect,
			NegotiationParameters.key4rouletteWheelSelect});//,key4AllocSelect
	static List<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
			DispersionSymbolicValue.Nul,
			DispersionSymbolicValue.Moyen,
			DispersionSymbolicValue.Max});
	static List<Double> doubleParameters = Arrays.asList(new Double[]{
			0.1,
			0.5,
			1.});
	//	static List<Double> doubleParameters = Arrays.asList(new Double[]{
	//			0.1,
	//			0.3,
	//			0.6,
	//			1.});
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
	//pref TODO : Non imple chez l'agent!!
	//	Collection<String> agentPref = Arrays.asList(new String[]{
	//			ReplicationExperimentationProtocol.key4agentKey_Relia,
	//			ReplicationExperimentationProtocol.key4agentKey_loadNRelia});
	//	static final String key4agentKey_Relia="onlyRelia";
	//	static final String key4agentKey_loadNRelia="firstLoadSecondRelia";

	//
	// Variation configuration
	//

	static boolean varyProtocol=false;
	static boolean  varyOptimizers=true;

	static boolean varyAgentsAndhosts=false;

	static boolean varyAccessibleHost=false;

	static boolean varyAgentSelection=false;
	static boolean varyHostSelection=false;

	static boolean varyHostDispo=false;
	static boolean varyHostFaultDispersion=false;

	static boolean varyAgentLoad=false;
	static boolean varyAgentLoadDispersion=false;

	static boolean varyHostCapacity=false;
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
				_maxSimulationTime / 60000
				+ "mins"
				+ (varyAgentSelection==true?"varyAgentSelection":"")
				+ (varyHostSelection?"varyHostSelection":"")
				+ (varyProtocol?"varyProtocol":"")
				+ (varyHostDispo?"varyHostDispo":"")
				+ (varyHostSelection?"varyHostSelection":"")
				+ (varyOptimizers?"varyOptimizers":"")
				+ (varyAccessibleHost?"varyAccessibleHost":"")
				+ (varyAgentLoad?"varyAgentLoad":"")
				+ (varyHostCapacity?"varyHostCapacity":""),
				new AgentName("ziReplExp"),
				startingNbAgents,
				startingNbHosts,
				doubleParameters.get(2),//kaccessible
				doubleParameters.get(1),//dispo mean
				DispersionSymbolicValue.Fort,//dispo dispersion
				0.5,//ReplicationExperimentationProtocol.doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Fort,//load dispersion
				2*doubleParameters.get(1),//capacity mean
				DispersionSymbolicValue.Nul,//capcity dispersion
				doubleParameters.get(1),//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				NegotiationParameters.key4mirrorProto,
				SocialChoiceFunction.key4UtilitaristSocialWelfare,
				NegotiationParameters.key4greedySelect,
				NegotiationParameters.key4greedySelect,
				false,
				doubleParameters2.get(0));
	}

	@Override
	public LinkedList<ExperimentationParameters> generateSimulation() {
		//		final String usedProtocol, agentSelection, hostSelection;
		//		f.mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch =
				new HashSet<ReplicationExperimentationParameters>();
		simuToLaunch.add(getDefaultParameters());
		if (varyAgentsAndhosts) {
			simuToLaunch = this.varyAgentsAndhosts(simuToLaunch);
		}
		if (varyAccessibleHost) {
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		}
		if (varyHostDispo) {
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		}
		if (varyHostFaultDispersion) {
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		}
		if (varyAgentLoad) {
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		}
		if (varyAgentLoadDispersion) {
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		}
		if (varyHostCapacity) {
			simuToLaunch = this.varyHostCapacity(simuToLaunch);
		}
		if (varyHostCapacityDispersion) {
			simuToLaunch = this.varyHostCapacityDispersion(simuToLaunch);
		}
		if (varyAgentCriticity) {
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		}
		if (varyAgentCriticityDispersion) {
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		}
		if (varyAgentSelection) {
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		}
		if (varyHostSelection) {
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		}
		if (varyOptimizers) {
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		}
		if (varyProtocol) {
			simuToLaunch = this.varyProtocol(simuToLaunch);
		}
		if (varyFault) {
			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);
		}

		simuToLaunch = this.varyDynamicCriticity(simuToLaunch);

		final Comparator<ExperimentationParameters> comp = new Comparator<ExperimentationParameters>() {

			@Override
			public int compare(final ExperimentationParameters o1,
					final ExperimentationParameters o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		final LinkedList<ExperimentationParameters> simus = new LinkedList<ExperimentationParameters>();
		for (ReplicationExperimentationParameters p : simuToLaunch)
			if (isValid(p)){
				simus.add(p);
			}else{
				logWarning("ABORTED !!! \n"+p, LogService.onBoth);
			}
		Collections.sort(simus,comp);
		return simus;
	}


	private boolean isValid(ReplicationExperimentationParameters p) {
		if (p.nbHosts*p.agentAccessiblePerHost<nbAgents)
			return false;

		return true;
	}

	@Override
	public Laborantin createLaborantin(APILauncherModule api)
			throws CompetenceException, IfailedException,
			NotEnoughMachinesException {
		ReplicationLaborantin l = new ReplicationLaborantin(this, api);
		setMyAgent(l);
		return l;
	}


	/*
	 *Distribution
	 */

	final double nbSimuPerMAchine = 1;
	@Override
	public Integer getMaxNumberOfAgent(final HostIdentifier id) {
		return new Integer((int) this.nbSimuPerMAchine*
				(startingNbAgents + startingNbHosts)+1);	
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
			for (final String v : protos){
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
			for (final String v : select){
				final ReplicationExperimentationParameters n =  p.clone();
				n._agentSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : select){
				final ReplicationExperimentationParameters n =  p.clone();
				n.set_hostSelection(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : welfare){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentsAndhosts(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : doubleParameters){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=(int)(v*startingNbAgents);
				n.nbHosts=(int)(v*startingNbHosts);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : doubleParameters){
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
			for (final Double v : doubleParameters){
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
			for (final DispersionSymbolicValue v : dispersion){
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
			for (final Double v : doubleParameters){
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
			for (final DispersionSymbolicValue v : dispersion){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostCapacity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : doubleParameters){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostCapacityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : dispersion){
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
			for (final Double v : doubleParameters){
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
			for (final DispersionSymbolicValue v : dispersion){
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
			for (final Double v : doubleParameters2){
				final ReplicationExperimentationParameters n = p.clone();
				n.setMaxSimultFailure(v);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			final Collection<ReplicationExperimentationParameters> exps) {
		assert dynamicCriticityKey>=-1 && dynamicCriticityKey<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			if (dynamicCriticityKey==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (dynamicCriticityKey==1){
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


}


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