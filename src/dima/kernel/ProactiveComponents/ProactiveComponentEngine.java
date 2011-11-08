package dima.kernel.ProactiveComponents;

/**
 * Insert the type's description here.
 * Creation date: (02/03/00 19:01:12)
 * @author: Gerard Rozsavolgyi
 */
import dima.basicinterfaces.ProactiveComponentInterface;
import dima.support.GimaObject;

public  class ProactiveComponentEngine extends GimaObject
implements Runnable {
	/**
	 *
	 */
	private static final long serialVersionUID = -7065332430531570543L;
	protected ProactiveComponentInterface proactivity;
	public volatile Thread thread;
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 16:15:58)
	 */
	public ProactiveComponentEngine() {	super();}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 16:15:58)
	 */
	public ProactiveComponentEngine(final ProactiveComponentInterface p)
	{
		super();
		this.proactivity = p;
		this.initialize();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 15:28:38)
	 */

	public Thread getThread() {
		return this.thread;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 15:28:38)
	 */

	public void initialize() {
		this.thread = new Thread(this);
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 15:28:38)
	 */

	@Override
	public void run() {
		this.proactivity.startUp();
//		this.thread.interrupt();
		System.out.println("yo");
		this.proactivity=null;
		this.thread = null;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 15:28:38)
	 */



	public void startUp() {

		this.thread.start();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (19/07/00 15:28:38)
	 */

}