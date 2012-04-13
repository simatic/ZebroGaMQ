/**
 * aMazing! Geolocalized multiplayer game for Android devices.
 * Conceived and realized within the course "Mixed Reality Games for 
 * Mobile Devices" at Fraunhofer FIT.
 * 
 * http://www.fit.fraunhofer.de/de/fb/cscw/mixed-reality.html
 * http://www.totem-games.org/?q=aMazing
 * 
 * Copyright (C) 2012  Alexander Hermans, Tianjiao Wang
 * 
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

package de.rwth.aMazing;

import java.util.ArrayList;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.Point;
import android.graphics.Rect;

import com.google.android.maps.GeoPoint;
import com.google.android.maps.MapView;
import com.google.android.maps.Overlay;
import com.google.android.maps.Projection;

class PathOverlay extends Overlay {
	private float rad;
	private Paint neutralPaint, p1Paint, p1ClaimPaint, p2Paint, p2ClaimPaint, returnPaint, selectedItemPaint;

	private GeoPoint lastReferencePoint = null;
	private Rect lastViewRect = null;
	// Stores the two paths of the players.
	private Path[] path = new Path[2];
	// Stores the last point in the path of the players.
	private Point[] previousPoint = new Point[2];
	// Stores the index in the mazeCorner list of the last points for the
	// players.
	private int[] previousPointIndex = new int[2];

	
	public PathOverlay(float RADIUS) {
		this.rad = RADIUS;

		neutralPaint = new Paint();
		neutralPaint.setARGB(64, 0, 255, 0);
		neutralPaint.setStrokeWidth(5);
		neutralPaint.setStrokeCap(Paint.Cap.ROUND);
		neutralPaint.setAntiAlias(true);
		neutralPaint.setDither(false);
		neutralPaint.setStyle(Paint.Style.FILL);

		p1Paint = new Paint();
		p1Paint.setARGB(128, 0, 0, 255);
		p1Paint.setStrokeWidth(5);
		p1Paint.setStrokeCap(Paint.Cap.ROUND);
		p1Paint.setAntiAlias(true);
		p1Paint.setDither(false);
		p1Paint.setStyle(Paint.Style.STROKE);

		p1ClaimPaint = new Paint();
		p1ClaimPaint.setARGB(64, 0, 0, 255);
		p1ClaimPaint.setStrokeWidth(5);
		p1ClaimPaint.setStrokeCap(Paint.Cap.ROUND);
		p1ClaimPaint.setAntiAlias(true);
		p1ClaimPaint.setDither(false);
		p1ClaimPaint.setStyle(Paint.Style.FILL);
		
		p2Paint = new Paint();
		p2Paint.setARGB(128, 255, 0, 0);
		p2Paint.setStrokeWidth(5);
		p2Paint.setStrokeCap(Paint.Cap.ROUND);
		p2Paint.setAntiAlias(true);
		p2Paint.setDither(false);
		p2Paint.setStyle(Paint.Style.STROKE);
		
		p2ClaimPaint = new Paint();
		p2ClaimPaint.setARGB(64, 255, 0, 0);
		p2ClaimPaint.setStrokeWidth(5);
		p2ClaimPaint.setStrokeCap(Paint.Cap.ROUND);
		p2ClaimPaint.setAntiAlias(true);
		p2ClaimPaint.setDither(false);
		p2ClaimPaint.setStyle(Paint.Style.FILL);
		
		
		returnPaint = new Paint();
		returnPaint.setARGB(200, 255, 0, 0);
		returnPaint.setStrokeWidth(5);
		returnPaint.setStrokeCap(Paint.Cap.ROUND);
		returnPaint.setAntiAlias(true);
		returnPaint.setDither(false);
		returnPaint.setStyle(Paint.Style.STROKE);
		
		
		selectedItemPaint = new Paint();
		selectedItemPaint.setARGB(64, 255, 127, 0);
		selectedItemPaint.setStrokeWidth(5);
		selectedItemPaint.setStrokeCap(Paint.Cap.ROUND);
		selectedItemPaint.setAntiAlias(true);
		selectedItemPaint.setDither(false);
		selectedItemPaint.setStyle(Paint.Style.FILL);
		
		

		path[0] = new Path();
		path[1] = new Path();
	}
	
	@Override
	public boolean onTap(GeoPoint p, MapView mapView) {
		if(GameSession.selectingForBreaker ==true){
			GameSession.selectedPoint = p;
			return true;	
		}else{
			return false; 
		} 
	}

	public void draw(Canvas canvas, MapView mapView, boolean shadow) {
		if (shadow) {
			// This is a run just for shadows. Ignore it and return.
			return;
		}
		final Projection projection = mapView.getProjection();
		final Rect viewRect = getMapViewRectangle(mapView);
		final GeoPoint referencePoint = projection.fromPixels(0, 0);

		boolean newProjection = !viewRect.equals(lastViewRect)
				|| !referencePoint.equals(lastReferencePoint);

		if (!newProjection) {
			//Old projection still is valid. Only recreate or update paths if needed.
			for (int p = 1; p <= 2; p++) {
				if (PathStorage.recreatePath(p) || (PathStorage.needUpdate(p) && previousPoint[p-1] == null )) {
					// We need to recreate the whole path.
					path[p - 1].reset();

					ArrayList<MazeCorner> cornerList = PathStorage.getCornerList(p);
					MazeCorner currentMazeCorner;
					GeoPoint currentGeoPoint;
					Point currentP = null, previousP = null;
					
					if (cornerList.size() > 1) {
						
						currentMazeCorner = cornerList.get(0);
						currentGeoPoint = currentMazeCorner.geoPoint;
						previousP = projection.toPixels(currentGeoPoint, null);
						path[p - 1].moveTo(previousP.x, previousP.y);
						boolean firstHidden = currentMazeCorner.hidden;
						for (int i = 1; i < cornerList.size(); i++) {
							currentMazeCorner = cornerList.get(i);
							if (currentMazeCorner.hidden || currentMazeCorner.generated) continue;
							currentGeoPoint = currentMazeCorner.geoPoint;
							currentP = projection.toPixels(currentGeoPoint,
									null);
							
							if(currentMazeCorner.connected){
								path[p - 1].moveTo(currentP.x, currentP.y);
								path[p - 1].lineTo(previousP.x, previousP.y);
							}else{
								if(!firstHidden) path[p - 1].lineTo(previousP.x+0.1f, previousP.y+0.1f);
								path[p - 1].moveTo(currentP.x, currentP.y);
								firstHidden = false; //We do not need this anymore
							}
							previousP = currentP;
						}
						PathStorage.setRecreatePath(false, p);
					}
					previousPoint[p - 1] = currentP;
					previousPointIndex[p - 1] = cornerList.size();

					

				} else if (PathStorage.needUpdate(p)) {
					// just add new points to path and update
					ArrayList<MazeCorner> cornerList = PathStorage.getCornerList(p);
					MazeCorner currentMazeCorner;
					GeoPoint currentGeoPoint;
					Point currentPoint;
					for (int i = previousPointIndex[p - 1]; i < cornerList
							.size(); i++) {
						currentMazeCorner = cornerList.get(i);
						if (currentMazeCorner.hidden || currentMazeCorner.generated) continue;
						currentGeoPoint = currentMazeCorner.geoPoint;
						currentPoint = projection.toPixels(currentGeoPoint,
								null);

						
						if(currentMazeCorner.connected){ 
							path[p - 1].moveTo(currentPoint.x, currentPoint.y);
							path[p - 1].lineTo(previousPoint[p - 1].x,previousPoint[p - 1].y);
						}else{
							path[p - 1].lineTo(previousPoint[p - 1].x+0.1f, previousPoint[p - 1].y+0.1f);
							path[p - 1].moveTo(currentPoint.x, currentPoint.y);
						}
						
						previousPoint[p - 1] = currentPoint;
					}
					previousPointIndex[p - 1] = cornerList.size();

					PathStorage.newPointsProcessed(p);

				}
			}
		} else {
			// We need to recreate both paths, with a new projection.
			path[0].reset();
			path[1].reset();

			for (int p = 1; p <= 2; p++) {
				ArrayList<MazeCorner> cornerList = PathStorage.getCornerList(p);
				MazeCorner currentMazeCorner;
				GeoPoint currentGeoPoint;
				Point currentP = null, previousP = null;

				if (cornerList.size() > 1) {
					currentMazeCorner = cornerList.get(0);
					currentGeoPoint = currentMazeCorner.geoPoint;
					previousP = projection.toPixels(currentGeoPoint, null);
					path[p - 1].moveTo(previousP.x, previousP.y);
					boolean firstHidden = currentMazeCorner.hidden;
					
					for (int i = 1; i < cornerList.size(); i++) {
						currentMazeCorner = cornerList.get(i);

						if (currentMazeCorner.hidden || currentMazeCorner.generated) continue;
						currentGeoPoint = currentMazeCorner.geoPoint;
						currentP = projection.toPixels(currentGeoPoint, null);

						
						if(currentMazeCorner.connected){
							path[p - 1].moveTo(currentP.x, currentP.y);
							path[p - 1].lineTo(previousP.x, previousP.y);
						}else{
							if(!firstHidden) path[p - 1].lineTo(previousP.x+0.1f, previousP.y+0.1f);
							path[p - 1].moveTo(currentP.x, currentP.y);
							firstHidden = false; //We do not need this anymore
						}
						
						previousP = currentP;
					}
					PathStorage.setRecreatePath(false, p);
				}
				previousPoint[p - 1] = currentP;
				previousPointIndex[p - 1] = cornerList.size();

				
			}
		}

		lastReferencePoint = referencePoint;
		lastViewRect = viewRect;

		// draw the two paths here.
		if (path[0] != null)
			canvas.drawPath(path[0], p1Paint);
		if (path[1] != null)
			canvas.drawPath(path[1], p2Paint);
		if (GameSession.violationInAction){
			drawCircle(canvas, neutralPaint, GameSession.lastCorrectLocation, projection, rad);
			drawCircle(canvas, returnPaint , GameSession.lastCorrectLocation, projection, rad*4f);
		}
		if (GameSession.selectingForBreaker && GameSession.selectedPoint!= null) {
			drawCircle(canvas, selectedItemPaint, GameSession.selectedPoint, projection, GameSession.breakerRadius);
		}
		
		final ArrayList<Item> items = GameSession.getItems();
		for(int i=0; i<GameSession.numberOfDisplayedItems(); i++){
			Item current = items.get(i);
			if(current.equals(GameSession.selectedItem)){
				//This item is selected during an item action. We take a different colour.
				if(current.type==0){
					//crown
					drawCircle(canvas, selectedItemPaint, current.getPoint(), projection, current.crownClaimRadius);
				}else{
					//item	
					drawCircle(canvas, selectedItemPaint, current.getPoint(), projection, GameSession.itemPickupRadius);
				}
			}else{
				if(current.type==0){
					//crown
					if(current.owner ==0){
						drawCircle(canvas, neutralPaint, current.getPoint(), projection, current.crownClaimRadius);
					}else if(current.owner == 1){
						drawCircle(canvas, p1ClaimPaint, current.getPoint(), projection, current.crownClaimRadius);
					}else{
						drawCircle(canvas, p2ClaimPaint, current.getPoint(), projection, current.crownClaimRadius);
					}
				}else{
					//item
					drawCircle(canvas, neutralPaint, current.getPoint(), projection, GameSession.itemPickupRadius);
				}
			}
		}
	}

	private void drawCircle(Canvas canvas, Paint paint,
			GeoPoint currentGeoPoint, Projection proj, float rad) {
		Point current = proj.toPixels(currentGeoPoint, null);
		// Calculate the correct radius. See
		// http://stackoverflow.com/questions/2077054/how-to-compute-a-radius-around-a-point-in-an-android-mapview
		float radius = (float) (proj.metersToEquatorPixels(rad) * (1 / Math
				.cos(Math.toRadians(currentGeoPoint.getLatitudeE6() / 1000000))));

		canvas.drawCircle(current.x, current.y, radius, paint);
	}

	private Rect getMapViewRectangle(MapView mV) {
		int w = mV.getLongitudeSpan();
		int h = mV.getLatitudeSpan();
		int cx = mV.getMapCenter().getLongitudeE6();
		int cy = mV.getMapCenter().getLatitudeE6();
		return new Rect(cx - w / 2, cy - h / 2, cx + w / 2, cy + h / 2);
	}

}
