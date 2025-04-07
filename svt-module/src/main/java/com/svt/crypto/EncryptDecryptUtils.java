package com.svt.crypto;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringWriter;
import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Arrays;
import java.util.Base64;
import java.util.Properties;
import java.util.UUID;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.svt.utils.common.Constants;

@Service
public class EncryptDecryptUtils {
	 public byte[] encodeToBase64(byte[] input) {

	        return Base64.getEncoder().encode(input);

	    }
	 
	    public byte[] decodeFromBase64(byte[] input) {

	        return Base64.getDecoder().decode(input);

	    }

	    public byte[] encryptKey(byte[] plaintextKey, byte[] publicKey, String encryptionMode, String encryptionAlgo) throws GeneralSecurityException {

	        return encryptedKey(plaintextKey, getPublic(publicKey, encryptionMode), encryptionAlgo);

	    }
	 
	    public byte[] decryptKey(byte[] decodedKey, byte[] privateKey, String decryptionMode, String decryptionAlgo) throws GeneralSecurityException {

	        return decryptedKey(decodedKey, getPrivate(privateKey, decryptionMode), decryptionAlgo);

	    }
	 
	    public byte[] encryptContent(byte[] content, byte[] iv, byte[] key, String encryptionMode, String encryptionAlgo) throws NoSuchAlgorithmException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException {

	        return encryptedContent(content, getSecretKey(key, encryptionMode), new IvParameterSpec(iv), encryptionAlgo);

	    }
	 
	    public byte[] decryptContent(byte[] encryptedContent, byte[] iv, byte[] key, String decryptionMode, String decryptionAlgo) throws GeneralSecurityException {

	        return decryptedContent(encryptedContent, getSecretKey(key, decryptionMode), iv, decryptionAlgo);

	    }

	    public byte[] encryptedKey(byte[] input, PublicKey key, String cipherAlgorithm) throws GeneralSecurityException {

	        Cipher cipher = Cipher.getInstance(cipherAlgorithm);

	        cipher.init(1, key);

	        return cipher.doFinal(input);

	    }
	 
	    public byte[] decryptedKey(byte[] input, PrivateKey key, String algorithm) throws GeneralSecurityException {

	        Cipher cipher = Cipher.getInstance(algorithm);

	        cipher.init(2, key);

	        return cipher.doFinal(input);

	    }
	 
	    public PublicKey getPublic(byte[] publicKey, String mode) throws NoSuchAlgorithmException, InvalidKeySpecException {

	        return KeyFactory.getInstance(mode).generatePublic(new X509EncodedKeySpec(Base64.getDecoder().decode(publicKey)));

	    }
	 
	    public byte[] generateSecretKey(int length, String algorithm) throws NoSuchAlgorithmException, NoSuchProviderException {

	        String random = UUID.randomUUID().toString();

	        random = random.replaceAll("-", "");

	        random = random.substring(0, 16);

	        return random.getBytes();

	    }
	 
	    public SecretKeySpec getSecretKey(byte[] keyBytes, String algorithm) {

	        return new SecretKeySpec(keyBytes, algorithm);

	    }
	 
	    public byte[] generateIv(String cipherAlgorithm) throws NoSuchAlgorithmException, NoSuchPaddingException {

	        byte[] iv = new byte[Cipher.getInstance(cipherAlgorithm).getBlockSize()];

	        new SecureRandom().nextBytes(iv);

	        return iv;

	    }
	 
	    public byte[] mergeTwoByteArrays(byte[] arrayOne, byte[] arrayTwo) {

	        return ByteBuffer.allocate(arrayOne.length + arrayTwo.length).put(arrayOne).put(arrayTwo).array();

	    }
	 
	    public byte[] encryptedContent(byte[] input, SecretKeySpec key, IvParameterSpec ivSpec, String cipherAlgorithm) throws InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException, NoSuchPaddingException, NoSuchAlgorithmException {

	        Cipher cipher = Cipher.getInstance(cipherAlgorithm);

	        cipher.init(1, key, ivSpec);

	        return cipher.doFinal(input);

	    }
	 
	    public byte[] decryptedContent(byte[] input, SecretKeySpec key, byte[] iv, String algorithm) throws GeneralSecurityException {

	        Cipher cipher = Cipher.getInstance(algorithm);

	        cipher.init(2, key, new IvParameterSpec(iv));

	        return cipher.doFinal(input);

	    }
	 
	    public PrivateKey getPrivate(byte[] privateKey, String mode) throws NoSuchAlgorithmException, InvalidKeySpecException {

	        return KeyFactory.getInstance(mode).generatePrivate(new PKCS8EncodedKeySpec(Base64.getDecoder().decode(privateKey)));

	    }
	 
	    public byte[] extractBytes(byte[] input, int startIndex, int endIndex) {

	        return Arrays.copyOfRange(input, startIndex, endIndex);

	    }
	 
	    public static String convertResponseToString(InputStream inputStream) throws IOException {

	        StringWriter writer = new StringWriter();

	        try (BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream))) {

	            String line;

	            while ((line = reader.readLine()) != null) {

	                writer.write(line);

	            }

	        }

	        String responseString = writer.toString();

	        System.out.println("Response String ===> " + responseString);

	        return responseString;

	    }
	    
	    public static String getAssymetricKey(String keyName) throws IOException  {
	        Properties properties = new Properties();
	        String filePath = Constants.getFilepath()  + File.separator + "SVT_LSM_MON_Keys.properties";
//	        String filePath = "D:\\Intekhab\\Weblogic\\user_projects\\domains\\base_domain\\SRVConfig\\CPCS_MDM_Keys.properties";
	        try (FileInputStream fis = new FileInputStream(filePath)) {
	            properties.load(fis);
	        }
	        String encodedKey = properties.getProperty(keyName);
	        return encodedKey;
	    }

}
