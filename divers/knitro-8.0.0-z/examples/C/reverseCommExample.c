/*******************************************************/
/* Copyright (c) 2006-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *  KNITRO example driver using reverse communications mode.
 *
 *  This executable invokes KNITRO to solve a simple nonlinear
 *  optimization test problem.  The purpose is to illustrate how to
 *  invoke KNITRO using the C language API.
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


#include <stdio.h>
#include <stdlib.h>

#include "knitro.h"
#include "problemDef.h"


/*------------------------------------------------------------------*/
/*     main                                                         */
/*------------------------------------------------------------------*/
int  main (int  argc, char  *argv[])
{
    int  nStatus;
    int  bDone;

    /*---- DECLARE VARIABLES THAT ARE PASSED TO KNITRO. */
    KTR_context  *kc;
    int          n, m, nnzJ, nnzH, objGoal, objType;
    int          *cType;
    int          *jacIndexVars, *jacIndexCons, *hessRows, *hessCols;
    double       obj, *x, *lambda, *c;
    double       *xLoBnds, *xUpBnds, *xInitial, *cLoBnds, *cUpBnds;
    double       *objGrad, *jac, *hess, *hvector;
    int          evalStatus = 0;


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

    /*---- NOW THAT WE HAVE THE PROBLEM SIZE, ALLOCATE ARRAYS THAT ARE
     *---- PASSED TO KNITRO DURING THE SOLVE ITERATIONS.
     *----
     *---- THIS EXAMPLE ALLOCATES BOTH hess AND hvector SO THAT ANY
     *---- HESSIAN OPTION CAN BE SELECTED.
     *----
     *---- NOTICE lambda HAS MULTIPLIERS FOR BOTH CONSTRAINTS AND BOUNDS
     *---- (A COMMON MISTAKE IS TO ALLOCATE ITS SIZE AS m).
     */
    x       = (double *) malloc (n     * sizeof(double));
    lambda  = (double *) malloc ((m+n) * sizeof(double));
    c       = (double *) malloc (m     * sizeof(double));
    objGrad = (double *) malloc (n     * sizeof(double));
    jac     = (double *) malloc (nnzJ  * sizeof(double));
    hess    = (double *) malloc (nnzH  * sizeof(double));
    hvector = (double *) malloc (n     * sizeof(double));

    /*---- CREATE A NEW KNITRO SOLVER INSTANCE. */
    kc = KTR_new();
    if (kc == NULL)
    {
        printf ("Failed to find a Ziena license.\n");
        return( -1 );
    }

    /*---- ILLUSTRATE HOW TO OVERRIDE DEFAULT OPTIONS.
     *---- OPTIONS MUST BE SET BEFORE CALLING KTR_init_problem.
     *---- (SEE callbackExample1.c FOR OTHER TECHNIQUES.)
     */
    if (KTR_set_int_param_by_name (kc, "hessopt", KTR_HESSOPT_SR1) != 0)
        return( -1 );
    if (KTR_set_int_param_by_name (kc, "outmode", KTR_OUTMODE_SCREEN) != 0)
        return( -1 );
    if (KTR_set_int_param (kc, KTR_PARAM_OUTLEV, KTR_OUTLEV_ALL) != 0)
        return( -1 );

    /*---- SPECIFY THAT THE USER IS ABLE TO PROVIDE EVALUATIONS
     *---- OF THE HESSIAN MATRIX WITHOUT THE OBJECTIVE COMPONENT.
     *---- TURNED OFF BY DEFAULT BUT SHOULD BE ENABLED IF POSSIBLE. */
    if (KTR_set_int_param (kc, KTR_PARAM_HESSIAN_NO_F, KTR_HESSIAN_NO_F_ALLOW) != 0)
        return( -1 );
    
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

    /*---- SOLVE THE PROBLEM.  IN REVERSE COMMUNICATIONS MODE, KNITRO
     *---- RETURNS WHENEVER IT NEEDS MORE PROBLEM INFORMATION.  THE CALLING
     *---- PROGRAM MUST INTERPRET KNITRO'S RETURN STATUS AND CONTINUE
     *---- SUPPLYING PROBLEM INFORMATION UNTIL KNITRO IS COMPLETE.
     *----
     *---- RETURN STATUS CODES ARE DEFINED IN "knitro.h" AND DESCRIBED
     *---- IN THE KNITRO MANUAL.
     */
    bDone = 0;
    while (!bDone)
    {
        nStatus = KTR_solve (kc, x, lambda, evalStatus, &obj, c,
                             objGrad, jac, hess, hvector, NULL);
        switch (nStatus)
        {
        case KTR_RC_EVALFC:
            /*---- KNITRO WANTS obj AND c EVALUATED AT THE POINT x. */
            obj = computeFC (x, c);
            break;
        case KTR_RC_EVALGA:
            /*---- KNITRO WANTS objGrad AND jac EVALUATED AT x. */
            computeGA (x, objGrad, jac);
            break;
        case KTR_RC_EVALH:
            /*---- KNITRO WANTS FULL hess EVALUATED AT (x, lambda). */
            computeH (x, 1.0, lambda, hess);
            break;
        case KTR_RC_EVALH_NO_F:
            /*---- KNITRO WANTS hess WITHOUT OBJ COMPONENT EVALUATED AT (x, lambda). */
            computeH (x, 0.0, lambda, hess);
            break;
        case KTR_RC_EVALHV:
            /*---- KNITRO WANTS FULL hvector EVALUATED AT (x, lambda). */
            computeHV (x, 1.0, lambda, hvector);
            break;
        case KTR_RC_EVALHV_NO_F:
            /*---- KNITRO WANTS hvector WITHOUT OBJ COMPONENT EVALUATED AT (x, lambda). */
            computeHV (x, 0.0, lambda, hvector);
            break;            
        case KTR_RC_NEWPOINT:
            /*---- IF USER OPTION "newpoint" IS SET TO 3=user, THEN
             *---- KNITRO RETURNS THIS STATUS CODE WHEN IT FINDS AN
             *---- IMPROVED ITERATE (x, lambda).  NO ACTION IS TAKEN IN
             *---- THIS EXAMPLE.
             */
            break;
        default:
            /*---- ANY OTHER STATUS CODE MEANS KNITRO IS FINISHED. */
            bDone = 1;
            break;
        }
        
        /*---- ASSUME THAT PROBLEM EVALUATION IS ALWAYS SUCCESSFUL.
         *---- IF A FUNCTION OR ITS DERIVATIVE COULD NOT BE EVALUATED
         *---- AT THE GIVEN (x, lambda), THEN SET evalStatus = 1 BEFORE
         *---- CALLING KTR_solve AGAIN. */
        evalStatus = 0;
    }

    printf ("\n\n");
    if (nStatus != 0)
        printf ("KNITRO failed to solve the problem, final status = %d\n",
                nStatus);
    else
    {
        /*---- AN EXAMPLE OF OBTAINING SOLUTION INFORMATION. */
        printf ("KNITRO successful, feasibility violation    = %e\n",
                KTR_get_abs_feas_error (kc));
        printf ("                   KKT optimality violation = %e\n",
                KTR_get_abs_opt_error (kc));
    }


    /*---- DELETE THE KNITRO SOLVER INSTANCE. */
    KTR_free (&kc);

    free (x);
    free (lambda);
    free (c);
    free (objGrad);
    free (jac);
    free (hess);
    free (hvector);

    return( 0 );
}

/*----- End of source code -----------------------------------------*/
