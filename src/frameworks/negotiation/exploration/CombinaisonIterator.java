package frameworks.negotiation.exploration;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;


public class CombinaisonIterator<T> implements Iterator<Collection<T>> {

	public List<T> univers;
	public Integer[] solPos;


	public CombinaisonIterator(final List<T> univers, final int k) {
		super();
		this.univers = univers;
		this.solPos = new Integer[k];
		for (int i = 0; i < k; i++) {
			this.solPos[i]=i;
		}
		this.solPos[k-1]=k-2;
	}

	@Override
	public boolean hasNext() {
		return this.solPos[0]<this.univers.size()-this.solPos.length;
	}

	@Override
	public Collection<T> next() {
		int nextPosToMove = this.solPos.length-1;
		int maxPos=this.univers.size()-1;

		assert nextPosToMove!=-1:nextPosToMove+ " "+maxPos;
		while (this.solPos[nextPosToMove]==maxPos){
			nextPosToMove--;
			maxPos--;
			assert nextPosToMove!=-1:nextPosToMove+ " "+maxPos;
		}
		if (nextPosToMove==-1){
			assert !this.hasNext();
			return null;
		}
		this.solPos[nextPosToMove]++;
		for (int i = 1; i< this.solPos.length-nextPosToMove; i++){
			this.solPos[nextPosToMove+i]=this.solPos[nextPosToMove]+i;
		}
		return this.convertSol(this.solPos);
	}

	@Override
	public void remove() {
		throw new RuntimeException();
	}

	//
	//
	//


	private Collection<T> convertSol(final Integer[] solPos){
		final Collection<T> sol = new ArrayList<T>();
		for (final Integer pos : solPos){
			sol.add(this.univers.get(pos));
		}
		return sol;
	}

	public static void main(final String[] args){
		final CombinaisonIterator<Integer> it = new CombinaisonIterator<Integer>(
				Arrays.asList(new Integer[]{0,1,2,3,4,5,6,7,8}), 3);

		System.out.println(it.univers);
		System.out.println("");
		while (it.hasNext()){
			System.out.println(it.afficheIntel(it.next()));
			System.out.println("");
		}
	}

	private String afficheIntel(final Collection<T> sol){
		String r = new String();
		for (int i = 0; i < this.univers.size(); i++){
			if (sol.contains(this.univers.get(i))){
				r+='X';
			}else {
				r+='_';
			}
			r+=" ";
		}
		return r;
	}
}
//
//	// print all subsets of the characters in s
//	public static void comb1(String s) { comb1("", s); }
//
//	// print all subsets of the remaining elements, with given prefix
//	private static void comb1(String prefix, String s) {
//		if (s.length() > 0) {
//			System.out.println(prefix + s.charAt(0));
//			comb1(prefix + s.charAt(0), s.substring(1));
//			comb1(prefix,               s.substring(1));
//		}
//	}
//
//	// alternate implementation
//	public static void comb2(String s) { comb2("", s); }
//	private static void comb2(String prefix, String s) {
//		System.out.println(prefix);
//		for (int i = 0; i < s.length(); i++)
//			comb2(prefix + s.charAt(i), s.substring(i + 1));
//	}
//
//
//	// read in N from command line, and print all subsets among N elements
//	public static void main(String[] args) {
//		int N = 3;//Integer.parseInt(args[0]);
//		String alphabet = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";
//		String elements = alphabet.substring(0, N);
//
//		// using first implementation
//		comb1(elements);
//		System.out.println();
//
//		// using second implementation
//		comb2(elements);
//		System.out.println();
//	}
//


//	// Charset
//	 static private char[] _charset = "012345".toCharArray();
//	// Longueur max
//	 static private Integer _longueur = 8;
//
//	// Fonction qui calcule le nombre de combinaisons max pour un charset et une longueur données
//	private static Integer maxCombi (Integer charsetsize, Integer longueur)
//	{
//	    // Variable qui cumule les combinaisons possibles (par défaut = 0)
//	    Double max = 0.0;
//
//	    // Cumul pour toutes les longueurs possibles
//	    for (int j=1;j<=longueur;j++)
//	   {
//	        max += Math.pow((double)charsetsize, (double)j);
//	    }
//
//	    return max.intValue();
//	}
//
//	public static void main(String[] args)
//	{
//		// Nombre de combinaisons possibles pour le charset _charset et la longueur _longueur définie plus haut
//		Integer nbCombinaison = maxCombi(_charset.length, _longueur);
//
//	        // Mot composé par la methode computeMot
//	        String mot = "";
//
//	        // Iteration sur la méthode computeMot
//	        for (int i=0;i<nbCombinaison;i++)
//	        {
//	            mot = computeMot (_charset.length,i);
//	            System.out.println (mot);
//	        }
//	}
//
//	private static String computeMot (Integer charsetsize, Integer indice)
//	{
//	    // Mot à retourner (par défaut vide)
//	    String result = "";
//
//	    // Calcul du mot
//	    while (indice>=0) {
//	         result = _charset[(indice%charsetsize)] + result;
//	         indice = (indice/charsetsize) - 1;
//	    }
//
//	    return result;
//	}
//

//}
