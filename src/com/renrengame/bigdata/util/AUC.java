package com.renrengame.bigdata.util;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.StringTokenizer;

import weka.classifiers.evaluation.NominalPrediction;
import weka.classifiers.evaluation.ThresholdCurve;
import weka.core.FastVector;



public class AUC {

		public static void main(String[] args) throws Exception{
			File file = new File("/home/hadoop/result3");
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line;
			FastVector fv = new FastVector();
			while((line = br.readLine())!=null){
				//StringTokenizer st = new StringTokenizer(line, "\t");
				//st.nextToken();
				//String re = st.nextToken();
				String [] line2 = line.split("\t");
				StringTokenizer st2 = new StringTokenizer(line2[1], "_");
				double[] pred = new double[2];
				double actual;
				//System.out.println(st2.nextToken());
				//System.out.println(st2.nextToken());
				//System.out.println(st2.nextToken());
				String p0 = st2.nextToken();
				String p1 = st2.nextToken();
				String ac = st2.nextToken();
		
				pred[0] = Double.parseDouble(p0);
				pred[1] = Double.parseDouble(p1);
				actual = Double.parseDouble(ac);
				NominalPrediction np = new NominalPrediction(actual, pred);
				fv.add(np);
			}
			ThresholdCurve tc = new ThresholdCurve();
			System.out.println(ThresholdCurve.getROCArea(tc.getCurve(fv, 0)));		
		}
}
