package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author
 * @version 1.0
 */

public class ACLStreamAbout extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 2967983016774246567L;

	public ACLStreamAbout(final String tx, final String rx,
			   final String msg,
			   final String irt, final String rw) {

	super(tx,rx,msg,irt,rw);
	this.setPerformative("stream-about");

	}
}
