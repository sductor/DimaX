package dimaxx.tools.mappedcollections;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

/**
 * Provides an adjacency map designed for undirected graphs or reflexive
 * relations in general. It guarantees that <code>get(V1) == V2</code> implies
 * <code>get(V2) == V1</code>. Add methods for such a structure ignore duplicate
 * elements. Additional information of type P on links can also be provided.
 * 
 * @author Vincent Letard
 * 
 * @param <V>
 *            type of the objects associated in this ReflexiveAdjacencyMap
 * @param <P>
 *            type of the parameters associated with links
 */
public class ReflexiveAdjacencyMap<V, P> {

    /**
     * Serial version identifier.
     */
    private static final long serialVersionUID = 2026814661687318199L;

    /**
     * Parameters of each association. Indexed by an injective function of the
     * respective equals of both associated values.
     */
    private Map<Integer, P> parameters;

    /**
     * Hash of adjacency.
     */
    private HashedHashSet<V, V> adjacency;

    /**
     * Default constructor.
     */
    public ReflexiveAdjacencyMap() {
	super();
	this.parameters = new HashMap<Integer, P>();
	this.adjacency = new HashedHashSet<V, V>();
    }

    // /**
    // * Constructs a ReflexiveAdjacencyMap using the one provided.
    // *
    // * @param map
    // */
    // public ReflexiveAdjacencyMap(ReflexiveAdjacencyMap<V, P> map) {
    // super();
    // this.addAll(map.entrySet());
    // }

    /**
     * Adds a parameterized association to the ReflexiveAdjacencyMap. Does
     * nothing if the association already exists. Use setParam to edit the
     * parameters.
     * 
     * @param obj1
     * @param obj2
     * @param param
     *            Parameters of the association.
     * @return <code>true</code> if the ReflexiveAdjacencyMap did not already
     *         contain the association or if the existing association is similar
     *         to one to be added.
     */
    public boolean add(final V obj1, final V obj2, final P param) {
	assert (this.checkAssociation(obj1, obj2));

	if (this.adjacency.get(obj1).contains(obj2)) {
	    if (this.parameters.get(
		    getIndexOfCouple(obj1.hashCode(), obj2.hashCode())).equals(
		    param))
		return true;
	    else
		return false;
	}
	boolean add1 = this.adjacency.add(obj1, obj2);
	boolean add2 = this.adjacency.add(obj2, obj1);
	this.parameters
.put(getIndexOfCouple(obj1.hashCode(), obj2.hashCode()),
		param);

	assert (true == add1 && true == add2);
	return true;
    }

    public static int getIndexOfCouple(final int i1, final int i2) {
	if (i1 < 0 || i2 < 0) {
	    throw new RuntimeException("Invalid argument");
	}
	if (i1 >= i2)
	    return (i1 * (i1 + 1)) / 2 + i2;
	else
	    return (i2 * (i2 + 1)) / 2 + i1;
    }

    public static int[] getCoupleOfIndex(final int index) {
	if (index < 0) {
	    throw new RuntimeException("Invalid argument");
	}
	int[] vector = new int[2];
	// int s = 0;
	// for (vector[0] = 0; s + vector[0] + 1 < index; vector[0]++) {
	// s += vector[0] + 1;
	// }
	// vector[1] = index - s;

	double delta = new Double(1 / 4 - 2 * index);
	vector[0] = Math.round((float) Math.floor(-1. / 2. + Math.sqrt(delta)));
	vector[1] = index - vector[0];

	assert (2 == vector.length);
	return vector;
    }

    // /**
    // * Adds all the elements of entryset to the ReflexiveAdjacencyMap.
    // *
    // * @param entryset
    // * The Set of the Map.Entry which will be added.
    // * @return <code>true</code> if the object changed as a result of the
    // call.
    // */
    // public boolean addAll(Set<Map.Entry<V, Set<V>>> entryset) {
    // boolean mod = false;
    // Iterator<Map.Entry<V, Set<V>>> it = entryset.iterator();
    // while (it.hasNext()) {
    // Map.Entry<V, Set<V>> entry = it.next();
    // mod = this.addAll(entry.getKey(), entry.getValue()) | mod;
    // }
    // return mod;
    // }

    // /**
    // * Associates all the elements in values with the key, and the key with
    // all
    // * the elements of values.
    // *
    // * @param key
    // * @param values
    // * @return <code>true</code> if the object changed as a result of the call
    // */
    // public boolean addAll(V key, Set<V> values) {
    // boolean mod = false;
    // Iterator<V> it = values.iterator();
    // while (it.hasNext())
    // mod = this.add(key, it.next()) | mod;
    // return mod;
    // }

    public P remove(final V obj1, final V obj2) {
	assert (this.checkAssociation(obj1, obj2));

	this.adjacency.remove(obj1, obj2);
	this.adjacency.remove(obj2, obj1);
	return this.parameters.remove(this.getIndexOfCouple(obj1.hashCode(),
		obj2
		.hashCode()));
    }

    public P get(final V obj1, final V obj2) {
	assert (this.checkAssociation(obj1, obj2));

	return this.parameters.get(this.getIndexOfCouple(obj1.hashCode(), obj2
		.hashCode()));
    }

    public Collection<V> getAssociation(final V obj) {
	return this.adjacency.get(obj);
    }

    public boolean removeAll(final V obj) {
	Collection<V> adj = this.adjacency.get(obj);
	this.adjacency.remove(obj); // XXX plus de liens vers la value suppression

	boolean b = false;
	Iterator<V> it = adj.iterator();
	while (it.hasNext()) {
	    b = this.adjacency.remove(it.next(), obj) || b;
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
	return (this.adjacency.get(obj1).contains(obj2) == this.adjacency.get(
		obj2).contains(obj1) == this.parameters
		.containsKey(getIndexOfCouple(
		obj1.hashCode(), obj2.hashCode())));
    }

    public Set<V> keySet() {
	return adjacency.keySet();
    }
}

// class Couple<E1, E2> {
//
// final E1 elt1;
// final E2 elt2;
//    
// public Couple(final E1 elt1, final E2 elt2) {
// this.elt1 = elt1;
// this.elt2 = elt2;
// }
//
// public E1 getElt1() {
// return elt1;
// }
//
// public E2 getElt2() {
// return elt2;
// }
//
// @Override
// public int hashCode() {
// int h1 = this.elt1.hashCode(), h2 = this.elt2.hashCode();
// int sum = h1 + h2;
// return (sum * sum + 3 * h1 + h2) / 2;
// }
//
// @Override
// public boolean equals(Object obj) {
// return (obj instanceof Couple<?, ?>
// && this.getElt1().equals(((Couple<?, ?>) obj).getElt1()) && this
// .getElt2().equals(((Couple<?, ?>) obj).getElt2()));
// }
// }

/*
 * abstract class Vector { private final ArrayList<Class<?>> types;
 * 
 * public Vector(final List<Class<?>> types) { this.types = new
 * ArrayList<Class<?>>(types); } }
 */