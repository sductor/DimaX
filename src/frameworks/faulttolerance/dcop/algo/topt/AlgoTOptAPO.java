package frameworks.faulttolerance.dcop.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.algo.BasicAlgorithm;
import frameworks.faulttolerance.dcop.algo.LockingBasicAlgorithm;
import frameworks.faulttolerance.dcop.algo.TerminateMessage;
import frameworks.faulttolerance.dcop.daj.Channel;
import frameworks.faulttolerance.dcop.daj.Message;
import frameworks.faulttolerance.dcop.daj.Program;
<<<<<<< HEAD
<<<<<<< HEAD
import frameworks.faulttolerance.dcop.dcop.AbstractConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.AbstractVariable;
=======
=======
>>>>>>> dcopX
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
import frameworks.faulttolerance.dcop.exec.DCOPApplication;
import frameworks.faulttolerance.dcop.exec.Stats;
import frameworks.negotiation.rationality.AgentState;

public class AlgoTOptAPO extends LockingBasicAlgorithm {

	int t;
	HashMap<Integer, Integer> bestSolution;
	TreeNode center;
	public boolean trivial;

<<<<<<< HEAD
<<<<<<< HEAD
	public AlgoTOptAPO(AbstractVariable v, int tt) {
=======
	public AlgoTOptAPO(ReplicationVariable v, int tt) {
>>>>>>> dcopX
=======
	public AlgoTOptAPO(ReplicationVariable v, int tt) {
>>>>>>> dcopX
		super(v, true, 2);
		t = tt;
		init();
	}

<<<<<<< HEAD
<<<<<<< HEAD
	public AlgoTOptAPO(AbstractVariable v, int tt, boolean s, int ws) {
=======
	public AlgoTOptAPO(ReplicationVariable v, int tt, boolean s, int ws) {
>>>>>>> dcopX
=======
	public AlgoTOptAPO(ReplicationVariable v, int tt, boolean s, int ws) {
>>>>>>> dcopX
		super(v, s, ws);
		t = tt;
		init();
	}

	protected void init() {
		self.setValue(self.getInitialValue());
		bestSolution = null;
		center = null;
		trivial = false;
	}

	@Override
	protected void main() {
		// TODO Auto-generated method stub

		for (int i = 0; i < in().getSize(); i++) {
			Program p = ((Channel) in(i)).getSender().getProgram();
			if (p instanceof BasicAlgorithm) {
				int id = ((BasicAlgorithm) p).getID();
				inChannelMap.put(id, i);
			}
		}

		for (int i = 0; i < out().getSize(); i++) {
			Program p = ((Channel) out(i)).getReceiver().getProgram();
			if (p instanceof BasicAlgorithm) {
				int id = ((BasicAlgorithm) p).getID();
				outChannelMap.put(id, i);
			}
		}

		if (t > 0)
			out().broadcast(new LocalConstraintMsg(self, t));
		out().broadcast(new ValueMsg(self, t + 1));
		changed = true;

		// HashMap<Integer, Integer> valTTLMap = new HashMap<Integer,
		// Integer>();
		HashMap<Integer, Integer> conTTLMap = new HashMap<Integer, Integer>();

		while (true) {
			// if (self.id == 90)
			// System.out.println("DEBUG");
			int index = in().select(1);
			if (index != -1) {

				done = false;

				Message msg = in(index).receive(1);
				if (msg == null)
					yield();

				int sender = -1;
				Program p = ((Channel) in(index)).getSender().getProgram();
				if (p instanceof BasicAlgorithm) {
					sender = ((BasicAlgorithm) p).getID();
				}

				if (msg instanceof ValueMsg) {
					ValueMsg vmsg = (ValueMsg) msg;
<<<<<<< HEAD
<<<<<<< HEAD
					AbstractVariable v = view.varMap.get(vmsg.id);
=======
					ReplicationVariable v = view.varMap.get(vmsg.id);
>>>>>>> dcopX
=======
					ReplicationVariable v = view.varMap.get(vmsg.id);
>>>>>>> dcopX
					assert v != null;
					if (vmsg.ttl > 1)
						out().broadcast(vmsg.forward());
					if (v.getValue() != vmsg.value) {
						v.setValue(vmsg.value);
						if (v.fixed)
							changed = true;
					}
				} else if (msg instanceof LocalConstraintMsg) {
					LocalConstraintMsg lmsg = (LocalConstraintMsg) msg;
					Integer lastTTL = conTTLMap.get(lmsg.id);
					if (lastTTL == null) {
						conTTLMap.put(lmsg.id, lmsg.ttl);
<<<<<<< HEAD
<<<<<<< HEAD
						AbstractVariable v = view.varMap.get(lmsg.id);
						if (v == null) {
							v = new AbstractVariable(lmsg.id, lmsg.domain, view);
=======
						ReplicationVariable v = view.varMap.get(lmsg.id);
						if (v == null) {
							v = DCOPFactory.constructVariable(lmsg.id, lmsg.domain, lmsg.state, view.getSocialWelfare());
>>>>>>> dcopX
=======
						ReplicationVariable v = view.varMap.get(lmsg.id);
						if (v == null) {
							v = DCOPFactory.constructVariable(lmsg.id, lmsg.domain, lmsg.state, view.getSocialWelfare());
>>>>>>> dcopX
							view.varMap.put(v.id, v);
						}
						v.fixed = false;
						for (double[] enc : lmsg.data) {
							if (enc[0] == v.id) {
<<<<<<< HEAD
<<<<<<< HEAD
								AbstractVariable n = view.varMap.get((int) enc[2]);
								if (n == null) {
									n = new AbstractVariable((int) enc[2],(int) enc[3], view);
=======
=======
>>>>>>> dcopX
								ReplicationVariable n = view.varMap.get((int) enc[2]);
								if (n == null) {
									AgentState s = null;
									if (!DCOPFactory.isClassical())
										s = lmsg.dataStates.get((int)enc[2]);
									n = DCOPFactory.constructVariable((int) enc[2],(int) enc[3], s, view.getSocialWelfare());
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
									if (lmsg.ttl <= 1)
										n.fixed = true;
									view.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
<<<<<<< HEAD
<<<<<<< HEAD
									AbstractConstraint c = new AbstractConstraint(v, n);
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(v, n);
>>>>>>> dcopX
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(v, n);
>>>>>>> dcopX
									view.conList.add(c);
									if (DCOPFactory.isClassical()){
										for (int i = 0; i < c.d1; i++)
											for (int j = 0; j < c.d2; j++) {
												((CPUFreeConstraint)c).f[i][j] = enc[4 + i * c.d2 + j];
											}
										((CPUFreeConstraint)c).cache();
									}
								}
							} else {
<<<<<<< HEAD
<<<<<<< HEAD
								AbstractVariable n = view.varMap.get((int) enc[0]);
								if (n == null) {
									n = new AbstractVariable((int)enc[0], (int)enc[1], view);
=======
=======
>>>>>>> dcopX
								ReplicationVariable n = view.varMap.get((int) enc[0]);
								if (n == null) {
									AgentState s = null;
									if (!DCOPFactory.isClassical())
										s = lmsg.dataStates.get((int)enc[0]);
									n = DCOPFactory.constructVariable((int)enc[0], (int)enc[1], s, view.getSocialWelfare());
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
									if (lmsg.ttl <= 1)
										n.fixed = true;
									view.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
<<<<<<< HEAD
<<<<<<< HEAD
									AbstractConstraint c = new AbstractConstraint(n, v);
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(n, v);
>>>>>>> dcopX
=======
									MemFreeConstraint c = DCOPFactory.constructConstraint(n, v);
>>>>>>> dcopX
									view.conList.add(c);
									if (DCOPFactory.isClassical()){
										for (int i = 0; i < c.d1; i++)
											for (int j = 0; j < c.d2; j++) {
												((CPUFreeConstraint)c).f[i][j] = enc[4 + i * c.d2 + j];
											}
										((CPUFreeConstraint)c).cache();
									}
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
								DCOPApplication app = (DCOPApplication) this.node
										.getNetwork().getApplication();
								app.numberConflicts++;
							} else {
								acceptSet.add(rmsg.id);
								if (acceptSet.size() >= view.varMap.size()) {
									// ///////////store the stats////////////
									Stats st = new Stats();
									st.gain = view.evaluate(bestSolution)
											- view.evaluate();
									st.varChanged = 0;
<<<<<<< HEAD
<<<<<<< HEAD
									for (AbstractVariable v : view.varMap.values())
										if (v.value != bestSolution.get(v.id))
=======
									for (ReplicationVariable v : view.varMap.values())
										if (v.getValue() != bestSolution.get(v.id))
>>>>>>> dcopX
=======
									for (ReplicationVariable v : view.varMap.values())
										if (v.getValue() != bestSolution.get(v.id))
>>>>>>> dcopX
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
											if (self.getValue() != lockVal) {
												//										System.out.println(self.id + " "
												//												+ self.id + " " + self.value
												//												+ "->" + lockVal);
												self.setValue(lockVal);
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
						continue;
					}
					if (self.getValue() != lockVal) {
						System.out.println(cmsg.gid + " " + self.id + " "
								+ self.getValue() + "->" + lockVal);
						self.setValue(lockVal);
						out().broadcast(new ValueMsg(self, t + 1));
					}
					removeLock(cmsg.gid);

				} else if (msg instanceof TerminateMessage) {
					break;
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
					double comp = Double.MIN_VALUE;
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
<<<<<<< HEAD
<<<<<<< HEAD
							for (AbstractVariable v : view.varMap.values()) {
								if (v.value != bestSolution.get(v.id))
=======
							for (ReplicationVariable v : view.varMap.values()) {
								if (v.getValue() != bestSolution.get(v.id))
>>>>>>> dcopX
=======
							for (ReplicationVariable v : view.varMap.values()) {
								if (v.getValue() != bestSolution.get(v.id))
>>>>>>> dcopX
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
							if (self.getValue() != lockVal) {
								System.out.println(self.id + " " + self.id
										+ " " + self.getValue() + "->" + lockVal);
								self.setValue(lockVal);
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
				done = false;
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
							DCOPApplication app = (DCOPApplication) this.node
									.getNetwork().getApplication();
							app.numberConflicts++;
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
							double gain = view.evaluate(bestSolution)
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
							DCOPApplication app = (DCOPApplication) this.node
									.getNetwork().getApplication();
							app.wastedCycles++;
						}
					} else if (bestSolution != null
							&& view.evaluate() >= view.evaluate(bestSolution))
						done = true;
				} else
					done = true;
				yield();
			}
		}
	}

	private void checkSolution() {
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values())
			if (v.fixed && v.value == -1) {
=======
		for (ReplicationVariable v : view.varMap.values())
			if (v.fixed && v.getValue() == -1) {
>>>>>>> dcopX
=======
		for (ReplicationVariable v : view.varMap.values())
			if (v.fixed && v.getValue() == -1) {
>>>>>>> dcopX
				bestSolution = null;
				return;
			}
		bestSolution = view.solve();
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
		if (root.value != view.varMap.get(self.id).getValue())
			root.mark = true;

<<<<<<< HEAD
<<<<<<< HEAD
		ArrayList<AbstractVariable> queue = new ArrayList<AbstractVariable>();
=======
		ArrayList<ReplicationVariable> queue = new ArrayList<ReplicationVariable>();
>>>>>>> dcopX
=======
		ArrayList<ReplicationVariable> queue = new ArrayList<ReplicationVariable>();
>>>>>>> dcopX
		HashMap<Integer, TreeNode> map = new HashMap<Integer, TreeNode>();
		queue.add(self);
		map.put(self.id, root);
		while (!queue.isEmpty()) {
<<<<<<< HEAD
<<<<<<< HEAD
			AbstractVariable v = queue.remove(0);
			TreeNode p = map.get(v.id);
			for (AbstractConstraint c : v.neighbors) {
				AbstractVariable n = c.getNeighbor(v);
=======
=======
>>>>>>> dcopX
			ReplicationVariable v = queue.remove(0);
			TreeNode p = map.get(v.id);
			for (MemFreeConstraint c : v.getNeighbors()) {
				ReplicationVariable n = c.getNeighbor(v);
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
				if (!map.containsKey(n.id)) {
					queue.add(n);
					TreeNode child = new TreeNode(n.id, bestSolution.get(n.id),
							n.fixed, p);
					if (v.getValue() != bestSolution.get(v.id)
							|| n.getValue() != bestSolution.get(n.id)) {
						child.mark = true;
						p.mark = true;
					}
					map.put(n.id, child);
				} else {
					TreeNode node = map.get(n.id);
					if (v.getValue() != bestSolution.get(v.id))
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
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values()) {
=======
		for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
=======
		for (ReplicationVariable v : view.varMap.values()) {
>>>>>>> dcopX
			if (!bestSolution.containsKey(v.id))
				return false;
		}
		double current = view.evaluate();
		double best = view.evaluate(bestSolution);
		if (current >= best) {
			bestSolution = view.getSolution();
			return false;
		} else
			return true;
	}

	private boolean isTrivial() {
		HashMap<Integer, Integer> minDis = new HashMap<Integer, Integer>();
		int maxId = 0;
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values())
			if (v.id > maxId)
				maxId = v.id;
		maxId++;
		for (AbstractVariable v : view.varMap.values()) {
			ArrayList<AbstractVariable> queue = new ArrayList<AbstractVariable>();
			queue.add(v);
			minDis.put(v.id * maxId + v.id, 0);
			HashSet<Integer> visited = new HashSet<Integer>();
			visited.add(v.id);
			while (!queue.isEmpty()) {
				AbstractVariable var = queue.remove(0);
				for (AbstractConstraint c : var.neighbors) {
					AbstractVariable n = c.getNeighbor(var);
					if (!visited.contains(n.id)) {
						queue.add(n);
						int depth = minDis.get(v.id * maxId + var.id);
						visited.add(n.id);
						minDis.put(v.id * maxId + n.id, depth + 1);
=======
=======
>>>>>>> dcopX
		for (ReplicationVariable v : view.varMap.values())
			if (v.id > maxId)
				maxId = v.id;
				maxId++;
				for (ReplicationVariable v : view.varMap.values()) {
					ArrayList<ReplicationVariable> queue = new ArrayList<ReplicationVariable>();
					queue.add(v);
					minDis.put(v.id * maxId + v.id, 0);
					HashSet<Integer> visited = new HashSet<Integer>();
					visited.add(v.id);
					while (!queue.isEmpty()) {
						ReplicationVariable var = queue.remove(0);
						for (MemFreeConstraint c : var.getNeighbors()) {
							ReplicationVariable n = c.getNeighbor(var);
							if (!visited.contains(n.id)) {
								queue.add(n);
								int depth = minDis.get(v.id * maxId + var.id);
								visited.add(n.id);
								minDis.put(v.id * maxId + n.id, depth + 1);
							}
						}
<<<<<<< HEAD
>>>>>>> dcopX
					}
				}

<<<<<<< HEAD
		int minD = 0;
		for (AbstractVariable v : view.varMap.values()) {
			int m = minDis.get(self.id * maxId + v.id);
			if (m > minD)
				minD = m;
		}

		for (AbstractConstraint c : self.neighbors) {
			AbstractVariable n = c.getNeighbor(self);
			int d = 0;
			for (AbstractVariable v : view.varMap.values()) {
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
=======
=======
					}
				}

>>>>>>> dcopX
				int minD = 0;
				for (ReplicationVariable v : view.varMap.values()) {
					int m = minDis.get(self.id * maxId + v.id);
					if (m > minD)
						minD = m;
				}

				for (MemFreeConstraint c : self.getNeighbors()) {
					ReplicationVariable n = c.getNeighbor(self);
					int d = 0;
					for (ReplicationVariable v : view.varMap.values()) {
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
<<<<<<< HEAD
>>>>>>> dcopX
=======
>>>>>>> dcopX
	}

	public String getText() {
		String val = "";
<<<<<<< HEAD
<<<<<<< HEAD
		for (AbstractVariable v : view.varMap.values()) {
			val += v.id + "  " + v.value + "/"
=======
		for (ReplicationVariable v : view.varMap.values()) {
			val += v.id + "  " + v.getValue() + "/"
>>>>>>> dcopX
=======
		for (ReplicationVariable v : view.varMap.values()) {
			val += v.id + "  " + v.getValue() + "/"
>>>>>>> dcopX
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

		return val + "ID: " + self.id + "\nVal: " + self.getValue() + "\nLockVal: "
		+ lockVal + "\nNextLock: " + reLockTime
		+ (done ? "\nDONE" : "");
	}
}
