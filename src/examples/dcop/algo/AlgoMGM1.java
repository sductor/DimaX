package examples.dcop.algo;

import java.util.HashSet;

import examples.dcop.daj.Channel;
import examples.dcop.daj.DcopMessage;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Helper;
import examples.dcop.dcop.Variable;

public class AlgoMGM1 extends BasicAlgorithm {

	int bestVal;

	HashSet<Integer> acceptSet;

	public AlgoMGM1(Variable v) {
		super(v);
		bestVal = -1;
		acceptSet = new HashSet<Integer>();
	}


	@Override
	public void initialisation() {
		self.value = Helper.random.nextInt(self.domain);
		out().broadcast(createValueMsg());

	}	


	@Override
	protected void main() {

		if (lock == -1 && bestVal != -1 && bestVal != self.value
				&& getTime() > reLockTime) {
			lock = self.id;
			acceptSet.clear();
			out().broadcast(createLockMsg());
		}

		int index = in().select(1);
		if (index != -1) {
			setDone(false);
			int sender = (((Channel) in(index))).getNeighbor().asInt();
			DcopMessage msg = in(index).receive();

			if (msg instanceof MGM1ValueMsg) {
				MGM1ValueMsg vmsg = (MGM1ValueMsg) msg;
				if (view.varMap.get(sender).value != vmsg.value) {
					view.varMap.get(sender).value = vmsg.value;
					bestVal = checkView();
				}
			} else if (msg instanceof MGM1LockMsg) {
				MGM1LockMsg lmsg = (MGM1LockMsg) msg;
				if (lmsg.lock) {
					if (lock == -1) {
						lock = sender;
						out(index).send(createAcceptMsg());
					} else
						out(index).send(createDenyMsg());
				} else {
					if (lock == sender)
						lock = -1;
				}
			} else if (msg instanceof MGM1ResponseMsg) {
				if (lock == self.id) {
					MGM1ResponseMsg rmsg = (MGM1ResponseMsg) msg;
					if (rmsg.accept)
						acceptSet.add(sender);
					else {
						lock = -1;
						out().broadcast(createUnLockMsg());
						reLockTime = getTime()
								+ Helper.random.nextInt(reLockInterval);
					}
					if (acceptSet.size() == self.neighbors.size()) {
						lock = -1;
						System.out.println("" + self.id + ":\t"
								+ self.value + " -> " + bestVal);
						out().broadcast(createUnLockMsg());
						self.value = bestVal;
						out().broadcast(createValueMsg());
					}
				}
			}
		} else {
			if (bestVal == self.value) {
				setDone(true);
//				if (checkStable())
//					break;
			} else
				setDone(false);
//			yield();
		}
	}

	int checkView() {
		int best = 0;
		int val = -1;
		for (int i = 0; i < self.domain; i++) {
			int sum = 0;
			for (Constraint c : self.neighbors) {
				Variable n = c.getNeighbor(self);
				if (n.value == -1)
					return -1;
				if (self == c.first)
					sum += c.f[i][n.value];
				else
					sum += c.f[n.value][i];
			}
			if (sum > best) {
				best = sum;
				val = i;
			}
		}
		return val;
	}

	public String getText() {
		return "ID: " + self.id + "\nVal: " + self.value + "\nBest: " + bestVal
				+ "\nNextLock: " + reLockTime;
	}

	MGM1ValueMsg createValueMsg() {
		return new MGM1ValueMsg(self.value);
	}

	MGM1LockMsg createLockMsg() {
		return new MGM1LockMsg(true);
	}

	MGM1LockMsg createUnLockMsg() {
		return new MGM1LockMsg(false);
	}

	MGM1ResponseMsg createAcceptMsg() {
		return new MGM1ResponseMsg(true);
	}

	MGM1ResponseMsg createDenyMsg() {
		return new MGM1ResponseMsg(false);
	}

}

class MGM1ValueMsg extends DcopMessage {
	int value;

	public MGM1ValueMsg(int v) {
		super();
		value = v;
	}

	public String getText() {
		return ("VALUE " + value);
	}
}

class MGM1LockMsg extends DcopMessage {
	boolean lock;

	public MGM1LockMsg(boolean l) {
		super();
		lock = l;
	}

	public String getText() {
		return (lock ? "LOCK" : "UNLOCK");
	}
}

class MGM1ResponseMsg extends DcopMessage {
	boolean accept;

	public MGM1ResponseMsg(boolean a) {
		super();
		accept = a;
	}

	public String getText() {
		return (accept ? "ACCEPT" : "DENY");
	}
}
