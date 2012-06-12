package examples.dcopAmeliorer.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import examples.dcopAmeliorer.algo.BasicAlgorithm;
import examples.dcopAmeliorer.algo.LockingBasicAlgorithm;
import examples.dcopAmeliorer.algo.Stats;
import examples.dcopAmeliorer.daj.Channel;
import examples.dcopAmeliorer.daj.DcopMessage;
import examples.dcopAmeliorer.dcop.Constraint;
import examples.dcopAmeliorer.dcop.Helper;
import examples.dcopAmeliorer.dcop.Variable;

public class AlgoTOptAPO extends LockingBasicAlgorithm {

	int t;
	HashMap<Integer, Integer> bestSolution;
	TreeNode center;
	public boolean trivial;

	public AlgoTOptAPO(final Variable v, final int tt) {
		super(v, true, 2);
		this.t = tt;
		this.init();
	}

	public AlgoTOptAPO(final Variable v, final int tt, final boolean s, final int ws) {
		super(v, s, ws);
		this.t = tt;
		this.init();
	}

	protected void init() {
		this.self.value = Helper.random.nextInt(this.self.domain);
		this.bestSolution = null;
		this.center = null;
		this.trivial = false;
	}

	@Override
	public void initialisation() {

		for (final Channel c : this.in().getChannels()) {
			this.inChannelMap.put(c.getNeighbor().asInt(),c.getNeighbor().asInt());
		}

		for (final Channel c : this.out().getChannels()) {
			this.outChannelMap.put(c.getNeighbor().asInt(),c.getNeighbor().asInt());
		}

		if (this.t > 0) {
			this.out().broadcast(new LocalConstraintMsg(this.self, this.t));
		}
		this.out().broadcast(new ValueMsg(this.self, this.t + 1));
		this.changed = true;
	}

	// HashMap<Integer, Integer> valTTLMap = new HashMap<Integer,
	// Integer>();
	HashMap<Integer, Integer> conTTLMap = new HashMap<Integer, Integer>();

	@Override
	protected void main() {

		// if (self.id == 90)
		// System.out.println("DEBUG");
		final int index = this.in().select(1);
		if (index != -1) {

			this.setDone(false);

			final DcopMessage msg = this.in(index).receive(1);
			//				if (msg == null)
			//					yield();

			final int sender = ((Channel) this.in(index)).getNeighbor().asInt();

			if (msg instanceof ValueMsg) {
				final ValueMsg vmsg = (ValueMsg) msg;
				final Variable v = this.view.varMap.get(vmsg.id);
				assert v != null;
				if (vmsg.ttl > 1) {
					this.out().broadcast(vmsg.forward());
				}
				if (v.value != vmsg.value) {
					v.value = vmsg.value;
					if (v.fixed) {
						this.changed = true;
					}
				}
			} else if (msg instanceof LocalConstraintMsg) {
				final LocalConstraintMsg lmsg = (LocalConstraintMsg) msg;
				final Integer lastTTL = this.conTTLMap.get(lmsg.id);
				if (lastTTL == null) {
					this.conTTLMap.put(lmsg.id, lmsg.ttl);
					Variable v = this.view.varMap.get(lmsg.id);
					if (v == null) {
						v = new Variable(lmsg.id, lmsg.domain, this.view);
						this.view.varMap.put(v.id, v);
					}
					v.fixed = false;
					for (final int[] enc : lmsg.data) {
						if (enc[0] == v.id) {
							Variable n = this.view.varMap.get(enc[2]);
							if (n == null) {
								n = new Variable(enc[2], enc[3], this.view);
								if (lmsg.ttl <= 1) {
									n.fixed = true;
								}
								this.view.varMap.put(n.id, n);
							}
							if (!v.hasNeighbor(n.id)) {
								final Constraint c = new Constraint(v, n);
								this.view.conList.add(c);
								for (int i = 0; i < c.d1; i++) {
									for (int j = 0; j < c.d2; j++) {
										c.f[i][j] = enc[4 + i * c.d2 + j];
									}
								}
								c.cache();
							}
						} else {
							Variable n = this.view.varMap.get(enc[0]);
							if (n == null) {
								n = new Variable(enc[0], enc[1], this.view);
								if (lmsg.ttl <= 1) {
									n.fixed = true;
								}
								this.view.varMap.put(n.id, n);
							}
							if (!v.hasNeighbor(n.id)) {
								final Constraint c = new Constraint(n, v);
								this.view.conList.add(c);
								for (int i = 0; i < c.d1; i++) {
									for (int j = 0; j < c.d2; j++) {
										c.f[i][j] = enc[4 + i * c.d2 + j];
									}
								}
								c.cache();
							}
						}
					}
					if (lmsg.ttl > 1) {
						this.out().broadcast(lmsg.forward());
					}
				} else if (lastTTL < lmsg.ttl) {
					this.conTTLMap.put(lmsg.id, lmsg.ttl);
					this.out().broadcast(lmsg.forward());
				}
			} else if (msg instanceof LockMsg) {
				final LockMsg lkmsg = (LockMsg) msg;
				final TreeNode root = lkmsg.node;
				if (lkmsg.lock) {
					if (!root.mark) {
						this.out(this.outChannelMap.get(sender)).send(
								new ResponseMsg(this.self.id, lkmsg.attempt,
										root.parent, true));
						for (final TreeNode n : root.children) {
							this.out(this.outChannelMap.get(n.id)).send(
									new LockMsg(lkmsg.gid, lkmsg.val,
											lkmsg.attempt, n, true));
						}
					} else {
						if (this.lockVal == root.value) {
							final Integer att = this.lockSet.get(lkmsg.gid);
							if (att == null || att < lkmsg.attempt) {
								this.lockSet.put(lkmsg.gid, lkmsg.attempt);
								this.out(this.outChannelMap.get(sender)).send(
										new ResponseMsg(this.self.id,
												lkmsg.attempt, root.parent,
												true));
								for (final TreeNode n : root.children) {
									this.out(this.outChannelMap.get(n.id))
									.send(
											new LockMsg(lkmsg.gid,
													lkmsg.val,
													lkmsg.attempt,
													n, true));
								}
							}
						} else if (this.lockVal == -1) {
							final Integer att = this.lockSet.get(lkmsg.gid);
							if (att == null || att < lkmsg.attempt) {
								final LockMsg l = this.lockMap.get(lkmsg.gid);
								if (l == null || l.attempt < lkmsg.attempt) {
									if (this.lockMap.isEmpty()) {
										this.lockMsgTimer = this.getTime();
									}
									this.lockMap.put(lkmsg.gid, lkmsg);

								}
							}
							for (final TreeNode n : root.children) {
								this.out(this.outChannelMap.get(n.id)).send(
										new LockMsg(lkmsg.gid, lkmsg.val,
												lkmsg.attempt, n, true));
							}
						} else {
							this.out(this.outChannelMap.get(sender)).send(
									new ResponseMsg(this.self.id, lkmsg.attempt,
											root.parent, false));
						}
					}
				} else {
					final Integer att = this.lockSet.get(lkmsg.gid);
					final LockMsg l = this.lockMap.get(lkmsg.gid);
					if ((att != null && att <= lkmsg.attempt)
							|| (l != null && l.attempt <= lkmsg.attempt)) {
						this.removeLock(lkmsg.gid);
					}
					for (final TreeNode n : root.children) {
						this.out(this.outChannelMap.get(n.id)).send(
								new LockMsg(lkmsg.gid, lkmsg.val,
										lkmsg.attempt, n, false));
					}
				}

			} else if (msg instanceof ResponseMsg) {
				final ResponseMsg rmsg = (ResponseMsg) msg;
				final TreeNode root = rmsg.node;
				if (root.parent == null) {
					if (this.waiting && rmsg.attempt == this.attempt) {
						if (!rmsg.accept) {
							//								System.out.println("=== " + self.id + " "
									//										+ "UNLOCK ===");
							if (this.lockBase < 16) {
								this.lockBase <<= 1;
							}
							this.reLockTime = this.getTime()
									+ Helper.random.nextInt(BasicAlgorithm.reLockInterval
											* this.lockBase);
							this.removeLock(this.self.id);
							this.waiting = false;
							for (final TreeNode n : root.children) {
								this.out(this.outChannelMap.get(n.id))
								.send(
										new LockMsg(this.self.id,
												this.view.varMap.size(),
												this.attempt, n, false));
							}
							//								DCOPApplication app = (DCOPApplication) this.node
							//										.getNetwork().getApplication();
							//								app.numberConflicts++;
						} else {
							this.acceptSet.add(rmsg.id);
							if (this.acceptSet.size() >= this.view.varMap.size()) {
								// ///////////store the stats////////////
								final Stats st = new Stats();
								st.gain = this.view.evaluate(this.bestSolution)
										- this.view.evaluate();
								st.varChanged = 0;
								for (final Variable v : this.view.varMap.values()) {
									if (v.value != this.bestSolution.get(v.id)) {
										st.varChanged++;
									}
								}
										st.attempts = this.attempt - this.preAttempt;

										final int present = this.getTime();
										st.cycles = present - this.preCycles;

										st.varLocked = this.center.getMarkedNodeSize();
										st.maxLockedDistance = this.center
												.maxdistanceMarkedNode();
										this.preAttempt = this.attempt;

										this.statList.add(st);
										// ///////////////////////////////////////
										this.lockBase = 1;
										if (this.self.value != this.lockVal) {
											//										System.out.println(self.id + " "
											//												+ self.id + " " + self.value
											//												+ "->" + lockVal);
											this.self.value = this.lockVal;
											this.out().broadcast(
													new ValueMsg(this.self, this.t + 1));
										}
										this.waiting = false;
										this.removeLock(this.self.id);
										for (final TreeNode n : root.children) {
											this.out(this.outChannelMap.get(n.id)).send(
													new CommitMsg(this.self.id, this.attempt,
															n));
										}
										this.reLockTime = this.getTime()
												+ 2
												* (this.t + 1)
												+ +Helper.random
												.nextInt(2 * (this.t + 1));
							}
						}
					}
				} else {
					this.out(this.outChannelMap.get(root.parent.id)).send(
							new ResponseMsg(rmsg.id, rmsg.attempt,
									root.parent, rmsg.accept));
				}
			} else if (msg instanceof CommitMsg) {
				final CommitMsg cmsg = (CommitMsg) msg;
				final TreeNode root = cmsg.node;
				for (final TreeNode n : root.children) {
					this.out(this.outChannelMap.get(n.id)).send(
							new CommitMsg(cmsg.gid, cmsg.attempt, n));
				}
				final Integer att = this.lockSet.get(cmsg.gid);
				if (att == null || att != cmsg.attempt) {
					return;
				}
				if (this.self.value != this.lockVal) {
					System.out.println(cmsg.gid + " " + this.self.id + " "
							+ this.self.value + "->" + this.lockVal);
					this.self.value = this.lockVal;
					this.out().broadcast(new ValueMsg(this.self, this.t + 1));
				}
				this.removeLock(cmsg.gid);

//			} else if (msg instanceof TerminateMessage) {
//				break;
			}
		} else {
			if (this.getTime() == 2 * this.t + 1) {
				this.trivial = this.isTrivial();
			}
			// if (getTime() > 1000 && !lockSet.isEmpty())
			// System.out.println(self.id + " " + lockSet);
			// TODO
			if (!this.lockMap.isEmpty()
					&& this.getTime() - this.lockMsgTimer >= this.windowsize) {
				// output lockmap size
				this.nlockReq += this.lockMap.size();
				// ///////////
				int comp = Integer.MIN_VALUE;
				int id = Integer.MAX_VALUE;
				LockMsg best = null;
				for (final LockMsg msg : this.lockMap.values()) {
					if (msg.val > comp || (msg.val == comp && msg.gid < id)) {
						comp = msg.val;
						id = msg.gid;
						best = msg;
					}
				}
				this.lockVal = best.node.value;
				this.lockSet.put(best.gid, best.attempt);
				if (best.node.parent != null) {
					this.out(this.outChannelMap.get(best.node.parent.id)).send(
							new ResponseMsg(this.self.id, best.attempt,
									best.node.parent, true));
				} else {
					this.acceptSet.add(this.self.id);
					if (this.acceptSet.size() >= this.view.varMap.size()) {
						// ///////////store the stats////////////
						final Stats st = new Stats();
						st.gain = this.view.evaluate(this.bestSolution)
								- this.view.evaluate();
						st.varChanged = 0;
						for (final Variable v : this.view.varMap.values()) {
							if (v.value != this.bestSolution.get(v.id)) {
								st.varChanged++;
							}
						}
						st.attempts = this.attempt - this.preAttempt;

						final int present = this.getTime();
						st.cycles = present - this.preCycles;

						st.varLocked = this.center.getMarkedNodeSize();
						st.maxLockedDistance = this.center
								.maxdistanceMarkedNode();
						this.preAttempt = this.attempt;
						this.statList.add(st);
						// ///////////////////////////////////////
						this.lockBase = 1;
						if (this.self.value != this.lockVal) {
							System.out.println(this.self.id + " " + this.self.id
									+ " " + this.self.value + "->" + this.lockVal);
							this.self.value = this.lockVal;
							this.out().broadcast(new ValueMsg(this.self, this.t + 1));
						}
						this.waiting = false;
						this.removeLock(this.self.id);
						for (final TreeNode n : this.center.children) {
							this.out(this.outChannelMap.get(n.id)).send(
									new CommitMsg(this.self.id, this.attempt, n));
						}
						this.reLockTime = this.getTime() + 2 * (this.t + 1)
								+ +Helper.random.nextInt(2 * (this.t + 1));
					}
				}

				for (final LockMsg msg : this.lockMap.values()) {
					if (msg.gid == best.gid) {
						continue;
					}
					if (msg.node.parent != null) {
						if (msg.node.value == this.lockVal) {
							this.lockSet.put(msg.gid, msg.attempt);
							this.out(this.outChannelMap.get(msg.node.parent.id))
							.send(
									new ResponseMsg(this.self.id,
											msg.attempt,
											msg.node.parent, true));
						} else {
							this.out(this.outChannelMap.get(msg.node.parent.id))
							.send(
									new ResponseMsg(this.self.id,
											msg.attempt,
											msg.node.parent, false));
						}
					} else {
						this.waiting = false;
						for (final TreeNode n : msg.node.children) {
							this.out(this.outChannelMap.get(n.id)).send(
									new LockMsg(this.self.id,
											this.view.varMap.size(), this.attempt, n,
											false));
						}
					}
				}
				this.lockMap.clear();
			}
			this.setDone(false);
			if (!this.trivial) {
				if (this.getTime() > 2 * this.t + 1 && this.changed) {
					this.changed = false;
					this.checkSolution();
					if (this.waiting && this.center != null) {
						// System.out.println("=== " + self.id + " " +
						// "UNLOCK ===");
						this.removeLock(this.self.id);
						this.waiting = false;
						for (final TreeNode n : this.center.children) {
							this.out(this.outChannelMap.get(n.id)).send(
									new LockMsg(this.self.id,
											this.view.varMap.size(), this.attempt, n,
											false));
						}
						//							DCOPApplication app = (DCOPApplication) this.node
						//									.getNetwork().getApplication();
						//							app.numberConflicts++;
					}
				}
				if (this.checkMove()) {
					if (!this.waiting && this.lockVal == -1 && this.getTime() > this.reLockTime) {
						this.center = this.constructTree();
						//							System.out.println(self.id + " " + center.getMarkedNodeSize() + "/"
						//									+ center.getSize());
						this.acceptSet.clear();
						this.waiting = true;
						this.attempt++;
						if (this.lockMap.isEmpty()) {
							this.lockMsgTimer = this.getTime();
						}
						final int gain = this.view.evaluate(this.bestSolution)
								- this.view.evaluate();
						this.lockMap.put(this.self.id, new LockMsg(this.self.id, gain,
								this.attempt, this.center, true));
						for (final TreeNode n : this.center.children) {
							this.out(this.outChannelMap.get(n.id)).send(
									new LockMsg(this.self.id, gain, this.attempt, n,
											true));
						}

					} else if (!this.waiting && this.lockVal == -1
							&& this.getTime() <= this.reLockTime) {
						//							DCOPApplication app = (DCOPApplication) this.node
						//									.getNetwork().getApplication();
						//							app.wastedCycles++;
					}
				} else if (this.bestSolution != null
						&& this.view.evaluate() >= this.view.evaluate(this.bestSolution)) {
					this.setDone(true);
				}
			}
			else {
				this.setDone(true);
			//				yield();
			}
		}
	}

	private void checkSolution() {
		for (final Variable v : this.view.varMap.values()) {
			if (v.fixed && v.value == -1) {
				this.bestSolution = null;
				return;
			}
		}
		if (this.view.conList.size() > this.view.varMap.size() * this.view.varMap.size() / 4) {
			this.bestSolution = this.view.branchBoundSolve();
		} else {
			this.bestSolution = this.view.DPOPSolve();
		}
	}

	// private boolean checkTree(TreeNode root) {
	// if (bestSolution == null)
	// return false;
	// if (root.value != bestSolution.get(root.id))
	// return false;
	// for (TreeNode n : root.children)
	// if (!checkTree(n))
	// return false;
	// return true;
	// }

	private TreeNode constructTree() {
		final TreeNode root = new TreeNode(this.self.id, this.bestSolution.get(this.self.id), false,
				null);
		if (root.value != this.view.varMap.get(this.self.id).value) {
			root.mark = true;
		}

		final ArrayList<Variable> queue = new ArrayList<Variable>();
		final HashMap<Integer, TreeNode> map = new HashMap<Integer, TreeNode>();
		queue.add(this.self);
		map.put(this.self.id, root);
		while (!queue.isEmpty()) {
			final Variable v = queue.remove(0);
			final TreeNode p = map.get(v.id);
			for (final Constraint c : v.neighbors) {
				final Variable n = c.getNeighbor(v);
				if (!map.containsKey(n.id)) {
					queue.add(n);
					final TreeNode child = new TreeNode(n.id, this.bestSolution.get(n.id),
							n.fixed, p);
					if (v.value != this.bestSolution.get(v.id)
							|| n.value != this.bestSolution.get(n.id)) {
						child.mark = true;
						p.mark = true;
					}
					map.put(n.id, child);
				} else {
					final TreeNode node = map.get(n.id);
					if (v.value != this.bestSolution.get(v.id)) {
						node.mark = true;
					}
				}
			}
		}
		if (!this.subsetlocking) {
			root.markAll();
		}
		return root;
	}

	private boolean checkMove() {
		if (this.bestSolution == null) {
			return false;
		}
		for (final Variable v : this.view.varMap.values()) {
			if (!this.bestSolution.containsKey(v.id)) {
				return false;
			}
		}
		final int current = this.view.evaluate();
		final int best = this.view.evaluate(this.bestSolution);
		if (current >= best) {
			this.bestSolution = this.view.getSolution();
			return false;
		} else {
			return true;
		}
	}

	private boolean isTrivial() {
		final HashMap<Integer, Integer> minDis = new HashMap<Integer, Integer>();
		int maxId = 0;
		for (final Variable v : this.view.varMap.values()) {
			if (v.id > maxId) {
				maxId = v.id;
			}
		}
				maxId++;
				for (final Variable v : this.view.varMap.values()) {
					final ArrayList<Variable> queue = new ArrayList<Variable>();
					queue.add(v);
					minDis.put(v.id * maxId + v.id, 0);
					final HashSet<Integer> visited = new HashSet<Integer>();
					visited.add(v.id);
					while (!queue.isEmpty()) {
						final Variable var = queue.remove(0);
						for (final Constraint c : var.neighbors) {
							final Variable n = c.getNeighbor(var);
							if (!visited.contains(n.id)) {
								queue.add(n);
								final int depth = minDis.get(v.id * maxId + var.id);
								visited.add(n.id);
								minDis.put(v.id * maxId + n.id, depth + 1);
							}
						}
					}
				}

				int minD = 0;
				for (final Variable v : this.view.varMap.values()) {
					final int m = minDis.get(this.self.id * maxId + v.id);
					if (m > minD) {
						minD = m;
					}
				}

				for (final Constraint c : this.self.neighbors) {
					final Variable n = c.getNeighbor(this.self);
					int d = 0;
					for (final Variable v : this.view.varMap.values()) {
						final int m = minDis.get(n.id * maxId + v.id);
						if (m > d) {
							d = m;
						}
					}
					if (d < minD)
					 {
						return true;
					// if (d == minD && self.id > n.id)
					// return true;
					}
				}
				return false;
	}

	@Override
	public String getText() {
		String val = "";
		for (final Variable v : this.view.varMap.values()) {
			val += v.id + "  " + v.value + "/"
					+ (this.bestSolution != null ? this.bestSolution.get(v.id) : "NA")
					+ (v.fixed ? "F" : "") + "\n";
		}
		val += "LockSet: ";
		for (final Integer i : this.lockSet.keySet()) {
			val += i + " ";
		}
		val += "\n";

		val += "AcceptSet: ";
		for (final Integer i : this.acceptSet) {
			val += i + " ";
		}
		val += "\n";

		return val + "ID: " + this.self.id + "\nVal: " + this.self.value + "\nLockVal: "
		+ this.lockVal + "\nNextLock: " + this.reLockTime
		+ (this.isStable() ? "\nDONE" : "");
	}

}
