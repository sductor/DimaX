package negotiation.dcopframework.dcop;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Random;
import java.util.Vector;

import negotiation.dcopframework.algo.topt.AsyncHelper;
import negotiation.dcopframework.algo.topt.DPOPTreeNode;
import negotiation.dcopframework.algo.topt.RewardMatrix;
import negotiation.dcopframework.algo.topt.TreeNode;


public class Graph {

	public HashMap<Integer, Variable> varMap;
	public Vector<Constraint> conList;

	private int _maxReward;
	private HashMap<Integer, Integer> _bestSolution;

	public Random random = new Random();

	public Graph() {
		this.varMap = new HashMap<Integer, Variable>();
		this.conList = new Vector<Constraint>();
	}

	public Variable getVar(final int i) {
		return this.varMap.get(i);
	}

	public boolean checkValues() {
		for (final Variable v : this.varMap.values())
			if (v.value == -1)
				return false;
				return true;
	}

	public int evaluate() {
		int sum = 0;
		for (final Constraint c : this.conList)
			sum += c.evaluate();
				return sum;
	}

	public int evaluate(final HashMap<Integer, Integer> sol) {
		this.backup();
		for (final Variable v : this.varMap.values())
			v.value = sol.get(v.id);
				final int sum = this.evaluate();
				this.recover();
				return sum;
	}

	public boolean sameSolution(final HashMap<Integer, Integer> sol) {
		if (sol == null)
			return false;
		for (final Variable v : this.varMap.values()) {
			if (!sol.containsKey(v.id))
				return false;
			if (v.value != sol.get(v.id))
				return false;
		}
		return true;
	}

	public void clear() {
		for (final Variable v : this.varMap.values())
			v.clear();
	}

	public void backup() {
		for (final Variable v : this.varMap.values())
			v.backupValue();
	}

	public void recover() {
		for (final Variable v : this.varMap.values())
			v.recoverValue();
	}

	public HashMap<Integer, Integer> branchBoundSolve() {
		final ArrayList<Variable> list = new ArrayList<Variable>();
		for (final Variable v : this.varMap.values())
			list.add(v);
				Collections.sort(list, new Comparator<Variable>() {
					@Override
					public int compare(final Variable v0, final Variable v1) {
						if (v0.getDegree() >= v1.getDegree())
							return -1;
						else
							return 1;
					}
				});
				final int[] queue = new int[list.size()];
				int idx = 0;
				for (final Variable v : list) {
					queue[idx] = v.id;
					idx++;
				}

				this.backup();

				for (final Variable v : this.varMap.values())
					if (!v.fixed && v.value == -1)
						v.value = 0;

						this._maxReward = this.evaluate();
						this._bestSolution = this.getSolution();

						// int r = _maxReward;

						for (final Variable v : this.varMap.values())
							if (!v.fixed)
								v.value = -1;

								this._bbEnumerate(queue, 0);

								// int rr = _maxReward;

								this.recover();

								return this._bestSolution;

	}

	private void _bbEnumerate(final int[] queue, final int idx) {
		if (idx == queue.length) {
			final int val = this.evaluate();
			if (val > this._maxReward) {
				this._maxReward = val;
				this._bestSolution = this.getSolution();
			}
			return;
		}
		final Variable v = this.varMap.get(queue[idx]);
		if (v.fixed)
			this._bbEnumerate(queue, idx + 1);
		else {
			for (int i = 0; i < v.domain; i++) {
				v.value = i;
				final int val = this.evaluate();
				if (val <= this._maxReward)
					continue;
				this._bbEnumerate(queue, idx + 1);
			}
			v.value = -1;
		}
	}

	public Graph simplifyGraph() {
		final Graph tmp = new Graph();
		int maxId = 0;
		boolean f = false;
		for (final Variable v : this.varMap.values()) {
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
		final Variable s = new Variable(maxId, 1, tmp);
		if (f)
			tmp.varMap.put(s.id, s);
		for (final Constraint c : this.conList)
			if (!c.first.fixed && !c.second.fixed) {
				final Constraint cc = new Constraint(tmp.varMap.get(c.first.id),
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
		return tmp;
	}

	public int getMaxId() {
		int max = 0;
		for (final Variable v : this.varMap.values())
			if (v.id > max)
				max = v.id;
				return max;
	}

	public HashMap<Integer, Integer> DPOPSolve() {
		final Graph tmp = this.simplifyGraph();
		final int maxId = tmp.getMaxId();
		final Variable top = tmp.varMap.get(maxId);
		final HashMap<Integer, Integer> sol = tmp._DPOPSolve(top);
		final HashMap<Integer, Integer> fsol = new HashMap<Integer, Integer>();
		for (final Integer i : sol.keySet())
			if (this.varMap.containsKey(i))
				fsol.put(i, sol.get(i));
				for (final Variable v : this.varMap.values())
					if (v.fixed)
						fsol.put(v.id, v.value);
						//System.out.println(this.evaluate(fsol));
						return fsol;
	}

	private HashMap<Integer, Integer> _DPOPSolve(final Variable top) {
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		final HashSet<Integer> visited = new HashSet<Integer>();
		final DPOPTreeNode root = new DPOPTreeNode(top.id, 0, 0, false, null);
		visited.add(top.id);
		final ArrayList<DPOPTreeNode> queue = new ArrayList<DPOPTreeNode>();
		this.depthFirstTraverse(root, visited, queue);
		while (!queue.isEmpty()) {
			final DPOPTreeNode node = queue.remove(0);
			this.computeMatrix(node);
			final DPOPTreeNode parent = (DPOPTreeNode) node.parent;
			if (parent != null) {
				boolean fullinfo = true;
				for (final TreeNode n : parent.children)
					if (((DPOPTreeNode) n).mat == null) {
						fullinfo = false;
						break;
					}
				if (fullinfo)
					queue.add(parent);
			}
		}
		queue.add(root);
		root.value = root.mat.value[0];
		while (!queue.isEmpty()) {
			final DPOPTreeNode node = queue.remove(0);
			int offset = 0;
			TreeNode n = node.parent;
			while (n != null) {
				offset += node.mat.getBase(n.id) * n.value;
				n = n.parent;
			}
			node.value = node.mat.value[offset];
			map.put(node.id, node.value);
			for (final TreeNode nn : node.children)
				queue.add((DPOPTreeNode) nn);
		}
		return map;
	}

	private void depthFirstTraverse(final DPOPTreeNode parent,
			final HashSet<Integer> visited, final ArrayList<DPOPTreeNode> leafList) {
		final Variable v = this.varMap.get(parent.id);
		assert v != null;
		for (final Constraint c : v.neighbors) {
			final Variable n = c.getNeighbor(v);
			if (!visited.contains(n.id)) {
				final DPOPTreeNode node = new DPOPTreeNode(n.id, 0, 0, false, parent);
				visited.add(n.id);
				this.depthFirstTraverse(node, visited, leafList);
			}
		}
		if (parent.children.isEmpty())
			leafList.add(parent);
	}

	private void computeMatrix(final DPOPTreeNode node) {
		// TODO consider children
		final Variable self = this.varMap.get(node.id);

		final HashSet<Integer> ancestors = new HashSet<Integer>();
		final ArrayList<RewardMatrix> matList = new ArrayList<RewardMatrix>();

		for (final TreeNode n : node.children) {
			final DPOPTreeNode dn = (DPOPTreeNode) n;
			assert dn.mat != null;
			matList.add(dn.mat);
		}
		TreeNode p = node.parent;
		while (p != null) {
			ancestors.add(p.id);
			p = p.parent;
		}

		final HashMap<Integer, Integer> list = new HashMap<Integer, Integer>();
		for (final Constraint c : self.neighbors) {
			final Variable n = c.getNeighbor(self);
			if (ancestors.contains(n.id))
				list.put(n.id, n.domain);
		}
		for (final RewardMatrix m : matList)
			for (final Integer i : m.domain.keySet())
				if (i != self.id)
					list.put(i, m.domain.get(i));

					node.mat = new RewardMatrix(list);

					this.backup();
					for (int i = 0; i < node.mat.getSize(); i++) {
						int best = -1;
						int bestVal = -1;
						for (int j = 0; j < self.domain; j++) {
							self.value = j;
							int sum = 0;
							for (final Constraint c : self.neighbors) {
								final Variable n = c.getNeighbor(self);
								if (ancestors.contains(n.id)) {
									n.value = node.mat.getValue(i, n.id);
									sum += c.evaluate();
								}
							}
							for (final RewardMatrix m : matList) {
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
		final HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (final Variable v : this.varMap.values())
			map.put(v.id, v.value);
				return map;
	}

	public Graph(final String inFilename) {
		// We assume in the input file, there is at most one link between two
		// variables
		try {
			final BufferedReader reader = new BufferedReader(new FileReader(
					inFilename));
			this.varMap = new HashMap<Integer, Variable>();
			this.conList = new Vector<Constraint>();
			String line;
			Constraint c = null;
			while ((line = reader.readLine()) != null)
				if (line.startsWith("VARIABLE")) {
					final Variable v = new Variable(line, this);
					this.varMap.put(v.id, v);
				} else if (line.startsWith("CONSTRAINT")) {
					final String[] ss = line.split(" ");
					assert ss.length >= 3;
					final int first = Integer.parseInt(ss[1]);
					final int second = Integer.parseInt(ss[2]);
					assert this.varMap.containsKey(first);
					assert this.varMap.containsKey(second);
					c = new Constraint(this.varMap.get(first), this.varMap.get(second));
					this.conList.add(c);
				} else if (line.startsWith("F")) {
					assert c != null;
					final String[] ss = line.split(" ");
					assert ss.length >= 4;
					final int x = Integer.parseInt(ss[1]);
					final int y = Integer.parseInt(ss[2]);
					final int v = Integer.parseInt(ss[3]);
					c.f[x][y] = v;
				}
			for (final Constraint cc : this.conList)
				cc.cache();
		} catch (final FileNotFoundException e) {
			e.printStackTrace();
		} catch (final IOException e) {
			e.printStackTrace();
		}

	}
}
