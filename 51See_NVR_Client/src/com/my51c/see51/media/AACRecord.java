/**
 * 
 */
package com.my51c.see51.media;

import android.content.Context;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;
import android.os.Environment;
import android.util.Log;

import com.sinaapp.bashell.VoAACEncoder;

import java.io.BufferedOutputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.TimeUnit;

/**
 * @author Administrator
 *
 */
public class AACRecord implements Runnable {
	
	private static final String TAG = "AACRecord";
	
	private MediaStreamer streamer;
	private AACEncoder aacEncoder;
	private boolean bStop;
	private AudioRecord audioRecord;
	private int bufferSize;
	
	private Thread getAudioPCM;
	private Thread feedEncodeAAC;
	
    public static int RECORDER_SAMPLERATE = 8000;
    private static final int RECORDER_CHANNELS = AudioFormat.CHANNEL_IN_MONO;
    private static final int RECORDER_AUDIO_ENCODING = AudioFormat.ENCODING_PCM_16BIT;
	
    private ArrayBlockingQueue<byte[]> audioFrames;
    private int nFetchBuf = 2048;
    int volume = 0;
    private Context context;
    String videopath = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"PCM"+File.separator;
	File mVideoPath = new File(videopath);
    DataOutputStream dos = null;
    File mFileDownload ;
    String videopath1 = Environment.getExternalStorageDirectory().getAbsolutePath()+File.separator+"PCM"+File.separator;
	File mVideoPath1 = new File(videopath1);
    DataOutputStream dos1 = null;
    File mFileDownload1 ;
    
	public AACRecord(MediaStreamer loms,Context context) {
		if(!mVideoPath.exists())
		{
			mVideoPath.mkdirs();
		}
		
		mFileDownload = new File(mVideoPath, "pcm_data");
		try {
			dos = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mFileDownload)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(!mVideoPath1.exists())
		{
			mVideoPath1.mkdirs();
		}
		
		mFileDownload1 = new File(mVideoPath1, "pcm_aac_data");
		try {
			dos1 = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(mFileDownload1)));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		this.context =context;
		streamer = loms;
		aacEncoder = new AACEncoder();
		bufferSize = AudioRecord.getMinBufferSize(RECORDER_SAMPLERATE,RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING);
		nFetchBuf = bufferSize;
		
		if(bufferSize < 2048)
		   bufferSize = 2048;
		audioRecord = new AudioRecord(MediaRecorder.AudioSource.MIC,
				RECORDER_SAMPLERATE, RECORDER_CHANNELS,RECORDER_AUDIO_ENCODING, bufferSize);
		
		audioFrames = new ArrayBlockingQueue<byte[]>(64);
	}
		
	public void start() {
		audioFrames.clear();
		bStop = false;
		getAudioPCM = new Thread(this);
		feedEncodeAAC = new Thread(runEncodeAAC);
		getAudioPCM.start();
		feedEncodeAAC.start();
	}
	
	public double getAmplitude()
	{	
		return volume;
	}
	
	private int computeVolume(byte[] audiodata, int nLength)
	{ 
		 int max = 0;
		 int temp = 0;
         for (int i = 0; i < nLength/2; i+=2) {
        	 temp = (audiodata[i] + 256 * audiodata[i+1]);
        	 if(Math.abs(temp) > max);
        	 	max = Math.abs(temp);
         }
       return max;
	}
	
	/** 
	 * Get audio data from AudioRecord.
	 */
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try 
		{
			audioRecord.startRecording();
		}
		catch (IllegalStateException e)
		{
			e.printStackTrace();
		}
		int read = 0;	
		
		while (!bStop) {
			
			byte[] data= new byte[bufferSize];
			read = audioRecord.read(data, 0, bufferSize);
			volume = computeVolume(data, read);
			System.out.println("---------�ɼ���Ƶ");
			Log.d(TAG, "Volume size: " + volume);
			if (AudioRecord.ERROR_INVALID_OPERATION != read) {
				try {
					audioFrames.add(data);
				} catch (IllegalStateException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}catch (NullPointerException e) {
					e.printStackTrace();
				}
			}			
		}
		
		if(audioRecord != null)
		{
			audioRecord.stop();
			audioRecord.release();
			audioRecord = null;
		}
	}

	Runnable runEncodeAAC = new Runnable() {
		
		@Override
		public void run() {
			// TODO Auto-generated method stub

			VoAACEncoder vo = new VoAACEncoder();
			vo.Init(RECORDER_SAMPLERATE, 16000, (short) 1, (short) 1);// ������:16000,bitRate:32k,������:1������:0.raw
			
			while (!bStop) {
				byte[] pcm = null;
				try {
					pcm = audioFrames.poll(100, TimeUnit.MILLISECONDS);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					continue;
				}
				if (pcm == null) {
					continue;
				}
				
				
				byte[] ret = vo.Enc(pcm);
				if (ret!=null) {
					streamer.sendAudioData(ret);	// ��������
				}
			}
			vo.Uninit();
		}
	};
	
	public void stop(){
		bStop = true;
		streamer.stopIntercomm();
		getAudioPCM.interrupt();
		feedEncodeAAC.interrupt();
	}
	
	// �����Լ��
	private int gcd(int a, int b) {
		while (b>0) {
			int tmp = b;
			b = a % b;
			a = tmp;
		}
		return a;
	}
	
	//����С������
	private int lcm(int a, int b) {
		return a * ( b / gcd(a, b));
	}
}
