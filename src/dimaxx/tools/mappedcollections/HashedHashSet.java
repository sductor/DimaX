package dimaxx.tools.mappedcollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;

public class HashedHashSet<K, V> extends Hashtable<K, Set<V>> {

	private static final long serialVersionUID = 3032270919408520500L;

	public boolean add(final K key, final V value) {
		if (this.containsKey(key)) {
			final Set<V> contenu = this.get(key);
			final boolean alreadyContains = contenu.add(value);
			this.put(key, contenu);
			return alreadyContains;
		} else {
			final Set<V> contenu = new HashSet<V>();
			contenu.add(value);
			this.put(key, contenu);
			return false;
		}
	}

	/**
	 * Remove the element of the collection mapped by the key
	 * @param key
	 * @param value
	 * @return
	 */
	public boolean remove(final K key, final V value) {
		final boolean r = this.get(key).remove(value);
		if (this.get(key).isEmpty()) {
			this.remove(key);
		}
		return r;
	}

	public Boolean removeAll(final K key, final Collection<V> values) {
		final Boolean r = this.get(key).removeAll(values);
		if (this.get(key).isEmpty()) {
			this.remove(key);
		}
		return r;
	}

	/**
	 * Fonction coûteuse
	 * @param value
	 * @return
	 */
	public boolean containsAvalue(final V item) {
		for (final K k : this.keySet()) {
			if (this.get(k).contains(item)) {
				return true;
			}
		}
		return false;
	}
	/**
	 * Fonction coûteuse
	 * @param value
	 * @return the set a key that mapped this value
	 */
	public Collection<K> removeAvalue(final V item) {
		final Collection<K> relevantKeys = new ArrayList<K>();
		for (final K k : this.keySet()) {
			if (this.get(k).contains(item)) {
				relevantKeys.add(k);
			}
		}
		for (final K k : relevantKeys) {
			this.remove(k,item);
		}
		return relevantKeys;
	}
	/**
	 * Fonction coûteuse
	 * @param meanProtoTimeExec
	 * @return
	 */
	public Collection<V> getAllValues() {
		final Collection<V> finalValues = new HashSet<V>();
		for (final Set<V> l : super.values()) {
			finalValues.addAll(l);
		}
		return finalValues;
	}

	@Override
	public synchronized Set<V> get(final Object key){
		if (this.containsKey(key)) {
			return super.get(key);
		} else {
			return new HashSet<V>();
		}
	}
}


//public V getLast(final K key) {
//	return this.get(key).getLast();
//}
//
//public V getFirst(final K key) {
//	return this.get(key).getFirst();
//}
//public V removeLast(final K key) {
//	return this.get(key).getFirst();
//}
//
//public V removeFirst(final K key) {
//	return this.get(key).removeFirst();
//}