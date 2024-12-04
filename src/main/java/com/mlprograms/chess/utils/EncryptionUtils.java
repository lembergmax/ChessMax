/*
 * Copyright (c) 2024 Max Lemberg. This file is part of ChessMax.
 * Licenced under the CC BY-NC 4.0 License.
 * See "http://creativecommons.org/licenses/by-nc/4.0/".
 */

package com.mlprograms.chess.utils;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;

public class EncryptionUtils {

	private static final String ALGORITHM = "AES";
	private static final int KEY_SIZE = 256;

	/**
	 * Generates a new AES encryption key.
	 *
	 * @return the generated key as a Base64 encoded string.
	 *
	 * @throws Exception
	 * 	if key generation fails.
	 */
	public static String generateKey() throws Exception {
		KeyGenerator keyGen = KeyGenerator.getInstance(ALGORITHM);
		keyGen.init(KEY_SIZE);
		SecretKey secretKey = keyGen.generateKey();
		return Base64.getEncoder().encodeToString(secretKey.getEncoded());
	}

	/**
	 * Encrypts a plain text using the given key.
	 *
	 * @param plainText
	 * 	the text to be encrypted.
	 * @param key
	 * 	the encryption key as a Base64 encoded string.
	 *
	 * @return the encrypted text as a Base64 encoded string.
	 *
	 * @throws Exception
	 * 	if encryption fails.
	 */
	public static String encrypt(String plainText, String key) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.ENCRYPT_MODE, secretKey);
		byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());
		return Base64.getEncoder().encodeToString(encryptedBytes);
	}

	/**
	 * Decrypts an encrypted text using the given key.
	 *
	 * @param encryptedText
	 * 	the text to be decrypted.
	 * @param key
	 * 	the encryption key as a Base64 encoded string.
	 *
	 * @return the decrypted plain text.
	 *
	 * @throws Exception
	 * 	if decryption fails.
	 */
	public static String decrypt(String encryptedText, String key) throws Exception {
		SecretKeySpec secretKey = new SecretKeySpec(Base64.getDecoder().decode(key), ALGORITHM);
		Cipher cipher = Cipher.getInstance(ALGORITHM);
		cipher.init(Cipher.DECRYPT_MODE, secretKey);
		byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedText));
		return new String(decryptedBytes);
	}

}
