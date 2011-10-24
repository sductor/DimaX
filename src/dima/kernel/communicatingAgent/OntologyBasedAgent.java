package dima.kernel.communicatingAgent;

import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import jtp.ReasoningException;
import jtp.ReasoningStep;
import jtp.ReasoningStepIterator;
import jtp.context.daml.DamlReasoningContext;
import jtp.fol.CNFSentence;
import jtp.fol.Clause;
import jtp.fol.Unifyable;
import jtp.rs.RSUtils;
import jtp.undo.Snapshot;
import jtp.undo.SnapshotUndoManager;
import jtp.util.UnexpectedException;

import org.jdom.Element;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.daml.DAMLClass;
import com.hp.hpl.jena.daml.DAMLDataInstance;
import com.hp.hpl.jena.daml.DAMLInstance;
import com.hp.hpl.jena.daml.DAMLModel;
import com.hp.hpl.jena.daml.DAMLProperty;
import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.mesa.rdf.jena.common.LiteralImpl;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.RDFNode;
import com.hp.hpl.mesa.rdf.jena.model.Statement;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;

import dima.basicagentcomponents.AgentIdentifier;
import dima.basiccommunicationcomponents.DQLcontent.DQLTranslator;
import dima.ontologies.basicKQMLMessages.KQML;
import dima.ontologies.basicKQMLMessages.KQMLAdvertise;
import dima.ontologies.basicKQMLMessages.KQMLAskAbout;
import dima.ontologies.basicKQMLMessages.KQMLBrokerAll;
import dima.ontologies.basicKQMLMessages.KQMLBrokerOne;
import dima.ontologies.basicKQMLMessages.KQMLDiscard;
import dima.ontologies.basicKQMLMessages.KQMLEos;
import dima.ontologies.basicKQMLMessages.KQMLForward;
import dima.ontologies.basicKQMLMessages.KQMLGenerator;
import dima.ontologies.basicKQMLMessages.KQMLNext;
import dima.ontologies.basicKQMLMessages.KQMLReady;
import dima.ontologies.basicKQMLMessages.KQMLRest;
import dima.ontologies.basicKQMLMessages.KQMLSorry;
import dima.ontologies.basicKQMLMessages.KQMLStandby;
import dima.ontologies.basicKQMLMessages.KQMLStreamAbout;
import dima.ontologies.basicKQMLMessages.KQMLStreamAll;
import dima.ontologies.basicKQMLMessages.KQMLSubscribe;
import dima.ontologies.basicKQMLMessages.KQMLTell;


/**
 * Titre :
 * Description :
 * Copyright :    Copyright (c) 2002
 * Soci�t� :
 * @author  : Othmane NADJEMI
 * @version 1.0
 */

public abstract class OntologyBasedAgent extends BasicCommunicatingAgent {

/**
	 *
	 */
	private static final long serialVersionUID = -7758169991515524731L;
protected String refKb = "file:/z:/oiled/ontologies/mixer_example";
protected static DAMLModel ontoModel;
/**
 * constructors
 */

  public OntologyBasedAgent() {
  super();
  }

/**
 * constructor of ontology based agents with "mixer_example" ontology
 * @param id Gdima.basicagentcomponents.AgentIdentifier
 */

  public OntologyBasedAgent(final AgentIdentifier id){
  super(id);
   try{
  final URL actualLocation = new URL(this.refKb);
  try{
   final InputSource source = new InputSource();
	try {
	  source.setCharacterStream(new InputStreamReader(actualLocation.openStream()));
	}
	catch (final IOException e) {
	  throw new UnexpectedException(e);
	}

       source.setSystemId(actualLocation.toString());

   ontoModel = new DAMLModelImpl();
   ontoModel.read(source.getCharacterStream(),
		     source.getSystemId() == null ? "" : source.getSystemId());
   }catch(final RDFException e) { System.out.println(e.toString());}
   }catch(final MalformedURLException e){}
  }



/**
 * contructeur of ontology based agents with an ontology kb
 * @param id Gdima.basicagentcomponents.AgentIdentifier
 * @param kb java.lang.String
 */

  public OntologyBasedAgent(final AgentIdentifier id, final String kb){

   super(id);
  this.refKb = kb;
 try{
  final URL actualLocation = new URL(this.refKb);
  try{
   final InputSource source = new InputSource();
	try {
	  source.setCharacterStream(new InputStreamReader(actualLocation.openStream()));
	}
	catch (final IOException e) {
	  throw new UnexpectedException(e);
	}

       source.setSystemId(actualLocation.toString());

   ontoModel = new DAMLModelImpl();
   ontoModel.read(source.getCharacterStream(),
		     source.getSystemId() == null ? "" : source.getSystemId());
   }catch(final RDFException e) { System.out.println(e.toString());}
   }catch(final MalformedURLException e){}

  }

/**
 * Methods that get all the instances, the classes or the properties of an ontology
 * for example GetAllInstances return an iterator about all the instance of the ontology, then we can browse
 * them DAMLInstance = Iterator.Next() by DAMLInstance e.g. if mixer is a DAMLInstance of material,
 * and them we can list all the statements of a DAMLInstance using a StatementIterator = DAMLInstance.listProperties()
 * @return java.util.Iterator
 */

  public Iterator getAllInstances(){
  return ontoModel.listDAMLInstances();
  }

  /**
   * @return java.util.Iterator
   */

  public Iterator getAllProperties(){
  return ontoModel.listDAMLProperties();
  }
  /**
   * @return java.util.Iterator
   */

  public Iterator getAllClasses(){
  return ontoModel.listDAMLClasses();
  }

  /**
   * In this method, we create a new instance "resource" of the upper class "upperClass" ,
   * when the instance of the upperClass is created the triple( resource of type upperClass) is add automatically
   * and then we add the property "predicat" with the value "value".
   * if the operation success (the instance has really this property) the method return true, else false.
   * In order to add more then one instance, use an for(;;) AddInstance
   *
   */

  public boolean addInstance(final String className, final String resource, final String predicat, final Object value) throws RDFException {

    final DAMLClass upper = (DAMLClass) ontoModel.getDAMLValue(className);

    // here we test if this class exist really in the ontology

    if(this.hasClass(upper))
    {
        final DAMLInstance instance = ontoModel.createDAMLInstance(upper,resource);
        final DAMLProperty property = (DAMLProperty) ontoModel.getDAMLValue(predicat);

       //here we verify if this property is one of this resource

            if(this.hasProperty(instance,property))
                {
                  instance.addProperty(property,value);
                  return true;
                }
     }
    return false;
  }

  /**
   * In this method, we suppose that an instance exist, and that we want to add a new property
   * to this instance with its value.
   * i.e. we suppose that the DAMLInstance exist.
   * if the operation success (the instance has really this property) the method return true, else false.
   */

  public boolean addPropertyToInstance(final String resource, final String predicat, final Object value) throws RDFException {

   final DAMLInstance instance = (DAMLInstance) ontoModel.getDAMLValue(resource);

   // here we verify if this resource exist really in the ontology
   if(this.hasInstance(instance))
   {
      final DAMLProperty property = (DAMLProperty) ontoModel.getDAMLValue(predicat);

      //here we verify if this property is one of this resource

        if(this.hasProperty(instance,property))
            {
              instance.addProperty(property,value);
              return true;
            }
    }
    return false;
  }

  /**
   * In this method, we change the value of the property predicat of the instance resource
   */

  public void changeProperty(final String resource, final String predicat, final Object newValue) throws RDFException {

    final DAMLInstance instance = (DAMLInstance) ontoModel.getDAMLValue(this.refKb+"#"+resource);
    final DAMLProperty property = (DAMLProperty) ontoModel.getDAMLValue(this.refKb+"#"+predicat);
    final RDFNode noeud = new LiteralImpl(newValue);
    instance.setPropertyValue(property, noeud);
  }

  /**
   * this method tests if an instance exist or not.
   */

  public boolean hasInstance(final String resource, final String predicat, final Object value) throws RDFException {

  StmtIterator stmt;
  Object o;
  Statement st;

  final DAMLInstance instance = (DAMLInstance) ontoModel.getDAMLValue(this.refKb+"#"+resource);
  final DAMLProperty property = (DAMLProperty) ontoModel.getDAMLValue(this.refKb+"#"+predicat);

  final Iterator it = ontoModel.listDAMLInstances();

    while(it.hasNext())
    {
      o = it.next();
        if(o instanceof DAMLDataInstance) // test if o is an instance of the instantiation of a DAML datatype
          continue;
        if(instance.equals(o))
         {
           stmt = ((DAMLInstance) o).listProperties(property);

                    while(stmt.hasNext())
                    {
                      st = stmt.next();
                      if(( (com.hp.hpl.mesa.rdf.jena.model.Literal) st.getObject()).equals(value))
                        return true;
                    }
         }
    }
    return false;
  }

  /**
   * here we test if a class exist really in the ontology
   */

  protected boolean hasClass(final DAMLClass upperClass) throws RDFException {
  DAMLClass  damlClass;

  final Iterator it =  this.getAllClasses();

    while(it.hasNext())
    {
      damlClass = (DAMLClass) it.next();
        if(damlClass.equals(upperClass))
            return true;
    }
    System.out.println("This class does not exist in this ontology");
    return false;

  }

  /**
   *  here we verify if this resource exist really in the ontology
   */

  protected boolean hasInstance(final DAMLInstance instance) throws RDFException {
  Iterator it;
  DAMLInstance inst;
  Object o;

  it = ontoModel.listDAMLInstances();
  while(it.hasNext())
  { o = it.next();
    if(o instanceof DAMLDataInstance)
      continue;
    inst = (DAMLInstance) o;
      if(inst.equals(instance))
        return true;
  }
  System.out.println("This resource does not exist in this ontology");
  return false;
  }

  /**
   * here we verify if this property is one of this resource
   *
   */

  protected boolean hasProperty(final DAMLInstance instance,final DAMLProperty property) throws RDFException {
    Iterator it;
    DAMLProperty prop;
    // we get the upper class of this instance

   final DAMLClass upperClass = (DAMLClass) instance.prop_type().getDAMLValue();

   // to verify if we can add this property is one of this instance

    it = upperClass.getDefinedProperties();
    while(it.hasNext())
    {
      prop = (DAMLProperty) it.next();
        if(prop.equals(property))
			return true;
    }
    System.out.println("This property does not exist in this class\n");
    return false;
  }

   /**
   * This method treat messages of type DQL or KIF
   */

   @Override
public void processNextMessage() {
      final KQML m = (KQML) this.getMessage();
      m.processKQML(this);
   }
   /**
    * This method submit a query in KIF to JTP reasoner
    *
    * @param queryKif , premise */

    public ReasoningStepIterator askReasoner(final String queryKif, final String premise)
    {

      final DamlReasoningContext drc = this.getDAMLReasoningContext(ontoModel);
      ReasoningStepIterator rsi = null;



      synchronized(drc)
      {
		final SnapshotUndoManager sum =
		    (SnapshotUndoManager) drc.getUndoManager();
		final Snapshot snap = sum.getSnapshot();

        try
        {
		    if(premise != null)
				drc.tellString(premise);

		     rsi = drc.ask(queryKif); // we ask the reasoner to reason about the query

	}catch(final Exception e)
                {
		    System.out.println("Caught exception working with query!"); // i think that here i return a sorry message
		}
            try {
		    sum.revertToSnapshot(snap);
		}
		catch(final Exception e) {
		    System.out.println("Caught exception reverting to snapshot");
		}

      }

      return rsi;
    }


    /**
     * This method get a vector of answers regarding to the parameters
     * @param mustBind "variables that the reasoner must bind", mayBind, count is an answerSizeBound "number of answers" to return
     */
    public Vector getAnswers(final ReasoningStepIterator rsi, final int count)
    {
	final Vector answers = new Vector();
	try{

            ReasoningStep rs = rsi.next();
              for (final int x = 0; (count == -1 || x < count) && rs != null;
			 rs = rsi.next())
		    {

                        final Map bindings = getTopLevelBindings(rs);  // here we have a Map of the answers "variables,values"

			final Vector aset = new Vector();

			final Iterator it = bindings.entrySet().iterator();

                        while(it.hasNext())
                        {
			    final Map.Entry ent = (Map.Entry)it.next();
			    final Object key = ent.getKey();
			    if(key == null)
					continue;

                            final String varname = key.toString().substring(1); //here we have the variable name
			    final Object ev = ent.getValue(); // here we have the value
                            if(ev == null)
								continue;
			    final String varval = this.unKIF(ev.toString());
                            final Vector ans =new Vector();
                            ans.add(0,varname);
                            ans.add(1,varval);
                            aset.add(ans);
			}


			answers.addElement(aset); // here we add the Vector aset to the vector answers.

                    }
	}catch(final ReasoningException e){}

      return answers;


    }

    public Vector getAnswers(final ReasoningStepIterator rsi, final Vector musts, final Vector mays, final int count)
    {
	            final Vector allBind = new Vector();
                    final Vector answers = new Vector();

	   try{
		    ReasoningStep rs = rsi.next();
                     final boolean needEmpty = rs != null;
		    for (final int x = 0; (count == -1 || x < count) && rs != null;
			 rs = rsi.next())
		    {

                        final Map bindings = getTopLevelBindings(rs);  // here we have a Map of the answers "variables,values"

                        if (allBind.contains(bindings) || bindings.isEmpty())
							continue;
			Vector aset = new Vector();

			final Iterator it = bindings.entrySet().iterator();

                        while(it.hasNext())
                        {
			    final Map.Entry ent = (Map.Entry)it.next();
			    final Object key = ent.getKey();
			    if(key == null)
					continue;

                            final String varname = key.toString().substring(1); //here we have the variable name
			    final Object ev = ent.getValue(); // here we have the value
			    if(ev == null)
					continue;
			    final String varval = this.unKIF(ev.toString());
                            if(varval.startsWith("Anon_") ||
			       varval.startsWith("jtp.frame.") ||
			       !musts.contains(varname) &&
				!mays.contains(varname))
								continue;
                            final Vector ans =new Vector();
                            ans.add(0,varname);
                            ans.add(1,varval);
                            aset.add(ans);
			}

                        /* Make sure we got bindings for all the musts */
			final Iterator i = musts.iterator();
			while(i.hasNext()) {
			    final String vn = (String) i.next();
			    final Iterator ai = aset.iterator();
			    boolean hadIt = false;
			    while(!hadIt && ai.hasNext()) {
				final Vector a = (Vector) ai.next();
				if(a.elementAt(0).toString().equals(vn))
					hadIt = true;
			    }
			    if(!hadIt) {
				aset = new Vector();
				break;
			    }
			}
                        allBind.add(bindings);

			if(aset.isEmpty())
				continue;
			answers.addElement(aset); // here we add the Vector aset to the vector answers.

                    }
		   } catch(final ReasoningException e){}

      return answers;

    }
   /**
    * This method translate the answer of the reasoner wich has the form :
    * |uri#|::|value|
    * to a DAML format : uri#value
    *
    */

     public String unKIF(final String kif)
    {
	final StringBuffer ret = new StringBuffer();
	for(int x = 0; x < kif.length(); ++x) {
	    final char c = kif.charAt(x);
	    if(c == '|') {
		if(kif.substring(x).startsWith("|::|"))
			x += 3;
	    }
	    else if(c == ':' && kif.charAt(x+1) == ':')
			++x;
		else
			ret.append(c);
	}
	return ret.toString();
    }

   /**
    * The method processAskOne Performative "ask-one"
    * @param m Gdima.basicKQMLMessages.KQML
    */

  public void processAskOne(final KQML m)
  {
    final String request = (String) m.getContent();
    System.out.println("Ask-One:"+"\n"+
                             "Sender: "+m.getSender()+"\n"+
                             "Receiver: "+m.getReceiver()+"\n"+
                             "Content: "+request+"\n"+
                             "Language: "+m.getLanguage()+"\n"+
                             "Ontology: "+m.getOntology()+"\n"+
                             "Reply-with: "+m.getReplyWith()+"\n");
      String response = null;
      Vector answers = null;
      if(m.getLanguage().equals(new String("DQL")))
      {
           final DQLTranslator p = new DQLTranslator();
          final Element elt = p.parse(request);
          final Element query = p.getQuery(elt);
          final String premise = p.getPremise(elt);
          final Vector mustBind = p.getMustBind(elt);
          final Vector mayBind = p.getMayBind(elt);
          final String queryKIF = p.dqlToKIF(query);
          System.out.println(queryKIF);
          final ReasoningStepIterator rsi = this.askReasoner(queryKIF,premise);
	  answers = this.getAnswers(rsi,mustBind,mayBind,1);
          response = p.kifToDQL(answers,query,1,this.refKb);
          }
       else
          if(m.getLanguage().equals(new String("KIF")))
          {
            final String queryKif = request;
            final ReasoningStepIterator rsi =  this.askReasoner(queryKif,null);
	    answers =this.getAnswers(rsi,1);
            response = this.answerToKif(answers,queryKif);

          }

          if(response.equals(null))
          {
            final KQMLSorry mess = new KQMLSorry(this.getId().toString(),m.getSender().toString(),request,m.getReplyWith(),null);
            mess.setLanguage(m.getLanguage());
            this.sendMessage(m.getSender(),mess);
          }
          else
          {
            final KQMLTell mess = new KQMLTell(this.getId().toString(),response,m.getLanguage(),null);
            mess.setSender(this.getId());
            mess.setInReplyTo(m.getReplyWith());
            mess.setOntology(m.getOntology());
            this.sendMessage(m.getSender(),mess);
          }

  }
  /**
    * The method processAskAll Performative "ask-all"
    * @param m Gdima.basicKQMLMessages.KQML
    */

  public void processAskAll(final KQML m)
  {final String request = (String) m.getContent();
   System.out.println("Ask-All:"+"\n"+
                         "Sender: "+m.getSender()+"\n"+
                         "Receiver: "+m.getReceiver()+"\n"+
                         "Content: "+request+"\n"+
                         "Language: "+m.getLanguage()+"\n"+
                         "Ontology: "+m.getOntology()+"\n"+
                             "Reply-with: "+m.getReplyWith()+"\n");
      String response = null;
      Vector answers = null;
      if(m.getLanguage().equals(new String("DQL")))
      {
          final DQLTranslator p = new DQLTranslator();
          final Element elt = p.parse(request);
          final String premise = p.getPremise(elt);
          final Element query = p.getQuery(elt);
          final Vector mustBind = p.getMustBind(elt);
          final Vector mayBind = p.getMayBind(elt);
          final String queryKif = p.dqlToKIF(query);
          final ReasoningStepIterator rsi = this.askReasoner(queryKif,premise);
	  answers = this.getAnswers(rsi,mustBind,mayBind,-1);
          response = p.kifToDQL(answers,query,-1,this.refKb);
          }
       else
          if(m.getLanguage().equals(new String("KIF")))
          {
            final String queryKif = request;
          final ReasoningStepIterator rsi = this.askReasoner(queryKif,null);
	  answers = this.getAnswers(rsi,-1);
          response = this.answerToKif(answers,queryKif);
          }

          if(response.equals(null))
          {
            final KQMLSorry mess = new KQMLSorry(this.getId().toString(),m.getSender().toString(),request,m.getReplyWith(),null);
            mess.setOntology(m.getOntology());
            this.sendMessage(m.getSender(),mess);
          }
          else
          {
            final KQMLTell mess = new KQMLTell(this.getId().toString(),response,m.getLanguage(),null);
            mess.setSender(this.getId());
            mess.setInReplyTo(m.getReplyWith());
            mess.setOntology(m.getOntology());
            this.sendMessage(m.getSender(),mess);
          }


  }
  /**
   *
   */

  public void processAskIf(final KQML m)
  {

  }
  /**
   *
   */

  public void processAskAbout(final KQMLAskAbout m)
  {

  }
  /**
   *
   */

  public void processAdvertise(final KQMLAdvertise m)
  {

  }

  /**
   * Performative "tell"
   */

  public void processTell(final KQML m)
  {final String request = (String) m.getContent();
     System.out.println("Tell:"+"\n"+
                             "Sender: "+m.getSender()+"\n"+
                             "Receiver: "+m.getReceiver()+"\n"+
                             "Content: "+request+"\n"+
                             "Language: "+m.getLanguage()+"\n"+
                             "Ontology: "+m.getOntology()+"\n"+
                             "In-reply-to: "+m.getInReplyTo()+"\n");
  if(m.getLanguage().equals(new String("DQL")))
   {  final DQLTranslator p = new DQLTranslator();
      final Vector answers =  p.getAnswerPatternInstance(request);
     for(int i=0; i<answers.size();i++)
      {final Element answer = (Element) answers.elementAt(i);
      final String kifContent = p.dqlToKIF(answer);
      final DamlReasoningContext drc = this.getDAMLReasoningContext(ontoModel);
      try{
      drc.tellKifString(kifContent);
      }catch(final jtp.ReasoningException e){System.out.println(e.getMessage());}
      }
   }
  else
  if(m.getLanguage().equals(new String("KIF")))
   {  final DamlReasoningContext drc = this.getDAMLReasoningContext(ontoModel);
      try{
      drc.tellKifString(request);
      }catch(final jtp.ReasoningException e){System.out.println(e.getMessage());}
   }

  }

  public void processUntell(final KQML m)
  {
    System.out.println("Untell");
  }
  /**
   *
   */

  public void processBrokerAll(final KQMLBrokerAll m)
  {

  }
  /**
   *
   */

  public void processBrokerOne(final KQMLBrokerOne m)
  {

  }
  /**
   *
   */

  public void processDiscard(final KQMLDiscard m)
  {

  }
  /**
   *
   */

  public void processEos(final KQMLEos m)
  {

  }
  /**
   *
   */

  public void processForward(final KQMLForward m)
  {

  }
  /**
   *
   */

  public void processGenerator(final KQMLGenerator m)
  {

  }
  /**
   *
   */

  public void processNext(final KQMLNext m)
  {

  }
  /**
   *
   */

  public void processReady(final KQMLReady m)
  {

  }
  /**
   *
   */

  public void processRest(final KQMLRest m)
  {

  }
  /**
   *
   */

  public void processSorry(final KQMLSorry m)
  {

  }
  /**
   *
   */

  public void processStandby(final KQMLStandby m)
  {

  }
  /**
   *
   */

  public void processStreamAbout(final KQMLStreamAbout m)
  {

  }

  public void processStreamAll(final KQMLStreamAll m)
  {

  }
  /**
   *
   */

  public void processSubscribe(final KQMLSubscribe m)
  {

  }
  /**
   * Method unabreviate, that replace urn:tkb#thing by refKb#thing
   */
  public String unabreviate(final String thing)
  {  StringBuffer ret;
    if(thing.startsWith("urn:tkb#"))
    { ret= new StringBuffer();
      ret.append(this.refKb+"#");
        for(int x=8; x<thing.length();x++)
			ret.append(thing.charAt(x));
      return ret.toString();
    }
    return thing;
  }
  /**
   * This method load a KB from a DAMLModel.
   * @param model a DAMLModel representation of the ontology
   * @return DamlReasoningContext
   */
  public DamlReasoningContext getDAMLReasoningContext(final DAMLModel model)
  {
    final DamlReasoningContext drc = new DamlReasoningContext();
    try
    {
      drc.setUp();
      drc.loadKB(model);
      }catch(final ReasoningException e){}catch(final Exception e){}

      return drc;
  }

@Override
public abstract void step();

/**
 *  These are copied from DamlQueryAnwerer because for some reason
 *  they're protected.
 *  We use them to have a Map that contains Keys (variables to bind)
 *  and Values (values of these variables
 */
    protected static Map getTopLevelBindings (final ReasoningStep rs)
    {
	final Map bindings = getBindings(rs);
	final Map retBindings = new HashMap(bindings.size());
	final Set topLevelVars = getQueryVariables(rs);

	for (final Iterator it = topLevelVars.iterator(); it.hasNext(); ) {
	    final Object v = it.next();
	    retBindings.put(v, bindings.get(v));
	}
	return retBindings;
    }

    protected static Map getBindings(ReasoningStep rs)
    {
	if (rs.getGoal() instanceof String)
	    rs = (ReasoningStep)rs.getSubProofs().get(0);
	return RSUtils.getRecursiveBindings(rs, null);
    }

    protected static Set getQueryVariables(final ReasoningStep rs)
    {
	final Object goal = rs.getGoal();
	if (goal instanceof CNFSentence)
	    return getQueryVariables((CNFSentence)goal);
	else {
	    final ReasoningStep subRS = (ReasoningStep)rs.getSubProofs().get(0);
	    return getQueryVariables((CNFSentence)subRS.getGoal());
	}
    }

    static Set getQueryVariables(final CNFSentence sent)
    {
	final Set vars = new HashSet(10);
	for (final Iterator i = sent.clauses().iterator(); i.hasNext(); ) {
	    final Clause cl = (Clause)i.next();
	    for (final Iterator j = cl.literals().iterator(); j.hasNext(); ) {
		final jtp.fol.Literal lit = (jtp.fol.Literal)j.next();
		if (lit.getArgs() instanceof Unifyable)
		    vars.addAll(((Unifyable)lit.getArgs()).getVariables(null));
	    }
	}
	return vars;
    }

    /* End copy and paste */

  public String answerToKif(final Vector answers, final String request)
      {
        final String response = new String();
        final Iterator ai = answers.iterator();
        final StringBuffer buff = new StringBuffer();
        while(ai.hasNext()) {
	    final Vector aset = (Vector) ai.next();
            final Iterator ansit = aset.iterator();
	    final Hashtable vars = new Hashtable();
            int x = 0;
               while(x< request.length())
               {
                 if(request.charAt(x)== '?')
                 break;
                 x++;
                }
              for(int y=0;y<x;y++)
                buff.append(request.charAt(y));
	    while(ansit.hasNext()) {
		final Vector a = (Vector) ansit.next();
                buff.append(a.elementAt(1));

                while(x<request.length())
                {  if(request.charAt(x)==' '||request.charAt(x)==')')
                    break;
                x++;
                }
                for(;x<request.length();x++)
                  buff.append(request.charAt(x));

	   }
       	}
        return buff.toString();

      }

}
