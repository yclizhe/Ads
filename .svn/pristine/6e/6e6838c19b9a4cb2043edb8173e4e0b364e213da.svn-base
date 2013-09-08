package com.renrengame.bigdata.util;


import java.awt.Font;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.UnsupportedEncodingException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;

import org.dom4j.Document;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.category.DefaultCategoryDataset;

/**
 * Visualize the data file statistics in user's preferred way
 * @author Zhe Li
 *
 */
public class StatisticViewer extends JFrame implements ActionListener{
	
	
	String LABEL_STRING; //static string for Label Attribute
	String SERVER_HOST; //10.248.52.81
	int PORT;
	Document XML_DOCUMENT;
	int CHART_HEIGHT;
	int CHART_WIDTH;
	int CTR_HEIGHT;
	int CTR_WIDTH;
	
	
	private JCheckBox m_ClassCheckBox;
	private JComboBox m_FieldXComboBox;
	private JComboBox m_FieldYComboBox;
	private JButton m_StartButton;
	private JPanel m_DataViewerPanel;
	private JPanel m_DataOutputPanel;
	private JScrollPane m_DataViewerScroll;
	private JScrollPane m_DataOutput;
	private JTextArea m_DataTextArea;
	private JTextField m_StartTimeTextField;
	private JTextField m_EndTimeTextField;
	private JTextField m_PathTextField;
	private JButton m_SaveAsButton;
	private JCheckBox m_SortCheckBox;
	private JComboBox m_ShowComboBox;
	
	private String m_CurStartTime;
	private String m_CurEndTime;
	private Reporter m_CurReporter;
	   
	/**
	 * Constructor,set the layout of this panel
	 */
	public StatisticViewer() {
		
		this.componentsGenerator();
		try {
			this.setConfig();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			JOptionPane.showMessageDialog(this, "Error in Setting Configure!", "Error", JOptionPane.ERROR_MESSAGE);
		}
		
		
		this.setLayout(new GridBagLayout());
		GridBagConstraints gc = new GridBagConstraints();
		
		gc.fill = GridBagConstraints.HORIZONTAL;
		gc.insets = new Insets(50,50,9,5);
		gc.gridx = 0;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		this.add(new JLabel("StartTime:"), gc);	
		gc.insets = new Insets(1,50,9,5);
		gc.gridy++;
		this.add(new JLabel("EndTime:"), gc);
		gc.gridy++;
		this.add(new JLabel("ClassLabel:"), gc);
		gc.gridy++;
		this.add(new JLabel("FieldX:"), gc);
		gc.gridy++;
		this.add(new JLabel("FieldY:"), gc);
		gc.gridy++;
		this.add(new JLabel("Sort:"), gc);
		gc.gridy++;
		this.add(new JLabel("Show:"), gc);
		gc.gridy++;
		
		gc.insets = new Insets(50,5,9,5);
		gc.gridx = 1;
		gc.gridy = 0;
		this.add(m_StartTimeTextField, gc);
		gc.gridy++;
		
		gc.insets = new Insets(1,5,9,5);
		this.add(m_EndTimeTextField, gc);
		gc.gridy++;
		this.add(m_ClassCheckBox, gc);
		gc.gridy++;
		this.add(m_FieldXComboBox, gc);
		gc.gridy++;
		this.add(m_FieldYComboBox, gc);
		gc.gridy++;
		this.add(m_SortCheckBox, gc);
		gc.gridy++;
		this.add(m_ShowComboBox, gc);
		gc.gridy++;
		
		gc.insets = new Insets(1,50,9,5);
		gc.gridx = 0;
		gc.gridy = 7;
		gc.gridwidth = 2;
		gc.gridheight = 1;
		this.add(m_StartButton, gc);	
	
		gc.insets = new Insets(1,50,10,5);
		gc.gridx = 0;
		gc.gridy = 8;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		this.add(m_SaveAsButton, gc);
		
		gc.insets = new Insets(1,5,10,5);
		gc.gridx = 1;
		gc.gridy = 8;
		gc.gridwidth = 1;
		gc.gridheight = 1;
		this.add(m_PathTextField, gc);
		
		gc.insets = new Insets(50,10,10,50);
		gc.weightx = 1;
		gc.weighty = 0;
		gc.fill = GridBagConstraints.BOTH;
		gc.gridx = 2;
		gc.gridy = 0;
		gc.gridwidth = 1;
		gc.gridheight = 9;
		this.add(m_DataOutput, gc);	
		gc.gridy++;
		m_DataOutput.setBorder(BorderFactory.createTitledBorder("OutputArea"));
		
		
		
		gc.insets = new Insets(10,50,50,50);
		gc.weightx = 0;
		gc.weighty = 1;
		gc.gridx = 0;
		gc.gridy = 9;
		gc.gridwidth = 3;
		gc.gridheight = 1;
		this.add(m_DataViewerScroll, gc);	
		gc.gridy++;
		m_DataViewerScroll.setBorder(BorderFactory.createTitledBorder("ChartArea"));
		
	}
	
	/**
	 * Initialize all the components is this panel
	 */
	private void componentsGenerator() {
		m_ClassCheckBox = new JCheckBox();
		m_FieldXComboBox = new JComboBox();
		m_FieldYComboBox = new JComboBox();
		m_FieldXComboBox.addItem("none");
		m_FieldXComboBox.addItem("ssnid");
		m_FieldXComboBox.addItem("log_version");
		m_FieldXComboBox.addItem("Req_Day");
		m_FieldXComboBox.addItem("Req_Time");
		m_FieldXComboBox.addItem("app_id");
		m_FieldXComboBox.addItem("cp_id");
		m_FieldXComboBox.addItem("uuid2");
		m_FieldXComboBox.addItem("terminal_type");
		m_FieldXComboBox.addItem("os");
		m_FieldXComboBox.addItem("sdk_type");
		m_FieldXComboBox.addItem("sdk_version");
		m_FieldXComboBox.addItem("oper_company");
		m_FieldXComboBox.addItem("net_type");
		m_FieldXComboBox.addItem("ad_size");
		m_FieldXComboBox.addItem("is_henping");
		m_FieldXComboBox.addItem("is_broke");
		m_FieldXComboBox.addItem("app_type_报刊杂志");
		m_FieldXComboBox.addItem("app_type_财务");
		m_FieldXComboBox.addItem("app_type_参考");
		m_FieldXComboBox.addItem("app_type_导航");
		m_FieldXComboBox.addItem("app_type_工具");
		m_FieldXComboBox.addItem("app_type_健康健美");
		m_FieldXComboBox.addItem("app_type_教育");
		m_FieldXComboBox.addItem("app_type_旅行");
		m_FieldXComboBox.addItem("app_type_商业");
		m_FieldXComboBox.addItem("app_type_社交");
		m_FieldXComboBox.addItem("app_type_摄影与录像");
		m_FieldXComboBox.addItem("app_type_生活");
		m_FieldXComboBox.addItem("app_type_体育");
		m_FieldXComboBox.addItem("app_type_天气");
		m_FieldXComboBox.addItem("app_type_图书");
		m_FieldXComboBox.addItem("app_type_效率");
		m_FieldXComboBox.addItem("app_type_新闻");
		m_FieldXComboBox.addItem("app_type_医疗");
		m_FieldXComboBox.addItem("app_type_音乐");
		m_FieldXComboBox.addItem("app_type_游戏");
		m_FieldXComboBox.addItem("app_type_娱乐");
		m_FieldXComboBox.addItem("province");
		m_FieldXComboBox.addItem("app_createTime");
		m_FieldXComboBox.addItem("app_updateTime");

		m_FieldYComboBox.addItem("none");
		m_FieldYComboBox.addItem("ad_id");
		m_FieldYComboBox.addItem("ad_group_id");
		m_FieldYComboBox.addItem("ad_owner");
		m_FieldYComboBox.addItem("ad_activity");
		m_FieldYComboBox.addItem("showType");
		m_FieldYComboBox.addItem("deviceType");
		m_FieldYComboBox.addItem("clickAction");
		m_FieldYComboBox.addItem("ad_createTime");
		m_FieldYComboBox.addItem("ad_updateTime");
		
		m_ShowComboBox = new JComboBox();
		m_ShowComboBox.addItem("all");
		m_ShowComboBox.addItem("top10");
		m_ShowComboBox.addItem("top30");
		m_ShowComboBox.addItem("top50");
		m_ShowComboBox.addItem("bottom10");
		m_ShowComboBox.addItem("bottom30");
		m_ShowComboBox.addItem("bottom50");
		
		m_SortCheckBox = new JCheckBox();
		
		//for testing
//		m_FieldXComboBox.addItem("A");
//		m_FieldYComboBox.addItem("B");

		m_StartButton = new JButton("Start");
//		m_DataTable = new JTable(){
//			   @Override
//			   public boolean isCellEditable(int row, int column) {
//				  return (column == 0) ? false : true;
//			   }	
//		};
		m_DataTextArea = new JTextArea();
		m_DataTextArea.setEditable(false);
		
		m_DataOutputPanel = new JPanel();
		m_DataOutputPanel.add(m_DataTextArea);
		m_DataOutput = new JScrollPane(m_DataOutputPanel);
		m_DataViewerPanel = new JPanel();
		m_DataViewerScroll = new JScrollPane(m_DataViewerPanel);
		
		m_StartButton.addActionListener(this);
		
		m_StartTimeTextField = new JTextField();
		m_EndTimeTextField = new JTextField();
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH");
		m_StartTimeTextField.setText(sdf.format(date));
		m_EndTimeTextField.setText(sdf.format(date));
		
		m_PathTextField = new JTextField();
		m_PathTextField.setEditable(false);
		m_SaveAsButton  = new JButton("Save As..");
		m_SaveAsButton.setEnabled(false);

		m_SaveAsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO Auto-generated method stub
				JFileChooser jc = new JFileChooser();
				jc.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				if(jc.showSaveDialog(m_SaveAsButton.getParent()) == JFileChooser.APPROVE_OPTION) {
					String path = jc.getSelectedFile().getAbsolutePath();
					m_PathTextField.setText(path);
					try {
						m_CurReporter.saveAsCSV(path);
						JOptionPane.showMessageDialog(m_SaveAsButton.getParent(), "Save as CSV successfully!", "Error", JOptionPane.INFORMATION_MESSAGE);
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(m_SaveAsButton.getParent(), "Save as CSV failed!", "Error", JOptionPane.ERROR_MESSAGE);
					} catch (UnsupportedEncodingException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						JOptionPane.showMessageDialog(m_SaveAsButton.getParent(), "Save as CSV failed!", "Error", JOptionPane.ERROR_MESSAGE);
					}
				}
			}
			
		});
		
	}
	
	private void setConfig() throws Exception{

		SAXReader reader = new SAXReader();
		File file = new File("StatisticViewer.conf");
		XML_DOCUMENT = reader.read(file);
		Element root = XML_DOCUMENT.getRootElement();
		PORT = Integer.parseInt(root.element("string").element("port").getText());
		LABEL_STRING = root.element("string").element("label").getText();
		SERVER_HOST  = root.element("string").element("host").getText();
		CHART_HEIGHT = Integer.parseInt(root.element("chart").element("height").getText());
		CHART_WIDTH = Integer.parseInt(root.element("chart").element("width").getText());
		CTR_HEIGHT = Integer.parseInt(root.element("chart").element("ctrheigth").getText());
		CTR_WIDTH = Integer.parseInt(root.element("chart").element("ctrwidth").getText());
		
	}
	
	/**
	 * The value of a field should be mapped from xml file or not
	 * @param name The name of the field
	 * @return
	 */
	private boolean isMapping(String name) {
		Element root = XML_DOCUMENT.getRootElement();
		if(root.element("valuemapping").elementByID(name) == null)
			return false;
		else
			return true;
	}
	
	/**
	 * Get the value of one field with specific key from StatisticViewer.conf file
	 * @param name The name of the field
	 * @param key
	 * @return
	 */
	private String mapValue(String name, String key) {
		if(isMapping(name)) {
			Element root = XML_DOCUMENT.getRootElement();
			if(root.element("valuemapping").elementByID(name).elementByID(key) == null)
				return key;
			else
				return root.element("valuemapping").elementByID(name).elementByID(key).getText();
		}
		else
			return key;
	}
	
	/**
	 * Get the key of one field with specific value from StatisticViewer.conf file
	 * @param name The name of the field
	 * @param value
	 * @return
	 */
	private String mapKey(String name, String value ) {
		Element root = XML_DOCUMENT.getRootElement();
		List<Element> list = root.element("valuemapping").elementByID(name).elements("data");
		list.indexOf(value);
		for(Element element : list) 
			if(element.getText().equals(value))
				return element.attributeValue("ID");
		return value;
	}
	
	/**
	 * Set the contents in m_DataTextArea
	 * @param reporter the r to be printed
	 */
	protected void updateResult(Reporter report) {
		StringBuffer strbuffer = new StringBuffer();
		strbuffer.append(report._classEmpiricalCounts);
		for (Function2D func : report._classOneVarCounts) {
			strbuffer.append(func);
			strbuffer.append("MI is: " + func.computeMI());
		}

		for (Function3D func : report._classTwoVarCounts) {
			strbuffer.append(func);
			strbuffer.append("CMI is: " + func.computeCMI());
		}
		strbuffer.append("\n");
		m_DataTextArea.setText(strbuffer.toString());
		m_DataOutputPanel.removeAll();
		m_DataOutputPanel.add(m_DataTextArea);
	}
	
	/**
	 * Set the contents in m_DataTextArea
	 * @param reporter the r to be printed
	 */
	protected void updateResult(JPanel panel) {
//		DefaultTableModel tm = (DefaultTableModel) m_DataTable.getModel();
//		String[][] data = {{"1","2","3"},{"4","5","6"}};
//		String[] header = {"1","2", "3"};
//		tm.setDataVector(data, header);
//		for(int i=0; i<100; i++) {
//			String[] c = {"1","2","3"};
//			tm.addRow(c);
//		}
		m_DataOutputPanel.removeAll();
		m_DataOutputPanel.add(panel);
		m_DataOutputPanel.updateUI();
	}

	/**
	 * update m_DataViewerPanel based on function1D in this reporter
	 * @param reporter The Reporter which contains functions
	 * @param name Name of the variable of which user choose to show distribution 
	 * @param labelflag Flag indicating if the variable is label or not
	 */
	protected void updateViewerPanel1D(Reporter reporter, String name, boolean labelflag) {
		
		if(name.startsWith("app_type")) 
			name = "app_type_" + mapKey("app_type", name.split("_")[2]);
		
		Function1D function1d;
		if(labelflag) 
			function1d = reporter._classEmpiricalCounts;
		
		else {
			Function2D function2d = reporter.function2DFinder(reporter._classVar.getName(), name);
			if(function2d == null) {
				JOptionPane.showMessageDialog(this, "No data returned", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			function1d = function2d.sumOut(function2d._x);
		}
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		ArrayList<String> statesList = function1d._var.getStates();
		double[] countList = function1d._cells;
		for(int i=0; i<statesList.size(); i++) 
			dataset.addValue(countList[function1d._var.indexOf(statesList.get(i))], mapValue(name, statesList.get(i)), "");
		
		JFreeChart chart = ChartFactory.createBarChart3D(name, name, "count", getSortedDataSet(dataset), PlotOrientation.VERTICAL, true, true, true);
		convertEncoding(chart);
		ChartPanel cp =  new ChartPanel(chart, CHART_WIDTH, CHART_HEIGHT, 
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
				true, true, true, true, true, true, true);
		m_DataViewerPanel.removeAll();
		m_DataViewerPanel.add(cp);	
		m_DataViewerPanel.updateUI();
		updateResult(reporter);
	}
	
	/**
	 * update m_DataViewerPanel based on function1D in this reporter
	 * @param reporter The Reporter which contains functions
	 * @param name1 Name of the first variable of which user choose to show distribution 
	 * @param name2 Name of the second variable of which user choose to show distribution 
	 * @param labelflag Flag indicating if the first variable is label or not
	 */
	protected void updateViewerPanel2D(Reporter reporter, String name1, String name2, boolean labelflag) {
		if(labelflag) {
			
			if(name2.startsWith("app_type")) 
				name2 = "app_type_" + mapKey("app_type", name2.split("_")[2]);
			
			DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[3];
			dataset[0] = new DefaultCategoryDataset();
			dataset[1] = new DefaultCategoryDataset();
			dataset[2] = new DefaultCategoryDataset();
			Function2D funtion2d= reporter.function2DFinder(name1, name2);
			if(funtion2d == null) {
				JOptionPane.showMessageDialog(this, "No data returned", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			double[][] countList = funtion2d._cells;
			Variable x = funtion2d._x;
			Variable y = funtion2d._y;
			ArrayList<String> statesList1 =  funtion2d._x.getStates();
			ArrayList<String> statesList2 =  funtion2d._y.getStates();
			for(int i=0; i<statesList1.size(); i++)
				for(int j=0; j<statesList2.size(); j++)
					dataset[i].addValue(countList[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))], 
							mapValue(name2, statesList2.get(j)), 
							"");
			
			for(int j=0; j<statesList2.size(); j++) {
				double shw, clk, ctr;
				clk = countList[x.indexOf("1")][y.indexOf(statesList2.get(j))];
				shw = countList[x.indexOf("0")][y.indexOf(statesList2.get(j))] + clk;
				if(shw == 0)
					ctr = 0;
				else
					ctr = clk/shw;
				dataset[2].addValue(ctr * 1000, 
						mapValue(name2, statesList2.get(j)), 
						"");
			}
			JFreeChart chart0 = ChartFactory.createBarChart3D(LABEL_STRING+"="+statesList1.get(0), name2, "count", getSortedDataSet(dataset[0]), PlotOrientation.VERTICAL, true, true, true);
			JFreeChart chart1 = ChartFactory.createBarChart3D(LABEL_STRING+"="+statesList1.get(1), name2, "count", getSortedDataSet(dataset[1]), PlotOrientation.VERTICAL, true, true, true);
			JFreeChart chart2 = ChartFactory.createBarChart3D("", "", "ctr(‰)", getSortedDataSet(dataset[2]), PlotOrientation.VERTICAL, false, true, true);
			convertEncoding(chart0);
			convertEncoding(chart1);
			ChartPanel cp0 =  new ChartPanel(chart0, CHART_WIDTH, CHART_HEIGHT, 
					ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
					ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
					true, true, true, true, true, true, true);
			ChartPanel cp1 =  new ChartPanel(chart1, CHART_WIDTH, CHART_HEIGHT, 
					ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
					ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
					true, true, true, true, true, true, true);
			ChartPanel cp2 =  new ChartPanel(chart2,CTR_WIDTH, CTR_HEIGHT, 
					ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
					ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
					true, true, true, true, true, true, true);
			m_DataViewerPanel.removeAll();
			m_DataViewerPanel.add(cp0);
			m_DataViewerPanel.add(cp1);
			m_DataViewerPanel.updateUI();	
			updateResult(cp2);
		}
		else {
			
			if(name1.startsWith("app_type")) 
				name1 = "app_type_" + mapKey("app_type", name1.split("_")[2]);
			
			DefaultCategoryDataset dataset = new DefaultCategoryDataset();
			Function3D function3d = reporter.function3DFinder(name1, name2);
			if(function3d == null ) {
				JOptionPane.showMessageDialog(this, "No data returned", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			Function2D funtion2d0= function3d._f0;
			Function2D funtion2d1= function3d._f1;
			if(funtion2d0 == null || funtion2d1 == null ) {
				JOptionPane.showMessageDialog(this, "No data returned", "Message", JOptionPane.INFORMATION_MESSAGE);
				return;
			}
			double[][] countList0 = funtion2d0._cells;
			double[][] countList1 = funtion2d1._cells;
			Variable x = funtion2d0._x;
			Variable y = funtion2d0._y;
			ArrayList<String> statesList1 =  x.getStates();
			ArrayList<String> statesList2 =  y.getStates();
			for(int i=0; i<statesList1.size(); i++)
				for(int j=0; j<statesList2.size(); j++) {
					double sum = countList0[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))]
							+ countList1[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))];
					dataset.addValue(sum, 
							mapValue(name1, statesList1.get(i))+"&"+mapValue(name2, statesList2.get(j)), 
							"");
				}

			JFreeChart chart = ChartFactory.createBarChart3D(name1+"&"+name2, name1+"&"+name2, "count", getSortedDataSet(dataset), PlotOrientation.VERTICAL, true, true, true);
			convertEncoding(chart);
			ChartPanel cp =  new ChartPanel(chart, CHART_WIDTH, CHART_HEIGHT, 
					ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
					ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
					true, true, true, true, true, true, true);
			m_DataViewerPanel.removeAll();
			m_DataViewerPanel.add(cp);	
			m_DataViewerPanel.updateUI();	
			updateResult(reporter);
		}
	}
	
	/**
	 * update m_DataViewerPanel based on function1D in this reporter
	 * @param reporter The Reporter which contains functions
	 * @param name1 Name of the first variable of which user choose to show distribution 
	 * @param name2 Name of the second variable of which user choose to show distribution 
	 * @param name3 Name of the third variable of which user choose to show distribution 
	 */
	protected void updateViewerPanel3D(Reporter reporter, String name1, String name2, String name3) {
		
		if(name2.startsWith("app_type")) 
			name2 = "app_type_" + mapKey("app_type", name2.split("_")[2]);
		

		DefaultCategoryDataset[] dataset = new DefaultCategoryDataset[3];
		dataset[0] = new DefaultCategoryDataset();
		dataset[1] = new DefaultCategoryDataset();
		dataset[2] = new DefaultCategoryDataset();
		Function3D function3d = reporter.function3DFinder(name2, name3);
		if(function3d == null ) {
			JOptionPane.showMessageDialog(this, "No data returned", "Message", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		Function2D funtion2d0= function3d._f0;
		Function2D funtion2d1= function3d._f1;
		if(funtion2d0 == null || funtion2d1 == null ) {
			JOptionPane.showMessageDialog(this, "No data returned", "Message", JOptionPane.INFORMATION_MESSAGE);
			return;
		}
		double[][] countList0 = funtion2d0._cells;
		double[][] countList1 = funtion2d1._cells;
		Variable label = reporter._classVar;
		Variable x = funtion2d0._x;
		Variable y = funtion2d0._y;
		ArrayList<String> statesList1 =  x.getStates();
		ArrayList<String> statesList2 =  y.getStates();
		for(int i=0; i<statesList1.size(); i++)
			for(int j=0; j<statesList2.size(); j++) {
				double shw, clk, ctr;
				clk = countList1[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))];
				shw = countList0[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))] + clk;
				if(shw == 0)
					ctr = 0;
				else
					ctr = clk/shw;
				dataset[2].addValue(ctr*1000, 
						mapValue(name2, statesList1.get(i))+"&"+mapValue(name3, statesList2.get(j)), 
						"");
				for(int l=0; l<label.getStates().size(); l++)
					if(label.getStates().get(l).equals("0"))
						dataset[l].addValue(countList0[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))], 
								mapValue(name2, statesList1.get(i))+"&"+mapValue(name3, statesList2.get(j)), 
								"");
					else
						dataset[l].addValue(countList1[x.indexOf(statesList1.get(i))][y.indexOf(statesList2.get(j))], 
								mapValue(name2, statesList1.get(i))+"&"+mapValue(name3, statesList2.get(j)), 
								"");
			}
		JFreeChart chart0 = ChartFactory.createBarChart3D(name1+"="+label.getStates().get(0),name2+"&"+name3, "count", getSortedDataSet(dataset[0]), PlotOrientation.VERTICAL, true, true, true);
		JFreeChart chart1 = ChartFactory.createBarChart3D(name1+"="+label.getStates().get(1),name2+"&"+name3, "count", getSortedDataSet(dataset[1]), PlotOrientation.VERTICAL, true, true, true);
		JFreeChart chart2 = ChartFactory.createBarChart3D("CTR",name2+"&"+name3, "ctr(‰)", getSortedDataSet(dataset[2]), PlotOrientation.VERTICAL, false, true, true);
		convertEncoding(chart0);
		convertEncoding(chart1);
		ChartPanel cp0 =  new ChartPanel(chart0, CHART_WIDTH, CHART_HEIGHT, 
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
				true, true, true, true, true, true, true);
		ChartPanel cp1 =  new ChartPanel(chart1, CHART_WIDTH, CHART_HEIGHT, 
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
				true, true, true, true, true, true, true);
		ChartPanel cp2 =  new ChartPanel(chart2,CTR_WIDTH, CTR_HEIGHT, 
				ChartPanel.DEFAULT_MINIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MINIMUM_DRAW_HEIGHT, 
				ChartPanel.DEFAULT_MAXIMUM_DRAW_WIDTH, ChartPanel.DEFAULT_MAXIMUM_DRAW_HEIGHT, 
				true, true, true, true, true, true, true);
		m_DataViewerPanel.removeAll();
		m_DataViewerPanel.add(cp0);	
		m_DataViewerPanel.add(cp1);	
		m_DataViewerPanel.updateUI();		
		updateResult(cp2);
	}
	
	/**
	 * Get the Report object from server and make outputs due to user's selection
	 */
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy MM dd HH");
		String starttime = m_StartTimeTextField.getText();
		String endtime = m_EndTimeTextField.getText();
		try {
			if(starttime.length()!=13 || endtime.length()!=13)
				throw new ParseException("Date length error!", 0);
			sdf.parse(starttime);
			sdf.parse(endtime);	
			Reporter reporter;
			if(starttime.equals(m_CurStartTime) && endtime.equals(m_CurEndTime) && m_CurReporter!=null )
				reporter = m_CurReporter;
			else {
				//************************************************** 
				//obtain the Reporter object from server via socket
				//m_CurReporter = Reporter.getReporter("suffStat");
				Socket socket = new Socket(SERVER_HOST, PORT);
				m_CurStartTime = starttime;
				m_CurEndTime = endtime;
				DataOutputStream dataoutput = new DataOutputStream(socket.getOutputStream());
				dataoutput.writeUTF(starttime);
				dataoutput.writeUTF(endtime);
				ObjectInputStream objectinput = new ObjectInputStream(socket.getInputStream());
				reporter = (Reporter) objectinput.readObject();
				if(reporter == null) {
					JOptionPane.showMessageDialog(this, "Null received from server!", "Error", JOptionPane.ERROR_MESSAGE);
					return;
				}
				else
					JOptionPane.showMessageDialog(this, "Received from server succussfull!", "Message", JOptionPane.INFORMATION_MESSAGE);
				m_CurReporter = reporter;
			}
			if(m_ClassCheckBox.isSelected()) {
				//class label is selected
				if(m_FieldXComboBox.getSelectedIndex()==0 && m_FieldYComboBox.getSelectedIndex()==0) 
					//Only class label is selected Funtion1D
					updateViewerPanel1D(reporter, LABEL_STRING, true);	
				else if(m_FieldXComboBox.getSelectedIndex()!=0 && m_FieldYComboBox.getSelectedIndex()==0) 
					//Class label and fieldX are selected Funtion2D
					updateViewerPanel2D(reporter, LABEL_STRING, (String)m_FieldXComboBox.getSelectedItem(), true);	
				else if(m_FieldXComboBox.getSelectedIndex()==0 && m_FieldYComboBox.getSelectedIndex()!=0) 
					//Class label and fieldY are selected Funtion2D
					updateViewerPanel2D(reporter, LABEL_STRING, (String)m_FieldYComboBox.getSelectedItem(), true);
				else 
					//All selected Function3D
					updateViewerPanel3D(reporter, LABEL_STRING, (String)m_FieldXComboBox.getSelectedItem(), (String)m_FieldYComboBox.getSelectedItem() );
				
			}
			else {
				if(m_FieldXComboBox.getSelectedIndex()!=0 && m_FieldYComboBox.getSelectedIndex()!=0) 
					//FieldX and fieldY are both selected Function2D
					updateViewerPanel2D(reporter, (String)m_FieldXComboBox.getSelectedItem(), (String)m_FieldYComboBox.getSelectedItem(), false);
				
				else if(m_FieldXComboBox.getSelectedIndex()!=0 && m_FieldYComboBox.getSelectedIndex()==0) 
					//FieldX is selected Funtion1D
					updateViewerPanel1D(reporter, (String)m_FieldXComboBox.getSelectedItem(), false);	
				
				else if(m_FieldXComboBox.getSelectedIndex()==0 && m_FieldYComboBox.getSelectedIndex()!=0) 
					//FieldY is selected Function1D
					updateViewerPanel1D(reporter, (String)m_FieldYComboBox.getSelectedItem(), false);	
				
				else 
					//None selected
					JOptionPane.showMessageDialog(this, "At least one item in (class,fieldX,fieldY) should be used", "Error", JOptionPane.ERROR_MESSAGE);
				
			}
			//updateResult(reporter);
			m_SaveAsButton.setEnabled(true);
			
		} catch (ParseException e2) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, "Wrong date format! (yyyy MM dd HH is required)", "Error", JOptionPane.ERROR_MESSAGE);
		} catch (UnknownHostException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, e1.toString(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, e1.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
		} catch (ClassNotFoundException e1) {
			// TODO Auto-generated catch block
			JOptionPane.showMessageDialog(this, e1.toString(), e1.toString(), JOptionPane.ERROR_MESSAGE);
		}
	}
	
	private void convertEncoding(JFreeChart chart) {
		chart.getLegend().setItemFont(new Font("宋体", Font.BOLD, 12));
	}
	
	/**
	 * An comparable class for data in JFreeChart
	 * @author zli
	 *
	 */
	public class ChartData implements Comparable<ChartData> {
		protected double value;
		protected String row;
		protected String column;
		
		public ChartData(double v, String r, String c) {
			value = v;
			row = r;
			column = c;
		}
		@Override
		public int compareTo(ChartData o) {
			// TODO Auto-generated method stub
			if(this.value > o.value)
				return 1;
			else if(this.value < o.value)
				return -1;
			else
				return 0;
		}	
	}
	
	/**
	 * Get a sorted dataset 
	 * @param set The original dataset 
	 * @return The sorted dataset
	 */
	DefaultCategoryDataset getSortedDataSet(DefaultCategoryDataset set) {
		
		int startindex;
		int endindex;
		int length;
		int setsize = set.getRowCount() * set.getColumnCount();
		if(!m_SortCheckBox.isSelected() && m_ShowComboBox.getSelectedItem().equals("all"))
			return set;
		if( m_ShowComboBox.getSelectedItem().equals("all")) {
			startindex = 0;
			endindex = startindex + setsize;
		}
		else {
			if(m_ShowComboBox.getSelectedItem().toString().startsWith("top")) {
				length = Integer.parseInt(m_ShowComboBox.getSelectedItem().toString().substring(3));
				startindex = set.getRowCount() * set.getColumnCount() - length;
				if(startindex < 0)
					startindex = 0;
				endindex = setsize;
			}
			else {
				length = Integer.parseInt(m_ShowComboBox.getSelectedItem().toString().substring(6));
				startindex = 0;
				endindex = startindex + length;
				if(endindex > setsize)
					endindex = setsize;
			}
		}
		ArrayList<ChartData> list = new ArrayList<ChartData>(); 
		for(int i=0; i<set.getRowCount(); i++)
			for(int j=0; j<set.getColumnCount(); j++) 
				list.add(new ChartData(set.getValue(i, j).doubleValue(),(String)set.getRowKey(i),(String)set.getColumnKey(j)));
		Collections.sort(list);
		DefaultCategoryDataset dataset = new DefaultCategoryDataset();
		for(int i=startindex; i<endindex; i++) {
			ChartData data = list.get(i);
			dataset.addValue(data.value, data.row, data.column);
		}
		return dataset;
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		StatisticViewer sv = new StatisticViewer();
		sv.setTitle("StatisticViewer\t\t\t(BY com.renrengame.bigdata: tao.chen1@renren-inc.com \tzhe.li1@renren-inc.com)");
		sv.setSize(1350, 850);
		sv.setVisible(true);
		sv.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}
