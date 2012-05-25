package negotiation.horizon.experimentation;

import negotiation.horizon.negotiatingagent.HorizonContract;
import negotiation.horizon.negotiatingagent.HorizonSpecification;
import negotiation.horizon.negotiatingagent.SubstrateNodeIdentifier;
import negotiation.horizon.negotiatingagent.SubstrateNodeState;
import negotiation.horizon.parameters.AllocableParameters;
import negotiation.horizon.parameters.HorizonAllocableParameters;
import negotiation.horizon.parameters.HorizonMeasurableParameters;
import negotiation.negotiationframework.SimpleNegotiatingAgent;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.ProposerCore;
import negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import negotiation.negotiationframework.rationality.RationalCore;
import dima.introspectionbasedagents.annotations.Competence;
import dima.introspectionbasedagents.services.CompetenceException;
import dima.introspectionbasedagents.services.information.ObservationService;

/**
 * A SubstrateNode is an agent representing a physical computer, able to
 * virtualize some virtual nodes.
 * 
 * @author Vincent Letard
 */
public class SubstrateNode
	extends
	SimpleNegotiatingAgent<HorizonSpecification, SubstrateNodeState, HorizonContract> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -9069889310850887134L;

    // Probably never used.
    private final AllocableParameters nativeMachineParameters;

    // @Competence
    // private final LinkHandler linkHandler;

    @Competence
    public MeasureHandler myMeasureHandler;

    /**
     * Instantiates a new SubstrateNode.
     * 
     * @param id
     * @param myInitialState
     * @param myRationality
     * @param selectionCore
     * @param myInformation
     * @throws CompetenceException
     */
    public SubstrateNode(
	    final SubstrateNodeIdentifier id,
	    final SubstrateNodeState myInitialState,
	    final HorizonAllocableParameters<SubstrateNodeIdentifier> nativeParameters,
	    final int energyConsumptionCoef,
	    final MeasureHandler measureHandler,
	    final RationalCore<HorizonSpecification, SubstrateNodeState, HorizonContract> myRationality,
	    final SelectionCore<SubstrateNode, HorizonSpecification, SubstrateNodeState, HorizonContract> selectionCore,
	    final ProposerCore<SubstrateNode, HorizonSpecification, SubstrateNodeState, HorizonContract> proposerCore,
	    final ObservationService myInformation,
	    final AbstractCommunicationProtocol<HorizonSpecification, SubstrateNodeState, HorizonContract> protocol)
	    throws CompetenceException {
	super(id, new SubstrateNodeState(id, 0, nativeParameters,
		energyConsumptionCoef), myRationality, selectionCore,
		proposerCore, myInformation, protocol);
	this.nativeMachineParameters = nativeParameters.getMachineParameters();
	this.myMeasureHandler = measureHandler;
    }

    @Override
    public SubstrateNodeIdentifier getIdentifier() {
	assert (super.getIdentifier().getClass()
		.equals(SubstrateNodeIdentifier.class));
	return (SubstrateNodeIdentifier) super.getIdentifier();
    }

    public HorizonMeasurableParameters<SubstrateNodeIdentifier> getMeasurableParameters() {
	return this.myMeasureHandler.performMeasures();
    }
}
