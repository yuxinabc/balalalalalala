package com.my51c.see51.data;

/**
 * 用户信息类
 * @author guo
 *
 */
public class UserInfo {

	private String username;
	private String nickname;
	public UserInfo(String username)
	{
		this.username = username;
	}
	/**
	 * 获取用户名
	 * @return 用户名
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * 获取昵称
	 * @return 昵称
	 */
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}
