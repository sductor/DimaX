package negotiation.faulttolerance.experimentation;

import java.io.File;
import negotiation.experimentationframework.ExperimentationParameters;
import negotiation.experimentationframework.ExperimentationResults;
import negotiation.negotiationframework.AllocationSocialWelfares;
import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.tools.distribution.DistributionParameters;
import dimaxx.tools.distribution.ZeroOneSymbolicValue;

public class ReplicationExperimentationParameters extends
ExperimentationParameters {
	private static final long serialVersionUID = -7191963637040889163L;


	public static int maxNumberOfAgentPerMachine=3000;
	//
	// Fields
	//

	public final double kAccessible;

	public final Double hostFaultProbabilityMean;
	public final ZeroOneSymbolicValue agentLoadMean;

	/*
	 *
	 */

	DistributionParameters<AgentIdentifier> agentCriticity;
	DistributionParameters<AgentIdentifier> agentProcessor;
	DistributionParameters<AgentIdentifier> agentMemory;

	public final String _usedProtocol;
	final static String key4mirrorProto = "mirror protocol";
	final static String key4CentralisedstatusProto = "Centralised status protocol";
	final static String key4statusProto = "status protocol";
	final static String key4multiLatProto = "multi lateral protocol";

	public final String _agentSelection;
	public final String _hostSelection;
	final static String key4greedySelect = "greedy select";
	final static String key4rouletteWheelSelect = "roolette wheel select";
	final static String key4AllocSelect = "alloc select";

	public final String _socialWelfare=AllocationSocialWelfares.key4leximinSocialWelfare;

	//
	// Constructor
	//

	public ReplicationExperimentationParameters(final File f,
			AgentIdentifier experimentatorId,
			final int nbAgents, final int nbHosts, final double k,
			final Double hostFaultProbabilityMean,
			final ZeroOneSymbolicValue agentLoadMean,
			final String usedProtocol, final String agentSelection,
			final String hostSelection) {
		super(f, experimentatorId, nbAgents, nbHosts);

		this.kAccessible = (int) (k * nbHosts);
		this.hostFaultProbabilityMean = hostFaultProbabilityMean;
		this.agentLoadMean = agentLoadMean;
		this._usedProtocol = usedProtocol;
		this._agentSelection = agentSelection;
		if (_usedProtocol.equals(key4mirrorProto))
			this._hostSelection = key4AllocSelect;
		else
			this._hostSelection = hostSelection;
	}

	//
	// Accessors
	//

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

	@Override
	public void initiate() {
		/*
		 * Parameters distribution
		 */

		this.agentCriticity = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(),
				ReplicationExperimentationProtocol.agentCriticityMean,
				ReplicationExperimentationProtocol.agentCriticityDispersion);
		this.agentProcessor = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(), this.agentLoadMean,
				ReplicationExperimentationProtocol.agentLoadDispersion);
		this.agentMemory = new DistributionParameters<AgentIdentifier>(
				this.getReplicasIdentifier(), this.agentLoadMean,
				ReplicationExperimentationProtocol.agentLoadDispersion);

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