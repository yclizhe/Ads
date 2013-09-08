#!/bin/bash

hadoop fs -rmr /data/output
pig \
-param clk_path=/data/clk \
-param shw_path=/data/shw \
-param ads_path=/data/t_ads_v3 \
-param apps_path=/data/t_apps \
-param output=/data/output \
-param delimiter="|" \
 apps_ada_shw_clk.pig

hadoop fs -rmr /data/output/_*

hadoop fs -rmr /data/value
hadoop jar ValueCounter.jar /data/output /data/value
hadoop fs -rmr /data/value/_*

hadoop fs -rmr /data/stats
hadoop jar CompSuffStat_NB.jar /data/output /data/stats
hadoop fs -rmr /data/stats/_*
