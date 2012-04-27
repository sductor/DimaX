package dimaxx.tools.mappedcollections;

import java.util.Collection;
import java.util.Hashtable;
import java.util.Map;

public abstract class AbstractBinaryAdjacencyMap<Elements extends Collection<?>, Parameter>
	extends Hashtable<Elements, Parameter> {
    
    public AbstractBinaryAdjacencyMap() {
	super();
    }

    public AbstractBinaryAdjacencyMap(final int initialCapacity) {
	super(initialCapacity);
    }

    public AbstractBinaryAdjacencyMap(Map<Elements, Parameter> t) {
	super();
	this.putAll(t);
    }

    public AbstractBinaryAdjacencyMap(final int initialCapacity,
	    final float loadFactor) {
	super(initialCapacity, loadFactor);
    }

    /**
     * @throws {@link IllegalArgumentException} - if e.size() is not 2
     */
    @Override
    public Parameter put(final Elements elts, final Parameter p) {
	if (elts.size() != 2) {
	    throw new IllegalArgumentException(
		    "Cardinality of association must be 2.");
	} else
	    return super.put(elts, p);
    }

    // TODO Override putAll !
}