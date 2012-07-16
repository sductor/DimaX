package frameworks.negotiation.horizon.parameters;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import dima.basicinterfaces.DimaComponentInterface;
import frameworks.negotiation.horizon.negotiatingagent.HorizonIdentifier;

/**
 * Represents the set of the network interfaces of a machine, providing the
 * information on the links starting from and their goal node.
 * 
 * @param <Identifier>
 *            Type of Identifier indexing the LinkParameters
 * @param <Parameters>
 *            Type of LinkParameters
 * @author Vincent Letard
 */
public class InterfacesParameters<Identifier extends HorizonIdentifier, Parameters extends LinkParameters>
implements DimaComponentInterface {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 6701844582979452499L;

	/**
	 * Mapping between Identifiers (goals) and LinkParameters.
	 */
	private final Map<Identifier, Parameters> map;

	/**
	 * Constructs a new object of type InterfacesParameters using the provided
	 * mapping.
	 * 
	 * @param m
	 *            the initial and final mapping
	 */
	public InterfacesParameters(final Map<Identifier, Parameters> m) {
		this.map = Collections.unmodifiableMap(m);
	}

	/**
	 * Return a String representation of this InterfacesParameters
	 */
	@Override
	public String toString() {
		final StringBuilder str = new StringBuilder();
		str.append("(");
		for (final Parameters params : this.map.values()) {
			str.append(params + ", ");
		}
		str.setCharAt(str.lastIndexOf(", "), ')');
		return str.toString();
	}

	/**
	 * Adds the parameters of each element of the maps to build a new one.
	 * 
	 * @param <Identifier>
	 *            Type of Identifiers indexing the LinkParameters
	 * 
	 * @param ifacesParams1
	 *            first map
	 * @param ifacesParams2
	 *            second map
	 * @return the map resulting of the addition of all the parameters in their
	 *         respective entries in the maps.
	 */
	public static <Identifier extends /* ResourceIdentifier & */HorizonIdentifier> InterfacesParameters<Identifier, LinkAllocableParameters> add(
			final InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams1,
			final InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams2) {
		final Map<Identifier, LinkAllocableParameters> newMap = new HashMap<Identifier, LinkAllocableParameters>();
		for (final Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams1.map
				.entrySet()) {
			final Identifier key = entry.getKey();
			if (ifacesParams2.map.containsKey(key)) {
				newMap.put(key, entry.getValue()
						.add(ifacesParams2.map.get(key)));
			} else {
				newMap.put(key, entry.getValue());
			}
		}
		for (final Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams2.map
				.entrySet()) {
			final Identifier key = entry.getKey();
			if (!ifacesParams1.map.containsKey(key)) {
				newMap.put(key, entry.getValue());
			}
		}
		return new InterfacesParameters<Identifier, LinkAllocableParameters>(
				newMap);
	}

	/**
	 * Subtracts the parameters of each element of the second map from the first
	 * one to build a new one.
	 * 
	 * @param <Identifier>
	 *            Type of Identifiers indexing the LinkParameters
	 * 
	 * @param ifacesParams1
	 *            first map
	 * @param ifacesParams2
	 *            second map
	 * @return the map resulting of the subtraction of all the parameters in
	 *         their respective entries in the maps.
	 */
	public static <Identifier extends /* ResourceIdentifier & */HorizonIdentifier> InterfacesParameters<Identifier, LinkAllocableParameters> subtract(
			final InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams1,
			final InterfacesParameters<Identifier, LinkAllocableParameters> ifacesParams2) {
		final Map<Identifier, LinkAllocableParameters> newMap = new HashMap<Identifier, LinkAllocableParameters>();
		for (final Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams1.map
				.entrySet()) {
			final Identifier key = entry.getKey();
			if (ifacesParams2.map.containsKey(key)) {
				newMap.put(key, entry.getValue().subtract(
						ifacesParams2.map.get(key)));
			} else {
				newMap.put(key, entry.getValue());
			}
		}
		for (final Map.Entry<Identifier, LinkAllocableParameters> entry : ifacesParams2.map
				.entrySet()) {
			final Identifier key = entry.getKey();
			if (!ifacesParams1.map.containsKey(key)) {
				throw new IllegalArgumentException(
						"Cannot subtract without a value.");
			}
		}
		return new InterfacesParameters<Identifier, LinkAllocableParameters>(
				newMap);
	}

	/**
	 * Returns the LinkParameters associated with the link towards the specified
	 * node.
	 * 
	 * @param key
	 *            The goal node of the link looked for.
	 * @return the Parameters of the link
	 * @throws UnexistingLinkException
	 *             if no link towards that goal exists from this node
	 */
	public Parameters get(final Identifier key) throws UnexistingLinkException {
		final Parameters result = this.map.get(key);
		if (null == result) {
			throw new UnexistingLinkException();
		}
		return result;
	}

	/**
	 * Returns a set of all links starting from this node.
	 * 
	 * @return a Set of Map.Entry
	 */
	public Set<Map.Entry<Identifier, Parameters>> entrySet() {
		return this.map.entrySet();
	}

	/**
	 * Returns all the Parameters of the links starting from this node.
	 * 
	 * @return a Set of LinkParameters
	 */
	public Collection<Parameters> values() {
		return this.map.values();
	}
}
