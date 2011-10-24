package dimaxx.tools.computingFuzzyLogic.controller;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeSet;


import org.jdom.Document;
import org.jdom.Element;
import org.jdom.input.SAXBuilder;

import dima.introspectionbasedagents.coreservices.loggingactivity.LogCompetence;
import dimaxx.hostcontrol.LocalHost;
import dimaxx.tools.computingFuzzyLogic.defuzzyficator.Defuzzificateur;
import dimaxx.tools.computingFuzzyLogic.implicator.Implicateur;

/**
 *
 * @author Sylvain Ductor
 */
public class FuzzyController implements Serializable {

	private static final long serialVersionUID = -3269201053415166658L;

	public String label;

	protected List<FuzzyRule> regles;
	protected Defuzzificateur dFuzz;
	protected List<FuzzySet> parametres;

	public FuzzyController(final String controleurFile,
			final Implicateur alors, final Defuzzificateur d) {

		final String f = LocalHost.getDir()
				+ "src/tools/fuzzyLogicApi/fuzzyControllerDataBase/"
				+ controleurFile;
		try {
			// URI uri;
			// uri = new URI(f);
			// File xml = new File(uri);
			final File xml = new File(f);
			this.init(xml, alors, d);
		} catch (final Exception e) {
			LogCompetence.writeException(this, "Le fichier " + f
					+ " n'existe pas", e);
		}
	}

	public void init(final File f, final Implicateur alors,
			final Defuzzificateur d) {

		final ControleurParser p = new ControleurParser(f);

		this.label = p.getLabel();
		this.parametres = p.getParametre();
		this.regles = p.getRegles(alors);
		this.dFuzz = d;
	}

	public List<FuzzyRule> getRegles() {
		return this.regles;
	}

	public List<FuzzySet> getParametre() {
		return this.parametres;
	}

	/**
	 *
	 * @param nom
	 * @return le subset ayant le bon nom, null s'il n'existe pas
	 */
	public FuzzySet getFuzzySet(final String nom) {
		for (final FuzzySet f : this.parametres)
			if (f.nom.equals(nom))
				return f;
		return null;
	}

	public FuzzySubSet mixageConclusions(final List<FuzzySubSet> conclusions) {
		if (conclusions.size() == 0)
			LogCompetence.writeException(this,
					"ERREUR Dans Mixage de Regle# aucune conclusion");

		final Iterator<FuzzySubSet> itConclusion = conclusions.iterator();

		FuzzySubSet cGene = itConclusion.next();

		while (itConclusion.hasNext())
			cGene = cGene.sousEnsembleUnion(itConclusion.next());
		// cGene.printValue();
		return cGene;
	}

	public FuzzySubSet getCalculateSubset(final List<Double> observations) {
		final List<FuzzySubSet> conclusions = this.getConclusions(observations);

		// myLog.ecrit(
		// "\n                  œœœœœœœœœœœœœœœœœœ                 \n=> MIXAGE DE REGLE \n"
		// ,0);
		return this.mixageConclusions(conclusions);
	}

	public List<FuzzySubSet> getConclusions(final List<Double> observations) {
		final List<FuzzySubSet> conclusions = new ArrayList<FuzzySubSet>(
				this.regles.size());

		final Iterator<FuzzyRule> itRegle = this.regles.iterator();
		while (itRegle.hasNext()) {
			final FuzzyRule r = itRegle.next();

			// Logger.ecrit(
			// "\n                  œœœœœœœœœœœœœœœœœœ                 \n=> Calcul de la regle "
			// + r.nom + "\n", 0);
			conclusions.add(r.MP(observations));
		}
		return conclusions;
	}

	public double calcule(final List<Double> observations) {
		// myLog.ecrit("###Calculs des conclusions des observations : ", 0);
		final FuzzySubSet mixRegle = this.getCalculateSubset(observations);

		// myLog.ecrit(""\n œœœœœœœœœœœœœœœœœœ \n=> Defuzzyfication \n",0);
		final double commande = this.dFuzz.defuzz(mixRegle);

		// myLog.ecrit("###Fin Calculs des conclusions des observations : ", 0);
		return commande;
	}

	public double deFuzz(final FuzzySubSet s) {
		return this.dFuzz.defuzz(s);
	}
}

class ControleurParser {

	Document document;
	Element racine;

	private final String controleurLabel;

	// Variables associe à chaque ensemble floue ses différents sef
	private Map<String, Map<String, FuzzySubSet>> variables;

	/**
	 * Constructeur
	 */

	public ControleurParser(final File F) {
		final SAXBuilder sxb = new SAXBuilder();

		try {
			this.document = sxb.build(F);
		} catch (final Exception e) {
			e.printStackTrace();
			System.exit(-1);
		}

		this.racine = this.document.getRootElement();

		this.controleurLabel = this.racine.getChildText("label");
		this.extractEntry();
	}

	/**
	 * Interface
	 */

	public String getLabel() {
		return this.controleurLabel;
	}

	public List<FuzzySet> getParametre() {
		String nomP;
		String unite;
		Couple borne;
		final List<FuzzySet> parametres = new ArrayList<FuzzySet>();

		final Iterator<Map<String, FuzzySubSet>> itMap = this.variables
				.values().iterator();
		while (itMap.hasNext()) {
			final List<FuzzySubSet> partition = new ArrayList<FuzzySubSet>(
					itMap.next().values());
			if (partition.size() == 0) {
				System.out.println("ERREUR : pas de sef!");
				System.exit(-1);
			}

			final FuzzySubSet a = partition.get(0);

			nomP = a.nomEnsemble;
			unite = a.nomSousEnsemble;
			borne = a.ensembleDefinition;

			parametres.add(new FuzzySet(nomP, unite, borne, partition));
		}
		return parametres;
	}

	@SuppressWarnings("unchecked")
	public List<FuzzyRule> getRegles(final Implicateur alors) {

		final List<Element> listRegles = this.racine.getChildren("regle");
		final List<FuzzyRule> regles = new ArrayList<FuzzyRule>(listRegles
				.size());

		// Extraction d'une regle
		final Iterator<Element> itR = listRegles.iterator();
		while (itR.hasNext()) {
			final Element regleCourante = itR.next();
			final String ident = regleCourante.getAttributeValue("ident");

			// Extraction des premisses de la regle
			final List<Element> listPremisses = regleCourante
					.getChildren("premisse");
			final List<FuzzySubSet> premisses = new ArrayList<FuzzySubSet>(
					listPremisses.size());

			final Iterator<Element> itP = listPremisses.iterator();
			while (itP.hasNext()) {
				final Element premisseCourante = itP.next();

				final String parametreCourant = premisseCourante
						.getChildText("identvariable");
				final String sefCourant = premisseCourante
						.getChildText("identsef");

				premisses.add(this.variables.get(parametreCourant).get(
						sefCourant));
			}

			// Extraction de la conclusion de la regle
			final String parametreConclusion = regleCourante.getChild(
					"conclusion").getChildText("identvariable");
			final String sefConclusion = regleCourante.getChild("conclusion")
					.getChildText("identsef");

			final FuzzySubSet conclusion = this.variables.get(
					parametreConclusion).get(sefConclusion);

			// Ajout de la regle
			regles.add(new FuzzyRule(ident, premisses, conclusion, alors));
		}

		return regles;
	}

	/**
	 * Primitives
	 */

	@SuppressWarnings("unchecked")
	private void extractEntry() {
		// System.out.println("  Extraction des entrée...");
		final List<Element> listVariables = this.racine.getChildren("variable");
		this.variables = new LinkedHashMap<String, Map<String, FuzzySubSet>>();

		// Extraction d'un ensemble floue
		final Iterator<Element> itV = listVariables.iterator();
		while (itV.hasNext()) {

			// Map qui contiendra les sef de l'ensemble floue courant
			final Map<String, FuzzySubSet> mSef = new LinkedHashMap<String, FuzzySubSet>();

			// Extraction des informations générales
			final Element variableCourante = itV.next();
			final String ident = variableCourante.getAttributeValue("ident");
			final String nom = variableCourante.getChildText("label");

			// Extraction des information sur les bornes
			final Element univers = variableCourante.getChild("univers");
			final String unite = univers.getAttributeValue("unite");
			final List<Element> bornes = univers.getChildren("borne");
			Double x, y;
			x = Math.min(new Double(bornes.get(0).getText()), new Double(bornes
					.get(1).getText()));
			y = Math.max(new Double(bornes.get(0).getText()), new Double(bornes
					.get(1).getText()));
			final Couple ensDef = new Couple(x, y);

			// Extraction des sous ensembles
			final List<Element> listSef = variableCourante.getChildren("sef");
			final Iterator<Element> itE = listSef.iterator();
			while (itE.hasNext()) {
				final Element sefCourant = itE.next();
				final String identSef = sefCourant.getAttributeValue("ident");// Pour
				// la
				// Map
				final String nomSef = sefCourant.getChildText("label");// Pour
																		// le
																		// sef

				// Extraction de la fonction d'appartenace du sef courant
				final List<Element> coordonne = sefCourant
						.getChildren("coordonnees");
				final TreeSet<Couple> fa = new TreeSet<Couple>();

				final Iterator<Element> itC = coordonne.iterator();
				while (itC.hasNext()) {
					final Element c = itC.next();
					final double abscisse = new Double(c.getChildText("x"))
							.doubleValue();
					final double ordonne = new Double(c.getChildText("y"))
							.doubleValue();
					final Couple p = new Couple(abscisse, ordonne);
					// System.out.println("   * Ajout du point " + p.toString()
					// + " dans le sef " + nom + ", " + nomSef);
					fa.add(p);
				}

				// Ajout du sous ensemble dans la Map de l'ensemble floue
				// courant
				final FuzzySubSet sef = new FuzzySubSet(fa, ensDef, nom,
						nomSef, unite);
				mSef.put(identSef, sef);
			}

			// Ajout de l'entrée
			this.variables.put(ident, mSef);

		}
		// System.out.println("Extraction des entrée  Faite!");
	}
}
