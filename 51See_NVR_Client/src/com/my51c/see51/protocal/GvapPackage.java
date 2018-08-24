package com.my51c.see51.protocal;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class GvapPackage
{
	/**�汾��Ϣ*/
	final String PROTOCAL_VERSION = "gvap/1.0"; // �汾��Ϣ
	private final String TAG = "GvapPackage";

	private long sendTime;
	private Map<String, List<String>> mapParams;
	private String version;
	private String command;
	private String resourceName;
	private String description;
	private int statusCode;
	private int type; // 1.����� 2.���ذ�
	private int contentLength;
	private byte content[];
	private int length;
	private GvapCommand commandId;
	private boolean waitForResp;
	private String parent_group = null; // �����ĸ������Ϣ���б�,Ĭ��Ϊnull,�����Ǹ�����


	public GvapPackage(byte[] byteArray) // ���캯��,�����ݽ�������һ����
	{
		content = null;
		length = byteArray.length;
		mapParams = new LinkedHashMap<String, List<String>>();
		String szHeader = new String(byteArray);
		//Log.d(TAG, szHeader);

//		Log.i(TAG, "new GvapPackage:"+szHeader);
		String arrayString[] = szHeader.split("\r\n");//gvap/1.0 200 OK  expire: 120   cseq: 1

		boolean isFirstLine = true;
		for (String line : arrayString)
		{
//			Log.i(TAG, "arrayString-item:"+line);//gvap/1.0   200   OK
			if (isFirstLine) // ��ʼ��
			{
				if (!parseCommandLine(line))
				{
					break;
				}
				isFirstLine = false;
			} else
			// ����ʼ��
			{
				parseParamLine(line);
			}
		}
	}
	
	
	private boolean parseCommandLine(String line)
	{
		String firstLine[] = line.split("\\s+");//  \\s+:�ո�,�س�,���еȿհ׷�
		for(String s: firstLine){
//			Log.i(TAG, "arrayString-item-parseCommandLine:"+s);
		}
		if (firstLine.length >= 3)
		{
			if (firstLine[0].toLowerCase().startsWith("gvap"))
			{
				this.type = 2;
				this.version = firstLine[0];//gvap/1.0
				this.statusCode = Integer.parseInt(firstLine[1]);//200
				this.description = firstLine[2];//OK
				this.commandId = GvapCommand.CMD_UNKNOWN;
			} else
			{
				this.type = 1;
				this.command = firstLine[0];
				this.resourceName = firstLine[1];
				this.version = firstLine[2];
				this.commandId = GvapCommand.getValue(this.command + " " + this.resourceName);
			}
		} else
		{
			return false;
		}
		return true;
	}

	private boolean parseParamLine(String line)
	{ 
		//Log.d("gvap content", line);
		String paramLine[] = line.split(":");
		for(String s: paramLine){
//			Log.i(TAG, "arrayString-item-parseParamLine:"+s);
		}
		if (paramLine.length >= 2)
		{
			String name = paramLine[0].trim().toLowerCase();//cseq
			String value = paramLine[1].trim();//1
			for(int i=2; i<paramLine.length; i++)//���paramLine[1]֮���valueֵ
			{
				value += ":";
				value += paramLine[i].trim();//1:xxx:xxx
//				Log.i(TAG, "parseParamLine value+: ="+value);
			}
			
			//Log.d("love", "name" + name + "		,value" + value);
			if (name.equals("content-length"))
			{
				this.contentLength = Integer.parseInt(value);
				return true;
			}
			List<String> paramList = mapParams.get(name);
			if (paramList == null)
			{
				paramList = new ArrayList<String>();
			}
			paramList.add(value);
			mapParams.put(name, paramList);//name-device_id  paramList:a8 52 02 01 47 02 03 05 0e
			return true;
		}
		return false;
		
//		total: 11
//		group-id: 26
//		group-id: 30
//		device-id: a812610000a8
//		device-id: a84261000052
//		device-id: b85261000002
//		device-id: c04261000001
//		device-id: c81261000047
//		device-id: c85261000002
//		device-id: c85261000003
//		device-id: c85261000005
//		device-id: c8526100000e
//		cseq: 2
	}

	public GvapPackage(GvapCommand cmdId) // ���캯���� ����һ�������
	{
		content = null;
		version = PROTOCAL_VERSION;//"gvap/1.0"
		this.type = 1;

		this.commandId = cmdId;//CMD_GET_DEVLIST("get dev-list")
		String arrayStr[] = cmdId.getCmdLine().split(" ");
		
		this.command = arrayStr[0];//get
		this.resourceName = arrayStr[1];//dev-list
//		Log.i(TAG, "GvapPackage-GvapCommand:"+arrayStr[0]+arrayStr[1]);
		mapParams = new LinkedHashMap<String, List<String>>();
		length = 0;
	}

	public GvapPackage(int statusCode, String description) // ���캯���� ����һ�����ذ�
	{
		content = null;
		version = PROTOCAL_VERSION;
		this.type = 2;
		this.statusCode = statusCode;
		this.description = description;
		mapParams = new LinkedHashMap<String, List<String>>();
		this.commandId = GvapCommand.CMD_UNKNOWN;
	}

	// ����ж����ͬ��ͷ�򣬸÷���ֻ���ص�һ��
	public String getParam(String name)
	{
		List<String> list = this.mapParams.get(name.toLowerCase());
		if (list != null && list.size() > 0)
			return list.get(0);
		else
			return null;
	}

	public List<String> getParamList(String name)
	{
		return this.mapParams.get(name.toLowerCase());
	}

	public int getIntegerParamWithDefault(String name, int nDefault)
	{
		String value = getParam(name);
		if (value != null)
		{
			try
			{
				return Integer.parseInt(value);
			} catch (NumberFormatException e)
			{
				return nDefault;
			}
		}
		return nDefault;
	}

	public void addParam(String name, String value)
	{
		List<String> paramList = mapParams.get(name.toLowerCase());
		if (paramList == null)
		{
			paramList = new ArrayList<String>();
		}
		paramList.add(value);
		mapParams.put(name.toLowerCase(), paramList);
		//length += name.length() + value.length() + 3; // ���ֵĳ��ȼ���ֵ�ĳ��ȣ��ټ��ϻس����к�ð�ŵĳ���
		length += name.length() + value.getBytes().length + 3;
	}

	public long getSendTime()
	{
		return sendTime;
	}

	public void setSendTime()
	{
		this.sendTime = System.currentTimeMillis();
	}

	public int getContentLength()
	{

		return contentLength;
	}

	public void appendContent(byte[] array)
	{
		this.content = array;
		this.contentLength = array.length;
	}

	public String getVersion()
	{
		return version;
	}

	public String getCommand()
	{
		return command;
	}

	public String getResourceName()
	{
		return this.resourceName;
	}

	public String getDescription()
	{
		return this.description;
	}

	public int getStatusCode()
	{
		return statusCode;
	}

	public int getType()
	{
		return type;
	}
	
	public static String getString(ByteBuffer buffer)  
    {  
        Charset charset = null;  
        CharsetDecoder decoder = null;  
        CharBuffer charBuffer = null;  
        try  
        {  
            charset = Charset.forName("UTF-8");  
            decoder = charset.newDecoder();  
            // charBuffer = decoder.decode(buffer);
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());  
            return charBuffer.toString();  
        }  
        catch (Exception ex)  
        {  
            ex.printStackTrace();  
            return "";  
        }  
    }
	 
	public ByteBuffer getByteBuffer()
	{

		String commandLine = "";
		if (this.type == 1)
		{
			commandLine = this.command + " " + this.resourceName + " " + PROTOCAL_VERSION + "\r\n";
		} else
		{
			commandLine = PROTOCAL_VERSION + String.format(" %d", this.statusCode) + " " + this.description + "\r\n";
		}
		length += commandLine.length();
		length += 2; // ��β�Ŀհ���ռλ
		ByteBuffer buf = ByteBuffer.allocate(length + 4); // �ܳ���Ҫ����ǰ�ĸ���ʾ���ȵ��ֽ�
		buf.put(String.format("%04X", length).getBytes()); // д�����ֽڳ���
		buf.put(commandLine.getBytes());

		Set<Map.Entry<String, List<String>>> set = this.mapParams.entrySet();
		for (Iterator<Map.Entry<String, List<String>>> it = set.iterator(); it.hasNext();)
		{
			Map.Entry<String, List<String>> entry = (Map.Entry<String, List<String>>) it.next();
			for (String value : entry.getValue())
			{
				buf.put(entry.getKey().getBytes());
				buf.put(":".getBytes());
				buf.put(value.getBytes());
				buf.put("\r\n".getBytes());
			}
		}
		buf.put("\r\n".getBytes()); // ��ӿ���
		buf.flip();
		//String printstr = getString(buf);
		//System.out.println("GvapPackage:"+printstr);
		return buf;
	}

	public byte[] getContent()
	{
		return content;
	}

	public GvapCommand getCommandId()
	{
		return commandId;
	}

	public boolean isWaitForResp()
	{
		return waitForResp;
	}

	public void setWaitForResp(boolean waitForResp)
	{
		this.waitForResp = waitForResp;
	}

	public String getParent_group()
	{
		return parent_group;
	}

	public void setParent_group(String parent_group)
	{
		this.parent_group = parent_group;
	}
}
