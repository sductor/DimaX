package frameworks.faulttolerance.dcop.algo.korig;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.algo.BasicAlgorithm;
import frameworks.faulttolerance.dcop.algo.TerminateMessage;
import frameworks.faulttolerance.dcop.daj.Channel;
import frameworks.faulttolerance.dcop.daj.Message;
import frameworks.faulttolerance.dcop.daj.Program;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.Helper;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.experimentation.ReplicationExperimentationParameters;
import frameworks.negotiation.rationality.AgentState;

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

	ArrayList<Message> buffer;

	int state;

	int sync;

	DcopReplicationGraph korigView;
	ReplicationVariable korigSelf;

	public AlgoKOptOriginal(ReplicationVariable v, int kk) {
		super(v);
		k = kk;
		self.setValue(self.getInitialValue());
		init();
	}

	private void init() {
		if (self.id == 0)
			System.out.println("STARTOVER");
		buffer = new ArrayList<Message>();
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
		for (MemFreeConstraint c : self.getNeighbors()) {
			c.getNeighbor(self).setValue(-1);
		}
		korigView = DCOPFactory.constructDCOPGraph(self.getSocialWelfare());
		for (ReplicationVariable v : view.varMap.values()) {
			korigView.varMap.put(v.id, DCOPFactory.constructVariable(v.id, v.getDomain(), v.getState(), korigView));
		}
		korigSelf = korigView.varMap.get(self.id);
		for (MemFreeConstraint c : view.conList) {
			MemFreeConstraint cc = DCOPFactory.constructConstraint(korigView.getVar(c.first.id),
					korigView.getVar(c.second.id));
			korigView.conList.add(cc);
			if (DCOPFactory.isClassical()){
			for (int i = 0; i < c.d1; i++)
				for (int j = 0; j < c.d2; j++)
					((CPUFreeConstraint)cc).f[i][j] = ((CPUFreeConstraint)c).f[i][j];
			}
		}
		sync = 0;
	}

	private void processMsg(Message msg) {
		if (msg instanceof KorigLocalMsg) {
			KorigLocalMsg lmsg = (KorigLocalMsg) msg;
			if (!localInfoSet.contains(lmsg.id)) {
				localInfoSet.add(lmsg.id);
				for (LocalInfo l : lmsg.map.values())
					localInfoMap.put(l.id, l);
				if (localInfoSet.size() == self.getNeighbors().size()) {
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
				if (gainInfoSet.size() == self.getNeighbors().size()) {
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
				if (commitInfoSet.size() == self.getNeighbors().size()) {
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
	protected void main() {

		out().broadcast(new KorigValueMsg(self));

		while (true) {
			int index = in().select(1);
			if (index != -1) {

				done = false;

				Message msg = in(index).receive(1);
				if (msg == null)
					continue;

				int sender = -1;
				Program p = ((Channel) in(index)).getSender().getProgram();
				if (p instanceof BasicAlgorithm) {
					sender = ((BasicAlgorithm) p).getID();
				}

				if (msg instanceof TerminateMessage) {
					break;
				} else if (msg instanceof KorigValueMsg) {
					KorigValueMsg vmsg = (KorigValueMsg) msg;
					view.varMap.get(sender).setValue(vmsg.value);
					boolean f = true;
					for (MemFreeConstraint c : self.getNeighbors()) {
						if (c.getNeighbor(self).getValue() == -1) {
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
					ArrayList<Message> tmp = new ArrayList<Message>();
					tmp.addAll(buffer);
					buffer.clear();
					for (Message msg : tmp)
						processMsg(msg);
				}

				if (localInfoCounter == k / 2 + 1) {
					localInfoCounter = 0;
					for (ReplicationVariable v : view.varMap.values()) {
						korigView.varMap.get(v.id).setValue(v.getValue());
					}
					
					for (LocalInfo l : localInfoMap.values()) {
						ReplicationVariable v = korigView.varMap.get(l.id);
						if (v == null) {
							v = DCOPFactory.constructVariable(l.id, l.domain, l.state,korigView);
							korigView.varMap.put(v.id, v);
						}
						for (double[] enc : l.data) {
							if (enc[0] == v.id) {
								ReplicationVariable n = korigView.varMap.get((int)enc[2]);
								if (n == null) {
									AgentState s = null;
									if (!DCOPFactory.isClassical())
										s = l.dataStates.get((int)enc[2]);
									n = DCOPFactory.constructVariable((int)enc[2], (int)enc[3], s, korigView);
									korigView.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
									MemFreeConstraint c = DCOPFactory.constructConstraint(v, n);
									korigView.conList.add(c);
									if (DCOPFactory.isClassical()){
									for (int i = 0; i < c.d1; i++)
										for (int j = 0; j < c.d2; j++) {
											((CPUFreeConstraint)c).f[i][j] = enc[4 + i * c.d2 + j];
										}
									}
								}
							} else {
								ReplicationVariable n = korigView.varMap.get((int)enc[0]);
								if (n == null) {
									AgentState s = null;
									if (!DCOPFactory.isClassical())
										s = l.dataStates.get((int)enc[2]);
									n = DCOPFactory.constructVariable((int)enc[0], (int)enc[1], s, korigView);
									korigView.varMap.put(n.id, n);
								}
								if (!v.hasNeighbor(n.id)) {
									MemFreeConstraint c = DCOPFactory.constructConstraint(n, v);
									korigView.conList.add(c);
									if (DCOPFactory.isClassical()){
									for (int i = 0; i < c.d1; i++)
										for (int j = 0; j < c.d2; j++) {
											((CPUFreeConstraint)c).f[i][j] = enc[4 + i * c.d2 + j];
										}
									}
								}
							}
						}
						for (Integer i : l.valMap.keySet())
							korigView.varMap.get(i).setValue(l.valMap.get(i));
					}

					HashSet<Integer> kgroup = new HashSet<Integer>();
					ArrayList<Integer> cList = new ArrayList<Integer>();
					kgroup.add(korigSelf.id);
					for (MemFreeConstraint c : korigSelf.getNeighbors()) {
						cList.add(c.getNeighbor(korigSelf).id);
					}
					while (kgroup.size() < k) {
						if (cList.isEmpty())
							break;
						int idx = cList.remove(Helper.random.nextInt(cList
								.size()));
						kgroup.add(idx);
						ReplicationVariable v = korigView.getVar(idx);
						for (MemFreeConstraint c : v.getNeighbors()) {
							int nid = c.getNeighbor(v).id;
							if (localInfoMap.containsKey(nid)
									&& !cList.contains(nid)
									&& !kgroup.contains(nid))
								cList.add(nid);
						}
					}

					for (ReplicationVariable v : korigView.varMap.values())
						v.fixed = true;
					for (Integer i : kgroup) {
						korigView.getVar(i).fixed = false;
					}
					HashMap<Integer, Integer> sol = korigView.solve();
					double gain = korigView.evaluate(sol) - korigView.evaluate();
					HashMap<Integer, Integer> vMap = new HashMap<Integer, Integer>();
					for (Integer i : kgroup) {
						ReplicationVariable v = korigView.getVar(i);
						vMap.put(v.id, sol.get(v.id));
						for (MemFreeConstraint c : v.getNeighbors()) {
							ReplicationVariable n = c.getNeighbor(v);
							vMap.put(n.id, sol.get(n.id));
						}
					}
					GainInfo g = new GainInfo(korigSelf.id, gain, vMap);
					gainInfoMap.put(g.id, g);
					out().broadcast(new KorigGainMsg(self.id, gainInfoMap));
				}

				if (gainInfoCounter == k / 2 + 1) {
					gainInfoCounter = 0;
					double maxGain = 0;
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
							double gain = gainInfoMap.get(leader).gain;
							int newValue = gainInfoMap.get(leader).valMap
									.get(self.id);
							if (self.getValue() != newValue)
								System.out.println(leader + " " + self.id + " "
										+ self.getValue() + "->" + newValue + " : "
										+ gain);
							self.setValue(newValue);
						}
					}
					init();
					out().broadcast(new KorigValueMsg(self));
				}
			}
		}
	}
}

class KorigValueMsg extends Message {
	int id;
	int value;

	public KorigValueMsg(ReplicationVariable v) {
		value = v.getValue();
	}

	public int getSize() {
		return 9;
	}
}

class KorigLocalMsg extends Message {
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

class KorigGainMsg extends Message {
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

class KorigCommitMsg extends Message {
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