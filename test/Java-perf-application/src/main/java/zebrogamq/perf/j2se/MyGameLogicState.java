/**
 * 
 */
package zebrogamq.perf.j2se;

import zebrogamq.gamelogic.GameLogicState;

/**
 * @author simatic
 *
 */
public class MyGameLogicState extends GameLogicState {

	public int nbMsgs;
	public int waitBetweenMsgs;
	public int payloadSize;

	/**
	 * 
	 */
	public MyGameLogicState() {
		super();
	}

}
