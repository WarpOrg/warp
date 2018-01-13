package net.warpgame.engine.audio.decoder;

import org.lwjgl.openal.AL10;

import java.nio.*;

import static org.lwjgl.openal.AL10.alBufferData;

/**
 * Created by Marcin on 30.04.2017.
 */
public class SoundData {
    private ByteBuffer data;

    private int frequency;
    private int channels;
    private int bitrate;
    private int bitsPerChannel;

    public SoundData(ByteBuffer data,int frequency, int channels, int bitrate, int bitsPerChannel){
        this.data = data;
        this.frequency = frequency;
        this.channels = channels;
        this.bitrate = bitrate;
        this.bitsPerChannel = bitsPerChannel;
    }

    public int fillBufferWithData(int buffer){
        alBufferData(buffer, getOpenALFormat(), data, frequency);
        return buffer;
    }

    private int getOpenALFormat()
    {
        final int MONO = 1;
        final int STEREO = 2;
        int openALFormat = -1;
        switch (channels) {
            case MONO:
                switch (bitsPerChannel) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_MONO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_MONO16;
                        break;
                }
                break;
            case STEREO:
                switch (bitsPerChannel) {
                    case 8:
                        openALFormat = AL10.AL_FORMAT_STEREO8;
                        break;
                    case 16:
                        openALFormat = AL10.AL_FORMAT_STEREO16;
                        break;
                }
                break;
        }
        return openALFormat;
    }
}