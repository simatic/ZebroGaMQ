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

'''
Created on Dec 15, 2011

This is not really a quad tree as normally described. 
It also stores points within the nodes. So it is not perfectly 
suited for organizing a grid. However it is easy to use when no
pre known field is known where the data will be placed on. Because
of the values in the nodes the tree will react somewhat funny when 
deleting values, but it works just fine. 

TODO maybe do some reshuffling of the tree when it is clear that the 
tree is out of balance OR replace everything with a tree spanning over 
a fixed field. 



@author: Alex
'''

class QuadTree(object):
    '''
    A Quadtree to store GPS points. 
    '''
    def __init__(self):
        '''
        Constructor
        '''
        print "QuadTree created."
        self.rootNode = None
        self.itemCount = 0
        
    def clear(self):
        self.rootNode = None
        
    def getAllItems(self, outputList):
        self.getAllItemsFromNode(self.rootNode, outputList)

    
    def getAllItemsFromNode(self, node, outputList):
        if node != None: 
            if node.data != None:
                outputList.append(node.data)
            if node.quad1 != None:
                self.getAllItemsFromNode(node.quad1, outputList)
            if node.quad2 != None:
                self.getAllItemsFromNode(node.quad2, outputList)
            if node.quad3 != None:
                self.getAllItemsFromNode(node.quad3, outputList)
            if node.quad4 != None:
                self.getAllItemsFromNode(node.quad4, outputList)
    
    def remove(self, data):
        return self.findValueEntry(self.rootNode, data, True)
        
    def contains(self, data):
        return self.findValueEntry(self.rootNode, data, False)

    def findValueEntry(self, node, data, removeWhenFound):
        if node != None and node.data is data:
            if removeWhenFound:
                node.data = None
                self.itemCount = self.itemCount - 1
            return True
        else:
            result = False
            if (node.quad1 != None):
                result = result or self.findValueEntry(node.quad1, data, removeWhenFound)
            if (node.quad2 != None and result == False):
                result = result or self.findValueEntry(node.quad2, data, removeWhenFound)
            if (node.quad3 != None and result == False):
                result = result or self.findValueEntry(node.quad3, data, removeWhenFound)
            if (node.quad4 != None and result == False):
                result = result or self.findValueEntry(node.quad4, data, removeWhenFound)
            return result
        
    def add(self, x, y, data):
        self.itemCount = self.itemCount + 1
        self.rootNode = self.addInNode(self.rootNode, x, y, data)
        
    def addInNode(self, node, x, y, data):
        if node == None:
            return TreeNode(x, y, data)
        
        elif node.data == None:
            node.data = data
            return node
        elif x < node.x and y < node.y:
            node.quad3 = self.addInNode(node.quad3, x, y, data)
        elif x < node.x and y >= node.y:
            node.quad2 = self.addInNode(node.quad2, x, y, data)
        elif x >= node.x and y < node.y:
            node.quad4 = self.addInNode(node.quad4, x, y, data)
        elif x >= node.x and y >= node.y:
            node.quad1 = self.addInNode(node.quad1, x, y, data)
        return node
    
    def findInArea(self, xMin, xMax, yMin, yMax, resultList):
        self.find(self.rootNode, xMin, xMax, yMin, yMax, resultList)
    
    def find(self, node, xMin, xMax, yMin, yMax, resultList):
        if node != None:
            if node.x >= xMin and node.x <= xMax and node.y >= yMin and node.y <= yMax:
                if node.data != None:
                    resultList.append(node.data)
        
            if xMin < node.x and yMin < node.y :
                self.find(node.quad3, xMin, xMax, yMin, yMax, resultList)              
            
            if xMin < node.x and yMax >= node.y :
                self.find(node.quad2, xMin, xMax, yMin, yMax, resultList)
                
            if xMax >= node.x and yMin < node.y :
                self.find(node.quad4, xMin, xMax, yMin, yMax, resultList)
            
            if xMax >= node.x and yMax >= node.y :
                self.find(node.quad1, xMin, xMax, yMin, yMax, resultList)
            
        
class TreeNode(object):

    def __init__(self, x, y, value):
        '''
        Constructor
        '''
        self.x = x
        self.y = y
        self.data = value
        self.quad1 = None
        self.quad2 = None
        self.quad3 = None 
        self.quad4 = None
    
    
    
        
