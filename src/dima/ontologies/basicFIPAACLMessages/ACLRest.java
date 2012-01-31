package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author
 * @version 1.0
 */

public class ACLRest extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 3100721931137795195L;

	public ACLRest(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("rest");
	}
}
