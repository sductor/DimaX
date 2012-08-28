package frameworks.negotiation.negotiationframework.protocoles.status;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;


import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.introspectionbasedagents.kernel.NotReadyException;
import dima.introspectionbasedagents.services.AgentCompetence;

import frameworks.negotiation.negotiationframework.NegotiatingAgent;
import frameworks.negotiation.negotiationframework.contracts.AbstractContractTransition;
import frameworks.negotiation.negotiationframework.contracts.ContractTrunk;
import frameworks.negotiation.negotiationframework.contracts.MatchingCandidature;
import frameworks.negotiation.negotiationframework.protocoles.AbstractCommunicationProtocol.SelectionCore;
import frameworks.negotiation.negotiationframework.rationality.AgentState;
import frameworks.negotiation.negotiationframework.selection.GreedySelectionModule;
import frameworks.negotiation.negotiationframework.selection.SelectionModule;
import frameworks.negotiation.negotiationframework.selection.SimpleSelectionCore;

public class StatusSelectionCore<
Agent extends NegotiatingAgent<PersonalState, Contract>,
PersonalState extends AgentState,
Contract extends MatchingCandidature>
implements SelectionCore<Agent,PersonalState, Contract>{

	final SimpleSelectionCore<Agent,PersonalState,Contract> referenceSelectionCore;
	Boolean optimizeDestruction;
	public StatusSelectionCore(SimpleSelectionCore referenceSelectionCore, Boolean optimizeDestruction) {
		this.referenceSelectionCore = referenceSelectionCore;
		this.optimizeDestruction=optimizeDestruction;
	}

	@Override
	public void select(
			final ContractTrunk<Contract> given,
			PersonalState s,
			final Collection<Contract> toAccept,
			final Collection<Contract> toReject,
			final Collection<Contract> toPutOnWait) {

		Collection<Contract> anticipatedAcceptation = new ArrayList<Contract>();

		if (optimizeDestruction){	
			for (Contract c : given.getAllParticipantContracts()){
				if (!c.isMatchingCreation()){
					toAccept.add(c);
					s = getMyAgent().getMyResultingState(c);
					given.addAcceptation(getIdentifier(), c);
					anticipatedAcceptation.add(c);
				}			
			}
		}

		referenceSelectionCore.select(given, s, toAccept, toReject, toPutOnWait);

		for (Contract c : anticipatedAcceptation){
			assert toAccept.contains(c);
			assert !toReject.contains(c);
			toPutOnWait.remove(c);
		}
	}

	/*
	 * 
	 */

	public AgentIdentifier getIdentifier() {
		return referenceSelectionCore.getIdentifier();
	}

	public Agent getMyAgent() {
		return referenceSelectionCore.getMyAgent();
	}

	public final boolean isActive() {
		return referenceSelectionCore.isActive();
	}

	public int hashCode() {
		return referenceSelectionCore.hashCode();
	}

	public void setActive(boolean active) {
		referenceSelectionCore.setActive(active);
	}

	public void die() {
		referenceSelectionCore.die();
	}

	public boolean retryWhen(AgentCompetence comp, String methodToTest,
			ActiveComponentInterface methodComponent, Object[] testArgs,
			Object[] methodsArgs) {
		return referenceSelectionCore.retryWhen(comp, methodToTest,
				methodComponent, testArgs, methodsArgs);
	}

	public void setMyAgent(Agent ag) {
		referenceSelectionCore.setMyAgent(ag);
	}

	public boolean when(AgentCompetence comp, String compMethodToTest,
			Class<?>[] compSignature, Object[] compargs,
			String agMethodToExecute, Class<?>[] agSignature, Object[] agargs) {
		return referenceSelectionCore
				.when(comp, compMethodToTest, compSignature, compargs,
						agMethodToExecute, agSignature, agargs);
	}

	public boolean when(AgentCompetence comp, String compMethodToTest,
			Object[] compargs, String agMethodToExecute, Object[] agargs) {
		return referenceSelectionCore.when(comp, compMethodToTest, compargs,
				agMethodToExecute, agargs);
	}

	public boolean when(AgentCompetence comp, String compMethodToTest,
			Object[] compargs, String agMethodToExecute) {
		return referenceSelectionCore.when(comp, compMethodToTest, compargs,
				agMethodToExecute);
	}

	public boolean when(AgentCompetence comp, String compMethodToTest,
			String agMethodToExecute, Object[] agargs) {
		return referenceSelectionCore.when(comp, compMethodToTest,
				agMethodToExecute, agargs);
	}

	public boolean when(AgentCompetence comp, String compMethodToTest,
			String agMethodToExecute) {
		return referenceSelectionCore.when(comp, compMethodToTest,
				agMethodToExecute);
	}

	public boolean retryWhen(AgentCompetence comp, String methodToTest,
			Object[] testArgs, Object[] methodsArgs) {
		return referenceSelectionCore.retryWhen(comp, methodToTest, testArgs,
				methodsArgs);
	}

	public boolean equals(Object obj) {
		return referenceSelectionCore.equals(obj);
	}

	public boolean whenIsReady(NotReadyException e) {
		return referenceSelectionCore.whenIsReady(e);
	}

	public Boolean signalException(String text, Throwable e) {
		return referenceSelectionCore.signalException(text, e);
	}

	public Boolean signalException(String text) {
		return referenceSelectionCore.signalException(text);
	}

	public Boolean logMonologue(String text, String details) {
		return referenceSelectionCore.logMonologue(text, details);
	}

	public Boolean logMonologue(String text) {
		return referenceSelectionCore.logMonologue(text);
	}

	public Boolean logWarning(String text, Throwable e, String details) {
		return referenceSelectionCore.logWarning(text, e, details);
	}

	public Boolean logWarning(String text, String details) {
		return referenceSelectionCore.logWarning(text, details);
	}

	public Boolean logWarning(String text, Throwable e) {
		return referenceSelectionCore.logWarning(text, e);
	}

	public Boolean logWarning(String text) {
		return referenceSelectionCore.logWarning(text);
	}

	public void addLogKey(String key, boolean toString, boolean toFile) {
		referenceSelectionCore.addLogKey(key, toString, toFile);
	}

	public void addLogKey(String key, String logType) {
		referenceSelectionCore.addLogKey(key, logType);
	}

	public void setLogKey(String key, boolean toScreen, boolean toFile) {
		referenceSelectionCore.setLogKey(key, toScreen, toFile);
	}

	public SelectionModule<Agent, PersonalState, Contract> getSelectionModule() {
		return referenceSelectionCore.getSelectionModule();
	}

	public <Notification extends Serializable> Boolean notify(
			Notification notification, String key) {
		return referenceSelectionCore.notify(notification, key);
	}

	public <Notification extends Serializable> Boolean notify(
			Notification notification) {
		return referenceSelectionCore.notify(notification);
	}

	public Boolean addToBlackList(AgentIdentifier o, Boolean add) {
		return referenceSelectionCore.addToBlackList(o, add);
	}

	public void observe(AgentIdentifier observedAgent, Class<?> notificationKey) {
		referenceSelectionCore.observe(observedAgent, notificationKey);
	}

	public void observe(AgentIdentifier observedAgent,
			String notificationToObserve) {
		referenceSelectionCore.observe(observedAgent, notificationToObserve);
	}

	public void stopObservation(AgentIdentifier observedAgent,
			Class<?> notificationKey) {
		referenceSelectionCore.stopObservation(observedAgent, notificationKey);
	}

	public void stopObservation(AgentIdentifier observedAgent,
			String notificationToObserve) {
		referenceSelectionCore.stopObservation(observedAgent,
				notificationToObserve);
	}

	public void autoObserve(Class<?> notificationKey) {
		referenceSelectionCore.autoObserve(notificationKey);
	}

	public void addObserver(AgentIdentifier observerAgent,
			Class<?> notificationKey) {
		referenceSelectionCore.addObserver(observerAgent, notificationKey);
	}

	public void addObserver(AgentIdentifier observerAgent,
			String notificationKey) {
		referenceSelectionCore.addObserver(observerAgent, notificationKey);
	}

	public void removeObserver(AgentIdentifier observerAgent,
			Class<?> notificationKey) {
		referenceSelectionCore.removeObserver(observerAgent, notificationKey);
	}

	public void removeObserver(AgentIdentifier observerAgent,
			String notificationKey) {
		referenceSelectionCore.removeObserver(observerAgent, notificationKey);
	}

	public void sendNotificationNow() {
		referenceSelectionCore.sendNotificationNow();
	}

	public Boolean isObserved(Class<?> notificationKey) {
		return referenceSelectionCore.isObserved(notificationKey);
	}

	public Collection<AgentIdentifier> getObservers(Class<?> notificationKey) {
		return referenceSelectionCore.getObservers(notificationKey);
	}

	public String toString() {
		return referenceSelectionCore.toString();
	}

	/*
	 * 
	 */



}


