package zebrogamq.gamelogic;


/**
 * This class should be extended to implement a special behaviour
 * on the reception of basic actions: join actions, lifecycle actions
 * and presence actions.
 * 
 * Indeed, when users receive a JOIN or a JOIN_OK message,the default 
 * behaviour is just to display a message. If you want to define your 
 * own behavior on the reception of such messages (by example to store 
 * the list of players), you should extend this class.
 * 
 * Once you get your own implementation of this class in your 
 * application, you need to call the method 
 * OptionalDelegationOfStandardActions.setInstance() on an instance of 
 * your class. Doing so, when you will receive messages matching with 
 * the basic actions, it will be your own methods that will be called.
 *
 */
public abstract class OptionalDelegationOfStandardActions {

	private static OptionalDelegationOfStandardActions instance;

	/**
	 * Replace the default NOpOptionalDelegationOfStandardActions
	 * class available by another class extending 
	 * OptionalDelegationOfStandardActions. 
	 * 
	 * This method MUST be called if you want to use the method defined
	 * in your own class rather than those of used by default.
	 * 
	 * @param inst an instance of a class extending OptionalDelegationOfStandardActions.
	 */
	public static void setInstance(
			final OptionalDelegationOfStandardActions inst) {
		instance = inst;
	}

	static OptionalDelegationOfStandardActions getInstance()
			throws ActionInvocationException {
		if (instance == null) {
			throw new ActionInvocationException();
		}
		return instance;
	}

	/**
	 * Method called once the client receive a TERMINATE message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public abstract void terminate(final GameLogicState state,
			final String[] header, final String body);

	/**
	 * Method called once the client receive a JOIN message. 
	 * 
	 * @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public abstract void join(final GameLogicState state,
			final String[] header, final String body);

	/**
	 * Method called once the client receive a JOIN_OK message 
	 * from the GameLogicServer. Such message is send by the 
	 * GameLogicServer on the reception of a JOIN message.
	 * 
	* @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public abstract void joinOK(final GameLogicState state,
			final String[] header, final String body);

	/**
	 * Method called once the client receives a PARTICIPANTS_LIST
	 * message from the GameLogicServer.
	 * 
	* @param state		the GameLogicState of the game logic
	 * @param header	an array containing four informations about the message
	 * 					- the sender
	 * 					- the recipient
	 * 					- the action kind
	 * 					- the action
	 * @param body 		the content of the message
	 */
	public abstract void participantsList(final GameLogicState state,
			final String[] header, final String body);
}
