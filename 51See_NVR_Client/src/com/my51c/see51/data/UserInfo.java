package com.my51c.see51.data;

/**
 * �û���Ϣ��
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
	 * ��ȡ�û���
	 * @return �û���
	 */
	public String getUsername() {
		return username;
	}
	/**
	 * ��ȡ�ǳ�
	 * @return �ǳ�
	 */
	public String getNickname() {
		return nickname;
	}
	public void setNickname(String nickname) {
		this.nickname = nickname;
	}
	
}
