package com.my51c.see51.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import com.my51c.see51.adapter.SetListAdapter;
import com.my51c.see51.widget.NewSwitch;
import com.my51see.see51.R;
import com.my51see.see51.R.color;

import java.util.ArrayList;
import java.util.HashMap;

public class SetFragment extends Fragment
{
	private final String TAG = "SetActivity" ;
	private SetListAdapter adapter = null;
	private ArrayList<HashMap<String, String>> map_SetList = null;
	private ListView setListView;
	
	private Button btnProgramExit;
	
	private TextView txtUser ;
	private ImageView imgUser;
	private Button btnLogin;
	
	private LinearLayout btnNewCamera;
	private LinearLayout btnFourViews;
	private LinearLayout btnMyCameraList;
	private LinearLayout btnLocalList;
	private LinearLayout btnExit;
	private TextView txLocalList;
	private LinearLayout showWarn, warnMsg, changePsw, about,localVideo;
	private LinearLayout loginLayout;
	private TextView loginLayoutTx;
	private ImageView loginLayoutImg;
	private NewSwitch warnSwitch;
	
	
	public void setIsLoginBtnName(boolean isLogin) {
		if(isAdded())
		{
			if (isLogin) {
				loginLayoutTx.setText(getResources().getString(R.string.logout));
				loginLayoutImg.setBackgroundResource(R.drawable.exit_img);
				btnLogin.setText(R.string.logout);
			}else {
				loginLayoutTx.setText(getString(R.string._clicktologin));
				txtUser.setTextColor(getResources().getColor(color.white));
				loginLayoutImg.setBackgroundResource(R.drawable.gointo_img);
				btnLogin.setText(R.string.login);
			}
		}
	}
	
	@Override 
	public void onCreate(Bundle arg0)
	{
		// TODO Auto-generated method stub
		super.onCreate(arg0);
		//Log.d(TAG, "onCreate");
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState)
	{
		//Log.d(TAG, "onCreateView");
		View v = inflater.inflate(R.layout.set_frame, container,
				false);
		map_SetList = new ArrayList<HashMap<String, String>>();
		HashMap<String, String> mapAlarm = new HashMap<String, String>();
		String alarmTitle = getResources().getString(R.string.alarmNotification);
		mapAlarm.put("item",alarmTitle);
		map_SetList.add(mapAlarm);
		HashMap<String, String> mapHistory = new HashMap<String, String>();
		String historyTitle = getResources().getString(R.string.alarmNotification);
		mapHistory.put("item",historyTitle);
		map_SetList.add(mapHistory);
		
		setListView = (ListView) v.findViewById(R.id.setListView);
		btnNewCamera = (LinearLayout) v.findViewById(R.id.btnNewCamera);
		btnProgramExit = (Button) v.findViewById(R.id.btnProgramExit);
		btnLocalList = (LinearLayout) v.findViewById(R.id.btnLocalCamera);
		btnMyCameraList = (LinearLayout) v.findViewById(R.id.btnMyCamera);
		btnFourViews = (LinearLayout) v.findViewById(R.id.btnFourViews);
		btnLogin = (Button) v.findViewById(R.id.btnLogin);
		txLocalList = (TextView)v.findViewById(R.id.txLocalList);
		txLocalList.setText(R.string.landev);
		btnExit = (LinearLayout) v.findViewById(R.id.progamexit);
		
		txtUser = (TextView) v.findViewById(R.id.txtUser);
		imgUser = (ImageView) v.findViewById(R.id.imgUser);
		showWarn = (LinearLayout)v.findViewById(R.id.showWarn);
		warnMsg = (LinearLayout)v.findViewById(R.id.warnMsg);
		changePsw = (LinearLayout)v.findViewById(R.id.changePsw);
		about = (LinearLayout)v.findViewById(R.id.about);
		localVideo = (LinearLayout)v.findViewById(R.id.local_video);
		loginLayout = (LinearLayout)v.findViewById(R.id.loginLayout);
		loginLayoutTx = (TextView)v.findViewById(R.id.loginLayoutTx);
		loginLayoutImg = (ImageView)v.findViewById(R.id.loginLayoutImg);
		warnSwitch = (NewSwitch)v.findViewById(R.id.showWarnSwitch);
		warnSwitch.setChecked(false);
		
		
		showWarn.setOnClickListener((MainActivity)getActivity());
		warnMsg.setOnClickListener((MainActivity)getActivity());
		changePsw.setOnClickListener((MainActivity)getActivity());
		about.setOnClickListener((MainActivity)getActivity());
		localVideo.setOnClickListener((MainActivity)getActivity());
		loginLayout.setOnClickListener((MainActivity)getActivity());
		warnSwitch.setOnCheckedChangeListener((MainActivity)getActivity());
		btnExit.setOnClickListener((MainActivity)getActivity());
		
		
		btnNewCamera.setOnClickListener((MainActivity)getActivity());
		btnProgramExit.setOnClickListener((MainActivity)getActivity());
		btnLocalList.setOnClickListener((MainActivity)getActivity());
		btnMyCameraList.setOnClickListener((MainActivity)getActivity());
		btnFourViews.setOnClickListener((MainActivity)getActivity());
		btnLogin.setOnClickListener((MainActivity)getActivity());
		
		setIsLoginBtnName(MainActivity.isLogin);
		return v;
	}
	@Override
	public void onResume()
	{
		// TODO Auto-generated method stub
		super.onResume();
		//Log.d(TAG, "onResume");
	
		releaseadapter();
		adapter = new SetListAdapter(getActivity(),(MainActivity)getActivity());
		MainActivity mainactivity = (MainActivity)getActivity();
		setListView.setOnItemClickListener(mainactivity);	
		
		setIsLoginBtnName(mainactivity.isLogin);
		setListView.setAdapter(adapter);
	}
	
	private void releaseadapter()
	{
		if(adapter != null)
		{
			adapter.release();
			adapter = null;
		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
	
	}
	
	public void setLoginState(String user)
	{
		txtUser.setText(user);
		txtUser.setTextColor(color.orange);
		if(user.equals(getString(R.string.notLogedIn)))
			imgUser.setBackgroundResource(R.drawable.vcard);
		else
			imgUser.setBackgroundResource(R.drawable.vcard_active);
	}

	public void onSaveInstanceState(Bundle outState) {  
        super.onSaveInstanceState(outState);  
        setUserVisibleHint(true);
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();

		showWarn.setOnClickListener(null);
		warnMsg.setOnClickListener(null);
		changePsw.setOnClickListener(null);
		about.setOnClickListener(null);
		localVideo.setOnClickListener(null);
		loginLayout.setOnClickListener(null);
		warnSwitch.setOnCheckedChangeListener(null);
		btnExit.setOnClickListener(null);
		
		btnNewCamera.setOnClickListener(null);
		btnProgramExit.setOnClickListener(null);
		btnLocalList.setOnClickListener(null);
		btnMyCameraList.setOnClickListener(null);
		btnFourViews.setOnClickListener(null);
		btnLogin.setOnClickListener(null);
		setListView.setOnItemClickListener(null);
		releaseadapter();
	}
}
