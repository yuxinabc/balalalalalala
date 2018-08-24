package com.my51c.see51.media;

import java.util.ArrayList;
import java.util.List;

public class CachedBuffer
{
	private List<byte[]>  listCache;
	private int nMaxDataCount;
	private int readWaterMark; // ��ȡˮλ����������Ϊ�պ�ֻ�еȵ�����������readWaterMark����������ʱ����ȡ�ɹ�
	private boolean isEmpty; // �������ձ�ǣ���������Ϊ��ʱ��Ϊtrue���������������ﵽreadWaterMarkʱ��Ϊfalse
	
	private int writeWaterMark; // д��ˮλ��������������ֻ�еȵ�����������writeWaterMark�����������Ժ󣬲��ܼ���д��
	private boolean isFull; // ����������ǣ���������Ϊ��ʱ��Ϊtrue������������������readWaterMarkʱ��Ϊfalse
	
	public CachedBuffer( int maxDataCount)
	{
		nMaxDataCount = maxDataCount;
		listCache = new ArrayList<byte[]>();
		readWaterMark = 0;
		isEmpty = true;
		writeWaterMark = maxDataCount;
		isFull = false;
	}
	public void setReadWaterMark(int waterMark)
	{
		this.readWaterMark = (waterMark > 0 && waterMark < nMaxDataCount) ?  waterMark : 0;
	}
	public void setWriteWaterMark(int waterMark)
	{
		this.writeWaterMark = (waterMark > 0 && waterMark < nMaxDataCount) ?  waterMark : nMaxDataCount;
	}	
	public byte[] get(int timeout)
	{		
	//	//Log.d("LocalMediaStreamer.get", "video buffer size = " + listCache.size() + 
	//			" isFull: " + isFull + "  isEmpty: " + isEmpty);
		byte retByte[] = null;
		synchronized(listCache)
		{
			if(listCache.size() == 0 && timeout != 0) // �������Ϊ�գ��ȴ�
			{
				isEmpty = true;
				try 
				{
					if(timeout > 0)
					{
						listCache.wait(timeout);
					}
					else
					{
						listCache.wait();
					}
				} 
				catch (InterruptedException e)
				{
					e.printStackTrace();
				} 
			}
			if(!isEmpty)
			{
				try {
					retByte = listCache.get(0);
					listCache.remove(0);
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				if(isFull && listCache.size() <= this.writeWaterMark)
				{
					isFull = false;
				}
			}
		}
		return retByte;
	}
	public boolean put(byte data[])
	{
		synchronized(listCache)
		{				
			////Log.d("LocalMediaStreamer.run", "running");
			if(isFull || this.listCache.size() >= this.nMaxDataCount) //��������
			{
				//Log.d("LocalMediaStreamer.run", "full");
				isFull = true;
				return false; 
			}	
			listCache.add(data);
			if(isEmpty && listCache.size() >= this.readWaterMark) // ���������ﵽ��ˮλ
			{
				//Log.d("LocalMediaStreamer.run", "empty");
				isEmpty = false;
				listCache.notify();
			}
			return true;
		}
	}	
}