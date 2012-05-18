package negotiation.horizon.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import negotiation.horizon.EmptyIntervalException;
import negotiation.horizon.Interval;
import negotiation.horizon.negotiatingagent.VirtualNetworkState.NodeNotInstanciatedException;
import negotiation.horizon.parameters.HorizonMeasurableParameters;
import negotiation.horizon.parameters.InterfacesParameters;
import negotiation.horizon.parameters.LinkMeasurableParameters;
import negotiation.negotiationframework.rationality.AgentState;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;
import dimaxx.tools.aggregator.HeavyDoubleAggregation;
import dimaxx.tools.aggregator.LightWeightedAverageDoubleAggregation;

public class HorizonPreferenceFunction extends
	SocialChoiceFunction<HorizonSpecification, HorizonContract> {

    public enum Service {
	Voice(3, 1, 2, 4), Videophony(3, 1, 2, 4), Telephony(3, 2, 4, 1), Multimedia(
		2, 1, 4, 3), VOD(3, 1, 4, 2), VPN(3, 1, 4, 2), DataTealTime(2,
		1, 4, 3), Data(1, 2, 4, 3), Streaming(1, 3, 4, 2);

	private final int[] priorities;

	private Service(final int packetLossRate, final int delay,
		final int jitter, final int availability) {
	    assert (packetLossRate != delay && packetLossRate != jitter
		    && packetLossRate != availability && delay != jitter
		    && delay != availability && jitter != availability);
	    this.priorities = new int[] { packetLossRate, delay, jitter,
		    availability };
	}

	public <Type extends Comparable<Type>> List<Type> sort(
		final List<Type> values) throws IllegalArgumentException {
	    if (values.size() != this.priorities.length)
		throw new IllegalArgumentException();

	    final List<Type> result = new ArrayList<Type>(values);

	    Iterator<Type> it = values.iterator();
	    for (int i : this.priorities)
		result.set(i, it.next());
	    return result;
	}

	public int getPriority(final int i) {
	    return this.priorities[i];
	}
    }

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2775977866055120559L;

    public HorizonPreferenceFunction(final SocialChoiceType socialWelfare,
	    final Service service) {
	super(socialWelfare);
    }

    @Override
    public UtilitaristEvaluator<AgentState> getUtilitaristEvaluator() {
	// service.sort;
	// Service.Voice.sort(i)
	return new UtilitaristEvaluator<AgentState>() {

	    @Override
	    public Double getUtilityValue(AgentState s) {
		if (!(s instanceof VirtualNetworkState) || !s.isValid())
		    throw new IllegalArgumentException();

		final Service qos = ((VirtualNetworkState) s).getQoS();
		final List<HorizonMeasurableParameters> prefs = ((VirtualNetworkState) s)
			.getNodesPreferences();
		List<HorizonMeasurableParameters> allocated;
		try {
		    allocated = ((VirtualNetworkState) s)
			    .getNodesCurrentService();
		} catch (NodeNotInstanciatedException e) {
		    throw new RuntimeException(e);
		}

		assert (prefs.size() == allocated.size());

		Iterator<HorizonMeasurableParameters> itPrefs = prefs
			.iterator();
		Iterator<HorizonMeasurableParameters> itAllocated = allocated
			.iterator();

		final HeavyDoubleAggregation bag = new HeavyDoubleAggregation();
		while (itPrefs.hasNext() && itAllocated.hasNext())
		    bag.add(HorizonPreferenceFunction.this.getUtility(itPrefs
			    .next(), itAllocated.next(), qos));
		return bag.getRepresentativeElement();
	    }
	};
    }

    private Double getUtility(final HorizonMeasurableParameters pref,
	    final HorizonMeasurableParameters alloc, final Service qos) {
	final HeavyDoubleAggregation packetLossRateBag = new HeavyDoubleAggregation();
	final HeavyDoubleAggregation delayBag = new HeavyDoubleAggregation();
	final HeavyDoubleAggregation jitterBag = new HeavyDoubleAggregation();

	InterfacesParameters<LinkMeasurableParameters> linksAlloc = alloc
		.getInterfacesParameters();
	for (Map.Entry<HorizonIdentifier, LinkMeasurableParameters> prefEntry : pref
		.getInterfacesParameters().entrySet()) {
	    assert (linksAlloc.containsKey(prefEntry.getKey()));
	    LinkMeasurableParameters prefLink = prefEntry.getValue(), allocLink = linksAlloc
		    .get(prefEntry.getKey());
	    {
		Interval<Float> i = Interval.inter(allocLink
			.getPacketLossRate(), prefLink.getPacketLossRate());
		try {
		    packetLossRateBag
			    .add((double) (i.getUpper() - i.getLower())
				    / (prefLink.getPacketLossRate().getUpper() - prefLink
					    .getPacketLossRate().getLower()));
		} catch (EmptyIntervalException e) {
		    packetLossRateBag.add(0.);
		}
	    }
	    {
		Interval<Integer> i = Interval.inter(allocLink.getDelay(),
			prefLink.getDelay());
		try {
		    delayBag.add((double) (i.getUpper() - i.getLower())
			    / (prefLink.getDelay().getUpper() - prefLink
				    .getDelay().getLower()));
		} catch (EmptyIntervalException e) {
		    delayBag.add(0.);
		}
	    }
	    {
		Interval<Integer> i = Interval.inter(allocLink.getJitter(),
			prefLink.getJitter());
		try {
		    jitterBag.add((double) (i.getUpper() - i.getLower())
			    / (prefLink.getJitter().getUpper() - prefLink
				    .getJitter().getLower()));
		} catch (EmptyIntervalException e) {
		    jitterBag.add(0.);
		}
	    }
	}
	final LightWeightedAverageDoubleAggregation utilityBag = new LightWeightedAverageDoubleAggregation();
	utilityBag.add(packetLossRateBag.getRepresentativeElement(), qos
		.getPriority(0));
	utilityBag.add(delayBag.getRepresentativeElement(), qos.getPriority(1));
	utilityBag
		.add(jitterBag.getRepresentativeElement(), qos.getPriority(2));
	utilityBag.add(
		((double) alloc.getMachineParameters().getAvailability())
			/ ((double) pref.getMachineParameters()
				.getAvailability()), qos.getPriority(3));

	return utilityBag.getRepresentativeElement();
    }

    @Override
    protected <State extends AgentState> Collection<State> cleanStates(
	    Collection<State> res) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public <State extends AgentState> Comparator<State> getComparator() {
	// TODO Auto-generated method stub
	return null;
    }
}
