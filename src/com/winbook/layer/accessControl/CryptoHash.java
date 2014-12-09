package com.winbook.layer.accessControl;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class CryptoHash {

	public static String SHA1(String message)
	{
		MessageDigest md = null;
		try {
			md = MessageDigest.getInstance("SHA-1");
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		md.update(message.getBytes());
		
		byte[] hashedPassword = md.digest();
		
		StringBuffer hexStr = new StringBuffer("");
		for (int i = 0; i < hashedPassword.length; i++) {
			hexStr.append(Integer.toString( ( hashedPassword[i] & 0xff ) + 0x100, 16).substring( 1 ));
		}
		
		return hexStr.toString();
	}
	

}
