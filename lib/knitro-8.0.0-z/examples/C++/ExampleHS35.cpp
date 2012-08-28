/*******************************************************/
/* Copyright (c) 2006-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++  This example subclasses NlpProblemDef to solve a simple QP:
//++    min  9 - 8x1 - 6x2 - 4x3
//++         + 2(x1^2) + 2(x2^2) + (x3^2) + 2(x1*x2) + 2(x1*x3)
//++    subject to  c[0]:  x1 + x2 + 2x3 <= 3
//++                x1 >= 0
//++                x2 >= 0
//++                x3 >= 0
//++    initpt (0.5, 0.5, 0.5)
//++
//++    Solution is x1=4/3, x2=7/9, x3=4/9, lambda=2/9  (f* = 1/9)
//++
//++  The problem comes from Hock and Schittkowski, HS35.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


//--------------------------------------------------------------------
//  Includes
//--------------------------------------------------------------------

#include  <iostream>
  using namespace std;

#include  "ExampleHS35.h"


//--------------------------------------------------------------------
//  Static variables and data
//--------------------------------------------------------------------

HS35 *  HS35::_pTheNlpProblemDefInstance = NULL;


//--------------------------------------------------------------------
//  Constructors and Destructors
//--------------------------------------------------------------------

HS35::HS35 (void)
{
    _daXInit = NULL;
    return;
}


HS35 *  HS35::getTheInstance (void)
{
    if (_pTheNlpProblemDefInstance == NULL)
        //---- MUST BE THE FIRST CALL, SO CREATE THE SINGLE INSTANCE.
        _pTheNlpProblemDefInstance = new HS35();

    return( _pTheNlpProblemDefInstance );
}



HS35::~HS35 (void)
{
    delete [] _daXInit;
    return;
}


//--------------------------------------------------------------------
//  Simple access methods
//--------------------------------------------------------------------

int  HS35::getN (void)
{
    return( _nN );
}


int  HS35::getM (void)
{
    return( _nM );
}


void  HS35::getInitialX (double * const  daX)
{
    if (_daXInit == NULL)
        {
        cout << "*** Must call 'loadProblemIntoKnitro' before 'HS35::getInitialX'\n";
        exit( EXIT_FAILURE );
        }

    for (int  i = 0; i < _nN; i++)
        daX[i] = _daXInit[i];
    return;
}


//--------------------------------------------------------------------
//  Method:  loadProblemIntoKnitro
//--------------------------------------------------------------------
/** Define the fixed problem definition information and pass it to
 *  KNITRO by calling KTR_init_problem.
 */
bool  HS35::loadProblemIntoKnitro (KTR_context_ptr  kc)
{
    int  i;

    _nN = 3;
    _nM = 1;

    //---- VARIABLES ARE BOUNDED FROM BELOW.
    _daXLo  = new double[_nN];
    _daXUp  = new double[_nN];
    for (i = 0; i < _nN; i++)
        {
        _daXLo[i] = 0.0;
        _daXUp[i] = KTR_INFBOUND;
        }

    //---- THE CONSTRAINTS IS A LINEAR INEQUALITY.
    //---- PUT THE CONSTANT TERM IN THE RIGHT-HAND SIDE.
    _naCType  = new int[_nM];
    _daCLo    = new double[_nM];
    _daCUp    = new double[_nM];
    _daCLo[0] = -KTR_INFBOUND;
    _daCUp[0] = 3.0;
    _naCType[0] = KTR_CONTYPE_LINEAR;

    //---- SPECIFY THE CONSTRAINT JACOBIAN SPARSITY STRUCTURE.
    _nNnzJ = 3;
    _naJacIndexVars = new int[_nNnzJ];
    _naJacIndexCons = new int[_nNnzJ];
    _naJacIndexCons[ 0] = 0;  _naJacIndexVars[ 0] = 0;
    _naJacIndexCons[ 1] = 0;  _naJacIndexVars[ 1] = 1;
    _naJacIndexCons[ 2] = 0;  _naJacIndexVars[ 2] = 2;

    //---- SPECIFY THE HESSIAN OF THE LAGRANGIAN SPARSITY STRUCTURE.
    _nNnzH = 5;
    _naHessRows = new int[_nNnzH];
    _naHessCols = new int[_nNnzH];
    _naHessRows[0] = 0;  _naHessCols[0] = 0;
    _naHessRows[1] = 0;  _naHessCols[1] = 1;
    _naHessRows[2] = 0;  _naHessCols[2] = 2;
    _naHessRows[3] = 1;  _naHessCols[3] = 1;
    _naHessRows[4] = 2;  _naHessCols[4] = 2;

    //---- INITIAL GUESS FOR x AND lambda.
    _daXInit = new double[_nN];
    double *  daLambdaInit = new double[_nM + _nN];
    for (i = 0; i < _nN; i++)
        _daXInit[i] = 0.5;
    for (i = 0; i < _nM + _nN; i++)
        daLambdaInit[i] = 0.0;

    i = KTR_init_problem (kc, _nN,
                          KTR_OBJGOAL_MINIMIZE, KTR_OBJTYPE_QUADRATIC,
                          _daXLo, _daXUp,
                          _nM, _naCType, _daCLo, _daCUp,
                          _nNnzJ, _naJacIndexVars, _naJacIndexCons,
                          _nNnzH, _naHessRows, _naHessCols,
                          _daXInit, daLambdaInit);

    delete [] _daXLo;
    delete [] _daXUp;
    delete [] _naCType;
    delete [] _daCLo;
    delete [] _daCUp;
    delete [] _naJacIndexVars;
    delete [] _naJacIndexCons;
    delete [] _naHessRows;
    delete [] _naHessCols;
    delete [] daLambdaInit;

    if (i != 0)
        {
        cout << "*** KTR_init_problem() returned " << i << "\n";
        return( false );
        }

    return( true );
}


//--------------------------------------------------------------------
//  Method:  areDerivativesImplemented
//--------------------------------------------------------------------
bool  HS35::areDerivativesImplemented
          (const DerivativesImplementedType  nWhichDers)
{
    if (nWhichDers == nCAN_COMPUTE_GA)
        return( true );
    if (nWhichDers == nCAN_COMPUTE_H)
        return( true );
    if (nWhichDers == nCAN_COMPUTE_HV)
        return( true );
    return( false );
}


//--------------------------------------------------------------------
//  Method:  evalFC
//--------------------------------------------------------------------
int  HS35::evalFC (const double * const  daX,
                         double * const  dObj,
                         double * const  daC,
                         void   *        userParams)
{
    *dObj =   9.0 - (8.0 * daX[0]) - (6.0 * daX[1]) - (4.0 * daX[2])
            + (2.0 * daX[0] * daX[0]) + (2.0 * daX[1] * daX[1])
                                      + (daX[2] * daX[2])
            + (2.0 * daX[0] * daX[1]) + (2.0 * daX[0] * daX[2]);

    daC[0] = daX[0] + daX[1] + (2.0 * daX[2]);

    return( 0 );
}


//--------------------------------------------------------------------
//  Method:  evalGA
//--------------------------------------------------------------------
int  HS35::evalGA (const double * const  daX,
                         double * const  daG,
                         double * const  daJ,
                         void   *        userParams)
{
    daG[0] = -8.0 + (4.0 * daX[0]) + (2.0 * daX[1]) + (2.0 * daX[2]);
    daG[1] = -6.0 + (2.0 * daX[0]) + (4.0 * daX[1]);
    daG[2] = -4.0 + (2.0 * daX[0])                  + (2.0 * daX[2]);

    daJ[ 0] = 1.0;
    daJ[ 1] = 1.0;
    daJ[ 2] = 2.0;

    return( 0 );
}


//--------------------------------------------------------------------
//  Method:  evalH
//--------------------------------------------------------------------
int  HS35::evalH (const double * const  daX,
                  const double * const  daLambda,
                  const double          dSigma,
                        double * const  daH,
                        void   *        userParams)
{
    daH[0] = dSigma*4.0;
    daH[1] = dSigma*2.0;
    daH[2] = dSigma*2.0;
    daH[3] = dSigma*4.0;
    daH[4] = dSigma*2.0;

    return( 0 );
}


//--------------------------------------------------------------------
//  Method:  evalHV
//--------------------------------------------------------------------
int  HS35::evalHV (const double * const  daX,
                   const double * const  daLambda,
                   const double          dSigma,
                         double * const  daHV,
                         void   *        userParams)
{
    daHV[0] = (dSigma*4.0 * daHV[0]) + (dSigma*2.0 * daHV[1]) + (dSigma*2.0 * daHV[2]);
    daHV[1] = (dSigma*2.0 * daHV[0]) + (dSigma*4.0 * daHV[1]);
    daHV[2] = (dSigma*2.0 * daHV[0])                          + (dSigma*2.0 * daHV[2]);

    return( 0 );
}


//--------------------------------------------------------------------
//  Exported Function:  getNlpProblemDef
//--------------------------------------------------------------------
#ifdef __cplusplus
extern "C"
{
    #if defined(_WIN32)
    __declspec(dllexport) NlpProblemDef *  getNlpProblemDef (void)
    #else
                          NlpProblemDef *  getNlpProblemDef (void)
    #endif
    {
        return( HS35::getTheInstance() );
    }
}
#endif

//++++++++++++++++ End of source code ++++++++++++++++++++++++++++++++
