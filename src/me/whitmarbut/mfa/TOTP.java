package me.whitmarbut.mfa;

import org.apache.commons.codec.binary.Base32;

import java.lang.StringBuilder;
import java.nio.ByteBuffer;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

public class TOTP {
	
	public static void main(String[] args) throws InvalidKeyException, NoSuchAlgorithmException {
		TOTP token_gen = new TOTP();
        String str = "";
        System.out.println("\nYour token: " + token_gen.getToken(str));
	}
	
	public String getToken(String key) {
		byte[] priv_key = getSecretBytes(key);
		byte[] hmac;
		try {
			hmac = getHmac(getTimestamp(), priv_key);
		} catch (Exception ex) {
			return null;
		}
		return getToken(hmac);
	}
	
	
	private byte[] getSecretBytes(String secret) {
		secret = secret.toUpperCase();
        Base32 b32 = new Base32();
        return b32.decode(secret);
	}
	
	private byte[] getHmac(int timestamp, byte[] key) throws NoSuchAlgorithmException, InvalidKeyException {
		SecretKeySpec key_spec = new SecretKeySpec(key, "HmacSHA1");
		Mac mac = Mac.getInstance("HmacSHA1");
		mac.init(key_spec);
		byte[] bin_timestamp = ByteBuffer.allocate(4).putInt(timestamp).array();
		
		ByteBuffer bbuff = ByteBuffer.allocate(8);
		bbuff.putInt(0); //Left pad 4 bytes to make a 64 bit int
		bbuff.putInt(timestamp);
		
		return mac.doFinal(bbuff.array());
	}
	
	private int getTimestamp() {
		return (int) Math.floor((System.currentTimeMillis()/1000)/30);
	}
	
	private String getToken( byte[] hmac ) {
		int offset = hmac[19] & 0xf;
		
		Integer token = (int) ((
			((hmac[offset] & 0x7f) << 24) |
			((hmac[offset+1] & 0xff) << 16) |
			((hmac[offset+2] & 0xff) << 8) |
			(hmac[offset+3] & 0xff)	 
		) % (Math.pow(10, 6)));
		String token_str = token.toString();
		if (token_str.length() < 6) {
			int to_add = 6 - token_str.length();
			for (int i = 0; i < to_add; i++) {
				token_str = "0"+token_str;
			}
		}
		
		return token_str;
	}
}
