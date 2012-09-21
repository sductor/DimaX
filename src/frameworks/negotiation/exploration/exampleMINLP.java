package frameworks.negotiation.exploration;
/*******************************************************/
/* Copyright (c) 2009-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  KNITRO example driver using reverse communications mode.
//
//  This executable invokes KNITRO to solve a simple nonlinear
//  mixed integer optimization test problem.  The purpose is to
//  illustrate how to invoke KNITRO using the Java language API.
//
//  Compile using the makefile.
//  Before running, make sure ../../lib is in the load path.
//  To run:
//    java -cp .;knitrojava.jar exampleMINLP
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import com.ziena.knitro.KnitroJava;


/** Solve test problem 1 from "An outer approximation algorithm for
 *  a class of mixed integer nonlinear programs", M. Duran and I.E. Grossmann,
 *  Mathematical Programming 36, pp. 307-339, 1986 (also known as synthes1
 *  in the MacMINLP test set).
 *
 *  min   5 x4 + 6 x5 + 8 x6 + 10 x1 - 7 x3 -18 log(x2 + 1)
 *        - 19.2 log(x1 - x2 + 1) + 10
 *  s.t.  0.8 log(x2 + 1) + 0.96 log(x1 - x2 + 1) - 0.8 x3 >= 0
 *        log(x2 + 1) + 1.2 log(x1 - x2 + 1) - x3 - 2 x6 >= -2
 *        x2 - x1 <= 0
 *        x2 - 2 x4 <= 0
 *        x1 - x2 - 2 x5 <= 0
 *        x4 + x5 <= 1
 *        0 <= x1 <= 2 
 *        0 <= x2 <= 2
 *        0 <= x3 <= 1
 *        x1, x2, x3 continuous
 *        x4, x5, x6 binary
 *
 *  The solution is (1.30098, 0, 1, 0, 1, 0).
 */
public class exampleMINLP
{


    //----------------------------------------------------------------
    //   CONSTRUCTOR
    //----------------------------------------------------------------
    public  exampleMINLP ()
    { }


    //----------------------------------------------------------------
    //   METHOD evaluateFC
    //----------------------------------------------------------------
    /** Compute the function and constraint values at x.
     *
     *  For more information about the arguments, refer to the KNITRO
     *  manual, especially the section on the Callable Library.
     */
    public double  evaluateFC (double[]  daX,
                               double[]  daC)
    {
        double  dTmp1 = daX[0] - daX[1] + 1.0;
        double  dTmp2 = daX[1] + 1.0;
        double  dObj = 5.0 * daX[3] + 6.0 * daX[4] + 8.0 * daX[5]
                       + 10.0 * daX[0] - 7.0 * daX[2]
                       - 18.0 * Math.log (dTmp2)
                       - 19.2 * Math.log (dTmp1) + 10.0;

        daC[0] = 0.8 * Math.log (dTmp2) + 0.96 * Math.log (dTmp1) - 0.8 * daX[2];
        daC[1] = Math.log (dTmp2) + 1.2 * Math.log (dTmp1) - daX[2] - 2 * daX[5];
        daC[2] = daX[1] - daX[0];
        daC[3] = daX[1] - 2 * daX[3];
        daC[4] = daX[0] - daX[1] - 2 * daX[4];
        daC[5] = daX[3] + daX[4];

        return( dObj );
    }


    //----------------------------------------------------------------
    //   METHOD evaluateGA
    //----------------------------------------------------------------
    /** Compute the function and constraint first deriviatives at x.
     *
     *  For more information about the arguments, refer to the KNITRO
     *  manual, especially the section on the Callable Library.
     */
    public void  evaluateGA (double[]  daX,
                             double[]  daObjGrad,
                             double[]  daJac)
    {
        double  dTmp1 = daX[0] - daX[1] + 1.0;
        double  dTmp2 = daX[1] + 1.0;

        daObjGrad[0] = 10.0 - (19.2 / dTmp1);
        daObjGrad[1] = (-18.0 / dTmp2) + (19.2 / dTmp1);
        daObjGrad[2] = -7.0;
        daObjGrad[3] = 5.0;
        daObjGrad[4] = 6.0;
        daObjGrad[5] = 8.0;

        //---- GRADIENT OF CONSTRAINT 0.
        daJac[0] = 0.96 / dTmp1;
        daJac[1] = (-0.96 / dTmp1) + (0.8 / dTmp2) ;
        daJac[2] = -0.8;
        //---- GRADIENT OF CONSTRAINT 1.
        daJac[3] = 1.2 / dTmp1;
        daJac[4] = (-1.2 / dTmp1) + (1.0 / dTmp2) ;
        daJac[5] = -1.0;
        daJac[6] = -2.0;
        //---- GRADIENT OF CONSTRAINT 2.
        daJac[7] = -1.0;
        daJac[8] = 1.0;
        //---- GRADIENT OF CONSTRAINT 3.
        daJac[9] = 1.0;
        daJac[10] = -2.0;
        //---- GRADIENT OF CONSTRAINT 4.
        daJac[11] = 1.0;
        daJac[12] = -1.0;
        daJac[13] = -2.0;
        //---- GRADIENT OF CONSTRAINT 5.
        daJac[14] = 1.0;
        daJac[15] = 1.0;

        return;
    }


    //----------------------------------------------------------------
    //   METHOD evaluateH
    //----------------------------------------------------------------
    /** Compute the Hessian of the Lagrangian at x and lambda.
     *
     *  For more information about the arguments, refer to the KNITRO
     *  manual, especially the section on the Callable Library.
     */
    public void  evaluateH (double[]  daX,
                            double[]  daLambda,
                            double    dSigma,
                            double[]  daHess)
    {
        double  dTmp1 = daX[0] - daX[1] + 1.0;
        double  dTmp2 = daX[1] + 1.0;

        daHess[0] = dSigma * (19.2 / (dTmp1 * dTmp1))
                     + daLambda[0] * (-0.96 / (dTmp1 * dTmp1))
                     + daLambda[1] * (-1.2  / (dTmp1 * dTmp1));
        daHess[1] = dSigma * (-19.2 / (dTmp1 * dTmp1))
                    + daLambda[0] * (0.96 / (dTmp1 * dTmp1))
                    + daLambda[1] * (1.2  / (dTmp1 * dTmp1));
        daHess[2] = dSigma * ((19.2 / (dTmp1 * dTmp1)) + (18.0 / (dTmp2 * dTmp2)))
                    + daLambda[0] * (  (-0.96 / (dTmp1 * dTmp1))
                                     - ( 0.8  / (dTmp2 * dTmp2)) )
                    + daLambda[1] * (  (-1.2  / (dTmp1 * dTmp1))
                                     - ( 1.0  / (dTmp2 * dTmp2)) );

        return;
    }


    //----------------------------------------------------------------
    //   MAIN METHOD FOR TESTING
    //----------------------------------------------------------------
    public static void  main (String[]  args)
    {
        //---- DEFINE THE OPTIMIZATION TEST PROBLEM.
        //---- FOR MORE INFORMATION ABOUT THE PROBLEM DEFINITION, REFER
        //---- TO THE KNITRO MANUAL, ESPECIALLY THE SECTION ON THE
        //---- CALLABLE LIBRARY.
        int  n = 6;
        int  objGoal   = KnitroJava.KTR_OBJGOAL_MINIMIZE;
        int  objType   = KnitroJava.KTR_OBJTYPE_GENERAL;
        int  objFnType = KnitroJava.KTR_FNTYPE_CONVEX;
        int[]  varType
            = {
                KnitroJava.KTR_VARTYPE_CONTINUOUS,
                KnitroJava.KTR_VARTYPE_CONTINUOUS,
                KnitroJava.KTR_VARTYPE_CONTINUOUS,
                KnitroJava.KTR_VARTYPE_BINARY,
                KnitroJava.KTR_VARTYPE_BINARY,
                KnitroJava.KTR_VARTYPE_BINARY
              };
        //---- SPECIFY BOUNDS OF [0,1] FOR BINARY VARIABLES.
        double[]  bndsLo = { 0.0, 0.0, 0.0, 0.0, 0.0, 0.0 };
        double[]  bndsUp = { 2.0, 2.0, 1.0, 1.0, 1.0, 1.0 };

        //---- NO INITIAL POINT PROVIDED; KNITRO WILL COMPUTE ONE.

        int  m = 6;
        int[]  cType
            = {
                KnitroJava.KTR_CONTYPE_GENERAL,
                KnitroJava.KTR_CONTYPE_GENERAL,
                KnitroJava.KTR_CONTYPE_LINEAR,
                KnitroJava.KTR_CONTYPE_LINEAR,
                KnitroJava.KTR_CONTYPE_LINEAR,
                KnitroJava.KTR_CONTYPE_LINEAR
              };
        int[]  cFnType
            = {
                KnitroJava.KTR_FNTYPE_CONVEX,
                KnitroJava.KTR_FNTYPE_CONVEX,
                KnitroJava.KTR_FNTYPE_CONVEX,
                KnitroJava.KTR_FNTYPE_CONVEX,
                KnitroJava.KTR_FNTYPE_CONVEX,
                KnitroJava.KTR_FNTYPE_CONVEX
              };
        double[]  cBndsLo
            = {
                 0.0,
                -2.0,
                -KnitroJava.KTR_INFBOUND,
                -KnitroJava.KTR_INFBOUND,
                -KnitroJava.KTR_INFBOUND,
                -KnitroJava.KTR_INFBOUND
              };
        double[]  cBndsUp
            = {
                KnitroJava.KTR_INFBOUND,
                KnitroJava.KTR_INFBOUND,
                0.0,
                0.0,
                0.0,
                1.0
              };

        int  nnzJ = 16;
        int[]  jacIxConstr = { 0, 0, 0, 1, 1, 1, 1, 2, 2, 3, 3, 4, 4, 4, 5, 5 };
        int[]  jacIxVar    = { 0, 1, 2, 0, 1, 2, 5, 0, 1, 1, 3, 0, 1, 4, 3, 4 };

        int  nnzH = 3;
        int[]  hessRow = { 0, 0, 1 };
        int[]  hessCol = { 0, 1, 1 };


        //---- SETUP AND RUN KNITRO TO SOLVE THE PROBLEM.

        //---- CREATE A SOLVER INSTANCE.
        KnitroJava  solver = null;
        try
        {
            solver = new KnitroJava();
        }
        catch (java.lang.Exception  e)
        {
            System.err.println (e);
            return;
        }

        //---- SET KNITRO PARAMETERS FOR MINLP SOLUTION.
        if (solver.setIntParamByName ("mip_method", 1) == false)
        {
            System.err.println ("Error setting parameter 'mip_method'");
            return;
        }
        if (solver.setIntParamByName ("algorithm", 3) == false)
        {
            System.err.println ("Error setting parameter 'algorithm'");
            return;
        }        
        if (solver.setIntParamByName ("outlev", 6) == false)
        {
            System.err.println ("Error setting parameter 'outlev'");
            return;
        }
        if (solver.setIntParamByName ("mip_outinterval", 1) == false)
        {
            System.err.println ("Error setting parameter 'mip_outinterval'");
            return;
        }
        if (solver.setIntParamByName ("hessian_no_f", 1) == false)
        {
            System.err.println ("Error setting parameter 'hessian_no_f'");
            return;
        }

        //---- INITIALIZE KNITRO WITH THE PROBLEM DEFINITION.
        if (solver.mipInitProblem (n, objGoal, objType, objFnType,
                                   varType, bndsLo, bndsUp,
                                   m, cType, cFnType, cBndsLo, cBndsUp,
                                   nnzJ, jacIxVar, jacIxConstr,
                                   nnzH, hessRow, hessCol,
                                   null, null) == false)
        {
            System.err.println ("Error initializing the problem, "
                                + "KNITRO status = "
                                + solver.getKnitroStatusCode());
            return;
        }

        //---- ALLOCATE ARRAYS FOR REVERSE COMMUNICATIONS OPERATION.
        double[]  daX       = new double[n];
        double[]  daLambda  = new double[m + n];
        double[]  daObj     = new double[1];
        double[]  daC       = new double[m];
        double[]  daObjGrad = new double[n];
        double[]  daJac     = new double[nnzJ];
        double[]  daHess    = new double[nnzH];

        exampleMINLP  thisObject = new exampleMINLP();

        //---- SOLVE THE PROBLEM.  IN REVERSE COMMUNICATIONS MODE, KNITRO
        //---- RETURNS WHENEVER IT NEEDS MORE PROBLEM INFORMATION.  THE CALLING
        //---- PROGRAM MUST INTERPRET KNITRO'S RETURN STATUS AND CONTINUE
        //---- SUPPLYING PROBLEM INFORMATION UNTIL KNITRO IS COMPLETE.
        int  nKnStatus;
        int  nEvalStatus = 0;
        do
        {
            nKnStatus = solver.mipSolve (nEvalStatus, daObj, daC,
                                         daObjGrad, daJac, daHess);
            if (nKnStatus == KnitroJava.KTR_RC_EVALFC)
            {
                //---- KNITRO WANTS daObj AND daC EVALUATED AT THE POINT x.
                daX = solver.getCurrentX();
                daObj[0] = thisObject.evaluateFC (daX, daC);
            }
            else if (nKnStatus == KnitroJava.KTR_RC_EVALGA)
            {
                //---- KNITRO WANTS daObjGrad AND daJac EVALUATED AT THE POINT x.
                daX = solver.getCurrentX();
                thisObject.evaluateGA (daX, daObjGrad, daJac);
            }
            else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
            {
                //---- KNITRO WANTS daHess EVALUATED AT THE POINT x.
                daX = solver.getCurrentX();
                daLambda = solver.getCurrentLambda();
                thisObject.evaluateH (daX, daLambda, 1.0, daHess);
            }
            else if (nKnStatus == KnitroJava.KTR_RC_EVALH)
            {
                //---- KNITRO WANTS daHess EVALUATED AT THE POINT x
                //---- WITHOUT OBJECTIVE COMPONENT INCLUDED.
                daX = solver.getCurrentX();
                daLambda = solver.getCurrentLambda();
                thisObject.evaluateH (daX, daLambda, 0.0, daHess);
            }
            
            //---- ASSUME THAT PROBLEM EVALUATION IS ALWAYS SUCCESSFUL.
            //---- IF A FUNCTION OR ITS DERIVATIVE COULD NOT BE EVALUATED
            //---- AT THE GIVEN (x, lambda), THEN SET nEvalStatus = 1 BEFORE
            //---- CALLING solve AGAIN.
            nEvalStatus = 0;
        }
        while (nKnStatus > 0);

        //---- DISPLAY THE RESULTS.
        System.out.print ("KNITRO finished, status " + nKnStatus + ": ");
        switch (nKnStatus)
        {
            case KnitroJava.KTR_RC_OPTIMAL:
                System.out.println ("converged to optimality.");
                break;
            case KnitroJava.KTR_RC_ITER_LIMIT:
                System.out.println ("reached the maximum number of allowed iterations.");
                break;
            case KnitroJava.KTR_RC_NEAR_OPT:
            case KnitroJava.KTR_RC_FEAS_XTOL:
            case KnitroJava.KTR_RC_FEAS_NO_IMPROVE:
            case KnitroJava.KTR_RC_FEAS_FTOL:
                System.out.println ("could not improve upon the current iterate.");
                break;
            case KnitroJava.KTR_RC_TIME_LIMIT:
                System.out.println ("reached the maximum CPU time allowed.");
                break;
            default:
                System.out.println ("failed.");
        }

        //---- EXAMPLES OF OBTAINING SOLUTION INFORMATION.
        System.out.println ("  optimal value = " + daObj[0]);
        System.out.println ("  integrality gap (abs) = "
                            + solver.getMipAbsGap());
        System.out.println ("  integrality gap (rel) = "
                            + solver.getMipRelGap());
        System.out.println ("  solution feasibility violation (abs)    = "
                            + solver.getAbsFeasError());
        System.out.println ("           KKT optimality violation (abs) = "
                            + solver.getAbsOptError());
        System.out.println ("  number MIP nodes processed   = "
                            + solver.getMipNumNodes());
        System.out.println ("  number MIP subproblem solves = "
                            + solver.getMipNumSolves());

        //---- BE CERTAIN THE NATIVE OBJECT INSTANCE IS DESTROYED.
        solver.destroyInstance();

        return;
    }


}

//+++++++++++++++++++ End of source file +++++++++++++++++++++++++++++
