package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author  Samir TOUAF
 * @version 1.0
 */

public class ACLSorry extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 6290724693515734788L;

	public ACLSorry(final String tx, final String rx,
		    final String msg,
		    final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("sorry");
	}
}
