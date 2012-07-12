/**
 * 
 */
package zebrogamq.perf.j2se;

/**
 * @author simatic
 *
 */
public class Stat {
	static long nbSentPingAll = 0;
	static long sizeSentPingAll = 0;
	static long nbRecPingAll = 0;
	static long sizeRecPingAll = 0;
	
	static long nbSentPingPlayer = 0;
	static long sizeSentPingPlayer = 0;
	static long nbRecPingPlayer = 0;
	static long sizeRecPingPlayer = 0;
	
	static long nbSentPingServer = 0;
	static long sizeSentPingServer = 0;
	static long nbRecPingServer = 0;
	static long sizeRecPingServer = 0;
	
	static long nbSentPongAll = 0;
	static long sizeSentPongAll = 0;
	static long nbRecPongAll = 0;
	static long sizeRecPongAll = 0;
	static long durationRecPongAll = 0;	
	
	static long nbSentPongPlayer = 0;
	static long sizeSentPongPlayer = 0;
	static long nbRecPongPlayer = 0;
	static long sizeRecPongPlayer = 0;
	static long durationRecPongPlayer = 0;	
	
	static long nbSentPongServer = 0;
	static long sizeSentPongServer = 0;
	static long nbRecPongServer = 0;
	static long sizeRecPongServer = 0;
	static long durationRecPongServer = 0;	
	
	static void addSentPingAll(long aSize){
		nbSentPingAll++;
		sizeSentPingAll += aSize;
	}
	
	static void addRecPingAll(long aSize){
		nbRecPingAll++;
		sizeRecPingAll += aSize;
	}

	static void addSentPingPlayer(long aSize){
		nbSentPingPlayer++;
		sizeSentPingPlayer += aSize;
	}
	
	static void addRecPingPlayer(long aSize){
		nbRecPingPlayer++;
		sizeRecPingPlayer += aSize;
	}

	static void addSentPingServer(long aSize){
		nbSentPingServer++;
		sizeSentPingServer += aSize;
	}
	
	static void addRecPingServer(long aSize){
		nbRecPingServer++;
		sizeRecPingServer += aSize;
	}

	static void addSentPongAll(long aSize){
		nbSentPongAll++;
		sizeSentPongAll += aSize;
	}
	
	static void addRecPongAll(long aSize, long aDuration){
		nbRecPongAll++;
		sizeRecPongAll += aSize;
		durationRecPongAll += aDuration;
	}

	static void addSentPongPlayer(long aSize){
		nbSentPongPlayer++;
		sizeSentPongPlayer += aSize;
	}
	
	static void addRecPongPlayer(long aSize, long aDuration){
		nbRecPongPlayer++;
		sizeRecPongPlayer += aSize;
		durationRecPongPlayer += aDuration;
	}

	static void addSentPongServer(long aSize){
		nbSentPongServer++;
		sizeSentPongServer += aSize;
	}
	
	static void addRecPongServer(long aSize, long aDuration){
		nbRecPongServer++;
		sizeRecPongServer += aSize;
		durationRecPongServer += aDuration;
	}
	
	static void results(){
		System.out.println("Type;Subtype;Way;nb;size;average;average duration (ms)");
		
		System.out.println("Ping;All;Sent;"+nbSentPingAll+";"+sizeSentPingAll+";"+((double)sizeSentPingAll/(double)nbSentPingAll));
		System.out.println("Ping;Player;Sent;"+nbSentPingPlayer+";"+sizeSentPingPlayer+";"+((double)sizeSentPingPlayer/(double)nbSentPingPlayer));
		System.out.println("Ping;Server;Sent;"+nbSentPingServer+";"+sizeSentPingServer+";"+((double)sizeSentPingServer/(double)nbSentPingServer));

		System.out.println("Ping;All;Rec;"+nbRecPingAll+";"+sizeRecPingAll+";"+((double)sizeRecPingAll/(double)nbRecPingAll));
		System.out.println("Ping;Player;Rec;"+nbRecPingPlayer+";"+sizeRecPingPlayer+";"+((double)sizeRecPingPlayer/(double)nbRecPingPlayer));
		// The following stat is meaningless, as only the server can fill these data
		//System.out.println("Ping;Server;Rec;"+nbRecPingServer+";"+sizeRecPingServer+";"+((double)sizeRecPingServer/(double)nbRecPingServer));
		
		System.out.println("Pong;All;Sent;"+nbSentPongAll+";"+sizeSentPongAll+";"+((double)sizeSentPongAll/(double)nbSentPongAll));
		System.out.println("Pong;Player;Sent;"+nbSentPongPlayer+";"+sizeSentPongPlayer+";"+((double)sizeSentPongPlayer/(double)nbSentPongPlayer));
		// The following stat is meaningless, as only the server can fill these data
		//System.out.println("Pong;Server;Sent;"+nbSentPongServer+";"+sizeSentPongServer+";"+((double)sizeSentPongServer/(double)nbSentPongServer));

		System.out.println("Pong;All;Rec;"+nbRecPongAll+";"+sizeRecPongAll+";"+((double)sizeRecPongAll/(double)nbRecPongAll)+";"+((double)durationRecPongAll/(double)nbRecPongAll));
		System.out.println("Pong;Player;Rec;"+nbRecPongPlayer+";"+sizeRecPongPlayer+";"+((double)sizeRecPongPlayer/(double)nbRecPongPlayer)+";"+((double)durationRecPongPlayer/(double)nbRecPongPlayer));
		System.out.println("Pong;Server;Rec;"+nbRecPongServer+";"+sizeRecPongServer+";"+((double)sizeRecPongServer/(double)nbRecPongServer)+";"+((double)durationRecPongServer/(double)nbRecPongServer));				
	}
}
