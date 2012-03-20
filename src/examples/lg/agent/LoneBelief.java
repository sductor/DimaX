package  examples.lg.agent;

import examples.lg.model.GameException;
import examples.lg.model.Letter;
import examples.lg.model.LetterGame;
import examples.lg.strategy.DropHeuristic;

/**
 * @author Arnaud
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LoneBelief {

	private final LetterGame game;
	private Letter neededLetter;
	private boolean complete;


	public LoneBelief(final LetterGame g) {
		this.game = g;
		this.neededLetter = null;
		this.complete = false;
	}

	public Letter getNeededLetter() {
		return this.neededLetter;
	}

	public void useLetter() {
		try {
			this.game.useLetter(this.getNeededLetter());
		} catch (final GameException e) {
			e.printStackTrace(System.out);
		}
	}

	public void dropLetter() {
		final Letter l = DropHeuristic.whichToDrop(this.game);
		if (l != null) {
			try {
				// action simple base sur la dcision complexe
				this.game.dropLetter(l);
			} catch (final GameException e) {
				e.printStackTrace(System.out);
			}
		}
	}

	/**
	 * Returns the game.
	 * @return LetterGame
	 */
	public LetterGame getGame() {
		return this.game;
	}



	/**
	 * Returns the complete.
	 * @return boolean
	 */
	public boolean isComplete() {
		return this.complete;
	}



	/**
	 * Sets the complete.
	 * @param complete The complete to set
	 */
	public void checkComplete() {
		this.complete = this.game.getWordToComplete().isComplete();
	}

	/**
	 * Sets the neededLetter.
	 * @param neededLetter The neededLetter to set
	 */
	public void checkNeededLetter() {
		this.neededLetter = this.game.getWordToComplete().getNeededLetter();
	}



}
