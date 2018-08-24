package com.my51c.see51.data;

import com.my51c.see51.common.AppData;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

public class CloudFileInfo {
	private String szFileName;
	private long   nFileSize;
	private long   nCurSize;
	private static String strpath;
	private boolean bDown = false;
	private long lTime;
	private String dateStr;
	private int lType;
	private int lStatus;
	private String szDeviceid;
	
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
	 * @return the lTime
	 */
	public long getlTime() {
		return lTime;
	}

	/**
	 * @param lTime the lTime to set
	 */
	public void setlTime(long lTime) {
		this.lTime = lTime;
		this.dateStr = new java.text.SimpleDateFormat("yyyyMMddHHmmss").format(new java.util.Date(lTime*1000));
		setSzFileName(dateStr);
	}

	/**
	 * @return the lType
	 */
	public int getlType() {
		return lType;
	}

	/**
	 * @param lType the lType to set
	 */
	public void setlType(int lType) {
		this.lType = lType;
	}

	/**
	 * @return the lStatus
	 */
	public int getlStatus() {
		return lStatus;
	}

	/**
	 * @param lStatus the lStatus to set
	 */
	public void setlStatus(int lStatus) {
		this.lStatus = lStatus;
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

	public CloudFileInfo() {
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
		CloudFileInfo.strpath = strpath;
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
	
	public String getRealFileName()
	{	
		String strTag = "";
		switch(lType)
		{
			case 1:
			{
				strTag = ".264";
			}
				break;
			case 2:
			{
				strTag = ".avi";
			}
				break;
			case 3:
			{
				strTag = ".jpg";
			}
				break;
			case 4:
			{
				strTag = ".mp4";
			}
				break;
			case 5:
			{
				strTag = ".ts";
			}
				break;
			default:
			{
				strTag = ".264";
			}
				break;
		}
		
		return getSzFileName()+strTag;
	}
	
	public boolean checkitcomplete()
	{	
		boolean bRet = false;
		String fullname = strpath + szDeviceid + File.separator + getSzFileName().substring(0, 8) + File.separator + getRealFileName();
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
