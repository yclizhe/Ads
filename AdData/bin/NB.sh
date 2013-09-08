#! /bin/bash

hadoop fs -rmr /data/NB/result
hadoop fs -rm /data/NB/NBModel.conf

hadoop fs -put ./NBModel.conf /data/NB

hadoop jar NBJob.jar /data/NB/2012110100-2012112323/ /data/NB/data2425 /data/NB/result

rm ../final/result

touch ../final/result 

hadoop fs -rmr /data/NB/result/_*

hadoop fs -get /data/NB/result/part* - >> ../final/result

wait

java -cp $CLASSPATH:./NBJob.jar com.renrengame.bigdata.util.AUC
