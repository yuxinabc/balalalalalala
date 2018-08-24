package com.my51c.see51.service;

import com.my51c.see51.protocal.GvapCommand;

/** 
* ͨ���¼��� <br> 
***** ͨ�ŵײ����¼�����ʱ��ͨ�������ӿڽ��¼����͵�������
*/ 

public enum GvapEvent {
	
	/** 
	* δ֪����
	*/ 
	UNKNOWN_ERROR("Unkonwn Error"),
	/** 
	* ������������Ϣ
	*/ 
	SERVER_REQUEST("Server Request"),
	/** 
	* �����ɹ�
	*/ 
	OPERATION_SUCCESS("Operation Success"),
	/** 
	* ����ʧ�ܣ�ʧ�ܵ�ԭ��һ��Ϊ�������ܾ�
	*/ 
	OPERATION_FAILED("Operation Failed"),
	/** 
	* ������ʱ��һ��Ϊ����ԭ��
	*/ 
	OPERATION_TIMEOUT("Operation Timeout"),
	/** 
	* ���ӱ�����
	*/ 
	CONNECTION_RESET("Connection Has Been Reset"),	
	/** 
	* ���ӷ�������ʱ
	*/ 
	CONNECT_TIMEOUT("Connect Timeout"),
	/** 
	* ���ӷ�����ʧ��
	*/ 
	CONNECT_FAILED("Connect Failed"),
	/** 
	* �������
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
	* ��ö�����Ӹ��Ӷ����Ա�����ʹ��
	*/ 
	public void attach(Object o)
	{
		this.attach = o;
	}
	/** 
	* ȡ�����Ӷ���
	*/ 
	public Object attach()
	{
		return this.attach;
	}

	/** 
	* ��ö��������������Ա�����ʹ��
	*/ 
	public void setRequest(Object request)
	{
		this.request = request;
	}
	/** 
	* ȡ���������
	*/ 
	public Object getRequest()
	{
		return this.request;
	}
	/** 
	* ��ȡ�ö��������ID
	*/ 
	public GvapCommand getCommandID() {
		return commandID;
	}
	
	/** 
	* ���ø��¼��Ĳ�������ID
	*/
	public void setCommandID(GvapCommand commandID) {
		this.commandID = commandID;
	}
	
	/** 
	* ͨ���¼������ӿ� <br> 
	***** 
	*/ 

	public interface GvapEventListener
	{
		public void onGvapEvent(GvapEvent event);
	}
}
