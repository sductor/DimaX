package  examples.lg.agent;

import dima.kernel.ProactiveComponents.ProactiveComponent;
import examples.lg.model.GameException;
import examples.lg.model.Letter;
import examples.lg.model.LetterGame;
import examples.lg.strategy.DropHeuristic;

/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LoneAgent2 extends ProactiveComponent {

	/**
	 *
	 */
	private static final long serialVersionUID = -7262505152684503032L;
	private LetterGame game;

	public LoneAgent2(final String word, final String deck) {
		try {
			this.game = new LetterGame(word, deck);
		} catch (final Exception e) {
			e.printStackTrace(System.out);
		}
	}

	/**
	 * @see Gdima.proactive.component.ProactiveComponent#isActive()
	 */
	@Override
	public boolean isActive() {
		return !this.game.getWordToComplete().isComplete();
	}

	@Override
	public void step() {}
	/**
	 * @see Gdima.proactive.component.ProactiveComponent#step()
	 */
	public void step2() {
		// perception simple
		final Letter needed = this.game.getWordToComplete().getNeededLetter();
		// perception plus complexe + d�cision simple
		if (this.game.getDeck().contains(needed))
			try {
				// action simple bas�e sur la perception simple
				this.game.useLetter(needed);
			} catch (final GameException e) {
				e.printStackTrace(System.out);
			}
		else { // il faut dropper une lettre, on se sert d'une heuristique
			// d�cision complexe
			final Letter l = DropHeuristic.whichToDrop(this.game);
			if (l != null)
				try {
					// action simple bas�e sur la d�cision complexe
					this.game.dropLetter(l);
				} catch (final GameException e) {
					e.printStackTrace(System.out);
				}

		}

		// d�bogage
		System.out.println("Word: " + this.game.getWordToComplete().getCurrentSubword());
		System.out.println(this.game.getDeck());
	}

}
