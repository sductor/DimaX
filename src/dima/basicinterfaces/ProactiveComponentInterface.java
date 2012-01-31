package dima.basicinterfaces;

public interface ProactiveComponentInterface extends ActiveComponentInterface{

	//
	// Accessors
	//


	//	/**
	//	 * Tests wheter a proactive object is active or no ie whether the
	//	 * ProactiveComponent.
	//	 */
	//	public void  setActive(boolean active);

	/**
	 * Tests wheter a proactive object has reached it goal or
	 */
	public boolean isAlive();

	//	/**
	//	 * Autodestruction of a proactive object
	//	 */
	//	public void kill();

	//
	// Methods
	//

	/**
	 * Initialization of a ProactiveComponent.
	 *
	 */
	public void proactivityInitialize();



	/**
	 * Startup execute the methods of the proactivity loop
	 */

	public void startUp();

	/*
	 * Beginning ProactivityLoop
	 ******************************/

	/**
	 * Insert the method's description here. Creation date: (08/02/2000
	 * 23:50:27)
	 *
	 * @return boolean
	 */
	public void preActivity();

	/**
	 * This is the main method for a proactive component : what to do while in
	 * activity.
	 *
	 */
	public void step();

	/**
	 * Insert the method's description here. Creation date: (08/02/2000
	 * 23:50:27)
	 *
	 * @return boolean
	 */
	public void postActivity();

	/*
	 * Ending ProactivityLoop
	 *************************/

	/**
	 * This method attempt to resume activity when the agent is not active
	 */
	public void tryToResumeActivity();

	/**
	 * This is the method containing the termination condition of a
	 * ProactiveComponent
	 *
	 */
	public void proactivityTerminate();
}
