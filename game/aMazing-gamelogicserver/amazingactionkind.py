from net.totem.gamelogicserver.gamelogicactionenumtype import GameLogicActionEnumeration
from myprotocol import computeGPSCoordinates, playerIsReady, itemRequest, playerLocationAccurate, teleportItem, drawItem, freePass, breakMaze, binoculars, rocket


def doNothing(state, header, body):
    pass

AmazingActionKind = GameLogicActionEnumeration("amazingActionKind", 102, 0, 1000,
    [("sendGPSCoordinates", computeGPSCoordinates),
     ("proximityWarning", doNothing),
     ("proximityViolation", doNothing),
     ("proximityViolationCorrected", doNothing),
     ("playerReady", playerIsReady),
     ("locationIsAccurate", playerLocationAccurate),
     ("itemRequest", itemRequest),
     ("itemUpdate", doNothing),
     ("updateCrownClaims", doNothing),
     ("finishGame", doNothing),
     ("teleportItem",teleportItem), 
     ("drawItem", drawItem),
     ("freePass", freePass),
     ("breakMaze", breakMaze),
     ("binoculars", binoculars),
     ("rocket", rocket),
     ("expandCrowns", doNothing)
     ])
