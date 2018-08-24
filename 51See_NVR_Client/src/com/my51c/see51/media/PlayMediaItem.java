package com.my51c.see51.media;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.net.Uri;
import android.os.Environment;
import android.text.format.Time;
import android.util.Log;

import com.my51c.see51.listener.OnGetSnapShotListener;
import com.my51c.see51.listener.OnGetVideoConnectionStatusListener;
import com.my51c.see51.listener.OnIntercomListener;
import com.my51c.see51.media.MediaStreamer.MediaEvent;
import com.my51c.see51.media.MediaStreamer.MediaEventListener;
import com.my51c.see51.media.MediaStreamer.VideoFrame;
import com.my51c.see51.widget.MyVideoSurface;
import com.my51see.see51.R;
import com.spoledge.aacdecoder.MultiPlayer;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PipedInputStream;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class PlayMediaItem implements MediaEventListener{
	private final String TAG = "PlayMediaItem";
	private int nIndex = -1;
	private MediaStreamer mediaStreamer;
	private VideoDecoder videoDecoder;
	
	private String deviceID;	
	
	
	public PlayMediaItem(int n)
	{
		nIndex = n;
	}
	
	public String getDeviceID() {
		return deviceID;
	}

	public void setDeviceID(String deviceID) {
		this.deviceID = deviceID;
	}

	private String devicename;
	private String url;
	private Context parentcontext;
	private boolean isLocal;
	private boolean connected = false;
	
	private Thread audioThread;
	private boolean enableIntercom = false;
	private AACRecord aacRecord;
	private boolean enableAudio = false;
	
	private  MyVideoSurface glSurfaceView;
	public MyVideoSurface getGlSurfaceView() {
		return glSurfaceView;
	}

	public void setGlSurfaceView(MyVideoSurface glSurfaceView) {
		this.glSurfaceView = glSurfaceView;
	}

	private  Thread drawSurfaceThread;
	private  Thread getIFrameThread;
	private  Thread connectThread;
	
	private  MultiPlayer multiPlayer;
	private  PipedInputStream audioInputStream;
	
	private  VideoFrame[] frames ;
	private  int iFrameIndex;
	
	private RecState recState = RecState.STOP;
	public RecState getRecState() {
		return recState;
	}

	public void setRecState(RecState recState) {
		this.recState = recState;
	}

	private RecState preState = RecState.STOP;
	private boolean isRecording = false;
	H264toMP4 recorder; 
	
	public boolean isRecording() {
		return isRecording;
	}

	public void setRecording(boolean isRecording) {
		this.isRecording = isRecording;
	}

	public H264toMP4 getRecorder() {
		return recorder;
	}

	public void setRecorder(H264toMP4 recorder) {
		this.recorder = recorder;
	}

	private File imageFile = null;
	private File videoFile;
	private boolean snap;
	
	private int nVideoWidth = 640;
	private int nVideoHeight = 480;
	int fps = -1;
	
	private int nDecodeHander = -1;
	DrawSurfaceRunnable drawSurfaceRunnable = new DrawSurfaceRunnable();
	private volatile Boolean forceExitThread = false;  	//强锟狡斤拷锟斤拷锟竭筹拷时锟斤拷锟矫此憋拷志位
	
	final static int LEFT_START_LINE = 5;
	
	private static final int POLL_INTERVAL = 300;
	
	public enum RecState{  
	    START,PAUSE,STOP  
	} 
	
	public static final int MSG_TIME_OUT 				= 1;
	public static final int MSG_DISCONNECT 			= 2;
	public static final int MSG_GET_IFRAME 			= 3;
	public static final int MSG_SNAP_OK 				= 4;
	private static final int MSG_NONE_NETWORK 			= 5;
	private static final int MSG_INVALID_IP				= 7;
	public static final int MSG_INIT_FAIL 				= 8;
	private static final int MSG_STOP_RECORD 			= 9;
	private static final int MSG_START_INTERCOMM 		= 10;
	private static final int MSG_INTERCOMM_REQ_FAIL 	= 11;
	public static final int MSG_ALARM_STATE_CHANGED 	= 12;
	public static final int MSG_AVQ_CHANGED 	= 13;
	private static final int MSG_TUTK_FAIL		= 14;
	private static final int MSG_GET_IFRAME_TUTK 			= 15;
	private static final int MSG_GET_COMMENT_SUCCESS			= 16;
	private static final int MSG_GET_COMMENT_FAILED			= 17;
	private static final int MSG_POST_COMMENT_SUCCESS			= 18;
	private static final int MSG_POST_COMMENT_FAILED			= 19;
	private static final int MSG_POST_COMMENT_NULL			= 20;
	public static final int MSG_SNAP_FAIL 				= 21;
	private OnGetSnapShotListener mOnGetSnapShotListener;
	private OnGetVideoConnectionStatusListener mOnGetVideoConnectionStatusListener;
	
	public OnGetVideoConnectionStatusListener getmOnGetVideoConnectionStatusListener() {
		return mOnGetVideoConnectionStatusListener;
	}

	public void setmOnGetVideoConnectionStatusListener(
			OnGetVideoConnectionStatusListener l) {
		this.mOnGetVideoConnectionStatusListener = l;
	}

	public void init(Context context, String Id, String DeviceName, String PlayUrl, boolean bLocal)
	{
		parentcontext = context;
		deviceID = Id;	
		devicename = DeviceName;
		url = PlayUrl;
		isLocal = bLocal;
		glSurfaceView = new MyVideoSurface(parentcontext);
		frames = new VideoFrame[2];
	}
	
	public void uninit()
	{	
		
		if(glSurfaceView != null)
		{	
			glSurfaceView = null;
		}
		
		frames = null;
		parentcontext = null;
	}
	
	public void start()
	{
		initdecoder();
		
		if(isLocal)
		{
			connectThread = new ConnectThread("local");
			connectThread.start();
		}
		else
		{
			connectThread = new ConnectThread("51see");
			connectThread.start();
		}
	}
	
	public void setWH(int nWidth, int nHeight)
	{
		if(glSurfaceView != null)
			glSurfaceView.resetRatio(nWidth, nHeight);
	}
	
	public void stop()
	{
		stopThread();
		uninitdecoder();
	
     	if(mediaStreamer!=null)
    	{
     		mediaStreamer.close();
     		mediaStreamer =null;
    	}
     	
     	mediaStreamer = null;
    	frames=null;  	
	}
	
	private void initdecoder()
	{
		if(videoDecoder != null)
		{
			uninitdecoder();
		}
			
	  	videoDecoder = VideoDecoderFactory.createDecoder();
		videoDecoder.initGlobal();
		nDecodeHander =  videoDecoder.initDecoder("H264");
	}
	
	private void uninitdecoder()
	{
		if(videoDecoder != null)
		{
			videoDecoder.uninitDecoder(nDecodeHander);
	    	videoDecoder.uninitGlobal();
	    	videoDecoder = null;
	    	nDecodeHander = -1;
		}
	}
	
	private void handleMessage(int nMsg, String strMsg){
		switch (nMsg) {
		case MSG_TIME_OUT:
		{
			recState = RecState.STOP;
			stopThread();
			if (mediaStreamer != null) {
				mediaStreamer.close();
				mediaStreamer = null; 
			}
			
			if(mOnGetVideoConnectionStatusListener != null)
			{
				mOnGetVideoConnectionStatusListener.onVideoDisconnection(deviceID, nIndex);
			}
			
			connectThread = new ConnectThread("51see");
			connectThread.start();	
		}
			break;

		case MSG_DISCONNECT:
		{
				recState = RecState.STOP;
				stopThread();
				if (mediaStreamer != null) {
					mediaStreamer.close();
					mediaStreamer = null;
				}
				
				if(mOnGetVideoConnectionStatusListener != null)
				{
					mOnGetVideoConnectionStatusListener.onVideoDisconnection(deviceID, nIndex);
				}
				
				connectThread = new ConnectThread("51see");
				connectThread.start();	
		}
			break;

		case MSG_STOP_RECORD:
			
			break;
			
		case MSG_GET_IFRAME:///local or 51see connected

			if(drawSurfaceThread!=null)//if 51see has connected, stop the video
			{
				break;
			}
			
			if(mOnGetVideoConnectionStatusListener != null)
			{
				mOnGetVideoConnectionStatusListener.onVideoConnection(deviceID, nIndex);
			}
	
			drawSurfaceThread = new Thread(
					drawSurfaceRunnable);
			drawSurfaceThread.start();
	
			connected = true;
		
			
			break;
		case MSG_GET_IFRAME_TUTK:///tutk connected
		{
			
		}
			break;
		case MSG_SNAP_OK:
			snap = false;
			if(mOnGetSnapShotListener != null)
			{
				mOnGetSnapShotListener.onAction();
			}
			break;
			
		case MSG_SNAP_FAIL:
			if(imageFile !=null && imageFile.length() == 0)
			{
				imageFile.delete();
				imageFile = null;
			}
			
			snap = false;
			if(mOnGetSnapShotListener != null)
			{
				mOnGetSnapShotListener.onFailAction();
			}
			break;

		case MSG_NONE_NETWORK:
	
			break;

		case MSG_INVALID_IP:
			
			break;
		case MSG_TUTK_FAIL:
		{
			
		}
			break;
			
		case MSG_INIT_FAIL:
			stopThread();
			
			if(mediaStreamer != null)
			{
				mediaStreamer.close();
				mediaStreamer = null;
			}
			
			if(mOnGetVideoConnectionStatusListener != null)
			{
				mOnGetVideoConnectionStatusListener.onVideoDisconnection(deviceID, nIndex);
			}
			
			connectThread = new ConnectThread("51see");
			connectThread.start();

			break;
			
		case MSG_ALARM_STATE_CHANGED:
				
			break;
		case MSG_AVQ_CHANGED:
					
			break;
		case MSG_START_INTERCOMM:
			
			break;
			
		case MSG_INTERCOMM_REQ_FAIL:
			String ret = strMsg;
			if (ret==null) {
				break;
			}else if (ret.equals("audioBusy")) {
				//Log.e(TAG, "Intercomm audioBusy");
			}else if (ret.equals("audioOff")) {
				//Log.e(TAG, "Intercomm audioOff");
			}else if (ret.equals("audioDisconnect")) {
				//Log.e(TAG, "Intercomm audioDisconnect");
			}
		default:
			break;
		}		
	}
	
	private Runnable palyAudio = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			multiPlayer = new MultiPlayer(null, 300, 150);
			try {
				mediaStreamer.createAudioStream();
				audioInputStream = new PipedInputStream(mediaStreamer.getAudioStream());
				multiPlayer.play(audioInputStream);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}
	};
	
	public void startAudio() {		
		mediaStreamer.setAudio(true);
		audioThread = new Thread(palyAudio);
		audioThread.start();		
		enableAudio = true;
	}
	
	public void stopAudio() {
		if(mediaStreamer != null) {
		   mediaStreamer.setAudio(false);
		}
		
		if(audioThread != null){				
			audioThread.interrupt();
		}
		
		if(mediaStreamer != null) {
			mediaStreamer.closeAudioStream();
		}
		
		try {
			if(audioInputStream != null)
			audioInputStream.close();
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		if (multiPlayer!=null) {
			multiPlayer.stop();
			multiPlayer = null;
		}
		enableAudio = false;
	}
	
	private OnIntercomListener mOnIntercomListener = new OnIntercomListener(){

		@Override
		public void onIntercom() {
			// TODO Auto-generated method stub
			if (aacRecord==null) {
				if(mediaStreamer!=null)
				aacRecord = new AACRecord(mediaStreamer, parentcontext);
			}
			
			aacRecord.start();
			handleMessage(MSG_START_INTERCOMM, "");
		}

		@Override
		public void onUnsupported(String error) {
			// TODO Auto-generated method stub
			handleMessage(MSG_INTERCOMM_REQ_FAIL, error);
		}
		
	};
	
	public void onSetInterComAction(boolean  bOn)
	{	
		if(bOn)
		{
			//this.mediaStreamer.setOnIntercomListener(mOnIntercomListener);
			if(mediaStreamer!=null)
			this.mediaStreamer.setOnIntercomListener(mOnIntercomListener);
			//Log.d(TAG, "start intercom");
			enableIntercom = true;
			//this.mediaStreamer.setInterCom(enableIntercom);
			if(mediaStreamer!=null)
			this.mediaStreamer.setInterCom(enableIntercom);
			if(enableAudio)
			{
			   enableAudio = !enableAudio;
			   startAudio();
			}
		}
		else
		{
			//Log.d(TAG, "stop intercom");
			enableIntercom = false;
			
			//this.mediaStreamer.setOnIntercomListener(null);
			if(mediaStreamer!=null)
			this.mediaStreamer.setOnIntercomListener(null);
			if (aacRecord!=null) {
				aacRecord.stop();
				aacRecord = null;
			}	

			if(!enableAudio)
			{
				enableAudio = !enableAudio;
				stopAudio();
			}
		}
	}
	
	class DrawSurfaceRunnable implements Runnable
	{
		public boolean recGetIFrame = false;
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			int[] resolution = new int[2];
			byte[] out;
			try {
				out = new byte[3*1920*1080];
			} catch (OutOfMemoryError e2) {
				// TODO Auto-generated catch block
				return;
			}
			
			long startTime, endTime;
			while(!forceExitThread){															
				startTime=System.currentTimeMillis();
				if (recState==RecState.START) {
					if(recGetIFrame){
						recorder.recordVideo(frames[iFrameIndex].getFrameData());
					}else {
						if(frames[iFrameIndex].isKeyFrame()){
							recGetIFrame = true;
							preState = RecState.START;
							recorder.recordVideo(frames[iFrameIndex].getFrameData());
						}
					}										
				}else if(recState==RecState.PAUSE){

				}else if (recState==RecState.STOP) {
					if (preState != RecState.STOP) {
						preState = RecState.STOP;
						recGetIFrame = false;
						recorder.stopRecording();
						fileScan(videoFile);
					}
				}
				
				try {	
					nowframe++;
					fps = frames[iFrameIndex].getFrameRate();
				
					int nsize = videoDecoder.decoderNal(nDecodeHander, frames[iFrameIndex].getFrameData(), out, resolution);
					if(nsize == 0)
					{
						try
						{
							frames[(iFrameIndex+1)%2] = mediaStreamer.getOneVideoFrame(-1); //  一直锟饺碉拷锟斤拷锟秸碉拷锟节讹拷帧锟脚匡拷始锟斤拷锟斤拷
							if(frames != null && frames[(iFrameIndex+1)%2]!=null /*&& 
									frames[(iFrameIndex+1)%2].getTimeStamp()>frames[iFrameIndex].getTimeStamp()*/){	
								iFrameIndex = (iFrameIndex+1)%2;
							}else if(forceExitThread){
								break;
							}
						}catch(NullPointerException e)
						{
							break;
						}
					}
					//Log.d("frame width and  height", "nVideoWidth:"+nVideoWidth+" nVideoHeight:"+nVideoHeight);
					nVideoWidth = resolution[0];
					nVideoHeight = resolution[1];
					//Log.d(TAG, "fps:"+fps+"width:"+nVideoWidth+"height:"+nVideoHeight);
				} catch (NullPointerException e) {
					// TODO: handle exception
					e.printStackTrace();  // mediaStreamer 锟窖撅拷锟较匡拷
					if (preState != RecState.STOP) {
						preState = RecState.STOP;
						recGetIFrame = false;
						recorder.stopRecording();
						fileScan(videoFile);
					}
					return;					
				}
			
				try {//try catch add by 12.14
					glSurfaceView.update(out, resolution[0], resolution[1]);
				} catch (OutOfMemoryError e1) {
					// TODO Auto-generated catch block
				
				}catch (NullPointerException e){
					Log.i(TAG, "--glSurfaceView.update:"+e);
			
				}

				if (snap) {
					//锟斤拷锟斤拷锟斤拷锟斤拷图片
						snap = false;
				        try {
							Bitmap bmp = RGB24ToARGB(out, resolution);
							if(bmp!=null)
							{
								saveSnapFile(bmp, true, imageFile);
								imageFile = null;
								bmp.recycle();
								bmp = null;
							}
							stopTimer();
							handleMessage(MSG_SNAP_OK, "");
							//mHandler.sendEmptyMessage(4);
				        } 
						catch (Exception e) {
							// TODO: handle exception
						}
				}
				
	
				try
				{
					frames[(iFrameIndex+1)%2] = mediaStreamer.getOneVideoFrame(-1); //  一直锟饺碉拷锟斤拷锟秸碉拷锟节讹拷帧锟脚匡拷始锟斤拷锟斤拷
					if(frames != null && frames[(iFrameIndex+1)%2]!=null /*&& 
							frames[(iFrameIndex+1)%2].getTimeStamp()>frames[iFrameIndex].getTimeStamp()*/){	
						iFrameIndex = (iFrameIndex+1)%2;
					}else if(forceExitThread){
						break;
					}
				}catch(NullPointerException e)
				{
					break;
				}
					
				endTime=System.currentTimeMillis();
				
				
				
				try {
					if(frames[0]!=null && frames[1]!=null){	
						long waitTime = (frames[iFrameIndex].getTimeStamp()-frames[(iFrameIndex+1)%2].getTimeStamp() )-	(endTime - startTime);
						
						if( waitTime>8){
													
								waitTime =waitTime-8;
								if(waitTime<1000){
									Thread.sleep(waitTime);								//锟斤拷幕锟斤拷转时锟结唤锟斤拷锟竭筹拷
								}else{
									;//Thread.sleep(250);
								}
							
						}
					}
				
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					break;
				}
				
			}
			
			if(resolution[0] > 0 && resolution[1] > 0)
			{
		        try 
		        {
					File snapFile = openSnapFile("snapshot", deviceID+".jpg");
					Bitmap bitMap_ARGB = RGB24ToARGB(out, resolution);
					if(bitMap_ARGB!=null)
					{
						saveSnapFile(bitMap_ARGB, true, snapFile);
						bitMap_ARGB.recycle();
						bitMap_ARGB = null;
					}
		        }
		        catch (Exception e) 
		        {
						// TODO: handle exception
				}
					
			}	
			out = null;
		}		
	}
	
	Runnable getIFrameRunnable = new Runnable(){

		@Override
		public void run() {
			// TODO Auto-generated method stub
			while(frames[iFrameIndex]==null ||
					!frames[iFrameIndex].isKeyFrame()){ 
			
				if(mediaStreamer!=null)
				frames[iFrameIndex] = mediaStreamer.getOneVideoFrame(200);				
				if(forceExitThread){
					break;				//强锟狡斤拷锟斤拷锟竭筹拷时锟斤拷锟矫此憋拷志位
				}
				
				if(frames[iFrameIndex] == null)
				{
					continue;
				}
			}
			if(!forceExitThread){	
				fps = frames[iFrameIndex].getFrameRate();
				handleMessage(MSG_GET_IFRAME, "");	//锟斤拷锟秸碉拷锟截硷拷帧锟斤拷锟斤拷始锟斤拷锟斤拷锟竭筹拷
			}
		}
	};
	
	public class ConnectThread extends Thread
	{
		String type;
		public ConnectThread(String type)
		{
			this.type = type;
		}
		
		@Override
		public void run()
		{	
			forceExitThread = false;
			if (url == null) {
				handleMessage(MSG_INVALID_IP, "");
			}
			
			Map<String, String> paramp = new HashMap<String, String>();
			paramp.put("UserName", "admin"); 	// 默锟斤拷锟矫伙拷锟斤拷
			paramp.put("Password", "admin"); 	// 默锟斤拷锟斤拷锟斤拷
			paramp.put("Id", deviceID);	
			
			if (isLocal) {
				mediaStreamer = new LocalMediaStreamer(url, paramp);
				
				if (mediaStreamer != null)
				{
					//mediaStreamer.setOnAlarmEnableListener(mOnAlarmEnableListener);
					//mediaStreamer.setOnAVQSetListener(mOnAVQSetListener);		
					mediaStreamer.setMediaDataListener(PlayMediaItem.this);
				}
				
				if (mediaStreamer == null || !mediaStreamer.open()
					|| videoDecoder == null) 
				{
					handleMessage(MSG_INIT_FAIL, "");
				} 
				else 
				{          
			    	getIFrameThread = new Thread(getIFrameRunnable);
					iFrameIndex = 0;
					getIFrameThread.start();
				}
				
			}
			else if("51see" == type)
			{
				Log.i(TAG, "51see");
				mediaStreamer = MediaStreamFactory.create51SeeMediaStreamer(url, paramp);
				
				if (mediaStreamer != null)
				{
					//mediaStreamer.setOnAlarmEnableListener(mOnAlarmEnableListener);
					//mediaStreamer.setOnAVQSetListener(mOnAVQSetListener);			
					 mediaStreamer.setMediaDataListener(PlayMediaItem.this);
				}
				
				if (mediaStreamer == null || !mediaStreamer.open()
						|| videoDecoder == null) {
					Log.i(TAG, "send MSG_INIT_FAIL");
					handleMessage(MSG_INIT_FAIL, "");
					
				} else {  
					Log.i(TAG, "getIFrameRunnable(51see)");
			    	getIFrameThread = new Thread(getIFrameRunnable);
			    
					iFrameIndex = 0;
					getIFrameThread.start();
					  
				}
			}
		}
	}
	
	
	private void stopThread() {
		doRecordAction(false);
		
		if (recState!=RecState.STOP) {
			recState=RecState.STOP;
		}
		
		forceExitThread = true;
		
	try {
			if(getIFrameThread != null){				
				getIFrameThread.interrupt();
			}
			
			if(connectThread != null)
			{
				connectThread.interrupt();
			}
			
			long start = System.currentTimeMillis();
			if(drawSurfaceThread != null){
				drawSurfaceThread.interrupt();
				drawSurfaceThread.join();				
			}
			long result = System.currentTimeMillis() - start;
			getIFrameThread = null;
			drawSurfaceThread = null;
			connectThread = null;
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		
		if (aacRecord!=null) {		
			aacRecord.stop();
			aacRecord = null;
		}
		
	
		connected = false;
	}
	
	
	Timer timer;
	TimerTask task ;
	
	
	private void startTimer(){  
		if(timer == null)
		{
			timer = new Timer(true);
		}
		
		if(task == null)
		{
			task = new TimerTask(){  
				public void run() {  
					handleMessage(MSG_SNAP_FAIL, "");
				}  
			};  
		}
		
	    timer.schedule(task, 3000); 
	}
	
	private void stopTimer(){ 
		
		snap = false;
		if(timer != null)
		{
			timer.cancel();
			timer = null;
		}
		
		if(task != null)
		{
			task.cancel();
			task = null;
		}
	}
	
	public void startSnapPic() {
		if(imageFile != null)
		{
			if(imageFile.length() == 0)
			{
				imageFile.delete();
				imageFile = null;
				stopTimer();
				
				//handleMessage(MSG_SNAP_FAIL, "");	
			}
		}
		
		snap = true;
		String sDStateString = Environment.getExternalStorageState();
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 锟斤拷取锟斤拷展锟芥储锟借备锟斤拷锟侥硷拷目录
				File SDFile = Environment.getExternalStorageDirectory();
				String imagePath = SDFile.getAbsolutePath()+ File.separator + parentcontext.getString(R.string.app_name)+ File.separator +"image";
				Time mTime = new Time(); // or Time t=new Time("GMT+8");
				mTime.setToNow(); 
				String filename = mTime.format2445() + ".jpg";
				File myFile = new File(imagePath);

				// 锟叫讹拷锟角凤拷锟斤拷锟�,锟斤拷锟斤拷锟斤拷锟津创斤拷
				if (!myFile.exists()) {
					myFile.mkdirs();
				}
				imageFile = new File(myFile, filename);
				imageFile.createNewFile();
				snap = true;
				startTimer();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	private File openSnapFile(String szPath, String szFileName) {
		
		File retFile = null;
		String sDStateString = Environment.getExternalStorageState();
		if (sDStateString.equals(Environment.MEDIA_MOUNTED)) {
			try {
				// 锟斤拷取锟斤拷展锟芥储锟借备锟斤拷锟侥硷拷目录
				File SDFile = Environment.getExternalStorageDirectory();
				szPath = SDFile.getAbsolutePath()
						+ File.separator + parentcontext.getString(R.string.app_name)+ File.separator + szPath;
				File myFile = new File(szPath);

				// 锟叫讹拷锟角凤拷锟斤拷锟�,锟斤拷锟斤拷锟斤拷锟津创斤拷
				if (!myFile.exists()) {
					myFile.mkdirs();
				}

				retFile = new File(myFile, szFileName);
				retFile.createNewFile();
				snap = true;
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (Exception e) {
				// TODO: handle exception
				e.printStackTrace();
			}
		}
		return retFile;
	}
	
	private void saveSnapFile(Bitmap bmp, boolean auto, File image) {
		snap = false;
		try {
			FileOutputStream outputStream = new FileOutputStream(image);
			bmp.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
			outputStream.flush();
			outputStream.close();
			outputStream = null;
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (NullPointerException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}
	
	public Bitmap RGB24ToARGB(byte[] out, int[] resolution) {
		try {
			
		int len = out.length;

		int[] buff = new int[len / 3];
		int j = 0;
		for (int i = 0; i < len; i += 3) {
			byte red = out[i];
			byte green = out[i + 1];
			byte blue = out[i + 2];
			byte alpha = (byte) 0xff;
			buff[j] = ((alpha << 24) & 0xFF000000) | ((red << 16) & 0x00FF0000)
					| ((green << 8) & 0xFF00) | (blue & 0xFF);
			j++;
		}
		Bitmap bmp;
		bmp = Bitmap.createBitmap(buff, resolution[0], resolution[1],Config.ARGB_8888);
		buff = null;
		return bmp;
			
		} catch (OutOfMemoryError e) {
			// TODO Auto-generated catch block
			Log.i(TAG, "Bitmap.createBitmap:"+e);
			return null;
		}
	} 
	
    //通知gallery刷锟斤拷
    public void fileScan(File file){   
        try {
        	handleMessage(MSG_STOP_RECORD, "");
			Uri data = Uri.parse("file://"+file.getAbsolutePath());   
			parentcontext.sendBroadcast(new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, data));
		} catch (Exception e) {
			// TODO Auto-generated catch block 
			e.printStackTrace();
		}   
    }

	@Override
	public void OnMediaDataException(MediaEvent event) {
		// TODO Auto-generated method stub
		int nMsg = -1;
		Log.d(TAG, "reconnection!");
		switch (event) {
		case CONN_TIME_OUT:
			nMsg = MSG_TIME_OUT;
			break;
		
		case CONN_DISCONNECT: 
		
			nMsg = MSG_DISCONNECT;
			break;
		}
		
		handleMessage(nMsg, "");
	}
	
	public MediaStreamer getMediaStreamer(){
		return mediaStreamer;
	}
	public VideoDecoder getVideoDecoder(){
		return videoDecoder;
	}
	public int getFps(){
		return fps;
	}
	public Thread getDrawSurfaceThread(){
		return drawSurfaceThread;
	}
	
	public void setOnGetSnapShotListener(OnGetSnapShotListener l) {
		this.mOnGetSnapShotListener = l;
	}
	
	static int nowframe = 0;
	public boolean doRecordAction(boolean bSetRecord) 
	{
		boolean bResult = false;
		
		if(bSetRecord)
		{
			if(recState == recState.START)
			{
				return true;
			}
			nowframe = 0;
			String sdState = Environment.getExternalStorageState();
			if (sdState.equals(Environment.MEDIA_MOUNTED)) {
				File SDFile = Environment.getExternalStorageDirectory();
				String videoPath = SDFile.getAbsolutePath() + File.separator+parentcontext.getString(R.string.app_name)+ File.separator
						+ "video";
				Time mTime = new Time(); // or Time t=new Time("GMT+8"); 锟斤拷锟斤拷Time
											// Zone锟斤拷锟较★拷
				mTime.setToNow(); // 取锟斤拷系统时锟戒。
				String filename = mTime.format2445() + ".mp4";
				File myFile = new File(videoPath);
				
				if (!myFile.exists()) {
					myFile.mkdirs();
				}
				videoFile = new File(myFile, filename);
				try {
					videoFile.createNewFile();
					
					if(getRecorder() != null)
					{
					   setRecorder(null);
					}
					
					setRecorder(new H264toMP4(videoFile.getAbsolutePath(), getVideoDecoder()));
					getRecorder().setFps(getFps());
					if (getRecorder().startRecording() != -1) {
						setRecState(RecState.START);
						setRecording(true);
						bResult = true;
					} else {
						setRecorder(null);// 锟斤拷始锟斤拷mp4锟斤拷锟斤拷失锟斤拷
					}
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		else
		{	
			if (preState != RecState.STOP) {
				preState = RecState.STOP;
				if(drawSurfaceRunnable!= null)
					drawSurfaceRunnable.recGetIFrame = false;
				recorder.stopRecording();
				fileScan(videoFile);
			}
			
			bResult = true;
			setRecState(RecState.STOP);
			setRecording(false);
			nowframe = 0;
		}
		
		return bResult;
	}
}
