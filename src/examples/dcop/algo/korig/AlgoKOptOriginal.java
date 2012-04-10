package examples.dcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import examples.dcop.algo.BasicAlgorithm;
import examples.dcop.algo.TerminateMessage;
import examples.dcop.daj.Channel;
import examples.dcop.daj.DcopMessage;
import examples.dcop.daj.Program;
import examples.dcop.dcop.Constraint;
import examples.dcop.dcop.Graph;
import examples.dcop.dcop.Helper;
import examples.dcop.dcop.Variable;

public class AlgoKOptOriginal extends BasicAlgorithm {

	int k;

	HashMap<Integer, LocalInfo> localInfoMap;
	HashMap<Integer, GainInfo> gainInfoMap;
	HashMap<Integer, CommitInfo> commitInfoMap;

	HashSet<Integer> localInfoSet;
	int localInfoCounter = 0;
	HashSet<Integer> gainInfoSet;
	int gainInfoCounter = 0;
	HashSet<Integer> commitInfoSet;
	int commitInfoCounter = 0;

	ArrayList<DcopMessage> buffer;

	int state;

	int sync;

	Graph korigView;
	Variable korigSelf;

	public AlgoKOptOriginal(Variable v, int kk) {
		super(v);
		k = kk;
		self.value = Helper.random.nextInt(self.domain);
		init();
	}

	private void init() {
		if (self.id == 0)
			System.out.println("STARTOVER");
		buffer = new ArrayList<DcopMessage>();
		localInfoMap = new HashMap<Integer, LocalInfo>();
		gainInfoMap = new HashMap<Integer, GainInfo>();
		commitInfoMap = new HashMap<Integer, CommitInfo>();
		localInfoSet = new HashSet<Integer>();
		gainInfoSet = new HashSet<Integer>();
		commitInfoSet = new HashSet<Integer>();
		localInfoCounter = 0;
		gainInfoCounter = 0;
		commitInfoCounter = 0;
		state = 0;
		for (Constraint c : self.neighbors) {
			c.getNeighbor(self).value = -1;
		}
		korigView = new Graph();
		for (Variable v : view.varMap.values()) {
			korigView.varMap.put(v.id, new Variable(v.id, v.domain, korigView));
		}
		korigSelf = korigView.varMap.get(self.id);
		for (Constraint c : view.conList) {
			Constraint cc = new Constraint(korigView.getVar(c.first.id),
					korigView.getVar(c.second.id));
			korigView.conList.add(cc);
			for (int i = 0; i < c.d1; i++)
				for (int j = 0; j < c.d2; j++)
					cc.f[i][j] = c.f[i][j];
		}
		sync = 0;
	}

	private void processMsg(DcopMessage msg) {
		if (msg instanceof KorigLocalMsg) {
			KorigLocalMsg lmsg = (KorigLocalMsg) msg;
			if (!localInfoSet.contains(lmsg.id)) {
				localInfoSet.add(lmsg.id);
				for (LocalInfo l : lmsg.map.values())
					localInfoMap.put(l.id, l);
						if (localInfoSet.size() == self.neighbors.size()) {
							localInfoSet.clear();
							localInfoCounter++;
							if (localInfoCounter < k / 2 + 1)
								out().broadcast(
										new KorigLocalMsg(self.id, localInfoMap));
						}
			} else
				buffer.add(msg);

		} else if (msg instanceof KorigGainMsg) {
			KorigGainMsg gmsg = (KorigGainMsg) msg;
			if (!gainInfoSet.contains(gmsg.id)) {
				gainInfoSet.add(gmsg.id);
				for (GainInfo g : gmsg.map.values())
					gainInfoMap.put(g.id, g);
						if (gainInfoSet.size() == self.neighbors.size()) {
							gainInfoSet.clear();
							gainInfoCounter++;
							if (gainInfoCounter < k / 2 + 1)
								out().broadcast(new KorigGainMsg(self.id, gainInfoMap));
						}
			} else
				buffer.add(msg);

		} else if (msg instanceof KorigCommitMsg) {
			KorigCommitMsg cmsg = (KorigCommitMsg) msg;
			if (!commitInfoSet.contains(cmsg.id)) {
				commitInfoSet.add(cmsg.id);
				for (CommitInfo g : cmsg.map.values())
					commitInfoMap.put(g.id, g);
						if (commitInfoSet.size() == self.neighbors.size()) {
							commitInfoSet.clear();
							commitInfoCounter++;
							if (commitInfoCounter < k + 1)
								out().broadcast(
										new KorigCommitMsg(self.id, commitInfoMap));
						}
			} else
				buffer.add(msg);

		}
	}

	@Override
	public void initialisation() {
		out().broadcast(new KorigValueMsg(self));		
	}

	@Override
	protected void main() {

		int index = in().select(1);
		if (index != -1) {

			setDone(false);

			DcopMessage msg = in(index).receive(1);
			if (msg == null)
				return;

			int sender = ((Channel) in(index)).getNeighbor().asInt();

			if (msg instanceof TerminateMessage) {
				break;
			} else if (msg instanceof KorigValueMsg) {
				KorigValueMsg vmsg = (KorigValueMsg) msg;
				view.varMap.get(sender).value = vmsg.value;
				boolean f = true;
				for (Constraint c : self.neighbors) {
					if (c.getNeighbor(self).value == -1) {
						f = false;
						break;
					}
				}
				if (f) {
					// state++;
					if (k > 1) {
						LocalInfo l = new LocalInfo(self);
						localInfoMap.put(self.id, l);
						out().broadcast(
								new KorigLocalMsg(self.id, localInfoMap));
					}
					localInfoCounter++;
				}
			} else {
				processMsg(msg);
			}
		} else {
			if (!buffer.isEmpty()) {
				ArrayList<DcopMessage> tmp = new ArrayList<DcopMessage>();
				tmp.addAll(buffer);
				buffer.clear();
				for (DcopMessage msg : tmp)
					processMsg(msg);
			}

			if (localInfoCounter == k / 2 + 1) {
				localInfoCounter = 0;
				for (Variable v : view.varMap.values()) {
					korigView.varMap.get(v.id).value = v.value;
				}

				for (LocalInfo l : localInfoMap.values()) {
					Variable v = korigView.varMap.get(l.id);
					if (v == null) {
						v = new Variable(l.id, l.domain, korigView);
						korigView.varMap.put(v.id, v);
					}
					for (int[] enc : l.data) {
						if (enc[0] == v.id) {
							Variable n = korigView.varMap.get(enc[2]);
							if (n == null) {
								n = new Variable(enc[2], enc[3], korigView);
								korigView.varMap.put(n.id, n);
							}
							if (!v.hasNeighbor(n.id)) {
								Constraint c = new Constraint(v, n);
								korigView.conList.add(c);
								for (int i = 0; i < c.d1; i++)
									for (int j = 0; j < c.d2; j++) {
										c.f[i][j] = enc[4 + i * c.d2 + j];
									}
							}
						} else {
							Variable n = korigView.varMap.get(enc[0]);
							if (n == null) {
								n = new Variable(enc[0], enc[1], korigView);
								korigView.varMap.put(n.id, n);
							}
							if (!v.hasNeighbor(n.id)) {
								Constraint c = new Constraint(n, v);
								korigView.conList.add(c);
								for (int i = 0; i < c.d1; i++)
									for (int j = 0; j < c.d2; j++) {
										c.f[i][j] = enc[4 + i * c.d2 + j];
									}
							}
						}
					}
					for (Integer i : l.valMap.keySet())
						korigView.varMap.get(i).value = l.valMap.get(i);
				}

				HashSet<Integer> kgroup = new HashSet<Integer>();
							ArrayList<Integer> cList = new ArrayList<Integer>();
							kgroup.add(korigSelf.id);
							for (Constraint c : korigSelf.neighbors) {
								cList.add(c.getNeighbor(korigSelf).id);
							}
							while (kgroup.size() < k) {
								if (cList.isEmpty())
									break;
								int idx = cList.remove(Helper.random.nextInt(cList
										.size()));
								kgroup.add(idx);
								Variable v = korigView.getVar(idx);
								for (Constraint c : v.neighbors) {
									int nid = c.getNeighbor(v).id;
									if (localInfoMap.containsKey(nid)
											&& !cList.contains(nid)
											&& !kgroup.contains(nid))
										cList.add(nid);
								}
							}

							for (Variable v : korigView.varMap.values())
								v.fixed = true;
									for (Integer i : kgroup) {
										korigView.getVar(i).fixed = false;
									}
									HashMap<Integer, Integer> sol = korigView.DPOPSolve();
									int gain = korigView.evaluate(sol) - korigView.evaluate();
									HashMap<Integer, Integer> vMap = new HashMap<Integer, Integer>();
									for (Integer i : kgroup) {
										Variable v = korigView.getVar(i);
										vMap.put(v.id, sol.get(v.id));
										for (Constraint c : v.neighbors) {
											Variable n = c.getNeighbor(v);
											vMap.put(n.id, sol.get(n.id));
										}
									}
									GainInfo g = new GainInfo(korigSelf.id, gain, vMap);
									gainInfoMap.put(g.id, g);
									out().broadcast(new KorigGainMsg(self.id, gainInfoMap));
			}

			if (gainInfoCounter == k / 2 + 1) {
				gainInfoCounter = 0;
				int maxGain = 0;
				int leader = -1;
				for (GainInfo g : gainInfoMap.values()) {
					if (g.valMap.containsKey(self.id)) {
						if (g.gain > maxGain) {
							maxGain = g.gain;
							leader = g.id;
						}
					}
				}
				CommitInfo c = new CommitInfo(self.id, leader);
				commitInfoMap.put(self.id, c);
				out().broadcast(new KorigCommitMsg(self.id, commitInfoMap));
			}

			if (commitInfoCounter == k + 1) {
				commitInfoCounter = 0;
				int leader = commitInfoMap.get(self.id).leader;
				if (leader != -1) {
					boolean f = true;
					for (Integer i : gainInfoMap.get(leader).valMap
							.keySet()) {
						if (commitInfoMap.get(i).leader != leader) {
							f = false;
							break;
						}
					}
					if (f) {
						int gain = gainInfoMap.get(leader).gain;
						int newValue = gainInfoMap.get(leader).valMap
								.get(self.id);
						if (self.value != newValue)
							System.out.println(leader + " " + self.id + " "
									+ self.value + "->" + newValue + " : "
									+ gain);
						self.value = newValue;
					}
				}
				init();
				out().broadcast(new KorigValueMsg(self));
			}
		}
	}

}

class KorigValueMsg extends DcopMessage {
	int id;
	int value;

	public KorigValueMsg(Variable v) {
		value = v.value;
	}

	public int getSize() {
		return 9;
	}
}

class KorigLocalMsg extends DcopMessage {
	int id;
	HashMap<Integer, LocalInfo> map;

	public KorigLocalMsg(int i, HashMap<Integer, LocalInfo> m) {
		id = i;
		map = new HashMap<Integer, LocalInfo>();
		for (LocalInfo l : m.values())
			map.put(l.id, l);
	}

	public int getSize() {
		int s = 0;
		for (LocalInfo l : map.values())
			s += l.getSize();
				return s + 5;
	}
}

class KorigGainMsg extends DcopMessage {
	int id;
	HashMap<Integer, GainInfo> map;

	public KorigGainMsg(int i, HashMap<Integer, GainInfo> m) {
		id = i;
		map = new HashMap<Integer, GainInfo>();
		for (GainInfo l : m.values())
			map.put(l.id, l);
	}

	public int getSize() {
		int s = 0;
		for (GainInfo l : map.values())
			s += l.getSize();
				return s + 5;
	}
}

class KorigCommitMsg extends DcopMessage {
	int id;
	HashMap<Integer, CommitInfo> map;

	public KorigCommitMsg(int i, HashMap<Integer, CommitInfo> m) {
		id = i;
		map = new HashMap<Integer, CommitInfo>();
		for (CommitInfo l : m.values())
			map.put(l.id, l);
	}

	public int getSize() {
		return map.size() * 8 + 5;
	}
}