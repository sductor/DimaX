package frameworks.negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Map;

import dima.introspectionbasedagents.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightAverageDoubleAggregation;
import dima.introspectionbasedagents.modules.aggregator.LightWeightedAverageDoubleAggregation;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.ResourceIdentifier;
import frameworks.negotiation.negotiationframework.opinion.OpinionDataBase;
import frameworks.negotiation.negotiationframework.opinion.OpinionHandler;
import frameworks.negotiation.negotiationframework.opinion.OpinionService.Opinion;

public class HostStateOpinionHandler
implements OpinionHandler<HostState>{

	final ResourceIdentifier myAgentIdentifer;
	
	public HostStateOpinionHandler(ResourceIdentifier myAgentIdentifer) {
		super();
		this.myAgentIdentifer = new ResourceIdentifier("opinion of "+this.myAgentIdentifer.getUrl(), myAgentIdentifer.getPort());
	}

	@Override
	public Double getNumericValue(final HostState o) {
			final HostState e = (HostState) o;
			return e.getMyCharge();
	}

	@Override
	public AbstractCompensativeAggregation<HostState> fuse(
			final Collection<? extends AbstractCompensativeAggregation<? extends HostState>> averages) {
		throw new RuntimeException("should not be called!");
	}

	@Override
	public HostState getRepresentativeElement(
			final Collection<? extends HostState> elems) {
		final LightAverageDoubleAggregation
		meanProcCu = new LightAverageDoubleAggregation(),
		meanProcMax = new LightAverageDoubleAggregation(),
		meanMemCu = new LightAverageDoubleAggregation(),
		meanMemMax = new LightAverageDoubleAggregation(),
		meanLambda = new LightAverageDoubleAggregation();

		for (final HostState o : elems) {
				final HostState e = (HostState) o;
				meanProcCu.add(e.getCurrentProcCharge());
				meanProcMax.add(e.getProcChargeMax());
				meanMemCu.add(e.getCurrentMemCharge());
				meanMemMax.add(e.getMemChargeMax());
				meanLambda.add(e.getLambda());
		}

		return new HostState(
				myAgentIdentifer,
				null,
				meanLambda.getRepresentativeElement(),
				meanProcMax.getRepresentativeElement(),
				meanProcCu.getRepresentativeElement(),
				meanMemMax.getRepresentativeElement(),
				meanMemCu.getRepresentativeElement(),
				false,// this.getCreationTime(),
				-1);
	}

	@Override
	public HostState getRepresentativeElement(
			final Map<? extends HostState, Double> elems) {
		final LightWeightedAverageDoubleAggregation
		meanProcCu = new LightWeightedAverageDoubleAggregation(),
		meanProcMax = new LightWeightedAverageDoubleAggregation(),
		meanMemCu = new LightWeightedAverageDoubleAggregation(),
		meanMemMax = new LightWeightedAverageDoubleAggregation(),
		meanLambda = new LightWeightedAverageDoubleAggregation();

		for (final Information o : elems.keySet()) {
			if (o instanceof HostState){
				final HostState e = (HostState) o;
				meanProcCu.add(e.getCurrentProcCharge(),elems.get(e));
				meanProcMax.add(e.getProcChargeMax(),elems.get(e));
				meanMemCu.add(e.getCurrentMemCharge(),elems.get(e));
				meanMemMax.add(e.getMemChargeMax(),elems.get(e));
				meanLambda.add(e.getLambda(),elems.get(e));
			} else {
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);
			}
		}

		return new HostState(
				myAgentIdentifer,
				null,
				meanProcMax.getRepresentativeElement(),
				meanProcCu.getRepresentativeElement(),
				meanMemMax.getRepresentativeElement(),
				meanMemCu.getRepresentativeElement(),
				meanLambda.getRepresentativeElement(),
				false, //this.getCreationTime(),
				-1);
	}

	@Override
	public Class<? extends Information> getInfoType() {
		return HostState.class;
	}
}

//} else if (o instanceof Opinion && ((Opinion)o).getRepresentativeElement() instanceof HostState) {
//	final HostState e = (HostState) ((Opinion)o).getRepresentativeElement();
//	return e.getMyCharge();
//} else if (o instanceof Opinion && ((Opinion)o).getRepresentativeElement() instanceof HostState){
//	final HostState e = (HostState) ((Opinion)o).getRepresentativeElement();
//	meanProcCu.add(e.getCurrentProcCharge(),elems.get(e));
//	meanProcMax.add(e.getProcChargeMax(),elems.get(e));
//	meanMemCu.add(e.getCurrentMemCharge(),elems.get(e));
//	meanMemMax.add(e.getMemChargeMax(),elems.get(e));
//	meanLambda.add(e.getLambda(),elems.get(e));
//} else if (o instanceof Opinion && ((Opinion)o).getRepresentativeElement() instanceof HostState) {
//	final HostState that = (HostState) ((Opinion)o).getRepresentativeElement();
//	return that.getMyAgentIdentifier().equals(
//			this.getMyAgentIdentifier())&&this.getStateCounter()==that.getStateCounter();