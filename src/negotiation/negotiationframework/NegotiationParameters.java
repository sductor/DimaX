package negotiation.negotiationframework;

public class NegotiationParameters {

	//
	// Negotiation Tickers
	//

	public static final long _timeToCollect =500;//500;//
	public static final long _initiatorPropositionFrequency = -1;//(long) (ExperimentationProtocol._timeToCollect*0.5);//(long)
	// public static final long _initiator_analysisFrequency = (long) (_timeToCollect*2);
	public static final long _contractExpirationTime = Long.MAX_VALUE;//10000;//20 * ReplicationExperimentationProtocol._timeToCollect;


	/**
	 * Clés statiques
	 */

	//Protocoles
	public final static String key4mirrorProto = "mirror protocol";
	public final static String key4CentralisedstatusProto = "Centralised status protocol";
	public final static String key4statusProto = "status protocol";
	public final static String key4multiLatProto = "multi lateral protocol";

	//Selection algorithms
	public final static String key4greedySelect = "greedy select";
	public final static String key4rouletteWheelSelect = "roolette wheel select";
	public final static String key4randomSelect = "random select";
	public final static String key4AllocSelect = "alloc select";

	//
	// Quantile
	//

	public static final long _statusObservationFrequency = 250;//10 * ReplicationExperimentationProtocol._timeToCollect;// (long)
	// (0.25*_contractExpirationTime);
	public static final int firstTercile = 33;// percent
	public static final int lastTercile = 66;// percent
	public static final double alpha_low = 1;
	public static final double alpha_high = 1;
}
