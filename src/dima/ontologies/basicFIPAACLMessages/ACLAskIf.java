package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author   Samir TOUAF
 * @version 1.0
 */

public class ACLAskIf extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = -2099303892849972155L;

	public ACLAskIf(final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("ask-if");
	}

	@Override
	public boolean isAsk ()
	{
		return true;
	}
}
