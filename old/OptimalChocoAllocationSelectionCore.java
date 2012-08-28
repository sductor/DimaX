package negotiation.negotiationframework.interaction.selectioncores;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import negotiation.negotiationframework.agent.AgentState;
import negotiation.negotiationframework.interaction.AbstractActionSpecification;
import negotiation.negotiationframework.interaction.MatchingCandidature;
import choco.Choco;
import choco.cp.model.CPModel;
import choco.cp.solver.CPSolver;
import choco.kernel.model.Model;
import choco.kernel.model.constraints.Constraint;
import choco.kernel.model.variables.integer.IntegerVariable;
import choco.kernel.solver.Solver;

public class OptimalChocoAllocationSelectionCore<
PersonalState extends AgentState, 
Contract extends MatchingCandidature<ActionSpec>, 
ActionSpec extends AbstractActionSpecification>
extends AbstractSelectionCore<PersonalState, Contract, ActionSpec> {

	@Override
	protected void select(List<Contract> initiatorContractToExplore,
			List<Contract> participantContractToExplore,
			List<Contract> initiatorOnWaitContract,
			List<Contract> alreadyAccepted, List<Contract> rejected) {


		int nbVariable = 
				initiatorContractToExplore.size()
				+participantContractToExplore.size()
				+initiatorOnWaitContract.size()
				+alreadyAccepted.size();
		MatchingCandidature[] contractTab = new MatchingCandidature[nbVariable];
		IntegerVariable[] allocation = new IntegerVariable[nbVariable];
		int contractNum=0;

		for (Contract c : alreadyAccepted){
			contractTab[contractNum] = c;
			allocation[contractNum] = Choco.makeIntVar(c.toString(), 1, 1);
		}
		for (Contract c : initiatorOnWaitContract){
			contractTab[contractNum] = c;
			allocation[contractNum] = Choco.makeIntVar(c.toString(), 1, 1);
		}
		for (Contract c : initiatorContractToExplore){
			contractTab[contractNum] = c;
			allocation[contractNum] = Choco.makeIntVar(c.toString(), -1, 1);
		}
		for (Contract c : participantContractToExplore){
			contractTab[contractNum] = c;
			allocation[contractNum] = Choco.makeIntVar(c.toString(), -1, 1);
		}

		Model m = new CPModel();
		m.addConstraints(((ChocoConstrainedDeonticAgent) getMyAgent()).getConstraints(contractTab, allocation));

		Solver s = new CPSolver();
		s.read(m);
//		s.maximize(restart)
		s.solve();
		final Collection<Contract> accepted = new ArrayList<Contract>();
		final Collection<Contract> notAccepted = new ArrayList<Contract>();
		final Collection<Contract> onWait = new ArrayList<Contract>();

		for (int i = 0; i < nbVariable; i++){
			if (s.getVar(allocation[i]).getVal()==-1)
				notAccepted.add((Contract) contractTab[i]);
			else if(s.getVar(allocation[i]).getVal()==1)
				accepted.add((Contract) contractTab[i]);
			else  if(s.getVar(allocation[i]).getVal()==0)
				accepted.add((Contract) contractTab[i]);
			else
				throw new RuntimeException("oooh!");
		}
		
		accepted.addAll(onWait);
		
		setAnswer(accepted, notAccepted);//, onWait);
	}

	public interface ChocoConstrainedDeonticAgent {

		/**
		 * Add constraint and limit the domain of variable :
		 * only to -1 : can not be accepted
		 * 1 : accepted
		 * 0 : on wait
		 * @param contractTab
		 * @param allocation
		 * @return
		 */
		Constraint[] getConstraints(
				MatchingCandidature[] contractTab,
				IntegerVariable[] allocation);

	}
}