package examples.myDCOP.algo;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import examples.dcop2.algo.DPOPTreeNode;
import examples.dcop2.algo.TreeNode;
import examples.myDCOP.dcop.Variable;

public class AlgoKOptAPO extends LockingBasicAlgorithm {

	int k;

	HashSet<Integer> activeSet;
	public HashMap<Integer, DPOPTreeNode> localTreeMap;
	HashSet<HashSet<Integer>> localTreeSet;
	DPOPTreeNode lockingNode;
	int gIdCounter;

	public AlgoKOptAPO(final Variable v, final int kk) {
		super(v, true, kk);
		this.k = kk;
		this.init();
	}

	public AlgoKOptAPO(final Variable v, final int kk, final boolean s, final int ws) {
		super(v, s, ws);
		this.k = kk;
		this.init();
	}

	protected void init() {
		this.self.value = Helper.random.nextInt(this.self.domain);
		this.localTreeSet = new HashSet<HashSet<Integer>>();
		this.activeSet = new HashSet<Integer>();
		this.localTreeMap = new HashMap<Integer, DPOPTreeNode>();
		this.gIdCounter = 0;
	}


	final HashMap<Integer, Integer> conTTLMap = new HashMap<Integer, Integer>();
	@Override
	public void initialisation() {
		for (int i = 0; i < this.in().getSize(); i++) {
			final Program p = ((Channel) this.in(i)).getOwner().getProgram();
			if (p instanceof BasicAlgorithm) {
				final int id = ((BasicAlgorithm) p).getID();
				this.inChannelMap.put(id, i);
			}
		}

		for (int i = 0; i < this.out().getSize(); i++) {
				this.outChannelMap.put(((Channel) this.out(i)).getNeighbor().asInt(), i);
		}

		if (this.k > 1) {
			this.out().broadcast(new LocalConstraintMsg(this.self, this.k / 2));
		}
		this.changed = true;
		this.out().broadcast(new ValueMsg(this.self, this.k / 2 + 1));

		// HashMap<Integer, Integer> valTTLMap = new HashMap<Integer,
		// Integer>();

	}

	private void unlock() {
		if (this.lockBase < 16) {
			this.lockBase <<= 1;
		}
		this.reLockTime = this.getTime()
				+ Helper.random.nextInt(BasicAlgorithm.reLockInterval * this.lockBase);
		this.removeLock(this.self.id);
		this.waiting = false;
		for (final TreeNode n : this.lockingNode.children) {
			this.out(this.outChannelMap.get(n.id)).send(
					new LockMsg(this.lockingNode.id, this.lockingNode.reward, this.attempt, n,
							false));
		}
		this.lockingNode = null;
		//		final DCOPApplication app = (DCOPApplication) this.node.getNetwork()
		//				.getApplication();
		//		app.numberConflicts++;
	}

	@Override
	protected void main() {
		final int index = this.in().select(1);
		if (index != -1) {

			this.setDone(false);

			final DcopMessage msg = this.in(index).receive(1);
			if (msg == null) {
//				yield();
			}

			int sender = -1;
			final Program p = ((Channel) this.in(index)).getOwner().getProgram();
			if (p instanceof BasicAlgorithm) {
				sender = ((BasicAlgorithm) p).getID();
			}

			if (msg instanceof ValueMsg) {
				final ValueMsg vmsg = (ValueMsg) msg;
				final Variable v = this.view.varMap.get(vmsg.id);
				assert v != null;
				if (vmsg.ttl > 1) {
					this.out().broadcast(vmsg.forward());
				}
				if (v.value != vmsg.value) {
					v.value = vmsg.value;
					// TODO
					if (this.waiting) {
						final TreeNode node = this.lockingNode.find(v.id);
						if (node != null) {
							this.unlock();
						}
					}

					for (final DPOPTreeNode root : this.localTreeMap.values()) {
						final TreeNode node = root.find(v.id);
						if (node != null && node.fixed) {
							this.computeOpt(root);
							if (this.checkGroup(root)) {
								this.activeSet.add(root.gid);
							} else {
								this.activeSet.remove(root.gid);
							}
						}
					}
				}
			} else if (msg instanceof LocalConstraintMsg) {
				final LocalConstraintMsg lmsg = (LocalConstraintMsg) msg;
				final Integer lastTTL = conTTLMap.get(lmsg.id);
				if (lastTTL == null) {
					conTTLMap.put(lmsg.id, lmsg.ttl);
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
					conTTLMap.put(lmsg.id, lmsg.ttl);
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
					if (this.waiting && this.lockingNode == root
							&& rmsg.attempt == this.attempt) {
						if (!rmsg.accept) {
							this.unlock();
						} else {
							this.acceptSet.add(rmsg.id);
							if (this.acceptSet.size() >= this.lockingNode.getSize()) {
								/////////////store the stats////////////
								final Stats st = new Stats();
								final int pregain = this.view.evaluate();
								this.view.backup();
								for (final Variable v : this.view.varMap.values()) {
									final TreeNode node = this.lockingNode.find(v.id);
									if (node != null) {
										v.value=node.value;
									}
								}
								st.gain = this.view.evaluate() - pregain;
								this.view.recover();

								st.varChanged = 0;
								for (final Variable v : this.view.varMap.values()) {
									final TreeNode node = this.lockingNode.find(v.id);
									if (node != null && v.value != node.value) {
										st.varChanged++;
									}
								}
								st.attempts = this.attempt - this.preAttempt;
								final int present = this.getTime();
								st.cycles = present - this.preCycles;
								st.varLocked = this.lockingNode.getMarkedNodeSize();
								st.maxLockedDistance = this.lockingNode.maxdistanceMarkedNode();
								this.preAttempt = this.attempt;
								this.statList.add(st);
								/////////////////////////////////////////

								this.lockBase = 1;
								if (this.self.value != this.lockVal) {
									System.out.println(this.self.id + " "
											+ this.self.id + " " + this.self.value
											+ "->" + this.lockVal);
									this.self.value = this.lockVal;
									this.out().broadcast(
											new ValueMsg(this.self, this.k / 2 + 1));
								}
								this.waiting = false;
								this.removeLock(this.self.id);
								for (final TreeNode n : root.children) {
									this.out(this.outChannelMap.get(n.id)).send(
											new CommitMsg(this.self.id, this.attempt,
													n));
								}
								this.reLockTime = this.getTime() + this.k + 1
										+ +Helper.random.nextInt(this.k + 1);
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
					continue;
				}
				if (this.self.value != this.lockVal) {
					System.out.println(cmsg.gid + " " + this.self.id + " "
							+ this.self.value + "->" + this.lockVal);
					this.self.value = this.lockVal;
					this.out().broadcast(new ValueMsg(this.self, this.k / 2 + 1));
				}
				this.removeLock(cmsg.gid);
			} else if (msg instanceof TerminateMessage) {
				break;
			}
		} else {
			if (this.getTime() > this.k + 1 && this.changed) {
				this.changed = false;
				this.constructTrees();
				for (final DPOPTreeNode root : this.localTreeMap.values()) {
					this.computeOpt(root);
					if (this.checkGroup(root)) {
						this.activeSet.add(root.gid);
					} else {
						this.activeSet.remove(root.gid);
					}
				}
			}

			if (!this.lockMap.isEmpty()
					&& this.getTime() - this.lockMsgTimer >= this.windowsize) {
				//output lockmap size
				this.nlockReq += this.lockMap.size();
				/////////////
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
					if (this.acceptSet.size() >= this.lockingNode.getSize()) {
						/////////////store the stats////////////
						final Stats st = new Stats();
						final int pregain = this.view.evaluate();
						this.view.backup();
						for (final Variable v : this.view.varMap.values()) {
							final TreeNode node = this.lockingNode.find(v.id);
							if (node != null) {
								v.value=node.value;
							}
						}
						st.gain = this.view.evaluate() - pregain;
						this.view.recover();

						st.varChanged = 0;
						for (final Variable v : this.view.varMap.values()) {
							final TreeNode node = this.lockingNode.find(v.id);
							if (node != null && v.value != node.value) {
								st.varChanged++;
							}
						}
						st.attempts = this.attempt - this.preAttempt;

						final int present = this.getTime();
						st.cycles = present - this.preCycles;

						st.varLocked = this.lockingNode.getMarkedNodeSize();
						st.maxLockedDistance = this.lockingNode.maxdistanceMarkedNode();
						this.preAttempt = this.attempt;
						this.statList.add(st);
						/////////////////////////////////////////
						this.lockBase = 1;
						if (this.self.value != this.lockVal) {
							System.out.println(this.self.id + " " + this.self.id
									+ " " + this.self.value + "->" + this.lockVal);
							this.self.value = this.lockVal;
							this.out().broadcast(new ValueMsg(this.self, this.k / 2 + 1));
						}
						this.waiting = false;
						this.removeLock(this.self.id);
						for (final TreeNode n : this.lockingNode.children) {
							this.out(this.outChannelMap.get(n.id)).send(
									new CommitMsg(this.self.id, this.attempt, n));
						}
						this.reLockTime = this.getTime() + this.k + 1
								+ +Helper.random.nextInt(this.k + 1);
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
						this.lockingNode = null;
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

			if (!this.waiting && this.getTime() > this.reLockTime && !this.activeSet.isEmpty()) {
				if (this.getTime() > this.k + 1) {
					// double p = 1.0 / activeSet.size();
					this.setDone(true);
					DPOPTreeNode best = null;
					int gain = 0;
					for (final DPOPTreeNode root : this.localTreeMap.values()) {
						if (this.activeSet.contains(root.gid)) {
							if (this.checkGroup(root)) {
								if (root.reward > gain) {
									gain = root.reward;
									best = root;
								}
							} else {
								this.activeSet.remove(root.gid);
							}
						}
					}
					if (best != null) {
						this.setDone(false);
						this.lock(best);
					}
				}
			} else if (!this.waiting && !this.lockSet.isEmpty()
					&& this.getTime() <= this.reLockTime && !this.activeSet.isEmpty()) {
//				final DCOPApplication app = (DCOPApplication) this.node
//						.getNetwork().getApplication();
//				app.wastedCycles++;
			}
			if (this.getTime() > this.k + 1 && this.activeSet.isEmpty()) {
				this.setDone(true);
			}
//			yield();
		}
	}

	private boolean checkGroup(final DPOPTreeNode root) {
		final int orgReward = this.view.evaluate();
		this.view.backup();
		final ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			final DPOPTreeNode p = queue.remove(0);
			this.view.varMap.get(p.id).value = p.value;
			for (final TreeNode n : p.children) {
				final DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		final int newReward = this.view.evaluate();
		this.view.recover();
		root.reward = newReward - orgReward;
		return newReward > orgReward;
	}

	private boolean computeOpt(final DPOPTreeNode root) {
		for (final Variable v : this.view.varMap.values()) {
			v.fixed = true;
		}
		final ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			final DPOPTreeNode p = queue.remove(0);
			this.view.varMap.get(p.id).fixed = p.fixed;
			for (final TreeNode n : p.children) {
				final DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		final HashMap<Integer, Integer> sol = this.view.DPOPSolve();
		final HashSet<Integer> set = new HashSet<Integer>();
		for (final Variable v : this.view.varMap.values()) {
			if (v.value != sol.get(v.id)) {
				set.add(v.id);
				for (final Constraint c : v.neighbors) {
					set.add(c.getNeighbor(v).id);
				}
			}
		}
		queue.add(root);
		boolean f = false;
		while (!queue.isEmpty()) {
			final DPOPTreeNode p = queue.remove(0);
			// if (set.contains(p.id))
			p.mark = true;
			if (p.value != sol.get(p.id)) {
				f = true;
			}
			p.value = sol.get(p.id);
			for (final TreeNode n : p.children) {
				final DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}

		if (!this.subsetlocking) {
			root.markAll();
		}
		return f;
	}
	private void lock(final DPOPTreeNode root) {
		this.attempt++;
		this.acceptSet.clear();
		this.lockingNode = root;
		this.waiting = true;

		if (this.lockMap.isEmpty()) {
			this.lockMsgTimer = this.getTime();
		}
		this.lockMap.put(this.self.id, new LockMsg(this.self.id, root.reward, this.attempt,
				this.lockingNode, true));
		for (final TreeNode n : root.children) {
			this.out(this.outChannelMap.get(n.id)).send(
					new LockMsg(this.self.id, root.reward, this.attempt, n, true));
		}

	}

	private void constructTrees() {
		// TODO Auto-generated method stub
		final HashMap<Integer, Integer> minDis = new HashMap<Integer, Integer>();
		int maxId = 0;
		for (final Variable v : this.view.varMap.values()) {
			if (v.id > maxId) {
				maxId = v.id;
			}
		}
		maxId++;
		for (final Variable v : this.view.varMap.values()) {
			if (v.fixed) {
				continue;
			}
			final ArrayList<Variable> queue = new ArrayList<Variable>();
			queue.add(v);
			minDis.put(v.id * maxId + v.id, 0);
			final HashSet<Integer> visited = new HashSet<Integer>();
			visited.add(v.id);
			while (!queue.isEmpty()) {
				final Variable var = queue.remove(0);
				for (final Constraint c : var.neighbors) {
					final Variable n = c.getNeighbor(var);
					if (n.fixed) {
						continue;
					}
					if (!visited.contains(n.id)) {
						queue.add(n);
						final int depth = minDis.get(v.id * maxId + var.id);
						visited.add(n.id);
						minDis.put(v.id * maxId + n.id, depth + 1);
					}
				}
			}
		}

		final HashSet<Integer> pSet = new HashSet<Integer>();
		final HashSet<Integer> cSet = new HashSet<Integer>();
		for (final Constraint c : this.self.neighbors) {
			final Variable n = c.getNeighbor(this.self);
			if (!n.fixed) {
				cSet.add(n.id);
			}
		}
		this.enumerate(pSet, cSet, minDis, maxId);
	}

	private void enumerate(final HashSet<Integer> pSet, final HashSet<Integer> cSet,
			final HashMap<Integer, Integer> minDis, final int maxId) {
		if (pSet.size() == this.k - 1) {
			boolean f = true;
			int d = 0;
			for (final Integer i : pSet) {
				final int dis = minDis.get(this.self.id * maxId + i);
				if (dis > d) {
					d = dis;
				}
			}

			for (final Integer i : pSet) {
				int tmp = minDis.get(i * maxId + this.self.id);
				for (final Integer j : pSet) {
					if (j == i) {
						continue;
					}
					final int dis = minDis.get(i * maxId + j);
					if (dis > tmp) {
						tmp = dis;
					}
				}
				if (tmp < d || (tmp == d && i < this.self.id)) {
					f = false;
					break;
				}
			}
			if (f) {
				for (final HashSet<Integer> tSet : this.localTreeSet) {
					if (tSet.containsAll(pSet)) {
						return;
					}
				}
				final HashSet<Integer> visited = new HashSet<Integer>();
				final DPOPTreeNode root = new DPOPTreeNode(this.self.id,
						(this.gIdCounter << 12) + this.self.id, 0, false, null);
				visited.add(this.self.id);
				final ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
				queue.add(root);
				int count = 0;
				while (!queue.isEmpty()) {
					final DPOPTreeNode node = queue.remove(0);
					final Variable v = this.view.varMap.get(node.id);
					for (final Constraint c : v.neighbors) {
						final Variable n = c.getNeighbor(v);
						if (visited.contains(n.id)) {
							continue;
						}
						visited.add(n.id);
						if (!pSet.contains(n.id)) {
							final DPOPTreeNode child = new DPOPTreeNode(n.id,
									node.gid, n.value, true, node);
						} else {
							final DPOPTreeNode child = new DPOPTreeNode(n.id,
									node.gid, n.value, false, node);
							count++;
							queue.add(child);
						}
					}
				}
				this.localTreeMap.put(root.gid, root);
				final HashSet<Integer> treeSet = new HashSet<Integer>();
				treeSet.addAll(pSet);
				this.localTreeSet.add(treeSet);
				this.gIdCounter++;
			}
			return;
		}

		// int max = 0;
		// for (Integer i : pSet)
		// if (max < i)
		// max = i;

		final int[] list = new int[cSet.size()];
		{
			int j = 0;
			for (final Integer i : cSet) {
				list[j] = i;
				j++;
			}
		}

		for (final int j : list) {
			if (j == this.self.id) {
				continue;
			}
			pSet.add(j);
			final HashSet<Integer> add = new HashSet<Integer>();
			final Variable v = this.view.getVar(j);
			for (final Constraint c : v.neighbors) {
				final Variable n = c.getNeighbor(v);
				if (!n.fixed && !pSet.contains(n.id) && !cSet.contains(n.id)) {
					add.add(n.id);
				}
			}
			cSet.addAll(add);
			cSet.remove(j);
			this.enumerate(pSet, cSet, minDis, maxId);
			cSet.removeAll(add);
			pSet.remove(j);
		}
	}

	@Override
	public String getText() {
		String val = "";
		for (final Variable v : this.view.varMap.values()) {
			val += v.id + "  " + v.value + " " + (v.fixed ? "F" : "") + "\n";
		}
		val += "LockSet: ";
		for (final Integer i : this.lockSet.values()) {
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
		+ (isDone() ? "\nDONE" : "");
	}
}
