"""
 ZebroGaMQ: Communication Middleware for Mobile Gaming
 Copyright: Copyright (C) 2009-2012
 Contact: denis.conan@telecom-sudparis.eu, michel.simatic@telecom-sudparis.eu

 This library is free software; you can redistribute it and/or
 modify it under the terms of the GNU Lesser General Public
 License as published by the Free Software Foundation; either
 version 3 of the License, or any later version.

 This library is distributed in the hope that it will be useful,
 but WITHOUT ANY WARRANTY; without even the implied warranty of
 MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 Lesser General Public License for more details.

 You should have received a copy of the GNU Lesser General Public
 License along with this library; if not, write to the Free Software
 Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307
 USA

 Developer(s): Denis Conan, Gabriel Adgeg
"""

import types, string, exceptions, inspect

# adapted from Python Recipe 67107: Enums for Python
# available at  http://code.activestate.com/recipes/67107-enums-for-python/
class ListOfActionsEnumException(exceptions.Exception):
    pass

class ListOfActionsEnumeration:
    def __init__(self, name, enumList):
        self.actionName = name
        lookup = { }
        reverseLookup = { }
        enuminstances = { }
        i = 0
        f = None
        uniqueNames = [ ]
        uniqueValues = [ ]
        for x in enumList:
            if type(x) == types.TupleType and len(x) == 2:
                x, e = x
            else:
                raise ListOfActionsEnumException, "enum is a name and a function only: " + x
            if type(x) != types.StringType:
                raise ListOfActionsEnumException, "enum name is not a string: " + x
            if type(e) != types.InstanceType:
                raise ListOfActionsEnumException, "enum instance is not an instance: " + e
            if x in uniqueNames:
                raise ListOfActionsEnumException, "enum name is not unique: " + x
            if i in uniqueValues:
                raise ListOfActionsEnumException, "enum value is not unique for " + x
            uniqueNames.append(x)
            uniqueValues.append(i)
            lookup[x] = i
            reverseLookup[i] = x
            enuminstances[i] = e
            i = i + 1
        self.lookup = lookup
        self.reverseLookup = reverseLookup
        self.enuminstances = enuminstances
    def __getattr__(self, attr):
        if not self.lookup.has_key(attr):
            raise AttributeError
        return self.lookup[attr]
    def whatis(self, value):
        return self.reverseLookup[value]
    def whichenuminstance(self, value):
        return self.enuminstances[value]
    def execute(self, index1, index2, state, header, body):
        return self.enuminstances[index1].execute(index2, state, header, body)
