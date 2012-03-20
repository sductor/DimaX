package  examples.lg.model;

import java.util.Vector;

/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Word {

	private String word;
	private int index; // position courante dans la construction du mot

	public Word(final String str) throws Exception {
		this.index = -1;
		if (Word.qualifies(str)) {
			this.word = str;
		} else {
			throw new Exception("Word contains exotic characters!");
		}

	}

	public Letter getLastAddedLetter() {

		try {
			return new Letter(this.word.charAt(this.index));
		} catch (final Exception e) {
			// should never occur
			return null;
		}
	}

	public String getCurrentSubword() {
		String ret = new String();
		ret = this.word.substring(0, this.index+1);
		return ret;
	}

	public Letter getNeededLetter() {
		try {
			return new Letter(this.word.charAt(this.index+1));
		} catch (final Exception e) {
			// should never occur
			return null;
		}
	}

	public Vector getNeededLetters() {
		final String end =  this.word.substring(this.index+1, this.word.length());
		final char[] ch = end.toCharArray();
		final Vector vec = new Vector();
		for (final char element : ch) {
			try {
				vec.add(new Letter(element));
			} catch (final Exception e) {
			}
		}
		return vec;
	}



	public static boolean qualifies(final String str) {
		boolean ret = true;
		final char[] tab = str.toCharArray();
		for (int i=0; i<tab.length; i++) {
			if (!(tab[i]>='A' && tab[i]<='Z')) {
				ret = false;
			}
		}
		return ret;
	}

	public void addLetter(final Letter l) throws GameException {
		if (this.isComplete()) {
			throw new GameException("Word already complete!");
		};
		if (this.getNeededLetter().getLetter() == l.getLetter()) {
			this.index++;
		} else {
			throw new GameException("Bad letter!");
		}

	}

	public boolean isComplete() {
		return this.index == this.word.length() - 1;
	}

	/**
	 * Returns the word.
	 * @return String
	 */
	public String getWord() {
		return this.word;
	}

	public static void main(final String[] args) {

		try {
			final Word w = new Word("BONJOUR");
			w.addLetter(new Letter('B'));
			w.addLetter(new Letter('O'));
			w.addLetter(new Letter('N'));
			System.out.println(w.getCurrentSubword());
			System.out.println("Last: " + w.getLastAddedLetter().getLetter());
			System.out.println("Needed: " + w.getNeededLetter().getLetter());
			w.addLetter(new Letter('J'));
			w.addLetter(new Letter('O'));
			w.addLetter(new Letter('U'));
			System.out.println(w.isComplete());
			w.addLetter(new Letter('R'));
			System.out.println(w.isComplete());
			w.addLetter(new Letter('R'));
		} catch (final Exception e) {
			e.printStackTrace(System.out);
		}

	}

}
