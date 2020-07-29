package com.jagex.runescape377.net;

public class ISAACCipher
{


	/**
	 * The golden ratio.
	 */
	private static final int GOLDEN_RATIO = 0x9e3779b9;

	/**
	 * The log of the size of the result and memory arrays.
	 */
	private static final int SIZEL = 8;

	/**
	 * The size of the result and memory arrays.
	 */
	private static final int SIZE = 1 << ISAACCipher.SIZEL;

	/**
	 * A mask for pseudorandom lookup.
	 */
	private static int MASK = (ISAACCipher.SIZE - 1) << 2;
	/**
	 * The results given to the user.
	 */
	private final int[] rsl;
	/**
	 * The internal state.
	 */
	private final int[] mem;
	/**
	 * The count through the results in the results array.
	 */
	private int count;
	/**
	 * The accumulator.
	 */
	private int a;

	/**
	 * The last result.
	 */
	private int b;

	/**
	 * The counter.
	 */
	private int c;

	/**
	 * Creates the random number generator without an initial seed.
	 */
	public ISAACCipher()
	{
		mem = new int[ISAACCipher.SIZE];
		rsl = new int[ISAACCipher.SIZE];
		init(false);
	}

	/**
	 * Creates the random number generator with the specified seed.
	 *
	 * @param seed The seed.
	 */
	public ISAACCipher(int[] seed)
	{
		mem = new int[ISAACCipher.SIZE];
		rsl = new int[ISAACCipher.SIZE];
		for (int i = 0; i < seed.length; ++i)
		{
			rsl[i] = seed[i];
		}
		init(true);
	}

	/**
	 * Generates 256 results.
	 */
	private void isaac()
	{
		int i, j, x, y;

		b += ++c;
		for (i = 0, j = ISAACCipher.SIZE / 2; i < ISAACCipher.SIZE / 2; )
		{
			x = mem[i];
			a ^= a << 13;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;

			x = mem[i];
			a ^= a >>> 6;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;

			x = mem[i];
			a ^= a << 2;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;

			x = mem[i];
			a ^= a >>> 16;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;
		}

		for (j = 0; j < ISAACCipher.SIZE / 2; )
		{
			x = mem[i];
			a ^= a << 13;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;

			x = mem[i];
			a ^= a >>> 6;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;

			x = mem[i];
			a ^= a << 2;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;

			x = mem[i];
			a ^= a >>> 16;
			a += mem[j++];
			mem[i] = y = mem[(x & ISAACCipher.MASK) >> 2] + a + b;
			rsl[i++] = b = mem[((y >> ISAACCipher.SIZEL) & ISAACCipher.MASK) >> 2] + x;
		}
	}

	/**
	 * Initialises this random number generator.
	 *
	 * @param flag Set to {@code true} if a seed was passed to the constructor.
	 */
	private void init(boolean flag)
	{
		int i;
		int a, b, c, d, e, f, g, h;
		a = b = c = d = e = f = g = h = ISAACCipher.GOLDEN_RATIO;

		for (i = 0; i < 4; ++i)
		{
			a ^= b << 11;
			d += a;
			b += c;
			b ^= c >>> 2;
			e += b;
			c += d;
			c ^= d << 8;
			f += c;
			d += e;
			d ^= e >>> 16;
			g += d;
			e += f;
			e ^= f << 10;
			h += e;
			f += g;
			f ^= g >>> 4;
			a += f;
			g += h;
			g ^= h << 8;
			b += g;
			h += a;
			h ^= a >>> 9;
			c += h;
			a += b;
		}

		for (i = 0; i < ISAACCipher.SIZE; i += 8)
		{ /*
		 * fill in mem[] with messy
		 * stuff
		 */
			if (flag)
			{
				a += rsl[i];
				b += rsl[i + 1];
				c += rsl[i + 2];
				d += rsl[i + 3];
				e += rsl[i + 4];
				f += rsl[i + 5];
				g += rsl[i + 6];
				h += rsl[i + 7];
			}
			a ^= b << 11;
			d += a;
			b += c;
			b ^= c >>> 2;
			e += b;
			c += d;
			c ^= d << 8;
			f += c;
			d += e;
			d ^= e >>> 16;
			g += d;
			e += f;
			e ^= f << 10;
			h += e;
			f += g;
			f ^= g >>> 4;
			a += f;
			g += h;
			g ^= h << 8;
			b += g;
			h += a;
			h ^= a >>> 9;
			c += h;
			a += b;
			mem[i] = a;
			mem[i + 1] = b;
			mem[i + 2] = c;
			mem[i + 3] = d;
			mem[i + 4] = e;
			mem[i + 5] = f;
			mem[i + 6] = g;
			mem[i + 7] = h;
		}

		if (flag)
		{ /* second pass makes all of seed affect all of mem */
			for (i = 0; i < ISAACCipher.SIZE; i += 8)
			{
				a += mem[i];
				b += mem[i + 1];
				c += mem[i + 2];
				d += mem[i + 3];
				e += mem[i + 4];
				f += mem[i + 5];
				g += mem[i + 6];
				h += mem[i + 7];
				a ^= b << 11;
				d += a;
				b += c;
				b ^= c >>> 2;
				e += b;
				c += d;
				c ^= d << 8;
				f += c;
				d += e;
				d ^= e >>> 16;
				g += d;
				e += f;
				e ^= f << 10;
				h += e;
				f += g;
				f ^= g >>> 4;
				a += f;
				g += h;
				g ^= h << 8;
				b += g;
				h += a;
				h ^= a >>> 9;
				c += h;
				a += b;
				mem[i] = a;
				mem[i + 1] = b;
				mem[i + 2] = c;
				mem[i + 3] = d;
				mem[i + 4] = e;
				mem[i + 5] = f;
				mem[i + 6] = g;
				mem[i + 7] = h;
			}
		}

		isaac();
		count = ISAACCipher.SIZE;
	}

	/**
	 * Gets the next random value.
	 *
	 * @return The next random value.
	 */
	public int nextInt()
	{
		if (0 == count--)
		{
			isaac();
			count = ISAACCipher.SIZE - 1;
		}
		return rsl[count];
	}

}