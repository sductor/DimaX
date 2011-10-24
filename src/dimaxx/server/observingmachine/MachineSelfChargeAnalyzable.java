package dimaxx.server.observingmachine;

/**
 * Fournit à l'hôte des informations sur l'utilisation de ces ressources
 *
 * @author Ductor Sylvain
 */
public interface MachineSelfChargeAnalyzable {

	public interface Charge {

		public Double getMemoryCharge();

		public Double getProcessorCharge();

		public Double getAbsoluteCharge();
	}

	public Charge getMyCharge();
}