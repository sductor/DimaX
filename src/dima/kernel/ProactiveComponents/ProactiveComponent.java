package dima.kernel.ProactiveComponents;

/**
 * Basis for all kind of Proactive Object.
 * A Proactive component has a goal to reach and is active until it has completed it.
 * A Proactive Component is launched with the message startup().
 * There are 2 methods wich must be defined by a concrete subclass :
 * 	- step() wich performs the next step of activity of the component
 *  - isActive() which test wether or not the component must remain active
 * A number of other methods can be overriden, especially :
 * proactivityInitialize() and proactivityTerminate()which represents initialization
 * and termination stages of a ProactiveComponent.
 * Creation date: (21/01/00 16:23:20)
 * @author: G�rard Rozsavolgyi
 */

import java.io.Serializable;

import dima.basicinterfaces.ProactiveComponentInterface;
import dima.support.GimaObject;




public abstract class ProactiveComponent extends GimaObject implements Serializable, ProactiveComponentInterface	{
	/**
	 *
	 */
	private static final long serialVersionUID = 1422700145416351091L;
	private boolean alive = true;

	/**
	 *  default constructor.
	 */
	public ProactiveComponent() {
		super();
		this.setAlive(true);
	}
	public void activate() {
		final ProactiveComponentEngine engine = new ProactiveComponentEngine (this);
		engine.startUp();}
	/**
	 * Describe the basic cycle of the agent. Itcan be readMailBox();
	 * Creation date: (07/05/00 09:28:47)
	 */
	public void wwait()
	{
		try {Thread.sleep (200, 10);}
		catch (final InterruptedException e) {}

	}

	/**
	 * Describe the basic cycle of the agent. Itcan be readMailBox();
	 * Wit n milliseconds
	 */
	public void wwait(final long n)
	{
		try {Thread.sleep (n, 10);}
		catch (final InterruptedException e) {}

	}
	/**
	 * Tests wheter a proactive object is active or no ie whether the ProactiveComponent.
	 */
	@Override
	public abstract boolean isActive();
	/**
Tests wheter a proactive object has reached it goal or
	 */
	@Override
	public boolean isAlive() {
		return this.alive;
	}


	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:50:27)
	 * @return boolean
	 */
	@Override
	public void postActivity() {}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:50:27)
	 * @return boolean
	 */
	@Override
	public void preActivity() {}
	/**
	 * Initialization of a ProactiveComponent.
	 *
	 */


	@Override
	public  void proactivityInitialize() {}
	/**
	 * Tant que  l'objet est actif (n'a pasatteint son but) et
	 * qu'on ne l'a pas tu�, on boucle...
	 *
	 */
	public void proactivityLoop() {

		while(this.isAlive()) {
			if (this.isActive()){
				this.preActivity();
				this.step();
				this.postActivity();
			} else {
				this.tryToResumeActivity();
			}
		}
	}
	/**
	 * This is the method containing the termination condition of a ProactiveComponent
	 *
	 */

	@Override
	public  void proactivityTerminate(){}
	/**
	 * To make live or die a ProactiveComponent
	 * Creation date: (08/02/2000 23:50:27)
	 * @param newAlive boolean
	 */
	public void setAlive(final boolean newAlive) {
		this.alive = newAlive;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (08/02/2000 23:50:27)
	 * @return boolean
	 */
	@Override
	public void startUp() {
		this.proactivityInitialize();

		this.proactivityLoop();

		this.proactivityTerminate();
	}
	/**
	 * This is the main method for a proactive component :
	 * what to do while in activity.
	 *
	 */

	@Override
	public abstract void step();



	/**
	 * This method attempt to resume activity when the agent is not active
	 */
	@Override
	public void tryToResumeActivity(){}

	/**
	 * Returns a String that represents the value of this object.
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or supplement this.
		return super.toString();
	}
}
