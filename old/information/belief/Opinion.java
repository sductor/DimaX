package negotiation.negotiationframework.information.belief;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.information.Information;
import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;

public interface Opinion<InformedState extends Information> extends Information{

	public class OpinionIdentifier extends AgentName {

		/**
		 * 
		 */
		private static final long serialVersionUID = 5183637540740255672L;

		public OpinionIdentifier(final AgentIdentifier s) {
			super(s.toString());
		}
	}

	public OpinionIdentifier getMyIdentifier();

	/*
	 * 
	 */

	public InformedState getRepresentativeInformation();

	/**
	 *  @return represent the heterogeneity of collected agent state
	 */
	public Double getSystemDispersion();
	
	/**
	 *  @return represent the number of agents state that has been aggregated to obtain this opinion
	 */
	public int getInformationNumber();

	/*
	 * 
	 */

	/**
	 * @return the minimum time an agent has changed its state
	 */
	public Long getMinInformationDynamicity();

	/**
	 * @return the maximum time an agent has changed its state
	 */
	public Long getMaxInformationDynamicity();

}