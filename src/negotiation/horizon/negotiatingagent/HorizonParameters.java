package negotiation.horizon.negotiatingagent;

import java.util.Collection;
import java.util.Map;

import negotiation.horizon.AbstractInformation;
import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

/**
 * @author Vincent Letard
 */
public class HorizonParameters<Identifier extends HorizonIdentifier> extends
	AbstractInformation implements AbstractActionSpecif {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 8142462973752131803L;

    private final NodeParameters nodesParams;

    private final InterfacesParameters<Identifier> ifacesParams;

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
    public HorizonParameters(final AgentIdentifier myAgentIdentifier,
	    final NodeParameters nodesParams,
	    final InterfacesParameters ifacesParams) {
	super(myAgentIdentifier);
	this.nodesParams = nodesParams;
	this.ifacesParams = ifacesParams;
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
     * @return the value of the field ifacesParameters
     */
    public InterfacesParameters getInterfacesParameters() {
	return this.ifacesParams;
    }

    /**
     * A instance of SingleNodeParameters is valid iff all parameters Intervals
     * are positive.
     * 
     * @return <code>true</code> or <code>false</code> whether the instance is
     *         valid.
     */
    public boolean isValid() {
	return this.nodesParams.isValid() && this.ifacesParams.isValid();
    }

    @Override
    public String toString() {
	return "{" + this.nodesParams + " | ifaces:" + this.ifacesParams + "}";
    }

    @Override
    public Double getNumericValue(Information e) {
	throw new UnsupportedOperationException();
    }

    @Override
    public AbstractCompensativeAggregation<Information> fuse(
	    Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Information getRepresentativeElement(
	    Collection<? extends Information> elems) {
	throw new UnsupportedOperationException();
    }

    @Override
    public Information getRepresentativeElement(
	    Map<? extends Information, Double> elems) {
	throw new UnsupportedOperationException();
    }

    public NodeParameters getNodeParameters() {
	return this.getNodeParameters();
    }
}
