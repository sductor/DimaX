/****
 *	Copyright (c) 2007, Olivier Marin, Laboratoire d'Informatique de Paris 6
 *	All rights reserved.
 ****/

/*
 This file is part of DARX.

 DARX is free software: you can redistribute it and/or modify
 it under the terms of the GNU Lesser General Public License as published by
 the Free Software Foundation, either version 3 of the License, or
 (at your option) any later version.

 DARX is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 GNU Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public License
 along with DARX. If not, see <http://www.gnu.org/licenses/>.
 */

package darx;

/**
 * LA DEFINITION QUI SUIT EST CELLE A COURT TERME <BR>
 * Ce composant sert a traduire le changement de valeur de la criticite en
 * modification de strategie.<BR>
 * <BR>
 * <BR>
 * LA DEFINITION QUI SUIT EST CELLE A LONG TERME<BR>
 * This is the component, used alongside each <code>TaskShell</code>, which
 * defines a task's replication group, that is its members and its scheme, and
 * handles its consistency.<BR>
 * This class provides methods to manage the information a specific task
 * possesses about its replication group in terms of replicant (un)registration,
 * that is it maintains up to date the number of replicants for this particular
 * task, the location of each replicant and the strategy it belongs to.<BR>
 * <BR>
 * NB: EVERY replicant, independently of its being leader of the replication
 * group or not, carries and maintains this information up to date so as to be
 * available to take the leadership in case of the current leader's failure.<BR>
 * <BR>
 * NB2: A scheme may be composed of multiple replication strategies.
 *
 * @author Olivier Marin
 *
 * @version %I%, %G%
 *
 * @see ReplicationStrategy
 */
public abstract class ReplicationManager {

	/**
	 * The current criticity of the handled replication group
	 */
	private double criticity;

	/**
	 * Constructs a new ReplicationManager.
	 */
	public ReplicationManager(final TaskShell shell,
			final ReplicationPolicy policy, final double criticity) {
		this.criticity = criticity;
	}

	// SELECTOR

	/**
	 * @return the criticity of the agent.
	 */
	public double getCriticity() {
		return this.criticity;
	}

	/**
	 * Updates the criticity of the replication group. If the newly computed
	 * value for the criticity is different from the previous one, then the
	 * current scheme is modified.
	 *
	 * @param new_criticity
	 *            the newly computed value for the criticity.
	 * @return the latest value of the criticity.
	 */
	public double setCriticity(final double new_criticity) {
		// compare the newly computed criticity with its previous value
		if (this.criticity != new_criticity) {
			this.criticity = new_criticity;
			// launch the scheme adaptation (includes publishing the
			// new criticity value to the rest of the group)
			this.adaptPolicy();
		}
		return this.criticity;
	}

	/**
	 * Adapts the current replication scheme using a specific heuristic.
	 */
	public abstract void adaptPolicy();

	// /**
	// * Adapts the current replication scheme using a specific heuristic.
	// */
	// public void adaptPolicy() {
	// if (criticity < 4) {
	// int update_delay = new Double(4000 - (criticity * 1000)).intValue();
	// PassiveReplicationStrategy new_strat =
	// new PassiveReplicationStrategy(update_delay);
	// try {
	// shell.setReplicationStrategy(new_strat);
	// } catch (RemoteException e) {}
	// } else {
	// ActiveReplicationStrategy new_strat =
	// new ActiveReplicationStrategy();
	// try {
	// shell.setReplicationStrategy(new_strat);
	// } catch (RemoteException e) {}
	// }
	// }

	/**
	 * Compares two replication strategies.
	 *
	 * @param rep_strat_a
	 *            the first strategy to be compared
	 * @param rep_strat_b
	 *            the second strategy to be compared
	 * @return 0 if both strategies are equivalent, a positive integer if
	 *         strategy A is more pessimistic than strategy B, a negative
	 *         integer otherwise.
	 */
	public int compareStrategies(final ReplicationStrategy rep_strat_a,
			final ReplicationStrategy rep_strat_b) {
		return rep_strat_a.compareTo(rep_strat_b);
	}

}
