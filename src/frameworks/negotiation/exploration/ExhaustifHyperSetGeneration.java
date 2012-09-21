package frameworks.negotiation.exploration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public abstract class ExhaustifHyperSetGeneration<T> {

	private final Collection<Collection<T>>  hyperset;

	/*
	 *
	 */

	public ExhaustifHyperSetGeneration(final List<T> elems){
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

	//
	//	private  Collection<Collection<T>> exploreSubset(final List<T> elems){
	//		Integer.toBinaryString(arg0)
	//	}








	private  Collection<Collection<T>> generateHyperSet(final List<T> s){
		final Collection<Collection<T>> result = new ArrayList<Collection<T>>();
		for (int i = 0; i < Math.pow(2, s.size()); i++){
			final String[] number = Integer.toBinaryString(i).split("");
			assert number.length-1< s.size():" i : "+i+"\n number "+Arrays.asList(number)+"\n s "+s;
			final List<T> subset = new ArrayList<T>();
			int pos = s.size()-1;
			for (int j =number.length-1; j > 0; j--){
				if (number[j].equals("1")) {
					subset.add(s.get(pos));
				}
				pos--;
			}
			if (this.toKeep(subset)) {
				result.add(subset);
			}
		}
		return result;
	}





	private  Collection<Collection<T>> generateHyperSetOld(final Collection<T> elems){

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
		while (r.hasNext()) {
			if (!this.toKeep(r.next())) {
				r.remove();
			}
		}

	}

	public static void main(final String[] args){
		final List<String> s = Arrays.asList(new String[]{"a","b","c"});

		for (int i = 0; i < Math.pow(2, s.size()); i++){
			final String[] number = Integer.toBinaryString(i).split("");
			assert number.length< s.size();
			System.out.println("so?  "+i+" "+Integer.toBinaryString(i)+" "+Arrays.asList(number));
			final List<String> subset = new ArrayList<String>();
			int pos = s.size()-1;
			for (int j =number.length-1; j >= 0; j--){
				System.out.println("------------------->"+j+" : "+number[j]);
				if (number[j].equals("1")) {
					subset.add(s.get(pos));
				}
				pos--;
			}
			System.out.println("yo "+ subset);
		}
	}
}


//return new ExhaustifHyperSetGeneration<InformedCandidature<Contract, ActionSpec>>(
//		new ArrayList<InformedCandidature<Contract, ActionSpec>>(concerned)) {
//
//	@Override
//	public boolean toKeep(final Collection<InformedCandidature<Contract, ActionSpec>> alloc) {
//		return ResourceInformedSelectionCore.this.getMyAgent().Iaccept(currentState,alloc)
//				&& !MatchingCandidature.areAllCreation(alloc);
//	}
//}.getHyperset();