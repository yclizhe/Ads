#!/bin/bash
hour=0
for date in 24 25
do
	while [ $hour -le 23 ]
	do
		if [ $hour -le 9 ]
			then hour=0$hour
		fi
		pattern="|201211$date""$hour""020[0,1,2]|"
		echo $date$hour  `grep -a "$pattern" shw | wc -l`
		hour=`expr $hour + 1`
	done
	hour=0
done
