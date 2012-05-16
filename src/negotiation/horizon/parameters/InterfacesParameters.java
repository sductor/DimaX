package negotiation.horizon.parameters;

import java.util.HashMap;
import java.util.Map;

import negotiation.horizon.negotiatingagent.HorizonIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public class InterfacesParameters<Links extends LinkParameters> extends
	HashMap<HorizonIdentifier/* TODO remonter le paramètre ? */, Links>
	implements DimaComponentInterface {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 6701844582979452499L;

    @Override
    public String toString() {
	StringBuilder str = new StringBuilder();
	str.append("(");
	for (Links params : this.values()) {
	    str.append(params + ", ");
	}
	str.setCharAt(str.lastIndexOf(", "), ')');
	return str.toString();
    }

    public static InterfacesParameters<LinkAllocableParameters> add(
	    InterfacesParameters<LinkAllocableParameters> ifacesParams1,
	    InterfacesParameters<LinkAllocableParameters> ifacesParams2) {
	final InterfacesParameters<LinkAllocableParameters> newIfacesParams = new InterfacesParameters<LinkAllocableParameters>();
	for (Map.Entry<HorizonIdentifier, LinkAllocableParameters> entry : ifacesParams1
		.entrySet()) {
	    HorizonIdentifier key = entry.getKey();
	    if (ifacesParams2.containsKey(key))
		newIfacesParams.put(key, entry.getValue().add(
			ifacesParams2.get(key)));
	    else
		newIfacesParams.put(key, entry.getValue());
	}
	for (Map.Entry<HorizonIdentifier, LinkAllocableParameters> entry : ifacesParams2
		.entrySet()) {
	    HorizonIdentifier key = entry.getKey();
	    if (!ifacesParams1.containsKey(key))
		newIfacesParams.put(key, entry.getValue());
	}
	return newIfacesParams;
    }

    public static InterfacesParameters<LinkAllocableParameters> subtract(
	    InterfacesParameters<LinkAllocableParameters> ifacesParams1,
	    InterfacesParameters<LinkAllocableParameters> ifacesParams2) {
	final InterfacesParameters<LinkAllocableParameters> newIfacesParams = new InterfacesParameters<LinkAllocableParameters>();
	for (Map.Entry<HorizonIdentifier, LinkAllocableParameters> entry : ifacesParams1
		.entrySet()) {
	    HorizonIdentifier key = entry.getKey();
	    if (ifacesParams2.containsKey(key))
		newIfacesParams.put(key, entry.getValue().subtract(
			ifacesParams2.get(key)));
	    else
		newIfacesParams.put(key, entry.getValue());
	}
	for (Map.Entry<HorizonIdentifier, LinkAllocableParameters> entry : ifacesParams2
		.entrySet()) {
	    HorizonIdentifier key = entry.getKey();
	    if (!ifacesParams1.containsKey(key))
		throw new IllegalArgumentException(
			"Cannot subtract without a value.");
	}
	return newIfacesParams;
    }
}
