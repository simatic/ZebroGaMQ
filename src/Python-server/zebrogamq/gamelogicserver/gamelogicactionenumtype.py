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
class ActionEnumException(exceptions.Exception):
    pass

class GameLogicActionEnumeration:
    def __init__(self, name, lowerActionNb, upperActionNb, kindNb, enumList):
        self.actionName = name
        self.lowerActionNumber = lowerActionNb
        self.upperActionNumber = upperActionNb
        self.kindNumber = kindNb
        lookup = { }
        reverseLookup = { }
        function = { }
        i = 0
        f = None
        uniqueNames = [ ]
        uniqueValues = [ ]
        for x in enumList:
            if type(x) == types.TupleType and len(x) == 2:
                x, f = x
            else:
                raise ActionEnumException, "enum is a name and a function only: " + x
            if type(x) != types.StringType:
                raise ActionEnumException, "enum name is not a string: " + x
            if type(f) != types.FunctionType:
                raise ActionEnumException, "enum function is not a function: " + f
            args, varargs, varkwargs, defaults = inspect.getargspec(f)
            if x in uniqueNames:
                raise ActionEnumException, "enum name is not unique: " + x
            if i in uniqueValues:
                raise ActionEnumException, "enum value is not unique for " + x
            uniqueNames.append(x)
            uniqueValues.append(i)
            lookup[x] = i
            reverseLookup[i] = x
            function[i] = f
            i = i + 1
        self.lookup = lookup
        self.reverseLookup = reverseLookup
        self.function = function
    def __getattr__(self, attr):
        if not self.lookup.has_key(attr):
            raise AttributeError
        return self.lookup[attr]
    def whatis(self, value):
        return self.reverseLookup[value]
    def whichfunction(self, value):
        return self.function[value]
    def execute(self, index, state, header, body):
        return self.function[index](state, header, body)
