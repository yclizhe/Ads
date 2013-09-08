package com.renrengame.bigdata.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

/**
 * This class provides an implementation of an Naive Bayes Model
 * 
 * @author
 * 
 */
public class NBModel extends Reporter {

	private double _smoothing = 1.0;
	private String _label = "is_click";

	/**
	 * Data struct of a combined variable. Note that this pair is disordered
	 */
	public class VariablePair {
		protected String _x;
		protected String _y;

		public VariablePair(String x, String y) {
			_x = x;
			_y = y;
		}

		/**
		 * Override equals method, used for isContained method in ArrayList
		 */
		public boolean equals(Object o) {
			if (!(o instanceof VariablePair))
				return false;
			VariablePair var = (VariablePair) o;
			if (var._x.equals(_x)) {
				if (var._y.equals(_y))
					return true;
				else
					return false;
			} else {
				if (var._x.equals(_y) && var._y.equals(_x))
					return true;
				else
					return false;
			}
		}

		public String toString() {
			return _x + ", " + _y;
		}
	}

	/**
	 * All combined vaiables used in this model
	 */
	protected ArrayList<VariablePair> _combinedVars;

	/**
	 * Constructor of this model Step 1: Set all member variables using the
	 * reporter object Step 2: Set _combinedVars and prune the funcion3D
	 * according to XML file Step 3: Build the model
	 * 
	 * @param XMLPath
	 *            Path of the XML file
	 * @param reporter
	 *            The Reporter object based on which this model is bulit
	 */
	public NBModel(String XMLPath, Reporter reporter) {

		super(reporter);

		_combinedVars = new ArrayList<VariablePair>();

		// System.out.println(this._classTwoVarCounts.size());
		this.pruneModel(XMLPath, false);
		// System.out.println(this._classTwoVarCounts.size());

		if (this.bulidModel())
			System.out.println("NBModel built successfully!");
		else
			System.out.println("Failed to build NBModel!");
	}
	
	public NBModel(String XMLPath, Reporter reporter, boolean isHdfsFile) {

		super(reporter);

		_combinedVars = new ArrayList<VariablePair>();

		// System.out.println(this._classTwoVarCounts.size());
		this.pruneModel(XMLPath, isHdfsFile);
		// System.out.println(this._classTwoVarCounts.size());

		if (this.bulidModel())
			System.out.println("NBModel built successfully!");
		else
			System.out.println("Failed to build NBModel!");
	}

	/**
	 * usered for pruning function3Ds according to combined variables defined in
	 * XML file Note that we only prune 3d functions, namely the combinations,
	 * but not the single variables.
	 * 
	 * @param XMLPath
	 * @param isHdfsFile Indicate if the XML file is in local disk or hdfs 
	 *		   if it's true, then XML is in hdfs, local disk otherwise;
	 * @return
	 */
	boolean pruneModel(String XMLPath, boolean isHdfsFile) {
		SAXReader reader = new SAXReader();
		File file = new File(XMLPath);
		Document doc;
		try {
			if(!isHdfsFile)
				doc = reader.read(file);
			else
				doc = reader.read(HDFS_File.ReadFile(XMLPath));
			Element root = doc.getRootElement();
			Iterator<Element> it = root.element("VariablePair").elementIterator("pair");
			ArrayList<Function3D> temp = new ArrayList<Function3D>();
			while (it.hasNext()) {
				Element ele = (Element) it.next();
				String[] vars = ele.getText().split(",");
				if (vars.length != 2)
					throw new Exception();
				_combinedVars.add(new VariablePair(vars[0], vars[1]));
				Function3D func3d = this.function3DFinder(vars[0], vars[1]);
				if (func3d != null)
					temp.add(func3d);
			}
			this._classTwoVarCounts.clear();
			this._classTwoVarCounts.addAll(temp);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Reading XML file failed!");
			return false;
		} catch (Exception e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Error in XML file!");
			return false;
		}
		return true;
	}

	/**
	 * Calculate all parameters (probabilities) in this model
	 */
	private boolean bulidModel() {
		boolean re = true;

		// computing prior probabilities
		if (!this._classEmpiricalCounts.normalize(_smoothing))
			re = false;

		// computing conditional probabilities of all regular variables: P(X|C)
		// P(Y|C)
		for (Function2D fun2d : this._classOneVarCounts)
			fun2d.normalize(fun2d._y, _smoothing);

		// computing conditional probabilities of all combined variables:
		// P(XY|C)
		for (Function3D fun3d : this._classTwoVarCounts)
			if (!fun3d.normalizeCondition(_smoothing))
				re = false;

		return re;

	}

	
	
	/**
	 * Given the qVars, output all the states configurations for them. This
	 * methods could be used in intelligent targeting recommendation.
	 * 
	 * @param qVars
	 * @return
	 */
	private static ArrayList<ArrayList<String>> generateAllStates(ArrayList<Variable> qVars) {
		
		ArrayList<ArrayList<String>> curStates = new ArrayList<ArrayList<String>>();
		
		// Initialization: contains one empty state specification.
		curStates.add(new ArrayList<String>());
		
		for (int i = 0; i < qVars.size(); i++) {
			
			Variable var = qVars.get(i);
			ArrayList<String> names = var.getStates();
			
			ArrayList<ArrayList<String>> newStates = new ArrayList<ArrayList<String>>();
	
			// Expansion: The states specficiations keep growing by add the
			// values of the "var" to the end of the states.
			for(ArrayList<String> curState: curStates){
				for(String name :  names){
					ArrayList<String> clone = (ArrayList<String>) curState.clone();
					clone.add(name);
					newStates.add(clone);
				}
			}
			
			curStates = newStates;
		}
		return curStates;
	}

	/**
	 * Given the conditional states for the conditional variables, compute the
	 * ctrs based on NBModel for the query vars. The return is a map which for
	 * every states, output its ctr. Note that the order in the output
	 * Arraylist<String> is the same as that in the qVars.
	 * 
	 * @param condVars
	 * @param condStates
	 * @param qVars
	 */
	public Map<ArrayList<String>, Double> getCTRs(ArrayList<Variable> condVars,
			ArrayList<String> condStates, ArrayList<Variable> qVars) {
		 //1. generate all value assignment for qvars.
		ArrayList<ArrayList<String>> allStates =  generateAllStates(qVars);

		// 2. for every possible values assignment, compute its ctr and insert into the map
		Map<ArrayList<String>, Double> result = new HashMap<ArrayList<String>, Double>();
		
		
		ArrayList<Variable> allVars = (ArrayList<Variable>) condVars.clone();
		for(int i = 0; i<qVars.size(); i++){
			allVars.add(qVars.get(i));
		}
		
		for(ArrayList<String> states : allStates){
			ArrayList<String> allStateNames = (ArrayList<String>) condStates.clone();
			for(int i = 0; i<states.size(); i++){
				allStateNames.add(states.get(i));
			}
			double ctr = getCTR(allVars,  allStateNames);
			
			result.put(allStateNames, new Double(ctr));
		}
		
		return result;
	}

	/**
	 * Calculate the CTR of one specific instance
	 * 
	 * @param vars
	 * @param states
	 *            States of the variables in this instance
	 * @return Estimated CTR of the instance
	 */
	public double getCTR(ArrayList<Variable> vars, ArrayList<String> states) {
		// Multiply prior probs
		double p0 = _classEmpiricalCounts._cells[_classEmpiricalCounts._var
				.indexOf("0")];
		double p1 = _classEmpiricalCounts._cells[_classEmpiricalCounts._var
				.indexOf("1")];

		// Multiply conditional probs of regular varilables
		for (int i = 0; i < vars.size(); i++) {
			Function2D func2d = this.function2DFinder(_label, vars.get(i).getName());
			
			//ingore the class variable
			if(vars.get(i).equals(this._classVar))
				continue;
			
			//If thisvariable does not exist in the reporter, then ignore it. 
			if (func2d == null) {
				System.out
						.println("In getCTR(), the input variable "
								+ vars.get(i).getName()
								+ " is not in the model. Please Ignore it when calling the function.");
				continue;
			}
			Variable label = func2d._x;
			Variable val = func2d._y;
			
			//If the state of the  variable does not exist in this reporter, then ignore this variable 
			if(val.indexOf(states.get(i)) == -1)
				continue;
			
			p0 *= func2d._cells[label.indexOf("0")][val.indexOf(states.get(i))];
			p1 *= func2d._cells[label.indexOf("1")][val.indexOf(states.get(i))];
		}

		// Multiply conditional probs of combined varilables
//		for (int i = 0; i < vars.size(); i++)
//			for (int j = 0; j < vars.size(); j++) {
//				Function3D func3d = this.function3DFinder(vars.get(i).getName(),
//						vars.get(j).getName());
//
//				if (func3d == null) // if this function3D is not a combined
//									// variable in this NBModel, then continue
//					continue;
//				Variable appvar = func3d._f0._x;
//				Variable advar = func3d._f0._y;
//				if (!_combinedVars.contains(new VariablePair(appvar.getName(),
//						advar.getName())))
//					continue;
//
//				// TODO: Bugs Here
//				p0 *= func3d._f0._cells[appvar.indexOf(states.get(i))][advar
//						.indexOf(states.get(j))];
//				p1 *= func3d._f1._cells[appvar.indexOf(states.get(i))][advar
//						.indexOf(states.get(j))];
//			}
		
		for(Function3D func3d : this._classTwoVarCounts) {
			Variable appvar = func3d._f0._x;
			Variable advar = func3d._f0._y;
			int appindex = vars.indexOf(appvar);
			int adindex = vars.indexOf(advar);
			if(appindex == -1 || adindex == -1) 
				continue;
			else {
				
				//If the state of the variable does not exist in this reporter, then ignore this variable 
				if(appvar.indexOf(states.get(appindex)) == -1 || advar.indexOf(states.get(adindex)) == -1)
					continue;	
				p0 *= func3d._f0._cells[appvar.indexOf(states.get(appindex))][advar.indexOf(states.get(adindex))];
				p1 *= func3d._f1._cells[appvar.indexOf(states.get(appindex))][advar.indexOf(states.get(adindex))];
			}					
		}
		
		
		double sum = p0 + p1;

		return p1 / sum;
	}
	
	public double getCTR(NBInstance instance) {

		return getCTR(instance.getVars(), instance.getStates());
	}

//	/**
//	 * @param args
//	 * @throws IOException 
//	 */
//	public static void main(String[] args) {
//		// TODO Auto-generated method stub
//		try {
//			Reporter reporter = Reporter.getReporterTest();
//			
//			//Construct a NBModel based on reporter
//			//after this step, the reporter object is not useful any more
//			NBModel nb = new NBModel("NBModel.conf", reporter);
//			
//			//Calculate the estimated CTR
//			ArrayList<Variable> vars = new ArrayList();
//			ArrayList<String> states = new ArrayList();
//			vars.add(new Variable("A"));
//			vars.add(new Variable("B"));
//			states.add("2");
//			states.add("1");
//			double ctr = nb.getCTR(vars, states);
//			System.out.println(ctr);
//			
//			double d11 = 36.0/75.0;
//			double d12 = 7.0/38.0;
//			double d13 = 9.0/39.0;
//			double d14 = 2.0/47.0;
//			
//			double d01 = 39.0/75.0;
//			double d02 = 10.0/41.0;
//			double d03 = 14.0/42.0;
//			double d04 = 3.0/50.0;
//			
//			double p1 = d11 * d12 * d13 * d14;
//			double p0 = d01 * d02 * d03 * d04;
//			System.out.println(p1 / (p0+p1));
//		}catch (IOException e) {
//			
//		}
//			
//	}
	
	/**
	 * @param args
	 * @throws IOException 
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
//		try {
//			Reporter reporter = Reporter.getReporter("/bigdata/NBModel/LogAnalytics/2012110100-2012110123/", true);
//			//Reporter reporter = Reporter.getReporter("LogAnalytics/2012110100-2012110123/", false);
//			
//			//Construct a NBModel based on reporter
//			//after this step, the reporter object is not useful any more
//			NBModel nb = new NBModel("/bigdata/NBModel/NBModel.conf", reporter, true);
//			//NBModel nb = new NBModel("NBModel.conf", reporter, false);
//			
//			NBInstance instance = new NBInstance(true);
//			instance.parse("189bd6b8f8a24660b6adde9cf6fa9138	0	7	2	4	086df68753ef4b52a2887e42d1ba36b6	245	IPAD2,1	IOS5.1.1	1	1.0.0	unknown	WIFI	728X90	1	0	1	0	0	0	0	0	0	0	0	0	0	0	0	0	1	0	0	0	0	0	1	20	25	25	f1b6645ab834428eac6b0cd3a9ad4f49	169	170	96	1	2	4	11	2");
//			//Calculate the estimated CTR
//			
//			double ctr = nb.getCTR(instance);
//			System.out.println(ctr);
//			
//		}catch (IOException e) {
//			
//		}
		
		
		try {
		//Reporter reporter = Reporter.getReporter("/bigdata/NBModel/LogAnalytics/2012110100-2012110123/", true);
		Reporter reporter = Reporter.getReporter("LogAnalytics/2012110100-2012113023/", false);
		
		//Construct a NBModel based on reporter
		//after this step, the reporter object is not useful any more
		//NBModel nb = new NBModel("/bigdata/NBModel/NBModel.conf", reporter, true);
		NBModel nb = new NBModel("NBModel.conf", reporter, false);
		
		NBInstance instance = new NBInstance("NBModel.conf", false);
		File file = new File("/home/hadoop/NBtest");
		BufferedReader br = new BufferedReader(new FileReader(file));
		String str;
		FileWriter fw = new FileWriter(new File("/home/hadoop/result"), true);
		BufferedWriter bw = new BufferedWriter(fw);
		int count1 = 0;
		int count2 = 0;
		while( (str=br.readLine()) != null) {
			instance.parse(str);
			double ctr = nb.getCTR(instance);
			String str2 = (1-ctr) + "_" + ctr + "_" + instance.getState("is_click") ;
			fw.write(str2+"\n");
			if(instance.getState("is_click").equals("1")){
				count1++;
				if(ctr > 0.5)
					count2++;
			}
		}
		br.close();
		bw.flush();
		bw.close();
		System.out.println(count1);
		System.out.println(count2);
		//instance.parse("189bd6b8f8a24660b6adde9cf6fa9138	0	7	2	4	086df68753ef4b52a2887e42d1ba36b6	245	IPAD2,1	IOS5.1.1	1	1.0.0	unknown	WIFI	728X90	1	0	1	0	0	0	0	0	0	0	0	0	0	0	0	0	1	0	0	0	0	0	1	20	25	25	f1b6645ab834428eac6b0cd3a9ad4f49	169	170	96	1	2	4	11	2");
		//Calculate the estimated CTR
		
	}catch (IOException e) {
		
	}
		
			
	}
	
////=======
//	public static void main(String[] args) throws IOException {
//		// 1 Read DataHeader(Including variables and states)
//		DataHeader header = new DataHeader(
//				new String[] { "2012110100-2012113023/Values/part-r-00000" });
//		// 2 Extract the three categories variables from header
//		Variable classVar = header.findClassVariable();
//		ArrayList<Variable> appVars = header.findAppVariables();
//		ArrayList<Variable> adVars = header.findAdVariables();
////>>>>>>> .r88
//		
//		// constrCopyOfReporterreporter. TCopyOfReporterter constructor automatically build
//		// the containers for containing the empirical counts needed.
//		Reporter rep = new Reporter(classVar, appVars, adVars);
//
//		// add new observation to update the reporter
//		rep.addObservations("2012110100-2012113023/Statistics/part-r-00000");
//		rep.addObservations("2012110100-2012113023/Statistics/part-r-00001");
//		rep.addObservations("2012110100-2012113023/Statistics/part-r-00002");
//
//		rep.outputMIs("MI.csv", "CMI.csv");
//		rep.outputNMIs("NMI.csv", "CNMI.csv");
//		System.exit(1);
//		
//		//rep.output();
//		//rep.saveAsCSV("C:/Users/Administrator/Desktop/tmp/test");
//
//		// Construct a NBModel based on reporter and the configuration about which combining pair to consider in the model.
//		// Since the NBmodel refer to the rep and changed the content to the rep, so after this step, the reporter object is not the same as before.
//		NBModel nb = new NBModel("NBModel.conf", rep);
//
//		ArrayList<Variable> vars = new ArrayList<Variable>();
//		//vars.add(header.getVar("ad_group_id"));
//		//vars.add(header.getVar("ad_id"));
//		//vars.add(header.getVar("ad_owner"));
//		vars.add(header.getVar("ad_activity"));
//		
//		ArrayList<String> states = new ArrayList<String>();
//		//states.add("214");
//		//states.add("abb14e730d804b78be4a4d1853b1a962");
//		//states.add("892");
//		states.add("106");
//		
//		
//		ArrayList<Variable> qVars = new ArrayList<Variable>();
//		//qVars.add(header.getVar("province"));
//		qVars.add(header.getVar("Req_Time"));
//		
//		Map<ArrayList<String>, Double> result = nb.getCTRs(vars, states, qVars);
//		for(ArrayList<String> key : result.keySet()){
//			System.out.println(key + ": " + result.get(key));
//		}
//		//double ctr = nb.getCTR(vars, states);
//	}

}
