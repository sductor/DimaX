package dima.ontologies.basicKQMLMessages;
import dima.kernel.communicatingAgent.OntologyBasedAgent;
/**
 * Titre :
 * Description :
 * Copyright :     Copyright (c) 2001
 * Soci�t� :       LIP6
 * @author         Samir TOUAF
 * @version        1.0
 */

public class KQMLForward extends KQML {

	/**
	 *
	 */
	private static final long serialVersionUID = -8327033608934203400L;
	private String to;
	private String from;

	public KQMLForward(final String tx, final String rx,
			final String msg,
			final String irt, final String rw,
			final String newFrom, final String newTo) {
		super(tx,rx,msg,irt,rw);
		this.setPerformative("forward");
		this.setTo (newTo);
		this.setFrom (newFrom);
	}
	public String getFrom() { return this.from;}
	public String getTo() {return this.to;}
	public void setFrom(final String newFrom) {this.from = newFrom;}
	public void setTo(final String newTo) {this.to = newTo;}

	@Override
	public void processKQML(final OntologyBasedAgent a)
	{
		a.processForward(this);
	}
}
