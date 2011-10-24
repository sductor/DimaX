package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :      LIP6
 * @author        Samir TOUAF
 * @version       1.0
 */

public class ACLStandby extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = -3438473164407819215L;

	public ACLStandby(final String tx, final String rx,
		       final String msg,
		       final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("standby");

	}
}
