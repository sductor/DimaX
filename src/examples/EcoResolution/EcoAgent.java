package examples.EcoResolution;

import java.util.Enumeration;
import java.util.Vector;

import dima.kernel.BasicAgents.BasicReactiveAgent;


//***************************************************************************
//*********** EcoAgent : abstract superclass of the EPS framework *********
//*********** Author : Alexis Drogoul - Sept. 1998 **************************
//***************************************************************************

public  abstract class EcoAgent extends BasicReactiveAgent{

//*********** Properties : a set of goals and a set of dependants

	/**
	 *
	 */
	private static final long serialVersionUID = -2012047840959970620L;
	EcoAgent		goalAgent;
	Vector 			dependantAgents;

//*********** States : a set of states in which the agent can be

	int 					state;
	public static final int TRY_SATISFACTION = 0;
	public static final int AGGRESSION_SATISFACTION = 1;
	public static final int TRY_ESCAPE = 2;
	public static final int AGGRESSION_ESCAPE = 3;
	public static final int SATISFIED =4;
	public static final int ESCAPE = 5;
	public static final int FORBIDDEN = 6;

	EcoAgent() {
		this.dependantAgents = new Vector();
		this.goalAgent = null;
	}
   public void assignDependant( final EcoAgent dependant)
   {
	   if ( this.isSatisfied() || !this.hasGoal())
	   {
	        if ( dependant.isSatisfied()) dependant.becomeSatisfied();
	        else dependant.trySatisfaction();
	    } else if ( !this.dependantAgents.contains(dependant)) this.dependantAgents.addElement( dependant);
	}
	public void assignGoal( final EcoAgent goal)
	{
		 this.goalAgent = goal;
		if ( goal != null) goal.assignDependant( this);
	}
//************* Generic methods inherited by the subclasses

	public void becomeSatisfied()
	{
		this.changeStateTo(SATISFIED);
		this.informDependantsOfSatisfaction();
	}
//*********** Default methods to be redefined or called in concrete subclasses

	public Vector bolts()
	{
		return new Vector();
	}
	public abstract boolean canEscapeWithConstraint( EcoAgent constraint);
	public abstract boolean canSatisfyOnPlace(EcoAgent place);
 	public void changeStateTo( final int s)
 	{
		this.state = s;
	}
	public abstract void  doEscapeActionWithConstraint( EcoAgent constraint);
	public abstract void  doEscapeAggressionWithConstraint( EcoAgent constraint);
//*********** Abstract methods to be redefined in concrete subclasses

	public abstract void  doSatisfactionActionOnPlace(EcoAgent place) ;
	public abstract void  doSatisfactionAggressionOnPlace(EcoAgent place);
	public abstract EcoAgent findEscapePlaceWithConstraint( EcoAgent constraint);
	public abstract EcoAgent findSatisfactionPlace();
	public void freeWithConstraint( final EcoAgent constraint)
	{
		if ( !this.isFree())
			for (final Enumeration e = this.bolts().elements(); e.hasMoreElements();)
			{
				final EcoAgent agent = (EcoAgent) e.nextElement();
				agent.tryEscapeWithConstraint( constraint);
			}
	}
	public boolean hasGoal()
	{
		return this.goalAgent != null;
	}
	public void informDependantsOfSatisfaction() {
		final Vector dep = this.dependantAgents;
		for (final Enumeration e = this.dependantAgents.elements(); e.hasMoreElements();)
		{
			final EcoAgent agent = ( EcoAgent) e.nextElement();
			if ( agent.isSatisfied()) agent.becomeSatisfied();
			else agent.trySatisfaction();
		}
	}
@Override
public  boolean isActive()
  {return this.isSatisfied();}
/**
Tests wheter a proactive object has reached it goal or
 */
@Override
public boolean isAlive() {

	return this.isSatisfied();
}
	public abstract boolean isFree();
	public abstract boolean isSatisfied();
/**
 * This is the main method for a proactive component :
 * what to do while in activity.
 *
 */

@Override
public  void step()
{this.trySatisfaction();}
  	public void tryEscapeWithConstraint( final EcoAgent constraint)
  	{
	    final EcoAgent place = this.findEscapePlaceWithConstraint( constraint);
		if ( place == this.goalAgent)
		{
			this.doSatisfactionActionOnPlace(place);
			this.becomeSatisfied();
		}
		else
		{
			if ( !this.canEscapeWithConstraint( constraint))
			{
				this.changeStateTo( AGGRESSION_ESCAPE);
				this.doEscapeAggressionWithConstraint( place);
			}
			this.doEscapeActionWithConstraint( place);
			if ( this.goalAgent == place) this.changeStateTo( SATISFIED);
			else this.changeStateTo( TRY_SATISFACTION);
		}
	}
	public void trySatisfaction()
	{
		final EcoAgent place = this.findSatisfactionPlace();

		if ( !this.canSatisfyOnPlace(place))
		{
			this.changeStateTo(AGGRESSION_SATISFACTION);
			this.doSatisfactionAggressionOnPlace(place);
		}
		this.doSatisfactionActionOnPlace(place);
		this.becomeSatisfied();
	}
}
