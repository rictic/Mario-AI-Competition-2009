__author__="Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ ="$Apr 30, 2009 1:53:54 PM$"

import numpy
    
def parseObservation(data):
    """
    parse the array of strings and return array 22 by 22 of doubles
    """
    levelScene = numpy.empty(shape = (22,22), dtype = numpy.int)
    data = data.split(' ')
    mayMarioJump = (data[0] == 'true')
    isMarioOnGround = (data[1] == 'true')
    assert len(data) == 486, "Error in data size given% %d! Required: 486" % len(data)

    k = 0
    for i in range(22):
        for j in range(22):
            levelScene[i,j] = int(data[k + 2])
            k += 1
    return mayMarioJump, isMarioOnGround, levelScene

if __name__ != "__main__":
    print "Module DataAdaptor loaded.";