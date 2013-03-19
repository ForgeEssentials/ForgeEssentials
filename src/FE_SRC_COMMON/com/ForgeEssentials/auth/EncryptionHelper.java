package com.ForgeEssentials.auth;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.logging.Level;

import com.ForgeEssentials.util.OutputHandler;

public class EncryptionHelper
{
	private static final SecureRandom	rand		= new SecureRandom();
	private static final char[]			hex			= { '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f' };
	private static final String			saltChars	= "ABCDEFGHIJGMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz1234567890-_=+[]{};:.,<>/?\\|~`";
	private MessageDigest				sha1;

	public EncryptionHelper()
	{
		try
		{
			sha1 = MessageDigest.getInstance("SHA1");
		}
		catch (NoSuchAlgorithmException e)
		{
			// probably impossible too
		}
	}

	/**
	 * Should replicate PHP exactly.
	 * @param s
	 * @return
	 */
	public String sha1(String s)
	{
		try
		{
			byte[] array = (s + ModuleAuth.salt).getBytes("UTF-8");
			array = sha1.digest(array);
			String hashed = byteArrayToHex(array);
			return hashed;
		}
		catch (Throwable t)
		{
			OutputHandler.exception(Level.SEVERE, "Unable to hash password", t);
		}

		return "";
	}

	private static String byteArrayToHex(byte[] bytes)
	{
		StringBuilder sb = new StringBuilder(bytes.length * 2);
		for (final byte b : bytes)
		{
			sb.append(hex[(b & 0xF0) >> 4]);
			sb.append(hex[b & 0x0F]);
		}
		return sb.toString();
	}

	public static String generateSalt()
	{
		return generateSalt(rand.nextInt(10) + 5);
	}

	public static String generateSalt(int length)
	{
		char[] array = new char[length];

		for (int i = 0; i < length; i++)
			array[i] = saltChars.charAt(rand.nextInt(saltChars.length()));

		return new String(array);
	}
}
