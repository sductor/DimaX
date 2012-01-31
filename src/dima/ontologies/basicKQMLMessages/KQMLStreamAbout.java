package dima.ontologies.basicKQMLMessages;
import dima.kernel.communicatingAgent.OntologyBasedAgent;
/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author
 * @version 1.0
 */

public class KQMLStreamAbout extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = -2987529515210135710L;
	public KQMLStreamAbout(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {

		super(tx,rx,msg,irt,rw);
		this.setPerformative("stream-about");

	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processStreamAbout(this);
	}
}
