//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : See51
//  @ File Name : DeviceSee51Info.java
//  @ Date : 2012-5-30
//  @ Author : Eric Guo <gjl@my51c.com>
//
//



package com.my51c.see51.data;

/**
 * 设备see51平台信息类
 * @author guo
 *
 */
public class DeviceSee51Info {
	private String deviceName;
	private String username;
	private String hwVersion;
	private String swVersion;
	private String dataURL;
	private String diviceID;
	private int type;
	private int status;
	private String location;
	
	public DeviceSee51Info(String diviceID)
	{
		this.diviceID = diviceID;
		this.deviceName = "ipcamera";//this.deviceName = diviceID; //默认名称和id相同
		this.username = "";
		this.hwVersion = "";
		this.swVersion = "";
		this.dataURL = "";
		this.location = "";
	}
	
	
	public String getLocation() {
		return location;
	}

	public void setLocation(String location) {
		this.location = location;
	}

	/**
	 * 获取设备名称
	 * @return 设备名称 
	 */
	public String getDeviceName() {
		return deviceName;
	}
	/**
	 * 设置设备名称
	 * @param deviceName 设备名称
	 */
	public void setDeviceName(String deviceName) {
		this.deviceName = deviceName;
	}
	/**
	 * 获取设备所属账户名
	 * @return 账户名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 设置设备账户名
	 * @param username
	 */
	public void setUsername(String username) {
		this.username = username;
	}
	/**
	 * 获取设备在线状态
	 * @return 0 - 设备不在线 1-设备在线，但无法连接视频 2-设备在线且可以连接视频
	 */
	public int getStatus() {
		return status;
	}
	/**
	 * 设置设备状态
	 * @param status 0 - 设备不在线 1-设备在线，但无法连接视频 2-设备在线且可以连接视频
	 */
	public void setStatus(int status) {
		this.status = status;
	}
	/**
	 * 获取设备数据地址，即设备播放视频地址
	 * @return 设备数据地址
	 */
	public String getDataURL() {
		return dataURL;
	}
	public void setDataURL(String dataURL) {
		this.dataURL = dataURL;
	}

	/**
	 * 获取设备ID
	 * @return 设备ID
	 */
	public String getDiviceID() {
		return diviceID;
	}
	/**
	 * 获取设备公共类型
	 * @return 0 - 非公共 1- 公共
	 */
	public int getType() {
		return type;
	}
	public void setType(int type) {
		this.type = type;
	}
	/**
	 * 获取设备软件版本号 
	 * @return 软件版本号
	 */
	public String getSwVersion() {
		return swVersion;
	}
	public void setSwVersion(String swVersion) {
		this.swVersion = swVersion;
	}
	/**
	 * 获取设备硬件版本号
	 * @return 硬件版本号
	 */
	public String getHwVersion() {
		return hwVersion;
	}
	public void setHwVersion(String hwVersion) {
		this.hwVersion = hwVersion;
	}

}
