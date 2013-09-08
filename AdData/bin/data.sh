#!/bin/bash

rm  /home/hadoop/AdData/shw
hour=0
for date in 24 25
do
	while [ $hour -le 23 ]
	do
		if [ $hour -le 9 ]
			then hour=0$hour
		fi

		datapath="/media/PENDRIVE/NB_ALL/data/201211$date"_shw/$hour
		pattern="|201211$date""$hour""020[0-9]|"
		cd $datapath
		gzip -dc `ls -1 | head -n 1` | grep -a "$pattern" >> /home/hadoop/AdData/shw
		echo $date-$hour is Done!
		hour=`expr $hour + 1`

	done
	hour=0
done
