package com.my51c.see51.data;

import java.nio.ByteBuffer;

public class Device3GShortParam implements Cloneable {

		private byte sz3GUser[];
		private byte sz3GPWD[];
		private byte sz3GAPN[];
		private byte szDialNum[];
		private byte szAlarmNum0[];
		private byte szAlarmNum1[];
		private byte szAlarmNum2[];
		private byte szAlarmNum3[];
		
		public Device3GShortParam()
		{
			sz3GUser = new byte[40];
			sz3GPWD = new byte[40];
			sz3GAPN = new byte[108];
			szDialNum = new byte[24];
			szAlarmNum0 = new byte[20];
			szAlarmNum1 = new byte[20];
			szAlarmNum2 = new byte[20];
			szAlarmNum3 = new byte[20];	
		}
		
		public Device3GShortParam(ByteBuffer byteBuf)
		{
			sz3GUser = new byte[40];
			sz3GPWD = new byte[40];
			sz3GAPN = new byte[108];
			szDialNum = new byte[24];
			szAlarmNum0 = new byte[20];
			szAlarmNum1 = new byte[20];
			szAlarmNum2 = new byte[20];
			szAlarmNum3 = new byte[20];
			
			byteBuf.get(sz3GUser, 0, 40);
			byteBuf.get(sz3GPWD, 0, 40);
			byteBuf.get(sz3GAPN, 0, 108);
			byteBuf.get(szDialNum, 0, 24);
			byteBuf.get(szAlarmNum0, 0, 20);
			byteBuf.get(szAlarmNum1, 0, 20);
			byteBuf.get(szAlarmNum2, 0, 20);
			byteBuf.get(szAlarmNum3, 0, 20);
		}

		public ByteBuffer toByteBuffer()
		{
			ByteBuffer retBuf = ByteBuffer.allocate(292);
			
			retBuf.put(sz3GUser, 0, 40);
			retBuf.put(sz3GPWD, 0, 40);
			retBuf.put(sz3GAPN, 0, 108);
			retBuf.put(szDialNum, 0, 24);
			retBuf.put(szAlarmNum0, 0, 20);
			retBuf.put(szAlarmNum1, 0, 20);
			retBuf.put(szAlarmNum2, 0, 20);
			retBuf.put(szAlarmNum3, 0, 20);
			
			retBuf.flip();
			return retBuf;
		}

		protected String byteToString(byte[] src)
		{
			int len = 0;
			for (; len < src.length; len++)
			{
				if (src[len] == 0)
				{
					break;
				}
			}
			return new String(src, 0, len);
		}

		protected byte[] StringToByte(String str, int length)
		{
			byte retByte[] = new byte[length];
			byte strByte[] = str.getBytes();
			for (int i = 0; i < strByte.length && i < length; i++)
			{
				retByte[i] = strByte[i];
			}
			return retByte;
		}
		
		public void printflog()
		{
			System.out.println("sz3GUser:"+getsz3GUser());
			System.out.println("sz3GPWD:"+getsz3GPWD());
			System.out.println("sz3GAPN:"+getsz3GAPN());
			System.out.println("szDialNum:"+getszDialNum());
			System.out.println("szAlarmNum0:"+getszAlarmNum0());
			System.out.println("szAlarmNum1:"+getszAlarmNum1());
			System.out.println("szAlarmNum2:"+getszAlarmNum2());
			System.out.println("szAlarmNum3:"+getszAlarmNum3());
		}
		
		public String getsz3GUser()
		{
			return byteToString(sz3GUser);
		}

		public String getsz3GPWD()
		{			
			return byteToString(sz3GPWD);
		}

		public String getsz3GAPN()
		{ 		
			return byteToString(sz3GAPN);
		}

		public String getszDialNum()
		{ 	
			return byteToString(szDialNum);
		}

		public String getszAlarmNum0()
		{ 	
			return byteToString(szAlarmNum0);
		}

		public String getszAlarmNum1()
		{ 
			return byteToString(szAlarmNum1);
		}

		public String getszAlarmNum2()
		{ 	
			return byteToString(szAlarmNum2);
		}
		
		public String getszAlarmNum3()
		{ 
			return byteToString(szAlarmNum3);
		}

		public void setsz3GUser(String strValue)
		{
			sz3GUser = StringToByte(strValue, sz3GUser.length);
		}

		public void setsz3GPWD(String strValue)
		{ 		
			sz3GPWD = StringToByte(strValue, sz3GPWD.length);
		}

		public void setsz3GAPN(String strValue)
		{		
			sz3GAPN = StringToByte(strValue, sz3GAPN.length);
		}

		public void setszDialNum(String strValue)
		{ 	
			szDialNum = StringToByte(strValue, szDialNum.length);
		}

		public void setszAlarmNum0(String strValue)
		{ 		
			szAlarmNum0 = StringToByte(strValue, szAlarmNum0.length);
		}

		public void setszAlarmNum1(String strValue)
		{ 
			szAlarmNum1 = StringToByte(strValue, szAlarmNum1.length);
		}

		public void setszAlarmNum2(String strValue)
		{ //
			szAlarmNum2 = StringToByte(strValue, szAlarmNum2.length);
		}
		
		public void setszAlarmNum3(String strValue)
		{ //
			szAlarmNum3 = StringToByte(strValue, szAlarmNum3.length);
		}	
		
		@Override
		public boolean equals(Object obj)
		{
			if (!(obj instanceof Device3GShortParam))
			{
				return false;
			}
			Device3GShortParam o = (Device3GShortParam) obj;
			if (this == o)
			{
				return true;
			}

			ByteBuffer buffer = o.toByteBuffer();
			if (buffer.equals(this.toByteBuffer()))
			{
				return true;
			}
			return false;
		}

		@Override
		public Object clone()
		{
			Device3GShortParam o = null;
			try
			{
				o = (Device3GShortParam) super.clone();
				
				o.sz3GUser = (byte[]) sz3GUser.clone();
				o.sz3GPWD = (byte[]) sz3GPWD.clone();
				o.sz3GAPN = (byte[]) sz3GAPN.clone();
				o.szDialNum = (byte[]) szDialNum.clone();
				o.szAlarmNum0 = (byte[]) szAlarmNum0.clone();
				o.szAlarmNum1 = (byte[]) szAlarmNum1.clone();
				o.szAlarmNum2 = (byte[]) szAlarmNum2.clone();
				o.szAlarmNum3 = (byte[]) szAlarmNum3.clone();

			} catch (CloneNotSupportedException e)
			{
				e.printStackTrace();
			}
			return o;
		}
}
