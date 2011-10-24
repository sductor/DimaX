package negotiation.faulttolerance.negotiatingagent;

import java.util.Collection;
import java.util.Map;

import negotiation.negotiationframework.agent.SimpleAgentState;
import negotiation.negotiationframework.information.ObservationService.Information;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import negotiation.tools.aggregator.AbstractCompensativeAggregation;
import negotiation.tools.aggregator.LightAverageDoubleAggregation;

public class HostInfo extends SimpleAgentState implements
		ReplicationSpecification {

	/**
	 * 
	 */
	private static final long serialVersionUID = 5735714307767564518L;
	private final Double procChargeMax;
	private final Double procCurrentCharge;

	private final Double memChargeMax;
	private final Double memCurrentCharge;

	private final double lambda;
	private boolean faulty;

	public HostInfo(ResourceIdentifier agentIdentifier, Long creationTime,
			Double procChargeMax, Double procCurrentCharge,
			Double memChargeMax, Double memCurrentCharge, double lambda,
			final boolean faulty) {
		super(agentIdentifier, creationTime);
		this.procChargeMax = procChargeMax;
		this.procCurrentCharge = procCurrentCharge;
		this.memChargeMax = memChargeMax;
		this.memCurrentCharge = memCurrentCharge;
		this.lambda = lambda;
		this.faulty = faulty;
	}

	/*
	 * 
	 */

	public HostInfo(HostState s) {
		super(s.getMyAgentIdentifier(), s.getCreationTime());
		this.procChargeMax = s.getProcChargeMax();
		this.procCurrentCharge = s.getCurrentProcCharge();
		this.memChargeMax = s.getMemChargeMax();
		this.memCurrentCharge = s.getCurrentMemCharge();
		this.lambda = s.getLambda();
		this.faulty = s.isFaulty();
	}

	@Override
	public ResourceIdentifier getMyAgentIdentifier() {
		return (ResourceIdentifier) super.getMyAgentIdentifier();
	}

	public Double getMyCharge() {
		return Math.max(this.getCurrentMemCharge() / this.getMemChargeMax(),
				this.getCurrentProcCharge() / this.getProcChargeMax());
	}

	public boolean ImSurcharged() {
		return this.getMyCharge() >= 1;
	}

	/*
	 * 
	 */

	public Double getCurrentProcCharge() {
		return this.procCurrentCharge;
	}

	public Double getCurrentMemCharge() {
		return this.memCurrentCharge;
	}

	public Double getProcChargeMax() {
		return this.procChargeMax;
	}

	public Double getMemChargeMax() {
		return this.memChargeMax;
	}

	public double getLambda() {
		return this.lambda;
	}

	/*
	 * 
	 */

	public boolean isFaulty() {
		return this.faulty;
	}

	public void setFaulty(final boolean faulty) {
		this.faulty = faulty;
	}

	/*
	 *
	 */

	@Override
	public boolean setLost(ResourceIdentifier h, boolean isLost) {
		throw new RuntimeException("not applicable");
	}

	@Override
	public int compareTo(Information o) {
		if (o instanceof HostInfo) {
			HostInfo e = (HostInfo) o;
			return this.getMyCharge().compareTo(e.getMyCharge());
		} else
			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	}

	@Override
	public Double getNumericValue(Information o) {
		if (o instanceof HostInfo) {
			HostInfo e = (HostInfo) o;
			return e.getMyCharge();
		} else
			throw new RuntimeException("melange d'infos!!!"+this+" "+o);
	}

	@Override
	public AbstractCompensativeAggregation<Information> fuse(
			Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
		throw new RuntimeException("should not be called!");
	}

	@Override
	public Information getRepresentativeElement(
			Collection<? extends Information> elems) {
		LightAverageDoubleAggregation 
		meanProcCu = new LightAverageDoubleAggregation(),
		meanProcMax = new LightAverageDoubleAggregation(), 
		meanMemCu = new LightAverageDoubleAggregation(), 
		meanMemMax = new LightAverageDoubleAggregation(),
		meanLambda = new LightAverageDoubleAggregation();
		
		for (Information o : elems)
			if (o instanceof HostInfo) {
				HostInfo e = (HostInfo) o;
				meanProcCu.add(e.getCurrentProcCharge());
				meanProcMax.add(e.getProcChargeMax());
				meanMemCu.add(e.getCurrentMemCharge());
				meanMemMax.add(e.getMemChargeMax());
				meanLambda.add(e.getLambda());
			} else
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);
			
		return new HostInfo(
				this.getMyAgentIdentifier(), 
				getCreationTime(),
				meanProcMax.getRepresentativeElement(), 
				meanProcCu.getRepresentativeElement(), 
				meanMemMax.getRepresentativeElement(), 
				meanMemCu.getRepresentativeElement(), 
				meanLambda.getRepresentativeElement(),
				false);
	}

	@Override
	public Information getRepresentativeElement(
			Map<? extends Information, Double> elems) {
		LightAverageDoubleAggregation 
		meanProcCu = new LightAverageDoubleAggregation(),
		meanProcMax = new LightAverageDoubleAggregation(), 
		meanMemCu = new LightAverageDoubleAggregation(), 
		meanMemMax = new LightAverageDoubleAggregation(),
		meanLambda = new LightAverageDoubleAggregation();
		
		for (Information o : elems.keySet())
			if (o instanceof ReplicaInfo) {
				HostInfo e = (HostInfo) o;
				meanProcCu.add(e.getCurrentProcCharge(),elems.get(e));
				meanProcMax.add(e.getProcChargeMax(),elems.get(e));
				meanMemCu.add(e.getCurrentMemCharge(),elems.get(e));
				meanMemMax.add(e.getMemChargeMax(),elems.get(e));
				meanLambda.add(e.getLambda(),elems.get(e));
			} else
				throw new RuntimeException("melange d'infos!!!"+this+" "+o);
			
		return new HostInfo(
				this.getMyAgentIdentifier(), 
				getCreationTime(),
				meanProcMax.getRepresentativeElement(), 
				meanProcCu.getRepresentativeElement(), 
				meanMemMax.getRepresentativeElement(), 
				meanMemCu.getRepresentativeElement(), 
				meanLambda.getRepresentativeElement(),
				false);
	}
}
