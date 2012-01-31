package dima.ontologies.basicKQMLMessages;
import dima.kernel.communicatingAgent.OntologyBasedAgent;
/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author  Samir TOUAF
 * @version 1.0
 */

public class KQMLBrokerAll extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = 2760825893933392157L;
	public KQMLBrokerAll(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("broker-all");
	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processBrokerAll(this);
	}
}
