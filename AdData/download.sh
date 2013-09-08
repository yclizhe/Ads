#! /bin/bash

rm -r output
rm -r final
rm -r stats
rm -r value

hadoop fs -get /data/output .
hadoop fs -get /data/stats .
hadoop fs -get /data/value .


mkdir final

cat output/par* >> final/output
cat stats/par* >> final/stats
cat value/par* >> final/value

cd final

grep "6	6	" output >> data24
grep "6	7	" output >> data25

cat data24 >> DATA
cat data25 >> DATA

rm output

cut -f 2,5-9,11-42,44,46-49 DATA >> data

java -Xmx1440m weka.core.converters.CSVLoader -H -N 1-42  -F '	' -B 1000000 data >> ad.arff
