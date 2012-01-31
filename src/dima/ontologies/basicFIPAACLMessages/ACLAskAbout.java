package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author
 * @version 1.0
 */

public class ACLAskAbout extends FIPAACLMessage {
	/**
	 *
	 */
	private static final long serialVersionUID = 5541514307932863319L;

	public ACLAskAbout (final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {

		super(tx,rx,msg,irt,rw);
		this.setPerformative("ask-about");
	}

	@Override
	public boolean isAsk ()
	{
		return true;
	}
}
