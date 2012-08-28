// ----------------------------------------------------------------------------
// $Id: MessageQueue.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// queue of of messages (unsynchronized)
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package vieux.dcopAmeliorer.daj;

public class MessageQueue {

	private MessageCell head;
	private MessageCell tail;
	private int size;

	// --------------------------------------------------------------------------
	// create empty queue
	// --------------------------------------------------------------------------
	protected MessageQueue() {
		this.head = null;
		this.tail = null;
		this.size = 0;
	}

	// --------------------------------------------------------------------------
	// returns true iff queue is empty
	// --------------------------------------------------------------------------
	public boolean isEmpty() {
		return this.head == null;
	}

	// --------------------------------------------------------------------------
	// add element to end of queue
	// --------------------------------------------------------------------------
	public void enqueue(final DcopMessage obj) {
		if (this.head == null) {
			this.head = new MessageCell(obj);
			this.tail = this.head;
		}
		else {
			final MessageCell cell = new MessageCell(obj);
			this.tail.setNext(cell);
			this.tail = cell;
		}
		this.size++;
	}

	// --------------------------------------------------------------------------
	// remove element from front of queue
	// --------------------------------------------------------------------------
	public DcopMessage dequeue() {
		Assertion.test(this.head != null, "queue is empty");
		final MessageCell cell = this.head;
		this.head = cell.getNext();
		if (this.head == null) {
			this.tail = null;
		}
		this.size--;
		return cell.getMessage();
	}

	// --------------------------------------------------------------------------
	// return head message cell
	// --------------------------------------------------------------------------
	public MessageCell getCell() {
		return this.head;
	}

	// --------------------------------------------------------------------------
	// return vector of messages in queue
	// --------------------------------------------------------------------------
	public DcopMessage[] getMessages() {
		final DcopMessage messages[] = new DcopMessage[this.size];
		MessageCell cell = this.head;
		for (int i = 0; i < this.size; i++) {
			Assertion.test(cell != null, "queue is too short");
			messages[i] = cell.getMessage();
			cell = cell.getNext();
		}
		Assertion.test(cell == null, "queue is too long");
		return messages;
	}

	public int getSize() {
		return this.size;
	}
}
