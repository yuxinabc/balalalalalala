package com.my51c.see51.service;

import android.text.format.Time;
import android.util.Log;

import com.my51c.see51.protocal.GvapCommand;
import com.my51c.see51.protocal.GvapPackage;
import com.my51c.see51.service.GvapEvent.GvapEventListener;

import java.io.IOException;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.IllegalBlockingModeException;
import java.nio.channels.SelectionKey;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

public class GvapServer
{
	private String hostName;
	private int port;
	private SocketChannel serverSocketChannel; // 锟矫伙拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷socket锟斤拷一锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	private boolean connect;
	private int packageSeq;
	private int hbExpire;
	private Time lastHb;
	private ArrayList<GvapEventListener> eventListner;
	/**锟饺达拷锟斤拷锟酵的帮拷*/
	private List<GvapPackage> preSendList; 
	/** 锟窖凤拷锟酵ｏ拷锟饺达拷锟截革拷锟侥帮拷*/
	private List<GvapPackage> sentOverList; // 锟窖凤拷锟酵ｏ拷锟饺达拷锟截革拷锟侥帮拷
	protected int request_timeout; // 锟斤拷锟斤拷时时锟戒，锟斤拷位锟斤拷
	
	private final String TAG = "GvapServer";
	
	final static int RECV_RESET = 0;
	final static int RECV_4BYTES = 1;
	final static int RECV_HEAD = 2;
	final static int RECV_CONTENT = 3;
	final static int RECV_COMPLETE = 4;
	int recvState = RECV_RESET;

	ByteBuffer lengthBuffer = ByteBuffer.allocate(4);
	ByteBuffer headBuffer;
	ByteBuffer contentBuffer;
	GvapPackage recvPackage = null;

	public GvapServer(String hostName, int port)
	{
		this.hostName = hostName;
		this.port = port;
		this.connect = false;
		serverSocketChannel = null;
		hbExpire = 0;
		lastHb = new Time();
		this.preSendList = new Vector<GvapPackage>();
		this.sentOverList = new Vector<GvapPackage>();
		this.eventListner = new ArrayList<GvapEventListener>();
		resetReceive();
	}
	
	public void resetUrl(String hostName, int port)
	{
		this.hostName = hostName;
		this.port = port;
	}
	
	/**lengthBuffer.clear()*/
	private void resetReceive()
	{
		recvState = RECV_RESET;
	}

	// 锟斤拷锟接凤拷锟斤拷锟斤拷锟斤拷selector为锟斤拷锟接成癸拷锟斤拷注锟斤拷锟斤拷锟较拷锟窖★拷锟斤拷锟斤拷锟� sendList为锟斤拷锟酵成癸拷锟斤拷锟斤拷谋锟斤拷锟斤拷斜锟�
	public boolean open(Selector selector)
	{
		SocketAddress address = getServerAddress();
		if (address != null)
		{
			try
			{
					Log.i(TAG, "--open:" + address.toString());
					serverSocketChannel = SocketChannel.open(address);//锟津开硷拷锟斤拷锟脚碉拷
					this.connect = true;
					Log.i(TAG, "--opened:" + address.toString());
					serverSocketChannel.configureBlocking(false);	
					//channel锟斤拷锟斤拷锟斤拷模式锟斤拷 锟斤拷Selector一锟斤拷使锟斤拷时锟斤拷Channel锟斤拷锟诫处锟节凤拷锟斤拷锟斤拷模式锟铰ｏ拷
					if(selector.isOpen())
					{
						serverSocketChannel.register(selector, SelectionKey.OP_READ, this); /*锟斤拷channel注锟结到selector锟较ｏ拷p1-通锟斤拷锟斤拷锟斤拷锟斤拷-选锟斤拷锟斤拷锟饺わ拷锟酵拷锟斤拷锟斤拷录锟斤拷锟�
																													p2-锟斤拷锟斤拷趣锟斤拷锟铰硷拷锟斤拷锟斤拷锟斤拷锟斤拷锟铰硷拷锟斤拷select()锟斤拷锟斤拷锟结返锟截讹拷锟铰硷拷锟窖撅拷锟斤拷锟斤拷锟斤拷锟斤拷些通锟斤拷锟斤拷*/
					}
					else
					{
						return false;
					}
					
					lastHb.setToNow();
					//Log.i(TAG, "--success!-" + address.toString());
					return true;
			} 
			catch (IOException e)
			{
				e.printStackTrace();
				this.connect = false;
				if (this.preSendList.size() > 0) // 锟斤拷锟饺达拷锟斤拷锟酵碉拷锟叫憋拷为锟斤拷时锟斤拷通知锟斤拷锟斤拷
				{
					//Log.i(TAG, "94锟斤拷onGvapEvent锟斤拷锟斤拷失锟斤拷");
					GvapEvent event = GvapEvent.CONNECT_FAILED;
					event.setCommandID(preSendList.get(0).getCommandId());
					this.onGvapEvent(event);
//					this.preSendList.clear();// 锟斤拷锟斤拷锟斤拷锟斤拷斜锟�
				}
				
			} catch (NullPointerException e)
			{
				// TODO: handle exception
				this.connect = false;
				e.printStackTrace();
			}catch (IllegalBlockingModeException e) {
				// TODO: handle exception
				this.connect = false;
				e.printStackTrace();
			}finally{
				
			}
		} else
		{
			setNetError(GvapEvent.NETWORK_ERROR);
			// this.preSendList.clear();
			
		}
		
		this.connect = false;
		return false;
	}
	
	public void setNetError(GvapEvent e){
		this.connect = false;
		GvapEvent event = e;
		if (preSendList.size() > 0)
		{
			event.setCommandID(preSendList.get(0).getCommandId());
		}
		this.onGvapEvent(e); 
	}
	
	public void clearpreSendList(){
		this.preSendList.clear();
	}

	public void receiveGvapFrame(SocketChannel socketChannel) throws IOException
	{
		GvapPackage gvapPackage = null;
		try
		{
			gvapPackage = receiveGvapPackage(socketChannel);
			if (gvapPackage != null)
			{
				onPackageReceived(gvapPackage);
				resetReceive();
			}
		} 
		catch (Exception e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
			resetReceive();
			close();
			onGvapEvent(GvapEvent.CONNECTION_RESET);
			throw new IOException();
		}
		
	}
	
	private GvapPackage receiveGvapPackage(SocketChannel sc) throws Exception
	{
		switch (recvState)
		{
		case RECV_RESET:
			lengthBuffer.clear();
		case RECV_4BYTES:
			recvState = RECV_4BYTES;
			sc.read(lengthBuffer);
			if (!lengthBuffer.hasRemaining())
			{
				int headLen = 0;
				try
				{
					headLen = Integer.parseInt(new String(lengthBuffer.array()), 16);
				} catch (NumberFormatException e)
				{
					// TODO Auto-generated catch block
					Log.w(TAG, "lengthBuffer: " + new String(lengthBuffer.array()));
					e.printStackTrace();
					throw new IOException();
				}
				if (headLen > 0)
				{
					headBuffer = ByteBuffer.allocate(headLen);
					headBuffer.clear();
					recvState = RECV_HEAD;
				} else
				{
					recvState = RECV_RESET;
				}
			}
			break;
		case RECV_HEAD:
			sc.read(headBuffer);
			if (!headBuffer.hasRemaining())
			{
				recvPackage = new GvapPackage(headBuffer.array());
				int contentLen = recvPackage.getContentLength();
				if (contentLen > 0)
				{
					contentBuffer = ByteBuffer.allocate(contentLen);
					contentBuffer.clear();
					recvState = RECV_CONTENT;
				} else
				{
					recvState = RECV_COMPLETE;
					return recvPackage;
				}
			}
			break;
		case RECV_CONTENT:
			sc.read(contentBuffer);
			
			if (!contentBuffer.hasRemaining())
			{
				recvPackage.appendContent(contentBuffer.array());
				recvState = RECV_COMPLETE;
				return recvPackage;
			}
			break;
		case RECV_COMPLETE:
			break;
		default:
			break;
		}
		return null;
	}
	
	// 锟斤拷锟斤拷锟斤拷息
	private void onPackageReceived(GvapPackage receivedPack)
	{
		// 锟矫帮拷锟窖撅拷锟斤拷锟截ｏ拷锟接等达拷锟截革拷锟叫憋拷锟斤拷删锟斤拷
		//Log.d(TAG, "onPackageReceived: " + receivedPack.getStatusCode());

		switch (receivedPack.getType())
		{
			case 1:
				processRequest(receivedPack);
				break;
			case 2://锟斤拷锟斤拷锟酵碉拷录锟斤拷锟筋、锟斤拷取锟叫憋拷锟斤拷锟斤拷锟侥凤拷锟斤拷锟斤拷息
			{
				String cseq = receivedPack.getParam("cseq");//1(2,3)//from GvapPackage.parseParamLine锟斤拷锟斤拷
				GvapPackage sentPack = null;
				if (cseq != null) //
				{
					for (GvapPackage pack : sentOverList)//sentOverList:锟窖凤拷锟酵的帮拷锟斤拷锟揭等达拷锟截革拷锟侥帮拷锟斤拷
					{
						if (cseq.equals(pack.getParam("cseq")))//pack.getParam("cseq"):from sendPackage->pack.addParam("cseq", Integer.toString(packageSeq++))
						{
							sentPack = pack;
							sentOverList.remove(pack);
							break;
						}
					}
					if (sentPack != null) // 锟斤拷锟絪entPack为null锟斤拷锟斤拷锟斤拷锟揭伙拷锟斤拷锟绞憋拷锟斤拷氐陌锟斤拷锟斤拷锟斤拷锟街�
					{
						processResponse(sentPack, receivedPack);
					}
				}
			}
				break;
		}
	}
	
	/** 锟斤拷锟斤拷锟缴凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷*/
	private boolean processRequest(GvapPackage request)
	{
			String szCmd = request.getCommand();
			String szResource = request.getResourceName();
			//Log.d(TAG, "processRequest:" + szCmd + " " + szResource);

			GvapEvent event = GvapEvent.SERVER_REQUEST;
			event.setCommandID(request.getCommandId());
			Log.i(TAG, "processRequest-CommandId:"+request.getCommand().toString());
			event.attach(request);
			this.onGvapEvent(event); // 锟斤拷锟斤拷锟较层处锟斤拷
			return true;
	}

	/**锟斤拷锟斤拷锟缴凤拷锟斤拷锟斤拷锟斤拷锟斤拷锟侥回革拷--通知锟斤拷UI*/
	private boolean processResponse(GvapPackage request, GvapPackage response)
	{
			String szCmd = request.getCommand();
			String szResource = request.getResourceName();
			int retCode = response.getStatusCode();//GvapPackage.parseCommandLine锟斤拷值
			//Log.d(TAG, "processResponse " + szCmd + " " + szResource + " " + retCode+"content:"+response.getContent());

			GvapEvent event;
			if (retCode == 200)
			{
				event = GvapEvent.OPERATION_SUCCESS;
			} else
			{
				Log.i(TAG, "--567 锟斤拷锟斤拷锟斤拷锟斤拷失锟斤拷-retCode="+retCode);
				event = GvapEvent.OPERATION_FAILED;
			}
			event.attach(response);
			event.setCommandID(request.getCommandId());//
			Log.i(TAG, "processResponse-CommandId:"+request.getCommandId().toString());
			event.setRequest(request);
			this.onGvapEvent(event); // 锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷息锟斤拷锟较诧拷
			return true;
	}
	
	
	public void close()
	{
		if (this.serverSocketChannel != null && this.isConnect())
		{
			
			Log.d(TAG, "GvapServer Close!");
			try
			{
				this.serverSocketChannel.close();
				this.serverSocketChannel.socket().close(); 
				this.serverSocketChannel = null;
			} 
			catch (IOException e)
			{
				e.printStackTrace();
			}
		}
		/*
		this.sentOverList.clear();
		this.preSendList.clear();
		*/
		this.connect = false;
		//Log.d(TAG, "connect changed from close");
	}

	public boolean isConnect()
	{
		// if (serverSocketChannel != null)
		// //Log.d(TAG, "serverSocketChannel.isConnected():" +
		// serverSocketChannel.isConnected());
		/*
		if (serverSocketChannel != null)
		{	
			this.connect = serverSocketChannel.isConnected();
		}
		else
		*/
		if (serverSocketChannel == null)
		{
			this.connect = false;
		}
		
		return this.connect;
	}

	// 锟斤拷锟酵帮拷锟斤拷pack-要锟斤拷锟酵的帮拷锟斤拷cseq-锟斤拷锟斤拷锟斤拷锟�, waitForResp -- 锟角凤拷锟斤拷要锟饺达拷锟斤拷应
	public boolean sendPackage(GvapPackage pack, boolean waitForResp)
	{
		pack.addParam("cseq", Integer.toString(packageSeq++));
		//Log.d("GvapPackage---",""+pack.getCommand()+" "+pack.getResourceName());
		//Log.d("GvapPackage---","cseq:"+pack.getParam("cseq"));
		if (waitForResp)
		{
			pack.setSendTime();
			pack.setWaitForResp(waitForResp);
		}
		synchronized (preSendList)
		{
			this.preSendList.add(pack);
		}
		return true;
	}

	// 锟斤拷取锟矫伙拷锟斤拷锟斤拷锟斤拷IP锟斤拷址
	private SocketAddress getServerAddress()
	{
//		for (String host : hostName)
		{
			try
			{
				InetAddress addr = InetAddress.getByName(hostName);
				return new InetSocketAddress(addr, port); // 锟斤拷锟斤拷锟缴癸拷锟酵凤拷锟斤拷
			} catch (UnknownHostException e)
			{
				e.printStackTrace();
			}
		}
		return null;
	}

	private int receive(SocketChannel sc, ByteBuffer buf, int length) throws IOException
	{
		int totalLen = 0;
		while (length > totalLen)
		{
			int readLen = sc.read(buf);
			if (readLen > 0)
			{
				totalLen += readLen;
			} else if (readLen < 0)
			{
				//Log.e(TAG, "readLen " + readLen + " bytes");
				break;
			}
		}
	
		return totalLen;
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
	
	public void setHbEXpire(int hearbeat_expire)
	{
		this.hbExpire = hearbeat_expire - 20;
	}

	private Time now = new Time();

	public void checkHearBeat()
	{
		now.setToNow();

		if (this.hbExpire != 0 && now.toMillis(false) - lastHb.toMillis(false) >= this.hbExpire * 1000)
		{
			GvapPackage hbPack = new GvapPackage(GvapCommand.CMD_HB);
			this.sendPackage(hbPack, false);
			lastHb.setToNow();
		}
	}

	public void addGvapEventListener(GvapEventListener o)
	{
		synchronized (this)
		{
			if (!this.eventListner.contains(o))
			{
				this.eventListner.add(0, o);
			}
		}
	}

	public void removeGvapEventListener(GvapEventListener o)
	{
		synchronized (this)
		{
			if (this.eventListner.contains(o))
			{
				this.eventListner.remove(o);
			}
		}
	}

	public void removeGvapEventListenerAll()
	{
		synchronized (this)
		{
			this.eventListner.clear();
		}
	}
	
	public void onGvapEvent(GvapEvent event)
	{
		synchronized (this)
		{
			for (GvapEventListener listener : eventListner)
			{
				listener.onGvapEvent(event);
			}
		}
	}

	public void checkSendList()
	{
		if (this.preSendList.size() > 0)
		{
			synchronized (preSendList)
			{
				try
				{
					GvapPackage pack = this.preSendList.get(0);
					if(this.serverSocketChannel != null)
					{
						this.serverSocketChannel.write(pack.getByteBuffer());
						Log.i(TAG, "checkSendList-send-Cmd:"+pack.getCommand()+pack.getCommandId());
						
						if (pack.isWaitForResp())
						{
							this.sentOverList.add(pack);
						}
						this.preSendList.remove(0);
					}
				} 
				catch (IOException e)
				{
					e.printStackTrace();
					Log.d(TAG, "connect changed from checkSendList's IOException");
					this.connect = false;
				} catch (Exception e)
				{
					this.connect = false;
					e.printStackTrace();
				}
			}
		}
	}
	
	public void checkUnSendList()
	{
		if (this.request_timeout > 0 && preSendList.size() > 0)
		{
			synchronized (preSendList)
			{
				try {
					GvapPackage pack;
					pack = preSendList.get(0);
					// 锟斤拷锟筋超时
					while (System.currentTimeMillis() - pack.getSendTime() > this.request_timeout * 1000)
					{
						preSendList.remove(0); // 删锟斤拷锟斤拷时锟侥帮拷
						this.onPackageTimeout(pack);//通知锟较诧拷锟斤拷锟斤拷锟绞�
						// 锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷时锟斤拷锟斤拷
						if (preSendList.size() > 0)
							pack = preSendList.get(0);
						else
							break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}

	public void checkTimeout()
	{
		if (this.request_timeout > 0 && sentOverList.size() > 0)
		{
			synchronized (sentOverList)
			{
				try {
					GvapPackage pack;
					pack = sentOverList.get(0);
					// 锟斤拷锟筋超时
					while (System.currentTimeMillis() - pack.getSendTime() > this.request_timeout * 1000)
					{
						sentOverList.remove(0); // 删锟斤拷锟斤拷时锟侥帮拷
						this.onPackageTimeout(pack);//通知锟较诧拷锟斤拷锟斤拷锟绞�
						// 锟斤拷锟斤拷锟斤拷一锟斤拷锟斤拷时锟斤拷锟斤拷
						if (sentOverList.size() > 0)
							pack = sentOverList.get(0);
						else
							break;
					}
				} catch (ArrayIndexOutOfBoundsException e) {
					// TODO Auto-generated catch block
					Log.i(TAG, "--checkTimeout-ArrayIndexOutOfBoundsException");
					e.printStackTrace();
				}
			}
		}
	}

	private boolean onPackageTimeout(GvapPackage pack)
	{
		// 锟斤拷锟筋超时锟斤拷锟斤拷
		GvapEvent event = GvapEvent.OPERATION_TIMEOUT;
		event.setCommandID(pack.getCommandId());
		event.attach(pack);
		this.onGvapEvent(event); // 通知锟斤拷锟较诧拷
		return true;
	}
	
	public void setRequest_timeout(int requestTimeout)
	{
		this.request_timeout = requestTimeout;

	}

	//锟斤拷锟斤拷锟斤拷
	public void receivePackage()
	{
		ByteBuffer lenBuf = ByteBuffer.allocate(4);
		lenBuf.clear();// 锟斤拷锟斤拷洗谓锟斤拷盏锟斤拷锟斤拷锟斤拷锟�
		int ret;
		try
		{
			ret = receive(serverSocketChannel, lenBuf, 4);
			if (ret == 4)
			{
				// 锟斤拷取锟斤拷头
				int headLen = Integer.parseInt(new String(lenBuf.array()), 16);
				if (headLen > 0)
				{
					ByteBuffer headBuf = ByteBuffer.allocate(headLen);
					if (receive(serverSocketChannel, headBuf, headLen) == headLen)
					{
						// 锟斤拷锟斤拷锟斤拷锟斤拷转锟斤拷锟斤拷gvap锟斤拷锟斤拷锟斤拷
						GvapPackage receivedPack = new GvapPackage(headBuf.array());
						int contentLen = receivedPack.getContentLength();
						if (contentLen > 0)
						{
							ByteBuffer contentBuf = ByteBuffer.allocate(contentLen);
							if (receive(serverSocketChannel, contentBuf, contentLen) == contentLen)
							{
								receivedPack.appendContent(contentBuf.array());
							}
						}
						this.onPackageReceived(receivedPack); // 锟斤拷锟斤拷锟斤拷息
					}
				}
			} else
			// 锟斤拷锟斤拷
			{
				//Log.e(TAG, "receive " + ret + " bytes");
				throw new IOException();
			}
		} catch (IOException e)
		{
			this.close(); // 锟届常锟斤拷锟斤拷乇锟絪ocket
			e.printStackTrace();
		}
	}

}
