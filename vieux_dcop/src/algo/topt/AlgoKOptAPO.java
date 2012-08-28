package vieux.src.algo.topt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import vieux.src.algo.BasicAlgorithm;
import vieux.src.algo.LockingBasicAlgorithm;
import vieux.src.algo.TerminateMessage;
import vieux.src.daj.Channel;
import vieux.src.daj.Message;
import vieux.src.daj.Program;
import vieux.src.dcop.Constraint;
import vieux.src.dcop.Helper;
import vieux.src.dcop.Variable;
import vieux.src.exec.DCOPApplication;
import vieux.src.exec.Stats;


public class AlgoKOptAPO extends LockingBasicAlgorithm {

	int k;

	HashSet<Integer> activeSet;	
	public HashMap<Integer, DPOPTreeNode> localTreeMap;
	HashSet<HashSet<Integer>> localTreeSet;
	DPOPTreeNode lockingNode;
	int gIdCounter;

	public AlgoKOptAPO(Variable v, int kk) {
		super(v, true, kk);
		k = kk;
		init();
	}

	public AlgoKOptAPO(Variable v, int kk, boolean s, int ws) {
		super(v, s, ws);
		k = kk;
		init();
	}

	protected void init() {
		self.value = Helper.random.nextInt(self.domain);
		localTreeSet = new HashSet<HashSet<Integer>>();
		activeSet = new HashSet<Integer>();
		localTreeMap = new HashMap<Integer, DPOPTreeNode>();
		gIdCounter = 0;
	}

	private void unlock() {
		if (lockBase < 16)
			lockBase <<= 1;
		reLockTime = getTime()
				+ Helper.random.nextInt(reLockInterval * lockBase);
		removeLock(self.id);
		waiting = false;
		for (TreeNode n : lockingNode.children) {
			out(outChannelMap.get(n.id)).send(
					new LockMsg(lockingNode.id, lockingNode.reward, attempt, n,
							false));
		}
		lockingNode = null;
		DCOPApplication app = (DCOPApplication) this.node.getNetwork()
				.getApplication();
		app.numberConflicts++;
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

		if (k > 1)
			out().broadcast(new LocalConstraintMsg(self, k / 2));
		changed = true;
		out().broadcast(new ValueMsg(self, k / 2 + 1));

		// HashMap<Integer, Integer> valTTLMap = new HashMap<Integer,
		// Integer>();
		HashMap<Integer, Integer> conTTLMap = new HashMap<Integer, Integer>();

		while (true) {
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
					Variable v = view.varMap.get(vmsg.id);
					assert v != null;
					if (vmsg.ttl > 1)
						out().broadcast(vmsg.forward());
					if (v.value != vmsg.value) {
						v.value = vmsg.value;
						// TODO
						if (waiting) {
							TreeNode node = lockingNode.find(v.id);
							if (node != null)
								unlock();
						}

						for (DPOPTreeNode root : localTreeMap.values()) {
							TreeNode node = root.find(v.id);
							if (node != null && node.fixed) {
								computeOpt(root);
								if (checkGroup(root))
									activeSet.add(root.gid);
								else
									activeSet.remove(root.gid);
							}
						}
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
						if (waiting && lockingNode == root
								&& rmsg.attempt == attempt) {
							if (!rmsg.accept) {
								unlock();
							} else {
								acceptSet.add(rmsg.id);
								if (acceptSet.size() >= lockingNode.getSize()) {
									/////////////store the stats////////////
									Stats st = new Stats();
									int pregain = view.evaluate();
									view.backup();
									for (Variable v : view.varMap.values()) {
										TreeNode node = lockingNode.find(v.id);
										if (node != null)
											v.value=node.value;
									}
									st.gain = view.evaluate() - pregain; 
									view.recover();
																																												
									st.varChanged = 0;
									for (Variable v : view.varMap.values()) {
										TreeNode node = lockingNode.find(v.id);
										if (node != null && v.value != node.value)
									    	st.varChanged++;
									}
									st.attempts = attempt - preAttempt;
									int present = getTime();
									st.cycles = present - preCycles; 
									st.varLocked = lockingNode.getMarkedNodeSize();
									st.maxLockedDistance = lockingNode.maxdistanceMarkedNode();
									preAttempt = attempt;
									statList.add(st);																
									/////////////////////////////////////////
							
									lockBase = 1;
									if (self.value != lockVal) {
										System.out.println(self.id + " "
												+ self.id + " " + self.value
												+ "->" + lockVal);
										self.value = lockVal;
										out().broadcast(
												new ValueMsg(self, k / 2 + 1));
									}
									waiting = false;
									removeLock(self.id);
									for (TreeNode n : root.children) {
										out(outChannelMap.get(n.id)).send(
												new CommitMsg(self.id, attempt,
														n));
									}
									reLockTime = getTime() + k + 1
											+ +Helper.random.nextInt(k + 1);
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
					if (self.value != lockVal) {
						System.out.println(cmsg.gid + " " + self.id + " "
								+ self.value + "->" + lockVal);
						self.value = lockVal;
						out().broadcast(new ValueMsg(self, k / 2 + 1));
					}
					removeLock(cmsg.gid);
				} else if (msg instanceof TerminateMessage) {
					break;
				}
			} else {
				if (this.getTime() > k + 1 && changed) {
					changed = false;
					constructTrees();
					for (DPOPTreeNode root : localTreeMap.values()) {
						computeOpt(root);
						if (checkGroup(root))
							activeSet.add(root.gid);
						else
							activeSet.remove(root.gid);
					}
				}

				if (!lockMap.isEmpty()
						&& getTime() - lockMsgTimer >= windowsize) {
					//output lockmap size
					nlockReq += lockMap.size();
					/////////////	
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
						if (acceptSet.size() >= lockingNode.getSize()) {
							/////////////store the stats////////////
							Stats st = new Stats();
							int pregain = view.evaluate();
							view.backup();
							for (Variable v : view.varMap.values()) {
								TreeNode node = lockingNode.find(v.id);
								if (node != null)
									v.value=node.value;
							}
							st.gain = view.evaluate() - pregain; 
							view.recover();
																																										
							st.varChanged = 0;
							for (Variable v : view.varMap.values()) {
								TreeNode node = lockingNode.find(v.id);
								if (node != null && v.value != node.value)
							    	st.varChanged++;
							}
							st.attempts = attempt - preAttempt;
							
							int present = getTime();
							st.cycles = present - preCycles; 
							
							st.varLocked = lockingNode.getMarkedNodeSize();
							st.maxLockedDistance = lockingNode.maxdistanceMarkedNode();
							preAttempt = attempt;
							statList.add(st);																
							/////////////////////////////////////////					
							lockBase = 1;
							if (self.value != lockVal) {
								System.out.println(self.id + " " + self.id
										+ " " + self.value + "->" + lockVal);
								self.value = lockVal;
								out().broadcast(new ValueMsg(self, k / 2 + 1));
							}
							waiting = false;
							removeLock(self.id);
							for (TreeNode n : lockingNode.children) {
								out(outChannelMap.get(n.id)).send(
										new CommitMsg(self.id, attempt, n));
							}
							reLockTime = getTime() + k + 1
									+ +Helper.random.nextInt(k + 1);
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
							lockingNode = null;
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

				if (!waiting && getTime() > reLockTime && !activeSet.isEmpty()) {
					if (getTime() > k + 1) {
						// double p = 1.0 / activeSet.size();
						done = true;
						DPOPTreeNode best = null;
						int gain = 0;
						for (DPOPTreeNode root : localTreeMap.values()) {
							if (activeSet.contains(root.gid)) {
								if (checkGroup(root)) {
									if (root.reward > gain) {
										gain = root.reward;
										best = root;
									}
								} else
									activeSet.remove(root.gid);
							}
						}
						if (best != null) {
							done = false;
							lock(best);
						}
					}
				} else if (!waiting && !lockSet.isEmpty()
						&& getTime() <= reLockTime && !activeSet.isEmpty()) {
					DCOPApplication app = (DCOPApplication) this.node
							.getNetwork().getApplication();
					app.wastedCycles++;
				}
				if (this.getTime() > k + 1 && activeSet.isEmpty())
					done = true;
				yield();
			}
		}
	}

	private boolean checkGroup(DPOPTreeNode root) {
		int orgReward = view.evaluate();
		view.backup();
		ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			DPOPTreeNode p = queue.remove(0);
			view.varMap.get(p.id).value = p.value;
			for (TreeNode n : p.children) {
				DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		int newReward = view.evaluate();
		view.recover();
		root.reward = newReward - orgReward;
		return newReward > orgReward;
	}

	private boolean computeOpt(DPOPTreeNode root) {
		for (Variable v : view.varMap.values())
			v.fixed = true;
		ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		queue.add(root);
		while (!queue.isEmpty()) {
			DPOPTreeNode p = queue.remove(0);
			view.varMap.get(p.id).fixed = p.fixed;
			for (TreeNode n : p.children) {
				DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		HashMap<Integer, Integer> sol = view.DPOPSolve();
		HashSet<Integer> set = new HashSet<Integer>();
		for (Variable v : view.varMap.values()) {
			if (v.value != sol.get(v.id)) {
				set.add(v.id);
				for (Constraint c : v.neighbors) {
					set.add(c.getNeighbor(v).id);
				}
			}
		}
		queue.add(root);
		boolean f = false;
		while (!queue.isEmpty()) {
			DPOPTreeNode p = queue.remove(0);
			// if (set.contains(p.id))
			p.mark = true;
			if (p.value != sol.get(p.id)) {
				f = true;
			}
			p.value = sol.get(p.id);
			for (TreeNode n : p.children) {
				DPOPTreeNode dn = (DPOPTreeNode) n;
				queue.add(dn);
			}
		}
		
		if (!subsetlocking)
			root.markAll();
		return f;
	}
	private void lock(DPOPTreeNode root) {
		attempt++;
		acceptSet.clear();
		lockingNode = root;
		waiting = true;

		if (lockMap.isEmpty())
			lockMsgTimer = getTime();
		lockMap.put(self.id, new LockMsg(self.id, root.reward, attempt,
				lockingNode, true));
		for (TreeNode n : root.children) {
			out(outChannelMap.get(n.id)).send(
					new LockMsg(self.id, root.reward, attempt, n, true));
		}

	}

	private void constructTrees() {
		// TODO Auto-generated method stub
		HashMap<Integer, Integer> minDis = new HashMap<Integer, Integer>();
		int maxId = 0;
		for (Variable v : view.varMap.values())
			if (v.id > maxId)
				maxId = v.id;
		maxId++;
		for (Variable v : view.varMap.values()) {
			if (v.fixed)
				continue;
			ArrayList<Variable> queue = new ArrayList<Variable>();
			queue.add(v);
			minDis.put(v.id * maxId + v.id, 0);
			HashSet<Integer> visited = new HashSet<Integer>();
			visited.add(v.id);
			while (!queue.isEmpty()) {
				Variable var = queue.remove(0);
				for (Constraint c : var.neighbors) {
					Variable n = c.getNeighbor(var);
					if (n.fixed)
						continue;
					if (!visited.contains(n.id)) {
						queue.add(n);
						int depth = minDis.get(v.id * maxId + var.id);
						visited.add(n.id);
						minDis.put(v.id * maxId + n.id, depth + 1);
					}
				}
			}
		}

		HashSet<Integer> pSet = new HashSet<Integer>();
		HashSet<Integer> cSet = new HashSet<Integer>();
		for (Constraint c : self.neighbors) {
			Variable n = c.getNeighbor(self);
			if (!n.fixed)
				cSet.add(n.id);
		}
		this.enumerate(pSet, cSet, minDis, maxId);
	}

	private void enumerate(HashSet<Integer> pSet, HashSet<Integer> cSet,
			HashMap<Integer, Integer> minDis, int maxId) {
		if (pSet.size() == k - 1) {
			boolean f = true;
			int d = 0;
			for (Integer i : pSet) {
				int dis = minDis.get(self.id * maxId + i);
				if (dis > d)
					d = dis;
			}

			for (Integer i : pSet) {
				int tmp = minDis.get(i * maxId + self.id);
				for (Integer j : pSet) {
					if (j == i)
						continue;
					int dis = minDis.get(i * maxId + j);
					if (dis > tmp)
						tmp = dis;
				}
				if (tmp < d || (tmp == d && i < self.id)) {
					f = false;
					break;
				}
			}
			if (f) {
				for (HashSet<Integer> tSet : localTreeSet) {
					if (tSet.containsAll(pSet))
						return;
				}
				HashSet<Integer> visited = new HashSet<Integer>();
				DPOPTreeNode root = new DPOPTreeNode(self.id,
						(gIdCounter << 12) + self.id, 0, false, null);
				visited.add(self.id);
				ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
				queue.add(root);
				int count = 0;
				while (!queue.isEmpty()) {
					DPOPTreeNode node = queue.remove(0);
					Variable v = view.varMap.get(node.id);
					for (Constraint c : v.neighbors) {
						Variable n = c.getNeighbor(v);
						if (visited.contains(n.id))
							continue;
						visited.add(n.id);
						if (!pSet.contains(n.id)) {
							DPOPTreeNode child = new DPOPTreeNode(n.id,
									node.gid, n.value, true, node);
						} else {
							DPOPTreeNode child = new DPOPTreeNode(n.id,
									node.gid, n.value, false, node);
							count++;
							queue.add(child);
						}
					}
				}
				localTreeMap.put(root.gid, root);
				HashSet<Integer> treeSet = new HashSet<Integer>();
				treeSet.addAll(pSet);
				localTreeSet.add(treeSet);
				gIdCounter++;
			}
			return;
		}

		// int max = 0;
		// for (Integer i : pSet)
		// if (max < i)
		// max = i;

		int[] list = new int[cSet.size()];
		{
			int j = 0;
			for (Integer i : cSet) {
				list[j] = i;
				j++;
			}
		}

		for (int i = 0; i < list.length; i++) {
			int j = list[i];
			if (j == self.id)
				continue;
			pSet.add(j);
			HashSet<Integer> add = new HashSet<Integer>();
			Variable v = view.getVar(j);
			for (Constraint c : v.neighbors) {
				Variable n = c.getNeighbor(v);
				if (!n.fixed && !pSet.contains(n.id) && !cSet.contains(n.id))
					add.add(n.id);
			}
			cSet.addAll(add);
			cSet.remove(j);
			this.enumerate(pSet, cSet, minDis, maxId);
			cSet.removeAll(add);
			pSet.remove(j);
		}
	}

	public String getText() {
		String val = "";
		for (Variable v : view.varMap.values()) {
			val += v.id + "  " + v.value + " " + (v.fixed ? "F" : "") + "\n";
		}
		val += "LockSet: ";
		for (Integer i : lockSet.values()) {
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
				+ (done ? "\nDONE" : "");
	}
}
