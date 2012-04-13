'''
Created on Dec 9, 2011

@author: Alex
'''
        
        
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
        