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

public class KQMLAskAbout extends KQML {
	/**
	 *
	 */
	private static final long serialVersionUID = -4907467282272335139L;
	public KQMLAskAbout (final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {

		super(tx,rx,msg,irt,rw);
		this.setPerformative("ask-about");
	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processAskAbout(this);
	}
}
