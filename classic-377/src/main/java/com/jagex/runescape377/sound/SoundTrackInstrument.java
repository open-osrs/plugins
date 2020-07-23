package com.jagex.runescape377.sound;

import com.jagex.runescape377.net.Buffer;

public class SoundTrackInstrument
{
	public static int buffer[];
	public static int noise[];
	public static int sine[];
	public static int phases[] = new int[5];
	public static int delays[] = new int[5];
	public static int volumeStep[] = new int[5];
	public static int pitchStep[] = new int[5];
	public static int pitchBaseStep[] = new int[5];
	public SoundTrackEnvelope pitchEnvelope;
	public SoundTrackEnvelope volumeEnvelope;
	public SoundTrackEnvelope pitchModEnvelope;
	public SoundTrackEnvelope pitchModAmpEnvelope;
	public SoundTrackEnvelope volumeModEnvelope;
	public SoundTrackEnvelope volumeModAmpEnvelope;
	public SoundTrackEnvelope gatingReleaseEnvelope;
	public SoundTrackEnvelope gatingAttackEnvelope;
	public int oscillVolume[] = new int[5];
	public int oscillPitchDelta[] = new int[5];
	public int oscillDelay[] = new int[5];
	public int delayTime;
	public int delayFeedback = 100;
	public SoundFilter filter;
	public SoundTrackEnvelope filterEnvelope;
	public int soundMillis = 500;
	public int pauseMillis;

	public static void decode()
	{
		SoundTrackInstrument.noise = new int[32768];
		for (int noiseId = 0; noiseId < 32768; noiseId++)
		{
			if (Math.random() > 0.5D)
			{
				SoundTrackInstrument.noise[noiseId] = 1;
			}
			else
			{
				SoundTrackInstrument.noise[noiseId] = -1;
			}
		}

		SoundTrackInstrument.sine = new int[32768];
		for (int sineId = 0; sineId < 32768; sineId++)
		{
			SoundTrackInstrument.sine[sineId] = (int) (Math.sin(sineId / 5215.1903000000002D) * 16384D);
		}

		SoundTrackInstrument.buffer = new int[0x35d54];
	}

	public int[] synthesize(int nS, int dt)
	{
		for (int position = 0; position < nS; position++)
		{
			buffer[position] = 0;
		}

		if (dt < 10)
		{
			return buffer;
		}
		double fS = nS / (dt + 0.0D);
		pitchEnvelope.reset();
		volumeEnvelope.reset();
		int pitchModStep = 0;
		int pitchModBaseStep = 0;
		int pitchModPhase = 0;
		if (pitchModEnvelope != null)
		{
			pitchModEnvelope.reset();
			pitchModAmpEnvelope.reset();
			pitchModStep = (int) (((pitchModEnvelope.end - pitchModEnvelope.smart) * 32.768000000000001D) / fS);
			pitchModBaseStep = (int) ((pitchModEnvelope.smart * 32.768000000000001D) / fS);
		}
		int volumeModStep = 0;
		int volumeModBaseStep = 0;
		int volumeModPhase = 0;
		if (volumeModEnvelope != null)
		{
			volumeModEnvelope.reset();
			volumeModAmpEnvelope.reset();
			volumeModStep = (int) (((volumeModEnvelope.end - volumeModEnvelope.smart) * 32.768000000000001D) / fS);
			volumeModBaseStep = (int) ((volumeModEnvelope.smart * 32.768000000000001D) / fS);
		}
		for (int oscillVolumeId = 0; oscillVolumeId < 5; oscillVolumeId++)
		{
			if (oscillVolume[oscillVolumeId] != 0)
			{
				phases[oscillVolumeId] = 0;
				delays[oscillVolumeId] = (int) (oscillDelay[oscillVolumeId] * fS);
				volumeStep[oscillVolumeId] = (oscillVolume[oscillVolumeId] << 14) / 100;
				pitchStep[oscillVolumeId] = (int) (((pitchEnvelope.end - pitchEnvelope.smart) * 32.768000000000001D * Math
					.pow(1.0057929410678534D, oscillPitchDelta[oscillVolumeId])) / fS);
				pitchBaseStep[oscillVolumeId] = (int) ((pitchEnvelope.smart * 32.768000000000001D) / fS);
			}
		}

		for (int offset = 0; offset < nS; offset++)
		{
			int pitchChange = pitchEnvelope.step(nS);
			int volumeChange = volumeEnvelope.step(nS);
			if (pitchModEnvelope != null)
			{
				int mod = pitchModEnvelope.step(nS);
				int modAmp = pitchModAmpEnvelope.step(nS);
				pitchChange += evaluateWave(modAmp, pitchModPhase, pitchModEnvelope.form) >> 1;
				pitchModPhase += (mod * pitchModStep >> 16) + pitchModBaseStep;
			}
			if (volumeModEnvelope != null)
			{
				int mod = volumeModEnvelope.step(nS);
				int modAmp = volumeModAmpEnvelope.step(nS);
				volumeChange = volumeChange * ((evaluateWave(modAmp, volumeModPhase, volumeModEnvelope.form) >> 1) + 32768) >> 15;
				volumeModPhase += (mod * volumeModStep >> 16) + volumeModBaseStep;
			}
			for (int oscillVolumeId = 0; oscillVolumeId < 5; oscillVolumeId++)
			{
				if (oscillVolume[oscillVolumeId] != 0)
				{
					int position = offset + delays[oscillVolumeId];
					if (position < nS)
					{
						buffer[position] += evaluateWave(volumeChange
							* volumeStep[oscillVolumeId] >> 15, phases[oscillVolumeId], pitchEnvelope.form);
						phases[oscillVolumeId] += (pitchChange * pitchStep[oscillVolumeId] >> 16) + pitchBaseStep[oscillVolumeId];
					}
				}
			}

		}

		if (gatingReleaseEnvelope != null)
		{
			gatingReleaseEnvelope.reset();
			gatingAttackEnvelope.reset();
			int counter = 0;
			boolean muted = true;
			for (int position = 0; position < nS; position++)
			{
				int onStep = gatingReleaseEnvelope.step(nS);
				int offStep = gatingAttackEnvelope.step(nS);
				int threshold;
				if (muted)
				{
					threshold = gatingReleaseEnvelope.smart + ((gatingReleaseEnvelope.end - gatingReleaseEnvelope.smart) * onStep >> 8);
				}
				else
				{
					threshold = gatingReleaseEnvelope.smart + ((gatingReleaseEnvelope.end - gatingReleaseEnvelope.smart) * offStep >> 8);
				}
				if ((counter += 256) >= threshold)
				{
					counter = 0;
					muted = !muted;
				}
				if (muted)
				{
					buffer[position] = 0;
				}
			}

		}
		if (delayTime > 0 && delayFeedback > 0)
		{
			int delay = (int) (delayTime * fS);
			for (int position = delay; position < nS; position++)
			{
				buffer[position] += (buffer[position - delay] * delayFeedback) / 100;
			}

		}
		if (filter.numPairs[0] > 0 || filter.numPairs[1] > 0)
		{
			filterEnvelope.reset();
			int t = filterEnvelope.step(nS + 1);
			int M = filter.compute(0, t / 65536F);
			int N = filter.compute(1, t / 65536F);
			if (nS >= M + N)
			{
				int n = 0;
				int delay = N;
				if (delay > nS - M)
				{
					delay = nS - M;
				}
				for (; n < delay; n++)
				{
					int y = (int) ((long) buffer[n + M] * (long) SoundFilter.invUnity >> 16);
					for (int position = 0; position < M; position++)
					{
						y += (int) ((long) buffer[(n + M) - 1 - position]
							* (long) SoundFilter.coefficient[0][position] >> 16);
					}

					for (int position = 0; position < n; position++)
					{
						y -= (int) ((long) buffer[n - 1 - position] * (long) SoundFilter.coefficient[1][position] >> 16);
					}

					buffer[n] = y;
					t = filterEnvelope.step(nS + 1);
				}

				char offset = '\200';
				delay = offset;
				do
				{
					if (delay > nS - M)
					{
						delay = nS - M;
					}
					for (; n < delay; n++)
					{
						int y = (int) ((long) buffer[n + M] * (long) SoundFilter.invUnity >> 16);
						for (int position = 0; position < M; position++)
						{
							y += (int) ((long) buffer[(n + M) - 1 - position]
								* (long) SoundFilter.coefficient[0][position] >> 16);
						}

						for (int position = 0; position < N; position++)
						{
							y -= (int) ((long) buffer[n - 1 - position]
								* (long) SoundFilter.coefficient[1][position] >> 16);
						}

						buffer[n] = y;
						t = filterEnvelope.step(nS + 1);
					}

					if (n >= nS - M)
					{
						break;
					}
					M = filter.compute(0, t / 65536F);
					N = filter.compute(1, t / 65536F);
					delay += offset;
				} while (true);
				for (; n < nS; n++)
				{
					int y = 0;
					for (int position = (n + M) - nS; position < M; position++)
					{
						y += (int) ((long) buffer[(n + M) - 1 - position]
							* (long) SoundFilter.coefficient[0][position] >> 16);
					}

					for (int position = 0; position < N; position++)
					{
						y -= (int) ((long) buffer[n - 1 - position]
							* (long) SoundFilter.coefficient[1][position] >> 16);
					}

					buffer[n] = y;
					filterEnvelope.step(nS + 1);
				}

			}
		}
		for (int position = 0; position < nS; position++)
		{
			if (SoundTrackInstrument.buffer[position] < -32768)
			{
				SoundTrackInstrument.buffer[position] = -32768;
			}
			if (SoundTrackInstrument.buffer[position] > 32767)
			{
				SoundTrackInstrument.buffer[position] = 32767;
			}
		}

		return SoundTrackInstrument.buffer;
	}

	public int evaluateWave(int amplitude, int phase, int table)
	{
		if (table == 1) // square wave
		{
			if ((phase & 0x7fff) < 16384)
			{
				return amplitude;
			}
			else
			{
				return -amplitude;
			}
		}
		if (table == 2) // sine wave
		{
			return sine[phase & 0x7fff] * amplitude >> 14;
		}
		if (table == 3) // sawtooth wave
		{
			return ((phase & 0x7fff) * amplitude >> 14) - amplitude;
		}
		if (table == 4) // random noise
		{
			return noise[phase / 2607 & 0x7fff] * amplitude;
		}
		else
		{
			return 0;
		}
	}

	public void decode(Buffer buffer)
	{
		pitchEnvelope = new SoundTrackEnvelope();
		pitchEnvelope.decode(buffer);
		volumeEnvelope = new SoundTrackEnvelope();
		volumeEnvelope.decode(buffer);
		int option = buffer.getUnsignedByte();
		if (option != 0)
		{
			buffer.currentPosition--;
			pitchModEnvelope = new SoundTrackEnvelope();
			pitchModEnvelope.decode(buffer);
			pitchModAmpEnvelope = new SoundTrackEnvelope();
			pitchModAmpEnvelope.decode(buffer);
		}
		option = buffer.getUnsignedByte();
		if (option != 0)
		{
			buffer.currentPosition--;
			volumeModEnvelope = new SoundTrackEnvelope();
			volumeModEnvelope.decode(buffer);
			volumeModAmpEnvelope = new SoundTrackEnvelope();
			volumeModAmpEnvelope.decode(buffer);
		}
		option = buffer.getUnsignedByte();
		if (option != 0)
		{
			buffer.currentPosition--;
			gatingReleaseEnvelope = new SoundTrackEnvelope();
			gatingReleaseEnvelope.decode(buffer);
			gatingAttackEnvelope = new SoundTrackEnvelope();
			gatingAttackEnvelope.decode(buffer);
		}
		for (int oscillId = 0; oscillId < 10; oscillId++)
		{
			int volume = buffer.getSmart();
			if (volume == 0)
			{
				break;
			}
			oscillVolume[oscillId] = volume;
			oscillPitchDelta[oscillId] = buffer.getSignedSmart();
			oscillDelay[oscillId] = buffer.getSmart();
		}

		delayTime = buffer.getSmart();
		delayFeedback = buffer.getSmart();
		soundMillis = buffer.getUnsignedShortBE();
		pauseMillis = buffer.getUnsignedShortBE();
		filter = new SoundFilter();
		filterEnvelope = new SoundTrackEnvelope();
		filter.decode(filterEnvelope, buffer);
	}
}
