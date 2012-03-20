package  examples.lg.strategy;

import java.util.Enumeration;

import examples.lg.model.Deck;
import examples.lg.model.Letter;
import examples.lg.model.LetterGame;
import examples.lg.model.Word;

/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class DropHeuristic {

	/* choue si toutes les lettres du deck sont candidates au mot mais
	 * non utilisable pour la needeedLetter
	 * */
	public static Letter whichToDrop(final LetterGame game) {
		final Deck deck = game.getDeck();
		final Word word = game.getWordToComplete();
		Letter ret = null;
		for (final Enumeration e = deck.getLetters().elements(); e.hasMoreElements();) {
			final Letter l = (Letter) e.nextElement();
			if (!word.getNeededLetters().contains(l)) {
				ret = l;
			}
		}
		return ret;
	}

}
