package frameworks.horizon.contracts;

import frameworks.horizon.negotiatingagent.SubstrateNodeIdentifier;
import frameworks.horizon.parameters.HorizonMeasurableParameters;

/**
 * Specifies the action of a SubstrateNode by providing information on the
 * quality of service currently provided by the node.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeSpecification extends HorizonSpecification {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = -1464884496624059098L;
	/**
	 * Identifier of the SubstrateNode.
	 */
	private final SubstrateNodeIdentifier agentId;
	/**
	 * Level of service provided.
	 */
	private final HorizonMeasurableParameters<SubstrateNodeIdentifier> measuredParams;

	/**
	 * Constructs a SubstrateNodeSpecification for the specified node and
	 * parameters.
	 * 
	 * @param id
	 *            Node specified by this SubstrateNodeSpecification.
	 * @param params
	 *            Level of service measured on the node.
	 */
	public SubstrateNodeSpecification(final SubstrateNodeIdentifier id,
			final HorizonMeasurableParameters<SubstrateNodeIdentifier> params) {
		this.agentId = id;
		this.measuredParams = params;
	}

	/**
	 * Gives the SubstrateNode specified by this object.
	 */
	@Override
	public SubstrateNodeIdentifier getMyAgentIdentifier() {
		return this.agentId;
	}

	/**
	 * Gives the level of service provided by the SubstrateNode specified by
	 * this object.
	 * 
	 * @return the measurable parameters measured at the creation of the object
	 */
	public HorizonMeasurableParameters<SubstrateNodeIdentifier> getParams() {
		return this.measuredParams;
	}

	@Override
	public SubstrateNodeSpecification clone(){
		return this;
	}
}
