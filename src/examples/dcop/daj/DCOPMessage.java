// ----------------------------------------------------------------------------
// $Id: Message.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// objects exchanged between processes
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop.daj;

import dima.basicagentcomponents.AgentName;
import dima.basiccommunicationcomponents.AbstractMessage;

public class DCOPMessage extends dima.basiccommunicationcomponents.Message{
	
	public int getSenderAsInt(){
		return new Integer(super.getSender().toString());
	}
	
	public void setSender(Integer sender) {
		super.setSender(new AgentName(sender.toString()));
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
}
