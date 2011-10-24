package dima.ontologies.basicFIPAACLMessages;

import java.util.Date;

import dima.basiccommunicationcomponents.Message;





/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2001
 * Soci�t� :      LIP6
 * @author        Samir TOUAF
 * @version       1.0
 */

public class FIPAACLMessage extends Message {
	/**
	 *
	 */
	private static final long serialVersionUID = -3266603793136144793L;
	String performative = null;
	String inReplyTo = null;
	String replyWith = null;
	String language = null;
	String ontology = null;
	String protocol = null;
	public java.lang.String conversationId;
	public Date replyBy;
public FIPAACLMessage ()
{
	this.setType("FIPAACLMessage");
}
public FIPAACLMessage (final String convId)
{
	this.setConversationId(convId);
	this.setType("FIPAACLMessage");
}
	public FIPAACLMessage (final String  msgContent, final Object [] paras)

	{super(msgContent, paras);
	}

	public FIPAACLMessage (final String  msgContent, final Object [] paras, final String rec )

	{super(msgContent, paras);
	this.setReceiver(rec);
	this.setType("FIPAACLMessage");
	}
	public FIPAACLMessage (final String newSender, final String newReceiver, final String newMessage) {
	this(newSender, newReceiver, newMessage, null,null);
	this.setType("FIPAACLMessage");
	}
	public FIPAACLMessage (final String tx, final String rx,
		 final String msg,
		 final String irt, final String rw) {
	this.setSender(tx);
	this.setReceiver(rx);
	this.setContent(msg);
	this.setInReplyTo(irt);
	this.setReplyWith(rw);
	this.setType("FIPAACLMessage");
	}
	public FIPAACLMessage (final String myContent, final String tx, final String rx,
		 final String msg,
		 final String irt, final String rw, final String t) {
	this.setContent(myContent);
	this.setSender(tx);
	this.setReceiver(rx);
	this.setContent(msg);
	this.setInReplyTo(irt);
	this.setReplyWith(rw);
	this.setType("FIPAACLMessage");
	}
/**
 * Insert the method's description here.
 * Creation date: (03/04/2003 14:03:40)
 * @return java.lang.String
 */
public java.lang.String getConversationId() {
	return this.conversationId;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @return java.lang.String
 */
public java.lang.String getInReplyTo() {
	return this.inReplyTo;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @return java.lang.String
 */
public java.lang.String getLanguage() {
	return this.language;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @return java.lang.String
 */
public java.lang.String getOntology() {
	return this.ontology;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @return java.lang.String
 */
public java.lang.String getPerformative() {
	return this.performative;
}
/**
 * Insert the method's description here.
 * Creation date: (09/04/2003 17:53:05)
 * @return java.lang.String
 */
public java.lang.String getProtocol() {
	return this.protocol;
}
/**
 * Insert the method's description here.
 * Creation date: (07/04/2003 11:01:59)
 * @return int
 */
public Date getReplyBy() {
	return this.replyBy;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @return java.lang.String
 */
public java.lang.String getReplyWith() {
	return this.replyWith;
}
/**
 * Insert the method's description here.
 * Creation date: (03/04/2003 14:03:40)
 * @param newConversationId java.lang.String
 */
public void setConversationId(final java.lang.String newConversationId) {
	this.conversationId = newConversationId;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @param newInReplyTo java.lang.String
 */
public void setInReplyTo(final java.lang.String newInReplyTo) {
	this.inReplyTo = newInReplyTo;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @param newLanguage java.lang.String
 */
public void setLanguage(final java.lang.String newLanguage) {
	this.language = newLanguage;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @param newOntology java.lang.String
 */
public void setOntology(final java.lang.String newOntology) {
	this.ontology = newOntology;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @param newPerformative java.lang.String
 */
public void setPerformative(final java.lang.String newPerformative) {
	this.performative = newPerformative;
}
/**
 * Insert the method's description here.
 * Creation date: (09/04/2003 17:53:05)
 * @param newProtocol java.lang.String
 */
public void setProtocol(final String newProtocol)
{
	this.protocol = newProtocol;
}
/**
 * Insert the method's description here.
 * Creation date: (07/04/2003 11:01:59)
 * @param newReplyBy int
 */
public void setReplyBy(final Date newReplyBy) {
	this.replyBy = newReplyBy;
}
/**
 * Insert the method's description here.
 * Creation date: (23/12/2002 09:23:02)
 * @param newReplyWith java.lang.String
 */
public void setReplyWith(final java.lang.String newReplyWith) {
	this.replyWith = newReplyWith;
}
	@Override
	public String toString() {
	return "(" + this.getPerformative() +
	    " :sender " + this.getSender() +
	    " :receiver " + this.getReceiver()   + ")";
	  }

/**
 * Insert the method's description here.
 * Creation date: (26/05/2003 12:37:28)
 * @return boolean
 */
public boolean isCallForParticipationMessage()
{
	if( this.getPerformative().equals("CallForProposal") && this.getProtocol().equals("FIPAContractNetProtocol") )
		return true;

	if( this.getPerformative().equals("Propose") && this.getProtocol().equals("BargainingProtocol") )
		return true;

	return false;
}

public boolean isAccept ()
{
	return false;
}

public boolean isAcceptProposal ()
{
	return false;
}

public boolean isAdvertise ()
{
	return false;
}

public boolean isAsk ()
{
	return false;
}

public boolean isBroke()
{
	return false;
}

public boolean isCallForProposal()
{
	return false;
}

public boolean isCFP()
{
	return false;
}

public boolean isPropose()
{
	return false;
}

public boolean isRefuse()
{
	return false;
}

public boolean isRefuseProposal()
{
	return false;
}

public boolean isReject()
{
	return false;
}

public boolean isRejectProposal()
{
	return false;
}

public boolean isTell()
{
	return false;
}
}
