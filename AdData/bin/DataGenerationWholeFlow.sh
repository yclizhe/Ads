#!/bin/bash

if [ $# -lt 3 ]; then
	echo This is for automatically generate statistics and values for one day
	echo The intermediate data are discarded for saving storage
	echo Insufficient Number of parameters
	echo Usage: DataGenerationWholeFlow year month day
	exit 0
fi

year=$1
month=$2
day=$3

hour=0
nexthour=1

while [ $hour -le 23 ]
	do
		inputhour=$hour
		if [ $hour -le 9 ]
			then inputhour=0$hour
		fi

		inputnexthour=$nexthour
		if [ $nexthour -le 9 ]
			then inputnexthour=0$nexthour
		fi
		
		hadoop fs -rmr /bigdata/LogAnalytics/Data/$year/$month/$day/$inputhour
		pig \
		-param clk_path="/gads/gads-scribed-report0/gads_val_clk/hours/$year/$month/$day/{$inputhour,$inputnexthour}" \
		-param shw_path=/gads/gads-scribed-report0/gads_val_shw/hours/$year/$month/$day/$inputhour \
		-param ads_path=/bigdata/LogAnalytics/t_ads_v3 \
		-param apps_path=/bigdata/LogAnalytics/t_apps \
		-param output=/bigdata/LogAnalytics/Data/$year/$month/$day/$inputhour/ \
		-param delimiter="|" \
		 apps_ada_shw_clk.pig

		#Compute values for this hour and remove unnecessary output to avoid later trouble
		hadoop fs -rmr /bigdata/LogAnalytics/Value/$year/$month/$day/$inputhour
		hadoop jar ValueCounter.jar /bigdata/LogAnalytics/Data/$year/$month/$day/$inputhour/ /bigdata/LogAnalytics/Value/$year/$month/$day/$inputhour/
		hadoop fs -rmr /bigdata/LogAnalytics/Data/$year/$month/$day/$inputhour/_logs
		hadoop fs -rmr /bigdata/LogAnalytics/Value/$year/$month/$day/$inputhour/_logs
		hadoop fs -rmr /bigdata/LogAnalytics/Value/$year/$month/$day/$inputhour/_SUCCESS

		#Start to compute statistics for the current hour=$inputhour
		hadoop fs -rmr /bigdata/LogAnalytics/Statistics/$year/$month/$day/$inputhour
		hadoop jar CompSuffStat_NB.jar /bigdata/LogAnalytics/Data/$year/$month/$day/$inputhour/ /bigdata/LogAnalytics/Statistics/$year/$month/$day/$inputhour/
		hadoop fs -rmr /bigdata/LogAnalytics/Statistics/$year/$month/$day/$inputhour/_logs
		hadoop fs -rmr /bigdata/LogAnalytics/Statistics/$year/$month/$day/$inputhour/_SUCCESS


		#remove the intermediate data
		hadoop fs -rmr /bigdata/LogAnalytics/Data/$year/$month/$day/$inputhour
		
		hour=`expr $hour + 1`
		nexthour=`expr $nexthour + 1`

	done