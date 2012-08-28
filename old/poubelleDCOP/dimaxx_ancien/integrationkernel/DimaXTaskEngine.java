package dimaxx.integrationkernel;

import dima.introspectionbasedagents.libraries.loggingactivity.LoggerManager;




public class DimaXTaskEngine extends Thread {

	DimaXTask<?> task;

	public DimaXTaskEngine(final DimaXTask<?> task) {
		super();
		this.task = task;
	}

	/**
	 * Run the execution loop of the associated agent, while this one is alive : <br>
	 * initialize() <br>
	 * while (alive) do <br>
	 *  - pre() <br>
	 *  - step() <br>
	 *  - post() <br>
	 * terminate()
	 */
	@Override
	public void run() {
		// Initiate
		if (this.task.dimaComponent.isAlive()){

			this.task.dimaComponent.proactivityInitialize();
			Thread.yield();

			// Run
			while (this.task.dimaComponent.isAlive())
				if (this.task.isActive()){//Allows Suspend & Resume

					this.task.dimaComponent.preActivity();
					Thread.yield();
					this.task.dimaComponent.step();
					Thread.yield();
					this.task.dimaComponent.postActivity();
					Thread.yield();
				}

			// Terminate
			LoggerManager.write(this, "Terminating "+ this.task.getTaskName());
			this.task.terminate();
			Thread.yield();
		}
	}
}
