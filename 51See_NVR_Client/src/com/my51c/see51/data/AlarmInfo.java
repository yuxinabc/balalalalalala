package com.my51c.see51.data;

import android.text.format.Time;

/**
 * ������Ϣ��
 * 
 * @author huangtiebing
 * 
 */
public class AlarmInfo
{
	private String deviceId;
	private String message;
	private String title;
	private Time alarmTime;
	private String devName;

	public AlarmInfo(String deviceId, String devName, String message, String title)
	{
		this.deviceId = deviceId;
		this.devName = devName;
		this.message = message;
		this.title = title;
		alarmTime = new Time();
		alarmTime.setToNow();
		//Log.d("love","alarmTime:"+alarmTime);
	}
	public String getTitle()
	{
		return title;
	}

	public Time getAlarmTime()
	{
		return alarmTime;
	}

	public String getDevName()
	{
		return devName;
	}

	public String getDeviceId()
	{
		return deviceId;
	}

	public String getMessage()
	{
		return message;
	}

}
