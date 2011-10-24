package  examples.lg.model;

/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class Supply {


	public Supply() {}

	public static Letter pick() {
		final double f = Math.random();

		final int i = (int) (f * 26) + 'A';
		final char c = (char)  i;
		try {
			return new Letter(c);
		} catch (final Exception e) {
			e.printStackTrace(System.out);
			return null;
		}


	}


	public static void main(final String[] args) {
		final Supply sup = new Supply();
		for (int i=0; i<100; i++)
			System.out.println(Supply.pick().getLetter());
	}

}
