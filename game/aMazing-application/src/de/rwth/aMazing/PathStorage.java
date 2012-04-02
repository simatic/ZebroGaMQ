package de.rwth.aMazing;

import java.util.ArrayList;
/**
 * This class stores the paths of both players.
 * 
 * @author Alex
 * 
 */

public class PathStorage {

	private static boolean[] newPoints = { false, false };
	private static boolean[] recreatePath = { false, false };
	private static ArrayList<MazeCorner> cornerListP1 = new ArrayList<MazeCorner>();
	private static ArrayList<MazeCorner> cornerListP2 = new ArrayList<MazeCorner>();
	private static int nextIDP1 = 0;

	
	public static void reset(){
		newPoints = new boolean[2];
		newPoints[0]= false;
		newPoints[1] = false; 
		recreatePath = new boolean[2];
		recreatePath[0] = false;
		recreatePath[1] = false; 
		cornerListP1.clear();
		cornerListP2.clear();
		nextIDP1 = 0;
	}
	
	public static void addNewMazeCorner(MazeCorner corner, int player) {
		if (player == 1) {
			newPoints[0] = true;
			cornerListP1.add(corner);
		} else if (player == 2) {
			newPoints[1] = true;
			cornerListP2.add(corner);

		}

	}

	public static ArrayList<MazeCorner> getCornerList(int player) {
		if (player == 1) {
			return cornerListP1;
		} else if (player == 2) {
			return cornerListP2;
		} else {
			return null;
		}
	}

	public static boolean needUpdate(int player) {
		return newPoints[player - 1];
	}

	public static void newPointsProcessed(int player) {
		newPoints[player - 1] = false;
	}

	public static boolean recreatePath(int player) {
		return recreatePath[player - 1];
	}

	public static void setRecreatePath(boolean recreatePath, int player) {
		PathStorage.recreatePath[player - 1] = recreatePath;

		// If we get a message that the path was recreated, we can also assume
		// new points have been processed.
		if (!recreatePath)
			newPoints[player - 1] = false;
	}

	// Returns the next ID, these are unique and should never be set by hand.
	public static int getNewID() {
		nextIDP1++;
		return (nextIDP1 - 1);
	}

	public static void addMazeCornersP2(String coordinates) {
		String[] mazeCorners = coordinates.split("[*]");
		for (int i = 0; i < mazeCorners.length; i++) {
			cornerListP2.add(MazeCorner.fromString(mazeCorners[i], true));
		}
		newPoints[1] = true;
	}

	public static void removeProximityViolations(String coordinates) {
		String[] deletionList = coordinates.split("[*]");
		for (int i = 0; i < deletionList.length; i++) {
			removeMazeCornerByID(Integer.parseInt(deletionList[i]), 1);
		}
		recreatePath[1] = true;
	}

	private static void removeMazeCornerByID(int ID, int player) {
		if (player == 1) {
			cornerListP1.get(ID).hidden = true;
			if (cornerListP1.size() > ID + 1) {
				cornerListP1.get(ID + 1).generated = false;
				cornerListP1.get(ID + 1).connected = false;
			}
			if (ID > 0)
				cornerListP1.get(ID - 1).generated = false;
		} else if (player == 2) {
			cornerListP2.get(ID).hidden = true;
			if (cornerListP2.size() > ID + 1) {
				cornerListP2.get(ID + 1).generated = false;
				cornerListP2.get(ID + 1).connected = false;
			}
			if (ID > 0)
				cornerListP2.get(ID - 1).generated = false;
		}
	}

	public static void addAll(ArrayList<MazeCorner> tempStorage, int player) {
		if (player == 1) {
			newPoints[0] = true;
			for (int i = 0; i < tempStorage.size(); i++) {
				if (tempStorage.get(i).hidden == false) {
					cornerListP1.add(tempStorage.get(i));
				}
			}
		} else if (player == 2) {
			newPoints[1] = true;
			for (int i = 0; i < tempStorage.size(); i++) {
				if (tempStorage.get(i).hidden == false) {
					cornerListP2.add(tempStorage.get(i));
				}
			}

		}

	}

	public static void breakMaze(String cornerIDs, int player) {
		String[] ids = cornerIDs.split("[*]");
		// Here we ignore the first element since it is always -1. This is done
		// to make sure the list isn't empty!
		int ID = 0;
		for (int i = 1; i < ids.length; i++) {
			ID = Integer.parseInt(ids[i]);
			if (player == 1) {
				cornerListP1.get(ID).hidden = true;
				if (cornerListP1.size() > ID + 1) {
					cornerListP1.get(ID + 1).generated = false;
					cornerListP1.get(ID + 1).connected = false;
				} else {
					// There previous point was the last one, we cannot hide it,
					// otherwise the path will not be drawn correctly.
					cornerListP1.get(ID).hidden = false;
					cornerListP1.get(ID).generated = false;
					cornerListP1.get(ID).connected = false;
				}
				if (ID > 0)
					cornerListP1.get(ID - 1).generated = false;
			} else if (player == 2) {
				cornerListP2.get(ID).hidden = true;
				if (cornerListP2.size() > ID + 1) {
					cornerListP2.get(ID + 1).generated = false;
					cornerListP2.get(ID + 1).connected = false;
				} else {
					// There previous point was the last one, we cannot hide it,
					// otherwise the path will not be drawn correctly.
					cornerListP2.get(ID).hidden = false;
					cornerListP2.get(ID).generated = false;
					cornerListP2.get(ID).connected = false;
				}
				if (ID > 0)
					cornerListP2.get(ID - 1).generated = false;
			}
		}
		recreatePath[player - 1] = true;
	}
}