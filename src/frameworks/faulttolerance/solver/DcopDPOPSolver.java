package frameworks.faulttolerance.solver;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;

import choco.cp.solver.constraints.reified.ExpressionSConstraint.VarMinDomComparator;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.DcopSolver;
import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.MemFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.negotiatingagent.HostState;
import frameworks.faulttolerance.negotiatingagent.ReplicationCandidature;
import frameworks.negotiation.exploration.ResourceAllocationSolver;
import frameworks.negotiation.exploration.Solver.ExceedLimitException;
import frameworks.negotiation.exploration.Solver.UnsatisfiableException;

public class DcopDPOPSolver implements DcopSolver {


	public HashMap<Integer, Integer> solve(DcopReplicationGraph drg){
		return DPOPSolve(drg);
	}


	private HashMap<Integer, Integer> DPOPSolve(DcopReplicationGraph drg) {
		DcopReplicationGraph tmp = simplifyGraph(drg);
		int maxId = getMaxId(tmp);
		ReplicationVariable top = tmp.varMap.get(maxId);
		HashMap<Integer, Integer> sol = _DPOPSolve(tmp,top);
		HashMap<Integer, Integer> fsol = new HashMap<Integer, Integer>();
		for (Integer i : sol.keySet())
			if (drg.varMap.containsKey(i))
				fsol.put(i, sol.get(i));
				for (ReplicationVariable v : drg.varMap.values()) {
					if (v.fixed)
						fsol.put(v.id, v.getValue());
				}
				//System.out.println(this.evaluate(fsol));
				return fsol;
	}

	private HashMap<Integer, Integer> _DPOPSolve(DcopReplicationGraph drg,ReplicationVariable top) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		HashSet visited = new HashSet();
		DPOPTreeNode root = new DPOPTreeNode(top.id, 0, 0, false, null);
		visited.add(top.id);
		ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		depthFirstTraverse(drg,root, visited, queue);
		while (!queue.isEmpty()) {
			DPOPTreeNode node = queue.remove(0);
			computeMatrix(drg,node);
			DPOPTreeNode parent = (DPOPTreeNode) node.parent;
			if (parent != null) {
				boolean fullinfo = true;
				for (TreeNode n : parent.children) {
					if (((DPOPTreeNode) n).mat == null) {
						fullinfo = false;
						break;
					}
				}
				if (fullinfo)
					queue.add(parent);
			}
		}
		queue.add(root);
		root.value = root.mat.value[0];
		while (!queue.isEmpty()) {
			DPOPTreeNode node = queue.remove(0);
			int offset = 0;
			TreeNode n = node.parent;
			while (n != null) {
				offset += node.mat.getBase(n.id) * n.value;
				n = n.parent;
			}
			node.value = node.mat.value[offset];
			map.put(node.id, node.value);
			for (TreeNode nn : node.children)
				queue.add((DPOPTreeNode) nn);
		}
		return map;
	}

	private DcopReplicationGraph simplifyGraph(DcopReplicationGraph drg) {
		DcopReplicationGraph tmp = DCOPFactory.constructDCOPGraph(drg.getSocialWelfare());
		int maxId = 0;
		boolean f = false;
		for (ReplicationVariable v : drg.varMap.values()) {
			if (v.id > maxId)
				maxId = v.id;
			if (!v.fixed)
				tmp.varMap.put(v.id, DCOPFactory.constructVariable(v.id, v.getDomain(), v.getState(), drg.getSocialWelfare()));
			else {
				f = true;
				assert v.getValue() != -1;
			}
		}
		maxId++;
//		ReplicationVariable s = DCOPFactory.constructVariable(maxId, 1, drg.varMap.get(maxId).getState(),tmp);
		ReplicationVariable s = DCOPFactory.constructVariable(maxId, 1, null,drg.getSocialWelfare());
		if (f)
			tmp.varMap.put(s.id, s);
		for (MemFreeConstraint c : drg.conList) {
			if (!c.first.fixed && !c.second.fixed) {
				MemFreeConstraint cc = 
						DCOPFactory.constructConstraint(
								tmp.varMap.get(c.first.id),
								tmp.varMap.get(c.second.id));
				if (DCOPFactory.isClassical()){
					for (int i = 0; i < cc.d1; i++)
						for (int j = 0; j < cc.d2; j++)
							((CPUFreeConstraint)cc).f[i][j] = ((CPUFreeConstraint)c).f[i][j];
				}
				tmp.conList.add(cc);
			} else if (c.first.fixed && !c.second.fixed) {
				MemFreeConstraint cc = s.getNeighbor(c.second.id);
				if (cc == null) {
					cc = DCOPFactory.constructConstraint(tmp.varMap.get(c.second.id), s);
					tmp.conList.add(cc);
				}
				if (DCOPFactory.isClassical()){
					for (int i = 0; i < cc.d1; i++)
						((CPUFreeConstraint)cc).f[i][0] += ((CPUFreeConstraint)c).f[c.first.getValue()][i];
				}

			} else if (!c.first.fixed && c.second.fixed) {
				MemFreeConstraint cc = s.getNeighbor(c.first.id);
				if (cc == null) {
					cc = DCOPFactory.constructConstraint(tmp.varMap.get(c.first.id), s);
					tmp.conList.add(cc);
				}
				if (DCOPFactory.isClassical()){
					for (int i = 0; i < cc.d1; i++)
						((CPUFreeConstraint)cc).f[i][0] += ((CPUFreeConstraint)c).f[i][c.second.getValue()];
				}
			}
		}
		return tmp;
	}
	private void depthFirstTraverse(DcopReplicationGraph drg, DPOPTreeNode parent,
			HashSet visited, ArrayList<DPOPTreeNode> leafList) {
		ReplicationVariable v = drg.varMap.get(parent.id);
		assert v != null;
		for (MemFreeConstraint c : v.getNeighbors()) {
			ReplicationVariable n = c.getNeighbor(v);
			if (!visited.contains(n.id)) {
				DPOPTreeNode node = new DPOPTreeNode(n.id, 0, 0, false, parent);
				visited.add(n.id);
				depthFirstTraverse(drg,node, visited, leafList);
			}
		}
		if (parent.children.isEmpty())
			leafList.add(parent);
	}

	private void computeMatrix(DcopReplicationGraph drg, DPOPTreeNode node) {
		// TODO consider children
		ReplicationVariable self = drg.varMap.get(node.id);

		HashSet ancestors = new HashSet();
		ArrayList<RewardMatrix> matList = new ArrayList<RewardMatrix>();

		for (TreeNode n : node.children) {
			DPOPTreeNode dn = (DPOPTreeNode) n;
			assert dn.mat != null;
			matList.add(dn.mat);
		}
		TreeNode p = node.parent;
		while (p != null) {
			ancestors.add(p.id);
			p = p.parent;
		}

		HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		for (MemFreeConstraint c : self.getNeighbors()) {
			ReplicationVariable n = c.getNeighbor(self);
			if (ancestors.contains(n.id))
				list.put(n.id, n.getDomain());
		}
		for (RewardMatrix m : matList) {
			for (Integer i : m.domain.keySet()) {
				if (i != self.id)
					list.put(i, m.domain.get(i));
			}
		}

		node.mat = new RewardMatrix(list);

		drg.backup();
		for (int i = 0; i < node.mat.getSize(); i++) {
			double best = -1;
			int bestVal = 0;
			for (int j = 0; j < self.getDomain(); j++) {
				self.setValue(j);
				double sum = 0;
				for (MemFreeConstraint c : self.getNeighbors()) {
					ReplicationVariable n = c.getNeighbor(self);
					if (ancestors.contains(n.id)) {
						n.setValue(node.mat.getValue(i, n.id));
						sum += c.evaluate();
					}
				}
				for (RewardMatrix m : matList) {
					int offset = AsyncHelper.translate(node.mat, m, i);
					assert m.contains(self.id);
					offset += j * m.getBase(self.id);
					sum += m.data[offset];
				}

				if (sum > best) {
					best = sum;
					bestVal = j;
				}
			}
			node.mat.data[i] = best;
			node.mat.value[i] = bestVal;
		}
		drg.recover();

		// if (node.parent == null && node.mat.data[0]>128)
		// System.out.println("REWARD " + node.id + " " + node.reward + " "
		// + node.mat.data[0]);
	}


	private int getMaxId(DcopReplicationGraph drg) {
		int max = 0;
		for (ReplicationVariable v : drg.varMap.values())
			if (v.id > max)
				max = v.id;
				return max;
	}
}
