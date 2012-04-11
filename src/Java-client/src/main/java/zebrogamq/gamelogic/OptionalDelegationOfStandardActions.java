package zebrogamq.gamelogic;

public abstract class OptionalDelegationOfStandardActions {

	private static OptionalDelegationOfStandardActions instance;

	public static void setInstance(
			final OptionalDelegationOfStandardActions inst) {
		instance = inst;
	}

	public static OptionalDelegationOfStandardActions getInstance()
			throws ActionInvocationException {
		if (instance == null) {
			throw new ActionInvocationException();
		}
		return instance;
	}

	public abstract void terminate(final GameLogicState state,
			final String[] header, final String body);

	public abstract void join(final GameLogicState state,
			final String[] header, final String body);

	public abstract void joinOK(final GameLogicState state,
			final String[] header, final String body);

	public abstract void participantsList(final GameLogicState state,
			final String[] header, final String body);
}
