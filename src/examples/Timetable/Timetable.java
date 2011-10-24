package examples.Timetable;

/**
 * Insert the type's description here.
 * Creation date: (19/03/03 21:34:29)
 * @author: Tarek JARRAYA
 */

import java.util.Vector;

import dima.basicagentcomponents.AgentName;
import dima.kernel.FIPAPlatform.AgentManagementSystem;
import dima.kernel.INAF.InteractionDomain.Constraint;
import dima.kernel.INAF.InteractionTools.Operator;


public class Timetable
{

/**
 * emploiDuTemps constructor comment.
 */
public Timetable()
{
	super();
}
/**
 * Starts the application.
 * @param args an array of command-line arguments
 */


public static void main(final java.lang.String[] args)
{

//initialisation du premier enseignant
	final Enseignant e1 = new Enseignant("enseignant1");

//initialisation du deuxi�me enseignant
	final Enseignant e2 = new Enseignant("enseignant2");

//initialisation du troisi�me enseignant
	final Enseignant e3 = new Enseignant("enseignant3");

//initialisation du premier groupe
	final Groupe g1 = new Groupe("groupe1");

//initialisation du deuxi�me groupe
	final Groupe g2 = new Groupe("groupe2");

//initialisation du troisi�me groupe
	final Groupe g3 = new Groupe("groupe3");


// ajouter des services "seance" aux agents enseignant et groupe

	for(int i =1;i<=2;i++ )
		for(int j=1;j<=4;j++ )
		{
			final Seance s = new Seance(i,j);

			e1.addService(s);
			e2.addService(s);
			e3.addService(s);

			g1.addService(s);
			g2.addService(s);
			g3.addService(s);

		}

// ajouter des needs aux agents enseignant

	for(int i = 1;i<=3;i++ )
	{
		e1.addNeed(new AgentName("groupe"+i));
		e2.addNeed(new AgentName("groupe"+i));
		e3.addNeed(new AgentName("groupe"+i));
	}

// ajouter des needs aux agents groupe

	for(int i = 1;i<=3;i++ )
	{
		g1.addNeed(new AgentName("enseignant"+i));
		g2.addNeed(new AgentName("enseignant"+i));
		g3.addNeed(new AgentName("enseignant"+i));
	}
// Ajouter des contraintes aux agents enseignants

	Constraint c = new Constraint(new Seance(1,4),new Operator("!="));

	g1.addConstraint(c);

	c = new Constraint(new Seance(1,2),new Operator("!="));

	g2.addConstraint(c);

	c = new Constraint(new Seance(1,4),new Operator("!="));

	g2.addConstraint(c);

	c = new Constraint(new Seance(2,1),new Operator("!="));

	g3.addConstraint(c);

	c = new Constraint(new Seance(2,2),new Operator("!="));

	g3.addConstraint(c);

	AgentManagementSystem.initAMS();

//ajouter les acquointances

	Vector v = new Vector();
	v.add(e1.getAddress());
	v.add(e2.getAddress());
	v.add(e3.getAddress());

	g1.addAquaintances(v);
	g2.addAquaintances(v);
	g3.addAquaintances(v);

	v = new Vector();
	v.add(g1.getAddress());
	v.add(g2.getAddress());
	v.add(g3.getAddress());

	e1.addAquaintances(v);
	e2.addAquaintances(v);
	e3.addAquaintances(v);

// Activer les agents

	e1.activate();
	e2.activate();
	e3.activate();

	g1.activate();
	g2.activate();
	g3.activate();
}
}
