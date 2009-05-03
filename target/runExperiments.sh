#!/bin/sh

rm *.m
# ./runServer.sh
ATTEMPTS=5

java -jar iMario.jar -m iMario1 -ag ForwardAgent -vlx 0 -vly 0 -an ${ATTEMPTS} -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario2 -ag ForwardJumpingAgent -vlx 330 -vly 0 -an ${ATTEMPTS} -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &
java -jar iMario.jar -m iMario3 -ag RandomAgent -vlx 660 -vly 0 -an ${ATTEMPTS} -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &

I=1
LIM=3
PORT=4242
VLX=0
VLY=320

while [ $I -le $LIM ]
do
	# echo $I $PORT $VLX $VLY	$(($I+3))
	java -jar iMario.jar -m iMario$(($I+3)) -ag ServerAgent:${PORT} -vlx ${VLX} -vly ${VLY} -an ${ATTEMPTS} -ld 5 -pw on -echo on -gv off -tc off -vis on -maxFPS on -ewf on -vaot on &	
	VLX=$(($VLX+330))
	PORT=$(($PORT+1))
	I=$(($I+1))
done

sleep 3 #Wait until all servers are ready. You may adjust this paramer.
./runClient.sh

jobs -lp
wait

sleep 1
./catResults.sh