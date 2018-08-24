package com.my51c.see51.media;

import java.util.Map;
/**
 * 媒体工厂类，根据不同的url创建相应的媒体类
 *
 */
public class MediaStreamFactory {
	
	/**
	 * 
	 * @param url 媒体地址 ,ssp为转发地址; tutkid为p2p地址;ddns为ddns地址
	 *   ssp://221.214.50.77:5552;tutkid://EZMT955P4NB38N6MY77J;ddns://183.16.119.183:9539
	 * @param param 媒体参数
	 * @return 流媒体对象
	 */
	public static MediaStreamer create51SeeMediaStreamer(String url,  Map<String, String> param) {
		MediaStreamer newStreamer = null;
		String sspUrl;
		String[] urls = url.split(";");
		if(urls != null && urls.length>0 &&
			urls[0].toLowerCase().startsWith("ssp://"))//根据前缀选择用什么方式连接
		{
			sspUrl = urls[0].substring(6);
			newStreamer = new LocalMediaStreamer(sspUrl, param);
		}
		return newStreamer;
	}
	
	public static RemoteInteractionStreamer createRemoteInteractionMediaStreamer(String url,  Map<String, String> param) {
		RemoteInteractionStreamer newStreamer = null;
		String sspUrl;
		if(url == null)
		   return newStreamer;
		
		String[] urls = url.split(";");
		
		if(urls != null && urls.length>0 &&
			urls[0].toLowerCase().startsWith("ssp://"))
		{
			sspUrl = urls[0].substring(6);
			newStreamer = new RemoteInteractionStreamer(sspUrl, param);
		}
		return newStreamer;
	}
	
	
	public static MediaStreamer createDdnsMediaStreamer(String url,  Map<String, String> param)
	{
		MediaStreamer newStreamer = null;
		String ddnsUrl;
		String[] urls = url.split(";");
		if(urls != null && urls.length>2 &&
			urls[2].toLowerCase().startsWith("ddns://"))//根据前缀选择用什么方式连接
		{
			ddnsUrl = urls[2].substring(7);
			newStreamer = new LocalMediaStreamer(ddnsUrl, param);
		}
		return newStreamer;		
	}	
}
