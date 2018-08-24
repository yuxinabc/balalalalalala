package com.my51c.see51.protocal;

import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;


public class SSPPackage
{
	private final String TAG = "SSPPackage";
	private ByteBuffer byteBuffer;
	private Map<String, Object> mapParams;
	private String version;
	private int nCmd;
	private int nContentLength;
	final String PROTOCAL_VERSION = "0100"; // 版本信息
	private byte contentByte[];

	public final static int COMMAND_D_S_REGISTER_REQ = 1;
	public final static int COMMAND_S_D_REGISTER_ACK = 2;
	public final static int COMMAND_D_S_DATETIME_REQ = 3;
	public final static int COMMAND_S_D_DATETIME_ACK = 4;
	public final static int COMMAND_D_S_NULL = 5;
	public final static int COMMAND_S_D_DATAINFO_REQ = 6;
	public final static int COMMAND_D_S_DATAINFO_ACK = 7;
	public final static int COMMAND_S_D_DATA_REQ = 8;
	public final static int COMMAND_D_S_DATA_ACK = 9;
	public final static int COMMAND_S_D_TRANSMIT_REQ = 10;
	public final static int COMMAND_D_S_TRANSMIT_ACK = 11;
	public final static int COMMAND_D_S_BROADCAST = 12;
	// / 服务端与客户端协议-转发命令
	public final static int COMMAND_C_S_TRANSMIT_LOGIN_REQ = 2001;
	public final static int COMMAND_S_C_TRANSMIT_LOGIN_ACK = 2002;
	public final static int COMMAND_C_S_TRANSMIT_DATAINFO_REQ = 6;
	public final static int COMMAND_S_C_TRANSMIT_DATAINFO_ACK = 7;
	public final static int COMMAND_C_S_TRANSMIT_DATA_REQ = 8;
	public final static int COMMAND_S_C_TRANSMIT_DATA_ACK = 9;
	public final static int COMMAND_C_S_TRANSMIT_REQ = 10;
	public final static int COMMAND_S_C_TRANSMIT_ACK = 11;
	public final static int COMMAND_S_C_TRANSMIT_NULL = 2003;
	public final static int COMMAND_C_S_TRANSMIT_NULL = 2003;
	
	public final static int COMMAND_C_S_TRANSMIT_INTERCOM_REQ = 3001; // 请求音频对讲，返回ret的值为1表示成功，为0表示失败
	public final static int COMMAND_S_C_TRANSMIT_INTERCOM_ACK = 3002; // 对请求音频对讲的回复
	public final static int COMMAND_S_C_TRANSMIT_INTERCOM_DATA = 3000; // 发送音频数据的命令

	public final static int COMMAND_C_S_TRANSMIT_MODIFY_REQ = 3011;
	public final static int COMMAND_S_C_TRANSMIT_MODIFY_ACK = 3012;

	public final static int COMMAND_C_S_TRANSMIT_RESTART_REQ = 3018; // 重启摄像头请求
	public final static int COMMAND_C_S_TRANSMIT_RESTART_ACK = 3019; // 重启摄像头应答

	public SSPPackage(int cmd)
	{
		// //Log.d(TAG, "SSPPackage constructor");
		mapParams = new HashMap<String, Object>();
		byteBuffer = ByteBuffer.allocate(512); // 默认先创建一个512字节长度的包
		byteBuffer.put(PROTOCAL_VERSION.getBytes()); // 添加版本信息
		byteBuffer.put(String.format("%04d", cmd).getBytes()); // 添加命令信息
		version = PROTOCAL_VERSION;
		byteBuffer.put(String.format("%06X", 0).getBytes()); // 添加命令信息
		// //Log.d(TAG, "SSPPackage constructor finished!" +
		// byteBuffer.toString());
	}

	public SSPPackage(byte buf[])
	{
		mapParams = new HashMap<String, Object>();
		if (buf != null && buf.length > 14)
		{
			int nPos = 0;
			version = new String(buf, 0, 4);
			nPos += 4;
			nCmd = Integer.parseInt(new String(buf, nPos, 4), 10);// 获取命令字
			// //Log.d(TAG, "nCmd:" + nCmd);
			nPos += 4;
			nContentLength = Integer.parseInt(new String(buf, nPos, 6), 16);
			nPos += 6;
			while (nPos < buf.length)
			{
				int nNameLen = Integer.parseInt(new String(buf, nPos, 2), 16);
				nPos += 2;
				int nValueLen = Integer.parseInt(new String(buf, nPos, 6), 16);
				nPos += 6;
				String szName = new String(buf, nPos, nNameLen);
				nPos += nNameLen;
				byte byteValue[] = new byte[nValueLen];
				System.arraycopy(buf, nPos, byteValue, 0, nValueLen); // 获取参数值
				nPos += nValueLen;
				this.mapParams.put(szName, byteValue);
				// //Log.d(TAG, "Find tag: " + szName);
				if (nCmd == 17)
				{
					//Log.d(TAG, "nNameLen: " + nNameLen);
					//Log.d(TAG, "nValueLen: " + nValueLen);
					//Log.d(TAG, "szName: " + szName);
					//Log.d(TAG, "byteValue: " + new String(byteValue));
//					//Log.d(TAG, "byteValue: " + byteValue[0]);
//					//Log.d(TAG, "byteValue: " + byteValue[1]);
//					//Log.d(TAG, "byteValue: " + byteValue[2]);
//					//Log.d(TAG, "byteValue: " + byteValue[3]);
					
					if (szName.equals("HisData"))
					{
						//Log.d(TAG,"in if()");
					
						int addr = byteValue[3] & 0xFF;
						addr |= ((byteValue[2] << 8) & 0xFF00);
						addr |= ((byteValue[1] << 16) & 0xFF0000);
						addr |= ((byteValue[0] << 24) & 0xFF000000);
						
						
						int addr2 = byteValue[0] & 0xFF;
						addr2 |= ((byteValue[1] << 8) & 0xFF00);
						addr2 |= ((byteValue[2] << 16) & 0xFF0000);
						addr2 |= ((byteValue[3] << 24) & 0xFF000000);
						
						//Log.d(TAG, "index: " + addr);
//						//Log.d(TAG, "totalRecords: " + Integer.parseInt(new String(byteValue, 4, 4), 16));
//						//Log.d(TAG, "unsigned: " + Integer.parseInt(new String(byteValue, 8, 4), 16));
					}
				}

			}
		}
	}

	public SSPPackage(ByteBuffer byteBuffer)
	{
		nCmd = Utils.ntohi(byteBuffer.getInt());
		//Log.d(TAG, "nCmd:" + nCmd);
		byteBuffer.get(contentByte, 0, 10);
		//Log.d(TAG, "contentString	:	" + byteToString(contentByte));
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

	public int getIntegerParam(String name)
	{
		int ret = 0;
		String szRetString = getString(name);
		if (szRetString != null && szRetString.length() > 0)
		{
			try
			{
				ret = Integer.parseInt(szRetString);
			} catch (Exception e)
			{
				ret = 0;
			}
		}
		return ret;
	}

	public String getString(String name)
	{
		byte byteValue[] = (byte[]) this.mapParams.get(name);
		if (byteValue != null)
			try
			{
				return new String(byteValue, "ISO-8859-1");
			} catch (UnsupportedEncodingException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		return null;
	}

	public byte[] getParam(String name)
	{
		return (byte[]) this.mapParams.get(name);
	}

	public static int getContentLength(byte buf[])
	{
		int nContentLength = 0;
		if (buf != null && buf.length >= 14) // 此时buf为写入状态，传入buf时切勿flip
		{
			try
			{
				nContentLength = Integer.parseInt(new String(buf, 8, 6), 16);
			} catch (NumberFormatException e)
			{
				e.printStackTrace();
				//Log.d("Exception", new String(buf));
			}
			return nContentLength;
		}
		return 0;
	}

	public void addParam(String name, String value)
	{

		//Log.d(TAG, "addParam(" + name + ", " + value + ")");
		addParam(name, value.getBytes());
	}

	public boolean addParam(String name, byte[] param)
	{
		int length = name.length() + param.length + 8;
		if (length > byteBuffer.limit() - byteBuffer.position()) // 空间不够，重新申请空间
		{
			byte newBuf[] = new byte[byteBuffer.capacity() + length + 512];
			int oldLen = byteBuffer.position();
			byteBuffer.flip();
			byteBuffer.get(newBuf, 0, oldLen);
			byteBuffer = ByteBuffer.wrap(newBuf);
			byteBuffer.position(oldLen);
		}
		String szNameLen = String.format("%02X", name.length());
		String szValueLen = String.format("%06X", param.length);
		byteBuffer.put(szNameLen.getBytes());
		byteBuffer.put(szValueLen.getBytes());
		byteBuffer.put(name.getBytes());
		byteBuffer.put(param);

		nContentLength += length;// 获取长度
		int nOldPos = byteBuffer.position();
		// byteBuffer.mark(); //记录当前位置
		byteBuffer.position(8); // 重新写入长度
		String szTotalLen = String.format("%06X", nContentLength);
		byteBuffer.put(szTotalLen.getBytes());
		// byteBuffer.reset();// 恢复到写入位置
		byteBuffer.position(nOldPos);

		// //Log.d(TAG, new String(byteBuffer.array()));
		return true;
	}

	public ByteBuffer getByteBuffer()
	{
		byteBuffer.flip();
		// //Log.d(TAG, new String(byteBuffer.array()));
		return byteBuffer;
	}

	public ByteBuffer getByteBufferNoFlip()
	{
		return byteBuffer;
	}

	public String getVersion()
	{
		return version;
	}

	public int getnCmd()
	{
		return nCmd;
	}

	public void addParam(String name, PTZ_CMD cmd)
	{
		this.addParam(name, String.format("%d", cmd.value));
	}
	
	public void addParam(String name, int cmd)
	{
		this.addParam(name, String.format("%d", cmd));
	}

	public enum PTZ_CMD
	{
		PTZ_TURN_TO_LEFT(1),
		PTZ_TURN_TO_RIGHT(2),
		PTZ_TURN_TO_UP(3),
		PTZ_TURN_TO_DOWN(4),
		PTZ_TURN_TO_ENLARGE(5),
		PTZ_TURN_TO_NARROW(6),
		PTZ_TURN_AUTO_H(7),
		PTZ_TURN_AUTO_V(8),
		PTZ_TURN_AUTO_SCAN(9),
		PTZ_TURN_SCAN_STOP(10),
		PTZ_TURN_X_STEP(11),
		PTZ_TURN_Y_STEP(12),
		PTZ_TURN_RESET(13),
		ORIGIN_SEND(14),
		PTZ_FLIP_H(15), 
		PTZ_FLIP_V(16),
		PTZ_FOCUSIN(17),
		PTZ_FOCUSOUT(18),
		PTZ_APERTUREIN(19),
		PTZ_APERTUREOUT(20),
		PTZ_CMD_END(21),
		
		////alarm on off
		CMD_ALERT_GET_STATE(100),
		CMD_ALERT_SET(101),
		////vdieo qulity
		CMD_AVQ_GET_STATE(102),
		CMD_AVQ_SET(103),
		
		////dual audio
		PROTOCOL_INTERCOM_DATA(3000),
		PROTOCOL_INTERCOM(3001),
		PROTOCOL_REPLAY_INTERCOM(3002),
		
		//history video
		HIS_VIDEO_REQ(3100),
		HIS_VIDEO_ACK(3101),
		GET_PIC_REQ(3102),
		GET_PIC_ACK(3103),
		
		PTZ_HIS_TEMP_HIM_REQ(3200), // 历史温度请求
		PTZ_HIS_TEMP_HIM_ACK(3201), // 历史温度回复
		PTZ_CUR_TEMP_HIM_REQ(3202), // 当前温度请求
		PTZ_CUR_TEMP_HIM_ACK(3203), // 当前温度回复
		
		//set devinfo
		GET_DEV_INFO_REQ(3300),
		GET_DEV_INFO_ACK(3301),
		SET_DEV_INFO_REQ(3302),
		SET_DEV_INFO_ACK(3303),
		GET_DEV_3GINFO_REQ(3304),
		GET_DEV_3GINFO_ACK(3305),
		SET_DEV_3GINFO_REQ(3306),
		SET_DEV_3GINFO_ACK(3307),
		
		//set rfid info
		GET_RFID_INFO_REQ(3400),
		GET_RFID_INFO_ACK(3401),
		SET_RFID_INFO_REQ(3402),
		SET_RFID_INFO_ACK(3403),
		
		GET_BLP_RESULT_REQ(3700),
		GET_BLP_RESULT_ACK(3701),
		SET_BLP_RESULT_REQ(3702),
		SET_BLP_RESULT_ACK(3703),
		
		GET_CURTAIN_STATUS_REQ(4000),
		GET_CURTAIN_STATUS_ACK(4001),
		SET_CURTAIN_STATUS_REQ(4002),
		SET_CURTAIN_STATUS_ACK(4003);
		
		private int value;

		PTZ_CMD(int num)
		{
			value = num;
		}

		public int value()
		{
			return this.value;
		}
	}

}
