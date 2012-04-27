package dimaxx.tools.mappedcollections;

import java.util.Map;
import java.util.Set;

public class ReflexiveBinaryAdjacencyMap<Element, Parameter> extends
	AbstractBinaryAdjacencyMap<Set<Element>, Parameter> {

    public ReflexiveBinaryAdjacencyMap(){
	super();
    }

    public ReflexiveBinaryAdjacencyMap(final Map<Set<Element>, Parameter> t) {
	super(t);
    }

    public ReflexiveBinaryAdjacencyMap(final int initialCapacity) {
	super(initialCapacity);
    }

    public ReflexiveBinaryAdjacencyMap(final int initialCapacity,
	    final float loadFactor) {
	super(initialCapacity, loadFactor);
    }
}
