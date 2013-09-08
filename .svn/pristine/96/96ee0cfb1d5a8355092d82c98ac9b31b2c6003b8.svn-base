package com.renrengame.bigdata.ads;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.FileSystem;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;

import com.renrengame.bigdata.util.ReadVariables;
import com.renrengame.bigdata.exceptions.ParseRecordException;
import com.renrengame.bigdata.exceptions.ParseSuffStatException;

;

/*
 * This class is responsible for computing the sufficient statistics from the daily (may not be) date.
 */
public class CompSuffStat_NB {

	// private static String input =
	// "hdfs://10.248.52.75:9000/user/hadoop/TaoChen/NB/EveryDayData";
	// private static String output =
	// "hdfs://10.248.52.75:9000/user/hadoop/TaoChen/NB/output";
	// private static String jobtracker = "10.248.52.75:9001";
	private static String jobname = "CompSuffStat_NB";

	public static class CompSuffStatNBMapper extends
			Mapper<Object, Text, Text, IntWritable> {

		private final static IntWritable one = new IntWritable(1);
		private Text word = new Text();

		public void map(Object key, Text value, Context context)
				throws IOException, InterruptedException {
			/**
			 * The variable name array constructed to store all these 50 values.
			 * Among them, the first is the ssnid which is not used, but just
			 * for identify the request. The second is the class label, then
			 * followed by 39 X variables and 9 Y variables.
			 */
			String[] variableNames = ReadVariables.readVariables4NB();
			String[] values = value.toString().split("\t");
			try {
				if (values.length != 49) {
					throw new ParseSuffStatException();
				}
			} catch (ParseSuffStatException e) {
				System.out.println(values.length);
				for (int i = 0; i < values.length; i++)
					System.out.println(values[i]);
				System.out
						.println("There are no exactly 49 values in the record");
			}

			/*
			 * Elicit these three string (some of them might be empty.) from
			 * these record (Array).
			 */
			String classString = variableNames[1] + "=" + values[1];
			String XString = null;
			String YString = null;
			// In the current variableName array. The 1st is the ssnid, the
			// 2nd is the class; Starting from the 3 - 40 are X variables. 41
			// - 49 are Y variables. Here i is the index for X, where "i = 2"
			// means do not consider the X variable in the key; j is the index
			// for Y where "j=40" means that do not consider Y variables in the
			// key.
			for (int i = 2; i <= 40; i++) {
				if (i == 2)
					XString = "";
				else {
					if (values[i - 1].isEmpty()
							|| values[i - 1].equalsIgnoreCase("NULL")
							|| values[i - 1].equalsIgnoreCase("\\N")
							|| values[i - 1].equalsIgnoreCase("unknown"))
						values[i - 1] = "MISSING";
					XString = " " + variableNames[i - 1] + "=" + values[i - 1];
				}

				for (int j = 40; j <= 49; j++) {
					if (j == 40)
						YString = "";
					else {
						if (values[j - 1].isEmpty()
								|| values[j - 1].equalsIgnoreCase("NULL")
								|| values[j - 1].equalsIgnoreCase("\\N")
								|| values[j - 1].equalsIgnoreCase("unknown"))
							values[j - 1] = "MISSING";
						YString = " " + variableNames[j - 1] + "="
								+ values[j - 1];
					}
					// When the key [classString XString Y String] is ready,
					// write (key, one) to the context.
					word.set(classString + XString + YString);
					context.write(word, one);
				}

			}
		}
	}

	/**
	 * The function of CompSuffStatReducer is to add up all the count for the
	 * same value assignment of a variable combination. And then output them.
	 * 
	 * @author hadoop
	 * 
	 */
	public static class CompSuffStatNBReducer extends
			Reducer<Text, IntWritable, Text, IntWritable> {
		private IntWritable result = new IntWritable();

		public void reduce(Text key, Iterable<IntWritable> values,
				Context context) throws IOException, InterruptedException {
			int sum = 0;
			for (IntWritable val : values) {
				sum += val.get();
			}
			result.set(sum);
			context.write(key, result);
		}
	}

	public static void main(String[] args) throws IOException,
			ClassNotFoundException, InterruptedException {

		// Properties pro = System.getProperties();
		// System.out.print(pro);

		String[] otherArgs = new GenericOptionsParser(args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println(otherArgs.length);
			System.err.println("Usage: CompSuffStat_NB <in> <out>");
			System.exit(2);
		}

		// Set the configuration of the mapreduce job engine.
		Configuration conf = new Configuration();
		// conf.set("mapred.job.tracker", jobtracker);

		// Setting about the specific job
		Job job = new Job(conf, jobname);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(IntWritable.class);
		job.setNumReduceTasks(6);
		
		String input = args[0];
		String output = args[1];

		// Specify input output
		FileInputFormat.addInputPath(job, new Path(input));
		Path outputPath = new Path(output);
		FileOutputFormat.setOutputPath(job, outputPath);

		// If the output already exists, delete it;
		// FileSystem fs = FileSystem.get(conf);
		job.setJarByClass(CompSuffStat_NB.class);
		job.setMapperClass(CompSuffStatNBMapper.class);
		job.setCombinerClass(CompSuffStatNBReducer.class);
		job.setReducerClass(CompSuffStatNBReducer.class);

		System.exit(job.waitForCompletion(true) ? 0 : 1);
	}

}
