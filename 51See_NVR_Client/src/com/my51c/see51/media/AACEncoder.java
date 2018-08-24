package com.my51c.see51.media;

public class AACEncoder {
    /**
     * Native JNI - initialize AAC encoder
     *
     */
    public native void init(int bitrate, int channels,
            int sampleRate, int bitsPerSample);

    /**
     * Native JNI - encode one frames
     * @param in PCM bytes
     * @param inlen length of in
     * @param out store encoded bytes
     * @param outlen aac data length in out
     */
    public native int encode(byte[] in,int inlen, byte[]out, int outlen);

    /**
     * Native JNI - uninitialize AAC encoder and flush file
     *
     */
    public native void uninit();

    static {
        System.loadLibrary("aac-encoder");
    }
}
