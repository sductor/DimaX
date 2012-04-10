package dima.introspectionbasedagents.ontologies.FIPAACLOntologie;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.Message;
import dima.introspectionbasedagents.ontologies.Envelope;
import dima.introspectionbasedagents.ontologies.MessageInEnvelope;
import dima.introspectionbasedagents.ontologies.MessageWithProtocol;
import dima.introspectionbasedagents.ontologies.Protocol;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.introspectionbasedagents.shells.MethodHandler;
import dima.kernel.communicatingAgent.BasicCommunicatingAgent;

//message type order :content java

public class FipaACLMessage extends Message implements MessageInEnvelope, MessageWithProtocol {

	private static final long serialVersionUID = 8615779082572568991L;

	public static class NoneProtocol extends Protocol {
		private static final long serialVersionUID = -4809105758307131733L;
		//		public NoneProtocol() {	super(null);}
		public static final String None = "None";
	}

	// Performatif
	private Performative performative;
	// Content
	private String content = NoneProtocol.None;
	// le langage utilisé dans le contenu du message
	private Language language = Language.Any;
	// le protocole utilisé,
	protected Class<? extends Protocol> protocol = NoneProtocol.class;
	// l'ontologie auquel le message se rattache
	private Ontology ontology = Ontology.Any;
	// la référence d'un message antérieur auquel le message actuel se rattache
	private FipaACLMessage inreplyto = null;
	// ou la référence d'un message ultérieur attendu en retour
	private List<FipaACLMessage> replywith = null;
	// reference to the agent that initated the conversation
	private AgentIdentifier replyto = null;
	// Expiration time of the communication
	private Date replyBy;
	// Piece jointe
	//private Object[] attachement = {};
	private Class<?>[] attachementSignature = {};

	private final Date creationTime;
	private String additionalInformation = "";
	private Exception attachedException = null;

	//
	// Constructor
	//

	public FipaACLMessage(final Performative performative,
			final Class<? extends Protocol> p) {
		super();
		this.creationTime = new Date();
		this.setType("fipa-acl");
		this.performative = performative;
		this.content = this.content.toString();
		this.protocol = p;
	}

	public FipaACLMessage(final Performative performative,
			final String content, final Class<? extends Protocol> p) {
		super();
		this.creationTime = new Date();
		this.setType("fipa-acl");
		this.performative = performative;
		this.content = content.toString();
		this.protocol = p;
	}

	//
	// Accessor
	//

	public Performative getPerformative() {
		return this.performative;
	}

	@Override
	public String getContent() {
		return this.content;
	}

	public Language getLanguage() {
		return this.language;
	}

	@Override
	public Class<? extends Protocol> getProtocol() {
		return this.protocol;
	}

	public Ontology getOntology() {
		return this.ontology;
	}

	public FipaACLMessage getInreplyto() {
		return this.inreplyto;
	}

	public List<FipaACLMessage> getReplywith() {
		return this.replywith;
	}

	public void setReplyto(final AgentIdentifier replyto) {
		this.replyto = replyto;
	}

	public AgentIdentifier getReplyTo() {
		if (this.replyto==null){
			if (this.getSender()==null){
				LogService.write("aaaaaargh");
				return null;
			} else {
				return this.getSender();
			}
		} else {
			return this.replyto;
		}
	}

	public String getConversationId() {
		return this.performative + "#" + this.content + "#"
				+ this.attachementSignature;
	}

	public Class<?>[] getAttachementSignature() {
		return this.attachementSignature;
	}

	public Object[] getAttachement() {
		return this.getArgs();
	}

	/**
	 * @return the validity time of the communication
	 */
	protected Date getReplyBy() {
		return this.replyBy;
	}

	/**
	 * @return the additionalInformation
	 */
	protected String getAdditionalInformation() {
		return this.additionalInformation;
	}

	/**
	 * @param additionalInformation
	 *            the additionalInformation to set
	 */
	public void setAdditionalInformation(final String additionalInformation) {
		this.additionalInformation = additionalInformation;
	}

	/**
	 * @return the attachedException
	 */
	protected Exception getAttachedException() {
		return this.attachedException;
	}

	/**
	 * @param attachedException
	 *            the attachedException to set
	 */
	public void setAttachedException(final Exception attachedException) {
		this.attachedException = attachedException;
	}

	/**
	 * @return the creationTime
	 */
	protected Date getCreationTime() {
		return this.creationTime;
	}

	public void setPerformative(final Performative performative) {
		this.performative = performative;
	}

	public void setContent(final String content) {
		this.content = content;
	}

	public void setLanguage(final Language language) {
		this.language = language;
	}

	public void setProtocol(final Class<? extends Protocol> protocol) {
		this.protocol = protocol;
	}

	public void setOntology(final Ontology ontology) {
		this.ontology = ontology;
	}

	public void setInreplyto(final FipaACLMessage inreplyto) {
		this.inreplyto = inreplyto;
	}

	public void setReplywith(final List<FipaACLMessage> replywith) {
		this.replywith = replywith;
	}

	/**
	 * @param the
	 *            validity time of the communication
	 */
	protected void setReplyBy(final Date replyBy) {
		this.replyBy = replyBy;
	}

	//
	// Methods
	//

	public String description() {
		return "\n *Performative:" + this.performative + "\n *Protocol:"
				+ this.protocol.getSimpleName() + "\n *signature:"
				+ (this.attachementSignature==null?"":Arrays.asList(this.attachementSignature))
				//		+ Arrays.asList(this.getArgs())==null?
				//				"":("\n *attachement:"+(Arrays.asList(this.getArgs()).toString()))
				;
	}

	@Override
	public Envelope getMyEnvelope() {
		return new FipaACLEnvelopeClass(this);
	}

	@Override
	public Object process(final BasicCommunicatingAgent a) {
		// Do Nothing
		return null;
	}

	
//	public FipaACLMessage clone(){
//		FipaACLMessage m = new FipaACLMessage(this.performative, this.content, this.protocol);
//		m.
//	}
	
	
	
	//////////////////////////////////////////////://Bug apres cleanup d'éclipse////// : remettre methodhandler et faire l'import...
	public void setAttachement(final Object[] attachement,
			final Class<?>[] attachementSignature) {
		if (MethodHandler.checkSignature(attachementSignature, attachement)) {
			this.setArgs(attachement);
			this.attachementSignature = attachementSignature;
		} else {
			LogService.writeException(this, "Unappropriate attachement");
		}
	}
	public void setAttachement(final Object[] attachement) {
		final Class<?>[] attachementSignature = MethodHandler.getSignature(attachement);
		this.setArgs(attachement);
		this.attachementSignature = attachementSignature;
	}
	public void setAttachement(final Object attachement) {
		this.setAttachement(new Object[]{attachement});
	}
	//
	// Primitive
	//

	/**
	 * Returns a String that represents the value of this object.
	 *
	 * @return a string representation of the receiver
	 */
	@Override
	public String toString() {
		// Insert code to print the receiver here.
		// This implementation forwards the message to super. You may replace or
		// supplement this.
		// return super.toString();
		return "MESSAGE of "+ this.getClass()
				+ " ("
				+ this.hashCode()
				+ ")\n"
				+ "from    : "
				+ this.getSender()
				+ "\n"
				+ "to      : "
				+ this.getReceiver()
				+ "\n" + "type    : " + this.getType() + "\n"
				+ "content : " + this.getContent()  + "\n"
				+"args?   : "
				+ (this.getArgs() != null ? this.getArgs().length != 0 : false)
				+ "\n" + "details : " + this.description();
	}
}

// System.out.println(attachementSignature[cpt]+" "+attachementSignature[cpt].getClass());
// System.out.println(attachement[cpt]+" "+attachement[cpt].getClass());
// System.out.println(attachementSignature[cpt].equals(attachement[cpt].getClass()));
