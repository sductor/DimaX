package examples.Enchere;

/**
 * Insert the type's description here.
 * Creation date: (22/06/2003 22:40:18)
 * @author: Tarek JARRAYA
 */

import java.util.Vector;

import dima.kernel.FIPAPlatform.AgentManagementSystem;


public class Enchere
{

	/**
	 * Starts the application.
	 * @param args an array of command-line arguments
	 */

	public static void main(final java.lang.String[] args)
	{

		// le vendeur
		final Vendeur vendeur = new Vendeur("vendeur");

		//le catalogue des articles
		final Vector articles = new Vector();
		articles.add(new Article("art1",120));
		articles.add(new Article("art2",130));
		/*	articles.add(new Article("art3",80));
	articles.add(new Article("art4",100));
	articles.add(new Article("art5",50));
	articles.add(new Article("art6",150));
	articles.add(new Article("art7",200));
		 */
		vendeur.setCatalogue(new Catalogue(articles));

		// les 3 acheteurs
		final Acheteur acheteur1 = new Acheteur("acheteur1");
		final Acheteur acheteur2 = new Acheteur("acheteur2");
		final Acheteur acheteur3 = new Acheteur("acheteur3");

		// ajouter les acquointances
		AgentManagementSystem.initAMS();

		vendeur.addAquaintance(acheteur1.getAddress());
		vendeur.addAquaintance(acheteur2.getAddress());
		vendeur.addAquaintance(acheteur3.getAddress());

		acheteur1.addAquaintance(vendeur.getAddress());
		acheteur2.addAquaintance(vendeur.getAddress());
		acheteur3.addAquaintance(vendeur.getAddress());

		System.out.println("la liste des acheteurs "+vendeur.getBuyers());

		System.out.println("la liste des acquointances "+vendeur.getAquaintances().values());

		// activer les agents

		acheteur1.activate();
		acheteur2.activate();
		acheteur3.activate();
		vendeur.activate();
	}
}
