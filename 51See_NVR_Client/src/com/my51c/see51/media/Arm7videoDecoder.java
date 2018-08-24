package com.my51c.see51.media;

class Arm7videoDecoder extends VideoDecoder {
	
	static private native void nativeInitGlobal();
	static private native int nativeInitDecoder(String codecId);
	static private native int nativeInitRecord(String filename,int fps);
	static private native int nativeDecoderNal(int nIndex, byte[] in, int len, byte[] out, int[] resolution);
	static private native int nativeWriteAudio(int nIndex, byte[] in, int len);
	static private native int nativeWriteVideo(int nIndex, byte[] in, int len);
	
	static private native void nativeUninitDecoder(int nIndex);
	static private native void nativeUninitRecord(int nIndex);
	static private native void nativeUninitGlobal();
	
	static private native int nativeInitAACDecode();
	static private native int nativeDecodeAAC(int nIndex, byte[] in, int inLen, byte[] out, int outLen);
	static private native void nativeUninitAACDecode(int nIndex);
	
	static private native int nativeInitAACEncode();
	static private native int nativeEncodeAAC(int nIndex, byte[] in, int inLen, byte[] out, int outLen);
	static private native void nativeUninitEncodeAAC(int nIndex);
	
	static{
		System.loadLibrary("Arm7VideoDecoder");
	}
	@Override
	public void initGlobal() {
		// TODO Auto-generated method stub
		nativeInitGlobal();
	}
	@Override
	public int initDecoder(String codecId) {
		// TODO Auto-generated method stub
		return nativeInitDecoder(codecId);
	}
	@Override
	public int initRecord(String filename,int fps) {
		// TODO Auto-generated method stub
		return nativeInitRecord(filename, fps);
	}
	@Override
	public int decoderNal(int nIndex, byte[] in, byte[] out, int[] resolution) {
		// TODO Auto-generated method stub
		return nativeDecoderNal(nIndex, in, in.length, out, resolution);
	}
	@Override
	public int writeAudio(int nIndex, byte[] in) {
		// TODO Auto-generated method stub
		return nativeWriteAudio(nIndex, in, in.length);
	}
	@Override
	public int writeVideo(int nIndex, byte[] in) {
		// TODO Auto-generated method stub
		return nativeWriteVideo(nIndex, in, in.length);
	}
	@Override
	public void uninitGlobal() {
		// TODO Auto-generated method stub
		nativeUninitGlobal();
	}
	@Override
	public void uninitDecoder(int nIndex) {
		// TODO Auto-generated method stub
		nativeUninitDecoder(nIndex);
	}
	@Override
	public void uninitRecord(int nIndex) {
		// TODO Auto-generated method stub
		nativeUninitRecord(nIndex);
	}
	
	@Override
	public int initAACDecoder() {
		// TODO Auto-generated method stub
		return nativeInitAACDecode();
	}
	@Override
	public int decodeAAC(int nIndex, byte[] in, int inLen, byte[] out, int outLen) {
		// TODO Auto-generated method stub
		return nativeDecodeAAC(nIndex, in, inLen, out,outLen);
	}
	@Override
	public void uninitAACDecoder(int nIndex) {
		// TODO Auto-generated method stub
		nativeUninitAACDecode(nIndex);
	}
	@Override
	public int initAACEncoder() {
		// TODO Auto-generated method stub
		return nativeInitAACEncode();
	}
	@Override
	public int encodeAAC(int nIndex, byte[] in, int inLen, byte[] out, int outLen) {
		// TODO Auto-generated method stub
		return nativeEncodeAAC(nIndex, in, inLen, out, outLen);
	}
	@Override
	public void uninitAACEncoder(int nIndex) {
		// TODO Auto-generated method stub
		nativeUninitEncodeAAC(nIndex);
	}
}
