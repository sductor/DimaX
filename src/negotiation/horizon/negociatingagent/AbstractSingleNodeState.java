package negotiation.horizon.negociatingagent;

import java.util.Collection;
import java.util.Map;

import negotiation.negotiationframework.rationality.SimpleAgentState;
import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.information.ObservationService.Information;
import dimaxx.tools.aggregator.AbstractCompensativeAggregation;

/**
 * This abstract class gathers fields and methods that are common properties of
 * a single node of the network.
 * 
 * @author Vincent Letard
 */
public abstract class AbstractSingleNodeState extends SimpleAgentState implements
HorizonSpecification {
    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2721088186118421802L;


    /**
     * Constructs a SingleNodeState initializing its fields.
     * 
     * @param myAgent
     * @param stateNumber
     * @param packetLossRate
     * @param delay
     * @param jitter
     * @param bandwidth
     * @param processor
     * @param ram
     */
    public AbstractSingleNodeState(AgentIdentifier myAgent, int stateNumber,
	    float packetLossRate, int delay, int jitter, int bandwidth,
	    int processor, int ram) {
	super(myAgent, stateNumber);

	assert (packetLossRate >= 0. && packetLossRate <= 100.);
	assert (delay >= 0);
	assert (jitter >= 0);
	assert (bandwidth >= 0);
	assert (processor > 0);
	assert (ram >= 0);

	this.packetLossRate = packetLossRate;
	this.delay = delay;
	this.jitter = jitter;
	this.bandwidth = bandwidth;
	this.processor = processor;
	this.ram = ram;
    }

    /**
     * @return the value of the field processor.
     */
    public int getProc() {
	return this.processor;
    }

    /**
     * @return the value of the field ram.
     */
    public int getRAM() {
	return this.ram;
    }

    @Override
    public Double getNumericValue(Information e) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public AbstractCompensativeAggregation<Information> fuse(
	    Collection<? extends AbstractCompensativeAggregation<? extends Information>> averages) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Information getRepresentativeElement(
	    Collection<? extends Information> elems) {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Information getRepresentativeElement(
	    Map<? extends Information, Double> elems) {
	// TODO Auto-generated method stub
	return null;
    }

}