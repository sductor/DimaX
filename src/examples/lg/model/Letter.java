package  examples.lg.model;

/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
import dima.kernel.INAF.InteractionDomain.AbstractService;

        public class Letter extends AbstractService {
	private char letter;


	public Letter(final char c) throws Exception {
		if (c>= 'A' && c <= 'Z')
			this.letter = c;
		else
			throw new Exception("Must be a letter");
	}
/**
 * Insert the method's description here.
 * Creation date: (04/04/2003 18:57:43)
 * @return boolean
 */
@Override
public boolean equals(final AbstractService s)
{
	return this.toString().equals(s.toString());
}
/**
 * Insert the method's description here.
 * Creation date: (04/04/2003 18:57:43)
 * @return boolean
 */
@Override
public boolean equals(final Object s)
{
	return this.toString().equals(((Letter)s).toString());
}
	/**
	 * Returns the letter.
	 * @return char
	 */
	public char getLetter() {
		return this.letter;
	}
/**
 * Insert the method's description here.
 * Creation date: (27/06/2003 11:04:37)
 * @return java.lang.String
 */
@Override
public String toString()
{
	return String.valueOf(this.getLetter());
}
}
