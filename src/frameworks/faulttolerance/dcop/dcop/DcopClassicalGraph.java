package frameworks.faulttolerance.dcop.dcop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Random;
import java.util.HashSet;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.algo.Algorithm;
import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;

import frameworks.experimentation.IfailedException;


public class DcopClassicalGraph extends DcopReplicationGraph<Integer>{

	private double _maxReward;
	private HashMap<Integer, Integer> _bestSolution;
	Algorithm algo;
	private Random random = new Random();	

	public DcopClassicalGraph() {
		super();
	}

	public DcopClassicalGraph(String inFilename) {
		super(inFilename);
	}

	public DcopClassicalGraph(String simulationName, long randSeed,
			int nbAgents, int nbHosts, Double agentCriticityMean,
			DispersionSymbolicValue agentCriticityDispersion,
			Double agentLoadMean, DispersionSymbolicValue agentLoadDispersion,
			Double hostCapacityMean,
			DispersionSymbolicValue hostCapacityDispersion,
			Double hostFaultProbabilityMean,
			DispersionSymbolicValue hostDisponibilityDispersion,
			boolean completGraph, int agentAccessiblePerHost)
			throws IfailedException {
		super(simulationName, randSeed, nbAgents, nbHosts, agentCriticityMean,
				agentCriticityDispersion, agentLoadMean, agentLoadDispersion,
				hostCapacityMean, hostCapacityDispersion, hostFaultProbabilityMean,
				hostDisponibilityDispersion, completGraph, agentAccessiblePerHost);
	}

	@Override
	public double evaluate() {
		int sum = 0;
		for (AbstractConstraint<Integer> c : conList)
			sum += c.evaluate();
				return sum;
	}


	@Override
	public HashMap<Integer, Integer> solve() {
		if (algo==Algorithm.TOPTAPO && conList.size() > varMap.size() * varMap.size() / 4)
			return branchBoundSolve();
		else
			return DPOPSolve();
	}

	/*
	 * 
	 */

	private HashMap<Integer, Integer> branchBoundSolve() {
		ArrayList<AbstractVariable<Integer>> list = new ArrayList<AbstractVariable<Integer>>();
		for (AbstractVariable<Integer> v : varMap.values())
			list.add(v);
				Collections.sort(list, new Comparator<AbstractVariable<Integer>>() {
					public int compare(AbstractVariable<Integer> v0, AbstractVariable<Integer> v1) {
						if (v0.getDegree() >= v1.getDegree())
							return -1;
						else
							return 1;
					}
				});
				int[] queue = new int[list.size()];
				int idx = 0;
				for (AbstractVariable<Integer> v : list) {
					queue[idx] = v.id;
					idx++;
				}

				this.backup();

				for (AbstractVariable<Integer> v : varMap.values())
					if (!v.fixed && v.getValue() == -1)
						v.setValue(0);

						_maxReward = this.evaluate();
						_bestSolution = this.getSolution();

						// int r = _maxReward;

						for (AbstractVariable<Integer> v : varMap.values()) {
							if (!v.fixed)
								v.setValue(-1);
						}

						_bbEnumerate(queue, 0);

						// int rr = _maxReward;

						this.recover();

						return _bestSolution;

	}

	private void _bbEnumerate(int[] queue, int idx) {
		if (idx == queue.length) {
			double val = this.evaluate();
			if (val > _maxReward) {
				_maxReward = val;
				_bestSolution = this.getSolution();
			}
			return;
		}
		AbstractVariable <Integer>v = varMap.get(queue[idx]);
		if (v.fixed)
			_bbEnumerate(queue, idx + 1);
		else {
			for (int i = 0; i < v.getDomain(); i++) {
				v.setValue(i);
				double val = this.evaluate();
				if (val <= _maxReward)
					continue;
				_bbEnumerate(queue, idx + 1);
			}
			v.setValue(-1);
		}
	}

	public DcopClassicalGraph simplifyGraph() {
		DcopClassicalGraph tmp = new DcopClassicalGraph();
		int maxId = 0;
		boolean f = false;
		for (AbstractVariable<Integer> v : this.varMap.values()) {
			if (v.id > maxId)
				maxId = v.id;
			if (!v.fixed)
				tmp.varMap.put(v.id, DCOPFactory.constructVariable(v.id, v.getDomain(), tmp));
			else {
				f = true;
				assert v.getValue() != -1;
			}
		}
		maxId++;
		AbstractVariable<Integer> s = DCOPFactory.constructVariable(maxId, 1, tmp);
		if (f)
			tmp.varMap.put(s.id, s);
		for (AbstractConstraint<Integer> c : this.conList) {
			if (!c.first.fixed && !c.second.fixed) {
				AbstractConstraint<Integer> cc = DCOPFactory.constructConstraint(tmp.varMap.get(c.first.id),
						tmp.varMap.get(c.second.id));
				if (DCOPFactory.isClassical()){
				for (int i = 0; i < cc.d1; i++)
					for (int j = 0; j < cc.d2; j++)
						((ClassicalConstraint)cc).f[i][j] = ((ClassicalConstraint)c).f[i][j];
				}
				tmp.conList.add(cc);
			} else if (c.first.fixed && !c.second.fixed) {
				AbstractConstraint<Integer> cc = s.getNeighbor(c.second.id);
				if (cc == null) {
					cc = DCOPFactory.constructConstraint(tmp.varMap.get(c.second.id), s);
					tmp.conList.add(cc);
				}
				if (DCOPFactory.isClassical()){
				for (int i = 0; i < cc.d1; i++)
					((ClassicalConstraint)cc).f[i][0] += ((ClassicalConstraint)c).f[c.first.getValue()][i];
				}

			} else if (!c.first.fixed && c.second.fixed) {
				AbstractConstraint<Integer> cc = s.getNeighbor(c.first.id);
				if (cc == null) {
					cc = DCOPFactory.constructConstraint(tmp.varMap.get(c.first.id), s);
					tmp.conList.add(cc);
				}
				if (DCOPFactory.isClassical()){
				for (int i = 0; i < cc.d1; i++)
					((ClassicalConstraint)cc).f[i][0] += ((ClassicalConstraint)c).f[i][c.second.getValue()];
				}
			}
		}
		return tmp;
	}

	public int getMaxId() {
		int max = 0;
		for (AbstractVariable<Integer> v : varMap.values())
			if (v.id > max)
				max = v.id;
				return max;
	}

	public HashMap<Integer, Integer> DPOPSolve() {
		DcopClassicalGraph tmp = this.simplifyGraph();
		int maxId = tmp.getMaxId();
		AbstractVariable<Integer> top = tmp.varMap.get(maxId);
		HashMap<Integer, Integer> sol = tmp._DPOPSolve(top);
		HashMap<Integer, Integer> fsol = new HashMap<Integer, Integer>();
		for (Integer i : sol.keySet())
			if (this.varMap.containsKey(i))
				fsol.put(i, sol.get(i));
				for (AbstractVariable<Integer> v : varMap.values()) {
					if (v.fixed)
						fsol.put(v.id, v.getValue());
				}
				//System.out.println(this.evaluate(fsol));
				return fsol;
	}

	private HashMap<Integer, Integer> _DPOPSolve(AbstractVariable<Integer> top) {
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
		AbstractVariable<Integer> v = this.varMap.get(parent.id);
		assert v != null;
		for (AbstractConstraint<Integer> c : v.neighbors) {
			AbstractVariable<Integer> n = c.getNeighbor(v);
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
		AbstractVariable<Integer> self = varMap.get(node.id);

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
		for (AbstractConstraint<Integer> c : self.neighbors) {
			AbstractVariable n = c.getNeighbor(self);
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

		this.backup();
		for (int i = 0; i < node.mat.getSize(); i++) {
			double best = -1;
			int bestVal = 0;
			for (int j = 0; j < self.getDomain(); j++) {
				self.setValue(j);
				double sum = 0;
				for (AbstractConstraint<Integer> c : self.neighbors) {
					AbstractVariable<Integer> n = c.getNeighbor(self);
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
		this.recover();

		// if (node.parent == null && node.mat.data[0]>128)
		// System.out.println("REWARD " + node.id + " " + node.reward + " "
		// + node.mat.data[0]);
	}
}
