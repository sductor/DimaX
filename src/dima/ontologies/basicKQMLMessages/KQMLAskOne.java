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

public class KQMLAskOne extends KQML {

    /**
	 *
	 */
	private static final long serialVersionUID = 1137313935830734782L;

	public KQMLAskOne (final String tx,
		       final String msg,final String language,
		       final String rw) {
	super(tx,null,msg,null,rw);
        this.setLanguage(language);
	this.setPerformative("ask-one");
    }

    @Override
	public void processKQML(final OntologyBasedAgent a)
    {
      a.processAskOne(this);
    }
}
