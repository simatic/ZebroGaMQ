import math

'''
How to calculate new points for a bounding box based on one GPS points 
and a metric distance (size = width or height)
lat ^ v 
 lng < >
R = earth perimeter 6371km 

Based on the Pythagoras theorem we have a rough approximation of the distance 
between two GPS points by:
x = (lon2-lon1) * Math.cos((lat1+lat2)/2);
y = (lat2-lat1);
d = Math.sqrt(x*x + y*y) * R;

By changing the last equation we get:
(distance/R)^2 = x*x + y*y 

Setting the lng difference to zero (x=0)  
 distance/R = y = latDistance

Setting the lat difference to zero (y=0):

distance/R =  (lng2 - lng1) * Math.cos(lat1)
distance/(R* Math.cos(lat)) = lng2 - lng1 
lngDistance = distance/(R* Math.cos(lat))

Very important! The distances are in radians, they need to be 
changed to degrees to work correctly!


Since our games are played on a small basis we can assume we have a somewhat 
fixed latitude. If we calculate the distances once we can save them and use 
them later on for fast calculations. Also because the differences are very small 
we can use the Pythagoras theorem. 

http://www.movable-type.co.uk/scripts/latlong.html

'''


def gpsCoordinatesToDistance(lat1, lng1, lat2, lng2):
    x = math.radians(lng2-lng1) * math.cos(math.radians((lat1+lat2)/2));
    y = math.radians(lat2-lat1);
    return math.sqrt(x*x + y*y) * 6371000.0;


def metricDistanceToLatitudeDistance(latitude, metricDistance):
    return math.degrees(metricDistance/6371000.0) 
    
def metricDistanceToLongditudeDistance(latitude, metricDistance):    
    return math.degrees(abs(metricDistance/(6371000.0* math.cos(math.radians(latitude)))))