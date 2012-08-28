/*******************************************************/
/* Copyright (c) 2006-2011 by Ziena Optimization LLC   */
/* All Rights Reserved                                 */
/*******************************************************/

//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//++  This example subclasses NlpProblemDef to solve a simple QP.
//++  See cpp definition source for a description of the NLP problem.
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  Includes
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++

#ifndef KNITRO_H__
#include  "knitro.h"
#endif
#ifndef NLPPROBLEMDEF_H__
#include  "nlpProblemDef.h"
#endif


//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
//  Class Declaration:  HS35
//++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++++
class HS35 : public NlpProblemDef
{
  protected:

    //++ Constructor is protected so that only "getTheInstance()" can call it.
    HS35 (void);

  public:

    //++ Returns a pointer to the single instance of the class, constructing
    //++ the instance if necessary.  This is the only way to access a
    //++ constructed instance of the class.
    static HS35 *  getTheInstance (void);

    ~HS35 (void);

    //++ Declare virtual base class methods that are implemented here.
    //++ See NlpProblemDef.h for descriptions.

    int   getN (void);
    int   getM (void);
    void  getInitialX (double * const  daX);
    bool  loadProblemIntoKnitro (KTR_context_ptr  kc);
    bool  areDerivativesImplemented
              (const DerivativesImplementedType  nWhichDers);

    int  evalFC (const double * const  daX,
                       double * const  dObj,
                       double * const  daC,
                       void   *        userParams);
    int  evalGA (const double * const  daX,
                       double * const  daG,
                       double * const  daJ,
                       void   *        userParams);
    int  evalH (const double * const  daX,
                const double * const  daLambda,
                const double          dSigma,
                      double * const  daH,
                      void   *        userParams);
    int  evalHV (const double * const  daX,
                 const double * const  daLambda,
                 const double          dSigma,
                       double * const  daHV,
                       void   *        userParams);

  private:

    //++ SINGLE INSTANCE OF THE CLASS.
    static HS35 *  _pTheNlpProblemDefInstance;

    //++ COPY CONSTRUCTOR IS DELIBERATELY NOT ALLOWED.
    HS35 (const HS35 &);

    //++ COPY OPERATOR IS DELIBERATELY NOT ALLOWED.
    HS35 &  operator=(const HS35 &);
};


//++ Export a function that returns the static pointer to this class.
//++ Since applications will load a shared object, the export needs
//++ to be for the C language.
#ifdef __cplusplus
extern "C"
{
    #if defined(_WIN32)
    __declspec(dllexport) NlpProblemDef *  getNlpProblemDef (void);
    #else
                          NlpProblemDef *  getNlpProblemDef (void);
    #endif
}
#endif


//++++++++++++++++ End of source code ++++++++++++++++++++++++++++++++
