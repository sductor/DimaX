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

public class KQMLGenerator extends KQML {

    /**
	 *
	 */
	private static final long serialVersionUID = 8745096862383761523L;
	public KQMLGenerator(final String tx, final String rx,
			 final String msg,
			 final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("generator");
    }
    @Override
	public void processKQML(final OntologyBasedAgent a)
    {
    a.processGenerator(this);
    }

}
