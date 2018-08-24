package com.my51c.see51.media;

public class H264toMP4 implements Runnable{

    String filename;
    private int fps = 5;
    VideoDecoder videoDecoder;
	boolean bRun;
	Thread writeMP4Thread;
	int nDecodeHander = -1;
	
	public H264toMP4(String name, VideoDecoder vd) {
		this.filename = name;
		videoDecoder = vd;
	}	
	
	public Boolean recordVideo(byte[] buffer) {
		//return videoBuffer.put(buffer);
		int ret = videoDecoder.writeVideo(nDecodeHander, buffer);
		//Log.d("H264toMP4", "writeVideo " + ((ret==0)?("success"):("failed")));
		return ret==0;
	}
	public Boolean recordAudio(byte[] buffer) {
		return true;
	}	
	
	public int startRecording() {
		bRun =true;
		int ret=videoDecoder.initRecord(filename,fps);
		nDecodeHander = ret;
		return ret;
	}
	
	public void stopRecording () {	
		videoDecoder.uninitRecord(nDecodeHander);
		nDecodeHander = -1;
		//Log.d("H264toMP4", "uninitRecord!!");
	}
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
//		//Log.d("H264toMP4", "start()!!");
//		while(bRun){
//			byte[] bf = getVideo();
//			if (bf != null && bf.length>0) {
//				int ret = videoDecoder.writeVideo(bf);
//				//Log.d("H264toMP4", "writeVideo " + ((ret==0)?("success"):("failed")));
//			}
//		}		
//		videoDecoder.uninitRecord();
//		//Log.d("H264toMP4", "uninitRecord!!");
	}

	/**
	 * @return the fps
	 */
	public int getFps() {
		return fps;
	}

	/**
	 * @param fps the fps to set
	 */
	public void setFps(int fps) {
		this.fps = fps;
	}	
}
