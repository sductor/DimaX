package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author  Samir TOUAF
 * @version 1.0
 */

public class ACLAskOne extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 188749524883912200L;

	public ACLAskOne (final String tx, final String rx,
		       final String msg,
		       final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("ask-one");
	}

@Override
public boolean isAsk ()
{
	return true;
}
}
