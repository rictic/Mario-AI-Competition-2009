#!/bin/sh

I=1
LIM=6
OUT=iMarioResult.m
while [ $I -le $LIM ]
do
	echo	processing iMario${I}.m ...
	cat iMario${I}.m >> ${OUT}
	I=$(($I+1))
done
echo "results saved to ${OUT}" 