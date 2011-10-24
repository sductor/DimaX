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

public class KQMLNext extends KQML {

  /**
	 *
	 */
	private static final long serialVersionUID = -6575131659494766740L;
public KQMLNext(final String tx, final String rx,
		  final String msg,
		  final String irt, final String rw) {
      super(tx,rx,msg,irt,rw);
      this.setPerformative("next");
  }
  @Override
public void processKQML(final OntologyBasedAgent a)
    {
    a.processNext(this);
    }
}
