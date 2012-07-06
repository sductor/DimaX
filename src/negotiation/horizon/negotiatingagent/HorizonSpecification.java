package negotiation.horizon.negotiatingagent;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import negotiation.negotiationframework.contracts.AbstractActionSpecif;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

/**
 * Horizon contract specification classes must implement this interface.
 * 
 * @author Vincent Letard
 */
public abstract class HorizonSpecification implements AbstractActionSpecif {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2180105313398969970L;

    private final Long creationTime;

    public HorizonSpecification() {
	this.creationTime = new Date().getTime();
    }

    @Override
    public Long getCreationTime() {
	return this.creationTime;
    }

    @Override
    public long getUptime() {
	return new Date().getTime() - this.creationTime;
    }

    @Override
    public int isNewerThan(Information that) {
	if (that instanceof HorizonSpecification)
	    return (int) (this.creationTime - ((HorizonSpecification) that).creationTime);
	else
	    throw new IllegalArgumentException();
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

}
