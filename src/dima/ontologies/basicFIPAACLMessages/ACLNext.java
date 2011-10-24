package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author
 * @version 1.0
 */

public class ACLNext extends FIPAACLMessage {

  /**
	 *
	 */
	private static final long serialVersionUID = -2589668165325696290L;

public ACLNext(final String tx, final String rx,
		  final String msg,
		  final String irt, final String rw) {
	  super(tx,rx,msg,irt,rw);
	  this.setPerformative("next");
  }
}
