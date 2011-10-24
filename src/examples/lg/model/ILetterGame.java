package  examples.lg.model;

/**
 * @author Arnaud
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public interface ILetterGame
{

	// simple action
	public void dropLetter(Letter l) throws GameException;
	// perception
	public Deck getDeck();
	// perception
	public Word getWordToComplete();
	// perception
	public boolean hasLetter(Letter l);
	// perception
	public Letter neededLetter();
	// simple action
	public void pickLetter() throws GameException;
	// simple action
	public void useLetter(Letter l) throws GameException;
}
