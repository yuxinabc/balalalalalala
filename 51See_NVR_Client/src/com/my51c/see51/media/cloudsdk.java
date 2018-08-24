package com.my51c.see51.media;

import com.my51c.see51.data.CloudHandle;

public class cloudsdk {

	
	static {
		
		System.loadLibrary("cloudclientsdk");
	}
	
	public native int Native_GHDSCClient_Init(CloudHandle cls);
	public native void Native_GHDSCClient_Fini(CloudHandle cls);
	public native int Native_GHDSCClient_Create(CloudHandle cls);
	public native void Native_GHDSCClient_Destory(CloudHandle cls);
	public native int Native_GHDSCClient_Connect(CloudHandle cls);
	public native boolean Native_GHDSCClient_IsConnect(CloudHandle cls);
	public native void Native_GHDSCClient_DisConnect(CloudHandle cls);
	public native int Native_GHDSCClient_Query_Count_Event(CloudHandle cls);
	public native int Native_GHDSCClient_Query_Data_Event(CloudHandle cls);
	public native int Native_GHDSCClient_Query_Count_Video(CloudHandle cls);
	public native int Native_GHDSCClient_Query_Month_Count_Video(CloudHandle cls);
	public native int Native_GHDSCClient_Query_Data_Video(CloudHandle cls);
	public native int Native_GHDSCClient_Download_Info_Video(CloudHandle cls);
	public native int Native_GHDSCClient_Download_data_Video(CloudHandle cls);
	public native void Native_GHDSCClient_Release_Data(CloudHandle cls, byte[] in);
	
}
