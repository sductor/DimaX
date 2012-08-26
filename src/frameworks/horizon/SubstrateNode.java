package frameworks.horizon;

import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;
import frameworks.horizon.contracts.HorizonContract;
import frameworks.horizon.contracts.HorizonSpecification;
import frameworks.horizon.negotiatingagent.SubstrateNodeCore;
import frameworks.horizon.negotiatingagent.SubstrateNodeIdentifier;
import frameworks.horizon.negotiatingagent.SubstrateNodeState;
import frameworks.horizon.parameters.AllocableParameters;
import frameworks.horizon.parameters.HorizonAllocableParameters;
import frameworks.horizon.parameters.HorizonMeasurableParameters;
import frameworks.negotiation.SimpleNegotiatingAgent;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol;
import frameworks.negotiation.protocoles.InactiveProposerCore;
import frameworks.negotiation.protocoles.AbstractCommunicationProtocol.SelectionCore;

/**
 * A SubstrateNode is an agent representing a physical computer, able to
 * virtualize some virtual nodes.
 * 
 * @author Vincent Letard
 */
public class SubstrateNode extends
SimpleNegotiatingAgent<SubstrateNodeState, HorizonContract> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -9069889310850887134L;

	// Could be used in later developpement.
	/**
	 * Total amount of parameters available in the machine.
	 */
	private final AllocableParameters nativeMachineParameters;

	// @Competence
	// private final LinkHandler linkHandler;

	/**
	 * Competence in charge of measuring the non functional parameters.
	 */
	@Competence
	public MeasureHandler myMeasureHandler;

	/**
	 * Instantiates a new SubstrateNode.
	 * 
	 * @param id
	 *            Identifier of the SubstrateNode.
	 * @param myInitialState
	 *            InitialState
	 * @param nativeParameters
	 *            initial parameters of the machine
	 * @param energyConsumptionCoef
	 *            how much energy consumes this machine ?
	 * @param measureHandler
	 *            module in charge of the measures
	 * @param myRationality
	 *            RationalCore of this agent
	 * @param selectionCore
	 *            the selection core
	 * @param myInformation
	 *            sensors of the agent
	 * @param protocol
	 *            negotiation protocol
	 * @throws CompetenceException
	 *             if an error occurred in the construction of the SubstrateNode
	 */
	public SubstrateNode(
			final SubstrateNodeIdentifier id,
			final SubstrateNodeState myInitialState,
			final HorizonAllocableParameters<SubstrateNodeIdentifier> nativeParameters,
			final int energyConsumptionCoef,
			final MeasureHandler measureHandler,
			final SubstrateNodeCore myRationality,
			final SelectionCore<SubstrateNode, SubstrateNodeState, HorizonContract> selectionCore,
			final ObservationService myInformation,
			final AbstractCommunicationProtocol<SubstrateNodeState,HorizonContract> protocol)
					throws CompetenceException {
		super(
				id,
				new SubstrateNodeState(id, 0, nativeParameters,	energyConsumptionCoef),
						myRationality,
						selectionCore,
						new InactiveProposerCore<HorizonSpecification, SubstrateNodeState, HorizonContract>(),
						myInformation, protocol);
		this.nativeMachineParameters = nativeParameters.getMachineParameters();
		this.myMeasureHandler = measureHandler;
	}

	/**
	 * Returns the Identifier of this SubstrateNode.
	 */
	@Override
	public SubstrateNodeIdentifier getIdentifier() {
		assert super.getIdentifier().getClass()
		.equals(SubstrateNodeIdentifier.class);
		return (SubstrateNodeIdentifier) super.getIdentifier();
	}

	/**
	 * Uses the MeasureHandler to give the current level of service provided by
	 * the node.
	 * 
	 * @return the current level of service.
	 */
	public HorizonMeasurableParameters<SubstrateNodeIdentifier> getMeasurableParameters() {
		return this.myMeasureHandler.performMeasures();
	}
}
