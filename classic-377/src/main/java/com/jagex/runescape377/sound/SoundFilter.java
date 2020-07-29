package com.jagex.runescape377.sound;

import com.jagex.runescape377.net.Buffer;

public class SoundFilter
{

	public static float[][] _coefficient = new float[2][8];
	public static int[][] coefficient = new int[2][8];
	public static float _invUnity;
	public static int invUnity;
	public int[] numPairs = new int[2];
	public int[][][] pairPhase = new int[2][2][4];
	public int[][][] magnitude = new int[2][2][4];
	public int[] unity = new int[2];

	public float adaptMagnitude(int i, float f, int dir)
	{
		float alpha = magnitude[i][0][dir] + f
			* (magnitude[i][1][dir] - magnitude[i][0][dir]);
		alpha *= 0.001525879F;
		return 1.0F - (float) Math.pow(10D, -alpha / 20F);
	}

	public float normalize(float f)
	{
		float f1 = 32.7032F * (float) Math.pow(2D, f);
		return (f1 * 3.141593F) / 11025F;
	}

	public float adaptPhase(int i, int dir, float f)
	{
		float f1 = pairPhase[dir][0][i] + f
			* (pairPhase[dir][1][i] - pairPhase[dir][0][i]);
		f1 *= 0.0001220703F;
		return normalize(f1);
	}

	public int compute(int dir, float f)
	{
		if (dir == 0)
		{
			float f1 = unity[0] + (unity[1] - unity[0]) * f;
			f1 *= 0.003051758F;
			_invUnity = (float) Math.pow(0.10000000000000001D, f1 / 20F);
			invUnity = (int) (_invUnity * 65536F);
		}
		if (numPairs[dir] == 0)
		{
			return 0;
		}
		float f2 = adaptMagnitude(dir, f, 0);
		_coefficient[dir][0] = -2F * f2 * (float) Math.cos(adaptPhase(0, dir, f));
		_coefficient[dir][1] = f2 * f2;
		for (int term = 1; term < numPairs[dir]; term++)
		{
			float f3 = adaptMagnitude(dir, f, term);
			float f4 = -2F * f3 * (float) Math.cos(adaptPhase(term, dir, f));
			float f5 = f3 * f3;
			_coefficient[dir][term * 2 + 1] = _coefficient[dir][term * 2 - 1] * f5;
			_coefficient[dir][term * 2] = _coefficient[dir][term * 2 - 1] * f4 + _coefficient[dir][term * 2 - 2]
				* f5;
			for (int i = term * 2 - 1; i >= 2; i--)
			{
				_coefficient[dir][i] += _coefficient[dir][i - 1] * f4 + _coefficient[dir][i - 2] * f5;
			}

			_coefficient[dir][1] += _coefficient[dir][0] * f4 + f5;
			_coefficient[dir][0] += f4;
		}

		if (dir == 0)
		{
			for (int l = 0; l < numPairs[0] * 2; l++)
			{
				_coefficient[0][l] *= _invUnity;
			}

		}
		for (int term = 0; term < numPairs[dir] * 2; term++)
		{
			coefficient[dir][term] = (int) (_coefficient[dir][term] * 65536F);
		}

		return numPairs[dir] * 2;
	}

	public void decode(SoundTrackEnvelope soundTrackEnvelope, Buffer buffer)
	{
		int numPair = buffer.getUnsignedByte();
		numPairs[0] = numPair >> 4;
		numPairs[1] = numPair & 0xf;
		if (numPair != 0)
		{
			unity[0] = buffer.getUnsignedShortBE();
			unity[1] = buffer.getUnsignedShortBE();
			int migrated = buffer.getUnsignedByte();
			for (int dir = 0; dir < 2; dir++)
			{
				for (int term = 0; term < numPairs[dir]; term++)
				{
					pairPhase[dir][0][term] = buffer.getUnsignedShortBE();
					magnitude[dir][0][term] = buffer.getUnsignedShortBE();
				}

			}

			for (int dir = 0; dir < 2; dir++)
			{
				for (int term = 0; term < numPairs[dir]; term++)
				{
					if ((migrated & 1 << dir * 4 << term) != 0)
					{
						pairPhase[dir][1][term] = buffer.getUnsignedShortBE();
						magnitude[dir][1][term] = buffer.getUnsignedShortBE();
					}
					else
					{
						pairPhase[dir][1][term] = pairPhase[dir][0][term];
						magnitude[dir][1][term] = magnitude[dir][0][term];
					}
				}

			}

			if (migrated != 0 || unity[1] != unity[0])
			{
				soundTrackEnvelope.decodeShape(buffer);
			}
			return;
		}
		else
		{
			unity[0] = unity[1] = 0;
			return;
		}
	}


}
