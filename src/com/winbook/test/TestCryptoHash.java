package com.winbook.test;

import org.junit.Test;

import com.winbook.layer.accessControl.CryptoHash;
import static org.junit.Assert.*;

public class TestCryptoHash {

	@Test
	public void testSHA1()
	{
		String message = "nkukreja";
		String expectedHash = "b7c460ee0574203c1f66e339bd4849666cc6d6d4";
		
		String sha1HashOfMessage = CryptoHash.SHA1(message);
		
		assertEquals(expectedHash, sha1HashOfMessage);
	}
}
