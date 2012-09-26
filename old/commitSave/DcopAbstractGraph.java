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
import frameworks.faulttolerance.dcop.DcopFactory.DCOPType;
import frameworks.faulttolerance.dcop.algo.topt.AsyncHelper;
import frameworks.faulttolerance.dcop.algo.topt.DPOPTreeNode;
import frameworks.faulttolerance.dcop.algo.topt.RewardMatrix;
import frameworks.faulttolerance.dcop.algo.topt.TreeNode;



public abstract class DcopAbstractGraph<Value> {

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
	public HashMap<Integer, AbstractVariable<Value>> varMap;
	public Vector<AbstractConstraint<Value>> conList;

	public DcopAbstractGraph() {
		varMap = new HashMap<Integer, AbstractVariable<Value>>();
		conList = new Vector<AbstractConstraint<Value>>();
=======
	public HashMap<Integer, AbstractVariable> varMap;
	public Vector<AbstractConstraint> conList;

	public DcopAbstractGraph() {
		varMap = new HashMap<Integer, AbstractVariable>();
		conList = new Vector<AbstractConstraint>();
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
	}

	public DcopAbstractGraph(String inFilename) {
		// We assume in the input file, there is at most one link between two
		// variables
		assert DcopFactory.type==DCOPType.Classical;
		try {
			BufferedReader reader = new BufferedReader(new FileReader(
					inFilename));
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
			varMap = new HashMap<Integer, AbstractVariable<Value>>();
			conList = new Vector<AbstractConstraint<Value>>();
			String line;
			AbstractConstraint<Value> c = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("VARIABLE")) {
					AbstractVariable<Value> v = DcopFactory.constructVariable(line, this);
=======
			varMap = new HashMap<Integer, AbstractVariable>();
			conList = new Vector<AbstractConstraint>();
			String line;
			AbstractConstraint c = null;
			while ((line = reader.readLine()) != null) {
				if (line.startsWith("VARIABLE")) {
					AbstractVariable v = new AbstractVariable(line, this);
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
					varMap.put(v.id, v);
				} else if (line.startsWith("CONSTRAINT")) {
					String[] ss = line.split(" ");
					assert ss.length >= 3;
					int first = Integer.parseInt(ss[1]);
					int second = Integer.parseInt(ss[2]);
					assert varMap.containsKey(first);
					assert varMap.containsKey(second);
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
					c = DcopFactory.constructConstraint(varMap.get(first), varMap.get(second));
=======
					c = new AbstractConstraint(varMap.get(first), varMap.get(second));
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
					conList.add(c);
				} else if (line.startsWith("F")) {
					assert c != null;
					String[] ss = line.split(" ");
					assert ss.length >= 4;
					int x = Integer.parseInt(ss[1]);
					int y = Integer.parseInt(ss[2]);
					int v = Integer.parseInt(ss[3]);
					((ClassicalConstraint)c).f[x][y] = v;
				}
			}
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
			for (AbstractConstraint<Value> cc : conList)
=======
			for (AbstractConstraint cc : conList)
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
				cc.cache();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

	}

	public abstract double evaluate();
	public abstract HashMap<Integer, Value> solve();
	

<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
	public AbstractVariable<Value> getVar(int i) {
=======
	public AbstractVariable getVar(int i) {
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
		return varMap.get(i);
	}

	public boolean checkValues() {
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
		for (AbstractVariable<Value> v : varMap.values())
			if (!v.isInstaciated())
=======
		for (AbstractVariable v : varMap.values())
			if (v.value == -1)
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
				return false;
		return true;
	}
	
	public double evaluate(HashMap<Integer, Value> sol) {
		this.backup();
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
		for (AbstractVariable<Value> v : varMap.values())
=======
		for (AbstractVariable v : varMap.values())
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
			v.value = sol.get(v.id);
		double sum = this.evaluate();
		this.recover();
		return sum;
	}

	public boolean sameSolution(HashMap<Integer, Value> sol) {
		if (sol == null)
			return false;
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
		for (AbstractVariable<Value> v : varMap.values()) {
=======
		for (AbstractVariable v : varMap.values()) {
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
			if (!sol.containsKey(v.id))
				return false;
			if (v.value != sol.get(v.id))
				return false;
		}
		return true;
	}

	public void clear() {
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
		for (AbstractVariable<Value> v : varMap.values())
=======
		for (AbstractVariable v : varMap.values())
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
			v.clear();
	}

	public void backup() {
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
		for (AbstractVariable<Value> v : varMap.values())
=======
		for (AbstractVariable v : varMap.values())
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
			v.backupValue();
	}

	public void recover() {
<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
		for (AbstractVariable<Value> v : varMap.values())
=======
		for (AbstractVariable v : varMap.values())
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
			v.recoverValue();
	}


<<<<<<< HEAD:src/frameworks/faulttolerance/dcop/dcop/DcopAbstractGraph.java
	public HashMap<Integer, Value> getSolution() {
		HashMap<Integer, Value> map = new HashMap<Integer, Value>();
		for (AbstractVariable<Value> v : varMap.values())
=======
	public HashMap<Integer, Integer> getSolution() {
		HashMap<Integer, Integer> map = new HashMap<Integer, Integer>();
		for (AbstractVariable v : varMap.values())
>>>>>>> dcopX:old/commitSave/DcopAbstractGraph.java
			map.put(v.id, v.value);
		return map;
	}

}
