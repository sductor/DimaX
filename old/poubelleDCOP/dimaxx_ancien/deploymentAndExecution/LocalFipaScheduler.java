package dimaxx.deploymentAndExecution;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;

import dima.introspectionbasedagents.competences.DuplicateCompetenceException;
import dima.introspectionbasedagents.competences.UnInstanciableCompetenceException;
import dima.introspectionbasedagents.libraries.loggingactivity.LoggerManager;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.ProactiveComponents.ProactiveComponent;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

public class LocalFipaScheduler extends ArrayList<BasicCommunicatingAgent>{


	/**
	 *
	 */
	private static final long serialVersionUID = -6806175893332597817L;
	public static int step = 0;

	public LocalFipaScheduler(final APILauncherAgent c) throws UnInstanciableCompetenceException, DuplicateCompetenceException {
		super(c.getAgents().size());
		AgentManagementSystem.initAMS();
		for (final BasicCommunicatingAgent a : c.getAgents()){
			this.add(a);
			AgentManagementSystem.getDIMAams().addAquaintance(a);
		}
	}

	public void initialize(){
		for (final ProactiveComponent c : this)
			c.proactivityInitialize();
	}

	@Override
	public boolean add(final BasicCommunicatingAgent c){
		AgentManagementSystem.getDIMAams().addAquaintance(c);
		return super.add(c);
	}

	public void executeStep(){
		Collections.shuffle(this);
		for (final ProactiveComponent c : this){
//			LoggerManager.write("\n\n-------------------->");//SIMULATION : executing "+c.toString()+"***********");
			c.preActivity();
//			LoggerManager.flush();
			c.step();
//			LoggerManager.flush();
			c.postActivity();
//			LoggerManager.flush();
		}

		final Iterator<BasicCommunicatingAgent> it = this.iterator();
		while(it.hasNext()){
			final ProactiveComponent c = it.next();
			if (!c.isAlive()){
				c.proactivityTerminate();
				it.remove();
			}
		}
	}

	public void runApplication(){
		this.initialize();
		int step = 0;
		while (!this.isEmpty()){
			LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");
			LoggerManager.flush();
			this.executeStep();
			LoggerManager.flush();
			step++;
		}
		LoggerManager.write("\n\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
		System.exit(1);
	}

	public void runApplication(final int nbMaxStep){
		this.initialize();
		LoggerManager.flush();
		while (!( this.isEmpty() || step > nbMaxStep) ){
			LoggerManager.write("\n\n***********SIMULATION : starting step "+step+", nbAgent:"+this.size()+"***********\n\n\n");
			LoggerManager.flush();
			this.executeStep();
			LoggerManager.flush();
			step++;
		}
		LoggerManager.write("\n\n***********SIMULATION : END OF SIMULATION***********\n\n\n");
		System.exit(1);
	}
}
