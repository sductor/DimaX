package frameworks.faulttolerance.negotiatingagent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Vector;

import choco.kernel.model.constraints.ConstraintType;
import dima.basicagentcomponents.AgentIdentifier;

import frameworks.faulttolerance.dcop.DCOPFactory;
import frameworks.faulttolerance.dcop.dcop.CPUFreeConstraint;
import frameworks.faulttolerance.dcop.dcop.CPUFreeVariable;
import frameworks.faulttolerance.dcop.dcop.DcopReplicationGraph;
import frameworks.faulttolerance.dcop.dcop.ReplicationConstraint;
import frameworks.faulttolerance.dcop.dcop.ReplicationVariable;
import frameworks.faulttolerance.solver.SolverFactory;
import frameworks.negotiation.contracts.AbstractContractTransition.IncompleteContractException;
import frameworks.negotiation.exploration.ResourceAllocationSolver;

public class DcopReplicationHostGraph 
extends DcopReplicationGraph 
implements ResourceAllocationSolver
<ReplicationCandidature, HostState>{

	HashMap<ReplicationVariable, ReplicationCandidature> concerned;
	int myId;
	
	public DcopReplicationHostGraph() {
		super();	
		varMap = new HashMap<Integer, ReplicationVariable>();
		conList = new Vector<ReplicationConstraint>();
	}

	public DcopReplicationHostGraph(
			final Collection<ReplicationCandidature> concerned) throws IncompleteContractException{
		super();	
		varMap = new HashMap<Integer, ReplicationVariable>();
		conList = new Vector<ReplicationConstraint>();
		initiate(concerned);

	}

	@Override
	public void initiate(Collection<ReplicationCandidature> concerned) {
		try {
			assert !concerned.isEmpty();

			final HostState me = concerned.iterator().next().getResourceInitialState();
			ReplicationVariable varMe = DCOPFactory.constructVariable(
					DCOPFactory.identifierToInt(me.getMyAgentIdentifier()), 
					(int) Math.pow(2, concerned.size()), me, this);
			
			varMap.put(varMe.id, varMe);
			myId=varMe.id;
			
			for (ReplicationCandidature c : concerned){
				assert c.getResourceInitialState().equals(me);
				ReplicationVariable a =  DCOPFactory.constructVariable(
						DCOPFactory.identifierToInt(c.getAgent()), 
						2, c.getAgentInitialState(), this);
				varMap.put(a.id,a);
				this.concerned.put(a,c);

				conList.add(DCOPFactory.constructConstraint(a, varMe));
			}
			
			if (SolverFactory.NegoIsMemoryConsumming()){
				instanciateConstraintsValues();
			}

		} catch (final IncompleteContractException e) {
			throw new RuntimeException("noooooooooooooooonnnnnnnnnn",e);
		}
	}

	@Override
	public Collection<ReplicationCandidature> getBestSolution() {
		return generateSolution(SolverFactory.solve(this));
	}

	@Override
	public boolean hasNext() {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public Collection<ReplicationCandidature> getNextSolution() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setTimeLimit(int millisec) {
		// TODO Auto-generated method stub

	}

	/**
	 * Transforme la solution actuelle du solveur en candidature accepté
	 * @return la liste des candidature de la solution du solveur différentes de l'allcoation courante
	 */
	private Collection<ReplicationCandidature> generateSolution(HashMap<Integer, Integer> alloc){
		final ArrayList<ReplicationCandidature> results = new ArrayList<ReplicationCandidature>();

		for (ReplicationVariable var : varMap.values()){
			if (var.id!=myId){
				boolean allocated=alloc.get(var.id)==1;
				ReplicationCandidature c = concerned.get(var.id);
				if (c.isMatchingCreation() && allocated || !c.isMatchingCreation() && !allocated) {
					results.add(c);
				}
			}
		}
		return results;
	}
}
