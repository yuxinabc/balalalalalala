package com.my51c.see51.protocal;

import android.util.Log;

import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.Group;
import com.my51c.see51.data.UserInfo;

import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.helpers.DefaultHandler;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

// 
public class GvapXmlParser
{
	public static String TAG = "GvapXmlParser"; 
	private static void parse(String xmlString, DefaultHandler xmlHandler)
	{
		try
		{
			SAXParserFactory factory = SAXParserFactory.newInstance();
			SAXParser parser = factory.newSAXParser();
			XMLReader xmlReader = parser.getXMLReader();
			xmlReader.setContentHandler(xmlHandler);

			StringReader read = new StringReader(xmlString);
			// �����µ�����ԴSAX ��������ʹ�� InputSource������ȷ����ζ�ȡ XML ����
			InputSource source = new InputSource(read);
			xmlReader.parse(source);
			read.close();
		} catch (FactoryConfigurationError e)
		{
			Log.w("GvapXmlParser", "xmlString: " + xmlString);
			e.printStackTrace();
		} catch (SAXException e)
		{
			Log.w("GvapXmlParser", "xmlString: " + xmlString);
			e.printStackTrace();
		} catch (ParserConfigurationException e)
		{
			Log.w("GvapXmlParser", "xmlString: " + xmlString);
			e.printStackTrace();
		} catch (IOException e)
		{
			Log.w("GvapXmlParser", "xmlString: " + xmlString);
			e.printStackTrace();
		}
	}

	public static void parseDevInfo(String xmlString, DeviceList devlist)
	{
		
		int a = xmlString.length()/2;
		String str1 = xmlString.substring(0, a);
		String str2 = xmlString.substring(a);
		//Log.i(TAG, "��һ���֣�"+str1);
		//Log.i(TAG, "�ڶ����֣�"+str2);
		if (devlist == null
				|| xmlString == null
				|| (devlist.getDeviceCount() == 0 && devlist.getGroupCount() == 0))
			return;
		if (devlist.getDeviceCount() != 0)
		{
			DeviceInfoParser xmlHandler = new DeviceInfoParser(devlist);
			parse(xmlString, xmlHandler);
		}
		if (devlist.getGroupCount() != 0)
		{
			GroupInfoParser xmlHandler = new GroupInfoParser(devlist);
			parse(xmlString, xmlHandler);
		}
	}

	public static List<UserInfo> parseUserInfo(String xmlString)
	{
		if (xmlString == null)
			return null;
		UserInfoParser xmlHandler = new UserInfoParser();
		parse(xmlString, xmlHandler);
		return xmlHandler.getUserList();
	}

	// �豸��Ϣ���豸����״̬XML������
	protected static class DeviceInfoParser extends DefaultHandler
	{
		private DeviceList devList;
		private Device currentDev;
		private String currentTag;
		private StringBuilder sb;  
		private boolean flag = false;  

		public DeviceInfoParser(DeviceList devlist)
		{
			currentDev = null;
			this.devList = devlist;
			currentTag = "";
		}

		@Override
		public void startDocument() throws SAXException
		{

		}

		@Override
		public void endDocument() throws SAXException
		{
			// TODO Auto-generated method stub
			super.endDocument();
		}

		// Ԫ�ؿ�ʼ

		@Override
		public void startElement(String uri, String elementName, String qName,
				Attributes attributes)
		{
			if ("dev".equals(elementName))
			{
				String strId = attributes.getValue("id");
				if (strId != null)
				{
					currentDev = this.devList.getDevice(strId);
				}
				String status = attributes.getValue("status");
				String alarm = attributes.getValue("alarm");
				//Log.d(TAG,"status:"+status);
				//if(alarm != null)
				//Log.e(TAG,"		,alarm:"+alarm);
				String url = attributes.getValue("url");
				if (currentDev != null && status != null)
				{
					currentDev.setDevStatus(Integer.parseInt(status), url);
				}
				
			} else
			{
				currentTag = elementName;
			}
			
			sb = new StringBuilder();  
			flag = true;
		}

		// Ԫ�ؽ����¼�
		@Override
		public void endElement(String uri, String elementName, String qName)
				throws SAXException
		{
			
			flag = false; 
			String value = sb.toString();
			
			if (currentDev != null)
			{
				if (currentTag.equals("name"))
				{
					currentDev.getSee51Info().setDeviceName(value);
				} else if (currentTag.equals("hversion"))
				{
					currentDev.getSee51Info().setHwVersion(value);
				} else if (currentTag.equals("sversion"))
				{
					currentDev.getSee51Info().setSwVersion(value);
				} else if (currentTag.equals("type"))
				{
					currentDev.getSee51Info().setType(Integer.parseInt(value));
				}
				else if (currentTag.equals("url"))
				{
					currentDev.getSee51Info().setDataURL(value);
				}
				else if(currentTag.equals("remark2"))
				{
					currentDev.getSee51Info().setLocation(value);
				}
				
			}
			
			
			if ("dev".equals(elementName)) // �������豸������
			{
				this.currentDev = null;
			} else
			{
				currentTag = ""; // ���tag���
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException
		{
			 if(!flag) 
			 {    
		        return;    
		     }   
			  
			 sb.append(new String(ch, start, length) );  
		}
	}

	// ����ϢXML������
	protected static class GroupInfoParser extends DefaultHandler
	{
		private DeviceList devList;
		private Group currentGro;
		private String currentTag;
		private StringBuilder sb;
		private boolean flag = false;
		 
		public GroupInfoParser(DeviceList devlist)
		{
			currentGro = null;
			this.devList = devlist;
			currentTag = "";
		}

		@Override
		public void startDocument() throws SAXException
		{

		}

		@Override
		public void endDocument() throws SAXException
		{
			// TODO Auto-generated method stub
			super.endDocument();
		}

		// Ԫ�ؿ�ʼ

		@Override
		public void startElement(String uri, String elementName, String qName,
				Attributes attributes)
		{
			if ("group".equals(elementName))
			{
				String strId = attributes.getValue("id");
				if (strId != null)
				{
					currentGro = this.devList.getGroup(strId);
				}
				String groupName = attributes.getValue("name");
				String version = attributes.getValue("version");
				if (currentGro != null && groupName != null)
				{
					// ����
					currentGro.setGroupName(groupName);
				}
				if (currentGro != null && version != null)
				{
					// �汾
					currentGro.setVersion(version);
				}
			} else
			{
				currentTag = elementName;
			}
			
			flag = true;
			sb = new StringBuilder();  
		}

		// Ԫ�ؽ����¼�
		@Override
		public void endElement(String uri, String elementName, String qName)
				throws SAXException
		{
			if ("group".equals(elementName))// �������������
			{
				this.currentGro = null;
			} else
			{
				currentTag = ""; // ���tag���
			}
			
			flag = false;
			
			String value = sb.toString();
			if (currentGro != null)
			{
				if (currentTag.equals("name"))
				{
					//Log.d("love", "valueName:" + value);
				} else if (currentTag.equals("version"))
				{
					//Log.d("love", "valueVersion:" + value);
				} else
				{
					//Log.d("love", "value:" + value);
				}
			}
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException
		{
			
			if(!flag) {    
	            return;    
	        }  
			
			sb.append(new String(ch, start, length) );
			
		}
	}

	// �û���Ϣ���豸����״̬XML������
	protected static class UserInfoParser extends DefaultHandler
	{
		private List<UserInfo> userList;
		private String currentTag;
		private UserInfo curUser;
		private StringBuilder sb;
		private boolean flag = false;

		public UserInfoParser()
		{
			this.userList = new ArrayList<UserInfo>();
			curUser = null;
			currentTag = "";
		}

		@Override
		public void startDocument() throws SAXException
		{

		}

		@Override
		public void endDocument() throws SAXException
		{
			// TODO Auto-generated method stub
			super.endDocument();
		}

		// Ԫ�ؿ�ʼ

		@Override
		public void startElement(String uri, String elementName, String qName,
				Attributes attributes)
		{
			if ("usr".equals(elementName))
			{
				curUser = new UserInfo(attributes.getValue("usrname"));
			} else
			{
				currentTag = elementName;
			}
			
			flag = true;
			sb = new StringBuilder(); 
		}

		// Ԫ�ؽ����¼�
		@Override
		public void endElement(String uri, String elementName, String qName)
				throws SAXException
		{
			if ("usr".equals(elementName)) // �������豸������
			{
				if (curUser != null)
				{
					userList.add(curUser);
				}
			} else
			{
				currentTag = ""; // ���tag���
			}
			
			
			String value = sb.toString();
			if (curUser != null)
			{
				if ("name".equals(this.currentTag))
				{
					curUser.setNickname(value);
				}
			}
			
			flag = false;
		}

		@Override
		public void characters(char[] ch, int start, int length)
				throws SAXException
		{
			
			if(!flag) {    
	            return;    
	        }    
	        sb.append(new String(ch, start, length) );
			
		}

		public List<UserInfo> getUserList()
		{
			return this.userList;
		}
	}
}
