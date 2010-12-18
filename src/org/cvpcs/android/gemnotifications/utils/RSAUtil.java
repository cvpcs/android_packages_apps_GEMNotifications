package org.cvpcs.android.gemnotifications.utils;

import android.util.Base64;

import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;

public class RSAUtil {
	// our static base64 x509 public key we use to encrypt all data.  note that
	// the private key that generated this public key has already been destroyed
	private static String PUBLIC_KEY_BASE64 = 
		"MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAxNiZ5CbjFBemaVQbrQXa" +
		"i1iEYkNjYXDPgEzNEs1+sUkHO4Naho0NC9+zNAkAsTNXC9/SMLGoPZ2ik+Z7yeOG" +
		"UAsdVQhL4RIotQb2R4jzXsxpqrRVU8ShKwxUlhPXvNX5PsKb4uopuMmp9pQA8kw/" +
		"V4xWTxw5UJW9mP2/bRIDyJVVVot8JJDhZjn/UqYWOg7DL0pFF4cmiD+yUsiNtWCa" +
		"bql8F0vGj+iBpQZyT42KE9J5TuuGpxCcpCgNNDycYthgLoCq2u8UxE4tuTDYjiGm" +
		"Yhf/+QomkZhv1LIkLZ3wfd+mw60EaGIlsNsE95ydnwrId6R2J6vy3XSfZXqL4cbL" +
		"cwIDAQAB";
	
	public static String encrypt(String data) throws Exception {
		byte[] enc = encrypt(data.getBytes());

		return Base64.encodeToString(enc, Base64.NO_WRAP | Base64.NO_PADDING);
	}
	
	public static byte[] encrypt(byte[] data) throws Exception {		
		X509EncodedKeySpec spec = new X509EncodedKeySpec(Base64.decode(PUBLIC_KEY_BASE64, 0));
		
		KeyFactory kf = KeyFactory.getInstance("RSA");
		PublicKey pub = kf.generatePublic(spec);
		
		Cipher cipher = Cipher.getInstance("RSA");
		cipher.init(Cipher.ENCRYPT_MODE, pub);
		
		return cipher.doFinal(data);
	}
	
	
}