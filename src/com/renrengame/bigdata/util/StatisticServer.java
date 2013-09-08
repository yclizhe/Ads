package com.renrengame.bigdata.util;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.ObjectOutput;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Server for StatisticViewer
 * @author zhe li
 * 
 */
public class StatisticServer {
	
	//public static int PORT = 5678;  
	public static String HADOOP_HOST = "h2-1";
	public static String HDFS_PATH = "/bigdata/LogAnalytics/Aggregate/";
	public static String SHELL_PATH = "/home/hadoop/LogAnalytics/bin/Server.sh";
	public static String FILE_PATH = "/home/hadoop/LogAnalytics/";


	ServerSocket s = null;  
	Socket socket = null;  

    public void startListener(int port){
    	  ExecutorService service = Executors.newFixedThreadPool(3);
    	  try {  
    		  s = new ServerSocket(port);  
    		  while(true){
    			  System.out.println("\nSocket Listening...");
    			  socket = s.accept(); 
    			  service.execute(new SocketThread(socket));	  
    		  }
    	  } catch (Exception e) {  
  	            // TODO Auto-generated catch block  
    		  e.printStackTrace();  
    	  } finally{  
    		  try {   
    			  s.close();  
    		  } catch (Exception e2) {   	                  
    		  }  
    	  }       
    }
    
    public class SocketThread extends Thread {
    	Socket socket;
    	public SocketThread(Socket s) {
    		socket = s;
    	}
    	
    	public void run(){
    		try{
    		  Date date = new Date();
			  SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			  System.out.println("****************************************************\nSocket started!");
			  System.out.println("Request Time:" + df.format(date));
			  System.out.println("Client IP: " +  socket.getInetAddress().getHostAddress());
			  DataInputStream datainput = new DataInputStream(socket.getInputStream());
			  String starttime = datainput.readUTF();
			  System.out.println("Received StartTime: " + starttime);
			  String endtime = datainput.readUTF();
			  System.out.println("Received EndTime: " + endtime);
			  String timespan = getTimeString(starttime, endtime);
			  String path = FILE_PATH + timespan + "/";
			  if(!isExist(path)) {
				  System.out.println("File" + path +  "not existed, starting bash script execution!");
				  if(exeShell(starttime, endtime)) {
					  System.out.println("Script executed successfully!");
				  }
				  else
					  System.out.println("Script execution failed!");;
			  }
			  if(isExist(path)) {
				  System.out.println("File" + path + " existed, sending file started!");
				  Reporter reporter = Reporter.getReporter(path, false);
    			  ObjectOutputStream objectoutput = new ObjectOutputStream(socket.getOutputStream());
    			  objectoutput.writeObject(reporter);
			  }
			  else {
				  System.out.println("File" + path + " not existed after script execution");
				  Reporter reporter  = null;
    			  ObjectOutputStream objectoutput = new ObjectOutputStream(socket.getOutputStream());
    			  objectoutput.writeObject(reporter);
			  }
	              socket.close();  
	              System.out.println("Socket closed (" + socket.getInetAddress().getHostAddress()+  ")!\n****************************************************\n");
    	
    		}catch(Exception e) {
    			e.printStackTrace();
    		}
    	}
    }
    
    /**
     * Execute shell script: aggregate values and statistics between starttime and endtime
     * @param starttime
     * @param endtime
     * @return
     */
	public  boolean exeShell(String starttime,String endtime){
		Runtime rt = Runtime.getRuntime();
		try {
			
			Process p = rt.exec("/bin/bash " + SHELL_PATH + " " +starttime+" "+endtime);
			System.out.println("SHELL: /bin/bash " + SHELL_PATH + " " +starttime+" "+endtime);
			if(p.waitFor() != 0) {
				System.out.println(p.getOutputStream().toString());			
				return false;
			}
			} catch (IOException e) {
					e.printStackTrace();
					return false;
				} catch (InterruptedException e) {
						e.printStackTrace();
						return false;
					}
		return true;
	}
	
	/**
	 * return time string is such format:yyyyMMddHH-yyyyMMddHH
	 * @param starttime
	 * @param endtime
	 * @return
	 * @throws ParseException
	 */
	public String getTimeString(String starttime, String endtime) throws ParseException {
		SimpleDateFormat sf1 = new SimpleDateFormat("yyyy MM dd HH");
		SimpleDateFormat sf2 = new SimpleDateFormat("yyyyMMddHH");
		String str = sf2.format(sf1.parse(starttime)) + "-" + sf2.format(sf1.parse(endtime));
		return str;
	}
	
	/**
	 * Chech files(Value and Statistics) existed or not under the specific path
	 * @param path
	 * @return
	 */
	public boolean isExist(String path) {
		File file1 = new File(path+"/Statistics");
		File file2 = new File(path+"/Values");
		if(!file1.exists() || !file2.exists())
			return false;
		return true;
	}
	
	public static void main(String [] args){
		StatisticServer ss = new StatisticServer();
		if(args.length == 0)
			ss.startListener(5678);
		else if(args.length == 1)
			ss.startListener(Integer.parseInt(args[0]));
		else
			System.out.println("Usage: java -jar StatisticServer.jar [PORT]");
		
		
	}
}

