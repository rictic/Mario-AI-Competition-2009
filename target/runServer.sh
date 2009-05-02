#!/bin/sh
### java -jar iMario.jar -ag ServerAgent:4224 -an 10 -ld 5 -pw on -echo on
java -jar iMario.jar -m iMario1 -ag ForwardAgent -vlx 0 -vly 0 -an 20 -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario2 -ag ForwardJumpingAgent -vlx 330 -vly 0 -an 20 -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario3 -ag RandomAgent -vlx 660 -vly 0 -an 20 -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario4 -ag ServerAgent:4242 -vlx 0 -vly 330 -an 20 -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario5 -ag ServerAgent:4243 -vlx 330 -vly 330 -an 20 -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario6 -ag ServerAgent:4244 -vlx 660 -vly 330 -an 20 -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &