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

public class KQMLSorry extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = -6151045777155568734L;
	public KQMLSorry(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("sorry");
	}
	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processSorry(this);
	}
}
