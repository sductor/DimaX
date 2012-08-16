// ----------------------------------------------------------------------------
// $Id: ChannelSet.java,v 1.5 1997/11/03 10:23:45 schreine Exp $
// collections of channels
//
// (c) 1997, Wolfgang Schreiner <Wolfgang.Schreiner@risc.uni-linz.ac.at>
// http://www.risc.uni-linz.ac.at/software/daj
// ----------------------------------------------------------------------------
package frameworks.dcop.daj;

public class ChannelSet {

	// channel set, size of set, number of channels in set
	private int setSize = 100;
	private Channel[] set = new Channel[setSize];
	private int setNum = 0;

	// --------------------------------------------------------------------------
	// add `channel` to set
	// if `receiver`, receiver nodes must be the same, otherwise sender nodes
	// --------------------------------------------------------------------------
	public void addChannel(Channel channel, boolean receiver) {
		if (setNum == setSize) {
			Channel oldSet[] = set;
			setSize *= 2;
			set = new Channel[setSize];
			for (int i = 0; i < setNum; i++)
				set[i] = oldSet[i];
		}
		if (setNum > 0) {
			if (receiver) Assertion.test(set[0].getReceiver() == channel.getReceiver(),
					"channel has different receiver node");
			else Assertion.test(set[0].getSender() == channel.getSender(),
					"channel has different sender node");
		}
		set[setNum] = channel;
		setNum = setNum + 1;
	}

	// --------------------------------------------------------------------------
	// return number of channels in set
	// --------------------------------------------------------------------------
	public int getSize() {
		return setNum;
	}

	// --------------------------------------------------------------------------
	// return channel numbered `i` in set
	// --------------------------------------------------------------------------
	protected Channel channel(int i) {
		if (i < 0) {
			Assertion.fail("channel index is negative");
		}
		if (i >= setNum) Assertion.fail("channel index is too large");
		return set[i];
	}

	// --------------------------------------------------------------------------
	// broadcast `message` to all channels in set
	// --------------------------------------------------------------------------
	public void send(Message message) {
		for (int i = 0; i < setNum; i++) {
			set[i].send(message);
		}
	}
	
	public void broadcast(Message message) {
		for (int i = 0; i < setNum; i++) {
			set[i].send(message);
		}
//		set[0].getSender().getNetwork().getScheduler().schedule();
	}

	// --------------------------------------------------------------------------
	// return index of non-empty channel in set
	// --------------------------------------------------------------------------
	public int select() {
		Assertion.test(setNum != 0, "channel set is empty");
		Node receiver;
		synchronized (this) {
			for (int i = 0; i < setNum; i++) {
				if (!set[i].registerEmpty(this)) {
					for (int j = 0; j < i; j++)
						set[j].unregister();
					return i;
				}
			}
			for (int i = 0; i < setNum; i++) {
				set[i].receiveBlock();
			}
			receiver = set[0].getReceiver();
			Scheduler scheduler = receiver.getNetwork().getScheduler();
			int index = scheduler.sleep();
			try {
				wait();
			}
			catch (InterruptedException e) {
				Assertion.fail("InterruptedException");
			}
			for (int i = 0; i < setNum; i++) {
				set[i].unregister();
				set[i].receiveAwake();
			}
			scheduler.awake(index);
		}
		//
		// there is a small race condition between making the thread active
		// and getting blocked; thus we might run into a deadlock
		//
		synchronized (receiver) {
			try {
				receiver.wait();
			}
			catch (InterruptedException e) {
				Assertion.fail("InterruptedException");
			}
		}
		// must not dequeue message before being blocked
		// in order to avoid inconsistencies in global network conditions
		for (int i = 0; i < setNum; i++) {
			if (!set[i].isEmpty()) return i;
		}
		Assertion.fail("no message delivered");
		return -1;
	}

	// --------------------------------------------------------------------------
	// return index of non-empty channel in set; do not block but poll at most 
	// `n` times; if then no message is found, -1 is returned
	// --------------------------------------------------------------------------
	public int select(int n) {
		Assertion.test(setNum != 0, "channel set is empty");
		boolean blocked = false;
		for (int j = 0; j < n; j++) {
			for (int i = 0; i < setNum; i++) {
				if (!set[i].isEmpty()) {
					if (blocked) {
						for (int k = 0; k < setNum; k++)
							set[k].receiveAwake();
					}
					return i;
				}
			}
			if (!blocked) {
				blocked = true;
				for (int k = 0; k < setNum; k++)
					set[k].receiveBlock();
			}
			
			Scheduler scheduler = set[0].getReceiver().getNetwork().getScheduler();
			scheduler.schedule();
			
		}
		if (blocked) {
			for (int k = 0; k < setNum; k++)
				set[k].receiveAwake();
		}
		return -1;
	}
}
