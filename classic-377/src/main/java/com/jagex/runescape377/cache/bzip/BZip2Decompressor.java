package com.jagex.runescape377.cache.bzip;

public class BZip2Decompressor
{

	public static Bzip2Context state = new Bzip2Context();

	public static int decompress(byte[] output, int lenght, byte[] compressed, int decompressedLength, int minLen)
	{
		synchronized (state)
		{
			state.compressed = compressed;
			state.nextIn = minLen;
			state.buf = output;
			state.nextOut = 0;
			state.decompressedLength = decompressedLength;
			state.availOut = lenght;
			state.bsLive = 0;
			state.bsBuff = 0;
			state.totalInLo32 = 0;
			state.totalInHi32 = 0;
			state.totalOutLo32 = 0;
			state.totalOutHigh32 = 0;
			state.currentBlock = 0;
			decompress(state);
			lenght -= state.availOut;
			return lenght;
		}
	}

	public static void method313(Bzip2Context bzip2Context)
	{
		byte stateOutCh = bzip2Context.stateOutCh;
		int stateOutLen = bzip2Context.stateOutLen;
		int nBlockUsed = bzip2Context.nBlockUsed;
		int k0 = bzip2Context.k0;
		int[] tt = Bzip2Context.tt;
		int tPos = bzip2Context.tPos;
		byte[] buf = bzip2Context.buf;
		int csNextOut = bzip2Context.nextOut;
		int csAvailOut = bzip2Context.availOut;
		int availOutInit = csAvailOut;
		int savedNBlockPP = bzip2Context.nBlock + 1;
		outer:
		do
		{
			if (stateOutLen > 0)
			{
				do
				{
					if (csAvailOut == 0)
					{
						break outer;
					}
					if (stateOutLen == 1)
					{
						break;
					}
					buf[csNextOut] = stateOutCh;
					stateOutLen--;
					csNextOut++;
					csAvailOut--;
				} while (true);
				if (csAvailOut == 0)
				{
					stateOutLen = 1;
					break;
				}
				buf[csNextOut] = stateOutCh;
				csNextOut++;
				csAvailOut--;
			}
			boolean flag = true;
			while (flag)
			{
				flag = false;
				if (nBlockUsed == savedNBlockPP)
				{
					stateOutLen = 0;
					break outer;
				}
				stateOutCh = (byte) k0;
				tPos = tt[tPos];
				byte k1 = (byte) (tPos & 0xff);
				tPos >>= 8;
				nBlockUsed++;
				if (k1 != k0)
				{
					k0 = k1;
					if (csAvailOut == 0)
					{
						stateOutLen = 1;
					}
					else
					{
						buf[csNextOut] = stateOutCh;
						csNextOut++;
						csAvailOut--;
						flag = true;
						continue;
					}
					break outer;
				}
				if (nBlockUsed != savedNBlockPP)
				{
					continue;
				}
				if (csAvailOut == 0)
				{
					stateOutLen = 1;
					break outer;
				}
				buf[csNextOut] = stateOutCh;
				csNextOut++;
				csAvailOut--;
				flag = true;
			}
			stateOutLen = 2;
			tPos = tt[tPos];
			byte k1 = (byte) (tPos & 0xff);
			tPos >>= 8;
			if (++nBlockUsed != savedNBlockPP)
			{
				if (k1 != k0)
				{
					k0 = k1;
				}
				else
				{
					stateOutLen = 3;
					tPos = tt[tPos];
					byte k1_ = (byte) (tPos & 0xff);
					tPos >>= 8;
					if (++nBlockUsed != savedNBlockPP)
					{
						if (k1_ != k0)
						{
							k0 = k1_;
						}
						else
						{
							tPos = tt[tPos];
							byte byte3 = (byte) (tPos & 0xff);
							tPos >>= 8;
							nBlockUsed++;
							stateOutLen = (byte3 & 0xff) + 4;
							tPos = tt[tPos];
							k0 = (byte) (tPos & 0xff);
							tPos >>= 8;
							nBlockUsed++;
						}
					}
				}
			}
		} while (true);
		int oldTotalOutLo32 = bzip2Context.totalOutLo32;
		bzip2Context.totalOutLo32 += availOutInit - csAvailOut;
		if (bzip2Context.totalOutLo32 < oldTotalOutLo32)
		{
			bzip2Context.totalOutHigh32++;
		}
		bzip2Context.stateOutCh = stateOutCh;
		bzip2Context.stateOutLen = stateOutLen;
		bzip2Context.nBlockUsed = nBlockUsed;
		bzip2Context.k0 = k0;
		Bzip2Context.tt = tt;
		bzip2Context.tPos = tPos;
		bzip2Context.buf = buf;
		bzip2Context.nextOut = csNextOut;
		bzip2Context.availOut = csAvailOut;
	}

	public static void decompress(Bzip2Context bzip2Context)
	{
		int gMinLen = 0;
		int[] gLimit = null;
		int[] gBase = null;
		int[] gPerm = null;
		bzip2Context.blockSize100k = 1;
		if (Bzip2Context.tt == null)
		{
			Bzip2Context.tt = new int[bzip2Context.blockSize100k * 0x186a0];
		}
		boolean flag19 = true;
		while (flag19)
		{
			byte uc = getUChar(bzip2Context);
			if (uc == 23)
			{
				return;
			}
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			bzip2Context.currentBlock++;
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			uc = getUChar(bzip2Context);
			uc = getBit(bzip2Context);
			bzip2Context.blockRandomised = uc != 0;
			if (bzip2Context.blockRandomised)
			{
				System.out.println("PANIC! RANDOMISED BLOCK!");
			}
			bzip2Context.origPtr = 0;
			uc = getUChar(bzip2Context);
			bzip2Context.origPtr = bzip2Context.origPtr << 8 | uc & 0xff;
			uc = getUChar(bzip2Context);
			bzip2Context.origPtr = bzip2Context.origPtr << 8 | uc & 0xff;
			uc = getUChar(bzip2Context);
			bzip2Context.origPtr = bzip2Context.origPtr << 8 | uc & 0xff;
			for (int i = 0; i < 16; i++)
			{
				byte bit = getBit(bzip2Context);
				bzip2Context.inUse16[i] = bit == 1;
			}

			for (int i = 0; i < 256; i++)
			{
				bzip2Context.inUse[i] = false;
			}

			for (int i = 0; i < 16; i++)
			{
				if (bzip2Context.inUse16[i])
				{
					for (int j = 0; j < 16; j++)
					{
						byte byte2 = getBit(bzip2Context);
						if (byte2 == 1)
						{
							bzip2Context.inUse[i * 16 + j] = true;
						}
					}

				}
			}

			makeMaps(bzip2Context);
			int alphaSize = bzip2Context.nInUse + 2;
			int nGroups = getBits(3, bzip2Context);
			int nSelectors = getBits(15, bzip2Context);
			for (int i = 0; i < nSelectors; i++)
			{
				int count = 0;
				do
				{
					byte terminator = getBit(bzip2Context);
					if (terminator == 0)
					{
						break;
					}
					count++;
				} while (true);
				bzip2Context.selectorMtf[i] = (byte) count;
			}

			byte[] pos = new byte[6];
			for (byte v = 0; v < nGroups; v++)
			{
				pos[v] = v;
			}

			for (int i = 0; i < nSelectors; i++)
			{
				byte v = bzip2Context.selectorMtf[i];
				byte temp = pos[v];
				for (; v > 0; v--)
				{
					pos[v] = pos[v - 1];
				}

				pos[0] = temp;
				bzip2Context.selector[i] = temp;
			}

			for (int t = 0; t < nGroups; t++)
			{
				int curr = getBits(5, bzip2Context);
				for (int i = 0; i < alphaSize; i++)
				{
					do
					{
						byte bit = getBit(bzip2Context);
						if (bit == 0)
						{
							break;
						}
						bit = getBit(bzip2Context);
						if (bit == 0)
						{
							curr++;
						}
						else
						{
							curr--;
						}
					} while (true);
					bzip2Context.len[t][i] = (byte) curr;
				}

			}

			for (int t = 0; t < nGroups; t++)
			{
				byte minLen = 32;
				int maxLen = 0;
				for (int i = 0; i < alphaSize; i++)
				{
					if (bzip2Context.len[t][i] > maxLen)
					{
						maxLen = bzip2Context.len[t][i];
					}
					if (bzip2Context.len[t][i] < minLen)
					{
						minLen = bzip2Context.len[t][i];
					}
				}

				createDecodeTables(bzip2Context.limit[t], bzip2Context.base[t], bzip2Context.perm[t],
					bzip2Context.len[t], minLen, maxLen, alphaSize);
				bzip2Context.minLens[t] = minLen;
			}

			int eob = bzip2Context.nInUse + 1;
			//int l5 = 0x186a0 * class1.blockSize100k;
			int groupNo = -1;
			int groupPos = 0;
			for (int i = 0; i <= 255; i++)
			{
				bzip2Context.unzftab[i] = 0;
			}

			int kk = 4095;
			for (int ii = 15; ii >= 0; ii--)
			{
				for (int jj = 15; jj >= 0; jj--)
				{
					bzip2Context.mtfa[kk] = (byte) (ii * 16 + jj);
					kk--;
				}

				bzip2Context.mtfbase[ii] = kk + 1;
			}

			int nblock = 0;
			if (groupPos == 0)
			{
				groupNo++;
				groupPos = 50;
				byte gSel = bzip2Context.selector[groupNo];
				gMinLen = bzip2Context.minLens[gSel];
				gLimit = bzip2Context.limit[gSel];
				gPerm = bzip2Context.perm[gSel];
				gBase = bzip2Context.base[gSel];
			}
			groupPos--;
			int zn = gMinLen;
			int zvec;
			byte zj;
			for (zvec = getBits(zn, bzip2Context); zvec > gLimit[zn]; zvec = zvec << 1 | zj)
			{
				zn++;
				zj = getBit(bzip2Context);
			}

			for (int nextSym = gPerm[zvec - gBase[zn]]; nextSym != eob; )
			{
				if (nextSym == 0 || nextSym == 1)
				{
					int es = -1;
					int n = 1;
					do
					{
						if (nextSym == 0)
						{
							es += n;
						}
						else if (nextSym == 1)
						{
							es += 2 * n;
						}
						n *= 2;
						if (groupPos == 0)
						{
							groupNo++;
							groupPos = 50;
							byte gSel = bzip2Context.selector[groupNo];
							gMinLen = bzip2Context.minLens[gSel];
							gLimit = bzip2Context.limit[gSel];
							gPerm = bzip2Context.perm[gSel];
							gBase = bzip2Context.base[gSel];
						}
						groupPos--;
						int zn_ = gMinLen;
						int zvec_;
						byte byte10;
						for (zvec_ = getBits(zn_, bzip2Context); zvec_ > gLimit[zn_]; zvec_ = zvec_ << 1 | byte10)
						{
							zn_++;
							byte10 = getBit(bzip2Context);
						}

						nextSym = gPerm[zvec_ - gBase[zn_]];
					} while (nextSym == 0 || nextSym == 1);
					es++;
					byte ec = bzip2Context.seqToUnseq[bzip2Context.mtfa[bzip2Context.mtfbase[0]] & 0xff];
					bzip2Context.unzftab[ec & 0xff] += es;
					for (; es > 0; es--)
					{
						Bzip2Context.tt[nblock] = ec & 0xff;
						nblock++;
					}

				}
				else
				{
					int nn = nextSym - 1;
					byte uc_;
					if (nn < 16)
					{
						int j10 = bzip2Context.mtfbase[0];
						uc_ = bzip2Context.mtfa[j10 + nn];
						for (; nn > 3; nn -= 4)
						{
							int k11 = j10 + nn;
							bzip2Context.mtfa[k11] = bzip2Context.mtfa[k11 - 1];
							bzip2Context.mtfa[k11 - 1] = bzip2Context.mtfa[k11 - 2];
							bzip2Context.mtfa[k11 - 2] = bzip2Context.mtfa[k11 - 3];
							bzip2Context.mtfa[k11 - 3] = bzip2Context.mtfa[k11 - 4];
						}

						for (; nn > 0; nn--)
						{
							bzip2Context.mtfa[j10 + nn] = bzip2Context.mtfa[(j10 + nn) - 1];
						}

						bzip2Context.mtfa[j10] = uc_;
					}
					else
					{
						int l10 = nn / 16;
						int i11 = nn % 16;
						int k10 = bzip2Context.mtfbase[l10] + i11;
						uc_ = bzip2Context.mtfa[k10];
						for (; k10 > bzip2Context.mtfbase[l10]; k10--)
						{
							bzip2Context.mtfa[k10] = bzip2Context.mtfa[k10 - 1];
						}

						bzip2Context.mtfbase[l10]++;
						for (; l10 > 0; l10--)
						{
							bzip2Context.mtfbase[l10]--;
							bzip2Context.mtfa[bzip2Context.mtfbase[l10]] = bzip2Context.mtfa[(bzip2Context.mtfbase[l10 - 1] + 16) - 1];
						}

						bzip2Context.mtfbase[0]--;
						bzip2Context.mtfa[bzip2Context.mtfbase[0]] = uc_;
						if (bzip2Context.mtfbase[0] == 0)
						{
							int i10 = 4095;
							for (int k9 = 15; k9 >= 0; k9--)
							{
								for (int l9 = 15; l9 >= 0; l9--)
								{
									bzip2Context.mtfa[i10] = bzip2Context.mtfa[bzip2Context.mtfbase[k9] + l9];
									i10--;
								}

								bzip2Context.mtfbase[k9] = i10 + 1;
							}

						}
					}
					bzip2Context.unzftab[bzip2Context.seqToUnseq[uc_ & 0xff] & 0xff]++;
					Bzip2Context.tt[nblock] = bzip2Context.seqToUnseq[uc_ & 0xff] & 0xff;
					nblock++;
					if (groupPos == 0)
					{
						groupNo++;
						groupPos = 50;
						byte byte14 = bzip2Context.selector[groupNo];
						gMinLen = bzip2Context.minLens[byte14];
						gLimit = bzip2Context.limit[byte14];
						gPerm = bzip2Context.perm[byte14];
						gBase = bzip2Context.base[byte14];
					}
					groupPos--;
					int k7 = gMinLen;
					int j8;
					byte byte11;
					for (j8 = getBits(k7, bzip2Context); j8 > gLimit[k7]; j8 = j8 << 1 | byte11)
					{
						k7++;
						byte11 = getBit(bzip2Context);
					}

					nextSym = gPerm[j8 - gBase[k7]];
				}
			}

			bzip2Context.stateOutLen = 0;
			bzip2Context.stateOutCh = 0;
			bzip2Context.cftab[0] = 0;
			System.arraycopy(bzip2Context.unzftab, 0, bzip2Context.cftab, 1, 256);

			for (int k2 = 1; k2 <= 256; k2++)
			{
				bzip2Context.cftab[k2] += bzip2Context.cftab[k2 - 1];
			}

			for (int l2 = 0; l2 < nblock; l2++)
			{
				byte byte7 = (byte) (Bzip2Context.tt[l2] & 0xff);
				Bzip2Context.tt[bzip2Context.cftab[byte7 & 0xff]] |= l2 << 8;
				bzip2Context.cftab[byte7 & 0xff]++;
			}

			bzip2Context.tPos = Bzip2Context.tt[bzip2Context.origPtr] >> 8;
			bzip2Context.nBlockUsed = 0;
			bzip2Context.tPos = Bzip2Context.tt[bzip2Context.tPos];
			bzip2Context.k0 = (byte) (bzip2Context.tPos & 0xff);
			bzip2Context.tPos >>= 8;
			bzip2Context.nBlockUsed++;
			bzip2Context.nBlock = nblock;
			method313(bzip2Context);
			flag19 = bzip2Context.nBlockUsed == bzip2Context.nBlock + 1 && bzip2Context.stateOutLen == 0;
		}
	}

	public static byte getUChar(Bzip2Context bzip2Context)
	{
		return (byte) getBits(8, bzip2Context);
	}

	public static byte getBit(Bzip2Context bzip2Context)
	{
		return (byte) getBits(1, bzip2Context);
	}

	public static int getBits(int numBits, Bzip2Context bzip2Context)
	{
		int bits;
		do
		{
			if (bzip2Context.bsLive >= numBits)
			{
				int k = bzip2Context.bsBuff >> bzip2Context.bsLive - numBits & (1 << numBits) - 1;
				bzip2Context.bsLive -= numBits;
				bits = k;
				break;
			}
			bzip2Context.bsBuff = bzip2Context.bsBuff << 8 | bzip2Context.compressed[bzip2Context.nextIn] & 0xff;
			bzip2Context.bsLive += 8;
			bzip2Context.nextIn++;
			bzip2Context.decompressedLength--;
			bzip2Context.totalInLo32++;
			if (bzip2Context.totalInLo32 == 0)
			{
				bzip2Context.totalInHi32++;
			}
		} while (true);
		return bits;
	}

	public static void makeMaps(Bzip2Context bzip2Context)
	{
		bzip2Context.nInUse = 0;
		for (int i = 0; i < 256; i++)
		{
			if (bzip2Context.inUse[i])
			{
				bzip2Context.seqToUnseq[bzip2Context.nInUse] = (byte) i;
				bzip2Context.nInUse++;
			}
		}

	}

	public static void createDecodeTables(int[] limit, int[] base, int[] ai2, byte[] len, int minLen, int maxLen, int alphaSize)
	{
		int pp = 0;
		for (int i = minLen; i <= maxLen; i++)
		{
			for (int j = 0; j < alphaSize; j++)
			{
				if (len[j] == i)
				{
					ai2[pp] = j;
					pp++;
				}
			}

		}

		for (int i = 0; i < 23; i++)
		{
			base[i] = 0;
		}

		for (int i = 0; i < alphaSize; i++)
		{
			base[len[i] + 1]++;
		}

		for (int i = 1; i < 23; i++)
		{
			base[i] += base[i - 1];
		}

		for (int i = 0; i < 23; i++)
		{
			limit[i] = 0;
		}

		int vec = 0;
		for (int i = minLen; i <= maxLen; i++)
		{
			vec += base[i + 1] - base[i];
			limit[i] = vec - 1;
			vec <<= 1;
		}

		for (int i = minLen + 1; i <= maxLen; i++)
		{
			base[i] = (limit[i - 1] + 1 << 1) - base[i];
		}

	}


}
