// ----------------------------------------------------------------------------
// $Id: MessageCell.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// cell for message list
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package vieux.dcopAmeliorer.daj;

public class MessageCell {

	private DcopMessage message;
	private MessageCell next;

	public MessageCell(final DcopMessage obj) {
		this.message = obj;
		this.next = null;
	}

	public MessageCell(final DcopMessage obj, final MessageCell cell) {
		this.message = obj;
		this.next = cell;
	}

	public DcopMessage getMessage() {
		return this.message;
	}

	public MessageCell getNext() {
		return this.next;
	}

	public void setMessage(final DcopMessage msg) {
		this.message = msg;
	}

	public void setNext(final MessageCell cell) {
		this.next = cell;
	}
}
