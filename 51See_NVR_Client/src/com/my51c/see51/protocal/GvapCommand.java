package com.my51c.see51.protocal;

public enum GvapCommand{
	/** 
	* δ֪����
	*/ 
	CMD_UNKNOWN("unkonwn"),
	/** 
	* ��¼����
	*/ 
	CMD_LOGIN("login user"),
	/** 
	* ע������
	*/ 
	CMD_LOGOUT("logout usr"),
	/** 
	* ��ȡ�汾������
	*/ 
	CMD_GET_VERSIONS("get versions"),
	/** 
	* ��ȡ�豸��Ϣ����
	*/ 
	CMD_GET_DEVINFO("get dev-info"),
	/** 
	* ��ȡ�豸�б�����
	*/ 
	CMD_GET_DEVLIST("get dev-list"),
	/** 
	* ��ȡ�����б�����
	*/ 
	CMD_GET_PUBLIST("get pub-list"),
	/** 
	* ��ȡ�û���Ϣ����
	*/ 
	CMD_GET_USRINFO("get usr-info"),
	/** 
	* ��ȡ�豸״̬����
	*/ 
	CMD_GET_DEVSTATUS("get dev-status"),
	/** 
	* �����û���Ϣ����
	*/ 
	CMD_UPDATE_USERINFO("modify_user reg-s"),
	/** 
	* �����豸��Ϣ����
	*/ 
	CMD_UPDATE_DEVINFO("modify_device reg-s"),
	/** 
	* ע���û�����
	*/ 
	CMD_REGISTER("register_user reg-s"),
	/** 
	* ���豸����
	*/ 
	CMD_BIND("bind reg-s"),
	/** 
	* ����豸����
	*/ 
	CMD_UNBIND("unbind reg-s"),
	/** 
	* ��������
	*/ 
	CMD_HB("heartbeat user"),
	/** 
	* ������֪ͨ����
	*/ 
	CMD_NOTIFY_DEVSTATUS("Notify dev-status");
	
	private final String cmdLine;
    GvapCommand(String cmdLine)
	{
		this.cmdLine = cmdLine;
	}
    public String getCmdLine()
    {
    	return this.cmdLine;
    }
    public static GvapCommand getValue(String str)
    {
    	for(GvapCommand value: GvapCommand.values())
    	{
    		if(value.getCmdLine().equals(str))
    		{
    			return value;
    		}
    	}
    	return CMD_UNKNOWN;
    }
};