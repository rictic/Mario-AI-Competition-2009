__author__ = "Sergey Karakovskiy, sergey at idsia fullstop ch"
__date__ = "$Apr 30, 2009 1:53:54 PM$"

import numpy
    
powsof2 = (1, 2, 4, 8, 16, 32, 64, 128, 256)

def decode(estate):
    """
    decodes the encoded state estate, which is a string of 61 chars
    """
    dstate = numpy.empty(shape = (22, 22), dtype = numpy.int)
    for i in range(22):
        for j in range(22):
            dstate[i, j] = 2
    row = 0
    col = 0
    totalBitsDecoded = 0
    for i in range(len(estate)):
        cur_char = estate[i]
        for j in range(8):
            totalBitsDecoded += 1
            if (col > 11 * 2 - 1):
                row += 1
                col = 0
            if (powsof2[j] & ord(cur_char) != 0):
                dstate[row, col] = 1
            else:
                dstate[row, col] = 0
            col += 1
            if (totalBitsDecoded == 484):
                break
    print "\ntotalBitsDecoded = ", totalBitsDecoded
    return dstate;


def extractObservation(data):
    """
     parse the array of strings and return array 22 by 22 of doubles
    """
    obsLength = 487
    levelScene = numpy.empty(shape = (22, 22), dtype = numpy.int)
    data = data.split(' ')
    if (data[0] == 'FIT'):
        status = int(data[1])
        distance = float(data[2])
        timeLeft = int(data[3])
        marioMode = int(data[4])
        coins = int(data[5])
#        print "S: %s, F: %s " % (data[1], data[2])
        #print "status %s, dist %s, timeleft %s, mmode %s, coins %s" % (status, distance, timeLeft, marioMode, coins) 
        return status, distance, timeLeft, marioMode, coins
    elif(data[0] == 'O'):
        mayMarioJump = (data[1] == 'true')
        isMarioOnGround = (data[2] == 'true')
#        assert len(data) == obsLength, "Error in data size given %d! Required: %d \n data: %s " % (len(data), obsLength, data)
        k = 0
        for i in range(22):
            for j in range(22):
                levelScene[i, j] = int(data[k + 3])
                k += 1
        return (mayMarioJump, isMarioOnGround, levelScene)
    elif(data[0][0] == 'E'): #Encoded observation, fastTCP mode, have to be decoded
#        assert len(data) == eobsLength
        mayMarioJump = (data[0][1] == '1')
        isMarioOnGround = (data[0][2] == '1')
        levelScene = decode(data[0][3:64])
        for i in range(22):
            for j in range(22):
                print levelScene[i, j], ' ',
            print 
        
#        enemies = decode(data[0][64:])
        return (mayMarioJump, isMarioOnGround, levelScene)
    else:
        raise "Wrong format or corrupted observation..."
