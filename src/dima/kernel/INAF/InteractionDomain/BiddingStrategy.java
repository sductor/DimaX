package dima.kernel.INAF.InteractionDomain;

/**
 * Insert the type's description here.
 * Creation date: (24/06/2003 10:43:34)
 * @author:
 */
public abstract class BiddingStrategy extends AbstractStrategy
{
	public AbstractService currentBetterProposal;
	/**
	 * BiddingStrategy constructor comment.
	 */
	public BiddingStrategy() {
		super();
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24/06/2003 15:19:25)
	 * @return Gdima.kernel.INAF.InteractionDomain.AbstractService
	 */
	public AbstractService getCurrentBetterProposal() {
		return this.currentBetterProposal;
	}
	/**
	 * Insert the method's description here.
	 * Creation date: (24/06/2003 15:19:25)
	 * @param newCurrentBetterProposal Gdima.kernel.INAF.InteractionDomain.AbstractService
	 */
	public void setCurrentBetterProposal(final AbstractService newCurrentBetterProposal) {
		this.currentBetterProposal = newCurrentBetterProposal;
	}
}
