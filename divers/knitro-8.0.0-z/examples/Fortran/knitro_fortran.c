/*******************************************************/
/* Copyright (c) 2006 by Ziena Optimization, Inc.      */
/* All Rights Reserved                                 */
/*******************************************************/

/*++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
 *  This file contains an example wrapper for the C language KNITRO
 *  application programming interface (API).  The wrapper holds the
 *  KTR_context structure, which cannot be passed to Fortran.  
 *  Function names are exported in the form required for a Fortran
 *  linker (no capital letters, trailing underscores).
 *
 *  The wrapper assumes array indices that embody the structure of
 *  sparse problem derivatives follow the C language convention and
 *  start numbering from zero.
 *
 *  This file is merely an example.  Not all KNITRO API calls are made
 *  available.  Consult the KNITRO manual and "knitro.h" for other calls.
 *++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++*/


/*------------------------------------------------------------------*/
/*     INCLUDES                                                     */
/*------------------------------------------------------------------*/

#include <stdio.h>
#include <stdlib.h>

#include "knitro.h"


/*------------------------------------------------------------------*/
/*     FILE GLOBAL ITEMS                                            */
/*------------------------------------------------------------------*/

const char  szName[] = { "--- (knitro_fortran) " };

static KTR_context *  g_kc = NULL;



/*------------------------------------------------------------------*/
/*     FUNCTION ktrf_open_instance                                  */
/*------------------------------------------------------------------*/
/** Create a new KNITRO instance.
 *  Exit with an error message if it failed.
 */
#if defined(sun) || defined (__APPLE__)
void  ktrf_open_instance_ (void)
#elif defined(_WIN32)
void  KTRF_OPEN_INSTANCE (void)
#else
void  ktrf_open_instance__ (void)
#endif
{
    if (g_kc != NULL)
        {
        fprintf (stderr, "%sKNITRO solver instance already exists.\n", szName);
        exit( -1 );
        }
    g_kc = KTR_new();
    if (g_kc == NULL)
        {
        fprintf (stderr, "%sFailed to create KNITRO solver instance.\n", szName);
        exit( -1 );
        }
    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION ktrf_load_param_file                                */
/*------------------------------------------------------------------*/
/** Tell KNITRO to load user options from the standard parameter file.
 */
#if defined(sun) || defined (__APPLE__)
void  ktrf_load_param_file_ (void)
#elif defined(_WIN32)
void  KTRF_LOAD_PARAM_FILE (void)
#else
void  ktrf_load_param_file__ (void)
#endif
{
    int  nStatus;

    if (g_kc == NULL)
        {
        fprintf (stderr, "%sKNITRO solver instance does not exist.\n", szName);
        exit( -1 );
        }

    nStatus = KTR_load_param_file (g_kc, "knitro.opt");
    if (nStatus != 0)
        {
        fprintf (stderr, "%sKNITRO returned error code %d from KTR_load_param_file.\n",
                 nStatus, szName);
        exit( -1 );
        }
    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION ktrf_init_problem                                   */
/*------------------------------------------------------------------*/
/** Pass the problem definition to KNITRO.
 *  Assume array indices in jacIndexVars, jacIndexCons, hessIndexRows,
 *  and hessIndexCols follow the C convention and start numbering
 *  from zero (the Fortran convention is to start from one).
 */
#if defined(sun) || defined (__APPLE__)
void  ktrf_init_problem_
#elif defined(_WIN32)
void  KTRF_INIT_PROBLEM
#else
void  ktrf_init_problem__
#endif
                          (int    * const  n,
                           int    * const  objGoal,
                           int    * const  objType,
                           double * const  xLoBnds,
                           double * const  xUpBnds,
                           int    * const  m,
                           int    * const  cType,
                           double * const  cLoBnds,
                           double * const  cUpBnds,
                           int    * const  nnzJ,
                           int    * const  jacIndexVars,
                           int    * const  jacIndexCons,
                           int    * const  nnzH,
                           int    * const  hessIndexRows,
                           int    * const  hessIndexCols,
                           double * const  xInitial)
{
    int  nStatus;

    if (g_kc == NULL)
        {
        fprintf (stderr, "%sKNITRO solver instance does not exist.\n", szName);
        exit( -1 );
        }

    nStatus =  KTR_init_problem (g_kc, *n, *objGoal, *objType,
                                 xLoBnds, xUpBnds,
                                 *m, cType, cLoBnds, cUpBnds,
                                 *nnzJ, jacIndexVars, jacIndexCons,
                                 *nnzH, hessIndexRows, hessIndexCols,
                                 xInitial, NULL);
    if (nStatus != 0)
        {
        fprintf (stderr, "%sKNITRO returned error code %d from KTR_init_problem.\n",
                 nStatus, szName);
        exit( -1 );
        }

    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION ktrf_solve                                          */
/*------------------------------------------------------------------*/
/** Invoke the solver to compute its next iteration.
 */
#if defined(sun) || defined (__APPLE__)
void  ktrf_solve_
#elif defined(_WIN32)
void  KTRF_SOLVE
#else
void  ktrf_solve__
#endif
                   (double * const  x,            /*--       OUTPUT */
                    double * const  lambda,       /*--       OUTPUT */
                    int    * const  evalStatus,   /*-- INPUT        */
                    double * const  obj,          /*-- INPUT OUTPUT */
                    double * const  c,            /*-- INPUT        */
                    double * const  objGrad,      /*-- INPUT        */
                    double * const  jac,          /*-- INPUT        */
                    double * const  hess,         /*-- INPUT        */
                    double * const  hvector,      /*-- INPUT OUTPUT */
                    int    * const  status)       /*--       OUTPUT */

{
    if (g_kc == NULL)
        {
        fprintf (stderr, "%sKNITRO solver instance does not exist.\n", szName);
        exit( -1 );
        }

    *status = KTR_solve (g_kc, x, lambda, *evalStatus,
                         obj, c, objGrad, jac, hess, hvector, NULL);

    return;
}


/*------------------------------------------------------------------*/
/*     FUNCTION ktrf_close_instance                                 */
/*------------------------------------------------------------------*/
/** Delete the KNITRO instance.
 *  Exit with an error message if it failed.
 */
#if defined(sun) || defined (__APPLE__)
void  ktrf_close_instance_ (void)
#elif defined(_WIN32)
void  KTRF_CLOSE_INSTANCE (void)
#else
void  ktrf_close_instance__ (void)
#endif
{
    if (g_kc == NULL)
        {
        fprintf (stderr, "%sKNITRO solver instance does not exist.\n", szName);
        exit( -1 );
        }
    if (KTR_free (&g_kc) != 0)
        {
        fprintf (stderr, "%sFailed to close KNITRO solver instance.\n", szName);
        exit( -1 );
        }
    g_kc = NULL;
    return;
}


/*---- End of source code ------------------------------------------*/
