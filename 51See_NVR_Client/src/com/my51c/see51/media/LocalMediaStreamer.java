//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : See51
//  @ File Name : LocalMediaStreamer.java
//  @ Date : 2012-5-30
//  @ Author : Eric Guo <gjl@my51c.com>
//
//



package com.my51c.see51.media;

import android.util.Log;

import com.my51c.see51.listener.OnAVQSetListener;
import com.my51c.see51.listener.OnAlarmEnableListener;
import com.my51c.see51.listener.OnIntercomListener;
import com.my51c.see51.protocal.SSPPackage;
import com.my51c.see51.protocal.SSPPackage.PTZ_CMD;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.PipedOutputStream;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.net.SocketTimeoutException;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ArrayBlockingQueue;


public class LocalMediaStreamer extends MediaStreamer implements Runnable {

	final int DEFAULT_PORT = 5552;
	private Selector selector;
	private Thread runThread;
	private boolean bStop=true;
	final static int ConnectTimeout = 15; //���ӳ�ʱʱ��
	private Timer hbTimer;
	private SocketChannel peer;
	
	private ArrayBlockingQueue<VideoFrame> videoFrames;
	private ArrayBlockingQueue<byte[]> audioFrames;
	
	public static final int MAX_CACHED_FRAMES = 32;  // ��󻺳����֡����
	private long lastTimeStamp = 0;
	
	private OnIntercomListener mOnIntercomListener;
	private OnAlarmEnableListener mOnAlarmEnableListener;
	private OnAVQSetListener mOnAVQSetListener;
	
	@Override
	public void setOnIntercomListener(OnIntercomListener l) {
		this.mOnIntercomListener = l;	
	}
	@Override
	public void setOnAlarmEnableListener(OnAlarmEnableListener l) {
		this.mOnAlarmEnableListener = l;	
	}
	@Override
	public void setOnAVQSetListener(OnAVQSetListener l) {
		this.mOnAVQSetListener = l;	
	}
	
	public LocalMediaStreamer(String url, Map<String, String>param) {
		super(url, param);
		this.port = DEFAULT_PORT;
		hbTimer = new Timer();
		
		videoFrames = new ArrayBlockingQueue<VideoFrame> (MAX_CACHED_FRAMES);			
		audioFrames = new ArrayBlockingQueue<byte[]>(MAX_CACHED_FRAMES);		
	}

	@Override
	public boolean open() {
		
		if(this.paramMap.get("UserName") == null || 
		   this.paramMap.get("Id") == null ||
		   this.paramMap.get("Password") == null ) {
		   return false;
		}
		
		lastTimeStamp = 0;
		m_bAlarmEnable = 1;
		m_nDefinitionCurrent = 3;
		videoFrames.clear();
		audioFrames.clear();
		runThread = new Thread(this);
		runThread.start();
		return true;
	}

	@Override
	public void close() {
		//Log.d("LocalMediaStreamer", " close start");
		if(bStop)
			return;
		
		bStop = true;
		dataListener = null;
		
		
		if (hbTimer!=null) {
			hbTimer.cancel();
		}
		
		if (runThread!=null) {
			runThread.interrupt();
			try {
				runThread.join();
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		disconnect();
		stopIntercomm();
		
		//Log.d("LocalMediaStreamer", " close end");
	}
	
	private void disconnect() {
		//dataListener = null;
		try {
			peer.socket().close();
			peer.close();
			selector.wakeup();
			selector.close();			
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}

	private boolean connect()
	{
		try
		{
			selector = Selector.open();
			SocketAddress address = new InetSocketAddress(this.hostAddr,this.port);
			
			
			peer=SocketChannel.open();
			peer.socket().setSoTimeout(5000);
			peer.configureBlocking(true);
			//peer.connect(address);
			peer.socket().connect(address, 10000);
//			peer.configureBlocking(false);
//			if(!peer.connect(address)) //������ʽ��û���������ӳɹ�����ȴ����ӳɹ�
//			{
//				long startTime = System.currentTimeMillis();	
//				while(!peer.finishConnect()) //�ȴ��������
//				{
//					if(System.currentTimeMillis() - startTime >= ConnectTimeout*1000 ) //���ӳ�ʱ
//					{
//						throw new IOException();
//					}
//					try {
//						Thread.sleep(100);
//					} catch (InterruptedException e) {
//							e.printStackTrace();
//					}
//				}
//			}
//			
//			peer.socket().setTcpNoDelay(true);
//			peer.register(selector, SelectionKey.OP_READ);
			//Log.d("LocalMediaStreamer", "Connect to:" + address.toString() + " sucess!");		
			//Log.d("LocalMediaStreamer", "send login command");
			// ���͵�¼����
			SSPPackage loginPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_LOGIN_REQ);
			loginPack.addParam("UserName", this.paramMap.get("UserName"));
			loginPack.addParam("Password", this.paramMap.get("Password"));
			loginPack.addParam("DeviceSerial", this.paramMap.get("Id"));
			//Log.d("Player", "id = " + this.paramMap.get("Id"));
			sendPacakge(loginPack);
			
			//Log.d("LocalMediaStreamer", "send video request command");			
			// ������Ƶ��������
			SSPPackage videoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_DATA_REQ);
			videoReqPack.addParam("Command", "Start");
			videoReqPack.addParam("Type", "Video");
			videoReqPack.addParam("StreamID", "1");	// 1:С����      0:������	
			sendPacakge(videoReqPack);
			
			/*
			SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_S_D_TRANSMIT_REQ);
			pack.addParam("Command_Param", PTZ_CMD.CMD_ALERT_GET_STATE);
			sendPacakge(pack);	
			*/
			getAlarmEnable();
		}
		
		catch(SocketTimeoutException e)
		{
			Log.d("LocalMediaStream", "socket time out!");
			if(this.dataListener != null && !bStop)
			{	
				dataListener.OnMediaDataException(MediaEvent.CONN_TIME_OUT);
			}
			return false;
		}
		catch(java.lang.AssertionError e )
		{
			return false;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Log.d("LocalMediaStreamer", "Connect  Failed!");	
			
			/*
			if(this.dataListener != null && !bStop)
			{	
				dataListener.OnMediaDataException(MediaEvent.CONN_TIME_OUT);
			}
			*/
			return false;
		}		
		return true;
	}
	
	private SSPPackage reSspPackage = new SSPPackage(0);
	private ByteBuffer resBuffer = reSspPackage.getByteBuffer();
	private void sendResponse(){
		synchronized (resBuffer) {
			try {
				peer.write(resBuffer);			
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
				resBuffer.rewind();
			}			
		}
	}
	
	private int receive(SocketChannel sc, ByteBuffer buf, int length) throws IOException
	{
		int totalLen = 0;
		int timeout = 0;
		while(length > totalLen)
		{
			int readLen = 0;
			try {
				//readLen = peer.read(buf);
				readLen = sc.read(buf);
			} catch (Exception e) {
				// TODO: handle exception
				throw new IOException();
			}
			
			if(readLen > 0)
			{
				//sendResponse();
				totalLen += readLen;
				timeout = 0;
			}
			else if(readLen < 0)
			{
				if (timeout++>1) {
					throw new IOException();
				}				
			}
			else {
				//Log.d("LocalMediaStreamer","read 0 byte");
				if (timeout++>1) {
					throw new IOException();
				}
			}                            		
		}
		return totalLen;
	}
	
	
	byte mediaBuf[] ;
	String mediaType;

	PipedOutputStream pipedOutputStream;
	
	@Override
	public void createAudioStream() {
		pipedOutputStream = new PipedOutputStream();
	}
	
	@Override
	public PipedOutputStream getAudioStream() {
		return pipedOutputStream;
	}
	
	@Override
	public void closeAudioStream() {
		if (pipedOutputStream!=null) {
			try {
				pipedOutputStream.close();
				pipedOutputStream = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
	}
	
	private void onReceivedPackage(SSPPackage pack)
	{
		//int ret= pack.getIntegerParam("Ret");

//		if (pack.getnCmd()==SSPPackage.COMMAND_D_S_TRANSMIT_ACK &&
//				pack.getParam()==SSPPackage.COMMAND_S_C_TRANSMIT_INTERCOM_ACK
//				&& mOnIntercomListener!=null) {
//			if (ret==1) {
//				mOnIntercomListener.onIntercom();
//			}else {
//				mOnIntercomListener.onUnsupported();
//			}
//			return;
//		}
		mediaBuf = pack.getParam("Data");
		mediaType = pack.getString("Type");
		
		if(mediaType != null && mediaBuf != null)
		{
			////Log.d("LocalMediaStreamer","mediaType = " + mediaType);
			if(mediaType.equals("Video")) // д����Ƶ������
			{
				////Log.d("LocalMediaStreamer", "���յ���Ƶ����" + mediaBuf.length);
				//videoBuffer.put(mediaBuf);
				putOneVideoFrame(new VideoFrame(mediaBuf));  
			}
			else if(mediaType.equals("Audio")&&pipedOutputStream!=null)
			{
			//	//Log.d("LocalMediaStreamer", "���յ���Ƶ����"+ mediaBuf.length);	
				//audioBuffer.put(mediaBuf);
				
				VideoFrame vf =new  VideoFrame(mediaBuf);
				
				try {
					pipedOutputStream.write(vf.getFrameData(),0,vf.getDataSize());
					pipedOutputStream.flush();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (NullPointerException e) {
					// TODO: handle exception
					e.printStackTrace();
					//Log.e("LocalMediaStreamer", "Audio OutputStream is closed");
				}
				//putOneAudioFrame(mediaBuf);
			} 
			return;
		}
		
		String ret = pack.getString("RetAudio");
		if (ret!=null && mOnIntercomListener!=null) {
			if (ret.equals("1")) {
				mOnIntercomListener.onIntercom();
				intercommData = new ArrayBlockingQueue<ByteBuffer>(10);
				intercommThread = new Thread(intercomRun);
				intercommThread.start();
			}else {
				mOnIntercomListener.onUnsupported(ret);
			}
		}
		////Log.d("LoocalMediaStreameronReceivedPackage", "RetAudio : " + ret);
		ret = pack.getString("RetAlert");
		if (ret!=null) {
			if (ret.equals("On")) 
			{
				m_bAlarmEnable = 1;
			}
			else 
			{
				m_bAlarmEnable = 0;
			}
			
			if(mOnAlarmEnableListener != null)
			mOnAlarmEnableListener.onAction();
		}
		
		//Log.d("LocalMediaStreameronReceivedPackage", "RetAlert : " + m_bAlarmEnable);
	}
	
	
	private ByteArrayOutputStream tmpOutStream = new ByteArrayOutputStream();
	private ByteBuffer headBuf = ByteBuffer.allocate(14);
	private ByteBuffer contentBuf;
	
	private SSPPackage receivePackage(SocketChannel sc) throws IOException
	{
		SSPPackage receivedPack =  null;		
		headBuf.clear();// ����ϴν��յ�������
    	//ByteArrayOutputStream tmpOutStream = new ByteArrayOutputStream(); // ��ʱ�������ݰ�
		tmpOutStream.reset();
		int readLen = 0;
    	try {
			readLen = receive(sc,headBuf, 14);
		} catch (Exception e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
			throw new IOException();
		}
    	
    	if(readLen == 14)
    	{
    		byte headArray[] = headBuf.array();
    		tmpOutStream.write(headArray);  // ������Stream��
    		
    		int contentLen = SSPPackage.getContentLength(headArray); // ��ȡ���ݵĳ���
    		contentBuf = ByteBuffer.allocate(contentLen);
    		contentBuf.clear();
    		
    		int len =0;  		
			len=receive(sc,contentBuf, contentLen);
    		if (len != contentLen) {
    			return receivedPack;
				//throw new IOException();
			}
    		contentBuf.flip();
    		tmpOutStream.write(contentBuf.array()); // �Ѱ��屣��������	 	
    		try {
				receivedPack = new SSPPackage(tmpOutStream.toByteArray());	  // ��������ת���ɰ�����
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else {
    		//Log.d("LocalMediaStreamer", "readLen != 14");
			throw new IOException();
		}
    	//sendResponse();
    	return receivedPack;
	}
	
	
	// ý�������߳�
	@Override
	public void run() {
		//Log.d("LocalMediaStreamer.run", "media thread started");
		bStop = false;
		while(!bStop)
		{
			if (connect()) {
				//Log.d("LocalMediaStreamer.run", "begin receive data!");
				try {
					hbTimer.schedule(new HearBeatTask(), 1000, 10000); // ����������
					long nStartTime = System.currentTimeMillis();
					while (!bStop) {
						
						SSPPackage pack = receivePackage(peer);
						if (pack != null) {
							onReceivedPackage(pack);
						}else {
							//Log.d("LocalMediaStreamer.run", "recv null");
						}
						
						if((System.currentTimeMillis() - nStartTime) > 10000)
						{	
							nStartTime = System.currentTimeMillis();
							getAlarmEnable();
						}
					}
				} catch (Exception e) {
					Log.d("LocalMediaStreamer.run", "Receive Exception!");
					//bStop = true;
					e.printStackTrace();
					
					if (this.dataListener != null) {
						dataListener.OnMediaDataException(MediaEvent.CONN_DISCONNECT);
					}
					
					//disconnect();
				}
			}
			else
			{
				Log.d("LocalMediaStreamer.run", "connect failed!");
				//bStop = true;
				/*
				if (this.dataListener != null) {
					dataListener.OnMediaDataException(MediaEvent.CONN_DISCONNECT);
				}
				*/
				disconnect();
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				continue;
			}
		}
			
	}

	
	public void putOneVideoFrame(VideoFrame frame) {
		
		long dura = frame.getTimeStamp() - lastTimeStamp;
		
		long tmp = 3000/frame.getFrameRate();
		
		if (tmp<1000) {
			tmp = 1000;
		}
		
		if (dura>tmp && !frame.isKeyFrame()) {
			////Log.d("LocalMediaStreamer.run", "putOneVideoFrame  tmp = " +tmp);		
			return;
		}
		

		try {
			videoFrames.add(frame);
			lastTimeStamp = frame.getTimeStamp();
			
		}catch (IllegalStateException  e) {
			videoFrames.clear();      // ���������˾�ȫ�����
			// TODO: handle exception
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

//	public VideoFrame getVideoFrame(int timeout) throws InterruptedException {				
//		VideoFrame frame = null;
//		frame = videoFrames.take();		
//		return frame;
//	}	
	
	@Override
	public VideoFrame getOneVideoFrame(int timeout){
//		byte buf[] = this.videoBuffer.get(timeout);
//		if(buf != null)
//		{
//			return new VideoFrame(buf);
//		}
//		return null;
				
		VideoFrame frame = null;	
		int  tempSecond = 0;
		try {
			while (videoFrames.size()<1) {
				Thread.sleep(100);	
				tempSecond += 100;
				if	(tempSecond > timeout && timeout !=  -1)
				{
					return frame;
				}
			}
			frame = videoFrames.take();
		
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
		
		return frame;
	}
	
	public void putOneAudioFrame(byte[] bytes) {
		audioFrames.add(bytes);
	}
	
	@Override
	public byte[] getOneAudioPack(int timeout) {
		//return this.audioBuffer.get(timeout);
		return audioFrames.poll();
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
		
	private void sendPacakge(SSPPackage pack)
	{
		final ByteBuffer bf = pack.getByteBuffer();
		String strbf = getString(bf);
		//Log.d("LocalMediaStreamer.run", "SEND PACK"+ strbf);
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					peer.write(bf);					
					bf.rewind();
				} catch (Exception e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}).start();
	}
	private class HearBeatTask extends TimerTask
	{
		SSPPackage hearbeat = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_NULL);
		ByteBuffer bf = hearbeat.getByteBuffer();
		public void run() {
			//lock.lock();
			try {
				//Log.d("LocalMediaStreamer.run", "HeartBeatTask!");
				bf.rewind();
				peer.write(bf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				Log.d("LocalMediaStreamer.run", "HeartBeatTask Exception!");
				e.printStackTrace();
				if (dataListener != null) {
					dataListener.OnMediaDataException(MediaEvent.CONN_DISCONNECT);
				}
			}finally{
		//		lock.unlock();
			}
		}		
	}
	@Override
	public void setAlarmEnable() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.CMD_ALERT_SET);
		if(m_bAlarmEnable==0)
		{
			pack.addParam("AlertVal","Off");
		}
		else
		{
			pack.addParam("AlertVal","On");
		}
		pack.addParam("Wait", "0");
		sendPacakge(pack);	
	}
	
	@Override
	public void getAlarmEnable() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.CMD_ALERT_GET_STATE);
		pack.addParam("Wait", "1");
		sendPacakge(pack);
		////Log.d("LocalMediaStreamer", "send get alarm status!");
	};
	
	@Override
	public void setDefinition() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.CMD_AVQ_SET);
		if(m_nDefinitionCurrent==1)
		{
			pack.addParam("AVQVal","Max");
		}
		else if(m_nDefinitionCurrent==3)
		{
			pack.addParam("AVQVal","Mid");
		}
		else if(m_nDefinitionCurrent==5)
		{
			pack.addParam("AVQVal","Min");
		}
		//pack.addParam("Wait", "0");
		sendPacakge(pack);	
		
		;//implement later
	}
	@Override
	public void turnLeft() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_TO_LEFT);
		sendPacakge(pack);
	}
	@Override
	public void turnRight() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_TO_RIGHT);
		sendPacakge(pack);		
	}

	@Override
	public void rollUp() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_TO_UP);
		sendPacakge(pack);		
	}

	@Override
	public void rollDown() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_TO_DOWN);
		sendPacakge(pack);		
	}

	@Override
	public void zoomIn() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_TO_ENLARGE);
		sendPacakge(pack);		
	}

	@Override
	public void zoomOut() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_S_D_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_TO_NARROW);
		sendPacakge(pack);		
	}
	
	@Override
	public void focusIn() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_FOCUSIN);
		sendPacakge(pack);		
	}

	@Override
	public void focusOut() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_S_D_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_FOCUSOUT);
		sendPacakge(pack);		
	}

	@Override
	public void restPtz() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_RESET);
		sendPacakge(pack);		
	}

	@Override
	public void scanV() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_AUTO_V);
		sendPacakge(pack);		
	}

	@Override
	public void scanH() {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_AUTO_H);
		sendPacakge(pack);		
	}

	@Override
	public void scanStop() {	
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_TURN_SCAN_STOP);
		sendPacakge(pack);	
	}
	
	@Override
	public void zoomInPosition(int x, int y)
	{
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_S_D_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.ORIGIN_SEND);
		pack.addParam("X_Axis", Integer.toString(x));
		pack.addParam("Y_Axis", Integer.toString(y));
		sendPacakge(pack);	
	}

	@Override
	public void flipV() {
		// TODO Auto-generated method stub
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_FLIP_V);
		sendPacakge(pack);
	}

	@Override
	public void flipH() {
		// TODO Auto-generated method stub
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", PTZ_CMD.PTZ_FLIP_H);
		sendPacakge(pack);		
	}

	@Override
	public void setAudio(boolean isEnable) {
		// TODO Auto-generated method stub
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_DATA_REQ);
		pack.addParam("Command", isEnable?"Start":"Stop");
		pack.addParam("Type", "Audio");	
		sendPacakge(pack);
	}
	@Override
	public void setVideo(boolean isEnable) {
		// TODO Auto-generated method stub
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_DATA_REQ);
		pack.addParam("Command", isEnable?"Start":"Stop");
		pack.addParam("Type", "Video");	
		sendPacakge(pack);
	}

	@Override
	public void setInterCom(boolean isEnable) {
		// TODO Auto-generated method stub
		if (isEnable) {
			SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
			pack.addParam("Command_Param", Integer.toString(SSPPackage.COMMAND_C_S_TRANSMIT_INTERCOM_REQ));
			pack.addParam("Wait", "1");
			sendPacakge(pack);			
		}		
	}
	
	private boolean bIntercomm;
	private ArrayBlockingQueue<ByteBuffer> intercommData;
	Runnable intercomRun = new Runnable() {
		ByteBuffer sendbuf;
		@Override
		public void run() {
			// TODO Auto-generated method stub
			bIntercomm = true;
			while (bIntercomm) {
				try {
					sendbuf = intercommData.take();
				} catch (InterruptedException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
					break;
				}
				if (sendbuf!=null) {
					try {
						peer.write(sendbuf);												
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	Thread intercommThread;
	
	@Override
	public void stopIntercomm() {
		bIntercomm = false;
	}
	
	@Override
	public void sendAudioData(byte[] buf) {
		SSPPackage pack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		pack.addParam("Command_Param", Integer.toString(SSPPackage.COMMAND_S_C_TRANSMIT_INTERCOM_DATA));
		pack.addParam("audiodata", buf);
		ByteBuffer tmpBuffer = pack.getByteBuffer();
		try {
			intercommData.add(tmpBuffer);
		} catch (IllegalStateException  e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
//		//Log.d("LocalMediaStreamer.sendAudioData", "head: "+new String(tmpBuffer.array(), 0, 56));
//		//Log.d("LocalMediaStreamer.sendAudioData", "aaclength: "+(tmpBuffer.array().length-56));
		//sendPacakge(pack);
	}
	
	public static int getAndroidSDKVersion() {
        int version = 0;
        try {
            //version = Integer.valueOf(android.os.Build.VERSION.SDK);
        	version = android.os.Build.VERSION.SDK_INT;
        } catch (NumberFormatException e) {
            //Log.e("MyMediaStreamer", e.toString());
        }
        return version;
    }
}
