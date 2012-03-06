package negotiation.horizon.negociatingagent;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.services.library.information.ObservationService.Information;

/**
 * The state of a SubstrateNode. The fields inherited from
 * {@link AbstractSingleNodeState} represent the level of service provided by
 * the SubstrateNode.
 * 
 * @author Vincent Letard
 */
public class SubstrateNodeState extends AbstractSingleNodeState {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = -2415474717777657012L;

    /**
     * Amount of computation capacity currently allocated.
     */
    private int allocatedProcessor = 0;
    /**
     * Amount of ram currently allocated.
     */
    private int allocatedRAM = 0;

    /**
     * Constructs a new SubstrateNodeState. Allocated resources are initially
     * set to 0.
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
    public SubstrateNodeState(AgentIdentifier myAgent, int stateNumber,
	    float packetLossRate, int delay, int jitter, int bandwidth,
	    int processor, int ram) {
	super(myAgent, stateNumber, packetLossRate, delay, jitter, bandwidth,
		processor, ram);
	this.allocatedProcessor = 0;
	this.allocatedRAM = 0;
    }
    
    /**
     * @return the value of the field allocatedProcessor.
     */
    public int getAllocatedProc() {
	return this.allocatedProcessor;
    }

    /**
     * Modifies the amount of allocated processor.
     * 
     * @param amount
     *            The amount allocated/freed (could be positive or negative).
     */
    public void allocateProcessor(int amount) {
	int new_val = amount + this.allocatedProcessor;
	assert (new_val >= 0);
	this.allocatedProcessor = new_val;
    }

    /**
     * Checks the validity of the new value of allocatedProcessor, which must be
     * positive and lower than the available amount.
     * 
     * @param new_val
     *            the new_value computed
     * @return false if the value is not valid, true otherwise.
     */
    private boolean checkProcUpdate(int new_val) {
	return (new_val >= 0 && new_val <= this.getProc());
    };

    /**
     * @return the value of the field allocatedRAM.
     */
    public int getAllocatedRAM() {
	return this.allocatedRAM;
    }

    /**
     * Modifies the amount of allocated ram.
     * 
     * @param amount
     *            The amount allocated/freed (could be positive or negative).
     */
    public void allocateRAM(int amount) {
	int new_val = amount + this.allocatedRAM;
	assert (new_val >= 0);
	this.allocatedRAM = new_val;
    }

    /**
     * Checks the validity of the new value of allocatedRAM, which must be
     * positive and lower than the available amount.
     * 
     * @param new_val
     *            the new_value computed
     * @return false if the value is not valid, true otherwise.
     */
    private boolean checkRAMUpdate(int new_val) {
	return (new_val >= 0 && new_val <= this.getRAM());
    };

    @Override
    public Collection<? extends AgentIdentifier> getMyResourceIdentifiers() {
	// TODO Auto-generated method stub
	return null;
    }

    @Override
    public Class<? extends Information> getMyResourcesClass() {
	return VirtualNodeState.class;
    }

    @Override
    public boolean isValid() {
	return (this.allocatedProcessor >= 0
		&& this.allocatedProcessor <= this.getProc()
		&& this.allocatedRAM >= 0 && this.allocatedRAM <= this.getRAM());
    }

}
