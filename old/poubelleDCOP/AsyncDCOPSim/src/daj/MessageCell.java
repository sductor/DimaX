// ----------------------------------------------------------------------------
// $Id: MessageCell.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// cell for message list
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package daj;

public class MessageCell {

	private Message message;
	private MessageCell next;

	public MessageCell(Message obj) {
		message = obj;
		next = null;
	}

	public MessageCell(Message obj, MessageCell cell) {
		message = obj;
		next = cell;
	}

	public Message getMessage() {
		return message;
	}

	public MessageCell getNext() {
		return next;
	}

	public void setMessage(Message msg) {
		message = msg;
	}

	public void setNext(MessageCell cell) {
		next = cell;
	}
}
