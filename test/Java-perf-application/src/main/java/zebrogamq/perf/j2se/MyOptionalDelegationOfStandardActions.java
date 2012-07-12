/**
 * 
 */
package zebrogamq.perf.j2se;

import java.util.Arrays;

import zebrogamq.gamelogic.GameLogicState;
import zebrogamq.gamelogic.OptionalDelegationOfStandardActions;
import zebrogamq.gamelogic.Util;

/**
 * @author simatic
 *
 */
public class MyOptionalDelegationOfStandardActions extends
		OptionalDelegationOfStandardActions {

	public String participants = "";
	
	public void terminate(final GameLogicState state,
			final String[] header, final String body){
		Util.println("***We exit!***");
		Stat.results();
		System.exit(0);
	}
	
	public void join(final GameLogicState state,
			final String[] header, final String body){
	}

	public void joinOK(final GameLogicState state,
			final String[] header, final String body){
	}

	public void participantsList(final GameLogicState state,
			final String[] header, final String body){
		participants = body;
		// We comment out th following metho, as we are unable to measure the duration :-(
		//Stat.addRecPongServer(body.length());
	}


}
