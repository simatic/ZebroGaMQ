'''
Created on Jan 18, 2012

@author: Alex
'''
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