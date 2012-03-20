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
public abstract class LoneDomain extends dima.kernel.communicatingAgent.ATNBasedCommunicatingAgent{
	/**
	 *
	 */
	private static final long serialVersionUID = 4215144812105413765L;
	protected LetterGame game;







	public boolean hasNeededLetter() {
		return this.game.hasLetter(this.getNeededLetter());
	}

	public void display() {
		System.out.println("Word: " + this.game.getWordToComplete().getCurrentSubword());

		System.out.println(this.game.getDeck());
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
				// action simple bas�e sur la d�cision complexe
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
	 * Sets the complete.
	 * @param complete The complete to set
	 */
	public boolean isComplete() {
		return this.game.getWordToComplete().isComplete();
	}

	/**
	 * Sets the neededLetter.
	 * @param neededLetter The neededLetter to set
	 */
	public Letter getNeededLetter() {
		return this.game.getWordToComplete().getNeededLetter();
	}

	public boolean noCondition() {
		return true;
	}

	@Override
	public void noAction() {}
}
