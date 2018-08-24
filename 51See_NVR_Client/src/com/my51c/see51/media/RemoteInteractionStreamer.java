package com.my51c.see51.media;

import android.annotation.SuppressLint;
import android.util.Log;

import com.my51c.see51.data.Device3GShortParam;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.listener.OnGet3GInfoListener;
import com.my51c.see51.listener.OnGetBLPInfoListener;
import com.my51c.see51.listener.OnGetCurtainInfoListener;
import com.my51c.see51.listener.OnGetDevInfoListener;
import com.my51c.see51.listener.OnGetFileDataOverListener;
import com.my51c.see51.listener.OnGetNVRDeviceListListener;
import com.my51c.see51.listener.OnGetRFInfoListener;
import com.my51c.see51.listener.OnGetSDFileDataListener;
import com.my51c.see51.listener.OnGetSDFileDirListener;
import com.my51c.see51.listener.OnGetSDFileListListener;
import com.my51c.see51.listener.OnSet3GInfoListener;
import com.my51c.see51.listener.OnSetCurtainInfoListener;
import com.my51c.see51.listener.OnSetDevInfoListener;
import com.my51c.see51.listener.OnSetRFInfoListener;
import com.my51c.see51.protocal.RFPackage;
import com.my51c.see51.protocal.SSPPackage;
import com.my51c.see51.protocal.SSPPackage.PTZ_CMD;
import com.my51c.see51.ui.SDRecordNVRActivity;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.SocketAddress;
import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.channels.NotYetConnectedException;
import java.nio.channels.Selector;
import java.nio.channels.SocketChannel;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.text.SimpleDateFormat;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class RemoteInteractionStreamer extends MediaStreamer implements Runnable {
	private final String TAG = "RemoteInteractionStreamer";
	final int DEFAULT_PORT = 5552;
	private Selector selector;
	private Thread runThread;
	private boolean bStop;
	private boolean bConnected;
	public boolean isbConnected() {
		return bConnected;
	}

	public void setbConnected(boolean bConnected) {
		this.bConnected = bConnected;
	}

	final static int ConnectTimeout = 15; 
	private Timer hbTimer;
	private SocketChannel peer;
	private OnGetDevInfoListener mOnGetDevInfoListener;
	private OnSetDevInfoListener mOnSetDevInfoListener;
	private OnGetRFInfoListener mOnGetRFInfoListener;
	private OnGetBLPInfoListener mOnGetBLPInfoListener;
	private OnGetCurtainInfoListener mOnGetCurtainInfoListener;
	private OnSetCurtainInfoListener mOnSetCurtainInfoListener;
	
	private OnSetRFInfoListener mOnSetRFInfoListener;
	private OnGet3GInfoListener mOnGet3GInfoListener;
	private OnSet3GInfoListener mOnSet3GInfoListener;
	private OnGetSDFileListListener mOnGetSDFileListListener;
	private OnGetSDFileDataListener mOnGetSDFileDataListener;
	private OnGetFileDataOverListener mOnGetFileDataOverListener;
	private OnGetSDFileDirListener mOnGetSDFileDirListener;
	private OnGetNVRDeviceListListener mOnGetNVRDeviceListListener;
	private String devID;
	
	private final int HIS_GET_DATEDIR  = 1;			//��ȡSD��Ŀ¼
	private final int HIS_GET_FILELIST = 2;			//��ȡ�ļ��б�
	private final int HIS_GET_FILEDATA = 3;			//��ȡ�ļ�����
	private final int HIS_GET_FILEOVER = 4;			//��ȡ�ļ����
	private final int HIS_REQ_FINISH   = 5;			//����������ֹ
	private final int HIS_GET_DATE_HOUR_FILELIST   = 7;			//��ȡ�ļ���������
	private final int HIS_GET_REC_ID_LIST   = 8;	//��ȡNVR�豸�б�
	

	/*get device info  listener*/
	public void setOnGetDevInfoListener(OnGetDevInfoListener l) {
		this.mOnGetDevInfoListener = l;	
	}
	
	/*set device info  listener*/
	public void setOnSetDevInfoListener(OnSetDevInfoListener l) {
		this.mOnSetDevInfoListener = l;	
	}
	
	/*get RF info  listener*/
	public void setOnGetRFInfoListener(OnGetRFInfoListener l) {
		this.mOnGetRFInfoListener = l;	
	}
	
	public void setOnGetBLPInfoListener(OnGetBLPInfoListener l){
		this.mOnGetBLPInfoListener = l;
	}
	
	/*set RF info  listener*/
	public void setOnSetRFInfoListener(OnSetRFInfoListener l) {
		this.mOnSetRFInfoListener = l;	
	}
	
	/*get 3G info  listener*/
	public void setOnGet3GInfoListener(OnGet3GInfoListener l) {
		this.mOnGet3GInfoListener = l;	
	}
	
	/*set 3G info  listener*/
	public void setOnSet3GInfoListener(OnSet3GInfoListener l) {
		this.mOnSet3GInfoListener = l;	
	}
	
	/*set sd card file list  listener*/
	public void setOnGetSDFileListListener(OnGetSDFileListListener l) {
		this.mOnGetSDFileListListener = l;	
	}
	
	
	/*set sd card file data  listener*/
	public void setOnGetSDFileDataListener(OnGetSDFileDataListener l) {
		this.mOnGetSDFileDataListener = l;	
	}
	
	/*get sd card file over  listener*/
	public void setOnGetFileDataOverListener(OnGetFileDataOverListener l) {
		this.mOnGetFileDataOverListener = l;	
	}
	
	/*get sdcard file directory  listener*/
	public void setOnGetSDFileDirListener(OnGetSDFileDirListener l)
	{
		this.mOnGetSDFileDirListener = l;
	}
	/*get curtain info  listener*/
	public void setOnGetCurtainInfoListener(OnGetCurtainInfoListener l)
	{
		this.mOnGetCurtainInfoListener = l;
	}
	/*set curtain info  listener*/
	public void setOnSetCurtainInfoListener(OnSetCurtainInfoListener l)
	{
		this.mOnSetCurtainInfoListener = l;
	}
	
	public void setOnGetNVRDeviceListListener(OnGetNVRDeviceListListener l){
		this.mOnGetNVRDeviceListListener = l;
	}
	
	
	public RemoteInteractionStreamer(String url, Map<String, String> param) {
		super(url, param);
		// TODO Auto-generated constructor stub
		
		this.port = DEFAULT_PORT;
		hbTimer = new Timer();
	}
	
	public String getDevId()
	{
		return devID;
	}
	
	public void setDevId(String l)
	{
		devID = l;
	}

	@Override
	public void run() {
		// TODO Auto-generated method stub
		//Log.d("RemoteInteractionStreamer.run", "media thread started");
		bStop = false;
		connect();
//		if (bConnected) {
			//Log.d("RemoteInteractionStreamer.run", "begin receive data!");
			hbTimer.schedule(new HearBeatTask(), 1000, 20000); 
			while (!bStop) {
				try 
				{
					if(bConnected==false)
					{
						reconnect();
						Thread.sleep(1000);
						//Log.d("RemoteInteractionStreamer.run", "connect fail, reconnect!");
						continue;
					}
					if(peer.isConnected()==false)
					{
						bConnected = false;
						//Log.d("RemoteInteractionStreamer.run", "connect break, reconnect!");
						continue;
					}	
					SSPPackage pack = receivePackage(peer);
					if (pack != null) {
						onReceivedPackage(pack);
					}else {
						//Log.d("RemoteInteractionStreamer.run", "recv null");
					}
				} catch (Exception e) {
					e.printStackTrace();
					
					bConnected=false;
					//if (this.dataListener != null) {
					//	dataListener
					//			.OnMediaDataException(MediaEvent.CONN_DISCONNECT);
					//}
				}
			}
			//Log.d("RemoteInteractionStreamer.run", "stoped");
			
//		}
	}

	@Override
	public boolean open() {
		// TODO Auto-generated method stub
		//Log.d("RemoteInteractionStreamer", "open");
		if(this.paramMap.get("UserName") == null || 
			this.paramMap.get("Id") == null ||
			this.paramMap.get("Password") == null
				)
		{
			//Log.d("RemoteInteractionStreamer", "1111");
			return false;
		}
		
		runThread = new Thread(this);
		runThread.start();
		return true;
	}

	@Override
	public void close() {
		// TODO Auto-generated method stub
		//Log.d("RemoteInteractionStreamer", " close start");
		bStop = true;
		disconnect();
		if (hbTimer!=null && bConnected == true) {
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
		//Log.d("RemoteInteractionStreamer", " close end");
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
			//Log.d(TAG, "host Addr :" + this.hostAddr);
			SocketAddress address = new InetSocketAddress(this.hostAddr,this.port);
			
			Log.d("RemoteInteractionStreamer", "Connect to:" + address.toString());
			peer=SocketChannel.open();
			
			peer.socket().setSoTimeout(0);
			peer.configureBlocking(true);
			peer.connect(address);

			//Log.d("RemoteInteractionStreamer", "Connect to:" + address.toString() + " sucess!");		
			//Log.d("RemoteInteractionStreamer", "send login command");
	
			SSPPackage loginPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_LOGIN_REQ);
			loginPack.addParam("UserName", this.paramMap.get("UserName"));
			loginPack.addParam("Password", this.paramMap.get("Password"));
			loginPack.addParam("DeviceSerial", this.paramMap.get("Id"));
			//Log.d("RemoteInteractionStreamer", "id = " + this.paramMap.get("Id"));
			sendPacakge(loginPack);

			getDevInfo();
			getRFDeviceInfo();
			getBLPDeviceInfo();
			getCurtainInfo();
			getNVRDeviceList();
			bConnected =  true;
		}
		catch(Exception e)
		{
			e.printStackTrace();
			//Log.d("RemoteInteractionStreamer", "Connect  Failed!");	
			if(this.dataListener != null && !bStop)
			{	
				dataListener.OnMediaDataException(MediaEvent.CONN_TIME_OUT);
			}
			bConnected =  false;
			return false;
		}		
		return true;
	}
	private boolean reconnect()//add by marshal
	{
		disconnect();
		connect();
		return true;
	}	
	
	private void sendPacakge(SSPPackage pack)
	{
		final ByteBuffer bf = pack.getByteBuffer();
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if(peer!=null && peer.isOpen())
					{	
						if(peer.write(bf)<=0)
						{
							peer.close();
							bConnected =  false;
							//Log.d("RemoteInteractionStreamer", "sendPacakge fail!");	
						}
					}
					else
					{
						try {
							peer.close();
							bConnected =  false;
						} catch (Exception e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}
						//Log.d("RemoteInteractionStreamer", "socket fail need reconnect!");	

					}
						
					bf.rewind();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					
					e.printStackTrace();
				} catch (NotYetConnectedException e) {
					// TODO: handle exception
				}				
			}
		}).start();
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
           
            charBuffer = decoder.decode(buffer.asReadOnlyBuffer());  
            return charBuffer.toString();  
        }  
        catch (Exception ex)  
        {  
            ex.printStackTrace();  
            return "";  
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
				readLen = sc.read(buf);
			} catch (Exception e) {
				// TODO: handle exception
				throw new IOException();
			}
			
			if(readLen > 0)
			{
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
				//Log.d("RemoteInteractionStreamer","read 0 byte");
				if (timeout++>1) {
					throw new IOException();
				}
			}                            		
		}
		return totalLen;
	}
	
	String ackType;
	String strRet;
	byte mediaBuf[];
	
	private void onReceivedPackage(SSPPackage pack)
	{	
		int recvcmd = pack.getnCmd();

		switch(recvcmd)
		{	
			case SSPPackage.COMMAND_S_C_TRANSMIT_LOGIN_ACK:
				{
					//Log.d(TAG, "login ack!!!!!!");
				}
				break;
			case SSPPackage.COMMAND_S_C_TRANSMIT_ACK:
				{
					ackType = pack.getString("Command_Param");
					Log.i(TAG, "ackType: "+ackType);
					if(ackType != null)
					{		
						int nAckCmd = 0;
						nAckCmd = Integer.parseInt(ackType);
						
						if(nAckCmd == PTZ_CMD.GET_DEV_INFO_ACK.value())
						{
							DealDevInfoAck(pack);
						}
						else if(nAckCmd == PTZ_CMD.GET_RFID_INFO_ACK.value())
						{
							DealRFDeviceAck(pack);
						}
						else if(nAckCmd == PTZ_CMD.GET_BLP_RESULT_ACK.value())
						{
							DealBLPPackage(pack);
						}
						else if(nAckCmd == PTZ_CMD.SET_RFID_INFO_ACK.value())
						{
							if(mOnSetRFInfoListener != null)
							   mOnSetRFInfoListener.onSetRFInfoSuccess();
						}
						else if(nAckCmd == PTZ_CMD.SET_DEV_INFO_ACK.value())
						{
							if(mOnSetDevInfoListener != null)
								mOnSetDevInfoListener.onSetDevInfoSuccess();
						}
						else if(nAckCmd == PTZ_CMD.SET_DEV_3GINFO_ACK.value())
						{
							if(mOnSet3GInfoListener != null)
								mOnSet3GInfoListener.onSet3GInfoSuccess();
						}
						else if(nAckCmd == PTZ_CMD.GET_DEV_3GINFO_ACK.value())
						{
							Deal3GDeviceAck(pack);
						}
						else if(nAckCmd == PTZ_CMD.HIS_VIDEO_ACK.value())
						{
							DealHisVideoAck(pack);
						}
						else if(nAckCmd == PTZ_CMD.SET_CURTAIN_STATUS_ACK.value())
						{
							if(mOnSetCurtainInfoListener != null)
								mOnSetCurtainInfoListener.OnSetCurtainInfoSuccess();
						}
					}
				}
				break;
			default:
				break;
		}
		
	}
	
	private ByteBuffer headBuf = ByteBuffer.allocate(14);
	private ByteBuffer contentBuf;
	private ByteArrayOutputStream tmpOutStream = new ByteArrayOutputStream();
	
	private SSPPackage receivePackage(SocketChannel sc) throws IOException
	{
//		Log.i(TAG, "--receivePackage");
		SSPPackage receivedPack =  null;		
		headBuf.clear();
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
    		tmpOutStream.write(headArray);
    		
    		int contentLen = SSPPackage.getContentLength(headArray); 
    		contentBuf = ByteBuffer.allocate(contentLen);
    		contentBuf.clear();
    		
    		
    		int len =0;  		
			len=receive(sc,contentBuf, contentLen);
    		if (len != contentLen) {
    			return receivedPack;
				//throw new IOException();
			}
    		
    		contentBuf.flip();
    		tmpOutStream.write(contentBuf.array());
    		
    		try {
				receivedPack = new SSPPackage(tmpOutStream.toByteArray());	  
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
    	}else {
    		//Log.d("RemoteInteractionStreamer", "readLen != 14");
			throw new IOException();
		}
    	//sendResponse();
    	return receivedPack;
	}
	

	private void DealDevInfoAck(SSPPackage pack)
	{
		
		strRet = pack.getString("Ret");
		if(strRet.equals("0"))
		{
			if(mOnGetDevInfoListener != null)
				mOnGetDevInfoListener.onGetDevInfoFailed();
			return;
		}
		
		mediaBuf = pack.getParam("DevInfo");
		if(mediaBuf != null)
		{
			if(mOnGetDevInfoListener != null)
				mOnGetDevInfoListener.onGetDevInfoSuccess(mediaBuf);
		}
	}
	
	private void DealRFDeviceAck(SSPPackage pack)
	{
		
		strRet = pack.getString("Ret");
		if(strRet.equals("0"))
		{
			if(mOnGetRFInfoListener != null)
			   mOnGetRFInfoListener.onGetRFInfoFailed();
			return;
		}
		
		mediaBuf = pack.getParam("RfidInfo");
		if(mediaBuf != null)
		{
			if(mOnGetRFInfoListener != null)
			   mOnGetRFInfoListener.onGetRFInfoSuccess(mediaBuf);
		}
	}
	
	private void DealBLPPackage(SSPPackage pack){
		strRet = pack.getString("Ret");
		if(strRet.equals("0"))
		{
			if(mOnGetBLPInfoListener != null){
				mOnGetBLPInfoListener.onGetBLPInfoFailed();
			return;
			}
		}
		mediaBuf = pack.getParam("BLPRes");
		if(mediaBuf != null)
		{
			if(mOnGetBLPInfoListener != null)
				mOnGetBLPInfoListener.onGetBLPInfoSuccess(mediaBuf);
		}
	}
	
	private void DealCurtainPackage(SSPPackage pack){
		strRet = pack.getString("Ret");
		if(strRet.equals("0"))
		{
			if(mOnGetCurtainInfoListener != null){
				mOnGetCurtainInfoListener.OnGetCurtainInfoFailed();
			return;
			}
		}
		mediaBuf = pack.getParam("Curtain");
		if(mediaBuf != null)
		{
			if(mOnGetCurtainInfoListener != null)
				mOnGetCurtainInfoListener.OnGetCurtainInfoSuccess(mediaBuf);
		}
	}
	
	private void Deal3GDeviceAck(SSPPackage pack)
	{
		
		strRet = pack.getString("Ret");
		if(strRet.equals("0"))
		{
			if(mOnGet3GInfoListener != null)
				mOnGet3GInfoListener.onGet3GInfoFailed();
			return;
		}
		
		mediaBuf = pack.getParam("DevInfo");
		if(mediaBuf != null)
		{
			if(mOnGet3GInfoListener != null)
			   mOnGet3GInfoListener.onGet3GInfoSuccess(mediaBuf);
		}
	}
	
	static int iPackCount = 0;
	private void DealHisVideoAck(SSPPackage pack)
	{
		
		strRet = pack.getString("Ret");
		if(strRet.equals("0"))
		{
			Log.i(TAG, "--strRet==0");
			return;
		}
		
		String hisCmdType = pack.getString("Type");
		if(hisCmdType != null)
		{		
			int nHisCmdType = 0;
			nHisCmdType = Integer.parseInt(hisCmdType);
			Log.i(TAG, "--nHisCmdType:"+nHisCmdType);
			switch(nHisCmdType)
			{
				case HIS_GET_DATEDIR:
				{
					if(SDRecordNVRActivity.isNVR){
						Log.i(TAG, "--DealNVRDeviceList");
						DealNVRDeviceList(pack);
					}else{
						Log.i(TAG, "--DealHisVideoDir");
						DealHisVideoDir(pack);
					}
				}
					break;
				case HIS_GET_FILELIST:
				{
					Log.i(TAG, "--DealHisVideoFileList");
					DealHisVideoFileList(pack);
				}
				break;

				case HIS_GET_FILEDATA:
				{
					DealHisSDFileData(pack);
				}
				break;
				case HIS_GET_FILEOVER:
				{
					DealHisSDOver(pack);
				}
				break;
				case HIS_REQ_FINISH:
				{
					
				}
				break;
				
				default:
					break;
			
			}
		
		}
		
	}
	
	private void DealNVRDeviceList(SSPPackage pack)
	{
		mediaBuf = pack.getParam("RecIDList");
		if(mOnGetNVRDeviceListListener != null)
		{
			mOnGetNVRDeviceListListener.OnGetNVRDeviceListSuccess(mediaBuf);	
		}
	}
	
	private void DealHisVideoDir(SSPPackage pack)
	{
		mediaBuf = pack.getParam("DirList");
		if(mOnGetSDFileDirListener != null)
		{
			mOnGetSDFileDirListener.onGetFileDir(mediaBuf);	
		}
	}
	
	private void DealHisVideoFileList(SSPPackage pack)
	{
		mediaBuf = pack.getParam("FileList");
		if(mOnGetSDFileListListener != null)
		{
			mOnGetSDFileListListener.onGetFileList(mediaBuf);	
		}
	}
	
	private void DealHisSDFileData(SSPPackage pack)
	{
		iPackCount++;
		Log.d("file count", ""+iPackCount);
		mediaBuf = pack.getParam("FileData");
		if(mOnGetSDFileDataListener != null)
		{
			mOnGetSDFileDataListener.onGetFileDataPiece(mediaBuf);	
		}
	}
	
	private void DealHisSDOver(SSPPackage pack)
	{
		Log.d("file count", "over:"+iPackCount);
		iPackCount = 0;
		if(mOnGetFileDataOverListener != null)
		{
			mOnGetFileDataOverListener.onGetFileDataOver();	
		}
	}
	
	public void getDevInfo()
	{
		//Log.d("RemoteInteractionStreamer", "send Device Info Request!");			
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.GET_DEV_INFO_REQ);
		deviceInfoReqPack.addParam("Wait", "1");         
		sendPacakge(deviceInfoReqPack);
	}
	
	public void setDevInfo(DeviceLocalInfo info)
	{
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.SET_DEV_INFO_REQ);
		deviceInfoReqPack.addParam("DevInfo", info.toByteBuffer().array());   
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void setCurtainInfo(String idAndStatus)
	{
//		rm -rf /mnt/sdcard/ipnc/20160324/*
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.SET_CURTAIN_STATUS_REQ);
		deviceInfoReqPack.addParam("Curtain", idAndStatus);
		Log.i(TAG, "Curtain cmd��"+idAndStatus);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void getCurtainInfo(){
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.GET_CURTAIN_STATUS_REQ);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void getBLPDeviceInfo(){
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.GET_BLP_RESULT_REQ);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	
	public void getRFDeviceInfo()
	{
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.GET_RFID_INFO_REQ);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void sendRFDevInfo(RFPackage info,String curid)
	{
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.SET_RFID_INFO_REQ);
		deviceInfoReqPack.addParam("RfidInfo", info.buildbuf()); 
		if(curid!=null){
			deviceInfoReqPack.addParam("curid", curid); 
		}
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void get3GDeviceInfo()
	{
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.GET_DEV_3GINFO_REQ);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void send3GDevInfo(Device3GShortParam info)
	{
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.SET_DEV_3GINFO_REQ);
		deviceInfoReqPack.addParam("DevInfo", info.toByteBuffer().array());   
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void getSDFileListByDate(String date,String nvrDeviceId)
	{	
		if(date == null || date.equals(""))
		{
			SimpleDateFormat sdf=new SimpleDateFormat("yyyyMM");    
			date=sdf.format(new java.util.Date()); 
		}
		Log.i(TAG,"--getSDFileListByDate"+date+"--"+nvrDeviceId);
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Type", HIS_GET_FILELIST);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.HIS_VIDEO_REQ);
		deviceInfoReqPack.addParam("DirName", date);
		deviceInfoReqPack.addParam("CurID", nvrDeviceId);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void getSDFolderFileListByDate(String dateHour,String nvrDeviceId)
	{	
		if(dateHour == null || dateHour.equals(""))
		{
			return;
		}
		Log.i(TAG,"--getSDFolderFileListByDate"+dateHour+"--"+nvrDeviceId);
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Type", HIS_GET_DATE_HOUR_FILELIST);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.HIS_VIDEO_REQ);
		deviceInfoReqPack.addParam("CurID", nvrDeviceId);
		deviceInfoReqPack.addParam("DataHourDirName", dateHour);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void getSDFileDir(String nvrDeviceId)
	{
		Log.i(TAG, "getSDFileDir");
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Type", HIS_GET_DATEDIR);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.HIS_VIDEO_REQ);
		deviceInfoReqPack.addParam("CurID", nvrDeviceId);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	@SuppressLint("NewApi")
	public void getSDFileData(String filename, String nvrid)
	{
		if(filename.isEmpty())
		{
			return;
		}
		
		iPackCount = 0;
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Type", HIS_GET_FILEDATA);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.HIS_VIDEO_REQ);
		deviceInfoReqPack.addParam("FileName", filename);
		deviceInfoReqPack.addParam("CurID", nvrid);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	public void getNVRDeviceList()
	{	
		Log.i(TAG, "getNVRDeviceList");
		SSPPackage deviceInfoReqPack = new SSPPackage(SSPPackage.COMMAND_C_S_TRANSMIT_REQ);
		deviceInfoReqPack.addParam("Type", HIS_GET_REC_ID_LIST);
		deviceInfoReqPack.addParam("Command_Param", PTZ_CMD.HIS_VIDEO_REQ);
		deviceInfoReqPack.addParam("Wait", "1");
		sendPacakge(deviceInfoReqPack);
	}
	
	private class HearBeatTask extends TimerTask
	{
			
		SSPPackage hearbeat = new SSPPackage(SSPPackage.COMMAND_S_C_TRANSMIT_NULL);
		ByteBuffer bf = hearbeat.getByteBuffer();
		public void run() {
			//lock.lock();
			if(bConnected==false)
				return;
			try {
				//Log.d("RemoteInteractionStreamer.run", "HeartBeatTask!");
				bf.rewind();
				peer.write(bf);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}finally{
		//		lock.unlock();
			}
		}		
	}
	
	@Override
	public VideoFrame getOneVideoFrame(int timout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public byte[] getOneAudioPack(int timeout) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void turnLeft() {
		// TODO Auto-generated method stub

	}

	@Override
	public void turnRight() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollUp() {
		// TODO Auto-generated method stub

	}

	@Override
	public void rollDown() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoomIn() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoomOut() {
		// TODO Auto-generated method stub

	}
	
	@Override
	public void focusIn() {
	}

	@Override
	public void focusOut() {
	}

	@Override
	public void restPtz() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanV() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanH() {
		// TODO Auto-generated method stub

	}

	@Override
	public void scanStop() {
		// TODO Auto-generated method stub

	}

	@Override
	public void zoomInPosition(int x, int y) {
		// TODO Auto-generated method stub

	}

	@Override
	public void flipV() {
		// TODO Auto-generated method stub

	}

	@Override
	public void flipH() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAudio(boolean isEnable) {
		// TODO Auto-generated method stub

	}
	@Override
	public void setVideo(boolean isEnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setInterCom(boolean isEnable) {
		// TODO Auto-generated method stub

	}

	@Override
	public void setAlarmEnable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void getAlarmEnable() {
		// TODO Auto-generated method stub

	}

	@Override
	public void setDefinition() {
		// TODO Auto-generated method stub

	}

}
