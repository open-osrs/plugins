package com.jagex.runescape377.sound;

import com.jagex.runescape377.net.Buffer;

public class SoundTrack
{

	public static byte aByte664 = 6;
	public static SoundTrack tracks[] = new SoundTrack[5000];
	public static int trackDelays[] = new int[5000];
	public static byte _buffer[];
	public static Buffer buffer;
	public SoundTrackInstrument instruments[] = new SoundTrackInstrument[10];
	public int loopBegin;
	public int loopEnd;

	public SoundTrack(int i)
	{
		while (i >= 0)
		{
			throw new NullPointerException();
		}

	}

	public static void load(Buffer buffer)
	{
		SoundTrack._buffer = new byte[0x6baa8];
		SoundTrack.buffer = new Buffer(SoundTrack._buffer);
		SoundTrackInstrument.decode();
		while (true)
		{
			int trackId = buffer.getUnsignedShortBE();
			if (trackId == 65535)
			{
				return;
			}
			SoundTrack.tracks[trackId] = new SoundTrack(-524);
			SoundTrack.tracks[trackId].decode(buffer);
			SoundTrack.trackDelays[trackId] = SoundTrack.tracks[trackId].delay();
		}
	}

	public static Buffer data(int trackId, int loops)
	{
		if (SoundTrack.tracks[trackId] != null)
		{
			SoundTrack soundTrack = SoundTrack.tracks[trackId];
			return soundTrack.encode(loops);
		}
		else
		{
			return null;
		}
	}

	public void decode(Buffer buffer)
	{
		for (int instrument = 0; instrument < 10; instrument++)
		{
			int active = buffer.getUnsignedByte();
			if (active != 0)
			{
				buffer.currentPosition--;
				instruments[instrument] = new SoundTrackInstrument();
				instruments[instrument].decode(buffer);
			}
		}

		loopBegin = buffer.getUnsignedShortBE();
		loopEnd = buffer.getUnsignedShortBE();
	}

	public int delay()
	{
		int delay = 0x98967f;
		for (int instrument = 0; instrument < 10; instrument++)
		{
			if (instruments[instrument] != null && instruments[instrument].pauseMillis / 20 < delay)
			{
				delay = instruments[instrument].pauseMillis / 20;
			}
		}

		if (loopBegin < loopEnd && loopBegin / 20 < delay)
		{
			delay = loopBegin / 20;
		}
		if (delay == 0x98967f || delay == 0)
		{
			return 0;
		}
		for (int instrument = 0; instrument < 10; instrument++)
		{
			if (instruments[instrument] != null)
			{
				instruments[instrument].pauseMillis -= delay * 20;
			}
		}

		if (loopBegin < loopEnd)
		{
			loopBegin -= delay * 20;
			loopEnd -= delay * 20;
		}
		return delay;
	}

	public Buffer encode(int j)
	{
		int size = mix(j);
		SoundTrack.buffer.currentPosition = 0;
		SoundTrack.buffer.putIntBE(0x52494646);    // "RIFF"
		SoundTrack.buffer.putIntLE(36 + size); // chunk length
		SoundTrack.buffer.putIntBE(0x57415645);    // "WAVE" (format)
		SoundTrack.buffer.putIntBE(0x666d7420);    // "FMT " (subchunk id)
		SoundTrack.buffer.putIntLE(16);       // subchunk size
		SoundTrack.buffer.putShortLECopy(1);      // PCM
		SoundTrack.buffer.putShortLECopy(1);      // channels (mono)
		SoundTrack.buffer.putIntLE(22050);    // sample rate
		SoundTrack.buffer.putIntLE(22050);    // byte rate
		SoundTrack.buffer.putShortLECopy(1);      // block alignment
		SoundTrack.buffer.putShortLECopy(8);      // bits per sample
		SoundTrack.buffer.putIntBE(0x64617461); // "DATA" (subchunk id)
		SoundTrack.buffer.putIntLE(size);   // length
		SoundTrack.buffer.currentPosition += size;
		return SoundTrack.buffer;
	}

	public int mix(int loops)
	{
		int millis = 0;
		for (int instrument = 0; instrument < 10; instrument++)
		{
			if (instruments[instrument] != null && instruments[instrument].soundMillis + instruments[instrument].pauseMillis > millis)
			{
				millis = instruments[instrument].soundMillis + instruments[instrument].pauseMillis;
			}
		}

		if (millis == 0)
		{
			return 0;
		}
		int nS = (22050 * millis) / 1000;
		int loopBegin = (22050 * this.loopBegin) / 1000;
		int loopEnd = (22050 * this.loopEnd) / 1000;
		if (loopBegin < 0 || loopBegin > nS || loopEnd < 0 || loopEnd > nS || loopBegin >= loopEnd)
		{
			loops = 0;
		}
		int length = nS + (loopEnd - loopBegin) * (loops - 1);
		for (int position = 44; position < length + 44; position++)
		{
			SoundTrack._buffer[position] = -128;
		}

		for (int instrument = 0; instrument < 10; instrument++)
		{
			if (instruments[instrument] != null)
			{
				int soundSamples = (instruments[instrument].soundMillis * 22050) / 1000;
				int pauseSamples = (instruments[instrument].pauseMillis * 22050) / 1000;
				int samples[] = instruments[instrument].synthesize(soundSamples, instruments[instrument].soundMillis);
				for (int soundSample = 0; soundSample < soundSamples; soundSample++)
				{
					int sample = (SoundTrack._buffer[soundSample + pauseSamples + 44] & 0xff) + (samples[soundSample] >> 8);
					if ((sample & 0xffffff00) != 0)
					{
						sample = ~(sample >> 31);
					}
					SoundTrack._buffer[soundSample + pauseSamples + 44] = (byte) sample;
				}

			}
		}

		if (loops > 1)
		{
			loopBegin += 44;
			loopEnd += 44;
			nS += 44;
			int offset = (length += 44) - nS;
			for (int position = nS - 1; position >= loopEnd; position--)
			{
				SoundTrack._buffer[position + offset] = SoundTrack._buffer[position];
			}

			for (int loopCounter = 1; loopCounter < loops; loopCounter++)
			{
				offset = (loopEnd - loopBegin) * loopCounter;
				for (int position = loopBegin; position < loopEnd; position++)
				{
					SoundTrack._buffer[position + offset] = SoundTrack._buffer[position];
				}

			}

			length -= 44;
		}
		return length;
	}


}
