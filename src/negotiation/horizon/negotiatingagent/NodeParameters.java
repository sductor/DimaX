package negotiation.horizon.negotiatingagent;

import dima.support.GimaObject;

public class NodeParameters extends GimaObject {

    /**
     * Computation capacity in IPS.
     * 
     * @uml.property name="processor"
     */
    private final Integer processor;
    /**
     * Amount of memory in ko.
     * 
     * @uml.property name="ram"
     */
    private final Integer ram;

    /**
     * Level of security of the node.
     * 
     * @uml.property name="security"
     */
    private final Integer availability;

    public NodeParameters(final Integer processor, final Integer ram,
	    final Integer availability) {
	this.processor = processor;
	this.ram = ram;
	this.availability = availability;
    }

    /**
     * @return the value of the field processor.
     */
    public Integer getProcessor() {
	return this.processor;
    }

    /**
     * @return the value of the field ram.
     */
    public Integer getRam() {
	return this.ram;
    }

    /**
     * @return the value of the field security.
     */
    public Integer getAvailability() {
	return this.availability;
    }

    public boolean isValid() {
	return processor >= 0 && ram >= 0 && availability >= 0;
    }
}
