package com.my51c.see51.sdk;

import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.DeviceSee51Info;
import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.protocal.GvapPackage;
import com.my51c.see51.protocal.GvapXmlParser;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.channels.DatagramChannel;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;

public class See51Delegate {
	
	final static String hostName[] = { //"192.168.20.171",//server replace here by marshal
										"user.hddvs.net"
										//,"user.my51c.net"
									  };
	final static int 	USER_SERVER_PORT = 5590;
	final static int SEARCH_TIMEOUT = 3000; // �����ȴ�ʱ��
	final static int CONNECT_TIMEOUT = 10000; // ���ӷ������ȴ�ʱ��
	final static int sndPort = 8628;
	final static int rcvPort = 8629;
	
	final static String USERNAME_STRING = "schyjxsmkj_sdk"; // �Ĵ���Դ��Ѹ����Ƽ�
	final static String PASSWORD_STRING = "schyjxsmkj2013&";
	
	//final static String USERNAME_STRING = "klsj_sdk_android";  //��Ѷ�Ƽ� �ͻ� �����ӽ�
	//final static String PASSWORD_STRING = "klsj2013&";	
	//final static String USERNAME_STRING = "gzngxm-sdk-android";  //�����Ϲ���÷(˼���Ƽ�)
	//final static String PASSWORD_STRING = "gzngxm2012&";
	//final static String USERNAME_STRING = "zhdx-sdk-android";    //�к궦��
	//final static String PASSWORD_STRING = "zhdx2012&";	
	private final static String SDK_VERSION = "20130328"; 
	
	
	/**
	 * ��ȡ�����豸�Ĳ���ip
	 * @param deviceId �豸id
	 * @return �豸�Ĳ���ip��ַ��null-û���ҵ��豸����null���ַ�������Ϊ0�����ʾ�ҵ��豸�����޷�������Ƶ���������ô���
	 */
	public static String getLocalUrl(String deviceId)
	{
		//Log.d("See51Delegate", "getLocalUrl"); 
		String retString = null;
		SDKLocalInterface localInterface = new SDKLocalInterface();
		if(localInterface.open())
		{
			//Log.d("See51Delegate", "localInterface.open() ok!");
			Device device = localInterface.searchDevice(deviceId);
			if(device != null)
			{
				//Log.d("See51Delegate", "url : " + device.getPlayURL());
				return device.getPlayURL();
			}else{
				//Log.d("See51Delegate", "device = null ");
			}
		}else{
			//Log.d("See51Delegate", "localInterface.open() failed!");
		}
		return retString;
	}
	
	public static DeviceList getLocalDeviceList(int timeout){
		SDKLocalInterface localInterface = new SDKLocalInterface();
		if(localInterface.open())
		{
			return localInterface.searchDeviceList(timeout);
		}
		return null;
	}
	
	/**
	 * ��ȡԶ���豸���ŵ�ַ
	 * @param deviceId �豸ID
	 * @return �豸���ŵ�ַ��null -��ȡʧ�ܣ���null�����ַ���--�豸������
	 */
	public static String getRemoteUrl(String deviceId)
	{
		String retString = null;
		SDKRemoteInterface gvapInterface = new SDKRemoteInterface();
		if(gvapInterface.connect() && gvapInterface.login())
		{
			retString = gvapInterface.getDevDataUrl(deviceId);
		}
		//Log.d("See51Delegate", "getRemoteUrl: " +retString);
		return retString;
	}
	
	private static class SDKRemoteInterface
	{
		private Selector selector;
		private SocketChannel peerChannel;
		private DeviceList deviceList;
		public SDKRemoteInterface()
		{
			deviceList = new DeviceList();
		}
		private SocketAddress getServerAddress() {
			
			for(String host : hostName)
			{
				try {
					InetAddress addr = InetAddress.getByName(host);
					return new InetSocketAddress(addr, USER_SERVER_PORT); // �����ɹ��ͷ��� 
				} catch (UnknownHostException e) {
					e.printStackTrace();
				}
			}
			return null;
		}	
		public boolean connect()
		{
			SocketAddress address = getServerAddress();
			if(address != null)
			{
				try
				{
					selector = Selector.open(); 
					//Log.d("See51Delegate", "Connect to:" + address.toString());
					// ����socket
					peerChannel=SocketChannel.open();
					peerChannel.configureBlocking(false);
					peerChannel.register(selector, SelectionKey.OP_READ);
					if(!peerChannel.connect(address)) //������ʽ��û���������ӳɹ�����ȴ����ӳɹ�
					{
						long startTime = System.currentTimeMillis();	
						while(!peerChannel.finishConnect()) //�ȴ��������
						{
							if(System.currentTimeMillis() - startTime >= CONNECT_TIMEOUT ) //���ӳ�ʱ
							{
								//Log.d("See51Delegate", "Connect time out " + address.toString());
								break;
							}
							try {
								Thread.sleep(100);
							} catch (InterruptedException e) {
									e.printStackTrace();
							}
						}
						//Log.d("See51Delegate", "Connect finished " + address.toString());
					}
				}
				catch(IOException e)
				{
					e.printStackTrace();
				}
			}	
			if(peerChannel != null)
				return peerChannel.isConnected();
			else
				return false;
		}
		private int receive(SocketChannel sc, ByteBuffer buf, int length) throws IOException
		{
			int totalLen = 0;
			while(length > totalLen)
			{
				int readLen = sc.read(buf);
				if(readLen > 0)
				{
					totalLen += readLen;
				}
				else if(readLen < 0)
				{
					break;
				}                            		
			}
			return totalLen;
		}
		private GvapPackage receivePackage()
		{
			GvapPackage receivedPack = null;
			ByteBuffer lenBuf = ByteBuffer.allocate(4);
	    	try
	    	{		
		    	if(receive(peerChannel, lenBuf, 4) == 4)
		    	{
		    		// ��ȡ��ͷ
		    		int headLen = Integer.parseInt(new String(lenBuf.array()), 16);
		    		if(headLen > 0)
		    		{
		    			ByteBuffer headBuf = ByteBuffer.allocate(headLen);
		    			if(receive(peerChannel, headBuf, headLen) == headLen)
		    			{
		        			// ��������ת����gvap������
		        			receivedPack = new GvapPackage(headBuf.array());
		        			int contentLen = receivedPack.getContentLength();	
		        			if(contentLen > 0)
		        			{
		        				ByteBuffer contentBuf = ByteBuffer.allocate(contentLen);
		        				if(receive(peerChannel, contentBuf, contentLen) == contentLen)
		            			{
		        					receivedPack.appendContent(contentBuf.array());
		            			}
		        			}
		    			}                       			
		    		}
		      	}
		    	else  // ����
		    	{
		    		throw new IOException();
		    	}	
	    	}
	    	catch(IOException e)
	    	{
	    		e.printStackTrace();	    		
	    	}
	    	return receivedPack;
		}
		private GvapPackage forceReceivePackage(int timeout)
		{			
			try 
			{
				long startTime = System.currentTimeMillis();	
				do
				{
					if(selector.select(200) > 0)
			    	{
			    		 for(SelectionKey sk:selector.selectedKeys())
			    		 {
			    			 //ɾ�����ڴ����selectionkey
			                 selector.selectedKeys().remove(sk);
			                 if(sk.isReadable())
			                 {		
			                     sk.interestOps(SelectionKey.OP_READ);
			                	 return receivePackage();			                	
			                 }
			    		 }
			    	}
					else if(System.currentTimeMillis() - startTime >= timeout ) 
		    		 {
						 //Log.d("See51Delegate", "forceReceivePackage timeout" + System.currentTimeMillis() + " " + startTime);
		    			 break; //��¼��ʱ 
		    		 }
				}while(true);
			}
			 catch (IOException e) {
					e.printStackTrace();
				}
		
			return null;
		}
		private boolean login()
		{
			//Log.d("See51Delegate","send Login Command");
			GvapPackage loginPack = new GvapPackage(GvapCommand.CMD_LOGIN);
			loginPack.addParam("username",USERNAME_STRING );
			loginPack.addParam("password",PASSWORD_STRING);
			loginPack.addParam("sdk", SDK_VERSION); // ���sdk��־
			if(sendPacakge(loginPack))
			{
				GvapPackage response = forceReceivePackage(CONNECT_TIMEOUT);
				if(response != null)
				{
					//Log.d("See51Delegate", "login returned " + response.getStatusCode());
					return response.getStatusCode() == 200;
				}
				else
				{
					//Log.d("See51Delegate", "login failed");
				}
			}		
			return false;
		}
		private String getDevDataUrl(String deviceId)
		{
			GvapPackage loginPack = new GvapPackage(GvapCommand.CMD_GET_DEVSTATUS);
			loginPack.addParam("device-id",deviceId );
			if(sendPacakge(loginPack))
			{
				GvapPackage response = forceReceivePackage(CONNECT_TIMEOUT);
				if(response != null)
				{
					//Log.d("See51Delegate", "getDevDataUrl returned " + response.getStatusCode());
					if(response.getStatusCode() == 200)
					{
						DeviceSee51Info info = new DeviceSee51Info(deviceId);
						Device device = new Device();
						device.setSee51Info(info);
						deviceList.add(deviceId, device);
						GvapXmlParser.parseDevInfo(new String(response.getContent()), deviceList);
						//Log.d("See51Delegate", "getDevDataUrl status:" + device.getSee51Info().getStatus());
						if(device.getSee51Info().getStatus() == 2)
						{
							return device.getSee51Info().getDataURL();
						}
					}
					return "";
				}
			}	
			//Log.d("See51Delegate", "getDevDataUrl Failed");
			return null;
		}
		private boolean sendPacakge(GvapPackage pack)
		{
			try {
				peerChannel.write(pack.getByteBuffer());
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
	protected static class SDKLocalInterface
	{
		private DatagramChannel datagramChannel;
		private Selector selector;
		// ����
		
		public boolean open()
		{
			try {
				datagramChannel = DatagramChannel.open();
				datagramChannel.socket().setReuseAddress(true);
				datagramChannel.socket().bind(new InetSocketAddress(rcvPort));  
				datagramChannel.configureBlocking(false);  
				datagramChannel.socket().setBroadcast(true);
            
				selector = Selector.open();  
				datagramChannel.register(selector, SelectionKey.OP_READ); 
			} catch (IOException e) {
				e.printStackTrace();
				return false;
			} 
			return true;
		}
		
		public DeviceList searchDeviceList(int timeout){
			timeout = timeout > 0? timeout*1000:SEARCH_TIMEOUT;
			DeviceList deviceList = new DeviceList(); 
			ByteBuffer byteBuf = ByteBuffer.allocate(1088);
			try
			{
				long start = System.currentTimeMillis();
	            do
	            {
	            	// ����������
	            	if(!searchLocal())
	            		break;
	            	long now = System.currentTimeMillis();
	            	if(selector.select(500) > 0)
	            	{	            		 
	            		 for(SelectionKey sk:selector.selectedKeys())
	            		 {
	            			 //ɾ�����ڴ����selectionkey
	                         selector.selectedKeys().remove(sk);
	                         if(sk.isReadable())
	                         {
	                        	 DatagramChannel sc=(DatagramChannel)sk.channel();
	                        	 byteBuf.clear();
	                        	 SocketAddress addr = sc.receive(byteBuf) ;
	                        	 byteBuf.flip();	
	                        	 
	                        	 DeviceLocalInfo localInfo = new DeviceLocalInfo(byteBuf); 
	                        	 deviceList.onReceivedMessage(localInfo, addr,false);	                            	 
	                             sk.interestOps(SelectionKey.OP_READ);
	                         }
	            		 }
	            	}
	            	if(now - start > timeout) // �����ȴ�SEARCH_TIMEOUTʱ��
	            	{
	            		break;
	            	}
	            }while(true);                      
			}
		
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				//Log.d("See51Delegate", ioe.getMessage());
			}			
			
			return deviceList;			
		}
		
		public Device searchDevice(String deviceId)
		{
			Device device = null; 
			ByteBuffer byteBuf = ByteBuffer.allocate(1088);
			try
			{
				long start = System.currentTimeMillis();
	            do
	            {
	            	// ����������
	            	if(!searchLocal())
	            	{	
	            		//Log.d("See51Delegate", "searchLocal() failed!");
	            		break;
	            	}
	            	long now = System.currentTimeMillis();
	            	if(selector.select(500) > 0)
	            	{
	            		 device = null;
	            		 for(SelectionKey sk:selector.selectedKeys())
	            		 {
	            			 //ɾ�����ڴ����selectionkey
	                         selector.selectedKeys().remove(sk);
	                         if(sk.isReadable())
	                         {
	                        	 DatagramChannel sc=(DatagramChannel)sk.channel();
	                        	 byteBuf.clear();
	                        	 
	                        	 SocketAddress addr = sc.receive(byteBuf) ;
	                        	 byteBuf.flip();	
	                        	 
	                        	 DeviceLocalInfo localInfo = new DeviceLocalInfo(byteBuf); 
	                        	 device = new Device();
	                        	 device.setLocalInfo(localInfo, addr);
	                        	 //Log.d("See51Delegate", "device.getID() : "+device.getID());
	                        	 //Log.d("See51Delegate", "deviceId : "+deviceId);
	                        	 	                        	 
	                        	 if(deviceId.equals(device.getID()))
	                        	 {
	                        		 ////Log.d("See51Delegate", "device.getID() : "+device.getID());	                        		 
	                        		 return device;
	                        	 }else{
	                        		 //Log.d("See51Delegate", "search device null");
	                        		 device = null;
	                        	 }       
	                             sk.interestOps(SelectionKey.OP_READ);
	                         }
	            		 }
	            	}
	            	if(now - start > SEARCH_TIMEOUT) // �����ȴ�SEARCH_TIMEOUTʱ��
	            	{
	            		//Log.d("See51Delegate", "searchLocal timeout !");
	            		break;
	            	}
	            }while(true);                      
			}		
			catch(IOException ioe)
			{
				ioe.printStackTrace();
				//Log.d("See51Delegate", ioe.getMessage());
			}
			return device;
		}
		private boolean searchLocal()
		{
			DeviceLocalInfo searchPack = new DeviceLocalInfo();
			searchPack.setCmd(0x56);
			searchPack.setPacketFlag("HdvsGet");
			try {
				datagramChannel.send(searchPack.toByteBuffer(), new InetSocketAddress("255.255.255.255", sndPort));
			} catch (IOException e){
				e.printStackTrace();
				return false;
			}
			return true;
		}
	}
}