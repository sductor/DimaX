package frameworks.negotiation.contracts;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.DimaComponentInterface;
import frameworks.negotiation.NegotiationParameters;

public class ContractIdentifier implements DimaComponentInterface {
	private static final long serialVersionUID = -2323466527487757579L;

	private final AgentIdentifier initiator;
	private final List<AgentIdentifier> participants;
	final Date contractCreation;
	private final long validityTime;

	public ContractIdentifier(final AgentIdentifier intiator, final Date date,
			final long validityTime,
			final Collection<AgentIdentifier> participants) {
		super();
		this.contractCreation = date;
		this.initiator = intiator;
		this.participants = new ArrayList<AgentIdentifier>(participants);
		Collections.sort(this.participants,this.idComp());
		//		this.participants.remove(this.getInitiator());
		this.validityTime = validityTime;
	}


	public ContractIdentifier(final AgentIdentifier intiator, final Date date,
			final long validityTime, final AgentIdentifier... participants) {
		super();
		this.contractCreation = date;
		this.initiator = intiator;
		this.participants = new ArrayList<AgentIdentifier>(Arrays.asList(participants));
		Collections.sort(this.participants,this.idComp());
		//		this.participants.remove(this.getInitiator());
		this.validityTime = validityTime;
	}

	Comparator<AgentIdentifier> idComp(){
		return new Comparator<AgentIdentifier>() {

			@Override
			public int compare(final AgentIdentifier o1, final AgentIdentifier o2) {
				return o1.toString().compareTo(o2.toString());
			}

		};
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

	public Collection<AgentIdentifier> getAllParticipants() {
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
				//				assert ((ContractIdentifier)that).contractCreation.equals(this.contractCreation):
				//					"un agent a envoyé deux prop DIFFERENTE dans la mm session!!\n"+this+that;
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
				+ this.getAllParticipants()
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
