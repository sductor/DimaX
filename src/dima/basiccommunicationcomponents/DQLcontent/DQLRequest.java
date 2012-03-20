package dima.basiccommunicationcomponents.DQLcontent;


import java.net.MalformedURLException;
import java.net.URL;
import java.util.Iterator;
import java.util.Vector;


public class DQLRequest
{
	String premise, query;
	Vector mustbind, maybind, answerkbs;
	int maxAns;
	public static String dqlns = "file:/dql-syntax#";
	public static String varns = "file:/dql-variables#";
	public static String rdfns = "http://www.w3.org/1999/02/22-rdf-syntax-ns#";
	public static String rdfsns = "http://www.w3.org/2000/01/rdf-schema#";
	public static String damlns = "http://www.daml.org/2001/03/daml+oil#";

	public static final int DQL_MUSTBIND = 1;
	public static final int DQL_MAYBIND = 2;
	public static final int DQL_DONTBIND = 3;

	public static final int DQL_NONE = 4;
	public static final int DQL_END = 5;
	public static final int DQL_CONTINUATION = 6;



	public static String abbreviate(final String res, final String pfx, final String url)
	{
		return res.startsWith(url)
				? pfx + ":" + res.substring(url.length())
						: res;
	}
	public static String abbreviate(final String res)
	{
		String ret = DQLRequest.abbreviate(res, "dql", DQLRequest.dqlns);
		ret = DQLRequest.abbreviate(ret, "var", DQLRequest.varns);
		ret = DQLRequest.abbreviate(ret, "rdf", DQLRequest.rdfns);
		ret = DQLRequest.abbreviate(ret, "rdfs", DQLRequest.rdfsns);
		return DQLRequest.abbreviate(ret, "daml", DQLRequest.damlns);
	}
	public static String abbreviate(final String res, final String kburl)
	{
		final String ret = DQLRequest.abbreviate(res);
		return kburl.equals("") ? ret : DQLRequest.abbreviate(ret, "tkb", kburl);
	}

	public static String unabbreviate(final String res, final String pfx, String url)
	{
		final String npfx = pfx + ":";
		final String filler = "";
		if(!url.endsWith("/") && !url.endsWith("#")) {
			url = url + "#";
		}
		return res.startsWith(npfx)
				? url + res.substring(npfx.length())
						: res;
	}
	public static String unabbreviate(final String res)
	{
		String ret = DQLRequest.unabbreviate(res, "dql", DQLRequest.dqlns);
		ret = DQLRequest.unabbreviate(ret, "var", DQLRequest.varns);
		ret = DQLRequest.unabbreviate(ret, "rdf", DQLRequest.rdfns);
		ret = DQLRequest.unabbreviate(ret, "rdfs", DQLRequest.rdfsns);
		return DQLRequest.unabbreviate(ret, "daml", DQLRequest.damlns);
	}
	public static String unabbreviate(final String res, final String kburl)
	{
		final String ret = DQLRequest.unabbreviate(res);
		return kburl.equals("") ? ret : DQLRequest.unabbreviate(ret, "tkb", kburl);
	}

	public static String getResourceNamespace(final String url)
	{
		final int pound = url.lastIndexOf('#');
		if(pound != -1) {
			return url.substring(0, pound + 1);
		}
		final int slash = url.lastIndexOf('/');
		if(slash != -1) {
			return url.substring(0, slash + 1);
		}
		return "";
	}

	public static String getResourceTag(final String url)
	{
		final int pound = url.lastIndexOf('#');
		if(pound != -1) {
			return url.substring(pound + 1);
		}
		final int slash = url.lastIndexOf('/');
		if(slash != -1) {
			return url.substring(slash + 1);
		}
		return url;
	}

	public DQLRequest()
	{
		this.premise = "";
		this.query = "";
		this.mustbind = new Vector();
		this.maybind = new Vector();
		this.answerkbs = new Vector();
		this.maxAns = -1;

	}

	public void addPremiseTriple(String predicate,
			String source,
			String object)
	{
		final String kburl=new String("");

		predicate= this.addNs(predicate);
		predicate = DQLRequest.abbreviate(predicate, kburl);
		final String predns = DQLRequest.getResourceNamespace(predicate);
		String predtag = predicate;
		if(!predns.equals("")) {
			predtag = "n0:" + DQLRequest.getResourceTag(predicate);
			predicate = predtag + " xmlns:n0=\"" + predns + "\"";
		}
		source = this.addNs(source);
		source = DQLRequest.unabbreviate(source, kburl);
		object = this.addNs(object);
		object = DQLRequest.unabbreviate(object, kburl);
		this.premise = this.premise + "<rdf:Description rdf:about=\"" + source + "\">\n";
		this.premise = this.premise + "<" + predicate;
		if(object.startsWith("urn:") || object.startsWith("http://") || object.startsWith("file:/")) {
			this.premise = this.premise + " rdf:resource=\"" + object + "\"/>\n";
		} else {
			this.premise = this.premise + ">" + object + "</" + predtag + ">\n";
		}
		this.premise = this.premise + "</rdf:Description>\n";
	}




	public void addQueryTriple(String predicate,
			String source,
			String object)
	{
		String kburl =new String("");
		predicate = this.addNs(predicate);
		if(kburl.startsWith(DQLRequest.varns)) {
			kburl = "urn:tkb#";
		}
		predicate = DQLRequest.abbreviate(predicate, kburl);
		final String predns = DQLRequest.getResourceNamespace(predicate);
		String predtag = predicate;
		if(!predns.equals("")) {
			predtag = "n0:" + DQLRequest.getResourceTag(predicate);
			predicate = predtag + " xmlns:n0=\"" + predns + "\"";
		}
		source = this.addNs(source);
		source = DQLRequest.unabbreviate(source, kburl);
		object = this.addNs(object);
		object = DQLRequest.unabbreviate(object, kburl);
		this.query = this.query + "<rdf:Description rdf:about=\"" + source + "\">\n";
		this.query = this.query + "<" + predicate;
		if(object.startsWith("urn:") || object.startsWith("http://")|| object.startsWith("file:/")) {
			this.query = this.query + " rdf:resource=\"" + object + "\"/>\n";
		} else {
			this.query = this.query + ">" + object + "</" + predtag + ">\n";
		}
		this.query = this.query + "</rdf:Description>\n";
	}



	public void setPremiseDAML(final String daml)
	{
		this.premise = daml.trim();
	}

	public void setQueryDAML(final String daml)
	{
		this.query = daml.trim();
	}

	/* Set variable to must bind, may bind, or don't bind */
	public void setVariableState(final String varname, final int state)
	{
		switch(state) {
		case DQLRequest.DQL_MUSTBIND:
			for(final Iterator i = this.mustbind.iterator(); i.hasNext(); ) {
				if(((String) i.next()).equals(varname)) {
					return;
				}
			}
			this.mustbind.add(varname);
			break;
		case DQLRequest.DQL_MAYBIND:
			for(final Iterator i = this.maybind.iterator(); i.hasNext(); ) {
				if(((String) i.next()).equals(varname)) {
					return;
				}
			}
			this.maybind.add(varname);
			break;
		}
	}

	public void setMaxAnswers(final int ma)
	{
		this.maxAns = ma;
	}

	public void addAnswerKB(String kburl) throws MalformedURLException
	{
		if(kburl.startsWith(DQLRequest.varns)) {
			this.answerkbs.add(kburl);
		} else {
			if(kburl.endsWith("#")) {
				kburl = kburl.substring(0, kburl.length() - 1);
			}
			this.answerkbs.add(new URL(kburl));
		}
	}

	public void addAnswerKB(final URL kburl)
	{
		this.answerkbs.add(kburl);
	}
	public void addAnswerKB_rdf(final String rdf)
	{
		this.answerkbs.add(rdf.trim());
	}

	public String addNs(final String str)
	{
		final int pound = str.lastIndexOf("#");
		if(pound ==-1)
		{
			final StringBuffer ret = new StringBuffer();
			ret.append("urn:tkb#");
			for(int x=0; x < str.length(); x++) {
				ret.append(str.charAt(x));
			}

			return ret.toString();
		}
		return str;
	}

	public String toXML()
	{
		String ret =
				"<dql:query xmlns:dql=\"" + DQLRequest.dqlns + "\"\n" +
						"           xmlns:var=\"" + DQLRequest.varns + "\"\n"+
						"           xmlns:rdf=\"" + DQLRequest.rdfns + "\">\n";

		if(this.answerkbs.size() == 1 &&
				this.answerkbs.elementAt(0) instanceof String &&
				!((String) this.answerkbs.elementAt(0)).startsWith(DQLRequest.varns))
		{
			String kburl = (String) this.answerkbs.elementAt(0);
			if(!kburl.endsWith("#")) {
				kburl = kburl + "#";
			}
			ret = ret + "           xmlns:tkb=\"" + kburl + "\"\n";
		}
		if(this.premise != null && !this.premise.equals("")) {
			if(!this.premise.startsWith("<rdf:RDF")) {
				this.premise = "<rdf:RDF>" + this.premise + "</rdf:RDF>";
			}
			ret = ret + "<dql:premise>\n" + this.premise + "\n" + "</dql:premise>\n";
		}
		if(this.query != null && !this.query.equals("")) {
			if(!this.query.startsWith("<rdf:RDF")) {
				this.query = "<rdf:RDF>" + this.query + "</rdf:RDF>";
			}
			ret = ret + "<dql:queryPattern>\n" + this.query + "\n" +
					"</dql:queryPattern>\n";
		}

		if(this.mustbind.size() > 0) {
			ret = ret + "<dql:mustBindVars>\n";
			final Iterator i = this.mustbind.iterator();
			while(i.hasNext()) {
				String s = (String) i.next();
				if(s.startsWith(DQLRequest.varns)) {
					s = s.substring(DQLRequest.varns.length());
				}
				ret = ret + "<var:" + s + "/>";
			}
			ret = ret + "</dql:mustBindVars>\n";
		}
		if(this.maybind.size() > 0) {
			ret = ret + "<dql:mayBindVars>\n";
			final Iterator i = this.maybind.iterator();
			while(i.hasNext()) {
				String s = (String) i.next();
				if(s.startsWith(DQLRequest.varns)) {
					s = s.substring(DQLRequest.varns.length());
				}
				ret = ret + "<var:" + s + "/>";
			}
			ret = ret + "</dql:mayBindVars>\n";
		}
		if(this.maxAns != -1) {
			ret = ret + "<dql:answerSizeBound>" + this.maxAns +
					"</dql:answerSizeBound>";
		}

		final Iterator i = this.answerkbs.iterator();
		if(i.hasNext())
		{
			ret = ret + "<dql:answerKBPattern>\n";
			while(i.hasNext()) {
				final Object o = i.next();
				if(o instanceof String) {
					String s = (String) o;
					if(s.startsWith(DQLRequest.varns)) {
						ret = ret + "<var:" +
								s.substring(DQLRequest.varns.length()) + "/>";
					} else {
						if(!s.startsWith("<rdf:RDF>")) {
							s = "<rdf:RDF>" + s + "</rdf:RDF>";
						}
						ret = ret + s;
					}
				}
				else if(o instanceof URL) {
					ret = ret + "<dql:kbRef rdf:resource=\"" +
							((URL) o).toString() + "\"/>";
				}
			}
			ret = ret +"</dql:answerKBPattern>\n";
		}
		ret = ret+"</dql:query>";
		return ret;
	}
}
