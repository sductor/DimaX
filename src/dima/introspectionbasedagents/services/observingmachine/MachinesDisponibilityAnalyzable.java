package dima.introspectionbasedagents.services.observingmachine;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

import dima.introspectionbasedagents.services.deployment.server.HostIdentifier;



public interface MachinesDisponibilityAnalyzable extends Serializable {

	public interface Disponibility extends Serializable {

		public Double getSurviveProbability();

		public Double getRepairProbability();
	}

	public Collection<HostIdentifier> getAccessibleHosts();

	public void updateDisponibility();

	public Disponibility getDisponibilityOf(Set<HostIdentifier> hosts);

	public Disponibility getDisponibilityOf(HostIdentifier host);
}