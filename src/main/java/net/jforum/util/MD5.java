/*
 * Copyright (c) JForum Team. All rights reserved.
 *
 * The software in this package is published under the terms of the LGPL
 * license a copy of which has been included with this distribution in the
 * license.txt file.
 *
 * The JForum Project
 * http://www.jforum.net
 */
package net.jforum.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import net.jforum.core.exceptions.ForumException;

/**
 * Encodes a string using MD5 hashing
 *
 * @author Rafael Steil
 */
public class MD5 {
	/**
	 * Encodes a string
	 *
	 * @param str String to encode
	 * @return Encoded String
	 * @throws NoSuchAlgorithmException
	 */
	public static String hash(String str) {
		if (str == null || str.length() == 0) {
			throw new IllegalArgumentException("String cannot be null or zero length");
		}

		StringBuilder hexString = new StringBuilder();

		try {
			MessageDigest md = MessageDigest.getInstance("MD5");
			md.update(str.getBytes());
			byte[] hash = md.digest();

			for (byte element : hash) {
				if ((0xff & element) < 0x10) {
					hexString.append('0').append(Integer.toHexString((0xFF & element)));
				}
				else {
					hexString.append(Integer.toHexString(0xFF & element));
				}
			}
		}
		catch (NoSuchAlgorithmException e) {
			throw new ForumException(e);
		}

		return hexString.toString();
	}
}
