/**
 * aMazing! Geolocalized multiplayer game for Android devices.
 * Conceived and realized within the course "Mixed Reality Games for 
 * Mobile Devices" at Fraunhofer FIT (http://www.fit.fraunhofer.de).
 * Copyright (C) 2012  Alexander Hermans, Tianjiao Wang
 * Contact: 
 * alexander.hermans0@gmail.com, tianjiao.wang@rwth-aachen.de,
 * richard.wetzel@fit.fraunhofer.de, lisa.blum@fit.fraunhofer.de, 
 * denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * 
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 * 
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 * 
 * Developer(s): Alexander Hermans, Tianjiao Wang
 * ZebroGaMQ:  Denis Conan, Gabriel Adgeg 
 */

package zebrogamq.communication;

import com.google.android.maps.GeoPoint;

import android.os.Handler;
import android.os.Message;

import de.rwth.aMazing.GameSession;
import de.rwth.aMazing.PathStorage;
import de.rwth.aMazing.SoundManager;

public class MyGameLogicProtocol{ 
	public  static Handler handler = null; 
	

	public static Object proximityViolation(String coordinates) {
		if (GameSession.violationInAction == false) {
			GameSession.violationInAction = true;
		}
		String[] data = coordinates.split("[|]");
		String[] gp = data[0].split("[*]");
		GameSession.lastCorrectLocation = new GeoPoint(Integer.parseInt(gp[0]), Integer.parseInt(gp[1]));
		if(data.length > 1) PathStorage.removeProximityViolations(data[1]);
		handler.sendEmptyMessage(0);
		return null;
	}

	public static Object proximityViolationCorrected(String body) {
		GameSession.violationInAction = false;
		GameSession.lastCorrectLocation = null; 
		handler.sendEmptyMessage(1);
		return null;
	}
	

	public static Object playerReady() {
		handler.sendEmptyMessage(5);
		return null;
	}
	public static void setHandler(Handler handler) {
		MyGameLogicProtocol.handler = handler;
	}

	public static Object itemUpdate(String body) {
		String[] data = body.split("[*]");
		switch(Integer.parseInt(data[0])){
			case -2:
				GameSession.pickup(Integer.parseInt(data[1]));
				handler.sendEmptyMessage(10);
				break;
			case -1:
				GameSession.remove(Integer.parseInt(data[1]));
				break;
			case 1:
				handler.sendEmptyMessage(-10);
				break;
			case 2:
				handler.sendEmptyMessage(-20);
				break;
			case 3:
				handler.sendEmptyMessage(-30);
				break;
			case 4:
				handler.sendEmptyMessage(-40);
				break;
				
			
		}
		return null;
	}

	public static Object handleP2Data(String data) {
		PathStorage.addMazeCornersP2(data);
		return null;
	}

	public static Object start(String body) {
		GameSession.parseGameSessionFromString(body);
		handler.sendEmptyMessage(2);
		return null;
	}

	public static Object updateCrownClaims(String crownInfo) {
        GameSession.updatCrownClaims(crownInfo);
        int[] owner = GameSession.owner;
        boolean[] claimChange = GameSession.claimChange;
        for(int i=0;i<5;i++){
        	if((owner[i]==1)&&claimChange[i]==true){SoundManager.playSound(6,1);}
        	if((owner[i]!=1)&&claimChange[i]==true){SoundManager.playSound(7,1);}
        }
        		
		return null;
	}

	public static Object displayWinner(String body) {
		Message msg = Message.obtain();
		msg.what = 8;
		msg.obj = body;
		handler.sendMessage(msg);
		return null;
	}

	public static Object teleportItem(String body) {
		String[] data = body.split("[|]");
		if(Integer.parseInt(data[0])==1){
			//We are expecting this message.
			GameSession.selectedItem = null;
			GameSession.selectingForTeleport = false;

			if(data[1].equals("fail")){
				//Moving item failed
				handler.sendEmptyMessage(602);
			}else{
				GameSession.updateItemLocation(data[1]);
				GameSession.removeByType(6); // 6 is the teleporter.
				handler.sendEmptyMessage(601);
			}
		}else{
			//An update caused by the other players. 
			GameSession.updateItemLocation(data[1]);	
			handler.sendEmptyMessage(600);
		}
		return null;
	}

	public static Object drawItem(String body) {
		String[] data = body.split("[|]");
		if(Integer.parseInt(data[0])==1){
			//We are expecting this message.
			GameSession.selectedItem = null;
			GameSession.selectingForMagnet = false;
			if(data[1].equals("fail")){
				//Moving item failed
				handler.sendEmptyMessage(302);
			}else{
				
				GameSession.updateItemLocation(data[1]);
				GameSession.removeByType(3); // 3 is magnet.
				handler.sendEmptyMessage(301);
			}
		}else{
			//An update caused by the other players. 
			GameSession.updateItemLocation(data[1]);	
			handler.sendEmptyMessage(300);
		}
		return null;
	}

	public static Object freePass(String body) {
		String[] data = body.split("[|]");
		if(data[0].equals("fail")){
			//Something went wrong
			handler.sendEmptyMessage(402 + Integer.parseInt(data[1]));
		}else{
			//Success, reset the flags update last correct Position and update the view
			GameSession.freePass(body);
			GameSession.removeByType(4); // 4 is the free pass.
			handler.sendEmptyMessage(401);
		}
		return null;
	}

	public static Object breakMaze(String body) {
		String[] data = body.split("[|]");
		if(Integer.parseInt(data[0])==1){			
			//We are expecting this message.
			GameSession.selectedPoint = null;
			GameSession.selectingForBreaker = false;
			if(data[1].equals("fail")){
				//Something went wrong
				handler.sendEmptyMessage(202);
			}else{
				//Success, remove the points and update the view
				PathStorage.breakMaze(data[1],2);
				GameSession.removeByType(2); // 2 is the breaker.
				handler.sendEmptyMessage(201);
			}
		}else{
			//An update caused by the other players. 
			PathStorage.breakMaze(data[1],1);
		}
		return null;
	}

	public static Object binoculars(String body) {
		if(body.equals("fail")){
			//Something went wrong
			handler.sendEmptyMessage(102);
		}else{
			//Success, set the flags in the gameSession and update the UI
			GameSession.binoculars(body);
			GameSession.removeByType(1); // 1 are the binoculars.
			handler.sendEmptyMessage(101);
		}
		return null;
	}

	public static Object rocket(String body) {
		if(body.equals("fail")){
			//Something went wrong
			handler.sendEmptyMessage(502);
		}else{
			//Success, set the flags in the gameSession and update the UI
			GameSession.rocket();
			GameSession.removeByType(5); // 5 is the rocket
			handler.sendEmptyMessage(501);
		}
		return null;
	}

	public static Object expandCrowns(String body) {
		//Message contains two parts:  crown claim area info | crown claim info
		String[] crownInfo = body.split("[|]");
		GameSession.updateCrownClaimArea(crownInfo[0]);
		GameSession.updatCrownClaims(crownInfo[1]);
		SoundManager.playSound(5, 1);
		return null;
	}


	
	
	
}
