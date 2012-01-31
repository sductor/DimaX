package dima.basiccommunicationcomponents.DQLcontent;


import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import jtp.fol.Clause;
import jtp.fol.DefaultLiteral;
import jtp.fol.Symbol;
import jtp.fol.daml.DAMLParser;
import jtp.fol.parser.ClauseIterator;
import jtp.fol.parser.ParsingException;
import jtp.util.UnexpectedException;

import org.jdom.Attribute;
import org.jdom.Document;
import org.jdom.Element;
import org.jdom.Namespace;
import org.jdom.input.SAXBuilder;
import org.jdom.output.XMLOutputter;
import org.xml.sax.InputSource;

import com.hp.hpl.jena.daml.common.DAMLModelImpl;
import com.hp.hpl.mesa.rdf.jena.model.Model;
import com.hp.hpl.mesa.rdf.jena.model.RDFException;
import com.hp.hpl.mesa.rdf.jena.model.RDFNode;
import com.hp.hpl.mesa.rdf.jena.model.Resource;
import com.hp.hpl.mesa.rdf.jena.model.Statement;
import com.hp.hpl.mesa.rdf.jena.model.StmtIterator;


public class DQLTranslator
{

	protected Vector phnds;
	private String response;
	private Namespace soap;
	private final Namespace dql;
	private final Namespace var;

	public DQLTranslator(){

		this.phnds = new Vector();
		this.dql = Namespace.getNamespace(DQLRequest.dqlns);
		this.var = Namespace.getNamespace(DQLRequest.varns);
	}
	private String _getNamespaceFromResource(final String url)
	{
		final int pound = url.lastIndexOf('#');
		if(pound != -1)
			return url.substring(0, pound + 1);
		final int slash = url.lastIndexOf('/');
		if(slash != -1)
			return url.substring(0, slash + 1);
		return "";
	}
	private String _getTagFromResource(final String url)
	{
		final int pound = url.lastIndexOf('#');
		if(pound != -1)
			return url.substring(pound + 1);
		final int slash = url.lastIndexOf('/');
		if(slash != -1)
			return url.substring(slash + 1);
		return url;
	}

	private String _getTag(final Element el)
	{
		final Namespace nss = el.getNamespace();
		final String ns = nss != null ? nss.getURI() : "";
		final String tag = ns + el.getName();
		return tag;
	}

	public static String tokenToKIF(String ret)
	{
		if(ret.startsWith("var:"))
			ret = "?" + ret.substring(4);
		else if(ret.startsWith(DQLRequest.varns))
			ret = "?" + ret.substring(DQLRequest.varns.length());
		else {
			final int rpi = ret.lastIndexOf('#');
			ret = ret.substring(rpi+1);
		}
		return ret;
	}

	public static String _getTarget(final Element e)
	{
		String ret = e.getAttributeValue("resource",
				Namespace.getNamespace(DQLRequest.rdfns));
		if(ret == null) {
			final List c = e.getChildren();
			if(c != null && !c.isEmpty())
				ret = ((Element) c.get(0)).getTextTrim();
		}
		if(ret != null)
			ret = DQLTranslator.tokenToKIF(ret);
		return ret;
	}


	public String dqlToKIF(final Element query)
	{
		/* replace variables, translate to KIF */
		String kif = "";

		try {
			final Element rdf = (Element) query.getChildren().iterator().next();
			final XMLOutputter xo = new XMLOutputter();
			final String daml = xo.outputString(rdf);
			final StringReader sr = new StringReader(daml);
			final InputSource source = new InputSource();
			source.setCharacterStream(sr);
			final ClauseIterator i = this.parse(source);
			while(i.hasNext()) {
				final Clause c = i.next();
				final Iterator j = c.literals().iterator();
				while(j.hasNext()) {
					final jtp.fol.Literal lit = (jtp.fol.Literal) j.next();
					final Symbol s = lit.getRelation();
					final String pkg = s.getPackage();
					final String name = s.getName();
					kif = kif + "(" + DQLTranslator.tokenToKIF(pkg+name) + " ";
					final List args = lit.getArgs();
					final Iterator k = args.iterator();
					while(k.hasNext()) {
						final jtp.fol.Symbol trm = (jtp.fol.Symbol) k.next();
						final String tpkg = trm.getPackage();
						final String tname = trm.getName();
						kif = kif + DQLTranslator.tokenToKIF(tpkg+tname) + " ";
					}
					kif = kif + ")";
				}
			}
		}
		catch(final ParsingException e) {
			System.out.println("I caught an exception parsing your query!");
			return "";
		}

		/* catch(IOException e) {
	    System.out.println("I have no idea what just happened. XMLOutputter " +
		       "threw an IOException");
	}*/
		while(true) {
			final int i = kif.indexOf("(?");
			if(i == -1)
				break;
			kif = kif.substring(0, i) + "(holds " + kif.substring(i + 1);
		}


		return kif;
	}

	public void addVars(final Element e, final Vector v)
	{
		final Iterator it = e.getChildren().iterator();
		while(it.hasNext()) {
			final Element c = (Element) it.next();
			final Namespace ns = c.getNamespace();
			if(ns.getURI().equals(DQLRequest.varns))
				v.add(c.getName());
		}
	}


	public Element replaceVars(final Element e, final Hashtable vars)
	{
		Namespace ns = e.getNamespace();
		String name = e.getName();
		if(ns != null && ns.getURI().equals(DQLRequest.varns)) {
			final String val = (String) vars.get(name);
			if(val != null) {
				final String nns = this._getNamespaceFromResource(val);
				ns = nns != null && !nns.equals("")
						? Namespace.getNamespace(nns)
								: null;
						name = this._getTagFromResource(val);
			}
		}
		final Element n = new Element(name,ns);

		final Iterator ai = e.getAttributes().iterator();
		while(ai.hasNext()) {
			final Attribute a = (Attribute) ai.next();
			final Namespace ans = a.getNamespace();
			final String aname = a.getName();
			final String aval = a.getValue();
			String valns = this._getNamespaceFromResource(aval);
			String valname = this._getTagFromResource(aval);
			if(valns != null && valns.equals(DQLRequest.varns)) {
				final String nval = (String) vars.get(valname);
				if(nval != null) {
					valns = this._getNamespaceFromResource(nval);
					valname = this._getTagFromResource(nval);
				}
			}
			n.getAttributes().add(new Attribute(aname, valns + valname, ans));
		}

		final Iterator i = e.getChildren().iterator();
		while(i.hasNext()) {
			final Element c = (Element) i.next();
			n.getChildren().add(this.replaceVars(c, vars));
		}
		return n;
	}

	class DQLProcess
	{
		public String kburl;
		public Element qap;
		public Iterator ai;
		public DQLProcess(final String kb, final Element qa, final Iterator a)
		{
			this.kburl = kb;
			this.qap = qa;
			this.ai = a;
		}
	}



	public void addAnswersFromIterator(final Iterator ai,
			final int count,
			final Element qap)
					throws IOException
					{
		boolean needEmpty = true;
		for(int x = 0; ai.hasNext() && (count == -1 || x < count); ++x) {
			final Vector aset = (Vector) ai.next();
			needEmpty = false;
			this.response = this.response.concat("  <dql:answer>"+"\n");
			this.response = this.response.concat("    <dql:binding-set>"+"\n");

			final Iterator ansit = aset.iterator();
			final Hashtable vars = new Hashtable();
			while(ansit.hasNext()) {
				final Vector a = (Vector) ansit.next();
				this.response = this.response.concat("      <var:" + a.elementAt(0).toString() +
						" rdf:resource=\"" + a.elementAt(1).toString() + "\"/>"+"\n");
				vars.put(a.elementAt(0), a.elementAt(1));
			}
			this.response = this.response.concat("    </dql:binding-set>"+"\n");
			final Element qtop = (Element) qap.getChildren().iterator().next();
			final Element atop = this.replaceVars(qtop,vars);
			this.response = this.response.concat("    <dql:answerPatternInstance>"+"\n");
			final XMLOutputter xo = new XMLOutputter();
			final String ret = this.treat(xo.outputString(atop));
			this.response = this.response.concat(ret+"\n");
			this.response = this.response.concat("    </dql:answerPatternInstance>"+"\n");
			this.response = this.response.concat("  </dql:answer>"+"\n");
		}
		/* This indicates a successful query when no vars are used. */
		if(needEmpty) { // in case where there is no answer.
			this.response = this.response.concat("  <dql:answer>"+"\n");
			this.response = this.response.concat("    <dql:binding-set>"+"\n");
			this.response = this.response.concat("    </dql:binding-set>"+"\n");
			this.response = this.response.concat("  </dql:answer>"+"\n");
		}

					}
	/**
	 * this method eliminate the name space of rdf from the String api (answer pattern instance)
	 */
	public String treat(String api)
	{final StringBuffer ret = new StringBuffer();
	int y;

	for( y=0; y <api.indexOf("xmlns:rdf")-1; y++) // -1 to eliminate the space before xmlns:rdf
		ret.append(api.charAt(y));

	final StringBuffer et = new StringBuffer();
	for(int x=0;x<api.length();x++)
		et.append(api.charAt(x));


	api=et.toString();

	for(y = y+56;y<api.length();y++) // 56 is the number of caracter in the namespace of rdf
		ret.append(api.charAt(y));

	return ret.toString();
	}


	public void addContinuation(final Iterator ai, final int count, final Element qap, final String kburl)
	{
		this.response = this.response.concat("  <dql:continuation>"+"\n");
		if(ai.hasNext()) {
			if(count != -1) {
				final int ph = this.phnds.size();
				final DQLProcess dp = new DQLProcess(kburl, qap, ai);
				this.phnds.add(dp);
				String pound = "";
				if(!kburl.endsWith("#") || !kburl.endsWith("/"))
					pound = "#";
				this.response = this.response.concat("    <dql:processHandle>"+"\n"+
						ph + "</dql:processHandle>"+"\n");
			}
			else {
				/* currently this will never happen */
				this.response = this.response.concat("    <dql:termination-token>"+"\n");
				this.response = this.response.concat("      <dql:end/>"+"\n");
				this.response = this.response.concat("    </dql:termination-token>"+"\n");
			}
		}
		else {
			this.response = this.response.concat("    <dql:termination-token>"+"\n");
			this.response = this.response.concat("      <dql:none/>"+"\n");
			this.response = this.response.concat("    </dql:termination-token>"+"\n");
		}
		this.response = this.response.concat("  </dql:continuation>"+"\n");

	}

	public void addHeader()
	{

		this.response = this.response.concat("<dql:answerBundle xmlns:dql=\"" +
				DQLRequest.dqlns + "\""+"\n");
		this.response = this.response.concat("                  xmlns:var=\"" +
				DQLRequest.varns + "\""+"\n");
		this.response = this.response.concat("                  xmlns:rdf=\"" +
				DQLRequest.rdfns + "\">"+"\n");

	}
	public void addFooter()
	{
		this.response = this.response.concat("</dql:answerBundle>");

	}

	/**
	 * This method return the premise if a one exist, else return null.
	 * @param dqlreq that represent the Element "Query" in the DQL message
	 * @return premiseSet that is the String representation of the premise.
	 */
	public String getPremise(final Element dqlreq)
	{
		final Element premise = dqlreq.getChild("premise",this.dql);
		// try{
		if(premise != null) {
			final Element rdf = premise.getChild("RDF",
					Namespace.getNamespace(DQLRequest.rdfns));

			final XMLOutputter xo = new XMLOutputter();
			final String premiseSet = xo.outputString(rdf);
			return premiseSet;

		}
		/* }catch(IOException e) {
            System.out.println("I have no idea what just happened. XMLOutputter " +
                       "threw an IOException");
	}*/
		return null;
	}

	/**
	 * This method return the query.
	 * @param dqlreq that represent the Element "Query" in the DQL message
	 * @return the Element "queryPattern"
	 */
	public Element getQuery(final Element dqlreq)
	{

		return dqlreq.getChild("queryPattern",this.dql);
	}

	/**
	 * This method return a Vector of the variables that must be bind.
	 */
	public Vector getMustBind(final Element dqlreq)
	{
		final Vector musts = new Vector();
		final Element mustvars = dqlreq.getChild("mustBindVars", this.dql);
		if(mustvars != null) {
			this.addVars(mustvars, musts);
			return musts;
		}
		return null;

	}

	/**
	 * This method return a Vector of the variables that may be bind.
	 */
	public Vector getMayBind(final Element dqlreq)
	{
		final Vector mays = new Vector();
		final Element mayvars = dqlreq.getChild("mayBindVars", this.dql);
		if(mayvars != null) {
			this.addVars(mayvars, mays);
			return mays;
		}
		return null;
	}
	/**
	 *
	 */
	public Vector getAnswerPatternInstance(final String request)
	{
		try{

			try {
				final StringReader sr = new StringReader(request);

				final Document env= new SAXBuilder().build(sr);

				final Element dqlans = env.getRootElement();
				final List answers = dqlans.getChildren("answer",this.dql);
				if(!answers.isEmpty())
				{ final Vector ansPattInss = new Vector();
				for(int i=0;i<answers.size();i++)
				{
					final Element answer = (Element) answers.get(i);
					final Element ansPattIns = answer.getChild("answerPatternInstance",this.dql);
					ansPattInss.addElement(ansPattIns);
				}

				return ansPattInss;
				}
			}catch(final org.jdom.JDOMException e){ System.out.println(e.getMessage());}
		} catch (final IOException e) {
			System.out.println("erreur");
		}
		return null;
	}

	/**
	 *
	 */

	public Element parse(final String reqxml)
	{
		try{
			try {
				final StringReader sr = new StringReader(reqxml);
				final Document env = new SAXBuilder().build(sr);
				final Element dqlreq = env.getRootElement();
				//Element my = dqlreq.getChild("query",dql);
				return dqlreq;
			}catch(final org.jdom.JDOMException e){ System.out.println(e.getMessage());}
		} catch (final IOException e) {
			System.out.println("erreur");
		}
		return null;
	}

	/**
	 *
	 */

	public String kifToDQL(final Vector answers,final Element query, final int count, final String kburl)
	{
		this.response = new String();
		try{
			this.addHeader();
			final Iterator ai = answers.iterator();
			this.addAnswersFromIterator(ai, count,query);
			this.addContinuation(ai, count, query, kburl);
			this.addFooter();

			return this.response;
		}catch(final IOException e){}
		return null;
	}
	/**
	 *
	 */
	public ClauseIterator parse (final InputSource source) throws ParsingException
	{
		final Model model = new DAMLModelImpl();
		try {
			model.read(source.getCharacterStream(),
					source.getSystemId() == null ? "" : source.getSystemId());
			final StmtIterator stmts = model.listStatements();
			final int modelSize = (int)model.size();

			//addDefaultNamespaceMappings();

			return new ClauseIterator()
			{
				int i = 0;

				@Override
				public boolean hasNext()
				{
					try {
						return stmts.hasNext();
					}
					catch (final RDFException rdfxc) {
						throw new UnexpectedException(rdfxc);
					}
				}

				@Override
				public Clause next() throws ParsingException
				{
					try {
						final Statement st = stmts.next();

						final ArrayList al = new ArrayList (2);
						al.add(DQLTranslator.this.makeSymbol(st.getSubject()));
						al.add(DQLTranslator.this.makeConstant(st.getObject()));

						final DefaultLiteral dfl = new DefaultLiteral(DQLTranslator.this.makeSymbol(st.getPredicate()), al, true);
						if (Boolean.getBoolean("jtp.fol.daml.DAMLParser.printDebugTriples"))
							System.out.println("[" + ++this.i + " of " + modelSize + "] " + dfl);
						return dfl;
					}
					catch (final RDFException rdfxc) {
						throw new UnexpectedException(rdfxc);
					}
				}
			};
		}
		catch (final RDFException rdfxc)
		{
			throw new UnexpectedException(rdfxc);
		}
	}

	public Symbol makeSymbol(final Resource r)
	{
		final DAMLParser p = new DAMLParser();
		try {
			return Symbol.newSymbol(r.isAnon() ? p.getPrettyAnonId(r.getId()) : r.getLocalName(),r.getNameSpace());
		}
		catch (final RDFException rdfxc) {
			throw new UnexpectedException(rdfxc);
		}
	}



	public Object makeConstant(final RDFNode node)
	{
		if (node instanceof Resource) return this.makeSymbol((Resource)node);

		String label;
		label = node.toString();

		try
		{
			return Integer.valueOf(label.trim());
		}
		catch (final NumberFormatException nxc1) {}

		try
		{
			return Float.valueOf(label.trim());
		}
		catch (final NumberFormatException nxc2) {}

		return label;
	}

}


