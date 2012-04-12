package zebrogamq.gamelogic;

/**
 * Default implementation of the class OptionalDelegationOfStandardActions.
 * 
 * There is no special behavior triggered on the reception of basic actions
 * (join actions, lifecycle actions and presence actions) when this class
 * is used.
 * 
 * To define your own behavior, please refer directly to the javadoc of 
 * OptionalDelegationOfStandardActions.
 */
public class NOpOptionalDelegationOfStandardActions extends
		OptionalDelegationOfStandardActions {

	public NOpOptionalDelegationOfStandardActions() {
	}

	public void terminate(final GameLogicState state, final String[] header,
			final String body) {
	}

	public void join(final GameLogicState state, final String[] header,
			final String body) {
	}

	public void joinOK(final GameLogicState state, final String[] header,
			final String body) {
	}

	public void participantsList(final GameLogicState state,
			final String[] header, final String body) {
	}
}
