package com.jagex.runescape377.cache.bzip;

public class Bzip2Context
{
	public static int tt[];
	public byte compressed[];
	public int nextIn;
	public int decompressedLength;
	public int totalInLo32;
	public int totalInHi32;
	public byte buf[];
	public int nextOut;
	public int availOut;
	public int totalOutLo32;
	public int totalOutHigh32;
	public byte stateOutCh;
	public int stateOutLen;
	public boolean blockRandomised;
	public int bsBuff;
	public int bsLive;
	public int blockSize100k;
	public int currentBlock;
	public int origPtr;
	public int tPos;
	public int k0;
	public int unzftab[] = new int[256];
	public int nBlockUsed;
	public int cftab[] = new int[257];
	public int nInUse;
	public boolean inUse[] = new boolean[256];
	public boolean inUse16[] = new boolean[16];
	public byte seqToUnseq[] = new byte[256];
	public byte mtfa[] = new byte[4096];
	public int mtfbase[] = new int[16];
	public byte selector[] = new byte[18002];
	public byte selectorMtf[] = new byte[18002];
	public byte len[][] = new byte[6][258];
	public int limit[][] = new int[6][258];
	public int base[][] = new int[6][258];
	public int perm[][] = new int[6][258];
	public int minLens[] = new int[6];
	public int nBlock;
}
