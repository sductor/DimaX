package examples.EcoResolution;


//******************************************************************
//Thread d'execution
//******************************************************************

class Execution extends Thread {

	EcoNPuzzle ecoTq;

	Execution( final EcoNPuzzle et) {
		this.ecoTq = et;
	}
	@Override
	public void run() {
		this.ecoTq.run();
	}
}
