package com.my51c.see51.service;

import com.my51c.see51.protocal.GvapCommand;

/** 
* 通信事件类 <br> 
***** 通信底层有事件发生时，通过监听接口将事件发送到监听者
*/ 

public enum GvapEvent {
	
	/** 
	* 未知错误
	*/ 
	UNKNOWN_ERROR("Unkonwn Error"),
	/** 
	* 服务器主动消息
	*/ 
	SERVER_REQUEST("Server Request"),
	/** 
	* 操作成功
	*/ 
	OPERATION_SUCCESS("Operation Success"),
	/** 
	* 操作失败，失败的原因一般为服务器拒绝
	*/ 
	OPERATION_FAILED("Operation Failed"),
	/** 
	* 操作超时，一般为网络原因
	*/ 
	OPERATION_TIMEOUT("Operation Timeout"),
	/** 
	* 连接被重置
	*/ 
	CONNECTION_RESET("Connection Has Been Reset"),	
	/** 
	* 连接服务器超时
	*/ 
	CONNECT_TIMEOUT("Connect Timeout"),
	/** 
	* 连接服务器失败
	*/ 
	CONNECT_FAILED("Connect Failed"),
	/** 
	* 网络错误
	*/ 
	NETWORK_ERROR("Network Error"),
	
	OPEN_FAILED("Open Failed"),
	
	REFRESH_FAILED("Refresh Failed"),
	
	LOGIN_FAILED("Login Failed");
	
	private Object attach;
	private Object request;
	private final String errorMessage;
	private GvapCommand commandID;
	
	GvapEvent(String message)
	{
		this.errorMessage = message;
	}
    public String toString()
    {
    	return this.errorMessage;
    }
	/** 
	* 向该对象添加附加对象，以备后续使用
	*/ 
	public void attach(Object o)
	{
		this.attach = o;
	}
	/** 
	* 取出附加对象
	*/ 
	public Object attach()
	{
		return this.attach;
	}

	/** 
	* 向该对象添加请求对象，以备后续使用
	*/ 
	public void setRequest(Object request)
	{
		this.request = request;
	}
	/** 
	* 取出请求对象
	*/ 
	public Object getRequest()
	{
		return this.request;
	}
	/** 
	* 获取该对象的命令ID
	*/ 
	public GvapCommand getCommandID() {
		return commandID;
	}
	
	/** 
	* 设置该事件的操作命令ID
	*/
	public void setCommandID(GvapCommand commandID) {
		this.commandID = commandID;
	}
	
	/** 
	* 通信事件监听接口 <br> 
	***** 
	*/ 

	public interface GvapEventListener
	{
		public void onGvapEvent(GvapEvent event);
	}
}
