package negotiation.horizon.parameters;

import java.util.HashMap;
import java.util.Map;

import negotiation.horizon.negotiatingagent.HorizonIdentifier;
import dima.basicinterfaces.DimaComponentInterface;

public class InterfacesParameters<Identifier extends HorizonIdentifier, Links extends LinkParameters>
	extends HashMap<Identifier, Links> implements DimaComponentInterface {

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

    /**
     * Adds the parameters of each element of the maps to build a new one.
     * 
     * @param ifacesParams1
     *            first map
     * @param ifacesParams2
     *            second map
     * @return the map resulting of the addition of all the parameters in their
     *         respective entries in the maps.
     */
    public static <Identifier extends /* ResourceIdentifier & */HorizonIdentifier> InterfacesParameters<Identifier, LinkAllocableParameters> add(
	    InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams1,
	    InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams2) {
	final InterfacesParameters<Identifier, LinkAllocableParameters> newIfacesParams = new InterfacesParameters<Identifier, LinkAllocableParameters>();
	for (Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams1
		.entrySet()) {
	    Identifier key = entry.getKey();
	    if (ifacesParams2.containsKey(key))
		newIfacesParams.put(key, entry.getValue().add(
			ifacesParams2.get(key)));
	    else
		newIfacesParams.put(key, entry.getValue());
	}
	for (Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams2
		.entrySet()) {
	    Identifier key = entry.getKey();
	    if (!ifacesParams1.containsKey(key))
		newIfacesParams.put(key, entry.getValue());
	}
	return newIfacesParams;
    }

    /**
     * Subtracts the parameters of each element of the second map from the first
     * one to build a new one.
     * 
     * @param ifacesParams1
     *            first map
     * @param ifacesParams2
     *            second map
     * @return the map resulting of the subtraction of all the parameters in
     *         their respective entries in the maps.
     */
    public static <Identifier extends /* ResourceIdentifier & */HorizonIdentifier> InterfacesParameters<Identifier, LinkAllocableParameters> subtract(
	    InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams1,
	    InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams2) {
	final InterfacesParameters<Identifier, LinkAllocableParameters> newIfacesParams = new InterfacesParameters<Identifier, LinkAllocableParameters>();
	for (Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams1
		.entrySet()) {
	    Identifier key = entry.getKey();
	    if (ifacesParams2.containsKey(key))
		newIfacesParams.put(key, entry.getValue().subtract(
			ifacesParams2.get(key)));
	    else
		newIfacesParams.put(key, entry.getValue());
	}
	for (Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams2
		.entrySet()) {
	    Identifier key = entry.getKey();
	    if (!ifacesParams1.containsKey(key))
		throw new IllegalArgumentException(
			"Cannot subtract without a value.");
	}
	return newIfacesParams;
    }
}
