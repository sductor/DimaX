package darx;

/**
 * This class exists only to be overridden in DIMAX; it provides a channel for
 * distributed exceptions towards a central log server.
 **/
public class Logger {

	private static final long serialVersionUID = 1L;

	/**
	 * Displays info from DIMAX.
	 *
	 * @param text
	 */
	public static void fromDARX(final String text) {
		System.out.println("DARX says >>>> " + text);
	}

	/**
	 * Displays a message related to the exception.
	 *
	 * @param classeAppelante
	 * @param text
	 * @param e
	 */
	public static void exception(final Object classeAppelante,
			final String text, final Throwable e) {
		System.out.println("\nLogging exception " + text);
		e.printStackTrace();
	}

	/**
	 * Displays a message related to the exception.
	 *
	 * @param caller
	 * @param text
	 */
	public static void exception(final Object caller, final String text) {
		System.out.println("\nLogging exception " + text);
	}

}