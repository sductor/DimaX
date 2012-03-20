package dimaxx.tools.mappedcollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.List;

public class HashedHashList<K, V> extends Hashtable<K, List<V>> {

	private static final long serialVersionUID = 3032270919408520500L;

	public Collection<V> add(final K key, final V value) {
		if (this.containsKey(key)) {
			final List<V> contenu = this.get(key);
			contenu.add(value);
			return this.put(key, contenu);
		} else {
			final List<V> contenu = new ArrayList<V>();
			contenu.add(value);
			return this.put(key, contenu);
		}
	}

	public Boolean remove(final K key, final V value) {
		final Boolean r = this.get(key).remove(value);
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
	public boolean containsAvalue(final V value) {
		for (final K k : this.keySet()) {
			for (final V v : this.get(k)) {
				if (v.equals(value)) {
					return true;
				}
			}
		}
		return false;
	}

	/**
	 * Fonction coûteuse
	 * @param meanProtoTimeExec
	 * @return
	 */
	public Collection<V> getAllValues() {
		final Collection<V> finalValues = new HashSet<V>();
		for (final List<V> l : super.values()) {
			finalValues.addAll(l);
		}
		return finalValues;
	}

	@Override
	public synchronized List<V> get(final Object key){
		if (this.containsKey(key)) {
			return super.get(key);
		} else {
			return new ArrayList<V>();
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