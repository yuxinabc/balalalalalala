package com.my51c.see51.media;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;


/** 
 * doceoder�����࣬���ݲ�ͬ��CPU�ܹ������벻ͬ�Ľ�����
 * 
 *
 */
public class VideoDecoderFactory {
	
	/**
	 * ��������������
	 * @return
	 */
	public static VideoDecoder createDecoder() {
		
		//getCpuType(); // ��ȡCPU����		
		//String cpu = getCpuType()[0].substring(0, 5);
		String cpu ="armv7";   // not support armv6 phone
		VideoDecoder decoder;
		if(cpu.equalsIgnoreCase("armv7")){
			//Log.d("VideoDocoderFactory", "armv7");
			decoder = new Arm7videoDecoder();		
		}else {
			//Log.d("VideoDocoderFactory", "armv6");
			decoder = new Arm6videoDecoder();
		}
		return decoder;
	}

	protected static String[] getCpuType()
	{
		String str1 = "/proc/cpuinfo";  
	    String str2="";  
	    String[] cpuInfo={"",""};  
	    String[] arrayOfString;  
	    try {  
	        FileReader fr = new FileReader(str1);  
	        BufferedReader localBufferedReader = new BufferedReader(fr, 8192);  
	        str2 = localBufferedReader.readLine();  
	        arrayOfString = str2.split("\\s+");  
	        for (int i = 2; i < arrayOfString.length; i++) {  
	            cpuInfo[0] = cpuInfo[0] + arrayOfString[i] + " ";  
	        }  
	        str2 = localBufferedReader.readLine();  
	        arrayOfString = str2.split("\\s+");  
	        cpuInfo[1] += arrayOfString[2];  
	        localBufferedReader.close();  
	    } catch (IOException e) {  
	    }  
	    //Log.d("VideoDocoderFactory", "cpuInfo[0]: " + cpuInfo[0]);
	    //Log.d("VideoDocoderFactory", "cpuInfo[1]: " + cpuInfo[1]); 
	    return cpuInfo;  
	}
}
