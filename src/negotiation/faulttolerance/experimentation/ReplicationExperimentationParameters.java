package negotiation.faulttolerance.experimentation;

import java.io.File;
import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationResults;
import negotiation.experimentationframework.Experimentator;
import negotiation.negotiationframework.AllocationSocialWelfares;
import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.distribution.ZeroOneSymbolicValue;
import dimaxx.tools.distribution.NormalLaw.DispersionSymbolicValue;

public class ReplicationExperimentationParameters extends
ExperimentationParameters {
	private static final long serialVersionUID = -7191963637040889163L;


	/***
	 * Variables
	 */

	private int kAccessible;



	public Double hostFaultProbabilityMean;
	public final DispersionSymbolicValue hostDisponibilityDispersion = DispersionSymbolicValue.Moyen;	

	public  Double agentLoadMean;
	public final DispersionSymbolicValue agentLoadDispersion = DispersionSymbolicValue.Moyen;

	public String _usedProtocol;

	public  String _agentSelection;

	private  String _hostSelection;

	public String _socialWelfare;

	/***
	 * Constantes
	 */

	public static final int nbAgents = 10;
	public static final int nbHosts = 6;

	public static final Double hostMaxProc = 2.;
	public static final Double hostMaxMem = 2.;

	/*
	 * Criticité
	 */

	public final ZeroOneSymbolicValue agentCriticityMean = ZeroOneSymbolicValue.Moyen;
	public final DispersionSymbolicValue agentCriticityDispersion = DispersionSymbolicValue.Fort;


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
	public static final double _host_maxSimultaneousFailure = 1;// 0.25;
	public static final long _timeScale = 10 * _host_maxFaultfrequency;
	public static final double _kValue = 7;
	public static final double _lambdaRepair = 1;
	public static final double _kRepair = .001;
	public static final double _theta = 0;// _host_maxFaultfrequency;//0.2;

	//
	// Quantile
	//

	public static final long _reliabilityObservationFrequency = 250;//10 * ReplicationExperimentationProtocol._timeToCollect;// (long)
	// (0.25*_contractExpirationTime);
	public static final int firstTercile = 33;// percent
	public static final int lastTercile = 66;// percent
	public static final double alpha_low = 1;
	public static final double alpha_high = 1;

	//
	// System Dynamicity
	//

	public static final double _criticityMin = 0.1;
	public static final double _criticityVariationProba = 20. / 100.;// 20%
	public static final double _criticityVariationAmplitude = 30. / 100.;// 10%
	public static final Long _criticity_update_frequency = null;// (long)

	// public static final double _dispoMax = 0.7;
	// public static final double _dispoVariationProba = 0./100.;
	// public static final double _dispoVariationAmplitude = 10./100.;
	// public static final long _dispo_update_frequency =2*_quantileInfoFrequency;

	//
	// Fields
	//

	DistributionParameters<AgentIdentifier> agentCriticity;
	DistributionParameters<AgentIdentifier> agentProcessor;
	DistributionParameters<AgentIdentifier> agentMemory;

	//
	// Constructor
	//

	private ReplicationExperimentationParameters(final File f,
			AgentIdentifier experimentatorId,
			final int nbAgents, final int nbHosts, final double k,
			final Double hostFaultProbabilityMean,
			final Double agentLoadMean,
			final String usedProtocol, 
			final String socialWelfare,
			final String agentSelection, 
			final String hostSelection) {
		super(f, experimentatorId, nbAgents, nbHosts);
		setkAccessible(k);
		this.hostFaultProbabilityMean = hostFaultProbabilityMean;
		this.agentLoadMean = agentLoadMean;
		this._usedProtocol = usedProtocol;
		this._socialWelfare=socialWelfare;
		this._agentSelection = agentSelection;
		set_hostSelection(hostSelection);

	}

	public boolean equals(Object o){
		if (o instanceof ReplicationExperimentationParameters){
			ReplicationExperimentationParameters that = (ReplicationExperimentationParameters) o;
			return this.getkAccessible()==that.getkAccessible() &&
			this.hostFaultProbabilityMean.equals( that.hostFaultProbabilityMean) &&
			this.agentLoadMean.equals(that.agentLoadMean) &&
			this._usedProtocol.equals(that._usedProtocol) &&
			this._socialWelfare.equals(that._socialWelfare) &&
			this._agentSelection.equals(that._socialWelfare);
		} else 
			return false;
	}
	
	public int hashCode(){
			return 
					2*this.getkAccessible()
					+4*this.hostFaultProbabilityMean.hashCode()
					+8*agentLoadMean.hashCode()
					+16*this._usedProtocol.hashCode()
					+32*this._socialWelfare.hashCode()
					+64*this._agentSelection.hashCode();
	}
	//
	// Accessors
	//

	public void setkAccessible(final double k) {
		this.kAccessible =(int) (k * nbHosts);
	}
	
	public void set_hostSelection(String hostSelection) {
		if (_usedProtocol.equals(ReplicationExperimentationProtocol.key4mirrorProto))
			this._hostSelection = ReplicationExperimentationProtocol.key4AllocSelect;
		else
			this._hostSelection = hostSelection;
	}

	public DistributionParameters<AgentIdentifier> getAgentCriticity() {
		return this.agentCriticity;
	}

	public DistributionParameters<AgentIdentifier> getAgentProcessor() {
		return this.agentProcessor;
	}

	public DistributionParameters<AgentIdentifier> getAgentMemory() {
		return this.agentMemory;
	}

	/*
	 * 
	 */

	@Override
	public int numberOfTimePoints() {
		return (int) (ReplicationExperimentationProtocol._simulationTime / ReplicationExperimentationProtocol._state_snapshot_frequency);
	}

	@Override
	public int getTimeStep(final ExperimentationResults ag) {
		return Math
				.max(0,
						(int) (ag.getUptime() / ReplicationExperimentationProtocol._state_snapshot_frequency) - 1);
	}

	@Override
	public Long geTime(final int i) {
		return (i + 1)
				* ReplicationExperimentationProtocol._state_snapshot_frequency;
	}

	@Override
	public long getMaxSimulationTime() {
		return ReplicationExperimentationProtocol._simulationTime;
	}

	//
	// Methods
	//

	public ReplicationExperimentationParameters clone(){
		return new ReplicationExperimentationParameters(
				getF(), 
				experimentatorId, 
				nbAgents, 
				nbHosts, 
				getkAccessible(), 
				hostFaultProbabilityMean, 
				agentLoadMean,
				_usedProtocol, 
				_socialWelfare,
				_agentSelection, 
				get_hostSelection());

	}

	@Override
	public void initiate() {
		this.agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(),
				agentCriticityMean,
				agentCriticityDispersion);
		this.agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(), this.agentLoadMean,
				agentLoadDispersion);
		this.agentMemory = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(), this.agentLoadMean,
				agentLoadDispersion);
	}

	public static ReplicationExperimentationParameters getGeneric(File f) {
		return new ReplicationExperimentationParameters(
				f,
				Experimentator.myId,
				nbAgents,
				nbHosts, 
				1., 
				0.6,
				0.3, 
				ReplicationExperimentationProtocol.key4mirrorProto, 
				ReplicationExperimentationProtocol.key4leximinSocialWelfare,
				ReplicationExperimentationProtocol.key4rouletteWheelSelect,
				ReplicationExperimentationProtocol.key4rouletteWheelSelect);
	}

	public String get_hostSelection() {
		return _hostSelection;
	}

	public int getkAccessible() {
		return kAccessible;
	}

	public void setkAccessible(int kAccessible) {
		this.kAccessible = kAccessible;
	}


}

// return "Simulation with parameters :\n"
// +"nbrAgents="+this.nbAgents
// +"; nbrHosts="+this.nbHosts
// +"; k="+this.kAccessible
// +"; agent load="+this.agentLoadMean
// +"; host faults="+this.hostFaultProbabilityMean
// +" nbrRepPerHostMoyen="+this.kAccessible/(this.nbAgents*this.agentLoadMean.getNumericValue());
// }