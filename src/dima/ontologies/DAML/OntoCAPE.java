package dima.ontologies.DAML;



/**
 * <p>Titre : </p>
 * <p>Description : </p>
 * <p>Copyright : Copyright (c) 2003</p>
 * <p>Soci�t� : LIP6 / IFP</p>
 * @author Othmane NADJEMI
 * @version 1.0
 */

public class OntoCAPE {
	protected static final String uri =
			"file:D:/mydoc/oc/daml/v07/modeling_task_specification.daml#";
	protected static final String uri1 =
			"file:D:/mydoc/oc/daml/v07/process_modeling_software.daml#";



	public static final String   modelComponent = OntoCAPE.uri1+"ModelComponent";
	public static final String   modelingTaskSpec = OntoCAPE.uri+"ModelingTaskSpec";

	public static String getURIMTS()
	{
		return OntoCAPE.uri;
	}
	public static String getURIMC()
	{
		return OntoCAPE.uri1;
	}

}