package dima.ontologies.basicFIPAACLMessages;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :
 * @author  Samir TOUAF
 * @version 1.0
 */

public class ACLBrokerAll extends FIPAACLMessage {

	/**
	 *
	 */
	private static final long serialVersionUID = 2604024783327772316L;

	public ACLBrokerAll(final String tx, final String rx,
			 final String msg,
			 final String irt, final String rw) {
	super(tx,rx,msg,irt,rw);
	this.setPerformative("broker-all");
	}

@Override
public boolean isBroke()
{
	return true;
}
}
