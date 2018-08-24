package com.my51c.see51.data;

import com.my51c.see51.common.AppData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class SDFileInfo {
	
	private String szFileName;
	private long   nFileSize;
	private long   nCurSize;
	private static String strpath;
	private boolean bDown = false;
	private String szDeviceid;
	private boolean waitingForDl = false;
	
	/**
	 * @return the szDeviceid
	 */
	public String getSzDeviceid() {
		return szDeviceid;
	}

	/**
	 * @param szDeviceid the szDeviceid to set
	 */
	public void setSzDeviceid(String szDeviceid) {
		this.szDeviceid = szDeviceid;
	}

	/**
	 * @return the bDown
	 */
	public boolean isbDown() {
		return bDown;
	}

	/**
	 * @param bDown the bDown to set
	 */
	public void setbDown(boolean bDown) {
		this.bDown = bDown;
	}

	public SDFileInfo() {
		// TODO Auto-generated constructor stub
		strpath = AppData.getWokringPath() + "SDRecord" + File.separator;
	}
	
	/**
	 * @return the strpath
	 */
	public static String getStrpath() {
		return strpath;
	}
	/**
	 * @param strpath the strpath to set
	 */
	public static void setStrpath(String strpath) {
		SDFileInfo.strpath = strpath;
	}
	
	public String getRealPath()
	{
		String fullpath = strpath + szDeviceid + File.separator + getSzFileName().substring(0, 8) + File.separator;
		return fullpath;
	}
	
	/**
	 * @return the szFileName
	 */
	public String getSzFileName() {
		return szFileName;
	}
	
	public String getFileTag()
	{
		String filename = getSzFileName();
		String[] itemTag = filename.split("\\.");
		return itemTag[1];
	}
	
	/**
	 * @param szFileName the szFileName to set
	 */
	public void setSzFileName(String szFileName) {
		this.szFileName = szFileName;
	}

	/**
	 * @return the nFileSize
	 */
	public long getnFileSize() {
		return nFileSize;
	}
	
	/**
	 * @param nFileSize the nFileSize to set
	 */
	public void setnFileSize(long nFileSize) {
		this.nFileSize = nFileSize;
	}
	
	/**
	 * @return the nCurSize
	 */
	public long getnCurSize() {
		return nCurSize;
	}
	
	/**
	 * @param nCurSize the nCurSize to set
	 */
	public void setnCurSize(long nCurSize) {
		this.nCurSize = nCurSize;
	}
	
	
	
	public boolean isWaitingForDl() {
		return waitingForDl;
	}

	public void setWaitingForDl(boolean waitingForDl) {
		this.waitingForDl = waitingForDl;
	}

	public boolean checkitcomplete()
	{	
		boolean bRet = false;
		String fullname = strpath + szDeviceid + File.separator + getSzFileName().substring(0, 8) + File.separator + getSzFileName();
		File myFiletest = new File(fullname);
		if(!myFiletest.exists())
		{
			return bRet;
		}
		
		FileInputStream fis = null;
		try {
			fis = new FileInputStream(myFiletest);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		int size = 0;
		try {
			 size = fis.available();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(size == getnFileSize() && size != 0)
		{
			bRet = true;
		}
		
		try {
			fis.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return bRet;
	}

}
