package dimaxx.tools.mappedcollections;

import java.util.Hashtable;
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
public class SymmetricBinaryAdjacencyMap<Element extends Comparable<Element>, Parameter>
	extends Hashtable<UnorderedPair<Element>, Parameter> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 6960173895284238727L;

    private final HashedHashSet<Element, Element> paired;

    public SymmetricBinaryAdjacencyMap() {
	super();
	this.paired = new HashedHashSet<Element, Element>();
    }

    public SymmetricBinaryAdjacencyMap(
	    final Map<UnorderedPair<Element>, Parameter> t) {
	super();
	this.paired = new HashedHashSet<Element, Element>();
	this.putAll(t);
    }

    public SymmetricBinaryAdjacencyMap(final int initialCapacity) {
	super(initialCapacity);
	this.paired = new HashedHashSet<Element, Element>();
    }

    public SymmetricBinaryAdjacencyMap(final int initialCapacity,
	    final float loadFactor) {
	super(initialCapacity, loadFactor);
	this.paired = new HashedHashSet<Element, Element>();
    }

    @Override
    public Parameter put(final UnorderedPair<Element> elts, final Parameter p) {
	this.paired.add(elts.getFirst(), elts.getSecond());
	this.paired.add(elts.getSecond(), elts.getFirst());
	return super.put(elts, p);
    }

    @Override
    public void putAll(
	    Map<? extends UnorderedPair<Element>, ? extends Parameter> map) {
	for (Map.Entry<?, ?> entry : map.entrySet())
	    this.put((UnorderedPair<Element>) entry.getKey(), (Parameter) entry
		    .getValue());
    }

    public Set<Element> getPaired(final Element e) {
	return this.paired.get(e);
    }
}
