package negotiation.dcopframework.algo;

import java.util.HashSet;

import negotiation.dcopframework.daj.Channel;
import negotiation.dcopframework.daj.Message;
import negotiation.dcopframework.daj.Program;
import negotiation.dcopframework.dcop.Constraint;
import negotiation.dcopframework.dcop.Helper;
import negotiation.dcopframework.dcop.Variable;

public class AlgoMGM1 extends BasicAlgorithm {

	int bestVal;

	HashSet<Integer> acceptSet;

	public AlgoMGM1(final Variable v) {
		super(v);
		this.bestVal = -1;
		this.acceptSet = new HashSet<Integer>();
	}

	@Override
	protected void main() {
		this.self.value = Helper.random.nextInt(this.self.domain);
		this.out().broadcast(this.createValueMsg());
		while (true) {

			if (this.lock == -1 && this.bestVal != -1 && this.bestVal != this.self.value
					&& this.getTime() > this.reLockTime) {
				this.lock = this.self.id;
				this.acceptSet.clear();
				this.out().broadcast(this.createLockMsg());
			}

			final int index = this.in().select(1);
			if (index != -1) {
				this.done = false;
				final int sender = ((Channel) this.in(index)).getSender();
				final Message msg = this.in(index).receive();

				if (msg instanceof MGM1ValueMsg) {
					final MGM1ValueMsg vmsg = (MGM1ValueMsg) msg;
					if (this.view.varMap.get(sender).value != vmsg.value) {
						this.view.varMap.get(sender).value = vmsg.value;
						this.bestVal = this.checkView();
					}
				} else if (msg instanceof MGM1LockMsg) {
					final MGM1LockMsg lmsg = (MGM1LockMsg) msg;
					if (lmsg.lock) {
						if (this.lock == -1) {
							this.lock = sender;
							this.out(index).send(this.createAcceptMsg());
						} else
							this.out(index).send(this.createDenyMsg());
					} else if (this.lock == sender)
						this.lock = -1;
				} else if (msg instanceof MGM1ResponseMsg)
					if (this.lock == this.self.id) {
						final MGM1ResponseMsg rmsg = (MGM1ResponseMsg) msg;
						if (rmsg.accept)
							this.acceptSet.add(sender);
						else {
							this.lock = -1;
							this.out().broadcast(this.createUnLockMsg());
							this.reLockTime = this.getTime()
									+ Helper.random.nextInt(BasicAlgorithm.reLockInterval);
						}
						if (this.acceptSet.size() == this.self.neighbors.size()) {
							this.lock = -1;
							System.out.println("" + this.self.id + ":\t"
									+ this.self.value + " -> " + this.bestVal);
							this.out().broadcast(this.createUnLockMsg());
							this.self.value = this.bestVal;
							this.out().broadcast(this.createValueMsg());
						}
					}
			} else {
				if (this.bestVal == this.self.value) {
					this.done = true;
					if (this.checkStable())
						break;
				} else
					this.done = false;
				this.yield();
			}
		}
	}

	protected boolean checkStable() {
		final Program[] prog = this.node.getNetwork().getPrograms();
		for (int i = 0; i < prog.length; i++) {
			if (!(prog[i] instanceof BasicAlgorithm))
				continue;
			final BasicAlgorithm p = (BasicAlgorithm) prog[i];
			if (!p.done)
				return false;
		}
		return true;
	}


	int checkView() {
		int best = 0;
		int val = -1;
		for (int i = 0; i < this.self.domain; i++) {
			int sum = 0;
			for (final Constraint c : this.self.neighbors) {
				final Variable n = c.getNeighbor(this.self);
				if (n.value == -1)
					return -1;
				if (this.self == c.first)
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

	@Override
	public String getText() {
		return "ID: " + this.self.id + "\nVal: " + this.self.value + "\nBest: " + this.bestVal
				+ "\nNextLock: " + this.reLockTime;
	}

	MGM1ValueMsg createValueMsg() {
		return new MGM1ValueMsg(this.self.value);
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

class MGM1ValueMsg extends Message {
	int value;

	public MGM1ValueMsg(final int v) {
		super();
		this.value = v;
	}

	@Override
	public String getText() {
		return ("VALUE " + this.value);
	}
}

class MGM1LockMsg extends Message {
	boolean lock;

	public MGM1LockMsg(final boolean l) {
		super();
		this.lock = l;
	}

	@Override
	public String getText() {
		return (this.lock ? "LOCK" : "UNLOCK");
	}
}

class MGM1ResponseMsg extends Message {
	boolean accept;

	public MGM1ResponseMsg(final boolean a) {
		super();
		this.accept = a;
	}

	@Override
	public String getText() {
		return (this.accept ? "ACCEPT" : "DENY");
	}
}
