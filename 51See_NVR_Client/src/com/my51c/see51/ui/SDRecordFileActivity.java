package com.my51c.see51.ui;

import android.app.Dialog;
import android.app.ListActivity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;
import com.my51c.see51.adapter.SDRecordFileListAdapter;
import com.my51c.see51.adapter.SDRecordFileListAdapter.ViewHolder;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.SDFileInfo;
import com.my51c.see51.listener.OnDeleteSDFileListener;
import com.my51c.see51.listener.OnGetFileDataOverListener;
import com.my51c.see51.listener.OnGetSDFileDataListener;
import com.my51c.see51.listener.OnGetSDFileListListener;
import com.my51c.see51.media.MediaStreamFactory;
import com.my51c.see51.media.MediaStreamer.MediaEvent;
import com.my51c.see51.media.MediaStreamer.MediaEventListener;
import com.my51c.see51.media.RemoteInteractionStreamer;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.my51c.see51.widget.ToastCommom;
import com.my51see.see51.R;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SDRecordFileActivity extends ListActivity implements OnRefreshListener {
	
	private final String TAG = "SDRecordFileActivity";
	private DeviceListView folderFileListView;
	private View progressView;
	private View waitTextView;
	private View emptyView;
	private AppData appData;
	private RemoteInteractionStreamer mediaStreamer;
	public  static String nowdate;
	private ArrayList<SDFileInfo> mFileList;
	private SDRecordFileListAdapter mAdapter;
	private SDFileInfo mSelSDInfo;
	private ViewHolder mSelholder;
	private boolean bdownload = false;
	private File mFileDownload;
	DataOutputStream dos;
	private String deviceID;
	private String dialogFileName;
	
	static final int MSG_UPDATE = 0;

	static final int MSG_ClEAR_PROGRESSBAR = 1;
	static final int MSG_START_DOWNLOAD = 2;
	static final int MSG_NEXT_DOWNLOAD = 3;
	static final int MSG_PERCENT_PROCESSBAR = 4;
	static final int MSG_UPDATE_DATA = 5;
	static final int MSG_REFRESH = 6;
	static final int MSG_DELETE_SUCCESS= 10;
	private static final int MSG_TIME_OUT 				= 2001;
	private static final int MSG_DISCONNECT 			= 2002;
	
	private String dateHour = null;
	private TextView titleName;
	private boolean isFirstItem = false;
	private String strFileList = null;
	private ArrayList<SDFileInfo> dlInfoList;
	private ArrayList<ViewHolder> dlHolderList;
	private boolean isDownLoading = false;
	private boolean canRefresh = true;
	private boolean isDlRemove = false;
	private ToastCommom toast = new ToastCommom();
	private String url;
	private String nvrDeviceId = "";
	
	private MediaEventListener mMediaEventListener = new MediaEventListener() {
		
		@Override
		public void OnMediaDataException(MediaEvent event) {
			// TODO Auto-generated method stub
			Message msg = new Message();
			switch (event) {
			case CONN_TIME_OUT:
				msg.what = MSG_TIME_OUT;
			break;
			
			case CONN_DISCONNECT: 
				msg.what = MSG_DISCONNECT;
			break;
			}
			
			mHandler.sendMessage(msg);
		}
	};
	
	@Override
	protected void onCreate(Bundle arg0) {
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		setContentView(R.layout.sdrecord_activity);
		
		dateHour = getIntent().getStringExtra("dateHour");
		isFirstItem = getIntent().getBooleanExtra("isFirstItem", false);
		LinearLayout backLayout = (LinearLayout)findViewById(R.id.sd_back_layout);
		ImageView searchImg = (ImageView)findViewById(R.id.sd_search_img);
		backLayout.setOnClickListener(new OnClickListener() {
			
			@Override
			public void onClick(View arg0) {
				backMainActivity();
			}
		});
		
		searchImg.setVisibility(View.GONE);
		titleName = (TextView)findViewById(R.id.sd_titleName);
		titleName.setText(dateHour);
		
		folderFileListView = (DeviceListView) findViewById(android.R.id.list);
		folderFileListView.setItemsCanFocus(true);
		folderFileListView.setonRefreshListener(this);
		
		mFileList = new ArrayList<SDFileInfo>();
		dlInfoList = new ArrayList<SDFileInfo>();
		dlHolderList = new ArrayList<SDRecordFileListAdapter.ViewHolder>();
		mAdapter = new SDRecordFileListAdapter(this, mFileList);
		folderFileListView.setAdapter(mAdapter);
		
		//folderFileListView.setOnItemClickListener(this);
		progressView = findViewById(R.id.progress_get_devices_image);
		waitTextView = findViewById(R.id.loading);
		emptyView    = findViewById(R.id.emptyView);
		
		appData = (AppData) getApplication();
		Bundle bundle = getIntent().getExtras();
		nvrDeviceId = bundle.getString("nvrDeviceId");
		deviceID = bundle.getString("id");
		url = bundle.getString("url");
		nowdate = getnowdate(); 
	
	}
	
	private void createRemoteOperaction()//锟斤拷取锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷锟斤拷
	{	
		Map<String, String> paramp = new HashMap<String, String>();
		paramp.put("UserName", "admin"); 
		paramp.put("Password", "admin"); 
		paramp.put("Id", deviceID);	
		Log.i(TAG, "createRemoteOperaction:deviceID"+deviceID+"-url:"+url);
		if(SDRecordFolderActivity.isLocal)
		{  
			mediaStreamer = new RemoteInteractionStreamer(url, paramp);	
		}
		else
		{
			mediaStreamer = MediaStreamFactory.createRemoteInteractionMediaStreamer(url, paramp);
		}
			
		if(mediaStreamer != null)
		{   
			//appData.setRemoteInteractionStreamer(mediaStreamer);
			mediaStreamer.open();
			mediaStreamer.setDevId(deviceID);
		}
		else
		{
			Log.i(TAG,"--mediaStreamer == null");
			//appData.setRemoteInteractionStreamer(null);
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		super.onDestroy();
		if(mediaStreamer!=null){
			mediaStreamer.close();
			mediaStreamer.setOnGetSDFileListListener(null);
			mediaStreamer.setOnGetSDFileDataListener(null);
			mediaStreamer.setOnGetFileDataOverListener(null);
			mediaStreamer = null;
		}
	}

	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		if(PlayAcy.isPlayBack){
			PlayAcy.isPlayBack = false;
		}
		
		createRemoteOperaction();
		if(mediaStreamer != null)
		{
			mediaStreamer.setOnGetSDFileListListener(mOnGetSDFileListListener);
			mediaStreamer.setOnGetSDFileDataListener(mOnGetSDFileDataListener);
			mediaStreamer.setOnGetFileDataOverListener(mOnGetFileDataOverListener);
			mediaStreamer.getSDFolderFileListByDate(dateHour,nvrDeviceId);
			  mediaStreamer.setMediaDataListener(mMediaEventListener);
			mHandler.sendEmptyMessageDelayed(MSG_REFRESH, 800);
		}
		
		super.onResume();
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		if(mediaStreamer != null)
		{
			mediaStreamer.setMediaDataListener(null);
		}
	}
	
	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_TIME_OUT:
			case MSG_DISCONNECT:
				emptyView.setVisibility(View.INVISIBLE);
				break;
			case MSG_UPDATE:
				mAdapter.notifyDataSetChanged();
				progressView.setVisibility(View.INVISIBLE);
				waitTextView.setVisibility(View.INVISIBLE);
				emptyView.setVisibility(View.INVISIBLE);
				break;
			case MSG_ClEAR_PROGRESSBAR:
				progressView.setVisibility(View.INVISIBLE);
				waitTextView.setVisibility(View.INVISIBLE);
				emptyView.setVisibility(View.INVISIBLE);
				break;
			case MSG_START_DOWNLOAD:
				
				break;
				
			case MSG_PERCENT_PROCESSBAR:
			{	
				computepercent(msg.arg1);
			}
				break;
			case MSG_DELETE_SUCCESS:
//				toast.ToastShow(getApplicationContext(), getString(R.string.delete_success), 1000);
				refreshsdfile();
				break;
			case MSG_NEXT_DOWNLOAD:
			{
				nextDownload();
			}
				break;
			case MSG_UPDATE_DATA:
				synchronized (mFileList) 
				{
					mFileList.clear();
				
					if(strFileList!=null){
						String []strItem = strFileList.split("\\|");
						Log.i(TAG, "--count of file:"+strFileList);
						
							for(int i=1; i<strItem.length; i++)
							{
								String []itemText = strItem[i].split(",");
								
								if(itemText.length != 2)
									continue;
								SDFileInfo tmp = new SDFileInfo();
								tmp.setSzDeviceid(deviceID);
								tmp.setSzFileName(itemText[0]);
				 				tmp.setnFileSize(Integer.parseInt(itemText[1]));
				 				if(!itemText[0].contains("tmp")){
				 					mFileList.add(0, tmp);
				 				}
							}
							mHandler.sendEmptyMessage(MSG_UPDATE);
					}
				}
				break;
			case MSG_REFRESH:
				refreshsdfile();
				break;
			default:
				break;
			}
		}
	};
	
	private OnGetSDFileListListener mOnGetSDFileListListener = new OnGetSDFileListListener() {
		@Override
		public void onGetFileList(byte[] devbuf) {
			// TODO Auto-generated method stub
			strFileList = byteToString(devbuf);
			mHandler.sendEmptyMessage(MSG_UPDATE_DATA);//锟节凤拷Ui锟竭筹拷锟斤拷锟睫革拷锟斤拷ListView锟襟定碉拷锟斤拷锟捷讹拷锟斤拷锟斤拷锟酵拷锟斤拷锟斤拷锟斤拷锟节凤拷UI锟竭筹拷锟叫革拷锟斤拷锟斤拷锟竭程控硷拷锟侥达拷锟斤拷
														//fix:锟斤拷锟竭筹拷锟铰伙拷玫锟斤拷锟斤拷锟斤拷锟斤拷却锟斤拷锟斤拷锟斤拷叱蹋锟斤拷锟街憋拷锟斤拷锟斤拷锟斤拷叱锟斤拷锟轿拷涓持�
		}
	};
	
	public static String byteToString(byte[] src)
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
	
	public void backMainActivity()
	{
		if(isDownLoading){
			final Dialog dialog = new Dialog(SDRecordFileActivity.this,R.style.Erro_Dialog);
			dialog.setContentView(R.layout.del_dialog);
		  		TextView delTx = (TextView)dialog.findViewById(R.id.erroTx);
		  		Button cancel = (Button)dialog.findViewById(R.id.del_cancel);
		  		Button ok = (Button)dialog.findViewById(R.id.del_ok);
		  		delTx.setText(getString(R.string.fiel_downlaoding));
		  		cancel.setText(getString(R.string.download_cancel));
		  		ok.setText(getString(R.string.download_continue));
		  		cancel.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO 删锟斤拷未锟斤拷锟斤拷锟斤拷锟侥硷拷锟斤拷锟剿筹拷
					delNonCompleteVideo(mSelSDInfo);
					SDRecordFileActivity.this.finish();
					overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
					dialog.dismiss();
				}
			});
		  		ok.setOnClickListener(new OnClickListener() {
				
				@Override
				public void onClick(View v) {
					// TODO Auto-generated method stub
					dialog.dismiss();
				}
			});
		  		dialog.show();
		}else{
			SDRecordFileActivity.this.finish();
			overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
		}
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		backMainActivity();
		return super.onKeyDown(keyCode, event);
	}

	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		if(canRefresh){
			canRefresh = false;
			refreshsdfile();
			new Timer().schedule(new TimerTask() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					canRefresh = true;
				}
			}, 1500);
		}else{
			Log.i(TAG, "hold on 3s");
		}
	}
	
	private void playVideo(String fileName,String name) {
		
		//Log.d("open file", fileName);
		File file = new File(fileName);
	
		if (!file.exists()) {
			return;
		}
		if (fileName.endsWith(".jpg")) {
			Intent intent = new Intent(Intent.ACTION_VIEW);
			intent.setDataAndType(Uri.fromFile(file), "image/jpeg");
			startActivity(intent);
		} else {
			Intent intent  = new Intent(SDRecordFileActivity.this, PlayAcy.class);
			intent.putExtra("isFromSD", true);
			intent.putExtra("string", fileName);
			intent.putExtra("name", name);
			startActivity(intent);
		}
	}
	

	public void refreshsdfile()
	{
		for(SDFileInfo info : dlInfoList){
			info.setWaitingForDl(false);
		}
		dlInfoList.clear();
		dlHolderList.clear();
		if(isDownLoading){
			removeDownload();
			isDownLoading = false;
		}
		progressView.setVisibility(View.VISIBLE);
		waitTextView.setVisibility(View.VISIBLE);
		mAdapter.notifyDataSetChanged();
		if(mediaStreamer != null)
		{
			mediaStreamer.getSDFolderFileListByDate(dateHour,nvrDeviceId);
		}
	}
	
	public String getnowdate()
	{	
		SimpleDateFormat sdf=new SimpleDateFormat("yyyyMMdd");    
		return sdf.format(new java.util.Date()); 
	}
	/*
	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
	*/
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		// TODO Auto-generated method stub
		if(canRefresh == false)
		   return;
		
		try {
			ViewHolder vhold = (ViewHolder)v.getTag();
//		mSelholder = vhold;
			SDFileInfo tmp = (SDFileInfo)vhold.tvfilename.getTag();
//		mSelSDInfo = tmp;
			Log.i(TAG, "锟斤拷锟絠tem锟斤拷"+position);
			
			if(vhold.ivdownload.getVisibility() == View.VISIBLE)
			{
				Log.i(TAG, "锟窖硷拷锟斤拷锟斤拷锟斤拷锟叫憋拷"+position);
				dlInfoList.add(tmp);
				dlHolderList.add(vhold);
				tmp.setWaitingForDl(true);
				if(!isDownLoading){
					isDownLoading = true;
					startdownload();
				}
				mAdapter.showbuttonType(vhold, 2);
				return;
			}
			
			if(vhold.ivcanceldownload.getVisibility() == View.VISIBLE)
			{
				Log.i(TAG, "锟斤拷锟狡筹拷锟斤拷锟斤拷锟叫憋拷"+position);
				tmp.setWaitingForDl(false);
				if(tmp.getSzFileName().equals(mSelSDInfo.getSzFileName())){//删锟斤拷锟斤拷锟斤拷锟斤拷锟截碉拷锟侥硷拷
					Log.i(TAG, "");
					removeDownload();
				}else{														//未锟斤拷锟截碉拷锟侥硷拷锟狡筹拷锟斤拷锟截讹拷锟斤拷
					dlInfoList.remove(tmp);
					dlHolderList.remove(vhold);
					tmp.setWaitingForDl(false);
				}
				mAdapter.showbuttonType(vhold, 1);
				return;
			}
			
			if(vhold.ivplay.getVisibility() == View.VISIBLE)
			{
				playVideo(tmp.getRealPath() + tmp.getSzFileName(),tmp.getSzFileName());
				System.out.println("------"+tmp.getRealPath() + tmp.getSzFileName());
				mAdapter.showbuttonType(vhold, 3);
				return;
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	
	public void removeDownload()
	{	
		isDlRemove = true;
		if(mediaStreamer != null)
		{
			mediaStreamer.close();
			mediaStreamer.setOnGetSDFileListListener(null);
			mediaStreamer.setOnGetSDFileDataListener(null);
			mediaStreamer.setOnGetFileDataOverListener(null);
			mediaStreamer = null;
		}
		
		if(dos != null)
		{
			try {
				dos.flush();
				dos.close();
				dos = null;
			} catch (IOException e) {
				// TODO Auto-generated catch block
				dos = null;
				e.printStackTrace();
			}
		}
		bdownload = false;
		mSelSDInfo.setbDown(bdownload);
		mSelholder.numbar.setVisibility(View.GONE);
		delNonCompleteVideo(mSelSDInfo);
		if(dlInfoList.size()!=0){
			dlInfoList.remove(0);
			dlHolderList.remove(0);
		}
		if(dlInfoList.size()!=0){
			startdownload();
			isDownLoading = true;
		}else{
			Log.i(TAG, "全锟斤拷锟斤拷锟斤拷锟斤拷桑锟斤拷锟斤拷锟斤拷斜锟轿拷锟�");
			isDownLoading = false;
		}
	}	
	
	public void startdownload()
	{
		if(isDlRemove){
			createRemoteOperaction();
			if(mediaStreamer != null)
			{
				Log.i(TAG, "锟斤拷锟铰达拷锟斤拷TCP锟斤拷锟接ｏ拷锟襟定硷拷锟斤拷");
				mediaStreamer.setOnGetSDFileListListener(mOnGetSDFileListListener);
				mediaStreamer.setOnGetSDFileDataListener(mOnGetSDFileDataListener);
				mediaStreamer.setOnGetFileDataOverListener(mOnGetFileDataOverListener);
			}
			isDlRemove = false;
		}
		mSelholder = dlHolderList.get(0);
		mSelSDInfo = dlInfoList.get(0);
		mSelSDInfo.setnCurSize(0);
		delNonCompleteVideo(mSelSDInfo);
		Log.i(TAG, "锟斤拷始锟斤拷锟截ｏ拷"+mSelSDInfo.getnCurSize()*100/mSelSDInfo.getnFileSize());
		if(mSelSDInfo == null)
		{
			return;
		}
		String videopath = mSelSDInfo.getRealPath();
		File mVideoPath = new File(videopath);
		if(!mVideoPath.exists())
		{
			mVideoPath.mkdirs();
		}
		
		mFileDownload = new File(mVideoPath, mSelSDInfo.getSzFileName());
		if(mediaStreamer != null)
		{
			Log.i(TAG, "锟斤拷锟斤拷锟斤拷锟斤拷锟侥硷拷锟斤拷"+mSelSDInfo.getSzFileName());
			try {
				Thread.sleep(1000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			mediaStreamer.getSDFileData( mSelSDInfo.getSzFileName(),nvrDeviceId);
		}
		
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mFileDownload)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			dos = null;
			e.printStackTrace();
		}
		bdownload = true;
		mSelSDInfo.setbDown(bdownload);
		mSelholder.numbar.setVisibility(View.VISIBLE);
	}
	
	public void nextDownload()
	{	
		try {
			if(dos != null)
			{
				try {
					dos.flush();
					dos.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					dos = null;
					e.printStackTrace();
				}
			}
			bdownload = false;
			mSelSDInfo.setbDown(bdownload);
			mSelholder.numbar.setVisibility(View.GONE);
			
			File tmpFile = new File(mSelSDInfo.getRealPath()+mSelSDInfo.getSzFileName());
			boolean bCompleteDown = false;
			if (tmpFile.exists() && tmpFile.isFile()){  
		        if(tmpFile.length() != mSelSDInfo.getnFileSize())
		        {
		        	bCompleteDown = false;
		        }
		        else
		        {
		        	bCompleteDown = true;
		        }
		    }else{  
		    	bCompleteDown = false;
		    }  
			
			if(bCompleteDown)
			{
				mAdapter.showbuttonType(mSelholder, 3);
			}
			else
			{
				mAdapter.showbuttonType(mSelholder, 1);
				toast.ToastShow(this, getString(R.string.download_failed), Toast.LENGTH_LONG );
			}
			
			
			if(dlInfoList.size()!=0){
				dlInfoList.remove(0);
				dlHolderList.remove(0);
			}
			if(dlInfoList.size()!=0){
				startdownload();
				isDownLoading = true;
			}else{
				Log.i(TAG, "全锟斤拷锟斤拷锟斤拷锟斤拷桑锟斤拷锟斤拷锟斤拷斜锟轿拷锟�");
				isDownLoading = false;
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (IndexOutOfBoundsException e) {
			// TODO: handle exception
			e.printStackTrace();
		}
	}	
	private OnDeleteSDFileListener mOnDeleteSDFileListener = new OnDeleteSDFileListener() {
		
		@Override
		public void OnDeleteSDFileSuccess() {
			// TODO Auto-generated method stub
			mHandler.sendEmptyMessage(MSG_DELETE_SUCCESS);
		}
		
		@Override
		public void OnDeleteSDFileFailed() {
			// TODO Auto-generated method stub
			
		}
	};
    private OnGetSDFileDataListener mOnGetSDFileDataListener = new OnGetSDFileDataListener() {

		@Override
		public void onGetFileDataPiece(byte[] devbuf) {
			// TODO Auto-generated method stub
			if(!isDlRemove){
				if(dos != null)
				{
					try {
						dos.write(devbuf);
						Message msg = mHandler.obtainMessage();
						msg.what = MSG_PERCENT_PROCESSBAR;
						msg.arg1 = devbuf.length;
						mHandler.sendMessage(msg);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
		}
	};
	
	private OnGetFileDataOverListener mOnGetFileDataOverListener = new OnGetFileDataOverListener() {

		@Override
		public void onGetFileDataOver() {
			// TODO Auto-generated method stub
			if(!isDlRemove){
				mSelSDInfo.setWaitingForDl(false);
				mHandler.sendEmptyMessage(MSG_NEXT_DOWNLOAD);//nextDownload();	mAdapter.showbuttonType(mSelholder, 3);
			}
		}
	};
	
	public void computepercent(int nAddSize)//		buf+curSizw/totall size
	{
		long nTotalSize = mSelSDInfo.getnFileSize();
		long nCurSize   = mSelSDInfo.getnCurSize() + nAddSize;
		mSelSDInfo.setnCurSize(nCurSize);
		double dper = (double)nCurSize /nTotalSize*100;
		mSelholder.numbar.setProgress((int)dper);
	}
	
	public void delNonCompleteVideo(SDFileInfo mSDFileInfo){
		
		File f = new File(mSDFileInfo.getRealPath()+"/"+mSDFileInfo.getSzFileName());
		if (f.exists()) {
			f.delete();
			Log.i(TAG, "删锟斤拷未锟斤拷锟斤拷锟斤拷锟斤拷募锟斤拷锟�"+mSDFileInfo.getSzFileName());
		}else{
			Log.i(TAG, "锟侥硷拷锟斤拷锟斤拷锟斤拷");
		}
	}
}
