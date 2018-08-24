package com.my51c.see51.protocal;

public enum GvapCommand{
	/** 
	* 未知命令
	*/ 
	CMD_UNKNOWN("unkonwn"),
	/** 
	* 登录命令
	*/ 
	CMD_LOGIN("login user"),
	/** 
	* 注销命令
	*/ 
	CMD_LOGOUT("logout usr"),
	/** 
	* 获取版本号命令
	*/ 
	CMD_GET_VERSIONS("get versions"),
	/** 
	* 获取设备信息命令
	*/ 
	CMD_GET_DEVINFO("get dev-info"),
	/** 
	* 获取设备列表命令
	*/ 
	CMD_GET_DEVLIST("get dev-list"),
	/** 
	* 获取公共列表命令
	*/ 
	CMD_GET_PUBLIST("get pub-list"),
	/** 
	* 获取用户信息命令
	*/ 
	CMD_GET_USRINFO("get usr-info"),
	/** 
	* 获取设备状态命令
	*/ 
	CMD_GET_DEVSTATUS("get dev-status"),
	/** 
	* 更新用户信息命令
	*/ 
	CMD_UPDATE_USERINFO("modify_user reg-s"),
	/** 
	* 更新设备信息命令
	*/ 
	CMD_UPDATE_DEVINFO("modify_device reg-s"),
	/** 
	* 注册用户命令
	*/ 
	CMD_REGISTER("register_user reg-s"),
	/** 
	* 绑定设备命令
	*/ 
	CMD_BIND("bind reg-s"),
	/** 
	* 解绑设备命令
	*/ 
	CMD_UNBIND("unbind reg-s"),
	/** 
	* 心跳命令
	*/ 
	CMD_HB("heartbeat user"),
	/** 
	* 服务器通知命令
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