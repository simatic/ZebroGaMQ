package zebrogamq.gamelogic;

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
