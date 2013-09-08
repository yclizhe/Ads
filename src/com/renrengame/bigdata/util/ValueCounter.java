package com.renrengame.bigdata.util;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.StringTokenizer;

import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.hadoop.mapreduce.Mapper.Context;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.GenericOptionsParser;


public class ValueCounter {

	
	
	static class DataMapper  extends Mapper<LongWritable, Text, Text, Text> {
		public static String[] _AttributeSet = { "shw::ssnid",  "is_click",  "log_version",  "Req_Day",  "Req_Time",  
			"app_id",  "cp_id",  "terminal_type",  "os",  "sdk_type",  "sdk_version",  "oper_company",  "net_type",  
			"ad_size",  "is_henping",  "is_broke",  "app_type_1",  "app_type_2",  "app_type_3",  "app_type_4",  "app_type_5",  "app_type_6",  
			"app_type_7",  "app_type_8",  "app_type_9",  "app_type_10",  "app_type_11",  "app_type_12",  "app_type_13",  "app_type_14", 
			"app_type_15",  "app_type_16",  "app_type_17",  "app_type_18",  "app_type_19",  "app_type_20",  "app_type_21",  
			"province",  "app_createTime",  "app_updateTime",  "ad_id",  "ad_group_id",  "ad_owner",  "ad_activity",  "showType", 
			"deviceType",  "clickAction",  "ad_createTime",  "ad_updateTime"};
		//public static ArrayList<HashSet<String>> _ValueSet = new ArrayList<HashSet<String>>();
		public void map(LongWritable key, Text value, Context context) 
				throws IOException, InterruptedException {
			String line = value.toString();
			String [] words = line.split("\t");
			
			//start from 1 instead of 0, skip shw::ssnid
			for(int attrIndex = 1; attrIndex<words.length; attrIndex++) {
				String val = words[attrIndex];
				if(val.isEmpty() || val.equalsIgnoreCase("NULL") || val.equalsIgnoreCase("\\N") ||val.equalsIgnoreCase("unknown"))
					context.write(new Text(_AttributeSet[attrIndex]), new Text("MISSING"));
				else
					context.write(new Text(_AttributeSet[attrIndex]), new Text(val));
			}
		}
//		protected void setup(Context context){
//			for(int i=0; i<_AttributeSet.length; i++) {
//				_ValueSet.add(new HashSet<String>());
//			}
//		}
	}
	
	static class DataReducer extends Reducer<Text,Text, Text, Text >{
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException {
			StringBuffer str = new StringBuffer();
			HashSet<String> set = new HashSet<String>();
			for(Text value :values)
				set.add(value.toString());
			int i = 0;
			for(String val:set){
				str.append(val);
				if(i!=set.size()-1)
					str.append(" ");
			}
			
			context.write(key, new Text(str.toString()));
		}
	}
	
	static class DataCombiner extends Reducer<Text,Text, Text, Text >{
		public void reduce(Text key, Iterable<Text> values, Context context) 
				throws IOException, InterruptedException {
			HashSet<String> set = new HashSet<String>();
			for(Text value :values)
				if(set.add(value.toString()))
					context.write(key, value);		
		}
	}
	
	public static void main (String [] args) throws Exception {
		String[] otherArgs = new GenericOptionsParser(args).getRemainingArgs();
		if (otherArgs.length != 2) {
			System.err.println(otherArgs.length);
			System.err.println("Usage: ValueCounter  <in> <out>");
			System.exit(2);
		}
		
		Configuration conf = new Configuration();
		Job job = new Job(conf, "ValueCounter"); 
		job.setJarByClass(ValueCounter.class);
		FileInputFormat.addInputPath(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapperClass(DataMapper.class);
		job.setReducerClass(DataReducer.class);
		job.setCombinerClass(DataCombiner.class);
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		job.setNumReduceTasks(5);
		
		System.exit(job.waitForCompletion(true) ? 0 : 1);
		
	}
}
