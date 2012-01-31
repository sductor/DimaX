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

public class KQMLTell extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = -8062510894655502854L;
	public KQMLTell(final String tx,
			final String msg,final String language,
			final String rw) {
		super(tx,null,msg,null,rw);
		this.setLanguage(language);
		this.setPerformative("tell");

	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processTell(this);
	}
}
