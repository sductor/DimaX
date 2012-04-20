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
public class LetterGame
{
	private final Word wordToComplete;
	private final Deck deck;
	public LetterGame(final String word, final String deck) throws Exception {
		this.wordToComplete = new Word(word);
		this.deck = new Deck(deck);

	}
	public void dropLetter(final Letter l) throws GameException{
		// vrifier qu'on l'a dans le deck
		if (!this.deck.contains(l)) {
			throw new GameException("Can't use a letter absent in the deck!");
		}
		this.deck.dropLetter(l);

		// on pique dans la pioche pour remplacer

		this.pickLetter();

	}
	public void exchangeLetter(final Letter l) throws GameException
	{
		// vrifier qu'on l'a dans le deck
		if (!this.deck.contains(l)) {
			throw new GameException("Can't use a letter absent in the deck!");
		}

		this.deck.dropLetter(l);

		// on lance un appel  proposition

		////exchangeLetter();
		//pickLetter();
	}
	/**
	 * Returns the deck.
	 * @return Deck
	 */
	public Deck getDeck() {
		return this.deck;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (10/09/2003 12:30:16)
	 */
	public Vector getNoNeededLetters()
	{
		final Vector needed = new Vector(this.getWordToComplete().getNeededLetters());

		final Vector all = new Vector(this.getDeck().getLetters());


		for (int i=0;i<needed.size();i++) {
			if ( all.contains( needed.get(i) ) ) {
				all.remove(needed.get(i));
			}
		}

		System.out.println(this.getWordToComplete().getWord()+" --> Les needed sont :"+needed+" et les non needed "+all);

		return all;
	}
	/**
	 * Returns the wordToComplete.
	 * @return Word
	 */
	public Word getWordToComplete() {
		return this.wordToComplete;
	}
	public boolean hasLetter(final Letter l) {
		return this.deck.contains(l);
	}
	public static void main(final String[] args) {
		try {
			final LetterGame game = new LetterGame("BONJOUR", "ANOBB");
			game.useLetter(new Letter('B'));
			game.useLetter(new Letter('O'));
			game.dropLetter(new Letter('B'));

			System.out.println(game.getDeck());
			game.pickLetter();
			game.pickLetter();
			game.pickLetter();
			System.out.println(game.getWordToComplete().getCurrentSubword());
		} catch (final Exception e) {
			e.printStackTrace(System.out);
		}

	}
	public Letter neededLetter() {
		return this.wordToComplete.getNeededLetter();
	}
	public void pickLetter() throws GameException {
		this.deck.addLetter(Supply.pick());
	}
	public void useLetter(final Letter l) throws GameException {
		// vrifier qu'on l'a dans le deck
		if (!this.deck.contains(l)) {
			throw new GameException("Can't use a letter absent in the deck!");
		}
		// ajouter au mot (si possible)
		try {
			this.wordToComplete.addLetter(l);
			// enlever du deck et repiocher
			this.dropLetter(l);

		} catch (final GameException e) {
			throw new GameException(e.getMessage());
		}
	}
}
