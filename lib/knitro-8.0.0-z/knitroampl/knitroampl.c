/*******************************************************/
/* Copyright (c) 2001-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

/* AMPL interface for the solver KNITRO 8.x */

#include <assert.h>
#include <stdio.h>
#if defined(_OPENMP)
   #include "omp.h"
#endif
#include "knitro.h"
#include "getstub.h"
#include "nlp.h"
#define SKIP_NL2_DEFINES
#undef f_OPNUM
#include "jacpdim.h"
#undef f_OPNUM
#include "r_opn0.hd"

#define FC_EVAL 1
#define GA_EVAL 2
#define H_EVAL  3

static int objno, objrep = 1, qpcheck = 1, relax, time_flag, wantfuncs;
static double Times[4];

/*---- MUST BE IN ALPHABETICAL ORDER, BECAUSE THE KEYWORD SEARCH
 *---- ALGORITHM IS A RAPID BINARY SEARCH.
 */
struct amplspectype {
  int    alg;
  int    bar_directinterval;    
  int    bar_feasible;
  double bar_feasmodetol;  
  double bar_initmu;
  int    bar_initpt;
  int    bar_maxbacktrack;
  int    bar_maxcrossit;
  int    bar_maxrefactor;
  int    bar_murule;
  int    bar_penaltycons;        
  int    bar_penaltyrule;
  int    bar_switchrule;    
  char * blasoptionlib;
  int    blasoption;
  char * cplexlibname;
  int    debug;
  double delta;
  double feastol;
  double feastolabs;
  int    gradopt;
  int    hessopt;
  int    honorbnds;
  double infeastol;
  int    linsolver;
  int    lmsize;
  int    lpsolver;
  double ma_maxtimecpu;
  double ma_maxtimereal;
  int    ma_outputsub;    
  int    ma_terminate;    
  int    maxcgit;
  int    maxit;
  double maxtimecpu;
  double maxtimereal;
  int    mip_branchrule;
  int    mip_nDebug;
  int    mip_gubBranch;
  int    mip_heuristic;
  int    mip_heuristic_maxit;    
  int    mip_implications;
  double mip_integerTol;
  double mip_integralGapAbs;
  double mip_integralGapRel;
  int    mip_knapsack;
  int    mip_lpalg;
  int    mip_maxnodes;    
  int    mip_maxsolves;    
  double mip_maxtimecpu;
  double mip_maxtimereal;
  int    mip_method;
  int    mip_outinterval;
  int    mip_outlevel;
  int    mip_outputsub;
  int    mip_pseudoinit;
  int    mip_rootalg;
  int    mip_rounding;
  int    mip_selectrule;
  int    mip_strong_candlim;
  int    mip_strong_level;    
  int    mip_strong_maxit;
  int    mip_terminate;
  double ms_maxbndrange;
  int    ms_maxsolves;
  double ms_maxtimecpu;
  double ms_maxtimereal;
  int    ms_outputsub;        
  int    ms_numToSave;
  double ms_saveTol;
  int    ms_seed;
  double ms_startptrange;
  int    ms_terminate;
  int    multistart;
  int    newpoint;
  double objrange;
  double opttol;
  double opttolabs;
  int    outappend;
  char * outdir;
  int    outlev;
  int    outmode;
  int    par_numthreads;
  double pivot;
  int    presolve;
  int    presolve_dbg;
  double presolve_tol;  
  int    scale;
  int    soc;
  double xtol;
};

static struct amplspectype amplspecs = {
KTR_ALG_AUTO,          /* alg             : alg option (0=auto, 1=direct, 2=cg, 3=active, 5=multi) */
10,                    /* bar_directinterval: frequency for trying to force direct steps */
KTR_BAR_FEASIBLE_NO,   /* bar_feasible    : emphasize feasibilty */
1.0e-4,                /* bar_feasmodetol : tolerance for entering stay feasible mode */
1.0e-1,                /* bar_initmu      : initial barrier parameter value */
KTR_BAR_INITPT_AUTO,   /* bar_initpt      : barrier alg initial point strategy */
3,                     /* bar_maxbacktrack: maximum number of backtracks during linesearch */
0,                     /* bar_maxcrossit  : maximum number of allowable crossover iters */
0,                     /* bar_maxrefactor : maximum number of KKT refactorizations allowed */
KTR_BAR_MURULE_AUTO,   /* bar_murule   : rule for updating barrier parameter */
KTR_BAR_PENCONS_AUTO,  /* bar_penaltycons : whether to penalize constraints */
KTR_BAR_PENRULE_AUTO,  /* bar_penaltyrule : rule for updating penalty parameter */
KTR_BAR_SWITCHRULE_AUTO,  /* bar_switchrule : rule for barrier switching */
"none",                /* blasoptionlib   : dynamic library name for BLAS */
#if defined (__sun)
KTR_BLASOPTION_KNITRO, /* blasoption      : which BLAS to use */
#else
KTR_BLASOPTION_INTEL,  /* blasoption      : which BLAS to use */
#endif
"none",                /* cplexlibname    : path to CPLEX */
KTR_DEBUG_NONE,        /* debug           : debugging level */
1.0e0,                 /* delta           : initial trust region radius */
1.0e-6,                /* feastol         : feasibility relative stopping tolerance */
0.0e0,                 /* feastolabs      : feasibility absolute stopping tolerance */
KTR_GRADOPT_EXACT,     /* gradopt         : gradient computation */
KTR_HESSOPT_EXACT,     /* hessopt         : Hessian (Hessian-vector) computation */
KTR_HONORBNDS_INITPT,  /* honorbnds       : enforce satisfaction of bounds */
1.0e-8,                /* infeastol       : infeasibility relative stopping tolerance */
KTR_LINSOLVER_AUTO,    /* linsolver       : which linear solver to use */ 
10,                    /* lmsize          : number of limited-memory pairs stored for LBFGS */
KTR_LP_INTERNAL,       /* lpsolver        : LP solver used by Active Set algorithm */
100000000.0,           /* ma_maxtimecpu   : maximum CPU time when 'alg=multi' */
100000000.0,           /* ma_maxtimereal  : maximum real time when 'alg=multi' */
0,                     /* ma_outputsub    : subproblem output enabled when 'alg=multi' */
KTR_MA_TERMINATE_OPTIMAL, /* ma_terminate : when to terminate when 'alg=multi' */
0,                     /* maxcgit         : maximum number of allowable CG iterations */
0,                     /* maxit           : maximum number of allowable iterations */
100000000.0,           /* maxtimecpu      : maximum CPU time per start point */
100000000.0,           /* maxtimereal     : maximum real time per start point */
KTR_MIP_BRANCH_AUTO,   /* mip_branchrule  : MIP branching rule */
0,                     /* mip_nDebug      : MIP debugging level */
0,                     /* mip_gubBranch   : branch on GUBs */
KTR_MIP_HEURISTIC_AUTO, /* mip_heuristic    : MIP heuristic approach */
100,                   /* mip_heuristic_maxit : MIP heuristic iter limit */
1,                     /* mip_implications : use logical implications */
1.0e-8,                /* mip_integerTol  : threshold for integrality */
1.0e-6,                /* mip_integralGap : stop test */
1.0e-6,                /* mip_integralRel : stop test */
1,                     /* mip_knapsack    : add knapsack cuts */
KTR_ALG_AUTO,          /* mip_lpalg       : algorithm for LP subproblems */
100000,                /* mip_maxnodes    : maximum nodes explored */
200000,                /* mip_maxsolves   : maximum subproblem solves */
100000000.0,           /* mip_maxtimecpu  : maximum CPU time for MIP */
100000000.0,           /* mip_maxtimereal : maximum real time for MIP */
0,                     /* mip_method      : MIP method */
10,                    /* mip_outinterval : MIP output interval */
1,                     /* mip_outlevel    : MIP output level */
0,                     /* mip_outputsub   : MIP subproblem output enabled */
KTR_MIP_PSEUDOINIT_AUTO, /* mip_pseudoinit : pseudo-cost initialization */
KTR_ALG_AUTO,          /* mip_rootalg     : root node relaxation algorithm */
KTR_MIP_ROUND_AUTO,    /* mip_rounding    : MIP rounding rule */
KTR_MIP_SEL_AUTO,      /* mip_selectrule  : MIP node selection rule */
10,                    /* mip_strong_candlim : strong branching candidate limit */
10,                    /* mip_strong_level : strong branching tree level limit */
1000,                  /* mip_strong_maxit : strong branching iter limit */
KTR_MIP_TERMINATE_OPTIMAL, /* mip_terminate : when to terminate MIP solve */
1000.0,                /* ms_maxbndrange  : maximum var range for multistart */
0,                     /* ms_maxsolves    : maximum KNITRO solves for multistart */
100000000.0,           /* ms_maxtimecpu   : maximum CPU time for multistart */
100000000.0,           /* ms_maxtimereal  : maximum real time for multistart */
0,                     /* ms_numToSave    : feasible points to save */
0,                     /* ms_outputsub    : parallel multistart subproblem output enabled */
1.0e-6,                /* ms_saveTol      : tol for feasible point equality */
0,                     /* ms_seed         : multistart seed for random generator*/
KTR_INFBOUND,          /* ms_startptrange : maximum var range for multistart */
KTR_MSTERMINATE_MAXSOLVES, /* ms_terminate : when to terminate multistart */
KTR_MULTISTART_NO,     /* multistart      : enable multistart */
KTR_NEWPOINT_NONE,     /* newpoint        : newpoint feature */
1.0e20,                /* objrange        : objective range */
1.0e-6,                /* opttol          : optimality relative stopping tolerance */
0.0e0,                 /* opttolabs       : optimality absolute stopping tolerance */
KTR_OUTAPPEND_NO,      /* outappen        : append to output files */
"none",                /* outdir          : directory for output files */
KTR_OUTLEV_ITER_10,    /* outlev          : printing level: 0-6 (0=off, 6=verbose) */
KTR_OUTMODE_SCREEN,    /* outmode         : 0=screen, 1=file, 2=both */
1,                     /* par_numthreads  : number of parallel threads */
1.0e-8,                /* pivot           : initial pivot threshold */
KTR_PRESOLVE_BASIC,    /* presolve        : level for KNITRO presolver */
0,                     /* presolve_dbg    : debugging level for KNITRO presolver */
1.0e-6,                /* presolve_tol    : tolerance for KNITRO presolver */
KTR_SCALE_ALLOW,       /* scale           : automatic scaling option */
KTR_SOC_MAYBE,         /* soc             : second order correction option */
1.0e-15                /* xtol            : stepsize stopping tolerance */
};

static char timing_desc[] = "Whether to report problem I/O and solve times:\n"
			"\t\t\t0 (default) = no\n"
			"\t\t\t1 = yes, on stdout"
#ifndef _WIN32
			"\n\t\t\t2 = yes, on stderr\n"
			"\t\t\t3 = yes, on both stdout and stderr"
#endif
			;
static char objrep_desc[] = "Whether to replace\n\t\t\t\tminimize obj: v;\n\t\t\twith\n\t\t\t\t"
				"minimize obj: f(x)\n\t\t\twhen variable v appears linearly\n\t"
				"\t\tin exactly one constraint of the form\n\t\t\t\t"
				"s.t. c: v >= f(x);\n\t\t\tor\n\t\t\t\ts.t. c: v == f(x);\n"
				"\t\t\tPossible objrep values:\n\t\t\t0 = no\n"
				"\t\t\t1 = yes for v >= f(x) (default)\n"
				"\t\t\t2 = yes for v == f(x)\n"
				"\t\t\t3 = yes in both cases";

/*---- NOTE THE AMPL OPTIONS PARSER ALLOWS STRING-VALUED OPTIONS WITH WHITE SPACES,
 *---- BUT THE VALUES ASSIGNED MUST BE QUOTED.  E.g.
 *----   knitro foo blasoptionlib='"some option with spaces"'
 *---- UNDER BASH, OR
 *----   option knitro_options 'blasoptionlib="some option with spaces"';
 *---- IN AN AMPL SESSION WOULD WORK. */ 
static keyword keywds[] = {      /* must be in alphabetical order */
 KW("alg",             I_val, &amplspecs.alg, "Algorithm (0=auto, 1=direct, 2=cg, 3=active, 5=multi)"), 
 KW("algorithm",       I_val, &amplspecs.alg, "Algorithm (0=auto, 1=direct, 2=cg, 3=active, 5=multi)"), 
 KW("bar_directinterval",I_val, &amplspecs.bar_directinterval, "Frequency for trying to force direct steps"),
 KW("bar_feasible",    I_val, &amplspecs.bar_feasible, "Emphasize feasibility"),
 KW("bar_feasmodetol", D_val, &amplspecs.bar_feasmodetol, "Tolerance for entering stay feasible mode"),
 KW("bar_initmu",      D_val, &amplspecs.bar_initmu, "Initial value for barrier parameter"),
 KW("bar_initpt",      I_val, &amplspecs.bar_initpt, "Barrier initial point strategy"),
 KW("bar_maxbacktrack",I_val, &amplspecs.bar_maxbacktrack, "Maximum number of linesearch backtracks"),
 KW("bar_maxcrossit",  I_val, &amplspecs.bar_maxcrossit, "Maximum number of crossover iterations"),
 KW("bar_maxrefactor", I_val, &amplspecs.bar_maxrefactor,  "Maximum number of KKT refactorizations allowed"),
 KW("bar_murule",      I_val, &amplspecs.bar_murule, "Rule for updating the barrier parameter"),
 KW("bar_penaltycons", I_val, &amplspecs.bar_penaltycons, "Apply penalty method to constraints"),
 KW("bar_penaltyrule", I_val, &amplspecs.bar_penaltyrule, "Rule for updating the penalty parameter"),
 KW("bar_switchrule",  I_val, &amplspecs.bar_switchrule, "Rule for barrier switching alg"), 
 KW("blasoption",      I_val, &amplspecs.blasoption,  "Which BLAS/LAPACK library to use"),
 KW("blasoptionlib",   C_val, &amplspecs.blasoptionlib,  "Name of dynamic BLAS/LAPACK library"),
 KW("cplexlibname",    C_val, &amplspecs.cplexlibname,  "Name of dynamic CPLEX library"),
 KW("debug",           I_val, &amplspecs.debug,    "Debugging level (0=none, 1=problem, 2=execution)"),
 KW("delta",           D_val, &amplspecs.delta,    "Initial trust region radius"),
 /* KW("direct",          I_val, &amplspecs.alg,      "Algorithm (0=auto, 1=direct, 2=cg, 3=active)"), */
 KW("feasible",        I_val, &amplspecs.bar_feasible, "Enable feasible version (0=F, 1=T)"),
 KW("feasmodetol",     D_val, &amplspecs.bar_feasmodetol, "Tolerance for entering feasible mode"),
 KW("feastol",         D_val, &amplspecs.feastol,  "Feasibility stopping tolerance"),
 KW("feastol_abs",     D_val, &amplspecs.feastolabs, "Absolute feasibility tolerance"),
 KW("feastolabs",      D_val, &amplspecs.feastolabs, "Absolute feasibility tolerance"),
 KW("gradopt",         I_val, &amplspecs.gradopt,  "Gradient computation method"),
 KW("hessopt",         I_val, &amplspecs.hessopt,  "Hessian computation method"),
 KW("honorbnds",       I_val, &amplspecs.honorbnds,"Enforce satisfaction of the bounds"),
 KW("infeastol",       D_val, &amplspecs.infeastol,  "Infeasibility stopping tolerance"),
 /* KW("iprint",          I_val, &amplspecs.outlev,   "Control printing level"),*/
 KW("linsolver",       I_val, &amplspecs.linsolver, "Which linear solver to use"),
 KW("lmsize",          I_val, &amplspecs.lmsize,   "Number of limited-memory pairs stored for LBFGS"),
 KW("lpsolver",        I_val, &amplspecs.lpsolver, "LP solver used by Active Set algorithm"),
 KW("ma_maxtime_cpu",  D_val, &amplspecs.ma_maxtimecpu, "Maximum CPU time when 'alg=multi', in seconds"),
 KW("ma_maxtime_real", D_val, &amplspecs.ma_maxtimereal, "Maximum real time when 'alg=multi', in seconds"),
 KW("ma_outsub",       I_val, &amplspecs.ma_outputsub, "Enable subproblem output when 'alg=multi'"),
 KW("ma_terminate",    I_val, &amplspecs.ma_terminate, "Termination condition when option 'alg=multi'"),
 KW("maxcgit",         I_val, &amplspecs.maxcgit,  "Maximum number of conjugate gradient iterations"),
 KW("maxcrossit",      I_val, &amplspecs.bar_maxcrossit, "deprecated, use 'bar_maxcrossit'"),
 KW("maxit",           I_val, &amplspecs.maxit,    "Maximum number of iterations"),
 KW("maxtime_cpu",     D_val, &amplspecs.maxtimecpu, "Maximum CPU time in seconds, per start point"),
 KW("maxtime_real",    D_val, &amplspecs.maxtimereal, "Maximum real time in seconds, per start point"),
 KW("mip_branchrule", I_val, &amplspecs.mip_branchrule, "MIP branching rule"),
 KW("mip_debug", I_val, &amplspecs.mip_nDebug, "MIP debugging level (0=none, 1=all)"),
 KW("mip_gub_branch", I_val, &amplspecs.mip_gubBranch, "Branch on GUBs (0=no, 1=yes)"),
 KW("mip_heuristic",    I_val, &amplspecs.mip_heuristic, "MIP heuristic search"),
 KW("mip_heuristic_maxit", I_val, &amplspecs.mip_heuristic_maxit, "MIP heuristic iteration limit"),
 KW("mip_implications", I_val, &amplspecs.mip_implications, "Add logical implications (0=no, 1=yes)"),
 KW("mip_integer_tol",  D_val, &amplspecs.mip_integerTol, "Threshold for deciding integrality"),
 KW("mip_integral_gap_abs", D_val, &amplspecs.mip_integralGapAbs, "Absolute integrality gap stop tolerance"),
 KW("mip_integral_gap_rel", D_val, &amplspecs.mip_integralGapRel, "Relative integrality gap stop tolerance"),
 KW("mip_knapsack",     I_val, &amplspecs.mip_knapsack, "Add knapsack cuts (0=no, 1=ineqs, 2=ineqs+eqs)"),
 KW("mip_lpalg",        I_val, &amplspecs.mip_lpalg, "LP subproblem algorithm"),
 KW("mip_maxnodes",     I_val, &amplspecs.mip_maxnodes, "Maximum nodes explored"),
 KW("mip_maxsolves",    I_val, &amplspecs.mip_maxsolves, "Maximum subproblem solves"),
 KW("mip_maxtime_cpu",  D_val, &amplspecs.mip_maxtimecpu, "Maximum CPU time in seconds for MIP"),
 KW("mip_maxtime_real", D_val, &amplspecs.mip_maxtimereal, "Maximum real in seconds time for MIP"),
 KW("mip_method", I_val, &amplspecs.mip_method, "MIP method (0=auto, 1=BB, 2=HQG)"),
 KW("mip_outinterval", I_val, &amplspecs.mip_outinterval, "MIP output interval"),
 KW("mip_outlevel", I_val, &amplspecs.mip_outlevel, "MIP output level"),
 KW("mip_outsub", I_val, &amplspecs.mip_outputsub, "Enable MIP subproblem output"),
 KW("mip_pseudoinit",  I_val, &amplspecs.mip_pseudoinit, "Pseudo-cost initialization"),
 KW("mip_rootalg",     I_val, &amplspecs.mip_rootalg, "Root node relaxation algorithm"),
 KW("mip_rounding",    I_val, &amplspecs.mip_rounding, "MIP rounding rule"),
 KW("mip_selectrule",  I_val, &amplspecs.mip_selectrule, "MIP node selection rule"),
 KW("mip_strong_candlim", I_val, &amplspecs.mip_strong_candlim, "Strong branching candidate limit"),
 KW("mip_strong_level", I_val, &amplspecs.mip_strong_level, "Strong branching tree level limit"),
 KW("mip_strong_maxit",   I_val, &amplspecs.mip_strong_maxit, "Strong branching iteration limit"),
 KW("mip_terminate",   I_val, &amplspecs.mip_terminate, "Termination condition for MIP"),
 KW("ms_enable",       I_val, &amplspecs.multistart, "Enable multistart"),
 KW("ms_maxbndrange",  D_val, &amplspecs.ms_maxbndrange, "Maximum unbounded variable range for multistart"),
 KW("ms_maxsolves",    I_val, &amplspecs.ms_maxsolves, "Maximum KNITRO solves for multistart"),
 KW("ms_maxtime_cpu",  D_val, &amplspecs.ms_maxtimecpu, "Maximum CPU time for multistart, in seconds"),
 KW("ms_maxtime_real", D_val, &amplspecs.ms_maxtimereal, "Maximum real time for multistart, in seconds"),
 KW("ms_num_to_save",  I_val, &amplspecs.ms_numToSave, "Feasible points to save from multistart"),
 KW("ms_outsub",       I_val, &amplspecs.ms_outputsub, "Enable subproblem output for parallel multistart"),
 KW("ms_savetol",      D_val, &amplspecs.ms_saveTol, "Tol for feasible points being equal"),
 KW("ms_seed",         I_val, &amplspecs.ms_seed, "Seed for multistart random generator"),
 KW("ms_startptrange", D_val, &amplspecs.ms_startptrange, "Maximum variable range for multistart"),
 KW("ms_terminate",    I_val, &amplspecs.ms_terminate, "Termination condition for multistart"),
 KW("multistart",      I_val, &amplspecs.multistart, "Enable multistart"),
 KW("newpoint",        I_val, &amplspecs.newpoint, "Use newpoint feature"), 
 KW("objno",           I_val, &objno, "objective number: 0 = none, 1 = first (default),\n\t\t"
					                  "  2 = second (if _nobjs > 1), etc."),
 KW("objrange",        D_val, &amplspecs.objrange, "Objective range"),
 KW("objrep",		   I_val, &objrep, objrep_desc),
 KW("opttol",          D_val, &amplspecs.opttol,   "Optimality stopping tolerance"),
 KW("opttol_abs",      D_val, &amplspecs.opttolabs, "Absolute optimality tolerance"),
 KW("opttolabs",       D_val, &amplspecs.opttolabs, "Absolute optimality tolerance"),
 KW("outappend",       I_val, &amplspecs.outappend, "Append to output files (0=no, 1=yes)"),
 KW("outdir",          C_val, &amplspecs.outdir,   "Directory for output files"),
 KW("outlev",          I_val, &amplspecs.outlev,   "Control printing level"),
 KW("outmode",         I_val, &amplspecs.outmode,  "Where to direct output (0=screen, 1=file, 2=both)"),
 KW("par_numthreads",  I_val, &amplspecs.par_numthreads,  "Number of parallel threads"),
 KW("pivot",           D_val, &amplspecs.pivot,    "Initial pivot tolerance"),
 KW("presolve",        I_val, &amplspecs.presolve, "KNITRO presolver level"),
 KW("presolve_dbg",    I_val, &amplspecs.presolve_dbg, "KNITRO presolver debugging level"),
 KW("presolve_tol",    D_val, &amplspecs.presolve_tol, "KNITRO presolver tolerance"),
 KW("relax",		   I_val, &relax, "whether to ignore integrality: 0 (default) = no, 1 = yes"),
 KW("scale",           I_val, &amplspecs.scale,    "Automatic scaling option"),
 KW("soc",             I_val, &amplspecs.soc,      "Second order correction options"),
 KW("timing",		I_val, &time_flag, timing_desc),
 KW("version",	       Ver_val, 0,                 "Report software version"),
 KW("wantsol",	       WS_val, 0, WS_desc_ASL+5),
 KW("xtol",            D_val, &amplspecs.xtol,     "Stepsize stopping tolerance")
};

/*---- THE BANNER AND VERSION STRING WILL BE FILLED IN LATER. */
/*---- David Gay writes the following: */
/*----   The following xxxvers cannot be computed later, but must */
/*----   be specified at compile time to permit "grep Version knitro" or "grep VERS knitro" */
/*----   to reveal the version of the "knitro" binary without executing it. */
/*----   With the brain-dead Linux version, "grep --binary-file=text VERS" */
/*----   Note that knitro_options appears in xxxvers so xxxvers won't be optimized away. */
#define RELEASE_DATE 20111111
static char xxxvers[] = "knitro_options\0\n"
                        "AMPL/KNITRO 8.0.0 Driver VERS Version 20111111\n";
static char g_szBanner[20 + 1];
static char g_szVersionString[70 + 1];
static Option_Info Oinfo = { "knitro",                /*-- SOLVER NAME */
                             g_szBanner,              /*-- BANNER */
                             xxxvers,                 /*-- SOLVER OPTIONS NAME */
                             keywds,                  /*-- OPTIONS KEYWORDS */
                             nkeywds,                 /*-- LENGTH OF keywds */
                             ASL_OI_want_funcadd,     /*-- YES funcadd -u */
                             g_szVersionString,       /*-- FOR knitroampl -v */
                             0,                       /*-- NO USAGE MESSAGE */
                             0,                       /*-- NO Solver_KW_func */
                             0,                       /*-- NO Fileeq_func */
                             0,                       /*-- COMMAND LINE */
                             0,
                             RELEASE_DATE };          /*-- DATE FOR THIS FILE */


/*------------------------------------------------------------------*/
/*     FUNCTION isaqp                                               */
/*------------------------------------------------------------------*/
/** Routine used by Knitro to detect QPs given to AMPL.
 *  TBD: This doesn't seem to work currently when the problem is
 *  transformed into a QP by the AMPL "objrep" procedure.
 */
static int isaqp(char *s)
{
    ASL *asl, *oasl;
    FILE *nl;
    fint *colqp, *rowqp;
    real *delsqp;
    int rcode;

    oasl = cur_ASL;
    if (oasl->i.nlc_)
	return 0;
    rcode=0;
    asl = ASL_alloc(ASL_read_fg);
    nl = jac0dim(s, (int)strlen(s));
    qp_read(nl,0);
    if (nqpcheck(0, &rowqp, &colqp, &delsqp) > 0) {
        rcode = 1;
    }
    ASL_free(&asl);
    cur_ASL = oasl;

    return rcode;
}


/*------------------------------------------------------------------*/
/*     FUNCTION allocateArrays                                      */
/*------------------------------------------------------------------*/
/** Use AMPL's "M1alloc" and don't worry about freeing the memory.
 *  According to David Gay, "ASL_free" releases the memory.
 */
static void  allocateArrays (const int              n,
                             const int              m,
                             const int              nnzJ,
                             const int              nnzH,
                             const int              isMPEC,
                             const int              nNumThreads,                             
                                   ASL    *  const  asl,
                                   double ** const  x,
                                   double ** const  objGrad,
                                   double ** const  c,
                                   double ** const  jac,
                                   int    ** const  jacIndexVars,
                                   int    ** const  jacIndexCons,
                                   double ** const  lambda,
                                   int    ** const  xType,
                                   int    ** const  cType,
                                   int    ** const  cFnType,
                                   double ** const  hess,
                                   int    ** const  hessIndexRows,
                                   int    ** const  hessIndexCols,
                                   double ** const  hessvec,
                                   double ** const  hessVector,
                                   double ** const  g,
                                   double ** const  c_ampl,
                                   int    ** const  lasteval)
{
    /*---- THESE ARRAYS GET PASSED TO KTR_solve. */
    *x            = (double *)M1alloc(n*sizeof(double));
    *objGrad      = (double *)M1alloc(n*sizeof(double));
    *c            = (double *)M1alloc(m*sizeof(double));
    *jac          = (double *)M1alloc(nnzJ*sizeof(double));
    *jacIndexVars = (int *)M1alloc(nnzJ*sizeof(int));
    *jacIndexCons = (int *)M1alloc(nnzJ*sizeof(int));
    *lambda       = (double *)M1alloc((m+n)*sizeof(double));
    *xType        = (int *)M1alloc(n*sizeof(int));
    *cType        = (int *)M1alloc(m*sizeof(int));
    *cFnType      = (int *)M1alloc(m*sizeof(int));

    /*---- ARRAYS ONLY NEEDED FOR EXACT HESSIAN. */
    if (amplspecs.hessopt == KTR_HESSOPT_EXACT) {
      *hess          = (double *)M1alloc(nnzH*sizeof(double));
      *hessIndexRows = (int *)M1alloc(nnzH*sizeof(int));
      *hessIndexCols = (int *)M1alloc(nnzH*sizeof(int));
    }

    /*---- ARRAYS ONLY NEEDED FOR EXACT HESSIAN-VECTOR PRODUCTS. */
    if (amplspecs.hessopt == KTR_HESSOPT_PRODUCT) {
      *hessvec     = (double *)M1alloc(n*sizeof(double));
      *hessVector  = (double *)M1alloc(n*sizeof(double));
    }

    /*---- THESE ARRAYS ARE NOT PASSED TO KTR_solve. */
    *g        = (double *)M1alloc(n_var*sizeof(double));
    *c_ampl   = (double *)M1alloc(n_con*sizeof(double));

    /*---- ARRAYS FOR CALLBACKS (WHICH MAY BE DONE IN PARALLEL) */
    *lasteval = (int *)M1alloc(nNumThreads*sizeof(int));
    
    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION getVariableTypes                                    */
/*------------------------------------------------------------------*/
/** Populate the "xType" array with variable type CONTINUOUS or INTEGER.
 *  Later on determine if any INTEGER variables are in fact BINARY.
 */
static void  getVariableTypes (const ASL * const  asl,
                                     int * const  naXType,
                                     int * const  pbProblemHasIntegerVars)
{
    int  i, k;
    int  nMax;


    /*---- AMPL ORDERS THE VARIABLES BY TYPE.  FROM THE "HOOKING" MANUAL:
     *----   1 - NONLINEAR, CONTINUOUS, IN OBJ AND CONS  (nlvb - nlvbi)
     *----   2 - NONLINEAR, INTEGER,    IN OBJ AND CONS  (nlvbi)
     *----   3 - NONLINEAR, CONTINUOUS, IN CON ONLY      (nlvc - (nlvb + nlvci))
     *----   4 - NONLINEAR, INTEGER,    IN CON ONLY      (nlvci)
     *----   5 - NONLINEAR, CONTINUOUS, IN OBJ ONLY      (nlvo - (nlvc + nlvoi))
     *----   6 - NONLINEAR, INTEGER,    IN OBJ ONLY      (nlvoi)
     *----   7 - LINEAR ARCS (NOT SUPPORTED WITH KNITRO) (nwv)
     *----   8 - LINEAR, CONTINUOUS, ANYWHERE
     *----                           (n_var - (max{nlvc,nlvo} + niv + nbv + nwv)
     *----   9 - LINEAR, BINARY, ANYWHERE                (nbv)
     *----  10 - LINEAR, INTEGER, ANYWHERE               (niv)
     */


    *pbProblemHasIntegerVars = FALSE;

    /*---- VARIABLES APPEARING NONLINEARLY IN THE OBJ AND CONSTRAINTS. */
    k = 0;
    for (i = 0; i < nlvb - nlvbi; i++)
        naXType[k++] = KTR_VARTYPE_CONTINUOUS;
    for (i = 0; i < nlvbi; i++)
    {
        naXType[k++] = KTR_VARTYPE_INTEGER;
        *pbProblemHasIntegerVars = TRUE;
    }

    /*---- VARIABLES APPEARING NONLINEARLY IN CONSTRAINTS ONLY. */
    for (i = 0; i < nlvc - (nlvb + nlvci); i++)
        naXType[k++] = KTR_VARTYPE_CONTINUOUS;
    for (i = 0; i < nlvci; i++)
    {
        naXType[k++] = KTR_VARTYPE_INTEGER;
        *pbProblemHasIntegerVars = TRUE;
    }

    /*---- VARIABLES APPEARING NONLINEARLY IN OBJ ONLY. */
    for (i = 0; i < nlvo - (nlvc + nlvoi); i++)
        naXType[k++] = KTR_VARTYPE_CONTINUOUS;
    for (i = 0; i < nlvoi; i++)
    {
        naXType[k++] = KTR_VARTYPE_INTEGER;
        *pbProblemHasIntegerVars = TRUE;
    }

    /*---- VARIABLES APPEARING LINEARLY IN THE OBJ OR CONSTRAINTS. */
    if (nlvc >= nlvo)
        nMax = nlvc;
    else
        nMax = nlvo;
    for (i = 0; i < n_var - (nMax + niv + nbv); i++)
        naXType[k++] = KTR_VARTYPE_CONTINUOUS;
    for (i = 0; i < nbv; i++)
    {
        naXType[k++] = KTR_VARTYPE_INTEGER;
        *pbProblemHasIntegerVars = TRUE;
    }
    for (i = 0; i < niv; i++)
    {
        naXType[k++] = KTR_VARTYPE_INTEGER;
        *pbProblemHasIntegerVars = TRUE;
    }

    assert (k == n_var);
    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION printProblemDef                                     */
/*------------------------------------------------------------------*/
/** To make this execute in AMPL, type:
 *    ampl: option solver knitroampl;
 *    ampl: option knitroampl_auxfiles rc;
 *    ampl: solve;
 */
static void  printProblemDef (const ASL    * const  asl,
                              const int    * const  xType,
                              const double * const  xLoBnds,
                              const double * const  xUpBnds,
                              const int    * const  cType,
                              const double * const  cLoBnds,
                              const double * const  cUpBnds,
                              const int    * const  ind1,
                              const int    * const  ind2)
{
    int  i, j;


    if ((maxcolnamelen == 0) || (maxrownamelen == 0))
        {
        /*---- IN AMPL TYPE:
         *----   ampl: option solver knitroampl;
         *----   ampl: option ka_auxfiles rc;
         *----   ampl: solve;
         */
        printf ("No AMPL var and con names found -- continuing  <knitroampl.c>\n");
        return;
        }

    printf ("----- Problem seen by KNITRO -----\n");

    printf ("Objective name:  %s\n", obj_name(0));

    /*---- PRINT OUT VARIABLES AND THEIR BOUNDS. */
    for (i = 0; i < n_var; i++)
    {
        if (xType[i] == KTR_VARTYPE_CONTINUOUS)
            printf ("%15.6e  <=  x[%4d]  <=  %15.6e  %s\n",
                    xLoBnds[i], i, xUpBnds[i], var_name(i));
        else if (xType[i] == KTR_VARTYPE_BINARY)
            printf ("%15.6e  <=  x[%4d]  <=  %15.6e  %s  (binary)\n",
                    xLoBnds[i], i, xUpBnds[i], var_name(i));
        else
            printf ("%15.6e  <=  x[%4d]  <=  %15.6e  %s  (integer)\n",
                    xLoBnds[i], i, xUpBnds[i], var_name(i));
    }
    printf ("\n");

    /*---- PRINT OUT REGULAR CONSTRAINTS AND THEIR BOUNDS. */
    j = 0;
    for (i = 0; i < n_con; i++)
    {
        if (cType[i] == KTR_CONTYPE_LINEAR)
            printf ("%15.6e  <=  c[%4d]  <=  %15.6e  %s  (linear)\n",
                    cLoBnds[i], j, cUpBnds[i], con_name(i));
        else if (cType[i] == KTR_CONTYPE_QUADRATIC)
            printf ("%15.6e  <=  c[%4d]  <=  %15.6e  %s  (quadratic)\n",
                    cLoBnds[i], j, cUpBnds[i], con_name(i));
        else
            printf ("%15.6e  <=  c[%4d]  <=  %15.6e  %s  (general)\n",
                    cLoBnds[i], j, cUpBnds[i], con_name(i));
        j++;
    }
        
    /*---- PRINT OUT COMPLEMENTARITY CONSTRAINTS. */
    j = 0;
    for (i = 0; i < n_cc; i++)
    {
        printf("%s complements %s\n", var_name(ind1[i]), var_name(ind2[i]));
    }
    
    printf ("-----------------------------------\n");
    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION setSolutionMessage                                  */
/*------------------------------------------------------------------*/
static int  setSolutionMessage(const int info)
{
    int infonum;

    switch(info) {
	case KTR_RC_OPTIMAL:		    infonum = 0;	break;
	case KTR_RC_NEAR_OPT:		    infonum = 1;	break;
	case KTR_RC_FEAS_XTOL:		    infonum = 2;	break;
	case KTR_RC_FEAS_NO_IMPROVE:	infonum = 3;	break;
	case KTR_RC_INFEASIBLE:		    infonum = 4;	break;
	case KTR_RC_INFEAS_XTOL:	    infonum = 5;	break;
	case KTR_RC_INFEAS_NO_IMPROVE:	infonum = 6;	break;
	case KTR_RC_INFEAS_MULTISTART:	infonum = 7;	break;
    case KTR_RC_INFEAS_CON_BOUNDS:	infonum = 8;	break;
    case KTR_RC_INFEAS_VAR_BOUNDS:	infonum = 9;	break;
	case KTR_RC_UNBOUNDED:		    infonum = 10;	break;
	case KTR_RC_ITER_LIMIT:		    infonum = 11;	break;
	case KTR_RC_TIME_LIMIT:		    infonum = 12;	break;
	case KTR_RC_FEVAL_LIMIT:	    infonum = 13;	break;
	case KTR_RC_MIP_EXH:		    infonum = 14;	break;
	case KTR_RC_MIP_FEAS_TERM:	    infonum = 15;	break;
	case KTR_RC_MIP_SOLVE_LIMIT:	infonum = 16;	break;
	case KTR_RC_MIP_NODE_LIMIT:	    infonum = 17;	break;
	case KTR_RC_LP_SOLVER_ERR:	    infonum = 18;	break;
	case KTR_RC_EVAL_ERR:		    infonum = 19;	break;
	case KTR_RC_OUT_OF_MEMORY:	    infonum = 20;	break;
	case KTR_RC_USER_TERMINATION:	infonum = 21;	break;
	case KTR_RC_INTERNAL_ERROR:	    infonum = 23;	break;
	default:
		infonum = info >= -599 && info <= -505 ? 22 : 24;
	}    
    return infonum;
}


/*------------------------------------------------------------------*/
/*     EVALUATION FUNCTIONS FOR CALLBACKS                           */
/*------------------------------------------------------------------*/

/* Struct for the values passed to the callback evaluation routines */
typedef struct User_Params {
    ASL *asl;
    int nobj;
    int nvar;
    int ncon;
    int bProblemHasIntegerVars;
    int bConsAllLinear;
    int bHaveEvaluatedJac;
    int *lasteval;
    double *c_ampl;
    double *g;
    double *hessvec;
    cgrad *cg;
    int nNumThreads; 
} User_Params;


/* Evaluate obj and c. */
int evalFC(const double *a_x, double *c, double *obj,
           User_Params * user)
{
    int evalStatus;
    fint nerror;
	double *x = (double*) a_x;
    ASL *asl;
    int nThreadID;

    nThreadID = 0;
#if defined(_OPENMP)
    if (user->nNumThreads > 0) {
        /*---- USING MULTIPLE THREADS, THIS CALLBACK MAY COME FROM A
         *---- PARALLEL REGION, GET THREAD ID */
        nThreadID=omp_get_thread_num();   
    }
#endif    
    evalStatus = 0;
    nerror = 0;
    asl = user->asl;

    /*---- EVALUATE OBJECTIVE FUNCTION. */
    if (user->nobj >= 0) {
        *obj = objval(user->nobj, x, &nerror);
        user->lasteval[nThreadID] = FC_EVAL;
        if (nerror) {
            if (amplspecs.debug > 0)
                fprintf (Stderr, "\n--- ERROR evaluating objective %s.\n",
                         nerror == 1 ? "function" : "gradient");
            nerror = 0;
            evalStatus = KTR_RC_EVAL_ERR;
        }
    }
    else {
        *obj = 0.0;
    }

    /*---- EVALUATE CONSTRAINT FUNCTIONS. */
    conval(x, c, &nerror);
    if (nerror) {
        if (amplspecs.debug > 0)
            fprintf (Stderr, "\n--- ERROR evaluating constraint %s.\n",
                     nerror == 1 ? "functions" : "gradients");
        nerror = 0;
        evalStatus = KTR_RC_EVAL_ERR;
    }
    
    user->lasteval[nThreadID] = FC_EVAL;

	return evalStatus;
} /*evalFC*/

/* Evaluate objgrad and jac. */
int evalGA(const double *a_x, double *jac, double *objGrad,
           User_Params * user)
{
    int i;
	int evalStatus;
    fint nerror;
	double *x = (double*) a_x;
    ASL *asl;
    int nThreadID;

    nThreadID = 0;
#if defined(_OPENMP)
    if (user->nNumThreads > 0) {
        /*---- USING MULTIPLE THREADS, THIS CALLBACK MAY COME FROM A
         *---- PARALLEL REGION, GET THREAD ID */
        nThreadID=omp_get_thread_num();   
    }
#endif
    evalStatus = 0;
    nerror = 0;
    asl = user->asl;

    /*---- EVALUATE GRADIENT OF THE OBJECTIVE. */
    if (user->nobj >= 0) {
         objgrd(user->nobj, x, user->g, &nerror);
         user->lasteval[nThreadID] = GA_EVAL;
         if (nerror) {
             fprintf (Stderr, "\n--- ERROR evaluating objective gradient.\n");
             nerror = 0;
             evalStatus = KTR_RC_EVAL_ERR;
         }
         if (user->nNumThreads == 1)
             xknown(x);
     }
     else {
         for (i = 0; i < user->nvar; i++)
             user->g[i] = 0.0;
     }
     for (i = 0; i < user->nvar; i++){          /* Dense obj grad */
         objGrad[i] = user->g[i];
     }
     
     /*---- EVALUATE CONSTRAINT JACOBIAN. */
     /*---- TOYED AROUND WITH DOING THIS EVALUATION ONLY ONCE FOR LINEAR
      *---- CONSTRAINTS, BUT IT CAUSED PROBLEMS FOR CASES WHERE KNITRO
      *---- RECURSIVELY CALLS ITSELF (e,g, MIP, MS, MULTI-ALG).  FOR NOW
      *---- JUST ALWAYS DO THE EVALUATION (HENCE THE || 1 BELOW) UNTIL
      *---- THIS IS INVESTIGATED FURTHER. */
     if (user->bConsAllLinear == FALSE || user->bHaveEvaluatedJac == FALSE ||
         user->bProblemHasIntegerVars || 1)
     {
         jacval(x, jac, &nerror);              /* Sparse Jacobian */
         user->lasteval[nThreadID] = GA_EVAL;
         user->bHaveEvaluatedJac = TRUE;
         if (nerror) {
             fprintf (Stderr, "\n--- ERROR evaluating constraint gradients.\n");
             nerror = 0;
             evalStatus = KTR_RC_EVAL_ERR;
         }
     }
     xunknown();

     return evalStatus;
} /*evalGA*/

/* Evaluate hessian or hessian-vector product. */
int evalH(const int nnzH, const double *a_x, double *hessian,
          double *hessVector, const double *a_lambda, double *obj,
          double *c, const int status, User_Params * user)
{
    /**  If active-set algorithm, the Hessian may be evaluated at
      *  a point at which the functions were NOT most recently evaluated.
      *  This can occur after a trial step is rejected since the Hessian
      *  may be re-evaluated with different multipliers for the next 
      *  trial step.  This can also occur if using interior algorithms with
      *  crossover (i.e. bar_maxcrossit>0) since it may switch to active-set 
      *  at the end, or when using the interior-point switching algorithm.
      *
      *  AMPL Hessian evaluation routine evaluates the Hessian at the
      *  point where the objective/constraints were MOST RECENTLY
      *  evaluated.  If we want to evaluate the Hessian at a point
      *  which is different from the most recent function evaluation,
      *  then we need to evaluate functions at this point before 
      *  evaluating the Hessian. We do this by checking whether the
      *  most recent evaluation was a gradient/Jacobian evaluation?
      *  If the most recent evaluation was a gradient/Jacobian we know
      *  that this Hessian evaluation follows an accepted trial step
      *  and is occurring at a point where the functions were most
      *  recently evaluated.
      */
    int i;
    int evalStatus;
    fint nerror;
	double *x = (double *) a_x;
	double *lambda = (double *) a_lambda;    
    double dTmpObj;
    double OW[1];
    ASL *asl;
    int bEvalObjConFirst;
    int nThreadID;

    nThreadID = 0;
#if defined(_OPENMP)
    if (user->nNumThreads > 0) {
        /*---- USING MULTIPLE THREADS, THIS CALLBACK MAY COME FROM A
         *---- PARALLEL REGION, GET THREAD ID */
        nThreadID=omp_get_thread_num();   
    }
#endif
    evalStatus = 0;
    nerror = 0;
    asl = user->asl;
    bEvalObjConFirst = FALSE;
    
    /*---- CHECK TO SEE WHETHER WE NEED TO EVALUATE THE
     *---- OBJECTIVE/CONSTRAINTS AT NEW POINT FIRST */
    if (   (status == KTR_RC_EVALH || status == KTR_RC_EVALH_NO_F)
        && (user->lasteval[nThreadID] != GA_EVAL) ) {
        bEvalObjConFirst = TRUE;
    } else if (   (status == KTR_RC_EVALHV || status == KTR_RC_EVALHV_NO_F)
               && (user->lasteval[nThreadID] != GA_EVAL)
               && (user->lasteval[nThreadID] != H_EVAL) ) {
        bEvalObjConFirst = TRUE;
    }
#if 1
    /*---- WHEN MULTIPLE THREADS AND ONLY ONE ASL STRUCTURE,
     *---- WE NEED TO BE MORE CAREFUL WITH HESSIAN EVALUATIONS.
     *---- (UNFORTUNATELY THIS COST US SOME EFFICIENCY). */
    if (user->nNumThreads > 1) /*--- AND CONCURRENT EVALS==TRUE */
        bEvalObjConFirst = TRUE;
#endif    
    if (bEvalObjConFirst) {
        if (user->nobj >= 0) {
            dTmpObj = objval(user->nobj, x, &nerror);
            user->lasteval[nThreadID] = FC_EVAL;
            if (user->nNumThreads == 1)
                xknown(x);
        }
        else {
            dTmpObj = 0.0;
        }
        conval(x, user->c_ampl, &nerror);
        user->lasteval[nThreadID] = FC_EVAL;
    }

    /*---- NOW EVALUATE THE HESSIAN OR HESSIAN-VECTOR PRODUCT
     *---- (WITH OR WOTHOUT THE OBJECTIVFE COMPONENT). */
    if (status == KTR_RC_EVALH) {
        /*---- COMPUTE NORMAL HESSIAN */
        OW[0] = 1.0; /*-- Knitro only accepts 1 objective (nobj=0) */
        sphes(hessian, -1, OW, lambda);
    } else if (status == KTR_RC_EVALH_NO_F) {
        /*---- COMPUTE HESSIAN EXCLUDING OBJECTIVE TERM */
        OW[0] = 0.0; /*-- Knitro only accepts 1 objective (nobj=0) */
        sphes(hessian, -1, OW, lambda);
    } else if (status == KTR_RC_EVALHV) {
        /*---- COMPUTE NORMAL HESSIAN-VEC PRODUCT */
        OW[0] = 1.0; /*-- Knitro only accepts 1 objective (nobj=0) */
        hvcomp (user->hessvec, hessVector, -1, OW, lambda);
        for (i=0; i<user->nvar; i++) {
            hessVector[i] = user->hessvec[i];
        }
    } else if (status == KTR_RC_EVALHV_NO_F) {
        /*---- COMPUTE HESSIAN-VEC PRODUCT EXCLUDING OBJECTIVE TERM */
        OW[0] = 0.0; /*-- Knitro only accepts 1 objective (nobj=0) */
        hvcomp (user->hessvec, hessVector, -1, OW, lambda);
        for (i=0; i<user->nvar; i++) {
            hessVector[i] = user->hessvec[i];
        }
    }
    user->lasteval[nThreadID] = H_EVAL;
    xunknown();
    
    return evalStatus;
	
} /*evalH*/


/*------------------------------------------------------------------*/
/*     CALLBACK FUNCTIONS                                           */
/*------------------------------------------------------------------*/

/* Callback function for evaluating obj and c. */
int callbackEvalFC(
		const int evalRequestCode,
        const int             n,
        const int             m,
        const int             nnzJ,
        const int             nnzH,
        const double * const  x,
        const double * const  lambda,
              double * const  obj,
              double * const  c,
              double * const  objGrad,
              double * const  jac,
              double * const  hessian,
              double * const  hessVector,
              void *  userParams) 
{
    int nRC;
    User_Params * user;

    user = userParams;
    
	if(evalRequestCode != KTR_RC_EVALFC){
		fprintf(Stderr, "ERROR: CallbackEvalFC incorrectly called with eval code %d\n", evalRequestCode);
		return (KTR_RC_CALLBACK_ERR);
	}
	
	nRC = evalFC(x, c, obj, user);
	return nRC;
} /*callbackEvalFC*/

/* Callback function for evaluating objgrad and jac. */
int callbackEvalGA(
		const int evalRequestCode,
        const int             n,
        const int             m,
        const int             nnzJ,
        const int             nnzH,
        const double * const  x,
        const double * const  lambda,
              double * const  obj,
              double * const  c,
              double * const  objGrad,
              double * const  jac,
              double * const  hessian,
              double * const  hessVector,
              void *  userParams) 
{
    int nRC;
    User_Params * user;
    
    user = userParams;
	if(evalRequestCode != KTR_RC_EVALGA){
		fprintf(Stderr, "ERROR: CallbackEvalGA incorrectly called with eval code %d\n", evalRequestCode);
        return (KTR_RC_CALLBACK_ERR);
	}
	
	nRC = evalGA(x, jac, objGrad, user);
	return nRC;
} /*callbackEvalGA*/

/* Callback function for evaluating hessian or hessVector --
 * but may update obj and c as well. */
int callbackEvalH(
		const int evalRequestCode,
        const int             n,
        const int             m,
        const int             nnzJ,
        const int             nnzH,
        const double * const  x,
        const double * const  lambda,
              double * const  obj,
              double * const  c,
              double * const  objGrad,
              double * const  jac,
              double * const  hessian,
              double * const  hessVector,
              void *  userParams) 
{
    int nRC;
    User_Params * user;

    user = userParams;
	if(evalRequestCode != KTR_RC_EVALH && evalRequestCode != KTR_RC_EVALHV &&
       evalRequestCode != KTR_RC_EVALH_NO_F && evalRequestCode != KTR_RC_EVALHV_NO_F) {
		fprintf(Stderr, "ERROR: CallbackEvalH incorrectly called with eval code %d\n", evalRequestCode);
        return (KTR_RC_CALLBACK_ERR);
	}

	nRC = evalH(nnzH, x, hessian, hessVector, lambda, obj, c,
                evalRequestCode, user);
	return nRC;
} /*callbackEvalH*/


/*------------------------------------------------------------------*/
/*     FUNCTION main                                                */
/*------------------------------------------------------------------*/
int main(int argc, char **argv)
{

  /*---- DECLARE VARIABLES PASSED TO KNITRO. */
  KTR_context *kc;
  int    *xType=NULL;
  int    *cType=NULL;
  int    *cFnType=NULL;  
  int    *jacIndexVars=NULL, *jacIndexCons=NULL;
  int    *hessIndexRows=NULL, *hessIndexCols=NULL;
  int    *xPriorities=NULL;
  double *x=NULL, *lambda=NULL;
  double *objGrad=NULL, *c=NULL, *jac=NULL;
  double *hess=NULL, *hessVector=NULL;
  double *xLoBnds=NULL, *xUpBnds=NULL;
  double *cLoBnds=NULL, *cUpBnds=NULL;
  int    n, m, nnzJ, nnzH;
  int    objGoal, objType;
  int    evalStatus;
  double obj;
  User_Params * user=NULL; /* For callback evaluations. */
  
  char      szVersion[15 + 1];
  double *  daPtrInitX;
  double *  daPtrInitLambda;
  
  /* DECLARE OTHER VARIABLES. */
  ASL *asl, *asl1;
  int i, j, k; 
  int info, flags, status, *vmi;
  double *c_ampl, *g, *pi;
  double *hessvec=NULL;
  fint *hcs, *hrn;
  int needx, nobj, nvc, ow, y, uptri;
  int infonum, errorflag;
  int isMPEC;
  int *lasteval;  
  char buf[360], *stub;
  FILE *nl;
  fint nerror = 0;
  cgrad *cg=NULL;  
  int  bConsAllLinear;
  int  bHaveEvaluatedJac;
  int  bProblemHasIntegerVars;
  int  nNumThreads, nThreadID;
  int (*Reader)(ASL*, FILE*, int);

  /*---- DEFINE SOLUTION STATUS MESSAGES AND NUMERIC VALUES FOR AMPL:
   *----   msg     - PART OF AMPL VAR solve_message
   *----   code    - AMPL VAR solve_result_num AND solve_result
   *----   wantsol - 0=NO SOLUTION POINT, 1=SOLUTION
   *---- CODES SHOULD CONFORM WITH "display $solve_result_table":
   *----     0 - DEFINITELY SOLVED
   *----   100 - PROBABLY SOLVED
   *----   200 - INFEASIBLE
   *----   300 - UNBOUNDED
   *----   400 - LIMIT EXCEEDED
   *----   500 - FAILURE
   */
  typedef struct { char *msg; int code, wantsol; } Sol_info;
  static Sol_info solinfo[] = {
    { /* 0  */  "Locally optimal solution.", 0, 1 },
    { /*-100*/  "Current feasible solution estimate cannot be improved. Nearly optimal.", 100, 1 },
    { /*-101*/  "Relative change in feasible solution estimate < xtol.", 101, 1 },
    { /*-102*/  "Current feasible solution estimate cannot be improved.", 102, 1 },
    { /*-200*/  "Convergence to an infeasible point. Problem may be locally infeasible.", 200, 1 },    
    { /*-201*/  "Relative change in infeasible solution estimate < xtol.", 201, 1 },
    { /*-202*/  "Current infeasible solution estimate cannot be improved.", 202, 1 },
    { /*-203*/  "Multistart: No primal feasible point found.", 203, 1 },
    { /*-204*/  "Problem determined to be infeasible.", 204, 1 },
    { /*-205*/  "Problem determined to be infeasible.", 205, 1 },
    { /*-300*/  "Problem appears to be unbounded.", 300, 1 },
    { /*-400*/  "Iteration limit reached.", 400, 1 },
    { /*-401*/  "Time limit reached.", 401, 1 },
    { /*-402*/  "Function evaluation limit reached.", 402, 1 },
    { /*-403*/  "MIP: All nodes have been explored.", 403, 1 },
    { /*-404*/  "MIP: Integer feasible point found.", 404, 1 },
    { /*-405*/  "MIP: Subproblem solve limit reached.", 405, 1 },
    { /*-406*/  "MIP: Node limit reached.", 406, 1 },
    { /*-501*/  "LP solver error.", 501, 1 },
    { /*-502*/  "Evaluation error.", 502, 1 },
    { /*-503*/  "Not enough memory.", 503, 1 },
    { /*-504*/  "Terminated by user.", 504, 1 },    
    { /*-505:-599*/ "Input or other API error.", 505, 0 },
    { /*-600*/  "Internal KNITRO error.", 506, 0 },
    { /*    */  "Unknown termination.", 507, 0 },
    { /*    */  "Illegal objno value.", 508, 0 }
  };

  /*---- DEFINE SUFFIXES RECOGNIZED BY KNITRO */
  static SufDecl suftab[] = {
    { "priority", 0, ASL_Sufkind_var }, /*-- BRANCH PRIORITIES */
  };
  SufDesc *dp;
  int     *p; /*---- pointer to priority values */
  
  Times[0] = xectim_();
  
  /*---- INITIALIZATION OF FLAGS. */
  bHaveEvaluatedJac = FALSE;

  Stderr_init_ASL(); /* define Stderr */
  if (argc < 2) {
      fprintf (Stderr, "usage: %s AMPL_stub_file <list of KNITRO options>\n",
               argv[0]);
      exit(1);
  }

  /*---- SET THE VERSION FOR "-v" COMMAND LINE OPTION. */
  KTR_get_release (15, szVersion);
  strcpy (g_szBanner, szVersion);
  sprintf (g_szVersionString, "AMPL/%s\n", szVersion);

  /*---- CREATE A NEW PROBLEM INSTANCE. */
  kc = KTR_new();
  if (kc == NULL) {
      fprintf (Stderr, "Failed to find a Ziena license.\n");
      fprintf (Stderr, "%s\n", szVersion);
      exit(1);
  }

  /*---- READ THE stub.nl FILE WRITTEN BY AMPL */
  asl  = ASL_alloc(ASL_read_pfgh);  
  stub = getstub(&argv, &Oinfo);
  if (wantfuncs) {
      show_funcs();
      return 0;
  }
  if (stub == NULL)
      /*---- EXIT AFTER PRINTING SOLVER OPTIONS (TEST IS "knitroampl --"). */
      usage_ASL (&Oinfo, 1);

  /*---- OPEN THE .nl FILE AND READ ITS DIMENSIONS.
   *---- SOLVER CODE WILL EXIT IF THERE IS AN ERROR. */
  nl = jac0dim(stub, (int)strlen(stub));

  if (nwv > 0)
  {
      fprintf (Stderr, "\nERROR: KNITRO cannot process linear arc variables.\n");
      exit(1);
  }
  if ((lnc > 0) || (nlnc > 0))
  {
      fprintf (Stderr, "\nERROR: KNITRO cannot process network constraints.\n");
      exit(1);
  }

  objno = -123456789;
  if (getopts(argv, &Oinfo)) {
      KTR_free(&kc);
      ASL_free(&asl);
      return( 1 );
  }
  if (objno == -123456789 && n_obj <= 1)
      objno = n_obj;
  if ((objno < 0) || (objno > n_obj)) {
      if (objno == -123456789)
          sprintf(buf, "objno must be specified in [0, %d]", n_obj);
      else
          sprintf(buf, "objno = %d must be in [0, %d]", objno, n_obj);
      solve_result_num = 508;
      x      = NULL;
      lambda = NULL;
      write_sol(buf, x, lambda, &Oinfo);
      KTR_free(&kc);
      ASL_free(&asl);
      return 1;
  }
  nobj = objno - 1;     /* Objective number */
  
  /*---- COPY GLOBAL amplspecs INTO KNITRO. */
  errorflag = KTR_set_int_param(kc,KTR_PARAM_ALGORITHM, amplspecs.alg);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_DIRECTINTERVAL, amplspecs.bar_directinterval);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_BAR_INITMU, amplspecs.bar_initmu);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_INITPT, amplspecs.bar_initpt);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_MAXBACKTRACK, amplspecs.bar_maxbacktrack);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_MAXREFACTOR, amplspecs.bar_maxrefactor); 
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_MURULE, amplspecs.bar_murule);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_PENCONS, amplspecs.bar_penaltycons);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_PENRULE, amplspecs.bar_penaltyrule);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_SWITCHRULE, amplspecs.bar_switchrule);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BLASOPTION, amplspecs.blasoption);
  if (strcmp (amplspecs.blasoptionlib, "none") != 0)
      errorflag = KTR_set_char_param_by_name(kc,"blasoptionlib", amplspecs.blasoptionlib);
  if (strcmp (amplspecs.cplexlibname, "none") != 0)
      errorflag = KTR_set_char_param_by_name(kc,"cplexlibname", amplspecs.cplexlibname);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_DEBUG, amplspecs.debug);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_DELTA, amplspecs.delta);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_FEASIBLE, amplspecs.bar_feasible);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_BAR_FEASMODETOL, amplspecs.bar_feasmodetol);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_FEASTOL, amplspecs.feastol);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_FEASTOLABS, amplspecs.feastolabs);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_GRADOPT, amplspecs.gradopt);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_HESSOPT, amplspecs.hessopt);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_HONORBNDS, amplspecs.honorbnds);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_INFEASTOL, amplspecs.infeastol);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_LINSOLVER, amplspecs.linsolver);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_LMSIZE, amplspecs.lmsize);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_LPSOLVER, amplspecs.lpsolver);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MA_MAXTIMECPU, amplspecs.ma_maxtimecpu);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MA_MAXTIMEREAL, amplspecs.ma_maxtimereal);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MA_OUTSUB,amplspecs.ma_outputsub);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MA_TERMINATE, amplspecs.ma_terminate);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MAXCGIT, amplspecs.maxcgit);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_BAR_MAXCROSSIT, amplspecs.bar_maxcrossit);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MAXIT, amplspecs.maxit);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MAXTIMECPU, amplspecs.maxtimecpu);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MAXTIMEREAL, amplspecs.maxtimereal);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_BRANCHRULE,amplspecs.mip_branchrule);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_DEBUG,amplspecs.mip_nDebug);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_GUB_BRANCH,amplspecs.mip_gubBranch);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_HEURISTIC,amplspecs.mip_heuristic);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_HEUR_MAXIT,amplspecs.mip_heuristic_maxit);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_IMPLICATNS,amplspecs.mip_implications);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MIP_INTEGERTOL,amplspecs.mip_integerTol);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MIP_INTGAPABS,amplspecs.mip_integralGapAbs);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MIP_INTGAPREL,amplspecs.mip_integralGapRel);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_KNAPSACK,amplspecs.mip_knapsack);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_LPALG,amplspecs.mip_lpalg);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_MAXNODES,amplspecs.mip_maxnodes);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_MAXSOLVES,amplspecs.mip_maxsolves);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MIP_MAXTIMECPU,amplspecs.mip_maxtimecpu);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MIP_MAXTIMEREAL,amplspecs.mip_maxtimereal);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_METHOD,amplspecs.mip_method);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_OUTINTERVAL,amplspecs.mip_outinterval);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_OUTLEVEL,amplspecs.mip_outlevel);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_OUTSUB,amplspecs.mip_outputsub);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_PSEUDOINIT,amplspecs.mip_pseudoinit);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_ROOTALG,amplspecs.mip_rootalg);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_ROUNDING,amplspecs.mip_rounding);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_SELECTRULE,amplspecs.mip_selectrule);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_STRONG_CANDLIM,amplspecs.mip_strong_candlim);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_STRONG_LEVEL,amplspecs.mip_strong_level);  
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_STRONG_MAXIT,amplspecs.mip_strong_maxit);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MIP_TERMINATE,amplspecs.mip_terminate);  
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MSMAXBNDRANGE,amplspecs.ms_maxbndrange);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MSMAXSOLVES,amplspecs.ms_maxsolves);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MSMAXTIMECPU, amplspecs.ms_maxtimecpu);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MSMAXTIMEREAL, amplspecs.ms_maxtimereal);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MSNUMTOSAVE,amplspecs.ms_numToSave);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MS_OUTSUB,amplspecs.ms_outputsub);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MSSAVETOL,amplspecs.ms_saveTol);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MSSEED,amplspecs.ms_seed);  
  errorflag = KTR_set_double_param(kc,KTR_PARAM_MSSTARTPTRANGE,amplspecs.ms_startptrange);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MSTERMINATE,amplspecs.ms_terminate);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_MULTISTART, amplspecs.multistart);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_NEWPOINT, amplspecs.newpoint);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_OBJRANGE, amplspecs.objrange);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_OPTTOL, amplspecs.opttol);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_OPTTOLABS, amplspecs.opttolabs);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_OUTAPPEND, amplspecs.outappend);
  if (strcmp (amplspecs.outdir, "none") != 0)
      errorflag = KTR_set_char_param_by_name(kc, "outdir", amplspecs.outdir);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_OUTLEV, amplspecs.outlev);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_OUTMODE, amplspecs.outmode);
#if defined(_OPENMP)
  errorflag = KTR_set_int_param(kc,KTR_PARAM_PAR_NUMTHREADS, amplspecs.par_numthreads);
#else
  errorflag = KTR_set_int_param(kc,KTR_PARAM_PAR_NUMTHREADS, 1);
#endif
  errorflag = KTR_set_double_param(kc,KTR_PARAM_PIVOT, amplspecs.pivot);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_PRESOLVE, amplspecs.presolve);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_PRESOLVEDEBUG, amplspecs.presolve_dbg);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_PRESOLVE_TOL, amplspecs.presolve_tol);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_SCALE, amplspecs.scale);
  errorflag = KTR_set_int_param(kc,KTR_PARAM_SOC, amplspecs.soc);
  errorflag = KTR_set_double_param(kc,KTR_PARAM_XTOL, amplspecs.xtol);

  /*---- ALLOW COMPUTATION OF THE HESSIAN WITHOUT OBJECTIVE COMPONENT IN AMPL */
  errorflag = KTR_set_int_param(kc,KTR_PARAM_HESSIAN_NO_F, KTR_HESSIAN_NO_F_ALLOW);

  /*---- PROHIBIT SIMULTANEOUS PARALLEL EVALUATIONS IN AMPL */
  errorflag = KTR_set_int_param(kc,KTR_PARAM_PAR_CONCURRENT_EVALS, KTR_PAR_CONCURRENT_EVALS_NO);

  /*---- SET EVALUATION CALLBACK FUNCTIONS. */
  if(KTR_set_func_callback (kc, &callbackEvalFC) != 0){
      fprintf(Stderr,"\nERROR: Failed to set callbackEvalFC.\n");
      exit(1);
  }
  if(KTR_set_grad_callback (kc, &callbackEvalGA) != 0){
      fprintf(Stderr,"\nERROR: Failed to set callbackEvalGA.\n");
      exit(1);
  }
  if(KTR_set_hess_callback (kc, &callbackEvalH) != 0){
      fprintf(Stderr,"\nERROR: Failed to set callbackEvalH.\n");
      exit(1);
  }  
  
  /*---- KNITRO MAY MODIFY hessopt, BUT NOT UNTIL AFTER KTR_init_problem().
   *---- THIS CODE MUST MAINTAIN CONSISTENCY WITH KNITRO TO CORRECTLY ALLOCATE
   *---- HESSIAN ARRAYS.  HERE WE HANDLE THE CASE WHERE THE USER ENTERED AN
   *---- ILLEGAL VALUE FOR hessopt, WHICH KNITRO WILL CHANGE. */
  if ((amplspecs.hessopt < 1) || (amplspecs.hessopt > 6))
      amplspecs.hessopt = KTR_HESSOPT_EXACT;

  if (amplspecs.hessopt == KTR_HESSOPT_EXACT || amplspecs.hessopt == KTR_HESSOPT_PRODUCT)
      Reader = pfgh_read_ASL;
  else {
      Reader = fg_read_ASL;
      fclose(nl);
      asl1 = ASL_alloc(ASL_read_fg);
      ASL_free(&asl);
      asl = asl1;
      nl = jac0dim(stub, (int)strlen(stub));
  }

  /*---- COMMAND TO STORE SUFFIXES INPUT BY USER */
  suf_declare(suftab, sizeof(suftab)/sizeof(SufDecl));    

  /*---- SET SOME PROBLEM DIMENSIONS. */
  obj_no = nobj;        /* Objective number in ASL */
  
  want_xpi0 = 3;  /* have nlreader set X0 and pi0 nonzero only if specified in the .nl file */
  
  /*---- READ THE stub.nl FILE. */
  flags = ASL_cc_simplify | ASL_sep_U_arrays;
  if (objrep & 1)
      flags |= ASL_obj_replace_ineq;
  if (objrep & 2)
      flags |= ASL_obj_replace_eq;
  Reader(asl,nl,flags);

  /*---- IF THERE IS AN OBJECTIVE, DO WE MAXIMIZE OR MINIMIZE? */
  objGoal = KTR_OBJGOAL_MINIMIZE;
  if (nobj >= 0)
      objGoal = objtype[nobj] ? KTR_OBJGOAL_MAXIMIZE : KTR_OBJGOAL_MINIMIZE;
  
  /*---- GET THE NUMBER OF NONZEROS FOR THE HESSIAN. */
  ow = 1;
  y = 1;
  uptri = 1;
  nnzH = 0;
  if (   (amplspecs.hessopt == KTR_HESSOPT_EXACT)
      || (amplspecs.hessopt == KTR_HESSOPT_PRODUCT) ) {
      /*---- SET UP SO AMPL EVALUATES THE EXACT HESSIAN OR HESSIAN-VECTOR
       *---- PRODUCTS.  PROBLEM chebyqad.mod CRASHES WITH
       *---- hessopt=KTR_HESSOPT_PRODUCT IF THIS CALL IS NOT MADE. */
      nnzH = sphsetup (-1, ow, y, uptri);
  }

  /*---- SET SOME PROBLEM DIMENSIONS/INFO. */
  isMPEC = n_cc > 0;
  n = n_var;
  m = n_con;
  nvc = n + m;
  nnzJ = nzc;

  /*---- DETERMINE NUMBER OF THREADS THAT WILL BE USED FOR THIS SOLVE. */
  nNumThreads = 1;
  nThreadID = 0;
#if defined(_OPENMP)
  if (amplspecs.par_numthreads < 0)
      omp_set_num_threads(1);
  else if (amplspecs.par_numthreads >= 1)
      omp_set_num_threads(amplspecs.par_numthreads);
  /*---- IF par_numthreads == 0, # OF THREADS DETERMINED BY $OMP_NUM_THREADS
   *---- OR IF THIS IS NOT SET, DETERMINED AUTOMATICALLY BY OPENMP. OPEN
   *---- A TEMPORARY PARALLEL REGION JUST TO READ THIS VALUE. */
#pragma omp parallel \
  private(nThreadID) \
  default(shared)
  {
      nThreadID=omp_get_thread_num();        
      if (nThreadID == 0) 
          nNumThreads=omp_get_num_threads();
  }
#endif
#if 0  
  printf("AMPL: Number of threads = %d\n",nNumThreads);  
#endif

  /*---- ALLOCATE SOME ARRAYS. */  
  allocateArrays (n, m, nnzJ, nnzH, isMPEC, nNumThreads, asl,
                  &x, &objGrad, &c, &jac, &jacIndexVars, &jacIndexCons, 
                  &lambda, &xType, 
                  &cType, &cFnType, 
                  &hess, &hessIndexRows, &hessIndexCols, &hessvec, &hessVector,
                  &g, &c_ampl, &lasteval);

  getVariableTypes (asl, xType, &bProblemHasIntegerVars);
  if (relax)
      bProblemHasIntegerVars = 0;

  /*---- COPY INITIAL POINT INTO x. */
  if (X0) {
	memcpy(x, X0, n*sizeof(real));
    daPtrInitX = x;
  } else {
	memset(x,  0, n*sizeof(real));
    daPtrInitX = NULL;
  }
  
  /*---- COPY INITIAL MULTIPLIERS INTO lambda.
   *---- (DO NOT INCLUDE COMPLEMENTARITY VARIABLES
   *---- TREATED AS GENERAL CONSTRAINTS BY AMPL) */
  daPtrInitLambda = NULL;
  if ((pi = pi0)) { /* correct for KNITRO's sign conventions */
      daPtrInitLambda = lambda;
      for(i = 0; i < m; ++i)
          lambda[i] = -pi[i];
  }
  
  /*---- COPY BOUNDS ON x INTO xLoBnds, xUpBnds. */  
  xLoBnds = LUv;
  xUpBnds = Uvx;
  for(i = 0; i < n; ++i) {
      if (xLoBnds[i] <= -KTR_INFBOUND)
          xLoBnds[i] = -KTR_INFBOUND;
      if (xUpBnds[i] >= KTR_INFBOUND)
          xUpBnds[i] = KTR_INFBOUND;
      if (   (xType[i] == KTR_VARTYPE_INTEGER)
          && (xLoBnds[i] == 0.0) && (xUpBnds[i] == 1.0) )
          xType[i] = KTR_VARTYPE_BINARY;
  }

  /*---- COPY BOUNDS ON c INTO cLoBnds, cUpBnds; SET VECTOR cType.   
   *---- (DO NOT INCLUDE COMPLEMENTARITY VARIABLES TREATED
   *----  AS GENERAL CONSTRAINTS BY AMPL) */
  bConsAllLinear = !objrep && !nlc;
  cLoBnds = LUrhs;
  cUpBnds = Urhsx;
  k = nlc;
  for(j = 0; j < m; ++j) {
      if (cLoBnds[j] <= -KTR_INFBOUND)
          cLoBnds[j] = -KTR_INFBOUND;
      if (cUpBnds[j] >= KTR_INFBOUND)
          cUpBnds[j] = KTR_INFBOUND;
      if (j < k) {
          cType[j] = KTR_CONTYPE_GENERAL;
          bConsAllLinear = FALSE;
      }else{
          cType[j] = KTR_CONTYPE_LINEAR;
      }
      cFnType[j] = KTR_FNTYPE_UNCERTAIN;
  }

  /*---- PRINT VARIABLE AND CONSTRAINT NAMES TO SHOW WHAT KNITRO
   *---- RECEIVES AFTER AMPL PRESOLVING. */
  if (amplspecs.presolve_dbg == 2)
      printProblemDef (asl, xType, xLoBnds, xUpBnds,
                       cType, cLoBnds, cUpBnds,
                       asl->i.ccind1, asl->i.ccind2);

  /*---- SET JACOBIAN STRUCTURE jacIndexCons, jacIndexVars.
   *---- (DO NOT INCLUDE SPARSE ELEMENTS FOR COMPLEMENTARITY
   *---- VARIABLES TREATED AS GENERAL CONSTRAINTS BY AMPL) */
  vmi = asl->i.vmap ? get_vminv_ASL(asl) : 0;
  for (i = j = 0; i < m; i++) {
	  for(cg = Cgrad[i]; cg; cg = cg->next) {
          k = cg->varno;
          jacIndexCons[j] = i;
          jacIndexVars[j] = vmi ? vmi[k] : k;
          cg->goff = j++;
      }
  }
  
  /*---- SET HESSIAN STRUCTURE hessIndexRows, hessIndexCols. */
  if (amplspecs.hessopt == KTR_HESSOPT_EXACT) {
      hcs = sputinfo->hcolstarts;
      hrn = sputinfo->hrownos;
      for (i = 0; i < n; i++){
          for (j = hcs[i], k = hcs[i+1]; j < k; j++) {
              hessIndexCols[j] = i;
              hessIndexRows[j] = hrn[j];
          }
      }
  }

  /*---- SET THE OBJECTIVE TYPE */
  objType = KTR_OBJTYPE_GENERAL;
  if (!isMPEC) {
      if (nlo == 0) {
          objType = KTR_OBJTYPE_LINEAR;
      } else {
          if (qpcheck && isaqp(stub) == 1)
              objType = KTR_OBJTYPE_QUADRATIC;
      }
  }
  
  /*---- INITIALIZE THE PROBLEM IN KNITRO. */
  if (bProblemHasIntegerVars)
  {
      status = KTR_mip_init_problem
                   (kc, n, objGoal, objType, KTR_FNTYPE_UNCERTAIN,
                    xType, xLoBnds, xUpBnds,
                    m, cType, cFnType, cLoBnds, cUpBnds,
                    nnzJ, jacIndexVars, jacIndexCons,
                    nnzH, hessIndexRows, hessIndexCols,
                    daPtrInitX, daPtrInitLambda);

      /*---- IF MIP, CHECK FOR DEFINED BRANCH PRIORITIES */
      if (status == 0) {
          dp = suf_get("priority", ASL_Sufkind_var);
          p = dp->u.i;
          if (p) {
              /*---- USER PRIORITIES DEFINED */
              xPriorities = (int *)M1alloc(n*sizeof(int));              
              for (i=0; i<n; i++) {
                  if (xType[i] == KTR_VARTYPE_INTEGER ||
                      xType[i] == KTR_VARTYPE_BINARY) {
                      xPriorities[i] = p[i];
                  } else {
                      xPriorities[i] = 0;
                  }
              }
              status = KTR_mip_set_branching_priorities(kc, xPriorities);
          }
      }
  }
  else
  {
      status = KTR_init_problem
                   (kc, n, objGoal, objType,
                    xLoBnds, xUpBnds,
                    m, cType, cLoBnds, cUpBnds,
                    nnzJ, jacIndexVars, jacIndexCons,
                    nnzH, hessIndexRows, hessIndexCols,
                    daPtrInitX, daPtrInitLambda);
  }

  if (status != 0)
  {
      info = status;
      infonum = setSolutionMessage(info);
      solve_result_num = solinfo[infonum].code;
      sprintf (buf, "%s: %s", szVersion, solinfo[infonum].msg);
      if (solinfo[infonum].wantsol == 0) {
          x      = NULL;
          lambda = NULL;
      }
      write_sol(buf, x, lambda, &Oinfo);
      
      KTR_free(&kc);
      /*---- THIS WILL FREE ALL MEMORY ALLOCATED WITH "M1alloc". */
      ASL_free(&asl);
      exit( 1 );
  }
  
  /*---- IF MPEC, GET THE INDICES OF THE COMPLEMENTARY VARIABLES
   *---- AND GIVE THIS INFORMATION TO KNITRO.
   */
  if (isMPEC) {
      if (KTR_addcompcons (kc, n_cc, asl->i.ccind1, asl->i.ccind2)) {
          /* should not happen */
          fprintf(Stderr,"\nERROR: Bad values for complementarities.\n");
          exit(1);
      }
      conval(x, c, &nerror);
      if (nerror)
          nerror = 0;
      else
          mpec_auxvars_ASL(asl, c, x);	/* adjust initial x and c */
  }

  /*----------------------------------------
   * CALL KNITRO SOLVER
   *----------------------------------------*/
  Times[1] = xectim_();
  info = 0;
  evalStatus = 0;
  needx = nobj >= 0 || m > 0;
  xunknown();
  
  /*---- ALLOCATE AND SET THE user STRUCTURE THAT WILL BE
   *---- PASSED TO CALLBACK EVALUATION ROUTINES. */
  user = (User_Params *) M1alloc (sizeof(*user));
  user->nNumThreads = nNumThreads;
  user->asl = asl;
  user->nvar = n_var;
  user->ncon = n_con;
  user->bProblemHasIntegerVars = bProblemHasIntegerVars;
  user->nobj = nobj;
  user->lasteval = lasteval;
  user->bConsAllLinear = bConsAllLinear;
  user->bHaveEvaluatedJac = bHaveEvaluatedJac;
  user->c_ampl = c_ampl;
  user->g = g;
  user->hessvec = hessvec;
  user->cg = cg;
  if (bProblemHasIntegerVars)
      status = KTR_mip_solve (kc, x, lambda, evalStatus, &obj, 
                              NULL, NULL, NULL, NULL, NULL, user);
  else {
      status = KTR_solve (kc, x, lambda, evalStatus, &obj, 
                          NULL, NULL, NULL, NULL, NULL, user);
  }
  
  Times[2] = xectim_();
  
  /*---- OPTIMIZATION FINISHED, WRITE SOLUTION MESSAGE. */
  info = status;
  infonum = setSolutionMessage(info);
  
  solve_result_num = solinfo[infonum].code;
  if (bProblemHasIntegerVars)
  {
      sprintf (buf, "%s: %s\nobjective %.*g; integrality gap %.3g\n%d nodes; %d subproblem solves",
               szVersion,
               /* print objective to $objective_precision significant figures */
               solinfo[infonum].msg, obj_prec(), obj,
               KTR_get_mip_abs_gap(kc),
               KTR_get_mip_num_nodes(kc),
               KTR_get_mip_num_solves(kc));
  }
  else
  {
      sprintf (buf, "%s: %s\nobjective %.*g; feasibility error %.3g\n%d iterations; %d function evaluations",
               szVersion,
               /* print objective to $objective_precision significant figures */
               solinfo[infonum].msg, obj_prec(), obj,
               KTR_get_abs_feas_error(kc),
               KTR_get_number_iters(kc),
               KTR_get_number_FC_evals(kc));
  }
  if (solinfo[infonum].wantsol == 0) {
      x      = NULL;
      lambda = NULL;
  } else {
      /* Correct for KNITRO's sign conventions. */
      for (i = 0; i < m; i++)
          lambda[i] = -lambda[i];
  }
  write_sol(buf, x, lambda, &Oinfo);

  /*---- DELETE KNITRO PROBLEM INSTANCE. */
  KTR_free(&kc);

  /*---- THIS WILL FREE ALL MEMORY ALLOCATED WITH "M1alloc". */
  ASL_free(&asl);

  if (time_flag & 3) {
	Times[3] = xectim_();
#ifdef _WIN32
	time_flag = 1;
#endif
	for(i = 1; i <= 2; ++i)
		if (time_flag & i)
			fprintf(i ==  1 ? stdout : Stderr,
			"\nTimes (seconds):\nInput =  %g\nSolve =  %g\nOutput = %g\n",
				Times[1] - Times[0], Times[2] - Times[1],
				Times[3] - Times[2]);
	}

  return 0;
}

