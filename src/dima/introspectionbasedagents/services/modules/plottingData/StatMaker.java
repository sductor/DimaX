package dima.introspectionbasedagents.services.modules.plottingData;

import java.util.LinkedList;
import java.util.List;

import dima.introspectionbasedagents.services.core.communicating.execution.SystemCommunicationService;


public class StatMaker extends SystemCommunicationService {

	private static final long serialVersionUID = 4820712229141678066L;

	static String sortie = "results.log";
	static String fichierScript = "plotWhiskerBox.gp";

	private final String myPath = System.getProperty("user.dir") + "/Stat/";

	public StatMaker() {
		System.out.println("Dossier de resultat : " + this.myPath);
	}

	public void trace(final String parametres,
			final LinkedList<List<Integer>> stats) {

		// String comment =
		// "#"+ParametrePopulation.printParametre()+"__PourPaysage:"+Paysage.printParam(Environnement.topologie)+
		// "\n# iteration, NombreGoodGene, NombreGoodphene, ";
		// String paysages ="";
		// if (TypePaysage.getGoodvalues().size()>1){
		// for (TypePaysage p : TypePaysage.getGoodvalues()){
		// paysages+="NbGoodGene_"+p.toString()+", "+"NbGoodPhene_"+p.toString()+", ";
		// }
		// paysages+="Nb_H.S.H., ";
		// // }
		// comment += paysages+"TaillePopulation";
		// PrintWriter pwf;
		// PrintWriter pwfich;
		//
		// Date date = new Date();
		// String Nomfichier = myPath+date.toString()+"__"+parametres+".gp";
		// String NomfichierGraph=myPath+sortie; //pour le script
		//
		// //String Nomfichier = myPath+"29_fev_08"+"_"+parametres+".gp"; //sous
		// windows, car ne gere pas les espaces
		// File f=new File(Nomfichier); //fichier
		// File fich=new File(NomfichierGraph); //fichier
		//
		// try{
		//
		// System.out.println("Ouverture en creation du fichier de sortie");
		// FileWriter f1 = new FileWriter(f); //creation de l'objet sur lequel
		// on va travailler
		// FileWriter fich1=new FileWriter(fich);
		//
		// System.out.println("adresse absolue des fichier: ");
		// System.out.println("Fichier de sauvegarde : "+f.getAbsolutePath());
		// System.out.println("Fichier Temporaire : "+fich.getAbsolutePath());
		//
		// Integer iteration = 0;
		//
		// pwf = new PrintWriter(f1); //, true); => true pour flusher
		// pwfich= new PrintWriter(fich1); //, true); => true pour flusher
		//
		// pwfich.println(comment);//ajout de la ligne d'explication dans le
		// fichier source
		// pwf.println(comment);//ajout de la ligne d'explication dans le
		// fichier source
		// pwfich.flush();
		//
		// System.out.println("\n\nParcours des Statistiques Sauvegarder");
		// for(List<Integer> values : stats){// pour chaque element de type
		// List<Integer> de la linkedList stats
		//
		// String texte = iteration.toString();
		// System.out.println("n° de l'itération: "+texte);
		// for (Integer v : values){//itération sur la List<Integer>
		// texte += " "+v.toString();
		// }
		// System.out.println("  => valeur du champ texte "+texte);
		//
		//
		// pwf.println(texte);
		// pwfich.println(texte);
		//
		// pwf.flush(); //passage à la ligne
		// pwfich.flush();
		//
		// //pwf.close();
		// //pwfich.close();
		//
		// iteration++;
		// }
		//
		// pwf.close();
		// pwfich.close();
		// f1.close();
		// fich1.close();
		// System.out.println("FICHIERS INSTANCIES");
		//
		// } catch (IOException e) {
		// System.out.println("Exception attrapee dans StatMaker");
		// e.printStackTrace();
		// System.exit(-1);
		// }
		//
		// System.out.println("avant gnuplot");
		// this.executeWithBash(fichierScript);
		// System.out.println("apres gnuplot => png creer");
	}
}
