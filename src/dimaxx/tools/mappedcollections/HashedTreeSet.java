package dimaxx.tools.mappedcollections;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.TreeSet;

public class HashedTreeSet<K, V> extends Hashtable<K, TreeSet<V>> {
	private static final long serialVersionUID = 3032270919408520500L;


	final Comparator<V> myComp;


	public HashedTreeSet(Comparator<V> myComp){
		this.myComp=myComp;
	}


	public boolean add(final K key, final V value) {
		if (this.containsKey(key)) {
			final TreeSet<V> contenu = this.get(key);
			final boolean alreadyContains = contenu.add(value);
			this.put(key, contenu);
			return alreadyContains;
		} else {
			final TreeSet<V> contenu = new TreeSet<V>(myComp);
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
		final boolean b = this.get(key).remove(value);
		if (this.get(key).isEmpty()) {
			TreeSet<V> v = this.remove(key);
			assert !this.containsKey(key):key+" "+this.get(key)+" "+v+" "+b+" \n--->"+this;
		}
		assert !this.containsKey(key)||!super.get(key).isEmpty();
		assert !this.get(key).contains(value);
		return b;
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
			if (this.get(k).remove(item))
				relevantKeys.add(k);
		}
		assert !this.containsAvalue(item);//:item + "-è-------------->"+this;
		assert !getAllValuesUnsorted().contains(item);//:item + "-è-------------->"+this;
		for (final K key : relevantKeys) {
			if (this.get(key).isEmpty()) {
				this.remove(key);
			}
		}
		return relevantKeys;
	}	

	//	/**
	//	 * Fonction coûteuse
	//	 * @param value
	//	 * @return the set a key that mapped this value
	//	 */
	//	public Collection<K> removeAvalue(final V item) {
	//		final Collection<K> relevantKeys = new ArrayList<K>();
	//		for (final K k : this.keySet()) {
	//			if (this.get(k).contains(item)) {
	//				relevantKeys.add(k);
	//			}
	//		}
	//		for (final K k : relevantKeys) {
	//			this.remove(k,item);
	//		}
	//		assert !getAllValuesUnsorted().contains(item);
	//		return relevantKeys;
	//	}

	/**
	 * Fonction *tres* coûteuse car refait le tri
	 * @param meanProtoTimeExec
	 * @return
	 */
	@Deprecated //tres couteux
	public TreeSet<V> getAllValues() {
		final TreeSet<V> finalValues = new TreeSet<V>(myComp);
		for (final TreeSet<V> l : super.values()) {
			finalValues.addAll(l);
		}
		return finalValues;
	}

	/**
	 * Fonction  coûteuse 
	 * @param meanProtoTimeExec
	 * @return
	 */
	public Set<V> getAllValuesUnsorted() {
		final Set<V> finalValues = new HashSet<V>();
		for (final TreeSet<V> l : this.values()) {
			finalValues.addAll(l);
		}
		return finalValues;
	}
	/**
	 * @param 
	 * @return
	 */
	public V getMaxValue() {
		V finalValue = null;
		for (final K key : super.keySet()){
			if (finalValue==null)
				finalValue=super.get(key).last();
			else {
				finalValue = myComp.compare(super.get(key).last(),finalValue)>0?super.get(key).last():finalValue;
			}
		}
		return finalValue;
	}

	/**
	 * @param 
	 * @return
	 */
	public V getMinValue() {
		V finalValue = null;
		for (final K key : super.keySet()){
			if (finalValue==null)
				finalValue=super.get(key).first();
			else {
				finalValue = myComp.compare(super.get(key).last(),finalValue)<0?super.get(key).first():finalValue;
			}
		}
		return finalValue;
	}

	/**
	 * @param a key
	 * @return the value associated to the key or an empty collection if no key is present
	 */
	@Override
	public synchronized TreeSet<V> get(final Object key){
		if (this.containsKey(key)) {
			return super.get(key);
		} else {
			return new TreeSet<V>(myComp);
		}
	}	
	/**
	 * @param a key
	 * @return the value associated to the key or an empty collection if no key is present
	 */
	@Override
	public synchronized TreeSet<V> remove(final Object key){
		if (this.containsKey(key)) {
			return super.remove(key);
		} else {
			return new TreeSet<V>(myComp);
		}
	}
}