"""
aMazing! Geolocalized multiplayer game for Android devices.
Conceived and realized within the course "Mixed Reality Games for 
Mobile Devices" at Fraunhofer FIT (http://www.fit.fraunhofer.de).
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

idCounter = 0

class Item(object):
    '''
    classdocs
    '''

    def __init__(self, lat, lng, type, extra):
        '''
        Constructor
        '''
        global idCounter
        self.id = idCounter
        idCounter = idCounter + 1
        self.lat = lat
        self.lng = lng
        self.type = type
        self.extra = extra
        self.owner = 0
        self.closestPoint = 10000 #Far away 
        self.crownClaimRadius = 0
        
        
    def toString(self):
        return repr(self.id) +"|"+ repr(self.lat) + "|"+ repr(self.lng) + "|"+ repr(self.type) + "|"+ repr(self.extra)