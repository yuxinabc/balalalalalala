package com.my51c.see51.protocal;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RFPackage {
	private ArrayList<Map<String, Object>> mData = new ArrayList<Map<String,Object>>();
	
	public RFPackage() {
		// TODO Auto-generated constructor stub
		resetMData();
	}
	
	
	public RFPackage(String buf) {
		// TODO Auto-generated constructor stub
		resetMData();
		parsebuf(buf);
	}
	
	public ArrayList<Map<String, Object>> getRFDevList()
	{
		return mData;
	}
	
	public void parseArrayList( ArrayList<Map<String, Object>> inList)
	{
		mData.clear();
		
		for(int i=0; i<inList.size(); i++)
		{
			Map<String, Object> map = new HashMap<String, Object>();
			Map<String, Object> temp = inList.get(i);
			for (Map.Entry<String, Object> iterator : temp.entrySet()) 
			{
				String key = new String(iterator.getKey());
				String value = new String((String)iterator.getValue());
				map.put(key, value);
            }
			mData.add(map);
		}
	}
	
//	  MY51CRFID:3320188812345678;name:cameraQQ;status:on;####
//	  MY51CRFID:3320188812345679;name:cameraQQ;status:on;####
//	  MY51CRFID:3320188812345680;name:cameraQQ;status:on;####
	public void parsebuf(String buf)
	{
		mData.clear();
		String tempStr = new String(buf);
		String[] lineArray = tempStr.split("####");//		MY51CRFID:3320188812345679;name:cameraQQ;status:on;
		for(int i=0; i< lineArray.length; i++)
		{	
			Map<String, Object> map = new HashMap<String, Object>();
			String[] itemArray = lineArray[i].split(";");//			MY51CRFID:3320188812345679
			for(int j=0; j < itemArray.length; j++)
			{	
				String[] keyValue = itemArray[j].split(":");
				map.put(keyValue[0], keyValue[1]);
			}
			
			mData.add(map);
		}	
	}
//	70000004,20150829101802,01000000,5a3a5701,#
//	70000004,20150831143508,01000000,723f4701,#
//	70000004,20150831184503,01000000,703e4301,#
//	70000004,20150831195443,01000000,673d3f01,#
	public void parseBLPbuf(String buf){
		
	}
	
	public String buildbuf()
	{
		String resultStr = new String();
		for(int i = 0; i < mData.size(); i++)
		{
			HashMap<String, Object> map =  (HashMap<String, Object>) mData.get(i);
			for (Map.Entry<String, Object> iterator : map.entrySet()) 
			{
				resultStr += iterator.getKey();
				resultStr += ":";
				resultStr += iterator.getValue();
				resultStr += ";";
            }
			resultStr += "####";
		}
		
		return resultStr;
	}
	
	public String getValue(String devId, String key)
	{
		String retValue = null;
		for(int i=0; i< mData.size(); i++)
		{
			HashMap<String, Object> map =  (HashMap<String, Object>) mData.get(i);
			if(map.containsValue(devId))
			{
				retValue = (String) map.get(key);
				break;
			}
		}
		
		return retValue;
	}
	
	public boolean addId(String devId)
	{			
		boolean bRet = false;
		
		if(devId == null || devId.equals(""))
		{
			return bRet;
		}
		
		for(int i=0; i< mData.size(); i++)
		{
			HashMap<String, Object> map =  (HashMap<String, Object>) mData.get(i);
			if(map.containsValue(devId))
			{
				bRet = true;
				break;
			}
		}
		
		if(!bRet)
		{
		   HashMap<String, Object> map = new HashMap<String, Object>();
		   map.put("MY51CRFID", devId);
		   map.put("status", "on");
		   map.put("name", "unknown");
		   mData.add(map);
		   bRet = true;
		}
		
		return bRet;
	}
	
	public boolean RemoveId(String devId)
	{
		boolean bRet = false;
		
		if(devId == null || devId.equals(""))
		{	
			return bRet;
		}
		
		HashMap<String, Object> map = null;
		for(int i=0; i< mData.size(); i++)
		{
			map =  (HashMap<String, Object>) mData.get(i);
			String tempId = (String)map.get("MY51CRFID");
			if(devId.equals(tempId))
			{
				bRet = true;
			    mData.remove(i);
				break;
			}
		}

		return bRet;
	}
	
	
	public boolean setValue(String devId, String key, String value)
	{
		boolean bRet = false;
		
		for(int i=0; i< mData.size(); i++)
		{
			HashMap<String, Object> map =  (HashMap<String, Object>) mData.get(i);
			if(map.containsValue(devId))
			{
				if(map.containsKey(key))
				{
					map.put(key, value);
					bRet = true;
					break;
				}
				else
				{
					break;
				}
				
			}
		}
		
		return bRet;
	}
	
	
	private void resetMData()
	{
		if(mData ==null)
		{
		   mData = new ArrayList<Map<String,Object>>();
		}
		mData.clear();
	}
}
