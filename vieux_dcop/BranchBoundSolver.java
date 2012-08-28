package frameworks.faulttolerance.dcop.dcop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Vector;

public class BranchBoundSolver {

	private double _maxReward;
	private HashMap<Integer, Integer> _bestSolution;

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
	
	
	private HashMap<Integer, Integer> branchBoundSolve() {
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
			double val = this.evaluate();
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
				double val = this.evaluate();
				if (val <= _maxReward)
					continue;
				_bbEnumerate(queue, idx + 1);
			}
			v.value = -1;
		}
	}
}
