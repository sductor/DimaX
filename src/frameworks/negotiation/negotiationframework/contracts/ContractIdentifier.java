package frameworks.negotiation.negotiationframework.contracts;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import frameworks.negotiation.negotiationframework.NegotiationParameters;

public class ContractIdentifier implements DimaComponentInterface {
	private static final long serialVersionUID = -2323466527487757579L;

	private final AgentIdentifier initiator;
	private final Collection<AgentIdentifier> participants;
	private final Date contractCreation;
	private final long validityTime;

	public ContractIdentifier(final AgentIdentifier intiator, final Date date,
			final long validityTime,
			final Collection<AgentIdentifier> participants) {
		super();
		this.contractCreation = date;
		this.initiator = intiator;
		this.participants = participants;
		//		this.participants.remove(this.getInitiator());
		this.validityTime = validityTime;
	}

	public ContractIdentifier(final AgentIdentifier intiator, final Date date,
			final long validityTime, final AgentIdentifier... participants) {
		super();
		this.contractCreation = date;
		this.initiator = intiator;
		this.participants = Arrays.asList(participants);
		//		this.participants.remove(this.getInitiator());
		this.validityTime = validityTime;
	}

	public AgentIdentifier getInitiator() {
		return this.initiator;
	}

	public Collection<AgentIdentifier> getNotInitiatingParticipants() {
		final Collection<AgentIdentifier> result = new HashSet<AgentIdentifier>();
		result.addAll(this.participants);
		result.remove(this.getInitiator());
		return result;
	}

	public Collection<AgentIdentifier> getParticipants() {
		return this.participants;
	}

	@Override
	public int hashCode() {
		// return toString().hashCode();
		// On choisit les deux nombres impairs
		int result = 7;
		final int multiplier = 17;

		// Pour chaque attribut, on calcule le hashcode
		// que l'on ajoute au résultat après l'avoir multiplié
		// par le nombre "multiplieur" :
		result = multiplier * result + this.initiator.toString().hashCode();
		for (final AgentIdentifier p : this.participants) {
			result = multiplier * result + p.toString().hashCode();
		}
		// result = multiplier*result + (contractCreation.hashCode());

		// On retourne le résultat :
		return result;
	}

	@Override
	public boolean equals(final Object that) {
		if (that instanceof ContractIdentifier) {
			if (((ContractIdentifier) that).initiator.equals(this.initiator)
					&& ((ContractIdentifier) that).participants.equals(this.participants)) {
				assert ((ContractIdentifier)that).contractCreation.equals(this.contractCreation):
					"un agent a envoyé deux prop dans la mm session!!\n"+this+that;
				return true;
			}
		}
		return false;
	}

	@Override
	public String toString() {
		return "\nContract ("
				+ this.contractCreation.getTime()+ "   " +this.contractCreation.toGMTString()
				+ ") (init : "
				+ this.initiator
				+ ", part : "
				+ this.getParticipants()
				+ "),(expired?"
				+ this.hasReachedExpirationTime()
				+ ", will expire?"
				+ this.willReachExpirationTime(NegotiationParameters._timeToCollect)
				+ ")";
	}

	public long getUptime() {
		return new Date().getTime() - this.contractCreation.getTime();
	}

	public boolean hasReachedExpirationTime() {
		return this.getUptime() > this.validityTime;
	}

	public boolean willReachExpirationTime(final long t) {
		return this.getUptime() + t > this.validityTime;
	}
}
