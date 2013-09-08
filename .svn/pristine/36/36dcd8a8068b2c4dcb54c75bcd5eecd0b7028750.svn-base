package com.renrengame.bigdata.ads;

//package org.apache.hadoop.examples;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.DoubleWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.renrengame.bigdata.exceptions.ParseSuffStatException;

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
public class AggregateStatistics {

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

	public static String _pathPrefix = "/bigdata/LogAnalytics/Statistics/";

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

	public static String _outputPrefix = "/bigdata/LogAnalytics/Aggregate/";

	public static class AggStatMapper extends
			Mapper<Object, Text, Text, DoubleWritable> {

		private Text word = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {

			// Split one line into two parts delimited by \t. It is supposed
			// that the first part is the variable=value combination; the second
			// part is the count of such assignment in some day(s).
			String[] keyvalue = value.toString().split("\t");
			try {
				if (keyvalue.length != 2) {
					throw new ParseSuffStatException();
				}
			} catch (ParseSuffStatException e) {
				System.out
						.println("Either key(Variable Assignment Combination) or value(count) is missing");
			}

			word.set(keyvalue[0]);
			DoubleWritable count = new DoubleWritable(Double.parseDouble(keyvalue[1])+0.0);
			context.write(word, count);
		}
	}

	public static class AggStatReducer extends
			Reducer<Text, DoubleWritable, Text, DoubleWritable> {
		private DoubleWritable result = new DoubleWritable();

		public void reduce(Text key, Iterable<DoubleWritable> values,
				Context context) throws IOException, InterruptedException {
			double sum = 0;
			for (DoubleWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
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
		Job job = new Job(conf, "AggregateStatistics");
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(DoubleWritable.class);

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

		job.setJarByClass(AggregateStatistics.class);
		job.setMapperClass(AggStatMapper.class);
		job.setCombinerClass(AggStatReducer.class);
		job.setReducerClass(AggStatReducer.class);
		job.setNumReduceTasks(3);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
