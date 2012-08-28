/*******************************************************/
/* Copyright (c) 2006-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *  KNITRO example that checks analytic derivatives.
 *
 *  This executable invokes KNITRO to check analytic derivatives
 *  of a simple nonlinear optimization test problem.  The example
 *  illustrates how to check using callback functions or reverse
 *  communications mode.  The check prints all differencs between
 *  analytic partial derivatives and finite difference estimates.
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


#include <stdio.h>
#include <stdlib.h>
#include <string.h>

#include "knitro.h"
#include "problemDef.h"


/*------------------------------------------------------------------*/
/*     FUNCTION callbackEvalFC                                      */
/*------------------------------------------------------------------*/
/** The signature of this function matches KTR_callback in knitro.h.
 *  Only "obj" and "c" are modified.
 */
int  callbackEvalFC (const int             evalRequestCode,
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
                           void   *        userParams)
{
    if (evalRequestCode != KTR_RC_EVALFC)
        {
        printf ("*** callbackEvalFC incorrectly called with eval code %d\n",
                evalRequestCode);
        return( -1 );
        }

    /*---- IN THIS EXAMPLE, CALL THE HS15 EVALUATION ROUTINE. */
    *obj = computeFC (x, c);
    return( 0 );
}


/*------------------------------------------------------------------*/ 
/*     FUNCTION callbackEvalGA                                      */
/*------------------------------------------------------------------*/
/** The signature of this function matches KTR_callback in knitro.h.
 *  Only "objGrad" and "jac" are modified.
 */
int  callbackEvalGA (const int             evalRequestCode,
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
                           void   *        userParams)
{
    if (evalRequestCode != KTR_RC_EVALGA)
        {
        printf ("*** callbackEvalGA incorrectly called with eval code %d\n",
                evalRequestCode);
        return( -1 );
        }

    /*---- IN THIS EXAMPLE, CALL THE HS15 EVALUATION ROUTINE. */
    computeGA (x, objGrad, jac);
    return( 0 );
}


/*------------------------------------------------------------------*/
/*     FUNCTION isCallbackRequested                                 */
/*------------------------------------------------------------------*/
/** Check command line arguments and return TRUE if the user
 *  requested callback mode, FALSE if reverse communications mode.
 *  Exit the program if there is an error.
 */
static int  isCallbackRequested (const int           argc,
                                       char * const  argv[])
{
    if (argc == 2)
        {
        if (strncmp (argv[1], "callback", 8) == 0)
            return( TRUE );
        if (strncmp (argv[1], "reversecomm", 11) == 0)
            return( FALSE );
        }

    printf ("Check analytic derivatives against finite differences.\n");
    printf ("Usage:\n");
    printf ("  checkDersExample reversecomm|callback\n");
    exit( -1 );
}


/*------------------------------------------------------------------*/
/*     main                                                         */
/*------------------------------------------------------------------*/
int  main (int  argc, char  *argv[])
{
    int     i;
    int     nStatus;
    int     bUseCallback;
    double  threshold;

    KTR_context  *kc;
    int          n, m, nnzJ, nnzH, objGoal, objType;
    int          *cType;
    int          *jacIndexVars, *jacIndexCons, *hessRows, *hessCols;
    double       obj, *x, *c;
    double       *xLoBnds, *xUpBnds, *xInitial, *cLoBnds, *cUpBnds;
    double       *objGrad, *jac;

    obj = 0.0;


    /*---- DETERMINE WHETHER THE USER WANTS TO TEST CALLBACK OR
     *---- REVERSE COMMUNICATION MODE. */
    bUseCallback = isCallbackRequested (argc, argv);


    /*---- FETCH THE SIZES OF THE PROBLEM TO BE SOLVED. */
    getProblemSizes (&n, &m, &nnzJ, &nnzH);

    /*---- ALLOCATE MEMORY FOR THE PROBLEM DEFINITION. */
    xLoBnds      = (double *) malloc (n * sizeof(double));
    xUpBnds      = (double *) malloc (n * sizeof(double));
    xInitial     = (double *) malloc (n * sizeof(double));
    cType        = (int    *) malloc (m * sizeof(int));
    cLoBnds      = (double *) malloc (m * sizeof(double));
    cUpBnds      = (double *) malloc (m * sizeof(double));
    jacIndexVars = (int    *) malloc (nnzJ * sizeof(int));
    jacIndexCons = (int    *) malloc (nnzJ * sizeof(int));
    hessRows     = (int    *) malloc (nnzH * sizeof(int));
    hessCols     = (int    *) malloc (nnzH * sizeof(int));

    /*---- FETCH THE DEFINITION OF THE PROBLEM TO BE SOLVED. */
    getProblemData (&objType, &objGoal, xLoBnds, xUpBnds, xInitial,
                    cType, cLoBnds, cUpBnds,
                    jacIndexVars, jacIndexCons,
                    hessRows, hessCols, NULL, NULL, NULL);

    /*---- CREATE A NEW KNITRO SOLVER INSTANCE. */
    kc = KTR_new();
    if (kc == NULL)
        {
        printf ("Failed to find a Ziena license.\n");
        return( -1 );
        }

    /*---- INITIALIZE KNITRO WITH THE PROBLEM DEFINITION. */
    nStatus = KTR_init_problem (kc, n, objGoal, objType,
                                xLoBnds, xUpBnds,
                                m, cType, cLoBnds, cUpBnds,
                                nnzJ, jacIndexVars, jacIndexCons,
                                nnzH, hessRows, hessCols, xInitial, NULL);

    /*---- KNITRO KEEPS ITS OWN COPY OF THE PROBLEM DEFINITION,
     *---- SO THE LOCAL MEMORY CAN BE FREED IMMEDIATELY. */
    free (xLoBnds);
    free (xUpBnds);
    free (xInitial);
    free (cType);
    free (cLoBnds);
    free (cUpBnds);
    free (jacIndexVars);
    free (jacIndexCons);
    free (hessRows);
    free (hessCols);

    /*---- CHOOSE A POINT AT WHICH TO CHECK DERIVATIVES. */
    x = (double *) malloc (n * sizeof(double));
    for (i = 0; i < n; i++)
        x[i] = 1.37;

    /*---- CHOOSE THE THRESHOLD FOR REPORTING DISCREPANCIES.
     *---- FOR THIS EXAMPLE, USE AN ARTIFICIALLY SMALL THRESHOLD
     *---- TO DEMONSTRATE WARNING MESSAGES.
     *----
     *---- FINITE DIFFERENCE ACCURACY IS LIMITED BY DOUBLE PRECISION
     *---- ACCURACY (USUALLY SPECIFIED AS DBL_EPSILON IN float.h),
     *---- AND THE MAGNITUDE OF HIGHER ORDER PARTIAL DERIVATIVES IN THE
     *---- NONLINEAR FUNCTIONS.  REALISTIC APPLICATIONS USING FIRST
     *---- DERIVATIVE APPROXIMATIONS CANNOT EXPECT ACCURACY BETTER THAN:
     *----   FORWARD DIFF - DBL_EPSILON^(1/2) (1.5e-08 ON MOST x86 MACHINES)
     *----   CENTRAL DIFF - DBL_EPSILON^(2/3) (3.7e-11 ON MOST x86 MACHINES)
     */
    threshold = 1.0e-10;

    /*---- CHECK FIRST DERIVATIVES USING FINITE DIFFERENCES. */
    if (bUseCallback == TRUE)
        {
        /*---- CALLBACK MODE.  DEFINE THE CALLBACKS. */
        if (KTR_set_func_callback (kc, &callbackEvalFC) != 0)
            exit( -1 );
        if (KTR_set_grad_callback (kc, &callbackEvalGA) != 0)
            exit( -1 );
        /*---- CHECK USING FORWARD DIFFERENCES. */
        nStatus = KTR_check_first_ders (kc, x, 1, threshold, threshold,
                                        0, 0.0, NULL, NULL, NULL, NULL);
        if (nStatus != 0)
            printf ("*** KTR_check_first_ders returned status %d\n",
                    nStatus);
        /*---- CHECK USING CENTRAL DIFFERENCES. */
        nStatus = KTR_check_first_ders (kc, x, 2, threshold, threshold,
                                        0, 0.0, NULL, NULL, NULL, NULL);
        if (nStatus != 0)
            printf ("*** KTR_check_first_ders returned status %d\n",
                    nStatus);
        }
    else
        {
        /*---- REVERSE COMMUNICATIONS MODE.  THE ROUTINE RETURNS
         *---- WHENEVER IT NEEDS MORE PROBLEM INFORMATION.  THE CALLING
         *---- PROGRAM MUST INTERPRET THE RETURN STATUS AND CONTINUE
         *---- SUPPLYING PROBLEM INFORMATION UNTIL COMPLETE.
         */

        /*---- ALLOCATE BUT DO NOT INITIALIZE.  KNITRO WILL SPECIFY
         *---- WHEN TO POPULATE THESE. */
        c       = (double *) malloc (m    * sizeof(double));
        objGrad = (double *) malloc (n    * sizeof(double));
        jac     = (double *) malloc (nnzJ * sizeof(double));

        /*---- CHECK USING FORWARD DIFFERENCES. */
        while (1)
            {
            nStatus = KTR_check_first_ders (kc, x, 1, threshold, threshold,
                                            0, obj, c, objGrad, jac, NULL);
            if      (nStatus == KTR_RC_EVALFC)
                /*---- NEED TO EVALUATE obj AND c AT THE POINT x. */
                obj = computeFC (x, c);
            else if (nStatus == KTR_RC_EVALGA)
                /*---- NEED TO EVALUATE objGrad AND jac ANALYTICALLY AT x. */
                computeGA (x, objGrad, jac);
            else if (nStatus == 0)
                /*---- FINISHED. */
                break;
            else
                {
                printf ("*** KTR_check_first_ders returned status %d\n",
                        nStatus);
                break;
                }
            }

        /*---- CHECK USING CENTRAL DIFFERENCES. */
        while (1)
            {
            nStatus = KTR_check_first_ders (kc, x, 2, threshold, threshold,
                                            0, obj, c, objGrad, jac, NULL);
            if      (nStatus == KTR_RC_EVALFC)
                /*---- NEED TO EVALUATE obj AND c AT THE POINT x. */
                obj = computeFC (x, c);
            else if (nStatus == KTR_RC_EVALGA)
                /*---- NEED TO EVALUATE objGrad AND jac ANALYTICALLY AT x. */
                computeGA (x, objGrad, jac);
            else if (nStatus == 0)
                /*---- FINISHED. */
                break;
            else
                {
                printf ("*** KTR_check_first_ders returned status %d\n",
                        nStatus);
                break;
                }
            }

        free (c);
        free (objGrad);
        free (jac);
        }

    free (x);

    /*---- DELETE THE KNITRO SOLVER INSTANCE. */
    KTR_free (&kc);

    return( 0 );
}

/*----- End of source code -----------------------------------------*/
