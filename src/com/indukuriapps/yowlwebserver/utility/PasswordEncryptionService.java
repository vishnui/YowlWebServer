package com.indukuriapps.yowlwebserver.utility;

import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

public class PasswordEncryptionService {

	public static boolean authenticate(String attemptedPassword, byte[] encryptedPassword, byte[] salt)  throws NoSuchAlgorithmException, InvalidKeySpecException {
	   // Encrypt the clear-text password using the same salt that was used to
	   // encrypt the original password
	   byte[] encryptedAttemptedPassword = getEncryptedPassword(attemptedPassword, salt);

	  // Authentication succeeds if encrypted password that the user entered
	  // is equal to the stored hash
	  return Arrays.equals(encryptedPassword, encryptedAttemptedPassword);
	}

	public static byte[] getEncryptedPassword(String password, byte[] salt) throws NoSuchAlgorithmException, InvalidKeySpecException {
	  String algorithm = "PBKDF2WithHmacSHA1";
	  int derivedKeyLength = 160;
	  int iterations = 20000;

	  SecretKeyFactory f = SecretKeyFactory.getInstance(algorithm);
	  
	  KeySpec spec = new PBEKeySpec(password.toCharArray(), salt, iterations, derivedKeyLength);

	  //apparently key does not support encoding? returns null, throws NullPointerException during registration
	  return f.generateSecret(spec).getEncoded();
	}

	public static byte[] generateSalt() throws NoSuchAlgorithmException {
	  // VERY important to use SecureRandom instead of just Random
	  SecureRandom random = SecureRandom.getInstance("SHA1PRNG");

	  // Generate a 8 byte (64 bit) salt as recommended by RSA PKCS5
	  byte[] salt = new byte[8];
	  random.nextBytes(salt);

	  return salt;
	}
	
	public static byte[] CalculateHMACSHA256Signature(String request){
	  byte[] key = DatatypeConverter.parseHexBinary("wlUwqCRXxTL7L/v4WvMZYlxlRsE6RPmmKzR/AbM1");
	  byte[] data = DatatypeConverter.parseHexBinary(request);
		  
	  // Create digest
	  SecretKey macKey = new SecretKeySpec(key, "HmacSHA256");
	  Mac mac;
	  try {
		  mac = Mac.getInstance("HmacSHA256");
		  mac.init(macKey);
	  } catch (NoSuchAlgorithmException e) {
		  return null ;
	  } catch (InvalidKeyException e) {  // Neither of these exception should ever arise since these keys and algorithms do exist and are tested
		  return null;
	  }
		
	  return mac.doFinal(data);
	}
}
