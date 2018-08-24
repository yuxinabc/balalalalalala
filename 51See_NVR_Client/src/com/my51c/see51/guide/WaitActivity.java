package com.my51c.see51.guide;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.my51see.see51.R;

import java.util.Timer;
import java.util.TimerTask;

public class WaitActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.waitactivity);
		Timer timer = new Timer();
		timer.schedule(new TimerTask() {
			
			@Override
			public void run() {
				Intent intent = new Intent(WaitActivity.this, DeviceIdActivity.class);
				intent.putExtra("BindStyle", "addByVoice");
				startActivity(intent);
				finish();
			}
		}, 4500);
	}
	
}
