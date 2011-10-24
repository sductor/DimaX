package dima.introspectionbasedagents.ontologies;


public interface MessageWithProtocol {

	public Class<? extends Protocol> getProtocol();
}
