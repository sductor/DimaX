// ----------------------------------------------------------------------------
// $Id: MessageQueue.java,v 1.2 1997/10/22 20:52:10 ws Exp $
// queue of of messages (unsynchronized)
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package examples.dcop.daj;

public class MessageQueue {

	private MessageCell head;
	private MessageCell tail;
	private int size;

	// --------------------------------------------------------------------------
	// create empty queue
	// --------------------------------------------------------------------------
	protected MessageQueue() {
		head = null;
		tail = null;
		size = 0;
	}

	// --------------------------------------------------------------------------
	// returns true iff queue is empty
	// --------------------------------------------------------------------------
	public boolean isEmpty() {
		return head == null;
	}

	// --------------------------------------------------------------------------
	// add element to end of queue
	// --------------------------------------------------------------------------
	public void enqueue(DcopMessage obj) {
		if (head == null) {
			head = new MessageCell(obj);
			tail = head;
		}
		else {
			MessageCell cell = new MessageCell(obj);
			tail.setNext(cell);
			tail = cell;
		}
		size++;
	}

	// --------------------------------------------------------------------------
	// remove element from front of queue
	// --------------------------------------------------------------------------
	public DcopMessage dequeue() {
		Assertion.test(head != null, "queue is empty");
		MessageCell cell = head;
		head = cell.getNext();
		if (head == null) tail = null;
		size--;
		return cell.getMessage();
	}

	// --------------------------------------------------------------------------
	// return head message cell
	// --------------------------------------------------------------------------
	public MessageCell getCell() {
		return head;
	}

	// --------------------------------------------------------------------------
	// return vector of messages in queue
	// --------------------------------------------------------------------------
	public DcopMessage[] getMessages() {
		DcopMessage messages[] = new DcopMessage[size];
		MessageCell cell = head;
		for (int i = 0; i < size; i++) {
			Assertion.test(cell != null, "queue is too short");
			messages[i] = cell.getMessage();
			cell = cell.getNext();
		}
		Assertion.test(cell == null, "queue is too long");
		return messages;
	}
	
	public int getSize() {
		return size;
	}
}
