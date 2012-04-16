"""
aMazing! Geolocalized multiplayer game for Android devices.
Conceived and realized within the course "Mixed Reality Games for 
Mobile Devices" at Fraunhofer FIT.

http://www.fit.fraunhofer.de/de/fb/cscw/mixed-reality.html
http://www.totem-games.org/?q=aMazing

Copyright (C) 2012  Alexander Hermans, Tianjiao Wang

Contact: 
alexander.hermans0@gmail.com, tianjiao.wang@rwth-aachen.de,
richard.wetzel@fit.fraunhofer.de, lisa.blum@fit.fraunhofer.de, 
denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

This program is free software: you can redistribute it and/or modify
it under the terms of the GNU General Public License as published by
the Free Software Foundation, either version 3 of the License, or
(at your option) any later version.

This program is distributed in the hope that it will be useful,
but WITHOUT ANY WARRANTY; without even the implied warranty of
MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
GNU General Public License for more details.

You should have received a copy of the GNU General Public License
along with this program.  If not, see <http://www.gnu.org/licenses/>.

Developer(s): Alexander Hermans, Tianjiao Wang
ZebroGaMQ:  Denis Conan, Gabriel Adgeg 
"""
        
        
class MazeCorner:
    def __init__(self, lat, lng, connected, generated, hidden, E6, id):
        "Creates a new MazeCorner based on latitude and longitude. Connected is True if the maze is simply continued by this point. Generated is True if the point is created by an interpolation. E6 is set to true if lat and lng are given in E6 format."        
        if E6:
            self.lng = float(lng) / float(10 ** 6)
            self.lat = float(lat) / float(10 ** 6)
        else:    
            self.lng = lng
            self.lat = lat
        self.connected = connected
        self.generated = generated
        self.hidden = hidden
        self.id = id

    
    def toString(self):
        return repr(self.id) + "/" + repr(int(self.lat * (10 ** 6))) + "/"  + repr(int(self.lng * (10 ** 6))) + "/" + repr(1 if self.connected == True else 0) + "/" + repr(1 if self.generated == True else 0) + "/" + repr(1 if self.hidden == True else 0)
        