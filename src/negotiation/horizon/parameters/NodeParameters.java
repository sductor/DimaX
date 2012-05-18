package negotiation.horizon.parameters;

import dima.support.GimaObject;

/**
 * @author Vincent Letard
 */
public class NodeParameters extends GimaObject /* AbstractInformation */{

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 8142462973752131803L;

    private final HorizonAllocableParameters allocableParams;
    private final HorizonMeasurableParameters measurableParams;

    // /**
    // * Static parameters with null values.
    // *
    // * @uml.property name="nONE"
    // * @uml.associationEnd
    // */
    // public final static SingleNodeParameters NONE = new
    // SingleNodeParameters(0,
    // 0, 0);

    /**
     * Constructs a new instance of SingleNodeParameters.
     */
    public NodeParameters(final HorizonAllocableParameters allocableParams,
	    final HorizonMeasurableParameters measurableParams) {
	this.allocableParams = allocableParams;
	this.measurableParams = measurableParams;
    }

    // public SingleNodeParameters(
    // final Collection<SingleNodeParameters> positives,
    // final Collection<SingleNodeParameters> negatives) {
    // int proc = 0, ram = 0;
    // for (SingleNodeParameters pos : positives) {
    // proc += pos.getProcessor();
    // ram += pos.getRam();
    // }
    // for (SingleNodeParameters neg : negatives) {
    // proc -= neg.getProcessor();
    // ram -= neg.getRam();
    // }
    // this.processor = proc;
    // this.ram = ram;
    // }

    /**
     * @return the value of the field allocableParameters
     */
    public HorizonAllocableParameters getAllocableParams() {
	return this.allocableParams;
    }

    public HorizonMeasurableParameters getMeasurableParams() {
	return this.measurableParams;
    }

    public boolean isValid() {
	return this.allocableParams.isValid();
    }

    @Override
    public String toString() {
	return "{ allocableParameters : " + this.allocableParams
		+ " | measurableParameters : " + this.measurableParams + "}";
    }
}
