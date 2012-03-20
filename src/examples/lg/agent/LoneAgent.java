package  examples.lg.agent;


/**
 * @author thiefaine
 *
 * To change this generated comment edit the template variable "typecomment":
 * Window>Preferences>Java>Templates.
 * To enable and disable the creation of type comments go to
 * Window>Preferences>Java>Code Generation.
 */
public class LoneAgent extends LoneDomain {



	/**
	 *
	 */
	private static final long serialVersionUID = -3659580667946276209L;

	/**
	 * @see Gdima.proactive.component.ProactiveComponent#competenceIsActive()
	 */
	@Override
	public boolean competenceIsActive() {

		return !this.isComplete(); // complete              (lecture)
	}

	@Override
	public void step() {

		// perception plus complexe + dcision simple
		if (this.hasNeededLetter()) {
			this.useLetter(); // useLetter()
		}
		else {
			this.dropLetter(); // dropLetter()
		}

		this.display();

	}


}
