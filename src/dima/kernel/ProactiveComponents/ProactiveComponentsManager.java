package dima.kernel.ProactiveComponents;

/**
 * This class provides the ability to deal with a Vector
 * of ProactiveComponents.
 * Creation date: (25/01/00 15:41:18)
 * @author: G�rard Rozsavolgyi
 */
import java.util.Vector;

public class ProactiveComponentsManager extends dima.support.GimaObject {
	/**
	 *
	 */
	private static final long serialVersionUID = -6674968966792437760L;
	protected Vector proactiveObjects;
	/**
	 * ProactiveObjectManager constructor comment.
	 */
	public ProactiveComponentsManager() {
		super();
	}
	/**
	 * ProactiveObjectManager constructor with a
	 * Vector composed of ProactiveObjects.
	 */
	public ProactiveComponentsManager(final Vector pao) {
		super();
		this.proactiveObjects = pao;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/02/02 16:22:14)
	 * @return java.util.Vector
	 */
	public java.util.Vector getProactiveObjects() {
		return this.proactiveObjects;
	}
	/**
	 * Tell all the known proactive components to stop
	 * Creation date: (09/02/2000 00:12:24)
	 */
	public void killAll() {
		final int taille = this.getProactiveObjects().size();
		for (int i = 0; i < taille; i++) {
			final ProactiveComponent pao = (ProactiveComponent) this.proactiveObjects.elementAt(i);
			pao.setAlive(false);
		}
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (07/02/02 16:22:14)
	 * @param newProactiveObjects java.util.Vector
	 */
	public void setProactiveObjects(final java.util.Vector newProactiveObjects) {
		this.proactiveObjects = newProactiveObjects;
	}
	/**
	 * Execute
	 * all known ProactiveObjects.
	 *
	 */

	public void startAll() {
		final Vector pv = this.proactiveObjects;
		for (int i=0; i<pv.size();i++) {
			final ProactiveComponent pao =(ProactiveComponent)pv.elementAt(i);
			final ProactiveComponentEngine paoEngine = new ProactiveComponentEngine(pao);
			paoEngine.startUp();
		}
	}
	/**
	 * Execute
	 * all known ProactiveObjects.
	 *
	 */

	public void startAllSimula() {
		final Vector pv = this.proactiveObjects;
		/*for (int i=0; i<pv.size();i++) {
		ProactiveComponent pao =(ProactiveComponent)pv.elementAt(i);
		ThreadedProactiveComponentEngine paoEngine = new ThreadedProactiveComponentEngine(pao);
		paoEngine.startUp();
	 activer un pas de chaque agent (pao.step()) tanque la simulation est active
	en offrant la possibilit� de controler la simulation (stopper, red�marrer
	} */
	}
}
