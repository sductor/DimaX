/*******************************************************/
/* Copyright (c) 2006-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  KNITRO example driver using reverse communications mode.
//
//  This executable invokes KNITRO to solve a simple nonlinear
//  optimization test problem.  The purpose is to illustrate how to
//  invoke KNITRO using the Java language API.
//
//  Compile using the makefile.
//  Before running, make sure ../../lib is in the load path.
//  To run:
//    java -cp .;knitrojava.jar exampleHS15
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

import com.ziena.knitro.KnitroJava;


/** Solve test problem HS15 from the Hock & Schittkowski collection.
 *
 *  min   100 (x2 - x1^2)^2 + (1 - x1)^2
 *  s.t.  x1 x2 >= 1
 *        x1 + x2^2 >= 0
 *        x1 <= 0.5
 *
 *  The standard start point (-2, 1) usually converges to the standard
 *  minimum at (0.5, 2.0), with final objective = 306.5.
 *  Sometimes the solver converges to another local minimum
 *  at (-0.79212, -1.26243), with final objective = 360.4.
  */
public class exampleHS15
{


    //----------------------------------------------------------------
    //   CONSTRUCTOR
    //----------------------------------------------------------------
    public  exampleHS15 ()
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
        double  dTmp = daX[1] - daX[0]*daX[0];
        double  dObj = 100.0 * dTmp*dTmp + (1.0 - daX[0])*(1.0 - daX[0]);
        daC[0] = daX[0] * daX[1];
        daC[1] = daX[0] + daX[1]*daX[1];

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
        double  dTmp = daX[1] - daX[0]*daX[0];
        daObjGrad[0] = (-400.0 * dTmp * daX[0]) - (2.0 * (1.0 - daX[0]));
        daObjGrad[1] = 200.0 * dTmp;

        daJac[0] = daX[1];
        daJac[1] = daX[0];
        daJac[2] = 1.0;
        daJac[3] = 2.0 * daX[1];

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
        daHess[0] = dSigma * ( (-400.0 * daX[1]) + (1200.0 * daX[0]*daX[0]) + 2.0);
        daHess[1] = (dSigma * (-400.0 * daX[0])) + daLambda[0];
        daHess[2] = (dSigma * 200.0) + (daLambda[1] * 2.0);
        
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
        int  n = 2;
        int  objGoal = KnitroJava.KTR_OBJGOAL_MINIMIZE;
        int  objType = KnitroJava.KTR_OBJTYPE_GENERAL;
        double[]  bndsLo = { -KnitroJava.KTR_INFBOUND,
                             -KnitroJava.KTR_INFBOUND };
        double[]  bndsUp = { 0.5,
                              KnitroJava.KTR_INFBOUND };
        int  m = 2;
        int[]  cType = { KnitroJava.KTR_CONTYPE_QUADRATIC,
                         KnitroJava.KTR_CONTYPE_QUADRATIC };
        double[]  cBndsLo = { 1.0,
                              0.0 };
        double[]  cBndsUp = { KnitroJava.KTR_INFBOUND,
                              KnitroJava.KTR_INFBOUND };
        int  nnzJ = 4;
        int[]  jacIxConstr = { 0, 0, 1, 1 };
        int[]  jacIxVar    = { 0, 1, 0, 1 };
        int  nnzH = 3;
        int[]  hessRow = { 0, 0, 1 };
        int[]  hessCol = { 0, 1, 1 };

        double[]  daXInit = { -2.0, 1.0 };


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

        //---- DEMONSTRATE HOW TO SET KNITRO PARAMETERS.
        if (solver.setCharParamByName ("outlev", "all") == false)
            {
            System.err.println ("Error setting parameter 'outlev'");
            return;
            }
        if (solver.setIntParamByName ("hessopt", 1) == false)
            {
            System.err.println ("Error setting parameter 'hessopt'");
            return;
            }        
        if (solver.setIntParamByName ("hessian_no_f", 1) == false)
            {
            System.err.println ("Error setting parameter 'hessian_no_f'");
            return;
            }        
        if (solver.setDoubleParamByName ("feastol", 1.0E-10) == false)
            {
            System.err.println ("Error setting parameter 'feastol'");
            return;
            }

        //---- INITIALIZE KNITRO WITH THE PROBLEM DEFINITION.
        if (solver.initProblem (n, objGoal, objType, bndsLo, bndsUp,
                                m, cType, cBndsLo, cBndsUp,
                                nnzJ, jacIxVar, jacIxConstr,
                                nnzH, hessRow, hessCol,
                                daXInit, null) == false)
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

        exampleHS15  thisObject = new exampleHS15();

        //---- SOLVE THE PROBLEM.  IN REVERSE COMMUNICATIONS MODE, KNITRO
        //---- RETURNS WHENEVER IT NEEDS MORE PROBLEM INFORMATION.  THE CALLING
        //---- PROGRAM MUST INTERPRET KNITRO'S RETURN STATUS AND CONTINUE
        //---- SUPPLYING PROBLEM INFORMATION UNTIL KNITRO IS COMPLETE.
        int  nKnStatus;
        int  nEvalStatus = 0;
        do
            {
            nKnStatus = solver.solve (nEvalStatus, daObj, daC,
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
            else if (nKnStatus == KnitroJava.KTR_RC_EVALH_NO_F)
                {
                //---- KNITRO WANTS daHess EVALUATED AT THE POINT x
                //---- WITHOUT OBJECTIVE COMPONENT.
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
            case KnitroJava.KTR_RC_FEAS_FTOL:
            case KnitroJava.KTR_RC_FEAS_NO_IMPROVE:
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
        daX = solver.getCurrentX();
        daLambda = solver.getCurrentLambda();
        System.out.println ("  solution feasibility violation    = "
                            + solver.getAbsFeasError());
        System.out.println ("           KKT optimality violation = "
                            + solver.getAbsOptError());
        System.out.println ("  number of function evaluations    = "
                            + solver.getNumberFCEvals());

        //---- BE CERTAIN THE NATIVE OBJECT INSTANCE IS DESTROYED.
        solver.destroyInstance();

        return;
    }


}

//+++++++++++++++++++ End of source file +++++++++++++++++++++++++++++
