package dima.introspectionbasedagents.ontologies.FIPAACLOntologie;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.Arrays;

import dima.introspectionbasedagents.kernel.MethodHandler;
import dima.introspectionbasedagents.ontologies.Envelope;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.ontologies.FIPAACLOntologie.FipaACLMessage.NoneProtocol;


public class FipaACLEnvelopeClass implements Envelope {

	private static final long serialVersionUID = -9005701398344690301L;

	//
	// Fields
	//

	protected Performative performative;
	protected String content = NoneProtocol.None;
	protected Language language = Language.Any;
	protected Class<? extends Protocol> protocol = NoneProtocol.class;
	protected Ontology ontology = Ontology.Any;
	protected Class<?>[] attachementSignature = {};
	//Signature senderSignature;// !!!!!!!!!!!!!!!!!!!!!!INUTILISE

	//
	// Constructor
	//

	public FipaACLEnvelopeClass(final FipaACLEnvelope f, final MethodHandler mt) {
		this.performative = f.performative();
		this.content = f.content();
		this.language = f.language();
		this.protocol = f.protocol();
		this.ontology = f.ontology();
		//		this.senderClass = f.senderClass();
		this.attachementSignature = f.attachementSignature();
	}

	public FipaACLEnvelopeClass(final FipaACLMessage m) {
		this.performative = m.getPerformative();
		this.content = m.getContent();
		this.language = m.getLanguage();
		this.protocol = m.getProtocol();
		this.ontology = m.getOntology();
		//		if (m.getSender() != null)
		//			this.senderClass = m.getSender().getClass();
		this.attachementSignature = m.getAttachementSignature();

	}

	//
	// Annotation
	//



	@Documented
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	public @interface FipaACLEnvelope {
		// Selection stricte
		Performative performative();

		Class<? extends Protocol> protocol();

		Class<?>[] attachementSignature() default {};

		// Selection lache
		String content() default NoneProtocol.None;

		Ontology ontology() default Ontology.Any;

		Language language() default Language.Any;

		//Signature senderSignature default Object.class;// !!!!!!!!!!!!!!!!!!!!!!INUTILISE
	}

	//
	// Primitive
	//

	@Override
	public boolean equals(final Object o) {
		if (o == this) {
			return true;
		}

		if (o instanceof FipaACLEnvelopeClass) {
			final FipaACLEnvelopeClass e = (FipaACLEnvelopeClass) o;
			if (e.hashCode() == o.hashCode()
					&&
					(this.content.equals(e.content) ||
							e.content.equals(NoneProtocol.None)
							|| this.content.equals(NoneProtocol.None))
							&&
							(this.language.equals(e.language)
									|| e.language.equals(Language.Any) || this.language
									.equals(Language.Any))
									&& (this.ontology.equals(e.ontology)
											|| e.ontology.equals(Ontology.Any) || e.ontology
											.equals(Ontology.Any))) {
				return true;
			} else {
				return false;
			}
		} else {
			return false;
		}
	}

	@Override
	public int hashCode() {
		return this.toString().hashCode();
		// (
		// (performative==null?0:performative.toString().hashCode()) ^
		// (protocol==null?0:protocol.toString().hashCode()) ^
		// (attachementSignature==null?0:attachementSignature.toString().hashCode()));
	}

	@Override
	public String toString() {
		return "FIPAACL Envellope of:"
				+"\n  * perf=" + this.performative
				/*+ "\n  * cont=" + this.content*/
				+ "\n  * pro=" + this.protocol
				+ "\n  * sig=" + Arrays.asList(this.attachementSignature);
	}
}


//public Envelope makeEnvellope(final MethodHandler mt) {
//if (mt.isAnnotationPresent(FipaACLEnvellope.class))
//	return new FipaACLEnvelope(mt
//			.getAnnotation(FipaACLEnvellope.class));
//else {
//	LoggerManager.writeWarning(this, FipaACLMessage.class
//			+ "IMPOSSIBLE : No fipa envellope " + "for this parser method : \n  ->"
//			+ mt);
//	return null;
//}
//// System.err.println("new message method added envellope(" +
//// "!!!!!!!!hashcode="+e.hashCode()+"!!!!!!!!):\n"+e);
//}