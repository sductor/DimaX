package negotiation.faulttolerance.candidaturenegotiation.mirrordestruction;

import negotiation.faulttolerance.negotiatingagent.ReplicationCandidature;
import negotiation.negotiationframework.interaction.ResourceIdentifier;
import dima.basicagentcomponents.AgentIdentifier;

public class ReplicationCandidatureWithMinInfo extends ReplicationCandidature {

	/**
	 *
	 */
	private static final long serialVersionUID = 6854154168844597539L;
	private final Double minHostedRelia;

	public ReplicationCandidatureWithMinInfo(final ResourceIdentifier r,
			final AgentIdentifier a, final boolean creation,
			final Double minHostedRelia) {
		super(r, a, creation);
		this.minHostedRelia = minHostedRelia;
		if (creation == false
				&& minHostedRelia.equals(Double.POSITIVE_INFINITY)
				|| creation == true
				&& this.getInitiator() instanceof ResourceIdentifier)
			throw new RuntimeException("mauvaise instanciation");
	}

	public ReplicationCandidatureWithMinInfo(final ResourceIdentifier r,
			final AgentIdentifier a, final boolean creation) {
		this(r, a, creation, Double.POSITIVE_INFINITY);
	}

	public Double getMinHostedReliability() {
		return this.minHostedRelia;
	}
	// public ReplicationCandidature clone(){
	// return new ReplicationCandidature(r, a, creation, minHostedRelia);
	//
	// }
}
