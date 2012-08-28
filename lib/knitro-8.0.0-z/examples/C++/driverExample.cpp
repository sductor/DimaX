/*******************************************************/
/* Copyright (c) 2006-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++  KNITRO example driver using C++.  The driver loads and solves
//++  an optimization problem at run time using the callback mode of
//++  KNITRO, and user options read from the file "knitro.opt".
//++
//++  KNITRO can be called from C++ in many different ways.  Here the
//++  goal is to provide a simple environment that accommodates any
//++  number of nonlinear optimization test problems.  Problems can
//++  be developed separately from this driver, compiled as a shared
//++  object, and then loaded and solved upon request at run time.
//++  Each problem must implement the abstract class and special
//++  function declared in NlpProblemDef.h.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


//--------------------------------------------------------------------
//  Includes
//--------------------------------------------------------------------

#if defined(_WIN32)
  #include  <windows.h>     //-- FOR LoadLibrary
#else
  #include  <dlfcn.h>       //-- FOR dlopen
#endif
#include  <iostream>
  using namespace std;
#include  <string.h>

#ifndef KNITRO_H__
#include  "knitro.h"
#endif
#ifndef NLPPROBLEMDEF_H__
#include  "nlpProblemDef.h"
#endif


//--------------------------------------------------------------------
//  Static variables and data
//--------------------------------------------------------------------

//---- POINTER TO THE CURRENT TEST PROBLEM TO BE SOLVED.  A STATIC IS
//---- USED BECAUSE THE WRAPPER FUNCTIONS MUST MATCH THE KTR_callback
//---- FUNCTION DEFINITION.
static NlpProblemDef *  g_pOptProblem = NULL;


//--------------------------------------------------------------------
//  Internal Function:  wrapperEvalFC
//--------------------------------------------------------------------
/** By necessity this wrapper signature matches the function KTR_callback.
 *  It calls the current optimization problem's eval method.
 */
static int  wrapperEvalFC (const int             evalRequestCode,
                           const int             n,
                           const int             m,
                           const int             nnzJ,
                           const int             nnzH,
                           const double * const  daX,
                           const double * const  daLambda,
                                 double * const  dObj,
                                 double * const  daC,
                                 double * const  daG,
                                 double * const  daJ,
                                 double * const  daH,
                                 double * const  daHV,
                                 void   *        userParams)
{
    if (g_pOptProblem == NULL)
        {
        cout << "*** Problem not defined  <wrapperEvalFC>\n";
        return( -1 );
        }
    if (evalRequestCode != KTR_RC_EVALFC)
        {
        cout << "*** Bad request code " << evalRequestCode
             << "  <wrapperEvalFC>\n";
        return( -1 );
        }
    return( g_pOptProblem->evalFC (daX, dObj, daC, userParams) );
}


//--------------------------------------------------------------------
//  Internal Function:  wrapperEvalGA
//--------------------------------------------------------------------
/** By necessity this wrapper signature matches the function KTR_callback.
 *  It calls the current optimization problem's eval method.
 */
static int  wrapperEvalGA (const int             evalRequestCode,
                           const int             n,
                           const int             m,
                           const int             nnzJ,
                           const int             nnzH,
                           const double * const  daX,
                           const double * const  daLambda,
                                 double * const  dObj,
                                 double * const  daC,
                                 double * const  daG,
                                 double * const  daJ,
                                 double * const  daH,
                                 double * const  daHV,
                                 void   *        userParams)
{
    if (g_pOptProblem == NULL)
        {
        cout << "*** Problem not defined  <wrapperEvalGA>\n";
        return( -1 );
        }
    if (evalRequestCode != KTR_RC_EVALGA)
        {
        cout << "*** Bad request code " << evalRequestCode
             << "  <wrapperEvalGA>\n";
        return( -1 );
        }
    return( g_pOptProblem->evalGA (daX, daG, daJ, userParams) );
}


//--------------------------------------------------------------------
//  Internal Function:  wrapperEvalHorHV
//--------------------------------------------------------------------
/** By necessity this wrapper signature matches the function KTR_callback.
 *  It calls the current optimization problem's eval method.
 */
static int  wrapperEvalHorHV (const int             evalRequestCode,
                              const int             n,
                              const int             m,
                              const int             nnzJ,
                              const int             nnzH,
                              const double * const  daX,
                              const double * const  daLambda,
                                    double * const  dObj,
                                    double * const  daC,
                                    double * const  daG,
                                    double * const  daJ,
                                    double * const  daH,
                                    double * const  daHV,
                                    void   *        userParams)
{
    if (g_pOptProblem == NULL)
        {
        cout << "*** Problem not defined  <wrapperEvalHorHV>\n";
        return( -1 );
        }
    if (evalRequestCode == KTR_RC_EVALH)
        {
        if (g_pOptProblem->areDerivativesImplemented (nCAN_COMPUTE_H) == false)
            {
            cout << "*** This problem not evaluate H  <wrapperEvalHorHV>\n";
            return( -1 );
            }
        return( g_pOptProblem->evalH (daX, daLambda, 1.0, daH, userParams) );
        }
    else if (evalRequestCode == KTR_RC_EVALH_NO_F)
        {
        if (g_pOptProblem->areDerivativesImplemented (nCAN_COMPUTE_H) == false)
            {
            cout << "*** This problem not evaluate H  <wrapperEvalHorHV>\n";
            return( -1 );
            }
        return( g_pOptProblem->evalH (daX, daLambda, 0.0, daH, userParams) );
        }    
    else if (evalRequestCode == KTR_RC_EVALHV)
        {
        if (g_pOptProblem->areDerivativesImplemented (nCAN_COMPUTE_HV) == false)
            {
            cout << "*** This problem not evaluate H*v  <wrapperEvalHorHV>\n";
            return( -1 );
            }
        return( g_pOptProblem->evalHV (daX, daLambda, 1.0, daHV, userParams) );
        }
    else if (evalRequestCode == KTR_RC_EVALHV_NO_F)
        {
        if (g_pOptProblem->areDerivativesImplemented (nCAN_COMPUTE_HV) == false)
            {
            cout << "*** This problem not evaluate H*v  <wrapperEvalHorHV>\n";
            return( -1 );
            }
        return( g_pOptProblem->evalHV (daX, daLambda, 0.0, daHV, userParams) );
        }    
    else
        {
        cout << "*** Bad request code " << evalRequestCode
             << "  <wrapperEvalHorHV>\n";
        return( -1 );
        }
}


//--------------------------------------------------------------------
//  Internal Function:  printUsage
//--------------------------------------------------------------------
static void  printUsage (void)
{
    cout << "Use KNITRO to solve an optimization problem coded in C++.\n";
    cout << "  Usage:  driverExample problem_name <checkders>\n";
    cout << "          'checkders' means check derivatives instead of solving\n";
    cout << "Note that problem names are case sensitive.\n";
    return;
}


//--------------------------------------------------------------------
//  Internal Function:  loadProblemCode
//--------------------------------------------------------------------
/** Convert the first command line argument to shared link object
 *  that contains a test problem.  The command line may also specify
 *  a boolean flag.
 */
static bool  loadProblemCode (const int                     argc,
                              const char          *  const  argv[],
                                    NlpProblemDef **        pOptProb,
                                    bool          *  const  pbWantToSolve)
{
    *pOptProb = NULL;
    *pbWantToSolve = true;

    //---- CHECK IF THE USER REQUESTED A HELP MESSAGE.
    if (argc < 2)
        return( false );
    for (int  i = 1; i < argc; i++)
        if ((strcmp (argv[i], "-?") == 0) || (strcmp (argv[i], "?") == 0))
            return( false );

    //---- LOAD THE LINK OBJECT.
    cout << "Attempting to load problem '" << argv[1] << "' ...\n";
    #if defined(_WIN32)
        HINSTANCE  hLib = LoadLibrary (argv[1]);
        if (hLib == NULL)
            {
            cout << "*** Could not load DLL '" << argv[1] << "'.\n\n";
            return( false );
            }
        FnType_getNlpProblemDef  fnPtr;
        fnPtr = (FnType_getNlpProblemDef) GetProcAddress (hLib,
                                                          "getNlpProblemDef");
        if (fnPtr == NULL)
            {
            cout << "*** Could not find getNlpProblemDef in DLL '"
                 << argv[1] << "'.\n\n";
            FreeLibrary (hLib);
            return( false );
            }
        *pOptProb = fnPtr();
    #else
        char *  szTmp = new char[100 + 1];
        strcpy (szTmp, argv[1]);
        if (strchr (szTmp, '.') == NULL)
            {
            #if defined(__APPLE__)
                strcat (szTmp, ".dylib");
            #else
                strcat (szTmp, ".so");
            #endif
            }
        void *  hLib = dlopen (szTmp, RTLD_NOW);
        if (hLib == NULL)
            {
            cout << "*** Could not load problem '" << szTmp << "'.\n";
            cout << "    " << dlerror() << "\n\n";
            return( false );
            }
        delete[] szTmp;
        dlerror();    //-- CLEAR THE ERROR ROUTINE.
        FnType_getNlpProblemDef  fnPtr;
        *(void **) (&fnPtr) = dlsym (hLib, "getNlpProblemDef");
        char *  pszError = dlerror();
        if (pszError != NULL)
            {
            cout << "*** Could not find getNlpProblemDef in DLL '"
                 << argv[1] << "'.\n";
            cout << "    " << pszError << "\n\n";
            dlclose (hLib);
            return( false );
            }
        *pOptProb = fnPtr();
    #endif

    //---- CHECK FOR OPTIONAL COMMAND LINE ARGUMENTS.
    if (argc == 3)
        {
        if (strcmp (argv[2], "checkders") == 0)
            *pbWantToSolve = false;
        else
            {
            cout << "*** Unknown argument '" << argv[2] << "'.\n\n";
            return( false );
            }
        }
    if (argc > 3)
        {
        cout << "*** Too many arguments.\n\n";
        return( false );
        }

    return( true );
}

                               
//--------------------------------------------------------------------
//  External Function:  main
//--------------------------------------------------------------------
int  main (const int           argc,
           const char * const  argv[])
{
    //---- LOAD THE TEST PROBLEM FROM THE COMMAND LINE ARGUMENTS.
    NlpProblemDef *  pOptProb;
    bool             bWantToSolve;
    if ((loadProblemCode (argc, argv, &pOptProb, &bWantToSolve) == false))
        {
        printUsage();
        exit( EXIT_FAILURE );
        }

    //---- OPEN A NEW INSTANCE OF KNITRO.
    KTR_context_ptr  kc;
    kc = KTR_new();
    if (kc == NULL)
        {
        cout << "*** KTR_new failed, maybe a license issue?\n";
        exit( EXIT_FAILURE );
        }

    //---- APPLY ANY USER OPTIONS (PROCEED EVEN IF THERE IS AN ERROR).
    KTR_load_param_file (kc, "knitro.opt");

    //---- LOAD THE PROBLEM INTO KNITRO.
    if (pOptProb->loadProblemIntoKnitro (kc) == false)
        {
        cout << "*** loadProblemIntoKnitro failed\n";
        exit( EXIT_FAILURE );
        }

    //---- SET CALLBACK POINTERS FOR EVALUATION OF PROBLEM INFORMATION.
    //---- IF THE TEST CODE DOES NOT SUPPLY DERIVATIVES, THEN THE
    //---- USER OPTIONS IN "knitro.opt" SHOULD REQUEST AN ALTERNATIVE,
    //---- SUCH AS FINITE DIFFERENCES.
    g_pOptProblem = pOptProb;
    if (KTR_set_func_callback (kc,
                               (KTR_callback * const) wrapperEvalFC) != 0)
        {
        cout << "*** KTR_set_func_callback failed\n";
        exit( EXIT_FAILURE );
        }
    if (pOptProb->areDerivativesImplemented (nCAN_COMPUTE_GA) == true)
        {
        if (KTR_set_grad_callback (kc,
                                   (KTR_callback * const) wrapperEvalGA) != 0)
            {
            cout << "*** KTR_set_grad_callback failed\n";
            exit( EXIT_FAILURE );
            }
        }
    if (   (pOptProb->areDerivativesImplemented (nCAN_COMPUTE_H) == true)
        || (pOptProb->areDerivativesImplemented (nCAN_COMPUTE_HV) == true))
        {
        //---- SPECIFY THAT THE USER IS ABLE TO PROVIDE EVALUATIONS
        //---- OF THE HESSIAN MATRIX WITHOUT THE OBJECTIVE COMPONENT.
        //---- TURNED OFF BY DEFAULT BUT SHOULD BE ENABLED IF POSSIBLE.            
        if (KTR_set_int_param (kc, KTR_PARAM_HESSIAN_NO_F, KTR_HESSIAN_NO_F_ALLOW) != 0)
            exit( EXIT_FAILURE );
        if (KTR_set_hess_callback (kc,
                                   (KTR_callback * const) wrapperEvalHorHV) != 0)
            {
            cout << "*** KTR_set_hess_callback failed\n";
            exit( EXIT_FAILURE );
            }
        }

    //---- ALLOCATE ARRAYS
    double *  daX      = new double[pOptProb->getN()];
    double *  daLambda = new double[pOptProb->getM() + pOptProb->getN()];

    if (bWantToSolve == true)
        {
        //---- CALL KNITRO AND SOLVE.
        double  dFinalObj;
        int  nStatus = KTR_solve (kc, daX, daLambda, 0, &dFinalObj,
                                  NULL, NULL, NULL, NULL, NULL, NULL);
        cout << "*** Final KNITRO status = " << nStatus << "\n";
        }
    else
        {
        //---- USE KNITRO TO CHECK THE DERIVATIVES CODED IN THE TEST PROBLEM.
        pOptProb->getInitialX (daX);
        KTR_check_first_ders (kc, daX, 2, 1.0e-14, 1.0e-14,
                              0, 0.0, NULL, NULL, NULL, NULL);
        }

    delete [] daX;
    delete [] daLambda;

    KTR_free (&kc);

    delete pOptProb;
    return( EXIT_SUCCESS );
}


//++++++++++++++++ End of source code ++++++++++++++++++++++++++++++++
