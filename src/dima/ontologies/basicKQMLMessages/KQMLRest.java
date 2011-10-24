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

public class KQMLRest extends KQML {

    /**
	 *
	 */
	private static final long serialVersionUID = -1434786894588023719L;
	public KQMLRest(final String tx, final String rx,
		    final String msg,
		    final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("rest");
    }
    @Override
	public void processKQML(final OntologyBasedAgent a)
    {
    a.processRest(this);
    }
}
