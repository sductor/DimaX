package frameworks.faulttolerance.dcop.dcop;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Vector;

import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;

public class DPOPSolver {

	public HashMap<Integer, Variable> varMap;
	public Vector<Constraint> conList;
	
	

	public void backup() {
		for (Variable v : varMap.values())
			v.backupValue();
	}

	public void recover() {
		for (Variable v : varMap.values())
			v.recoverValue();
	}


	public HashMap<Integer, Integer> getSolution() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Variable v : varMap.values())
			map.put(v.id, v.value);
		return map;
	}
	
	public DcopClassicalGraph simplifyGraph() {
		DcopClassicalGraph tmp = new DcopClassicalGraph();
		int maxId = 0;
		boolean f = false;
		for (Variable v : this.varMap.values()) {
			if (v.id > maxId)
				maxId = v.id;
			if (!v.fixed)
				tmp.varMap.put(v.id, new Variable(v.id, v.domain, tmp));
			else {
				f = true;
				assert v.value != -1;
			}
		}
		maxId++;
		Variable s = new Variable(maxId, 1, tmp);
		if (f)
			tmp.varMap.put(s.id, s);
		for (Constraint c : this.conList) {
			if (!c.first.fixed && !c.second.fixed) {
				Constraint cc = new Constraint(tmp.varMap.get(c.first.id),
						tmp.varMap.get(c.second.id));
				for (int i = 0; i < cc.d1; i++)
					for (int j = 0; j < cc.d2; j++)
						cc.f[i][j] = c.f[i][j];
				tmp.conList.add(cc);
			} else if (c.first.fixed && !c.second.fixed) {
				Constraint cc = s.getNeighbor(c.second.id);
				if (cc == null) {
					cc = new Constraint(tmp.varMap.get(c.second.id), s);
					tmp.conList.add(cc);
				}
				for (int i = 0; i < cc.d1; i++)
					cc.f[i][0] += c.f[c.first.value][i];

			} else if (!c.first.fixed && c.second.fixed) {
				Constraint cc = s.getNeighbor(c.first.id);
				if (cc == null) {
					cc = new Constraint(tmp.varMap.get(c.first.id), s);
					tmp.conList.add(cc);
				}
				for (int i = 0; i < cc.d1; i++)
					cc.f[i][0] += c.f[i][c.second.value];
			}
		}
		return tmp;
	}
	public HashMap<Integer, Integer> DPOPSolve() {
		DcopClassicalGraph tmp = this.simplifyGraph();
		int maxId = tmp.getMaxId();
		Variable top = tmp.varMap.get(maxId);
		HashMap<Integer, Integer> sol = tmp._DPOPSolve(top);
		HashMap<Integer, Integer> fsol = new HashMap<Integer, Integer>();
		for (Integer i : sol.keySet())
			if (this.varMap.containsKey(i))
				fsol.put(i, sol.get(i));
				for (Variable v : varMap.values()) {
					if (v.fixed)
						fsol.put(v.id, v.value);
				}
				//System.out.println(this.evaluate(fsol));
				return fsol;
	}

	private HashMap<Integer, Integer> _DPOPSolve(Variable top) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		HashSet<Integer> visited = new HashSet<Integer>();
		DPOPTreeNode root = new DPOPTreeNode(top.id, 0, 0, false, null);
		visited.add(top.id);
		ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		this.depthFirstTraverse(root, visited, queue);
		while (!queue.isEmpty()) {
			DPOPTreeNode node = queue.remove(0);
			computeMatrix(node);
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

	private void depthFirstTraverse(DPOPTreeNode parent,
			HashSet<Integer> visited, ArrayList<DPOPTreeNode> leafList) {
		Variable v = this.varMap.get(parent.id);
		assert v != null;
		for (Constraint c : v.neighbors) {
			Variable n = c.getNeighbor(v);
			if (!visited.contains(n.id)) {
				DPOPTreeNode node = new DPOPTreeNode(n.id, 0, 0, false, parent);
				visited.add(n.id);
				this.depthFirstTraverse(node, visited, leafList);
			}
		}
		if (parent.children.isEmpty())
			leafList.add(parent);
	}

	private void computeMatrix(DPOPTreeNode node) {
		// TODO consider children
		Variable self = varMap.get(node.id);

		HashSet<Integer> ancestors = new HashSet<Integer>();
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
		for (Constraint c : self.neighbors) {
			Variable n = c.getNeighbor(self);
			if (ancestors.contains(n.id))
				list.put(n.id, n.domain);
		}
		for (RewardMatrix m : matList) {
			for (Integer i : m.domain.keySet()) {
				if (i != self.id)
					list.put(i, m.domain.get(i));
			}
		}

		node.mat = new RewardMatrix(list);

		this.backup();
		for (int i = 0; i < node.mat.getSize(); i++) {
			double best = -1;
			int bestVal = 0;
			for (int j = 0; j < self.domain; j++) {
				self.value = j;
				double sum = 0;
				for (Constraint c : self.neighbors) {
					Variable n = c.getNeighbor(self);
					if (ancestors.contains(n.id)) {
						n.value = node.mat.getValue(i, n.id);
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
		this.recover();

		// if (node.parent == null && node.mat.data[0]>128)
		// System.out.println("REWARD " + node.id + " " + node.reward + " "
		// + node.mat.data[0]);
	}
	



	private int getMaxId() {
		int max = 0;
		for (Variable v : varMap.values())
			if (v.id > max)
				max = v.id;
				return max;
	}
}
