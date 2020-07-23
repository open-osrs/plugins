package com.jagex.runescape377.sound;

import com.jagex.runescape377.net.Buffer;

public class SoundTrackEnvelope
{

	public int numPhases;
	public int phaseDuration[];
	public int phasePeak[];
	public int smart;
	public int end;
	public int form;
	public int critical;
	public int phaseIndex;
	public int step;
	public int amplitude;
	public int ticks;

	public void decode(Buffer buffer)
	{
		form = buffer.getUnsignedByte();
		smart = buffer.getIntBE();
		end = buffer.getIntBE();
		decodeShape(buffer);
	}

	public void decodeShape(Buffer buffer)
	{
		numPhases = buffer.getUnsignedByte();
		phaseDuration = new int[numPhases];
		phasePeak = new int[numPhases];
		for (int phase = 0; phase < numPhases; phase++)
		{
			phaseDuration[phase] = buffer.getUnsignedShortBE();
			phasePeak[phase] = buffer.getUnsignedShortBE();
		}

	}

	public void reset()
	{
		critical = 0;
		phaseIndex = 0;
		step = 0;
		amplitude = 0;
		ticks = 0;

	}

	public int step(int period)
	{
		if (ticks >= critical)
		{
			amplitude = phasePeak[phaseIndex++] << 15;
			if (phaseIndex >= numPhases)
			{
				phaseIndex = numPhases - 1;
			}
			critical = (int) ((phaseDuration[phaseIndex] / 65536D) * period);
			if (critical > ticks)
			{
				step = ((phasePeak[phaseIndex] << 15) - amplitude) / (critical - ticks);
			}
		}
		amplitude += step;
		ticks++;
		return amplitude - step >> 15;
	}
}
