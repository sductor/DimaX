package dima.introspectionbasedagents.kernel;

import dima.introspectionbasedagents.services.AgentCompetence;

public class NotReadyException extends Exception {
	private static final long serialVersionUID = -804054179327698565L;

	final AgentCompetence comp;
	final String compMethodToTest;
	final Class<?>[] compSignature;
	final Object[] compargs;
	final String agMethodToExecute;
	final Class<?>[] agSignature;
	final Object[] agargs;

	public NotReadyException(){
		this(null,null,null,null,null,null,null);
	}

	public NotReadyException(
			final AgentCompetence comp,
			final String compMethodToTest, final String agMethodToExecute){
		this(comp, compMethodToTest, null, new Object[]{}, agMethodToExecute, null, new Object[]{});
	}

	public NotReadyException(
			final AgentCompetence comp,
			final String compMethodToTest,  final Object[] compargs,
			final String agMethodToExecute, final Object[] agargs){
		this(comp, compMethodToTest, null, compargs, agMethodToExecute, null, agargs);
	}

	public NotReadyException(
			final AgentCompetence comp,
			final String compMethodToTest,
			final String agMethodToExecute, final Object[] agargs){
		this(comp, compMethodToTest, null, new Object[]{}, agMethodToExecute, null, agargs);
	}

	public NotReadyException(
			final AgentCompetence comp,
			final String compMethodToTest,  final Object[] compargs,
			final String agMethodToExecute){
		this(comp, compMethodToTest, null, compargs, agMethodToExecute, null, new Object[]{});
	}

	public NotReadyException(final AgentCompetence comp, final String compMethodToTest,
			final Class<?>[] compSignature, final Object[] compargs,
			final String agMethodToExecute, final Class<?>[] agSignature, final Object[] agargs) {
		super();
		this.comp = comp;
		this.compMethodToTest = compMethodToTest;
		this.compSignature = compSignature;
		this.compargs = compargs;
		this.agMethodToExecute = agMethodToExecute;
		this.agSignature = agSignature;
		this.agargs = agargs;
	}



}

