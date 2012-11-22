package frameworks.faulttolerance.experimentation;

import java.io.File;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;

import dima.introspectionbasedagents.modules.distribution.NormalLaw.DispersionSymbolicValue;
import dima.introspectionbasedagents.services.loggingactivity.LogService;
import dima.support.GimaObject;
import frameworks.experimentation.ExperimentationParameters;
import frameworks.negotiation.NegotiationParameters;
import frameworks.negotiation.NegotiationParameters.SelectionType;
import frameworks.negotiation.rationality.SocialChoiceFunction.SocialChoiceType;

public class ReplicationExperimentationGenerator extends GimaObject{

	/**
	 * 
	 */
	private static final long serialVersionUID = -3325093836959956134L;
	//problem
	private final Long[] seeds = new Long[]{(long) 889757,(long) 1223435,(long) 5564864,(long) 646464,(long) 94864};
	//
	//		private Integer[] nbAgentDomain = new Integer[]{500,2500,5000,7500,10000};
	//	public static int _AgentDefault=2500;
	//		private Integer[] nbAgentDomain = new Integer[]{50,250,500,750,1000};
	//		public static int _AgentDefault=250;
//		private Integer[] nbAgentDomain = new Integer[]{100};
//			public static int _AgentDefault=100;
	//		private Integer[] nbAgentDomain = new Integer[]{50,100,500,1000,5000,10000};
	//		public static int _AgentDefault=1000;
	private final Integer[] nbAgentDomain = new Integer[]{250,500,1000,2500,5000,7500,10000};
	public static int _AgentDefault=500;
	int maxAgentNb = Collections.max(Arrays.asList(this.nbAgentDomain));

//	private final Integer[] nbHostDomain = new Integer[]{6};
	//			private Integer[] nbHostDomain = new Integer[]{24};
			private Integer[] nbHostDomain = new Integer[]{16};
//			private Integer[] nbHostDomain = new Integer[]{5};
	int maxHostNb = Collections.max(Arrays.asList(this.nbHostDomain));

	//solveur
	//	private Integer[] kDomain= new Integer[]{5,10};
	//	public static int _kDefault1=5;
	//	public static int _kDefault2=10;
	//	private Integer[] kDomain= new Integer[]{50,250,500,750,1000};
	//		public static int _kDefault1=1000;
	//		public static int _kDefault2=200;
	private final Integer[] kDomain= new Integer[]{5,50,100,250,500};
	public static int _kDefault=250;
	//		private Integer[] kDomain= new Integer[]{5,10,50,100,500,1000};
	//		public static int _kDefault=100;
	//		public static int _kDefaultAll=100;


	//	private Double[] hostCapacity = new Double[]{0.5};//-((double) maxAgentNb/((double) maxHostNb)),
	//	private Double[] graphDensityDomain = new Double[]{0.5};//15.,
	private static Double[] hostCapacity = new Double[]{0.3};//null;//new Double[]{0.2,0.8};//-((double) maxAgentNb/((double) maxHostNb)),
	private static  Double[] graphDensityDomain =new Double[]{ 1.0};//	null;// new Double[]{0.8,0.2};//15.,

	//	static{
	//		if (ReplicationLaborantin.informativeParameter4Graph==0){
	//			hostCapacity = new Double[]{0.3};
	//			graphDensityDomain = new Double[]{1.};
	//
	//		} else if (ReplicationLaborantin.informativeParameter4Graph==1){
	//			hostCapacity = new Double[]{0.8};
	//			graphDensityDomain = new Double[]{0.8};
	//		} else if (ReplicationLaborantin.informativeParameter4Graph==2){
	//			hostCapacity = new Double[]{0.2};
	//			graphDensityDomain = new Double[]{0.8};
	//		} else if (ReplicationLaborantin.informativeParameter4Graph==3){
	//			hostCapacity = new Double[]{0.8};
	//			graphDensityDomain = new Double[]{0.2};
	//		} else if (ReplicationLaborantin.informativeParameter4Graph==4){
	//			hostCapacity = new Double[]{0.2};
	//			graphDensityDomain = new Double[]{0.2};
	//		}
	//	}

	private final SocialChoiceType[] welfareDomain =
			new SocialChoiceType[]{SocialChoiceType.Utility,
			SocialChoiceType.Nash,
			SocialChoiceType.Leximin};

	private final String[] protosDomain = new String[]{
			NegotiationParameters.key4mirrorProto,
			NegotiationParameters.key4statusProto,
			NegotiationParameters.key4GeneticProto,
			NegotiationParameters.key4DcopProto
	};
	private final SelectionType[] selectDomain = new SelectionType[]{
			//			SelectionType.Opt,
			//			SelectionType.Greedy,
			//			SelectionType.Better,
			//			SelectionType.Random,
			SelectionType.RoolettWheel};

	//Social
	private final Double[][] alphaDomain=
			new Double[][]{new Double[]{Double.NaN,Double.NaN},
			new Double[]{0.3,0.3},
			new Double[]{0.6,0.6},
			new Double[]{0.6,0.3},
			new Double[]{0.3,0.6}};
	static Double _alpha_lowDefault=0.6;
	static Double _alpha_highDefault=0.6;
	//	private Double[][] alphaDomain=
	//			new Double[][]{new Double[]{Double.NaN,Double.NaN},
	//			new Double[]{0.2,0.4},
	//			new Double[]{0.4,0.6},
	//			new Double[]{0.6,0.8},
	//			new Double[]{0.2,0.8}};
	private final Double[] kOpinionDomain=
			new Double[]{Double.NaN,0.3,0.6};//,0.6,1.};
	static Double _kOpinionDefault=0.6;


	public static Double getValue(final Double value, final double ref){
		if (value.equals(Double.NaN)) {
			return Double.NaN;
		} else if (value <0) {
			return -value;
		} else{
			assert value<=1.:value;
			return value*ref;
		}
	}

	Double[] dispoMeanDomain = new Double[]{0.7};
	Double[] criticityMeanDomain = new Double[]{0.5};
	Double[] agentLoadMeanDomain = new Double[]{0.5};
	DispersionSymbolicValue[] dispersionDomain = new DispersionSymbolicValue[]{
			DispersionSymbolicValue.Moyen,
			DispersionSymbolicValue.Nul,
			DispersionSymbolicValue.Max};



	static boolean varyProtocol=true;
	static boolean  varyOptimizers=true;

	static boolean varyAgents=true;
	static boolean varyHosts=false;

	static boolean varyAccessibleAgent=true;
	static boolean varySimultaneousAcceptation=true;
	static boolean varyOpinionDiffusion=true;
	static boolean varyAlpha=true;

	static boolean varyAgentSelection=false;
	static boolean varyHostSelection=true;

	static boolean varyHostDispo=false;
	static boolean varyHostFaultDispersion=false;

	static boolean varyAgentLoad=false;
	static boolean varyAgentLoadDispersion=false;

	static boolean varyHostCapacity=true;
	static boolean varyHostCapacityDispersion=false;

	static boolean varyAgentCriticity=false;
	static boolean varyAgentCriticityDispersion=false;

	//	static boolean varyFault=false;
	static int dynamicCriticityKey=-1; //-1 never dynamics, 1 always dynamics, 0 both


	//		startingNbAgents =(int)((startingNbHosts * hostCapacityMean)/agentLoadMean);

	public static final boolean multiDim=true;

	//
	//	public static final int startingNbHosts = 8;
	//	public static int startingNbAgents =15;

	//		public static final int startingNbHosts = 5;
	//		public static int startingNbAgents =10;



	//
	// Protocole
	//

	//
	//  Génération de simulation
	// /////////////////////////////////

	//
	// Set of values
	//
	//
	//
	// Variation configuration
	//

	//
	// Default values
	//

	public Long[] getSeeds() {
		return this.seeds;
	}

	ReplicationExperimentationParameters getDefaultParameters() {
		assert this.alphaDomain[0][1]!=0.;
		return new ReplicationExperimentationParameters(
				this.nbAgentDomain[0],
				this.nbHostDomain[0],
				ReplicationExperimentationGenerator.getValue(ReplicationExperimentationGenerator.graphDensityDomain[0],this.nbAgentDomain[0]).intValue(),//ReplicationExperimentationParameters.doubleParameters.get(2),//kaccessible
				this.dispoMeanDomain[0],//dispo mean
				DispersionSymbolicValue.Moyen,//dispo dispersion
				this.agentLoadMeanDomain[0],//ReplicationExperimentationProtocol.doubleParameters.get(1),//load mean
				DispersionSymbolicValue.Moyen,//DispersionSymbolicValue.Moyen,//load dispersion
				ReplicationExperimentationGenerator.getValue(ReplicationExperimentationGenerator.hostCapacity[0],this.nbAgentDomain[0]),//capacity mean//(double)ReplicationExperimentationParameters.startingNbAgents/(double)ReplicationExperimentationParameters.startingNbHosts,//ReplicationExperimentationParameters.doubleParameters.get(1),2.5,//
				DispersionSymbolicValue.Faible,//DispersionSymbolicValue.Faible,//capcity dispersion
				this.criticityMeanDomain[0],//criticity mean
				DispersionSymbolicValue.Fort,//criticity dispersion
				Integer.MAX_VALUE,
				this.kDomain[0],
				ReplicationExperimentationGenerator.getValue(this.kOpinionDomain[0],this.nbAgentDomain[0]),
				this.protosDomain[0],//NegotiationParameters.key4statusProto,//NegotiationParameters.key4CentralisedstatusProto,//
				this.welfareDomain[0],
				SelectionType.Greedy,//NegotiationParameters.key4rouletteWheelSelect,//
				SelectionType.RoolettWheel,//NegotiationParameters.key4BetterSelect,//NegotiationParameters.key4greedySelect,//
				this.alphaDomain[0][0],
				this.alphaDomain[0][1],
				false,
				false);
	}

	public static String getProtocolId() {
		return ExperimentationParameters._maxSimulationTime / 1000
				+ "secs"
				+ (ReplicationExperimentationGenerator.varyAgentSelection==true?"varyAgentSelection":"")
				+ (ReplicationExperimentationGenerator.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationGenerator.varyProtocol?"varyProtocol":"")
				+ (ReplicationExperimentationGenerator.varyHostDispo?"varyHostDispo":"")
				+ (ReplicationExperimentationGenerator.varyHostSelection?"varyHostSelection":"")
				+ (ReplicationExperimentationGenerator.varyOptimizers?"varyOptimizers":"")
				+ (ReplicationExperimentationGenerator.varyAccessibleAgent?"varyAccessibleHost":"")
				+ (ReplicationExperimentationGenerator.varySimultaneousAcceptation?"varySimultaneousAcceptation":"")
				+ (ReplicationExperimentationGenerator.varyOpinionDiffusion?"varyOpinionDiffusion":"")
				+ (ReplicationExperimentationGenerator.varyAgentLoad?"varyAgentLoad":"")
				+ (ReplicationExperimentationGenerator.varyHostCapacity?"varyHostCapacity":"");
	}

	public LinkedList<ExperimentationParameters<ReplicationLaborantin>> generateSimulation() {
		//		final String usedProtocol, agentSelection, hostSelection;
		//		f.mkdirs();
		new File(LogService.getMyPath()+"result_"+ReplicationExperimentationGenerator.getProtocolId()+"/").mkdirs();
		Collection<ReplicationExperimentationParameters> simuToLaunch =
				new HashSet<ReplicationExperimentationParameters>();
		simuToLaunch.add(this.getDefaultParameters());
		assert this.getDefaultParameters().alpha_high!=0;
		if (ReplicationExperimentationGenerator.varyAgents) {
			simuToLaunch = this.varyAgents(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHosts) {
			simuToLaunch = this.varyHosts(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAccessibleAgent) {
			simuToLaunch = this.varyAccessibleHost(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyOpinionDiffusion) {
			simuToLaunch = this.varyOpinionDiffusion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAlpha) {
			simuToLaunch = this.varyAlpha(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varySimultaneousAcceptation) {
			simuToLaunch = this.varySimultaneousAcceptation(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostDispo) {
			simuToLaunch = this.varyHostDispo(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostFaultDispersion) {
			simuToLaunch = this.varyHostFaultDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentLoad) {
			simuToLaunch = this.varyAgentLoad(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentLoadDispersion) {
			simuToLaunch = this.varyAgentLoadDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostCapacity) {
			simuToLaunch = this.varyHostCapacity(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostCapacityDispersion) {
			simuToLaunch = this.varyHostCapacityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentCriticity) {
			simuToLaunch = this.varyAgentCriticity(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentCriticityDispersion) {
			simuToLaunch = this.varyAgentCriticityDispersion(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyAgentSelection) {
			simuToLaunch = this.varyAgentSelection(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyHostSelection) {
			simuToLaunch = this.varyHostSelection(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyOptimizers) {
			simuToLaunch = this.varyOptimizers(simuToLaunch);
		}
		if (ReplicationExperimentationGenerator.varyProtocol) {
			simuToLaunch = this.varyProtocol(simuToLaunch);
		}
		//		if (ReplicationExperimentationGenerator.varyFault) {
		//			simuToLaunch = this.varyMaxSimultFailure(simuToLaunch);
		//		}
		//		System.out.println("yoooooooooooooooooooooooooooooooooooooooooooooooooooooooooo"+simuToLaunch);

		simuToLaunch = this.varyDynamicCriticity(simuToLaunch);

		final Comparator<ExperimentationParameters<ReplicationLaborantin>> comp =
				new Comparator<ExperimentationParameters<ReplicationLaborantin>>() {

			@Override
			public int compare(final ExperimentationParameters<ReplicationLaborantin> o1,
					final ExperimentationParameters<ReplicationLaborantin> o2) {
				return o1.getSimulationName().compareTo(o2.getSimulationName());
			}
		};

		final LinkedList<ExperimentationParameters<ReplicationLaborantin>> simus =
				new LinkedList<ExperimentationParameters<ReplicationLaborantin>>();
		for (final ReplicationExperimentationParameters p : simuToLaunch) {
			//			System.out.println("considering "+p);
			if (p.isValid()){
				//				System.out.println("added!");
				simus.add(p);
			}else{
				//								this.logWarning("ABORTED !!! "+(p.nbHosts*p.agentAccessiblePerHost<p.nbAgents)+" "+(p.agentAccessiblePerHost<=0)+" \n"+p, LogService.onBoth);
			}
		}
		Collections.sort(simus,comp);
		return simus;
	}


	/*
	 *
	 */


	//
	// Primitive
	//



	private Collection<ReplicationExperimentationParameters> varyProtocol(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final String v : Arrays.asList(this.protosDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n._usedProtocol=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentSelection(final Collection<ReplicationExperimentationParameters> exps){
		assert false:"la selection agent est tjs greedy";
	final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
	for (final ReplicationExperimentationParameters p : exps) {
		for (final SelectionType v : Arrays.asList(this.selectDomain)){
			final ReplicationExperimentationParameters n =  p.clone();
			n._agentSelection=v;
			result.add(n);
		}
	}
	return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostSelection(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final SelectionType v : Arrays.asList(this.selectDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n._hostSelection=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOptimizers(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final SocialChoiceType v : Arrays.asList(this.welfareDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n._socialWelfare=v;
				result.add(n);
			}
		}
		return result;
	}
	//	private Collection<ReplicationExperimentationParameters> varyAgents(final Collection<ReplicationExperimentationParameters> exps){
	//		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
	//		for (final ReplicationExperimentationParameters p : exps) {
	//			for (final Double v : ReplicationExperimentationGenerator.doubleParameters6){
	//				final ReplicationExperimentationParameters n =  p.clone();
	//				n.nbAgents=(int)(v*ReplicationExperimentationGenerator.startingNbAgents);
	//				//				n.nbAgents=(int)((v  * n.nbHosts * n.hostCapacityMean)/n.agentLoadMean);
	//				result.add(n);
	//			}
	//		}
	//		return result;
	//	}
	private Collection<ReplicationExperimentationParameters> varyAgents(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			assert p.alpha_high!=0;
			final List<Integer> nbAgentsList = Arrays.asList(this.nbAgentDomain);
			for (final Integer v : nbAgentsList){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbAgents=v;
				result.add(n);
			}

			//			for (final Double v : ReplicationExperimentationParameters.doubleParameters6){
			//				final ReplicationExperimentationParameters n =  p.clone();
			//				n.nbAgents=(int)(v*ReplicationExperimentationParameters.startingNbAgents);
			////				n.nbAgents=(int)((v  * n.nbHosts * n.hostCapacityMean)/n.agentLoadMean);
			//				result.add(n);
			//			}

			//
			//
			//				final ReplicationExperimentationParameters n1 =  p.clone();
			//				n1.nbAgents=50;
			//				result.add(n1);
			//				final ReplicationExperimentationParameters n2 =  p.clone();
			//				n2.nbAgents=80;
			//				result.add(n2);
			//				final ReplicationExperimentationParameters n3 =  p.clone();
			//				n3.nbAgents=150;
			//				result.add(n3);
			//				final ReplicationExperimentationParameters n4 =  p.clone();
			//				n4.nbAgents=300;
			//				result.add(n4);


		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHosts(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Integer v : Arrays.asList(this.nbHostDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n.nbHosts=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAccessibleHost(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : Arrays.asList(ReplicationExperimentationGenerator.graphDensityDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n.agentAccessiblePerHost=ReplicationExperimentationGenerator.getValue(v,new Double(p.nbAgents)).intValue();
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyOpinionDiffusion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : Arrays.asList(this.kOpinionDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n.opinionDiffusionDegree=ReplicationExperimentationGenerator.getValue(v,new Double(p.nbAgents));
				result.add(n);
				//				System.out.println(Arrays.asList(v)+" "+n.opinionDiffusionDegree);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAlpha(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double[] v : Arrays.asList(this.alphaDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				assert n.alpha_high!=0.;
				n.alpha_low=v[0];
				n.alpha_high=v[1];
				result.add(n);
				//				if (n.opinionDiffusionDegree.equals(0.6))System.out.println(Arrays.asList(v));
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varySimultaneousAcceptation(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Integer v : Arrays.asList(this.kDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n.kSolver=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostDispo(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v :  Arrays.asList(this.dispoMeanDomain)){
				final ReplicationExperimentationParameters n =  p.clone();
				n.hostFaultProbabilityMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyHostFaultDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : Arrays.asList(this.dispersionDomain)){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostFaultProbabilityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoad(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : Arrays.asList(this.agentLoadMeanDomain)){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadMean=v;
				result.add(n);
			}
		}
		return result;
	}

	private Collection<ReplicationExperimentationParameters> varyAgentLoadDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : Arrays.asList(this.dispersionDomain)){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentLoadDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	//	private Collection<ReplicationExperimentationParameters> varyHostCapacity2(final Collection<ReplicationExperimentationParameters> exps){
	//		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
	//		for (final ReplicationExperimentationParameters p : exps) {
	//			for (final Double v : ReplicationExperimentationParameters.doubleParameters){
	//				final ReplicationExperimentationParameters n = p.clone();
	//				n.hostCapacityMean=v;
	//				result.add(n);
	//			}
	//		}
	//		return result;
	//	}

	private Collection<ReplicationExperimentationParameters> varyHostCapacity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : Arrays.asList(ReplicationExperimentationGenerator.hostCapacity)){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityMean=ReplicationExperimentationGenerator.getValue(v,p.nbAgents);
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyHostCapacityDispersion(
			final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : Arrays.asList(this.dispersionDomain)){
				final ReplicationExperimentationParameters n = p.clone();
				n.hostCapacityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticity(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final Double v : Arrays.asList(this.criticityMeanDomain)){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityMean=v;
				result.add(n);
			}
		}
		return result;
	}
	private Collection<ReplicationExperimentationParameters> varyAgentCriticityDispersion(final Collection<ReplicationExperimentationParameters> exps){
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			for (final DispersionSymbolicValue v : Arrays.asList(this.dispersionDomain)){
				final ReplicationExperimentationParameters n = p.clone();
				n.agentCriticityDispersion=v;
				result.add(n);
			}
		}
		return result;
	}

	//	private Collection<ReplicationExperimentationParameters> varyMaxSimultFailure(final Collection<ReplicationExperimentationParameters> exps){
	//		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
	//		for (final ReplicationExperimentationParameters p : exps) {
	//			for (final Double v : ReplicationExperimentationGenerator.doubleParameters2){
	//				final ReplicationExperimentationParameters n = p.clone();
	//				n.setMaxSimultFailure(v);
	//				result.add(n);
	//			}
	//		}
	//		return result;
	//	}
	private Collection<ReplicationExperimentationParameters> varyDynamicCriticity(
			final Collection<ReplicationExperimentationParameters> exps) {
		assert ReplicationExperimentationGenerator.dynamicCriticityKey>=-1 && ReplicationExperimentationGenerator.dynamicCriticityKey<=1;
		final Collection<ReplicationExperimentationParameters> result=new HashSet<ReplicationExperimentationParameters>();
		for (final ReplicationExperimentationParameters p : exps) {
			if (ReplicationExperimentationGenerator.dynamicCriticityKey==-1){
				p.dynamicCriticity=false;
				result.add(p);
			} else if (ReplicationExperimentationGenerator.dynamicCriticityKey==1){
				p.dynamicCriticity=true;
				result.add(p);
			} else {
				final ReplicationExperimentationParameters n = p.clone();
				n.dynamicCriticity=!p.dynamicCriticity;
				result.add(n);
			}
		}
		return result;

	}

}




//static List<String> protos = Arrays.asList(new String[]{
//		NegotiationParameters.key4mirrorProto,
//		NegotiationParameters.key4CentralisedstatusProto
//		//			,NegotiationParameters.key4statusProto
//});
//static List<SocialChoiceType> welfare = Arrays.asList(new SocialChoiceType[]{SocialChoiceType.Utility, SocialChoiceType.Leximin,SocialChoiceType.Nash});//
//static List<String> select = Arrays.asList(new String[]{
//		NegotiationParameters.key4greedySelect,
//		NegotiationParameters.key4randomSelect,
//		NegotiationParameters.key4rouletteWheelSelect});//,key4AllocSelect
//static List<DispersionSymbolicValue> dispersion = Arrays.asList(new DispersionSymbolicValue[]{
//		DispersionSymbolicValue.Nul,
//		DispersionSymbolicValue.Moyen,
//		DispersionSymbolicValue.Max});
//static List<Double> doubleParameters = Arrays.asList(new Double[]{
//		0.1,
//		0.5,
//		1.});
//static List<Double> doubleParameters4 = Arrays.asList(new Double[]{
//		0.3,
//		0.6,
//		1.});
//static List<Double> doubleParameters5 = Arrays.asList(new Double[]{
//		0.1,
//		0.33
//		,0.66
//			,1.
//});
//static List<Double> doubleParameters2 = Arrays.asList(new Double[]{
//		0.,
//		0.5,
//		1.});
//static List<Double> doubleParameters3 = Arrays.asList(new Double[]{
//		0.,
//		0.25,
//		0.5,
//		0.75,
//		1.});
//static List<Double> doubleParameters6 = Arrays.asList(new Double[]{
//		//			0.01,
//		0.1,
//		//			0.25,
//		0.5,
//		//			0.75,
//		//			1.
//});
//pref TODO : Non imple chez l'agent!!
//	Collection<String> agentPref = Arrays.asList(new String[]{
//			ReplicationExperimentationProtocol.key4agentKey_Relia,
//			ReplicationExperimentationProtocol.key4agentKey_loadNRelia});
//	static final String key4agentKey_Relia="onlyRelia";
//	static final String key4agentKey_loadNRelia="firstLoadSecondRelia";
