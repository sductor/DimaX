package dima.ontologies.basicKQMLMessages;

import dima.basiccommunicationcomponents.Message;
import dima.kernel.communicatingAgent.OntologyBasedAgent;

/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :      LIP6
 * @author        Samir TOUAF
 * @version       1.0
 */

public abstract class KQML extends Message {
	/**
	 *
	 */
	private static final long serialVersionUID = -5065262085890989010L;
	String performative = null;
	String inReplyTo = null;
	String replyWith = null;
	String language = null;
	String ontology = null;

	public KQML () {}
	public KQML (final String newSender, final String newReceiver, final String newMessage) {
		this(newSender, newReceiver, newMessage, null,null);
	}
	public KQML (final String tx, final String rx,
			final String msg,
			final String irt, final String rw) {
		this.setSender(tx);
		this.setReceiver(rx);
		this.setContent(msg);
		this.setInReplyTo(irt);
		this.setReplyWith(rw);
	}
	public String getInReplyTo() { return this.inReplyTo; }
	public String getLanguage() { return this.language; }
	public String getOntology() { return this.ontology; }
	public String getPerformative() {return this.performative;}
	public String getReplyWith() { return this.replyWith; }
	public void setInReplyTo(final String irt) { this.inReplyTo = irt; }
	public void setLanguage(final String l) { this.language = l; }
	public void setOntology(final String o) { this.ontology = o; }
	public void setPerformative(final String p) { this.performative = p; }
	public void setReplyWith(final String rw) { this.replyWith = rw; }
	@Override
	public String toString() {
		return "(" + this.getPerformative() +
				" :sender " + this.getSender() +
				" :receiver " + this.getReceiver()   + ")";
	}
	public abstract void processKQML(OntologyBasedAgent a);
}
