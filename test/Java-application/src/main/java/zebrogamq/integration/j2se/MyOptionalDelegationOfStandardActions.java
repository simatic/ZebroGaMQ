package zebrogamq.integration.j2se;

import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.OptionalDelegationOfStandardActions;

public class MyOptionalDelegationOfStandardActions extends
		OptionalDelegationOfStandardActions {

	public MyOptionalDelegationOfStandardActions() {
		System.out.println("CONSTRUCTOR..." + "CONSTRUCTOR..."
				+ "CONSTRUCTOR..." + "CONSTRUCTOR...");
	}

	public void terminate(final GameLogicState state, final String[] header,
			final String body) {
		System.out.println("TERMINATE..." + "TERMINATE..." + "TERMINATE..."
				+ "TERMINATE...");
	}

	public void join(final GameLogicState state, final String[] header,
			final String body) {
		System.out.println("JOIN..." + "JOIN..." + "JOIN..." + "JOIN...");
	}

	public void joinOK(final GameLogicState state, final String[] header,
			final String body) {
		System.out.println("JOINOK..." + "JOINOK..." + "JOINOK..."
				+ "JOINOK...");
	}

	public void participantsList(final GameLogicState state,
			final String[] header, final String body) {
		System.out.println("PARTICIPANTLIST..." + "PARTICIPANTLIST..."
				+ "PARTICIPANTLIST..." + "PARTICIPANTLIST...");
	}
}
