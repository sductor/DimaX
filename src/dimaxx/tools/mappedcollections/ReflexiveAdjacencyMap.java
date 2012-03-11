package dimaxx.tools.mappedcollections;

import java.util.Collection;
import java.util.Iterator;

/**
 * Provides an adjacency map designed for undirected graphs or reflexive
 * relations in general. It guarantees that <code>get(V1) == V2</code> implies
 * <code>get(V2) == V1</code>
 * 
 * @author Vincent Letard
 * 
 * @param <V>
 *            type of the objects associated in this ReflexiveAdjacencyMap
 */
public class ReflexiveAdjacencyMap<V> extends HashedHashSet<V, V> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2026814661687318199L;

    @Override
    public boolean add(V obj1, V obj2) {
	assert (this.checkAssociation(obj1, obj2));
	super.add(obj1, obj2);
	return super.add(obj2, obj1);
    }

    @Override
    public boolean remove(final V obj1, final V obj2) {
	assert (this.checkAssociation(obj1, obj2));
	super.remove(obj1, obj2);
	return super.remove(obj2, obj1);
    }

    @Override
    public Boolean removeAll(final V obj1, final Collection<V> objs) {
	Iterator<V> it = objs.iterator();
	Boolean b = true;
	while (it.hasNext()) {
	    b = this.remove(obj1, it.next()) || b;
	}
	return b;
    }

    /**
     * Checks the consistency of the association between <code>obj1</code> and
     * <code>obj2</code>.
     * 
     * @param obj1
     * @param obj2
     * @return <code>true</code> if the association meets the class contract
     *         specification.
     */
    private boolean checkAssociation(V obj1, V obj2) {
	return this.get(obj1).contains(obj2) == this.get(obj2).contains(obj1);
    }
}
