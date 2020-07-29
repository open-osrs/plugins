package com.jagex.runescape377.cache.cfg;

import com.jagex.runescape377.cache.Archive;
import com.jagex.runescape377.net.Buffer;

public class ChatCensor
{


	private static final String[] exceptions = {"cook", "cook's", "cooks", "seeks", "sheet", "woop", "woops",
		"faq", "noob", "noobs"};
	private static int[] fragments;
	private static char[][] badWords;
	private static byte[][][] badBytes;
	private static char[][] domains;
	private static char[][] topLevelDomains;
	private static int[] topLevelDomainsType;

	public static void load(Archive archive)
	{
		Buffer fragmentsEnc = new Buffer(archive.getFile("fragmentsenc.txt"));
		Buffer badEnc = new Buffer(archive.getFile("badenc.txt"));
		Buffer domainEnc = new Buffer(archive.getFile("domainenc.txt"));
		Buffer topLevelDomainsBuffer = new Buffer(archive.getFile("tldlist.txt"));
		loadDictionaries(fragmentsEnc, badEnc, domainEnc, topLevelDomainsBuffer);
	}

	private static void loadDictionaries(Buffer fragmentsEnc, Buffer badEnc,
											Buffer domainEnc, Buffer topLevelDomainsBuffer)
	{
		loadBadEnc(badEnc);
		loadDomainEnc(domainEnc);
		loadFragmentsEnc(fragmentsEnc);
		loadTopLevelDomains(topLevelDomainsBuffer);
	}

	private static void loadTopLevelDomains(Buffer buffer)
	{
		int length = buffer.getIntBE();
		topLevelDomains = new char[length][];
		topLevelDomainsType = new int[length];
		for (int index = 0; index < length; index++)
		{
			topLevelDomainsType[index] = buffer.getUnsignedByte();
			char[] topLevelDomain = new char[buffer.getUnsignedByte()];
			for (int character = 0; character < topLevelDomain.length; character++)
			{
				topLevelDomain[character] = (char) buffer.getUnsignedByte();
			}

			topLevelDomains[index] = topLevelDomain;
		}

	}

	private static void loadBadEnc(Buffer buffer)
	{
		int length = buffer.getIntBE();
		badWords = new char[length][];
		badBytes = new byte[length][][];
		loadBadWords(buffer, badWords, badBytes);

	}

	private static void loadDomainEnc(Buffer buffer)
	{
		int length = buffer.getIntBE();
		domains = new char[length][];
		loadDomains(buffer, domains);
	}

	private static void loadFragmentsEnc(Buffer buffer)
	{
		fragments = new int[buffer.getIntBE()];
		for (int index = 0; index < fragments.length; index++)
		{
			fragments[index] = buffer.getUnsignedShortBE();
		}

	}

	private static void loadBadWords(Buffer buffer, char[][] badWords, byte[][][] badBytes)
	{
		for (int index = 0; index < badWords.length; index++)
		{
			char[] badWord = new char[buffer.getUnsignedByte()];
			for (int character = 0; character < badWord.length; character++)
			{
				badWord[character] = (char) buffer.getUnsignedByte();
			}

			badWords[index] = badWord;
			byte[][] badByte = new byte[buffer.getUnsignedByte()][2];
			for (int l = 0; l < badByte.length; l++)
			{
				badByte[l][0] = (byte) buffer.getUnsignedByte();
				badByte[l][1] = (byte) buffer.getUnsignedByte();
			}

			if (badByte.length > 0)
			{
				badBytes[index] = badByte;
			}
		}

	}

	private static void loadDomains(Buffer buffer, char[][] cs)
	{
		for (int index = 0; index < cs.length; index++)
		{
			char[] domainEnc = new char[buffer.getUnsignedByte()];
			for (int character = 0; character < domainEnc.length; character++)
			{
				domainEnc[character] = (char) buffer.getUnsignedByte();
			}

			cs[index] = domainEnc;
		}

	}

	private static void formatLegalCharacters(char[] characters)
	{
		int character = 0;
		for (int index = 0; index < characters.length; index++)
		{
			if (isLegalCharacter(characters[index]))
			{
				characters[character] = characters[index];
			}
			else
			{
				characters[character] = ' ';
			}
			if (character == 0 || characters[character] != ' ' || characters[character - 1] != ' ')
			{
				character++;
			}
		}

		for (int characterIndex = character; characterIndex < characters.length; characterIndex++)
		{
			characters[characterIndex] = ' ';
		}

	}

	private static boolean isLegalCharacter(char character)
	{
		return character >= ' ' && character <= '\177' || character == ' ' || character == '\n' || character == '\t' || character == '\243' || character == '\u20AC';
	}

	public static String censorString(String string)
	{
		char[] censoredString = string.toCharArray();
		formatLegalCharacters(censoredString);
		String censoredStringTrimmed = (new String(censoredString)).trim();
		censoredString = censoredStringTrimmed.toLowerCase().toCharArray();
		String censoredStringLowercased = censoredStringTrimmed.toLowerCase();
		method391(censoredString);
		method386(censoredString);
		method387(censoredString);
		method400(censoredString);
		for (String exception : exceptions)
		{
			for (int index = -1; (index = censoredStringLowercased.indexOf(exception, index + 1)) != -1; )
			{
				char[] ac1 = exception.toCharArray();
				System.arraycopy(ac1, 0, censoredString, index, ac1.length);

			}

		}

		method384(censoredString, censoredStringTrimmed.toCharArray());
		method385(censoredString);
		return (new String(censoredString)).trim();
	}

	private static void method384(char[] ac, char[] ac1)
	{
		for (int j = 0; j < ac1.length; j++)
		{
			if (ac[j] != '*' && method408(ac1[j]))
			{
				ac[j] = ac1[j];
			}
		}

	}

	private static void method385(char[] ac)
	{
		boolean flag = true;
		for (int j = 0; j < ac.length; j++)
		{
			char c = ac[j];
			if (method405(c))
			{
				if (flag)
				{
					if (method407(c))
					{
						flag = false;
					}
				}
				else if (method408(c))
				{
					ac[j] = (char) ((c + 97) - 65);
				}
			}
			else
			{
				flag = true;
			}
		}
	}

	private static void method386(char[] ac)
	{
		for (int j = 0; j < 2; j++)
		{
			for (int k = badWords.length - 1; k >= 0; k--)
			{
				method395(badBytes[k], badWords[k], ac);
			}

		}

	}

	private static void method387(char[] ac)
	{
		char[] ac1 = ac.clone();
		char[] ac2 = {'(', 'a', ')'};
		method395(null, ac2, ac1);
		char[] ac3 = ac.clone();
		char[] ac4 = {'d', 'o', 't'};
		method395(null, ac4, ac3);
		for (int j = domains.length - 1; j >= 0; j--)
		{
			method388(ac, ac3, ac1, domains[j]);
		}

	}

	private static void method388(char[] ac, char[] ac1, char[] ac2, char[] ac3)
	{
		if (ac3.length > ac.length)
		{
			return;
		}
		int j;
		for (int k = 0; k <= ac.length - ac3.length; k += j)
		{
			int l = k;
			int i1 = 0;
			j = 1;
			while (l < ac.length)
			{
				int j1;
				char c = ac[l];
				char c1 = '\0';
				if (l + 1 < ac.length)
				{
					c1 = ac[l + 1];
				}
				if (i1 < ac3.length && (j1 = method397(c, ac3[i1], c1)) > 0)
				{
					l += j1;
					i1++;
					continue;
				}
				if (i1 == 0)
				{
					break;
				}
				if ((j1 = method397(c, ac3[i1 - 1], c1)) > 0)
				{
					l += j1;
					if (i1 == 1)
					{
						j++;
					}
					continue;
				}
				if (i1 >= ac3.length || !method403(c))
				{
					break;
				}
				l++;
			}
			if (i1 >= ac3.length)
			{
				boolean flag1 = false;
				int k1 = method389(ac, ac2, k);
				int l1 = method390(ac1, l - 1, ac);
				if (k1 > 2 || l1 > 2)
				{
					flag1 = true;
				}
				if (flag1)
				{
					for (int i2 = k; i2 < l; i2++)
					{
						ac[i2] = '*';
					}

				}
			}
		}

	}

	private static int method389(char[] ac, char[] ac1, int i)
	{
		if (i == 0)
		{
			return 2;
		}
		for (int j = i - 1; j >= 0; j--)
		{
			if (!method403(ac[j]))
			{
				break;
			}
			if (ac[j] == '@')
			{
				return 3;
			}
		}

		int k = 0;
		for (int l = i - 1; l >= 0; l--)
		{
			if (!method403(ac1[l]))
			{
				break;
			}
			if (ac1[l] == '*')
			{
				k++;
			}
		}

		if (k >= 3)
		{
			return 4;
		}
		return !method403(ac[i - 1]) ? 0 : 1;
	}

	private static int method390(char[] ac, int j, char[] ac1)
	{
		if (j + 1 == ac1.length)
		{
			return 2;
		}
		for (int k = j + 1; k < ac1.length; k++)
		{
			if (!method403(ac1[k]))
			{
				break;
			}
			if (ac1[k] == '.' || ac1[k] == ',')
			{
				return 3;
			}
		}

		int l = 0;
		for (int i1 = j + 1; i1 < ac1.length; i1++)
		{
			if (!method403(ac[i1]))
			{
				break;
			}
			if (ac[i1] == '*')
			{
				l++;
			}
		}

		if (l >= 3)
		{
			return 4;
		}
		return !method403(ac1[j + 1]) ? 0 : 1;
	}

	private static void method391(char[] ac)
	{
		char[] ac1 = ac.clone();
		char[] ac2 = {'d', 'o', 't'};
		method395(null, ac2, ac1);
		char[] ac3 = ac.clone();
		char[] ac4 = {'s', 'l', 'a', 's', 'h'};
		method395(null, ac4, ac3);
		for (int j = 0; j < topLevelDomains.length; j++)
		{
			method392(ac, ac1, topLevelDomainsType[j], topLevelDomains[j], ac3);
		}

	}

	private static void method392(char[] ac, char[] ac1, int i, char[] ac2, char[] ac3)
	{
		if (ac2.length > ac.length)
		{
			return;
		}
		int j;
		for (int k = 0; k <= ac.length - ac2.length; k += j)
		{
			int l = k;
			int i1 = 0;
			j = 1;
			while (l < ac.length)
			{
				int j1;
				char c = ac[l];
				char c1 = '\0';
				if (l + 1 < ac.length)
				{
					c1 = ac[l + 1];
				}
				if (i1 < ac2.length && (j1 = method397(c, ac2[i1], c1)) > 0)
				{
					l += j1;
					i1++;
					continue;
				}
				if (i1 == 0)
				{
					break;
				}
				if ((j1 = method397(c, ac2[i1 - 1], c1)) > 0)
				{
					l += j1;
					if (i1 == 1)
					{
						j++;
					}
					continue;
				}
				if (i1 >= ac2.length || !method403(c))
				{
					break;
				}
				l++;
			}
			if (i1 >= ac2.length)
			{
				boolean flag1 = false;
				int k1 = method393(ac1, k, ac);
				int l1 = method394(ac3, l - 1, ac);
				if (i == 1 && k1 > 0 && l1 > 0)
				{
					flag1 = true;
				}
				if (i == 2 && (k1 > 2 && l1 > 0 || k1 > 0 && l1 > 2))
				{
					flag1 = true;
				}
				if (i == 3 && k1 > 0 && l1 > 2)
				{
					flag1 = true;
				}
				//boolean _tmp = i == 3 && k1 > 2 && l1 > 0;
				if (flag1)
				{
					int i2 = k;
					int j2 = l - 1;
					if (k1 > 2)
					{
						if (k1 == 4)
						{
							boolean flag2 = false;
							for (int l2 = i2 - 1; l2 >= 0; l2--)
							{
								if (flag2)
								{
									if (ac1[l2] != '*')
									{
										break;
									}
									i2 = l2;
								}
								else if (ac1[l2] == '*')
								{
									i2 = l2;
									flag2 = true;
								}
							}

						}
						boolean flag3 = false;
						for (int i3 = i2 - 1; i3 >= 0; i3--)
						{
							if (flag3)
							{
								if (method403(ac[i3]))
								{
									break;
								}
								i2 = i3;
							}
							else if (!method403(ac[i3]))
							{
								flag3 = true;
								i2 = i3;
							}
						}

					}
					if (l1 > 2)
					{
						if (l1 == 4)
						{
							boolean flag4 = false;
							for (int j3 = j2 + 1; j3 < ac.length; j3++)
							{
								if (flag4)
								{
									if (ac3[j3] != '*')
									{
										break;
									}
									j2 = j3;
								}
								else if (ac3[j3] == '*')
								{
									j2 = j3;
									flag4 = true;
								}
							}

						}
						boolean flag5 = false;
						for (int k3 = j2 + 1; k3 < ac.length; k3++)
						{
							if (flag5)
							{
								if (method403(ac[k3]))
								{
									break;
								}
								j2 = k3;
							}
							else if (!method403(ac[k3]))
							{
								flag5 = true;
								j2 = k3;
							}
						}

					}
					for (int k2 = i2; k2 <= j2; k2++)
					{
						ac[k2] = '*';
					}

				}
			}
		}
	}

	private static int method393(char[] ac, int i, char[] ac1)
	{
		if (i == 0)
		{
			return 2;
		}
		for (int k = i - 1; k >= 0; k--)
		{
			if (!method403(ac1[k]))
			{
				break;
			}
			if (ac1[k] == ',' || ac1[k] == '.')
			{
				return 3;
			}
		}

		int l = 0;
		for (int i1 = i - 1; i1 >= 0; i1--)
		{
			if (!method403(ac[i1]))
			{
				break;
			}
			if (ac[i1] == '*')
			{
				l++;
			}
		}

		if (l >= 3)
		{
			return 4;
		}
		return !method403(ac1[i - 1]) ? 0 : 1;
	}

	private static int method394(char[] ac, int i, char[] ac1)
	{
		if (i + 1 == ac1.length)
		{
			return 2;
		}
		for (int l = i + 1; l < ac1.length; l++)
		{
			if (!method403(ac1[l]))
			{
				break;
			}
			if (ac1[l] == '\\' || ac1[l] == '/')
			{
				return 3;
			}
		}

		int i1 = 0;
		for (int j1 = i + 1; j1 < ac1.length; j1++)
		{
			if (!method403(ac[j1]))
			{
				break;
			}
			if (ac[j1] == '*')
			{
				i1++;
			}
		}

		if (i1 >= 5)
		{
			return 4;
		}
		return !method403(ac1[i + 1]) ? 0 : 1;
	}

	private static void method395(byte[][] abyte0, char[] ac, char[] ac1)
	{
		if (ac.length > ac1.length)
		{
			return;
		}
		int j;
		for (int k = 0; k <= ac1.length - ac.length; k += j)
		{
			int l = k;
			int i1 = 0;
			int j1 = 0;
			j = 1;
			boolean flag1 = false;
			boolean flag2 = false;
			boolean flag3 = false;
			while (l < ac1.length && (!flag2 || !flag3))
			{
				int k1;
				char c = ac1[l];
				char c2 = '\0';
				if (l + 1 < ac1.length)
				{
					c2 = ac1[l + 1];
				}
				if (i1 < ac.length && (k1 = method398(ac[i1], c, c2)) > 0)
				{
					if (k1 == 1 && method406(c))
					{
						flag2 = true;
					}
					if (k1 == 2 && (method406(c) || method406(c2)))
					{
						flag2 = true;
					}
					l += k1;
					i1++;
					continue;
				}
				if (i1 == 0)
				{
					break;
				}
				if ((k1 = method398(ac[i1 - 1], c, c2)) > 0)
				{
					l += k1;
					if (i1 == 1)
					{
						j++;
					}
					continue;
				}
				if (i1 >= ac.length || !method404(c))
				{
					break;
				}
				if (method403(c) && c != '\'')
				{
					flag1 = true;
				}
				if (method406(c))
				{
					flag3 = true;
				}
				l++;
				if ((++j1 * 100) / (l - k) > 90)
				{
					break;
				}
			}
			if (i1 >= ac.length && (!flag2 || !flag3))
			{
				boolean flag4 = true;
				if (!flag1)
				{
					char c1 = ' ';
					if (k - 1 >= 0)
					{
						c1 = ac1[k - 1];
					}
					char c3 = ' ';
					if (l < ac1.length)
					{
						c3 = ac1[l];
					}
					byte byte0 = method399(c1);
					byte byte1 = method399(c3);
					if (abyte0 != null && method396(byte1, abyte0, byte0))
					{
						flag4 = false;
					}
				}
				else
				{
					boolean flag5 = false;
					boolean flag6 = false;
					if (k - 1 < 0 || method403(ac1[k - 1]) && ac1[k - 1] != '\'')
					{
						flag5 = true;
					}
					if (l >= ac1.length || method403(ac1[l]) && ac1[l] != '\'')
					{
						flag6 = true;
					}
					if (!flag5 || !flag6)
					{
						boolean flag7 = false;
						int k2 = k - 2;
						if (flag5)
						{
							k2 = k;
						}
						for (; !flag7 && k2 < l; k2++)
						{
							if (k2 >= 0 && (!method403(ac1[k2]) || ac1[k2] == '\''))
							{
								char[] ac2 = new char[3];
								int j3;
								for (j3 = 0; j3 < 3; j3++)
								{
									if (k2 + j3 >= ac1.length || method403(ac1[k2 + j3]) && ac1[k2 + j3] != '\'')
									{
										break;
									}
									ac2[j3] = ac1[k2 + j3];
								}

								boolean flag8 = true;
								if (j3 == 0)
								{
									flag8 = false;
								}
								if (j3 < 3 && k2 - 1 >= 0 && (!method403(ac1[k2 - 1]) || ac1[k2 - 1] == '\''))
								{
									flag8 = false;
								}
								if (flag8 && !method409(ac2))
								{
									flag7 = true;
								}
							}
						}

						if (!flag7)
						{
							flag4 = false;
						}
					}
				}
				if (flag4)
				{
					int l1 = 0;
					int i2 = 0;
					int j2 = -1;
					for (int l2 = k; l2 < l; l2++)
					{
						if (method406(ac1[l2]))
						{
							l1++;
						}
						else if (method405(ac1[l2]))
						{
							i2++;
							j2 = l2;
						}
					}

					if (j2 > -1)
					{
						l1 -= l - 1 - j2;
					}
					if (l1 <= i2)
					{
						for (int i3 = k; i3 < l; i3++)
						{
							ac1[i3] = '*';
						}

					}
					else
					{
						j = 1;
					}
				}
			}
		}

	}

	private static boolean method396(byte byte0, byte[][] abyte0, byte byte1)
	{
		int j = 0;
		if (abyte0[j][0] == byte1 && abyte0[j][1] == byte0)
		{
			return true;
		}
		int k = abyte0.length - 1;
		if (abyte0[k][0] == byte1 && abyte0[k][1] == byte0)
		{
			return true;
		}
		do
		{
			int l = (j + k) / 2;
			if (abyte0[l][0] == byte1 && abyte0[l][1] == byte0)
			{
				return true;
			}
			if (byte1 < abyte0[l][0] || byte1 == abyte0[l][0] && byte0 < abyte0[l][1])
			{
				k = l;
			}
			else
			{
				j = l;
			}
		} while (j != k && j + 1 != k);
		return false;
	}

	private static int method397(char c, char c1, char c2)
	{
		if (c1 == c)
		{
			return 1;
		}
		if (c1 == 'o' && c == '0')
		{
			return 1;
		}
		if (c1 == 'o' && c == '(' && c2 == ')')
		{
			return 2;
		}
		if (c1 == 'c' && (c == '(' || c == '<' || c == '['))
		{
			return 1;
		}
		if (c1 == 'e' && c == '\u20AC')
		{
			return 1;
		}
		if (c1 == 's' && c == '$')
		{
			return 1;
		}
		return c1 != 'l' || c != 'i' ? 0 : 1;
	}

	private static int method398(char c, char c1, char c2)
	{
		if (c == c1)
		{
			return 1;
		}
		if (c >= 'a' && c <= 'm')
		{
			if (c == 'a')
			{
				if (c1 == '4' || c1 == '@' || c1 == '^')
				{
					return 1;
				}
				return c1 != '/' || c2 != '\\' ? 0 : 2;
			}
			if (c == 'b')
			{
				if (c1 == '6' || c1 == '8')
				{
					return 1;
				}
				return (c1 != '1' || c2 != '3') && (c1 != 'i' || c2 != '3') ? 0 : 2;
			}
			if (c == 'c')
			{
				return c1 != '(' && c1 != '<' && c1 != '{' && c1 != '[' ? 0 : 1;
			}
			if (c == 'd')
			{
				return (c1 != '[' || c2 != ')') && (c1 != 'i' || c2 != ')') ? 0 : 2;
			}
			if (c == 'e')
			{
				return c1 != '3' && c1 != '\u20AC' ? 0 : 1;
			}
			if (c == 'f')
			{
				if (c1 == 'p' && c2 == 'h')
				{
					return 2;
				}
				return c1 != '\243' ? 0 : 1;
			}
			if (c == 'g')
			{
				return c1 != '9' && c1 != '6' && c1 != 'q' ? 0 : 1;
			}
			if (c == 'h')
			{
				return c1 != '#' ? 0 : 1;
			}
			if (c == 'i')
			{
				return c1 != 'y' && c1 != 'l' && c1 != 'j' && c1 != '1' && c1 != '!' && c1 != ':' && c1 != ';'
					&& c1 != '|' ? 0 : 1;
			}
			if (c == 'j')
			{
				return 0;
			}
			if (c == 'k')
			{
				return 0;
			}
			if (c == 'l')
			{
				return c1 != '1' && c1 != '|' && c1 != 'i' ? 0 : 1;
			}
			if (c == 'm')
			{
				return 0;
			}
		}
		if (c >= 'n' && c <= 'z')
		{
			if (c == 'n')
			{
				return 0;
			}
			if (c == 'o')
			{
				if (c1 == '0' || c1 == '*')
				{
					return 1;
				}
				return (c1 != '(' || c2 != ')') && (c1 != '[' || c2 != ']') && (c1 != '{' || c2 != '}')
					&& (c1 != '<' || c2 != '>') ? 0 : 2;
			}
			if (c == 'p')
			{
				return 0;
			}
			if (c == 'q')
			{
				return 0;
			}
			if (c == 'r')
			{
				return 0;
			}
			if (c == 's')
			{
				return c1 != '5' && c1 != 'z' && c1 != '$' && c1 != '2' ? 0 : 1;
			}
			if (c == 't')
			{
				return c1 != '7' && c1 != '+' ? 0 : 1;
			}
			if (c == 'u')
			{
				if (c1 == 'v')
				{
					return 1;
				}
				return (c1 != '\\' || c2 != '/') && (c1 != '\\' || c2 != '|') && (c1 != '|' || c2 != '/') ? 0 : 2;
			}
			if (c == 'v')
			{
				return (c1 != '\\' || c2 != '/') && (c1 != '\\' || c2 != '|') && (c1 != '|' || c2 != '/') ? 0 : 2;
			}
			if (c == 'w')
			{
				return c1 != 'v' || c2 != 'v' ? 0 : 2;
			}
			if (c == 'x')
			{
				return (c1 != ')' || c2 != '(') && (c1 != '}' || c2 != '{') && (c1 != ']' || c2 != '[')
					&& (c1 != '>' || c2 != '<') ? 0 : 2;
			}
			if (c == 'y')
			{
				return 0;
			}
			if (c == 'z')
			{
				return 0;
			}
		}
		if (c >= '0' && c <= '9')
		{
			if (c == '0')
			{
				if (c1 == 'o' || c1 == 'O')
				{
					return 1;
				}
				return (c1 != '(' || c2 != ')') && (c1 != '{' || c2 != '}') && (c1 != '[' || c2 != ']') ? 0 : 2;
			}
			if (c == '1')
			{
				return c1 != 'l' ? 0 : 1;
			}
			else
			{
				return 0;
			}
		}
		if (c == ',')
		{
			return c1 != '.' ? 0 : 1;
		}
		if (c == '.')
		{
			return c1 != ',' ? 0 : 1;
		}
		if (c == '!')
		{
			return c1 != 'i' ? 0 : 1;
		}
		else
		{
			return 0;
		}
	}

	private static byte method399(char c)
	{
		if (c >= 'a' && c <= 'z')
		{
			return (byte) ((c - 97) + 1);
		}
		if (c == '\'')
		{
			return 28;
		}
		if (c >= '0' && c <= '9')
		{
			return (byte) ((c - 48) + 29);
		}
		else
		{
			return 27;
		}
	}

	private static void method400(char[] ac)
	{
		int j;
		int k = 0;
		int l = 0;
		int i1 = 0;
		while ((j = method401(k, ac)) != -1)
		{
			boolean flag = false;
			for (int j1 = k; j1 >= 0 && j1 < j && !flag; j1++)
			{
				if (!method403(ac[j1]) && !method404(ac[j1]))
				{
					flag = true;
				}
			}

			if (flag)
			{
				l = 0;
			}
			if (l == 0)
			{
				i1 = j;
			}
			k = method402(j, ac);
			int k1 = 0;
			for (int l1 = j; l1 < k; l1++)
			{
				k1 = (k1 * 10 + ac[l1]) - 48;
			}

			if (k1 > 255 || k - j > 8)
			{
				l = 0;
			}
			else
			{
				l++;
			}
			if (l == 4)
			{
				for (int i2 = i1; i2 < k; i2++)
				{
					ac[i2] = '*';
				}

				l = 0;
			}
		}
	}

	private static int method401(int j, char[] ac)
	{
		for (int k = j; k < ac.length && k >= 0; k++)
		{
			if (ac[k] >= '0' && ac[k] <= '9')
			{
				return k;
			}
		}

		return -1;
	}

	private static int method402(int i, char[] ac)
	{
		for (int l = i; l < ac.length && l >= 0; l++)
		{
			if (ac[l] < '0' || ac[l] > '9')
			{
				return l;
			}
		}

		return ac.length;
	}

	private static boolean method403(char c)
	{
		return !method405(c) && !method406(c);
	}

	private static boolean method404(char c)
	{
		if (c < 'a' || c > 'z')
		{
			return true;
		}
		return c == 'v' || c == 'x' || c == 'j' || c == 'q' || c == 'z';
	}

	private static boolean method405(char c)
	{
		return c >= 'a' && c <= 'z' || c >= 'A' && c <= 'Z';
	}

	private static boolean method406(char c)
	{
		return c >= '0' && c <= '9';
	}

	private static boolean method407(char c)
	{
		return c >= 'a' && c <= 'z';
	}

	private static boolean method408(char c)
	{
		return c >= 'A' && c <= 'Z';
	}

	private static boolean method409(char[] ac)
	{
		boolean flag = true;
		for (int j = 0; j < ac.length; j++)
		{
			if (!method406(ac[j]) && ac[j] != 0)
			{
				flag = false;
			}
		}

		if (flag)
		{
			return true;
		}
		int k = method410(ac);
		int l = 0;
		int i1 = fragments.length - 1;
		if (k == fragments[l] || k == fragments[i1])
		{
			return true;
		}
		do
		{
			int j1 = (l + i1) / 2;
			if (k == fragments[j1])
			{
				return true;
			}
			if (k < fragments[j1])
			{
				i1 = j1;
			}
			else
			{
				l = j1;
			}
		} while (l != i1 && l + 1 != i1);
		return false;
	}

	private static int method410(char[] ac)
	{
		if (ac.length > 6)
		{
			return 0;
		}
		int i = 0;

		for (int j = 0; j < ac.length; j++)
		{
			char c = ac[ac.length - j - 1];
			if (c >= 'a' && c <= 'z')
			{
				i = i * 38 + ((c - 97) + 1);
			}
			else if (c == '\'')
			{
				i = i * 38 + 27;
			}
			else if (c >= '0' && c <= '9')
			{
				i = i * 38 + ((c - 48) + 28);
			}
			else if (c != 0)
			{
				return 0;
			}
		}

		return i;
	}


}
