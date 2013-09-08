#!/bin/bash
for date in 24 25
do
	pattern="|201211$date[0-9][0-9]020[0,1,2]|"
	echo $date `grep -a "$pattern" shw | wc -l`
done
