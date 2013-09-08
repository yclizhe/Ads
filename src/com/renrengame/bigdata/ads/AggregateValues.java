package com.renrengame.bigdata.ads;

//package org.apache.hadoop.examples;


import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.mapred.KeyValueTextInputFormat;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;
import org.apache.hadoop.mapreduce.lib.input.KeyValueTextInputFormat;

import com.renrengame.bigdata.exceptions.ParseSuffStatException;
import com.renrengame.bigdata.util.Variable;

/**
 * This class is supposed to aggregate several sufficient statistics. Suppose
 * that we have produce the daily sufficient statistics by using the
 * CompSuffStat mapreduce job. If you want to have the same set of statistics
 * for several days, say a week, you can use these days sufficient statistics as
 * inputs, call this AggregateStatistics as a mapreduce job. Finally you can get
 * the aggregated statistics based on which you can compute some quantities that
 * are of interests. For example, the parameters for a NB models.
 * 
 * @author hadoop
 */
public class AggregateValues {

	private static int[] _start = new int[4];

	public static void setStart(int year, int month, int day, int hour) {
		_start[0] = year;
		_start[1] = month;
		_start[2] = day;
		_start[3] = hour;
	}

	private static int[] _end = new int[4];

	public static void setEnd(int year, int month, int day, int hour) {
		_end[0] = year;
		_end[1] = month;
		_end[2] = day;
		_end[3] = hour;
	}

	public static String _pathPrefix = "/bigdata/LogAnalytics/Value/";

	public static ArrayList<Path> generatePathList() {
		GregorianCalendar startDate = new GregorianCalendar(_start[0],
				_start[1], _start[2], _start[3], 0);
		GregorianCalendar endDate = new GregorianCalendar(_end[0], _end[1],
				_end[2], _end[3], 59);
		GregorianCalendar curDate = new GregorianCalendar(_start[0], _start[1],
				_start[2], _start[3], 0);
		ArrayList<Path> paths = new ArrayList<Path>();
		while (endDate.compareTo(curDate) > 0) {
			String suffix = curDate.get(Calendar.YEAR) + "/"
			+ (curDate.get(Calendar.MONTH)<10 ? "0" : "") + curDate.get(Calendar.MONTH) + "/"
			+ (curDate.get(Calendar.DAY_OF_MONTH)<10 ? "0" : "") + curDate.get(Calendar.DAY_OF_MONTH) + "/"
			+ (curDate.get(Calendar.HOUR_OF_DAY)<10 ? "0" : "") + curDate.get(Calendar.HOUR_OF_DAY);
			paths.add(new Path(_pathPrefix + suffix));
			curDate.add(GregorianCalendar.HOUR, 1);// 下一小時
		}
		return paths;
	}

	public static String _outputPrefix = "/bigdata/LogAnalytics/Aggregate/Values/";

	public static class AggValueMapper extends
			Mapper<Object, Text, Text, Text> {

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			String[] values = value.toString().split("[\\s\\t]");
			Text thisKey = new Text(values[0].trim());
			for(int i = 1; i<values.length; i++){
				context.write(thisKey, new Text(values[i].trim()));	
			}
		}
	}

	public static class AggValueReducer extends
			Reducer<Text, Text, Text, Text> {
		public void reduce(Text key, Iterable<Text> values,
				Context context) throws IOException, InterruptedException {
			StringBuffer str = new StringBuffer();
			HashSet<String> set = new HashSet<String>();
			for(Text val : values){
				set.add(val.toString());
			}
			String[] allValues = new String[set.size()];
			set.toArray(allValues);

			for(int i = 0; i<allValues.length; i++){
				str.append(allValues[i]);
				if(i!=allValues.length-1)
					str.append(" ");
			}
			context.write(key, new Text(str.toString()));
//			int i = 0;
//			for(String val:set){
//				str.append(val);
//				if(i!=set.size()-1)
//				{
//					str.append(" ");
//					//i++;
//				}
//			}
//			context.write(key, new Text(str.toString()));

			
			//			for(Text value : values){
//     		context.write(key, new Text(value.toString()));
//			}
		}
	}

	/**
	 * Input the start time and the end time, and then aggregate the statistics
	 * among this period
	 * 
	 * @param args
	 * @throws IOException
	 * @throws ClassNotFoundException
	 * @throws InterruptedException
	 */
	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {
		String[] otherArgs = new GenericOptionsParser(args).getRemainingArgs();
		if (otherArgs.length != 8) {
			System.err.println(otherArgs.length);
			System.err
					.println("Usage: AggregateStatistics sYear(YYYY)  sMonth(MM) sDay(DD) sHour(HH) eYear(YYYY) eMonth(MM) eDay(DD) eHour(HH)");
			System.exit(2);
		}

		// Set the configuration of the mapreduce job engine.
		Configuration conf = new Configuration();
		// conf.set("mapred.job.tracker", "10.248.52.75:9001");

		// Setting about the specific job
		Job job = new Job(conf, "AggregateValues");
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);

		//K V format
		//job.setInputFormatClass(KeyValueTextInputFormat.class);
		//conf.set("key.value.separator.in.input.line", ",");
		
		// Specify the data to be aggregated
		setStart(Integer.parseInt(args[0]), Integer.parseInt(args[1]),
				Integer.parseInt(args[2]), Integer.parseInt(args[3]));
		setEnd(Integer.parseInt(args[4]), Integer.parseInt(args[5]),
				Integer.parseInt(args[6]), Integer.parseInt(args[7]));
		ArrayList<Path> paths = generatePathList();
		for (Path path : paths) {
			FileInputFormat.addInputPath(job, path);
		}

		FileOutputFormat.setOutputPath(job, new Path(_outputPrefix + "/"
				+ args[0] + args[1] + args[2] + args[3] + "-" + args[4] + args[5]
				+ args[6] + args[7]));

		job.setJarByClass(AggregateValues.class);
		job.setMapperClass(AggValueMapper.class);
		//job.setCombinerClass(AggValueReducer.class);
		job.setReducerClass(AggValueReducer.class);
		job.setNumReduceTasks(1);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
