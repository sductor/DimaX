package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author
 * @version 1.0
 */

public class ACLUntell extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 789706147708180933L;

	public ACLUntell(final String tx, final String rx,
		      final String msg,
		      final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("untell");

	}
}
