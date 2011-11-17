package negotiation.experimentationframework;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import dimaxx.server.HostIdentifier;

public class MachineNetwork extends HashMap<HostIdentifier,Integer> {

	public MachineNetwork(List<HostIdentifier> machines) {
		super();
		for (HostIdentifier h : machines)
			this.put(h, new Integer(0));
	}

	public Collection<HostIdentifier> reserveMachine(ExperimentationProtocol e, int nbAgents) 
	throws NotEnoughMachinesException{
		int numberOfAgentPerMachine=Math.max(1,nbAgents/e.getNumberOfMachinePerSimulation());
		LinkedList<HostIdentifier> foundMachine=new LinkedList<HostIdentifier>();
		for (HostIdentifier h : keySet()){
			if (this.get(h)+numberOfAgentPerMachine<=e.getMaxNumberOfAgentPerMachine(h))
				foundMachine.add(h);
			if (foundMachine.size()==e.getNumberOfMachinePerSimulation())
				break;
		}
		if (foundMachine.size()<e.getNumberOfMachinePerSimulation())
			throw new NotEnoughMachinesException();
		else{
			for (HostIdentifier h : foundMachine)
				this.put(h, this.get(h)+numberOfAgentPerMachine);				
			return foundMachine;
		}
	}

	public void freeMachine(HashMap<AgentIdentifier, HostIdentifier> agent_machines){
		for (AgentIdentifier id : agent_machines.keySet()){
			HostIdentifier hostOfAgent = agent_machines.get(id);
			this.put(hostOfAgent, this.get(hostOfAgent)-1);
		}
	}

}
