package negotiation.tools.aggregator;


public class HeavyDoubleMinMaxAggregator extends HeavyMinMaxAggregator<Double>{

	/**
	 *
	 */
	private static final long serialVersionUID = 1238869190656073978L;

	public HeavyDoubleMinMaxAggregator() {
		super(new DoubleAggregatorFunctions());
	}

}
