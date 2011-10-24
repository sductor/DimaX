package dima.introspectionbasedagents.ontologies.FIPAACLOntologie;

public enum Performative {

	/*
	 * passage d'information
	 */
	// Communication par l'expéditeur d'une proposition, pensée vrai par
	// celui-ci.
	Inform,
	// Communication par l'expéditeur d'une proposition (pensée vrai par
	// celui-ci), et demande au receveur une confirmation ou une
	// non-confirmation.
	// Macro-action impliquant l'usage de "request".
	InformIf,
	// Communication par l'expéditeur d'une demande de l’objet qui correspond à
	// une description envoyée.
	// Macro-action impliquant l'usage de "request".
	InformRef,

	// Communication par l'expéditeur de la confirmation de la validité (selon
	// les règles de l'agent) de la proposition préalablement reçue.
	Confirm,
	// Communication par l'expéditeur de la confirmation de la non validité
	// (selon les règles de l'agent) de la proposition préalablement reçue.
	Disconfirm,

	/*
	 * manipulation des erreurs
	 */
	// Communication par l'expéditeur d'une non compréhension d'une action
	// effectuée par le destinataire.
	NotUnderstood,
	// Communication par l'expéditeur de l'échec d'une action essayée.
	Failure,

	/*
	 * réquisition d'information
	 */
	// Communication...
	QueryIf,
	// Communication par l'expéditeur d'une demande par l'expéditeur de l'objet
	// réferrencé par une expression.
	QueryRef,
	// Communication par l'expéditeur d'une demande d'un objet donnée par une
	// référence envoyé par l'expéditeur, et de renotifier l'agent ayant
	// souscrit dès que l'objet en question change.
	Subscribe,

	/*
	 * négociation
	 */
	// Communication par l'expéditeur d'une demande d'effectuer une certaine
	// action.
	Cfp,
	// Communication par l'expéditeur d'une proposition d'action conditionnée à
	// certaines préconditions données.
	Propose,
	// Communication, pendant une négociation, par l'expéditeur de son refus
	// d'effectuer des actions.
	RejectProposal,
	// Communication, pendant une négociation, par l'expéditeur de son
	// acceptation d'effectuer des actions.
	AcceptProposal,

	// Communication par l'expéditeur d'un message à propager à des agents dont
	// la description est fournie. Le destinataire du message traite le
	// sous-message à propager comme s'il lui était directement destiné et
	// envoie le message "propate" au agent qu'il a identifié
	Propagate,
	// Communication par l'expéditeur d'une demande d'une transmission d'un
	// message à des agents dont la description est donnée.
	Proxy,

	/*
	 * distribution de tâches (ou exécution d'une action)
	 */
	// Communication par l'expéditeur de son acceptation d'effectuer une action
	// donnée, et en donne les raisons.
	Agree,
	// Communication par l'expéditeur de son refus d'effectuer une action
	// donnée, et en donne les raisons.
	Refuse,
	// Communication...
	Cancel,
	// Communication par l'expéditeur d'une demande au destinataire d'effectuer
	// une action.
	Request,
	// Communication par l'expéditeur d'une demande, au destinataire,
	// d'effectuer une action quand une proposition donnée devient vrai.
	RequestWhen,
	// Communication par l'expéditeur d'une demande, au destinataire,
	// d'effectuer une action dès qu'une proposition donnée devient vrai, et à
	// chaque fois que celle-ci redevient vrai.
	RequestWhenever,


	//
	//
	//
	//Perso
	Wait
}
