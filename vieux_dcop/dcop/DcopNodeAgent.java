package vieux.dcop;

import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.introspectionbasedagents.kernel.BasicCompetentAgent;
import dima.introspectionbasedagents.services.CompetenceException;

public class DcopNodeAgent<Value, Constraint> extends BasicCompetentAgent{

	public DcopNodeAgent(String newId) throws CompetenceException {
		super(newId);
	}

	public Collection<AgentIdentifier> getNeighbors() {
		// TODO Auto-generated method stub
		return null;
	}

	public Value getValue() {
		// TODO Auto-generated method stub
		return null;
	}

	public Constraint getConstraint() {
		// TODO Auto-generated method stub
		return null;
	}

	public void updateLocalView(ValueMessage m) {
		// TODO Auto-generated method stub
		
	}

	public void updateConstraint(ConstraintMessage m) {
		// TODO Auto-generated method stub
		
	}

}
