package dima.ontologies.basicKQMLMessages;
import dima.kernel.communicatingAgent.OntologyBasedAgent;

/**
 * Titre :
 * Description :
 * Copyright :     Copyright (c) 2001
 * Soci�t� :       LIP6
 * @author         Samir TOUAF
 * @version        1.0
 */

public class KQMLSubscribe extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = -951952340661253263L;
	public KQMLSubscribe(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("subscribe");
	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processSubscribe(this);
	}
}
