package dimaxx.tools;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public abstract class HyperSetGeneration<T> {

	private final Collection<Collection<T>>  hyperset;

	/*
	 * 
	 */

	public HyperSetGeneration(final Collection<T> elems){
		this.hyperset = this.generateHyperSet(elems);
		this.filter(this.hyperset);
	}

	public Collection<Collection<T>> getHyperset() {
		return this.hyperset;
	}

	/*
	 * 
	 */

	public abstract boolean toKeep(Collection<T> elem);

	/*
	 * 
	 */

	private  Collection<Collection<T>> generateHyperSet(final Collection<T> elems){

		final Collection<Collection<T>> result =
				new HashSet<Collection<T>>();
		final Collection<Collection<T>> toAdd =
				new HashSet<Collection<T>>();

		for (final T singleton : elems) {
			final List<T> a = new ArrayList<T>();
			a.add(singleton);
			toAdd.add(a);//on ajoute le contrat singleton

			//on ajoute tous les précédent ensemble enrichi avec le singleton
			for (final Collection<T> alloc : result){
				final List<T> a2= new ArrayList<T>();
				a2.addAll(alloc);
				a2.add(singleton);
				toAdd.add(a2);
			}

			result.addAll(toAdd);
			toAdd.clear();
		}

		return result;
	}


	private  void filter(final Collection<Collection<T>> hyperset){
		final Iterator<Collection<T>> r = hyperset.iterator();
		while (r.hasNext())
			if (!this.toKeep(r.next()))
				r.remove();

	}
}
