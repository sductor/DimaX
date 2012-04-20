package examples.dcop.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import examples.dcop.algo.LockingBasicAlgorithm;
import examples.dcop.algo.Stats;
import examples.dcop.daj.Channel;
import examples.dcop.daj.DcopMessage;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Helper;
import examples.dcop.dcop.Variable;

public class AlgoTOptAPO extends LockingBasicAlgorithm {

	int t;
	HashMap<Integer, Integer> bestSolution;
	TreeNode center;
	public boolean trivial;

	public AlgoTOptAPO(Variable v, int tt) {
		super(v, true, 2);
		t = tt;
		init();
	}

	public AlgoTOptAPO(Variable v, int tt, boolean s, int ws) {
		super(v, s, ws);
		t = tt;
		init();
	}

	protected void init() {
		self.value = Helper.random.nextInt(self.domain);
		bestSolution = null;
		center = null;
		trivial = false;
	}

	@Override
	public void initialisation() {
		// TODO Auto-generated method stub

		for (Channel c : in().getChannels()) {
			inChannelMap.put(c.getNeighbor().asInt(),c.getNeighbor().asInt());
		}

		for (Channel c : out().getChannels()) {
			outChannelMap.put(c.getNeighbor().asInt(),c.getNeighbor().asInt());
		}

		if (t > 0)
			out().broadcast(new LocalConstraintMsg(self, t));
		out().broadcast(new ValueMsg(self, t + 1));
		changed = true;
	}

	// HashMap<Integer, Integer> valTTLMap = new HashMap<Integer,
	// Integer>();
	HashMap<Integer, Integer> conTTLMap = new HashMap<Integer, Integer>();

	@Override
	protected void main() {

		// if (self.id == 90)
		// System.out.println("DEBUG");
		int index = in().select(1);
		if (index != -1) {

			setDone(false);

			DcopMessage msg = in(index).receive(1);
			//				if (msg == null)
			//					yield();

			int sender = ((Channel) in(index)).getNeighbor().asInt();

			if (msg instanceof ValueMsg) {
				ValueMsg vmsg = (ValueMsg) msg;
				Variable v = view.varMap.get(vmsg.id);
				assert v != null;
				if (vmsg.ttl > 1)
					out().broadcast(vmsg.forward());
				if (v.value != vmsg.value) {
					v.value = vmsg.value;
					if (v.fixed)
						changed = true;
				}
			} else if (msg instanceof LocalConstraintMsg) {
				LocalConstraintMsg lmsg = (LocalConstraintMsg) msg;
				Integer lastTTL = conTTLMap.get(lmsg.id);
				if (lastTTL == null) {
					conTTLMap.put(lmsg.id, lmsg.ttl);
					Variable v = view.varMap.get(lmsg.id);
					if (v == null) {
						v = new Variable(lmsg.id, lmsg.domain, view);
						view.varMap.put(v.id, v);
					}
					v.fixed = false;
					for (int[] enc : lmsg.data) {
						if (enc[0] == v.id) {
							Variable n = view.varMap.get(enc[2]);
							if (n == null) {
								n = new Variable(enc[2], enc[3], view);
								if (lmsg.ttl <= 1)
									n.fixed = true;
								view.varMap.put(n.id, n);
							}
							if (!v.hasNeighbor(n.id)) {
								Constraint c = new Constraint(v, n);
								view.conList.add(c);
								for (int i = 0; i < c.d1; i++)
									for (int j = 0; j < c.d2; j++) {
										c.f[i][j] = enc[4 + i * c.d2 + j];
									}
								c.cache();
							}
						} else {
							Variable n = view.varMap.get(enc[0]);
							if (n == null) {
								n = new Variable(enc[0], enc[1], view);
								if (lmsg.ttl <= 1)
									n.fixed = true;
								view.varMap.put(n.id, n);
							}
							if (!v.hasNeighbor(n.id)) {
								Constraint c = new Constraint(n, v);
								view.conList.add(c);
								for (int i = 0; i < c.d1; i++)
									for (int j = 0; j < c.d2; j++) {
										c.f[i][j] = enc[4 + i * c.d2 + j];
									}
								c.cache();
							}
						}
					}
					if (lmsg.ttl > 1)
						out().broadcast(lmsg.forward());
				} else if (lastTTL < lmsg.ttl) {
					conTTLMap.put(lmsg.id, lmsg.ttl);
					out().broadcast(lmsg.forward());
				}
			} else if (msg instanceof LockMsg) {
				LockMsg lkmsg = (LockMsg) msg;
				TreeNode root = lkmsg.node;
				if (lkmsg.lock) {
					if (!root.mark) {
						out(outChannelMap.get(sender)).send(
								new ResponseMsg(self.id, lkmsg.attempt,
										root.parent, true));
						for (TreeNode n : root.children) {
							out(outChannelMap.get(n.id)).send(
									new LockMsg(lkmsg.gid, lkmsg.val,
											lkmsg.attempt, n, true));
						}
					} else {
						if (lockVal == root.value) {
							Integer att = lockSet.get(lkmsg.gid);
							if (att == null || att < lkmsg.attempt) {
								lockSet.put(lkmsg.gid, lkmsg.attempt);
								out(outChannelMap.get(sender)).send(
										new ResponseMsg(self.id,
												lkmsg.attempt, root.parent,
												true));
								for (TreeNode n : root.children) {
									out(outChannelMap.get(n.id))
									.send(
											new LockMsg(lkmsg.gid,
													lkmsg.val,
													lkmsg.attempt,
													n, true));
								}
							}
						} else if (lockVal == -1) {
							Integer att = lockSet.get(lkmsg.gid);
							if (att == null || att < lkmsg.attempt) {
								LockMsg l = lockMap.get(lkmsg.gid);
								if (l == null || l.attempt < lkmsg.attempt) {
									if (lockMap.isEmpty())
										lockMsgTimer = getTime();
									lockMap.put(lkmsg.gid, lkmsg);

								}
							}
							for (TreeNode n : root.children) {
								out(outChannelMap.get(n.id)).send(
										new LockMsg(lkmsg.gid, lkmsg.val,
												lkmsg.attempt, n, true));
							}
						} else {
							out(outChannelMap.get(sender)).send(
									new ResponseMsg(self.id, lkmsg.attempt,
											root.parent, false));
						}
					}
				} else {
					Integer att = lockSet.get(lkmsg.gid);
					LockMsg l = lockMap.get(lkmsg.gid);
					if ((att != null && att <= lkmsg.attempt)
							|| (l != null && l.attempt <= lkmsg.attempt)) {
						removeLock(lkmsg.gid);
					}
					for (TreeNode n : root.children) {
						out(outChannelMap.get(n.id)).send(
								new LockMsg(lkmsg.gid, lkmsg.val,
										lkmsg.attempt, n, false));
					}
				}

			} else if (msg instanceof ResponseMsg) {
				ResponseMsg rmsg = (ResponseMsg) msg;
				TreeNode root = rmsg.node;
				if (root.parent == null) {
					if (waiting && rmsg.attempt == attempt) {
						if (!rmsg.accept) {
							//								System.out.println("=== " + self.id + " "
									//										+ "UNLOCK ===");
							if (lockBase < 16)
								lockBase <<= 1;
							reLockTime = getTime()
									+ Helper.random.nextInt(reLockInterval
											* lockBase);
							removeLock(self.id);
							waiting = false;
							for (TreeNode n : root.children) {
								out(outChannelMap.get(n.id))
								.send(
										new LockMsg(self.id,
												view.varMap.size(),
												attempt, n, false));
							}
							//								DCOPApplication app = (DCOPApplication) this.node
							//										.getNetwork().getApplication();
							//								app.numberConflicts++;
						} else {
							acceptSet.add(rmsg.id);
							if (acceptSet.size() >= view.varMap.size()) {
								// ///////////store the stats////////////
								Stats st = new Stats();
								st.gain = view.evaluate(bestSolution)
										- view.evaluate();
								st.varChanged = 0;
								for (Variable v : view.varMap.values())
									if (v.value != bestSolution.get(v.id))
										st.varChanged++;
										st.attempts = attempt - preAttempt;

										int present = getTime();
										st.cycles = present - preCycles;

										st.varLocked = center.getMarkedNodeSize();
										st.maxLockedDistance = center
												.maxdistanceMarkedNode();
										preAttempt = attempt;

										statList.add(st);
										// ///////////////////////////////////////
										lockBase = 1;
										if (self.value != lockVal) {
											//										System.out.println(self.id + " "
											//												+ self.id + " " + self.value
											//												+ "->" + lockVal);
											self.value = lockVal;
											out().broadcast(
													new ValueMsg(self, t + 1));
										}
										waiting = false;
										removeLock(self.id);
										for (TreeNode n : root.children) {
											out(outChannelMap.get(n.id)).send(
													new CommitMsg(self.id, attempt,
															n));
										}
										reLockTime = getTime()
												+ 2
												* (t + 1)
												+ +Helper.random
												.nextInt(2 * (t + 1));
							}
						}
					}
				} else {
					out(outChannelMap.get(root.parent.id)).send(
							new ResponseMsg(rmsg.id, rmsg.attempt,
									root.parent, rmsg.accept));
				}
			} else if (msg instanceof CommitMsg) {
				CommitMsg cmsg = (CommitMsg) msg;
				TreeNode root = cmsg.node;
				for (TreeNode n : root.children) {
					out(outChannelMap.get(n.id)).send(
							new CommitMsg(cmsg.gid, cmsg.attempt, n));
				}
				Integer att = lockSet.get(cmsg.gid);
				if (att == null || att != cmsg.attempt) {
					return;
				}
				if (self.value != lockVal) {
					System.out.println(cmsg.gid + " " + self.id + " "
							+ self.value + "->" + lockVal);
					self.value = lockVal;
					out().broadcast(new ValueMsg(self, t + 1));
				}
				removeLock(cmsg.gid);

//			} else if (msg instanceof TerminateMessage) {
//				break;
			}
		} else {
			if (getTime() == 2 * t + 1) {
				trivial = isTrivial();
			}
			// if (getTime() > 1000 && !lockSet.isEmpty())
			// System.out.println(self.id + " " + lockSet);
			// TODO
			if (!lockMap.isEmpty()
					&& getTime() - lockMsgTimer >= windowsize) {
				// output lockmap size
				nlockReq += lockMap.size();
				// ///////////
				int comp = Integer.MIN_VALUE;
				int id = Integer.MAX_VALUE;
				LockMsg best = null;
				for (LockMsg msg : lockMap.values()) {
					if (msg.val > comp || (msg.val == comp && msg.gid < id)) {
						comp = msg.val;
						id = msg.gid;
						best = msg;
					}
				}
				lockVal = best.node.value;
				lockSet.put(best.gid, best.attempt);
				if (best.node.parent != null) {
					out(outChannelMap.get(best.node.parent.id)).send(
							new ResponseMsg(self.id, best.attempt,
									best.node.parent, true));
				} else {
					acceptSet.add(self.id);
					if (acceptSet.size() >= view.varMap.size()) {
						// ///////////store the stats////////////
						Stats st = new Stats();
						st.gain = view.evaluate(bestSolution)
								- view.evaluate();
						st.varChanged = 0;
						for (Variable v : view.varMap.values()) {
							if (v.value != bestSolution.get(v.id))
								st.varChanged++;
						}
						st.attempts = attempt - preAttempt;

						int present = getTime();
						st.cycles = present - preCycles;

						st.varLocked = center.getMarkedNodeSize();
						st.maxLockedDistance = center
								.maxdistanceMarkedNode();
						preAttempt = attempt;
						statList.add(st);
						// ///////////////////////////////////////
						lockBase = 1;
						if (self.value != lockVal) {
							System.out.println(self.id + " " + self.id
									+ " " + self.value + "->" + lockVal);
							self.value = lockVal;
							out().broadcast(new ValueMsg(self, t + 1));
						}
						waiting = false;
						removeLock(self.id);
						for (TreeNode n : center.children) {
							out(outChannelMap.get(n.id)).send(
									new CommitMsg(self.id, attempt, n));
						}
						reLockTime = getTime() + 2 * (t + 1)
								+ +Helper.random.nextInt(2 * (t + 1));
					}
				}

				for (LockMsg msg : lockMap.values()) {
					if (msg.gid == best.gid)
						continue;
					if (msg.node.parent != null) {
						if (msg.node.value == lockVal) {
							lockSet.put(msg.gid, msg.attempt);
							out(outChannelMap.get(msg.node.parent.id))
							.send(
									new ResponseMsg(self.id,
											msg.attempt,
											msg.node.parent, true));
						} else {
							out(outChannelMap.get(msg.node.parent.id))
							.send(
									new ResponseMsg(self.id,
											msg.attempt,
											msg.node.parent, false));
						}
					} else {
						waiting = false;
						for (TreeNode n : msg.node.children) {
							out(outChannelMap.get(n.id)).send(
									new LockMsg(self.id,
											view.varMap.size(), attempt, n,
											false));
						}
					}
				}
				lockMap.clear();
			}
			setDone(false);
			if (!trivial) {
				if (getTime() > 2 * t + 1 && changed) {
					changed = false;
					checkSolution();
					if (waiting && center != null) {
						// System.out.println("=== " + self.id + " " +
						// "UNLOCK ===");
						removeLock(self.id);
						waiting = false;
						for (TreeNode n : center.children) {
							out(outChannelMap.get(n.id)).send(
									new LockMsg(self.id,
											view.varMap.size(), attempt, n,
											false));
						}
						//							DCOPApplication app = (DCOPApplication) this.node
						//									.getNetwork().getApplication();
						//							app.numberConflicts++;
					}
				}
				if (checkMove()) {
					if (!waiting && lockVal == -1 && getTime() > reLockTime) {
						center = constructTree();
						//							System.out.println(self.id + " " + center.getMarkedNodeSize() + "/"
						//									+ center.getSize());
						acceptSet.clear();
						waiting = true;
						attempt++;
						if (lockMap.isEmpty())
							lockMsgTimer = getTime();
						int gain = view.evaluate(bestSolution)
								- view.evaluate();
						lockMap.put(self.id, new LockMsg(self.id, gain,
								attempt, center, true));
						for (TreeNode n : center.children) {
							out(outChannelMap.get(n.id)).send(
									new LockMsg(self.id, gain, attempt, n,
											true));
						}

					} else if (!waiting && lockVal == -1
							&& getTime() <= reLockTime) {
						//							DCOPApplication app = (DCOPApplication) this.node
						//									.getNetwork().getApplication();
						//							app.wastedCycles++;
					}
				} else if (bestSolution != null
						&& view.evaluate() >= view.evaluate(bestSolution))
					setDone(true);
			} else
				setDone(true);
			//				yield();
		}
	}

	private void checkSolution() {
		for (Variable v : view.varMap.values())
			if (v.fixed && v.value == -1) {
				bestSolution = null;
				return;
			}
		if (view.conList.size() > view.varMap.size() * view.varMap.size() / 4)
			bestSolution = view.branchBoundSolve();
		else
			bestSolution = view.DPOPSolve();
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
		TreeNode root = new TreeNode(self.id, bestSolution.get(self.id), false,
				null);
		if (root.value != view.varMap.get(self.id).value)
			root.mark = true;

		ArrayList<Variable> queue = new ArrayList<Variable>();
		HashMap<Integer, TreeNode> map = new HashMap<Integer, TreeNode>();
		queue.add(self);
		map.put(self.id, root);
		while (!queue.isEmpty()) {
			Variable v = queue.remove(0);
			TreeNode p = map.get(v.id);
			for (Constraint c : v.neighbors) {
				Variable n = c.getNeighbor(v);
				if (!map.containsKey(n.id)) {
					queue.add(n);
					TreeNode child = new TreeNode(n.id, bestSolution.get(n.id),
							n.fixed, p);
					if (v.value != bestSolution.get(v.id)
							|| n.value != bestSolution.get(n.id)) {
						child.mark = true;
						p.mark = true;
					}
					map.put(n.id, child);
				} else {
					TreeNode node = map.get(n.id);
					if (v.value != bestSolution.get(v.id))
						node.mark = true;
				}
			}
		}
		if (!subsetlocking)
			root.markAll();
		return root;
	}

	private boolean checkMove() {
		if (bestSolution == null)
			return false;
		for (Variable v : view.varMap.values()) {
			if (!bestSolution.containsKey(v.id))
				return false;
		}
		int current = view.evaluate();
		int best = view.evaluate(bestSolution);
		if (current >= best) {
			bestSolution = view.getSolution();
			return false;
		} else
			return true;
	}

	private boolean isTrivial() {
		HashMap<Integer, Integer> minDis = new HashMap<Integer, Integer>();
		int maxId = 0;
		for (Variable v : view.varMap.values())
			if (v.id > maxId)
				maxId = v.id;
				maxId++;
				for (Variable v : view.varMap.values()) {
					ArrayList<Variable> queue = new ArrayList<Variable>();
					queue.add(v);
					minDis.put(v.id * maxId + v.id, 0);
					HashSet<Integer> visited = new HashSet<Integer>();
					visited.add(v.id);
					while (!queue.isEmpty()) {
						Variable var = queue.remove(0);
						for (Constraint c : var.neighbors) {
							Variable n = c.getNeighbor(var);
							if (!visited.contains(n.id)) {
								queue.add(n);
								int depth = minDis.get(v.id * maxId + var.id);
								visited.add(n.id);
								minDis.put(v.id * maxId + n.id, depth + 1);
							}
						}
					}
				}

				int minD = 0;
				for (Variable v : view.varMap.values()) {
					int m = minDis.get(self.id * maxId + v.id);
					if (m > minD)
						minD = m;
				}

				for (Constraint c : self.neighbors) {
					Variable n = c.getNeighbor(self);
					int d = 0;
					for (Variable v : view.varMap.values()) {
						int m = minDis.get(n.id * maxId + v.id);
						if (m > d)
							d = m;
					}
					if (d < minD)
						return true;
					// if (d == minD && self.id > n.id)
					// return true;
				}
				return false;
	}

	public String getText() {
		String val = "";
		for (Variable v : view.varMap.values()) {
			val += v.id + "  " + v.value + "/"
					+ (bestSolution != null ? bestSolution.get(v.id) : "NA")
					+ (v.fixed ? "F" : "") + "\n";
		}
		val += "LockSet: ";
		for (Integer i : lockSet.keySet()) {
			val += i + " ";
		}
		val += "\n";

		val += "AcceptSet: ";
		for (Integer i : acceptSet) {
			val += i + " ";
		}
		val += "\n";

		return val + "ID: " + self.id + "\nVal: " + self.value + "\nLockVal: "
		+ lockVal + "\nNextLock: " + reLockTime
		+ (isStable() ? "\nDONE" : "");
	}

}
