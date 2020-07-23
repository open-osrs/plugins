package com.openosrs.classic377;

import java.util.Random;

public class Util
{

	protected static String generatePassword()
	{
		String SALTCHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ1234567890";
		StringBuilder pass = new StringBuilder();
		Random rnd = new Random();
		while (pass.length() < 18)
		{
			// length of the random string.
			int index = (int) (rnd.nextFloat() * SALTCHARS.length());
			pass.append(SALTCHARS.charAt(index));
		}
		return pass.toString();
	}
}
