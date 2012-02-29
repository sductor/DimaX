package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.Map;

import negotiation.negotiationframework.interaction.contracts.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

public class VirtualNodeState implements HorizonSpecification {

	/**
	 * 
	 */
	private static final long serialVersionUID = -2721088186118421802L;

	@Override
	public Long getCreationTime() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AgentIdentifier getMyAgentIdentifier() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public long getUptime() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public int isNewerThan(Information that) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Double getNumericValue(Information e) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public AbstractCompensativeAggregation<Information> fuse(
			Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Information getRepresentativeElement(
			Collection<? extends Information> elems) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Information getRepresentativeElement(
			Map<? extends Information, Double> elems) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Collection<? extends AgentIdentifier> getMyResourceIdentifiers() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Class<? extends Information> getMyResourcesClass() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getStateCounter() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public boolean isValid() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public boolean setLost(ResourceIdentifier h, boolean isLost) {
		// TODO Auto-generated method stub
		return false;
	}

}
