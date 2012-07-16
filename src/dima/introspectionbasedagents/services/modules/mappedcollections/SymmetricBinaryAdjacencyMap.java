package dima.introspectionbasedagents.services.modules.mappedcollections;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

/**
 * An symmetric adjacency map to connect elements with a parameter describing
 * their relation.
 * 
 * @param <Element>
 *            Type of the elements associated
 * @param <Parameter>
 *            Type of the parameter of the links
 * @author Vincent Letard
 */
public class SymmetricBinaryAdjacencyMap<Element extends Comparable<Element>, Parameter> {

	/**
	 * Serial version identifier.
	 */
	private static final long serialVersionUID = 6960173895284238727L;

	/**
	 * Mapping of the elements.
	 */
	private final HashedHashSet<Element, Element> paired;
	/**
	 * Mapping of the link parameters.
	 */
	private final Map<OrderedPair<Element>, Parameter> params;

	/**
	 * Constructs a new empty SymmetricBinaryAdjacencyMap.
	 */
	public SymmetricBinaryAdjacencyMap() {
		super();
		this.paired = new HashedHashSet<Element, Element>();
		this.params = new HashMap<OrderedPair<Element>, Parameter>();
	}

	/**
	 * Adds a link between two elements (not necessarily pre-existing).
	 * 
	 * @param elts
	 *            Elements to link
	 * @param p
	 *            Parameter (label) of the link.
	 * @return the previous parameter of the link, or <code>null</code> if the
	 *         link did not exist before.
	 */
	public Parameter add(final OrderedPair<Element> elts, final Parameter p) {
		this.paired.add(elts.getFirst(), elts.getSecond());
		this.paired.add(elts.getSecond(), elts.getFirst());
		return this.params.put(elts, p);
	}

	/**
	 * Returns the elements associated with the one provided.
	 * 
	 * @param e
	 *            the element looked for.
	 * @return the Set of all the elements linked to e
	 */
	public Set<Element> getPaired(final Element e) {
		final Set<Element> result = this.paired.get(e);
		return result == null ? new HashSet<Element>() : result;
	}

	/**
	 * Gives the parameter of a link.
	 * 
	 * @param elts
	 *            Elements linked
	 * @return the parameter of the link between these element.
	 * @throws ElementsNotLinkedException
	 *             if the binding does not exists in the Map
	 */
	public Parameter getLinkParam(final OrderedPair<Element> elts)
			throws ElementsNotLinkedException {
		final Parameter result = this.params.get(elts);
		if (null == result) {
			throw new ElementsNotLinkedException();
		} else {
			return result;
		}
	}
}
