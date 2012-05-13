package negotiation.horizon.negotiatingagent;

import java.util.Comparator;

import negotiation.negotiationframework.contracts.ReallocationContract;
import negotiation.negotiationframework.rationality.SocialChoiceFunction;

public class HorizonChoiceFunction
	extends
	SocialChoiceFunction<HorizonParameters<HorizonIdentifier>, ReallocationContract<HorizonCandidature, HorizonParameters<HorizonIdentifier>>> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2775977866055120559L;

    public HorizonChoiceFunction(SocialChoiceType socialWelfare) {
	super(socialWelfare);
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
	// TODO Auto-generated method stub
	return null;
    }

}
