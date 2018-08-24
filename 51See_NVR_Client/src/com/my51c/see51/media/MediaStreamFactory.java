package com.my51c.see51.media;

import java.util.Map;
/**
 * ý�幤���࣬���ݲ�ͬ��url������Ӧ��ý����
 *
 */
public class MediaStreamFactory {
	
	/**
	 * 
	 * @param url ý���ַ ,sspΪת����ַ; tutkidΪp2p��ַ;ddnsΪddns��ַ
	 *   ssp://221.214.50.77:5552;tutkid://EZMT955P4NB38N6MY77J;ddns://183.16.119.183:9539
	 * @param param ý�����
	 * @return ��ý�����
	 */
	public static MediaStreamer create51SeeMediaStreamer(String url,  Map<String, String> param) {
		MediaStreamer newStreamer = null;
		String sspUrl;
		String[] urls = url.split(";");
		if(urls != null && urls.length>0 &&
			urls[0].toLowerCase().startsWith("ssp://"))//����ǰ׺ѡ����ʲô��ʽ����
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
			urls[2].toLowerCase().startsWith("ddns://"))//����ǰ׺ѡ����ʲô��ʽ����
		{
			ddnsUrl = urls[2].substring(7);
			newStreamer = new LocalMediaStreamer(ddnsUrl, param);
		}
		return newStreamer;		
	}	
}
