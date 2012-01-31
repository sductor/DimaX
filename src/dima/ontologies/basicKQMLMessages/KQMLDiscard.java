package dima.ontologies.basicKQMLMessages;
import dima.kernel.communicatingAgent.OntologyBasedAgent;
/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :      LIP6
 * @author        Samir TOUAF
 * @version       1.0
 */

public class KQMLDiscard extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = 2554903548862035509L;
	public KQMLDiscard(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("discard");
	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processDiscard(this);
	}
}
