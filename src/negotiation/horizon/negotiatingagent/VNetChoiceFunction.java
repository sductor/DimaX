package negotiation.horizon.negotiatingagent;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.Iterator;
import java.util.List;
import java.util.SortedMap;
import java.util.TreeMap;

import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;

public class VNetChoiceFunction
	extends
	SocialChoiceFunction<HorizonParameters<HorizonIdentifier>, ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>> {

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

	/**
	 * @param <Type>
	 * @param values
	 * @return
	 * @throws IllegalArgumentException
	 */
	public <Type extends Comparable<Type>> List<Parameter<Type>> sort(
		final List<Parameter<Type>> values) {
	    if (values.size() != this.priorities.length)
		throw new IllegalArgumentException();

	    final List<Parameter<Type>> result = new ArrayList<Parameter<Type>>(
		    Math.max(this.priorities[0], Math.max(this.priorities[1],
			    Math.max(this.priorities[2], this.priorities[3]))) + 1);

	    Iterator<Parameter<Type>> it = values.iterator();
	    for (int i : this.priorities) {
		result.set(i, it.next());
	    }
	    return result;
	}
    }

    private final Service service;

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2775977866055120559L;

    public VNetChoiceFunction(final SocialChoiceType socialWelfare,
	    final Service service) {
	super(socialWelfare);
	this.service = service;
    }

    @Override
    public Comparator<HorizonParameters<HorizonIdentifier>> getComparator() {
	return new Comparator<HorizonParameters<HorizonIdentifier>>() {
	    @Override
	    public int compare(final HorizonParameters<HorizonIdentifier> p1,
		    final HorizonParameters<HorizonIdentifier> p2) {

	    }
	};
    }

    @Override
    public UtilitaristEvaluator<HorizonParameters<HorizonIdentifier>> getUtilitaristEvaluator() {
	service.sort;
	Service.Voice.sort(i)
	// TODO Auto-generated method stub
	return null;
    }
}
