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

public class KQMLEos extends KQML {

    /**
	 *
	 */
	private static final long serialVersionUID = 387998450490246419L;
	public KQMLEos(final String tx, final String rx,
		   final String msg,
		   final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("eos");

    }
    @Override
	public void processKQML(final OntologyBasedAgent a)
    {
    a.processEos(this);
    }
}
