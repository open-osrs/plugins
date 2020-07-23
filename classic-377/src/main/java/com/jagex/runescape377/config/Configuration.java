package com.jagex.runescape377.config;

import java.math.BigInteger;

public class Configuration
{
	/**
	 * IP Address or Hostname of the server to establish a connection.
	 */
	public static String CODEBASE = "127.0.0.1";

	/**
	 * Name of the cache folder located in the users home directory.
	 */
	public static String CACHE_NAME = ".oprsclassic";

	/**
	 * Port for establishing a connection to the game server.
	 */
	public static int GAME_PORT = 43594;

	/**
	 * Port for establishing a connection to the on demand service.
	 */
	public static int ONDEMAND_PORT = 43594;

	/**
	 * Port for establishing a connection to the update server.
	 */
	public static int JAGGRAB_PORT = 43595;

	/**
	 * Port for establishing a backup connection to the update
	 * server in case the initial JAGGRAB connection fails.
	 */
	public static int HTTP_PORT = 80;

	/**
	 * Whether or not the update server should be used.
	 */
	public static boolean JAGGRAB_ENABLED = true;

	/**
	 * Whether or not the network packets should be encrypted.
	 */
	public static boolean RSA_ENABLED = true;

	/**
	 * Public key to be used in RSA network encryption.
	 */
	public static BigInteger RSA_PUBLIC_KEY = new BigInteger("65537");

	/**
	 * Modulus to be used in the RSA network encryption.
	 */
	public static BigInteger RSA_MODULUS = new BigInteger("173690704813226339278988505151930177085661629513029086769700360543883620582872190483614694195513693213454953909349863954153102405907551876124629131961664750527374005189688680724564876905221441864918417236834760777740385353369303105216516198927126091523745993346754240012911593232344695234410229297179431508213");

	/**
	 * Use static username/password pair.
	 */
	public static boolean USE_STATIC_DETAILS = true;

	/**
	 * Static username and password
	 */

	public static String USERNAME = "";
	public static String PASSWORD = "";

	/**
	 * Do you want to render roofs
	 */
	public static boolean ROOFS_ENABLED = true;

	/**
	 * Always light up teleports
	 */
	public static boolean FREE_TELEPORTS = true;

	/**
	 * When rightclicking objects show id and location
	 */
	public static boolean DEBUG_CONTEXT = true;


}
