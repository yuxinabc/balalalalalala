//
//
//  Generated by StarUML(tm) Java Add-In
//
//  @ Project : See51
//  @ File Name : DeviceEventListener.java
//  @ Date : 2012-5-30
//  @ Author : Eric Guo <gjl@my51c.com>
//
//



package com.my51c.see51.listener;

/** 
* 设备列表监听接口 <br> 
***** &nbsp;&nbsp需要监听列表改变事件的对象实现该接口，并将自身通过DeviceList的addListListener方法注册到设备列表<br>
*当设备列表发生改变时，调用此接口
*/ 

public interface DeviceListListener {
	public void onListUpdate();
}
