package com.renrengame.bigdata.util;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import com.renrengame.bigdata.util.NBModel.VariablePair;

/**
 * Abstract Class for Instance, each type of Instance used for one model should extend this class 
 * @author hadoop
 *
 */
public abstract class Instance {

	 protected ArrayList<Variable> _vars = new ArrayList<Variable>();
	 protected ArrayList<String> _states = new ArrayList<String>();
	
	 /**
	  * Key method of all instances, each type of Instance(e.g. NBInstace) must implement this method
	  * @param line
	  * @return
	  */
	 public abstract boolean parse(String line);
	 
	 public ArrayList<Variable> getVars() {
		 return _vars;
	 }
	 
	 public ArrayList<String> getStates() {
		 return _states;
	 }
	 
	 /**
	  * Get the state of one specific variable in this instance
	  * @param var The Variable
	  * @return
	  */
	 public String getState(Variable var) {
		 int index = _vars.indexOf(var);
		 if(index == -1)
			 return null;
		 else
			 return _states.get(index);
	 }
	 
	 /**
	  * Get the state of a variable with specific name in this instance
	  * @param name Name of the variable
	  * @return
	  */
	 public String getState(String name) {
		 return getState(new Variable(name));
	 }
	 
	 /**
	  * Initialize the vars list according to XML file
	  * @param XMLPath
	  * @param isHdfsFile Indicate if the XML file is in local disk or hdfs,  
	  * 		if it's true, then XML is in hdfs, local disk otherwise;
	  * @return
	  */
	 public boolean setVars(String XMLPath, boolean isHdfsFile) {
		SAXReader reader = new SAXReader();
		File file = new File(XMLPath);
		Document doc;
		try {
			if(!isHdfsFile)
				doc = reader.read(file);
			else
				doc = reader.read(HDFS_File.ReadFile(XMLPath));
			Element root = doc.getRootElement();
			Iterator<Element> it = root.element("VariableAll").elementIterator("variable");
			while (it.hasNext()) {
				Element ele = (Element) it.next();
				String name = ele.getText();
				_vars.add(new Variable(name));
			}
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("Reading XML file failed!");
			return false;
		}
		return true;		
	 }

}
