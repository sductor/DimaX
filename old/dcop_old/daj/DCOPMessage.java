// ----------------------------------------------------------------------------
// $Id: Message.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// objects exchanged between processes
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop_old.daj;

import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.AbstractMessage;
import dima.basicinterfaces.AbstractMessageInterface;
import examples.dcop_old.algo.topt.LocalConstraintMsg;

public abstract class DCOPMessage extends dima.basiccommunicationcomponents.Message{

	public NodeIdentifier getSender(){
		return (NodeIdentifier) super.getSender();
	}
	

	// --------------------------------------------------------------------------
	// return string denoting message content
	// --------------------------------------------------------------------------
	public String getText() {
		return ("(no information)");
	}

	public int getSize() {
		return 0;
	}
	
	public String description() {
		return getText();
	}


	public DCOPMessage() {
		super();
		setType("dcop");
	}


	public DCOPMessage forward(){
		return this;
	}
}
