package frameworks.faulttolerance.dcop.dcop;

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

import frameworks.faulttolerance.dcop.DcopFactory;
import frameworks.faulttolerance.dcop.algo.Algorithm;
import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;

import frameworks.experimentation.IfailedException;
import frameworks.faulttolerance.experimentation.ReplicationInstanceGraph;


public class DcopClassicalGraph<Value> extends DcopReplicationGraph<Value>{

	private double _maxReward;
	private HashMap<Integer, Value> _bestSolution;
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
		for (AbstractConstraint c : conList)
			sum += c.evaluate();
				return sum;
	}


	@Override
	public HashMap<Integer, Value> solve() {
		if (algo==Algorithm.TOPTAPO && conList.size() > varMap.size() * varMap.size() / 4)
			return branchBoundSolve();
		else
			return DPOPSolve();
	}

	/*
	 * 
	 */

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
	private HashMap<Integer, Value> branchBoundSolve() {
		ArrayList<AbstractVariable<Value>> list = new ArrayList<AbstractVariable<Value>>();
		for (AbstractVariable<Value> v : varMap.values())
			list.add(v);
				Collections.sort(list, new Comparator<AbstractVariable<Value>>() {
					public int compare(AbstractVariable<Value> v0, AbstractVariable<Value> v1) {
=======
	private HashMap<Integer, Integer> branchBoundSolve() {
		ArrayList<AbstractVariable> list = new ArrayList<AbstractVariable>();
		for (AbstractVariable v : varMap.values())
			list.add(v);
				Collections.sort(list, new Comparator<AbstractVariable>() {
					public int compare(AbstractVariable v0, AbstractVariable v1) {
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
						if (v0.getDegree() >= v1.getDegree())
							return -1;
						else
							return 1;
					}
				});
				int[] queue = new int[list.size()];
				int idx = 0;
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
				for (AbstractVariable<Value> v : list) {
=======
				for (AbstractVariable v : list) {
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
					queue[idx] = v.id;
					idx++;
				}

				this.backup();

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
				for (AbstractVariable<Value> v : varMap.values())
					if (!v.fixed && !v.isInstaciated())
=======
				for (AbstractVariable v : varMap.values())
					if (!v.fixed && v.value == -1)
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
						v.value = 0;

						_maxReward = this.evaluate();
						_bestSolution = this.getSolution();

						// int r = _maxReward;

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
						for (AbstractVariable<Value> v : varMap.values()) {
=======
						for (AbstractVariable v : varMap.values()) {
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
							if (!v.fixed)
								v.uninstanciate();
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
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		AbstractVariable<Value> v = varMap.get(queue[idx]);
=======
		AbstractVariable v = varMap.get(queue[idx]);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
		if (v.fixed)
			_bbEnumerate(queue, idx + 1);
		else {
			for (int i = 0; i < v.domain; i++) {
				v.value = i;
				double val = this.evaluate();
				if (val <= _maxReward)
					continue;
				_bbEnumerate(queue, idx + 1);
			}
			v.value = -1;
		}
	}

	public DcopClassicalGraph<Value> simplifyGraph() {
		DcopClassicalGraph<Value> tmp = new DcopClassicalGraph<Value>();
		int maxId = 0;
		boolean f = false;
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		for (AbstractVariable<Value> v : this.varMap.values()) {
			if (v.id > maxId)
				maxId = v.id;
			if (!v.fixed)
				tmp.varMap.put(v.id, DcopFactory.constructVariable(v.id, v.domain, tmp));
=======
		for (AbstractVariable v : this.varMap.values()) {
			if (v.id > maxId)
				maxId = v.id;
			if (!v.fixed)
				tmp.varMap.put(v.id, new AbstractVariable(v.id, v.domain, tmp));
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
			else {
				f = true;
				assert v.value != -1;
			}
		}
		maxId++;
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		AbstractVariable<Value> s = DcopFactory.constructVariable(maxId, 1, tmp);
		if (f)
			tmp.varMap.put(s.id, s);
		for (AbstractConstraint<Value> c : this.conList) {
			if (!c.getFirst().fixed && !c.getSecond().fixed) {
				AbstractConstraint<Value> cc = DcopFactory.constructConstraint(tmp.varMap.get(c.getFirst().id),
						tmp.varMap.get(c.getSecond().id));
				if (DcopFactory.isClassical()){
=======
		AbstractVariable s = new AbstractVariable(maxId, 1, tmp);
		if (f)
			tmp.varMap.put(s.id, s);
		for (AbstractConstraint c : this.conList) {
			if (!c.first.fixed && !c.second.fixed) {
				AbstractConstraint cc = new AbstractConstraint(tmp.varMap.get(c.first.id),
						tmp.varMap.get(c.second.id));
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
				for (int i = 0; i < cc.d1; i++)
					for (int j = 0; j < cc.d2; j++)
						((ClassicalConstraint)cc).f[i][j] = ((ClassicalConstraint)c).f[i][j];
				}
				tmp.conList.add(cc);
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
			} else if (c.getFirst().fixed && !c.getSecond().fixed) {
				AbstractConstraint<Value> cc = s.getNeighbor(c.getSecond().id);
				if (cc == null) {
					cc = DcopFactory.constructConstraint(tmp.varMap.get(c.getSecond().id), s);
=======
			} else if (c.first.fixed && !c.second.fixed) {
				AbstractConstraint cc = s.getNeighbor(c.second.id);
				if (cc == null) {
					cc = new AbstractConstraint(tmp.varMap.get(c.second.id), s);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
					tmp.conList.add(cc);
				}
				if (DcopFactory.isClassical()){
				for (int i = 0; i < cc.d1; i++)
					((ClassicalConstraint)cc).f[i][0] += ((ClassicalConstraint)c).f[((ClassicalConstraint)c).getFirst().value][i];
				}

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
			} else if (!c.getFirst().fixed && c.getSecond().fixed) {
				AbstractConstraint<Value> cc = s.getNeighbor(c.getFirst().id);
				if (cc == null) {
					cc = DcopFactory.constructConstraint(tmp.varMap.get(c.getFirst().id), s);
=======
			} else if (!c.first.fixed && c.second.fixed) {
				AbstractConstraint cc = s.getNeighbor(c.first.id);
				if (cc == null) {
					cc = new AbstractConstraint(tmp.varMap.get(c.first.id), s);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
					tmp.conList.add(cc);
				}
				if (DcopFactory.isClassical()){
				for (int i = 0; i < cc.d1; i++)
					((ClassicalConstraint)cc).f[i][0] += ((ClassicalConstraint)c).f[i][((ClassicalConstraint)c).getSecond().value];
				}
			}
		}
		return tmp;
	}

	public int getMaxId() {
		int max = 0;
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		for (AbstractVariable<Value> v : varMap.values())
=======
		for (AbstractVariable v : varMap.values())
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
			if (v.id > max)
				max = v.id;
				return max;
	}

	public HashMap<Integer, Value> DPOPSolve() {
		DcopClassicalGraph<Value> tmp = this.simplifyGraph();
		int maxId = tmp.getMaxId();
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		AbstractVariable<Value> top = tmp.varMap.get(maxId);
		HashMap<Integer, Value> sol = tmp._DPOPSolve(top);
		HashMap<Integer, Value> fsol = new HashMap<Integer, Value>();
		for (Integer i : sol.keySet())
			if (this.varMap.containsKey(i))
				fsol.put(i, sol.get(i));
				for (AbstractVariable<Value> v : varMap.values()) {
=======
		AbstractVariable top = tmp.varMap.get(maxId);
		HashMap<Integer, Integer> sol = tmp._DPOPSolve(top);
		HashMap<Integer, Integer> fsol = new HashMap<Integer, Integer>();
		for (Integer i : sol.keySet())
			if (this.varMap.containsKey(i))
				fsol.put(i, sol.get(i));
				for (AbstractVariable v : varMap.values()) {
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
					if (v.fixed)
						fsol.put(v.id, v.value);
				}
				//System.out.println(this.evaluate(fsol));
				return fsol;
	}

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
	private HashMap<Integer, Value> _DPOPSolve(AbstractVariable<Value> top) {
		HashMap<Integer, Value> map = new HashMap<Integer, Value>();
=======
	private HashMap<Integer, Integer> _DPOPSolve(AbstractVariable top) {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
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
				for (TreeNode<Value> n : parent.children) {
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
			TreeNode<Value> n = node.parent;
			while (n != null) {
				offset += node.mat.getBase(n.id) * n.value;
				n = n.parent;
			}
			node.value = node.mat.value[offset];
			map.put(node.id, node.value);
			for (TreeNode<Value> nn : node.children)
				queue.add((DPOPTreeNode) nn);
		}
		return map;
	}

	private void depthFirstTraverse(DPOPTreeNode parent,
			HashSet<Integer> visited, ArrayList<DPOPTreeNode> leafList) {
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		AbstractVariable<Value> v = this.varMap.get(parent.id);
		assert v != null;
		for (AbstractConstraint<Value> c : v.neighbors) {
			AbstractVariable<Value> n = c.getNeighbor(v);
=======
		AbstractVariable v = this.varMap.get(parent.id);
		assert v != null;
		for (AbstractConstraint c : v.neighbors) {
			AbstractVariable n = c.getNeighbor(v);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
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
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		AbstractVariable<Value> self = varMap.get(node.id);
=======
		AbstractVariable self = varMap.get(node.id);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java

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
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
		for (AbstractConstraint<Value> c : self.neighbors) {
			AbstractVariable<Value> n = c.getNeighbor(self);
=======
		for (AbstractConstraint c : self.neighbors) {
			AbstractVariable n = c.getNeighbor(self);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
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
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
			double best = -1;
=======
			int best = -1;
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
			int bestVal = -1;
			for (int j = 0; j < self.domain; j++) {
				self.value = j;
				double sum = 0;
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopClassicalGraph.java
				for (AbstractConstraint<Value> c : self.neighbors) {
					AbstractVariable<Value> n = c.getNeighbor(self);
=======
				for (AbstractConstraint c : self.neighbors) {
					AbstractVariable n = c.getNeighbor(self);
>>>>>>> dcopX:old/commitSave/DcopClassicalGraph.java
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
}
