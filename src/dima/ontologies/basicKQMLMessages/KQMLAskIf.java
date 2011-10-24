package dima.ontologies.basicKQMLMessages;
import dima.kernel.communicatingAgent.OntologyBasedAgent;
/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author   Samir TOUAF
 * @version 1.0
 */

public class KQMLAskIf extends KQML {

    /**
	 *
	 */
	private static final long serialVersionUID = 3848164711605645669L;
	public KQMLAskIf(final String tx, final String rx,
			 final String msg,
			 final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("ask-if");
    }
    @Override
	public void processKQML(final OntologyBasedAgent a)
    {
    a.processAskIf(this);
    }
}
