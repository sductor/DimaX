package frameworks.dcop.dcop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.HashSet;
import java.util.Vector;

import frameworks.dcop.algo.topt.AsyncHelper;
import frameworks.dcop.algo.topt.DPOPTreeNode;
import frameworks.dcop.algo.topt.RewardMatrix;
import frameworks.dcop.algo.topt.TreeNode;



public class Graph {

	public HashMap<Integer, Variable> varMap;
	public Vector<Constraint> conList;

	private int _maxReward;
	private HashMap<Integer, Integer> _bestSolution;

	public Random random = new Random();	

	public Graph() {
		varMap = new HashMap<Integer, Variable>();
		conList = new Vector<Constraint>();
	}

	public Variable getVar(int i) {
		return varMap.get(i);
	}

	public boolean checkValues() {
		for (Variable v : varMap.values())
			if (v.value == -1)
				return false;
		return true;
	}

	public int evaluate() {
		int sum = 0;
		for (Constraint c : conList)
			sum += c.evaluate();
		return sum;
	}

	public int evaluate(HashMap<Integer, Integer> sol) {
		this.backup();
		for (Variable v : varMap.values())
			v.value = sol.get(v.id);
		int sum = this.evaluate();
		this.recover();
		return sum;
	}

	public boolean sameSolution(HashMap<Integer, Integer> sol) {
		if (sol == null)
			return false;
		for (Variable v : varMap.values()) {
			if (!sol.containsKey(v.id))
				return false;
			if (v.value != sol.get(v.id))
				return false;
		}
		return true;
	}

	public void clear() {
		for (Variable v : varMap.values())
			v.clear();
	}

	public void backup() {
		for (Variable v : varMap.values())
			v.backupValue();
	}

	public void recover() {
		for (Variable v : varMap.values())
			v.recoverValue();
	}

	public HashMap<Integer, Integer> branchBoundSolve() {
		ArrayList<Variable> list = new ArrayList<Variable>();
		for (Variable v : varMap.values())
			list.add(v);
		Collections.sort(list, new Comparator<Variable>() {
			public int compare(Variable v0, Variable v1) {
				if (v0.getDegree() >= v1.getDegree())
					return -1;
				else
					return 1;
			}
		});
		int[] queue = new int[list.size()];
		int idx = 0;
		for (Variable v : list) {
			queue[idx] = v.id;
			idx++;
		}

		this.backup();

		for (Variable v : varMap.values())
			if (!v.fixed && v.value == -1)
				v.value = 0;

		_maxReward = this.evaluate();
		_bestSolution = this.getSolution();

		// int r = _maxReward;

		for (Variable v : varMap.values()) {
			if (!v.fixed)
				v.value = -1;
		}

		_bbEnumerate(queue, 0);

		// int rr = _maxReward;

		this.recover();

		return _bestSolution;

	}

	private void _bbEnumerate(int[] queue, int idx) {
		if (idx == queue.length) {
			int val = this.evaluate();
			if (val > _maxReward) {
				_maxReward = val;
				_bestSolution = this.getSolution();
			}
			return;
		}
		Variable v = varMap.get(queue[idx]);
		if (v.fixed)
			_bbEnumerate(queue, idx + 1);
		else {
			for (int i = 0; i < v.domain; i++) {
				v.value = i;
				int val = this.evaluate();
				if (val <= _maxReward)
					continue;
				_bbEnumerate(queue, idx + 1);
			}
			v.value = -1;
		}
	}

	public Graph simplifyGraph() {
		Graph tmp = new Graph();
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

	public int getMaxId() {
		int max = 0;
		for (Variable v : varMap.values())
			if (v.id > max)
				max = v.id;
		return max;
	}

	public HashMap<Integer, Integer> DPOPSolve() {
		Graph tmp = this.simplifyGraph();
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
			int best = -1;
			int bestVal = -1;
			for (int j = 0; j < self.domain; j++) {
				self.value = j;
				int sum = 0;
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

	public HashMap<Integer, Integer> getSolution() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (Variable v : varMap.values())
			map.put(v.id, v.value);
		return map;
	}

	public Graph(String inFilename) {
		// We assume in the input file, there is at most one link between two
		// variables
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					inFilename));
			varMap = new HashMap<Integer, Variable>();
			conList = new Vector<Constraint>();
			String line;
			Constraint c = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("VARIABLE")) {
					Variable v = new Variable(line, this);
					varMap.put(v.id, v);
				} else if (line.startsWith("CONSTRAINT")) {
					String[] ss = line.split(" ");
					assert ss.length >= 3;
					int first = Integer.parseInt(ss[1]);
					int second = Integer.parseInt(ss[2]);
					assert varMap.containsKey(first);
					assert varMap.containsKey(second);
					c = new Constraint(varMap.get(first), varMap.get(second));
					conList.add(c);
				} else if (line.startsWith("F")) {
					assert c != null;
					String[] ss = line.split(" ");
					assert ss.length >= 4;
					int x = Integer.parseInt(ss[1]);
					int y = Integer.parseInt(ss[2]);
					int v = Integer.parseInt(ss[3]);
					c.f[x][y] = v;
				}
			}
			for (Constraint cc : conList)
				cc.cache();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}
}
