package frameworks.negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Map;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicagentcomponents.AgentName;
import dima.introspectionbasedagents.services.BasicAgentModule;
import dima.introspectionbasedagents.services.core.information.ObservationService.Information;
import dima.introspectionbasedagents.services.core.opinion.OpinionHandler;
import dima.introspectionbasedagents.services.core.opinion.SimpleOpinionService;
import dima.introspectionbasedagents.services.core.opinion.OpinionService.Opinion;
import dima.introspectionbasedagents.services.modules.aggregator.AbstractCompensativeAggregation;
import dima.introspectionbasedagents.services.modules.aggregator.LightAverageDoubleAggregation;
import dima.introspectionbasedagents.services.modules.aggregator.LightWeightedAverageDoubleAggregation;
import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicaStateOpinionHandler
implements OpinionHandler<ReplicaState> {

	/*
	 * Opinion
	 */
	private final SocialChoiceType socialWelfare;
	private final AgentIdentifier myAgentIdentifer;


	public ReplicaStateOpinionHandler(SocialChoiceType socialWelfare,
			AgentIdentifier myAgentIdentifer) {
		super();
		this.socialWelfare = socialWelfare;
		this.myAgentIdentifer = new AgentName("opinion of "+myAgentIdentifer.toString());
	}

	@Override
	public Double getNumericValue(final ReplicaState o) {
			final ReplicaState e = (ReplicaState) o;
			return e.getMyReliability();
	}

	@Override
	public AbstractCompensativeAggregation<ReplicaState> fuse(
			final Collection<? extends AbstractCompensativeAggregation<? extends ReplicaState>> averages) {
		throw new RuntimeException("should not be called!");
	}

	@Override
	public ReplicaState getRepresentativeElement(
			final Collection<? extends ReplicaState> elems) {
		final LightAverageDoubleAggregation
		meanCrit = new LightAverageDoubleAggregation(),
		meanDisp = new LightAverageDoubleAggregation(),
		meanMem = new LightAverageDoubleAggregation(),
		meanProc = new LightAverageDoubleAggregation();

		for (final ReplicaState e : elems) {
				meanCrit.add(e.getMyCriticity());
				meanDisp.add(e.getMyFailureProb());
				meanMem.add(e.getMyMemCharge());
				meanProc.add(e.getMyProcCharge());
		
		}

		final ReplicaState rep = new ReplicaState(
				myAgentIdentifer,
				meanCrit.getRepresentativeElement(),
				null,
				meanProc.getRepresentativeElement(),
				meanMem.getRepresentativeElement(),
				meanDisp.getRepresentativeElement(),// this.getCreationTime(),
				this.socialWelfare,
				-1);
		return rep;
	}

	@Override
	public ReplicaState getRepresentativeElement(
			final Map<? extends ReplicaState, Double> elems) {
		final LightWeightedAverageDoubleAggregation
		meanCrit = new LightWeightedAverageDoubleAggregation(),
		meanDisp = new LightWeightedAverageDoubleAggregation(),
		meanMem = new LightWeightedAverageDoubleAggregation(),
		meanProc = new LightWeightedAverageDoubleAggregation();

		for (final ReplicaState e : elems.keySet()) {
				meanCrit.add(e.getMyCriticity(),elems.get(e));
				meanDisp.add(e.getMyFailureProb(),elems.get(e));
				meanMem.add(e.getMyMemCharge(),elems.get(e));
				meanProc.add(e.getMyProcCharge(),elems.get(e));
		}
		final ReplicaState rep = new ReplicaState(
				myAgentIdentifer,
				meanCrit.getRepresentativeElement(),
				null,
				meanProc.getRepresentativeElement(),
				meanMem.getRepresentativeElement(),
				meanDisp.getRepresentativeElement(), //this.getCreationTime(),
				this.socialWelfare,
				-1);
		return rep;
	}

	@Override
	public Class<? extends Information> getInfoType() {
		return ReplicaState.class;
	}

}
	
//}  else if (o instanceof Opinion && ((Opinion)o).getRepresentativeElement() instanceof ReplicaState) {
//	final ReplicaState e = (ReplicaState) ((Opinion)o).getRepresentativeElement();
//	meanCrit.add(myCriticity,elems.get(e));
//	meanDisp.add(myFailureProb,elems.get(e));
//	meanMem.add(myMemCharge,elems.get(e));
//	meanProc.add(myProcCharge,elems.get(e));
//} else {
//	throw new RuntimeException("melange d'infos!!!"+this+" "+o);
//}
//} else if (o instanceof Opinion && ((Opinion)o).getRepresentativeElement() instanceof ReplicaState) {
//	//				if (!((ReplicaState) o).getMyCriticity().equals(Double.NaN))
//	//					throw new RuntimeException();
//	final ReplicaState e = (ReplicaState) ((Opinion)o).getRepresentativeElement();
//	meanCrit.add(myCriticity);
//	meanDisp.add(myFailureProb);
//	meanMem.add(myMemCharge);
//	meanProc.add(myProcCharge);
//} else {
//	throw new RuntimeException("melange d'infos!!!"+this+" "+o);
//}
//} else if (o instanceof Opinion && ((Opinion)o).getMeanInfo() instanceof ReplicaState){
//	final ReplicaState e = (ReplicaState) ((Opinion)o).getRepresentativeElement();
//	return e.getMyReliability();