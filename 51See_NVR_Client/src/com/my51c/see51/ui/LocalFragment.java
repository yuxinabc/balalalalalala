package com.my51c.see51.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.ListFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.my51c.see51.adapter.DeviceListAdapter;
import com.my51c.see51.adapter.DeviceListAdapter.ViewHolder;
import com.my51c.see51.common.AppData;
import com.my51c.see51.data.Device;
import com.my51c.see51.data.DeviceList;
import com.my51c.see51.data.DeviceLocalInfo;
import com.my51c.see51.data.SelectionDevice;
import com.my51c.see51.listener.DeviceListListener;
import com.my51c.see51.widget.DeviceListView;
import com.my51c.see51.widget.DeviceListView.OnRefreshListener;
import com.my51c.see51.widget.ReboundScrollView;
import com.my51see.see51.R;

import java.util.Calendar;
import java.util.ConcurrentModificationException;
import java.util.Timer;
import java.util.TimerTask;

public class LocalFragment extends ListFragment implements DeviceListListener,
OnClickListener, OnRefreshListener {

	private final String TAG = "LocalFragment";
	private DeviceListView listView;
	private DeviceListAdapter adapter;
	private AppData appData;
	private DeviceList localList;
	private View progressView;
	private View waitTextView;
	private View emptyView;
	private TextView emptyDevice;
	static final int MSG_UPDATE = 0;
	static final int MSG_ClEAR_PROGRESSBAR = 1;
	static final int MSG_CHECKTIME = 2;
	private boolean isChecked = false;
	private ReboundScrollView reboundScrollView;

	private Handler mHandler = new Handler()
	{
		@Override
		public void handleMessage(Message msg)
		{
			switch (msg.what)
			{
			case MSG_UPDATE:
				Log.d(TAG, "MSG_UPDATE");
				adapter.updateDeviceData();
				appData.checkLocalDeviceList();
				adapter.notifyDataSetChanged();
				progressView.setVisibility(View.INVISIBLE);
				waitTextView.setVisibility(View.INVISIBLE);
				emptyView.setVisibility(View.INVISIBLE);
				mHandler.sendEmptyMessageDelayed(MSG_CHECKTIME, 3000);
				break;
			case MSG_ClEAR_PROGRESSBAR:
				progressView.setVisibility(View.INVISIBLE);
				waitTextView.setVisibility(View.INVISIBLE);
				emptyView.setVisibility(View.INVISIBLE);
				if(localList.getDeviceCount() == 0)
				{
					emptyDevice.setVisibility(View.VISIBLE);
					Log.d(TAG, "empty Device 1");
				}
			case MSG_CHECKTIME:
				checkDevTime();
				//appData.checkLocalDeviceList();
				/*
				if(localList.getDeviceCount() == 0)
				{
					emptyDevice.setVisibility(View.VISIBLE);
					Log.d(TAG, "empty Device 3");
				}
				else
				{
					emptyView.setVisibility(View.INVISIBLE);
				}
				*/
				emptyView.setVisibility(View.INVISIBLE);
				adapter.notifyDataSetChanged();
				break;
			default:
				break;
			}
		}
	};
	private Timer timer;
	private TimerTask timerTask;

	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setHasOptionsMenu(true);
	}

	@Override
	public void onActivityCreated(Bundle savedInstanceState)
	{
		super.onActivityCreated(savedInstanceState);
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		View v = inflater
				.inflate(R.layout.layout_device_list, container, false);
		listView = (DeviceListView) v.findViewById(android.R.id.list);
		progressView = v.findViewById(R.id.progress_get_devices_image);
		waitTextView = v.findViewById(R.id.loading);
		emptyView = v.findViewById(R.id.emptyView);
		emptyDevice = (TextView)v.findViewById(R.id.emptydevice);
		emptyDevice.setVisibility(View.INVISIBLE);
		
		listView.setItemsCanFocus(true);
		listView.setonRefreshListener(this);
		return v;
	}	
	
	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		//Log.d("FavoriteActivity", "onResume");
		appData = (AppData) getActivity().getApplication();
		timer = new Timer();
		timerTask = new TimerTask()
		{
			@Override
			public void run()
			{
				// TODO Auto-generated method stub
				mHandler.sendEmptyMessage(MSG_ClEAR_PROGRESSBAR);
			}
		};
		timer.schedule(timerTask, 10000);
		
		localList = appData.getLocalList();
		if (localList.getDeviceCount() == 0)
		{
			progressView.setVisibility(View.VISIBLE);
			waitTextView.setVisibility(View.VISIBLE);
			emptyView.setVisibility(View.VISIBLE);
			Log.d(TAG, "empty Device 2");
		}
		else {
			progressView.setVisibility(View.INVISIBLE);
			waitTextView.setVisibility(View.INVISIBLE);	
			emptyView.setVisibility(View.INVISIBLE);
		}
		
		emptyDevice.setVisibility(View.INVISIBLE);
		
		adapter = new DeviceListAdapter(getActivity(), localList, this, true);
		localList.addListListener(this);
		setListAdapter(adapter);
		
		checkDevTime();
	}
	
	public void checkDevTime(){
		try {
			if(!isChecked){
				if(localList.getDeviceCount()!=0){
					Calendar calendar = Calendar.getInstance();
					isChecked = true;
					for(Device device : localList){
						int year = device.getLocalInfo().getnYear();
						Log.i(TAG, "--id:"+device.getID()+"--year:"+year);
						if(year < 2015){
							
							DeviceLocalInfo localDeviceInfo = device.getLocalInfo(); 
							localDeviceInfo.setnYear(calendar.get(Calendar.YEAR));
							localDeviceInfo.setnMon((byte)(calendar.get(Calendar.MONTH)+1));
							localDeviceInfo.setnDay((byte)calendar.get(Calendar.DATE));
							localDeviceInfo.setnHour((byte) calendar.get(Calendar.HOUR_OF_DAY));
							localDeviceInfo.setnMin((byte) calendar.get(Calendar.MINUTE));
							localDeviceInfo.setnSec((byte) calendar.get(Calendar.SECOND));	
							appData.getLocalService().setDeviceParam(localDeviceInfo);
						}
					}
				}
			}
		} catch (NullPointerException e) {
			Log.i(TAG, "--e:"+e);
			e.printStackTrace();
		}catch (ConcurrentModificationException e) {
			// TODO: handle exception
			Log.i(TAG, "--e:"+e);
			e.printStackTrace();
		}
	}
	
	@Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser) {
            this.onRefresh();
	    } else {
	    	
	    }
	}
	
	@Override
	public void onPause()
	{
		super.onPause();
		timerTask.cancel();
		timer.cancel();
		localList.removeListener(this);
	}
	
	@Override
	public void onStop()
	{
		super.onStop();

	}

	@Override
	public void onDetach()
	{
		super.onDetach();
	}
	@Override
	public void onDestroy()
	{
		super.onDestroy();
	}

	@Override
	public void onDestroyView()
	{
		super.onDestroyView();
	}	
	
	@Override
	public void onListItemClick(ListView l, View v, int position, long id)
	{
		try {
			String Gro_Dev = ((ViewHolder) v.getTag()).Gro_Dev;
			if (Gro_Dev.equals("device"))
			{
				Device dev = (Device) ((ViewHolder) v.getTag()).info.getTag();
				if (dev != null && dev.getPlayURL()!= null)
				{						
					SelectionDevice selDev = new SelectionDevice();
					selDev.setDeviceid(dev.getID());
					selDev.setDevicename(dev.getLocalInfo().getDeviceName());
					selDev.setUrl(dev.getPlayURL());
					selDev.setLocal(true);
					
					if(!dev.isbLocalSelected())
					{
						if(!appData.addSelectionDev(selDev))
						{
							Toast.makeText(getActivity(), getString(R.string.enoughselectiondevice), Toast.LENGTH_LONG ).show();
						}
						else
						{
							dev.setbLocalSelected(true);
							adapter.notifyDataSetChanged();
						}
					}
					else
					{
						dev.setbLocalSelected(false);
						appData.removeSelectDev(selDev);
						adapter.notifyDataSetChanged();
					}
					
				
				}
			}
		} catch (NullPointerException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	@Override
	public void onListUpdate() {
		// TODO Auto-generated method stub
		
		mHandler.sendEmptyMessage(MSG_UPDATE);
		if (localList.getDeviceCount() > 0)
		{
			emptyView.setVisibility(View.INVISIBLE);
			timer.cancel();
		} else
		{
			timerTask.cancel();
			timer.cancel();
			timer = new Timer();
			timerTask = new TimerTask()
			{

				@Override
				public void run()
				{
					// TODO Auto-generated method stub
					mHandler.sendEmptyMessage(MSG_ClEAR_PROGRESSBAR);
				}
			};
			timer.schedule(timerTask, 1000);
		}
		
		
	}
	
	@Override
	public void onRefresh() {
		// TODO Auto-generated method stub
		isChecked = false;
		emptyDevice.setVisibility(View.INVISIBLE);
		ConnectivityManager cm = (ConnectivityManager) getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo netinfo = cm.getActiveNetworkInfo();
		if (netinfo == null)
		{
			return;
		}
		if (netinfo.isConnected())
		{
			int t = cm.getActiveNetworkInfo().getType();
			//Log.d("LocalFragment", "Network type = " + t);
			if (t == ConnectivityManager.TYPE_WIFI)
			{
				// ����wifi����ethernet�����������������豸
				// TODO Auto-generated method stub
				appData.getLocalList().clear();
				appData.getLocalService().search();
				adapter.notifyDataSetChanged();
			}
		}	
	}

	@Override
	public void onClick(View v)
	{
		// TODO Auto-generated method stub
		// ��Ӧinfo����
		String Gro_Dev = ((ViewHolder) v.getTag()).Gro_Dev;
		if (Gro_Dev.equals("device"))
		{
			Device dev = (Device) ((ViewHolder) v.getTag()).info.getTag();
			Intent intent = new Intent(this.getActivity(), DeviceInfoActivity.class);
			intent.putExtra("id", dev.getID());
			intent.putExtra("version", " " + " / " + dev.getLocalInfo().getCameraVer());
			intent.putExtra("name", dev.getLocalInfo().getDeviceName());
			intent.putExtra("isLocal", true);
			startActivity(intent);
			this.getActivity().overridePendingTransition(R.anim.slide_out_left , R.anim.slide_in_right);
		}
	}
}
