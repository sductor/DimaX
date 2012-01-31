package  examples.lg.model;

import java.util.Enumeration;
import java.util.Vector;
/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Deck {

	private final Vector letters = new Vector();
	private final int size = 5;

	public Deck() {}

	public Deck(final String str) throws Exception{
		if(!Word.qualifies(str))
			throw new Exception("Hum hum...");
		final char[] ch = str.toCharArray();
		for (final char element : ch)
			this.letters.add(new Letter(element));
	}

	public boolean contains(final Letter l) {
		final char c = l.getLetter();
		boolean ret = false;
		for(final Enumeration e=this.letters.elements(); e.hasMoreElements();)
			if (((Letter)e.nextElement()).getLetter() == c)
				ret = true;

		return ret;
	}

	public void addLetter(final Letter l) throws GameException{
		if (this.letters.size() >= this.size)
			throw new GameException("Deck full!");
		this.letters.add(l);
	}

	public void dropLetter(final Letter l) throws GameException {
		boolean removed = false;
		final char c = l.getLetter();
		for(final Enumeration e=this.letters.elements(); e.hasMoreElements();) {
			final Letter l2 = (Letter)e.nextElement();
			if (l2.getLetter() == c && !removed) {
				this.letters.removeElement(l2);
				removed = true;
			}
		}
		if (removed = false)
			throw new GameException("Can't drop a letter that is absent!");
	}

	@Override
	public String toString()
	{
		String str = new String("");

		for(final Enumeration e=this.letters.elements(); e.hasMoreElements();)
			str = str + ((Letter)e.nextElement()).getLetter() + " ";

		return str;
	}

	public static void main(final String[] args) {
		Deck deck;
		try {
			deck = new Deck("ABBDA");
			System.out.println(deck.toString());
			System.out.println(deck.contains(new Letter('V')));
			System.out.println(deck.contains(new Letter('D')));
			deck.dropLetter(new Letter('B'));
			System.out.println(deck.toString());
			deck.dropLetter(new Letter('Z'));
			System.out.println(deck.toString());
		} catch (final Exception e) {
			e.printStackTrace(System.out);
		}


	}

	/**
	 * Returns the letters.
	 * @return Vector
	 */
	public Vector getLetters() {
		return this.letters;
	}

}
