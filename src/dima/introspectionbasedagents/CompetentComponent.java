package dima.introspectionbasedagents;

import java.io.Serializable;
import java.util.Collection;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basicinterfaces.ActiveComponentInterface;
import dima.basicinterfaces.IdentifiedComponentInterface;
import dima.introspectionbasedagents.services.AgentCompetence;
import dima.introspectionbasedagents.services.core.loggingactivity.LogService;

public interface CompetentComponent extends ActiveComponentInterface, IdentifiedComponentInterface{

	//
	// Hook
	//

	public boolean when(
			AgentCompetence comp,
			String compMethodToTest, String agMethodToExecute);

	public boolean when(
			AgentCompetence comp,
			String compMethodToTest,  Object[] compargs,
			String agMethodToExecute, Object[] agargs);

	public boolean when(
			AgentCompetence comp,
			String compMethodToTest,
			String agMethodToExecute, Object[] agargs);

	public boolean when(
			AgentCompetence comp,
			String compMethodToTest,  Object[] compargs,
			String agMethodToExecute);

	public boolean when(
			AgentCompetence comp,
			String compMethodToTest,  Class<?>[] compSignature, Object[] compargs,
			String agMethodToExecute,  Class<?>[] agSignature, Object[] agargs);

	/**
	 * retryWhen order the reexecution of the method it is called when the competence boolean method is verified.
	 * !!!! The caller method must not take any argument !!!!
	 * @param  methodComponent : must be "this"
	 * @param comp
	 * @param methodToTest
	 * @param objects
	 * @return
	 */
	public boolean retryWhen(AgentCompetence comp,
			String methodToTest,  Object[] testArgs, Object[] methodsArgs);

	public boolean retryWhen(AgentCompetence comp,
			String methodToTest, ActiveComponentInterface methodComponent, Object[] testArgs, Object[] methodsArgs);
	/*
	 * 
	 */


	public boolean whenIsReady(NotReadyException e);
	
	//
	// Competence
	//

	/*
	 * Pattern Observer
	 */

	public <Notification extends Serializable> Boolean notify(
			Notification notification, String key) ;

	public <Notification extends Serializable> Boolean notify(
			Notification notification);

	/**/

	public void observe(AgentIdentifier observedAgent, Class<?> notificationKey);

	public void observe(AgentIdentifier observedAgent,
			String notificationToObserve);

	public void stopObservation(AgentIdentifier observedAgent,
			Class<?> notificationKey);

	public void stopObservation(AgentIdentifier observedAgent,
			String notificationToObserve);

	public void autoObserve(final Class<?> notificationKey);
	
	public void addObserver(final AgentIdentifier observerAgent, final Class<?> notificationKey);

	public void addObserver(final AgentIdentifier observerAgent, final String notificationKey);

	public void removeObserver(final AgentIdentifier observerAgent, final Class<?> notificationKey);

	public void removeObserver(final AgentIdentifier observerAgent, final String notificationKey);

	/**/
	
	public Boolean isObserved(final Class<?> notificationKey);
	
	public Collection<AgentIdentifier> getObservers(final Class<?> notificationKey);
	
	/**/

	public Boolean addToBlackList(AgentIdentifier o, Boolean add) ;

	/*
	 * Log
	 */


	public Boolean logException(String text, Throwable e);

	public Boolean logException(String text, String details, Throwable e);

	public Boolean logException(String text, String details);

	public Boolean logException(String text);

	public Boolean logMonologue(String text, String details);

	public Boolean logMonologue(String text);

	public Boolean logWarning(String text, Throwable e);

	public Boolean logWarning(String text, String details, Throwable e);

	public Boolean logWarning(String text, String details);

	public Boolean logWarning(String text);

}
