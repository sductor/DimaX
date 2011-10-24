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

public class KQMLAskAll extends KQML {

  /**
	 *
	 */
	private static final long serialVersionUID = 8609296128599450993L;

public KQMLAskAll (final String tx,
		       final String msg,final String language,
		       final String rw) {
	super(tx,null,msg,null,rw);
        this.setLanguage(language);
      this.setPerformative("ask-all");
  }

   @Override
public void processKQML(final OntologyBasedAgent a)
    {
      a.processAskAll(this);
    }
}
