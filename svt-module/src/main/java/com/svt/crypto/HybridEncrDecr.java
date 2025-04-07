package com.svt.crypto;

import com.svt.utils.common.Constants;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.core.JsonGenerator;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.KeyFactory;
import java.security.NoSuchAlgorithmException;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.Properties;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HybridEncrDecr {
	
	private static final Logger logger = LoggerFactory.getLogger(HybridEncrDecr.class);
	
	public static String getEncryptedRequestPacket(String requestPacket) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
      
		Map<String, Object> finalMap = new HashMap<>();
        Gson gson = new GsonBuilder().disableHtmlEscaping().create();
        SecretKey secretKey = generateSecretKey(generate16DigitRandomNumber());
        byte[] ivBytes = generateIv();
        finalMap.put("requestId", "");
        finalMap.put("service", "SVT");
        finalMap.put("encryption", true);
        finalMap.put("encryptedKey", getEncodedData(encryptRSA(secretKey.getEncoded(), (PublicKey) getAsymmetricKey("RESPONSE_PUBLIC_KEY"))));
        finalMap.put("oaepHashingAlgorithm", "NONE");
        finalMap.put("iv", getEncodedData(ivBytes));
        finalMap.put("encryptedData", getEncodedData(encryptAES(requestPacket.getBytes(), secretKey, new IvParameterSpec(ivBytes))));
        finalMap.put("clientInfo", "");
        finalMap.put("optionalParam", "");
        logger.info("In getEncryptedRequestPacket before to json finalMap"+ finalMap);
        return gson.toJson(finalMap);
        
    }

    public static String getDecryptedResponsePacket(String encryptedResponsePacket) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        JsonObject jsonObject = new JsonParser().parse(encryptedResponsePacket).getAsJsonObject();
        byte[] decryptedAESKey = decryptRSA(getDecodedData(jsonObject.get("encryptedKey").getAsString()),
                (PrivateKey) getAsymmetricKey("RESPONSE_PRIVATE_KEY")),
                decryptedData = decryptAES(getDecodedData(jsonObject.get("encryptedData").getAsString()),
                        new SecretKeySpec(decryptedAESKey, 0, decryptedAESKey.length, "AES"),
                        new IvParameterSpec(getDecodedData(jsonObject.get("iv").getAsString())));
        return new String(decryptedData).substring(15);
    }

    private static Key getAsymmetricKey(String keyName) throws IOException, NoSuchAlgorithmException, InvalidKeySpecException {
        Properties properties = new Properties();
        String filePath = Constants.getFilepath()  + File.separator + "SVT_LSM_MON_Keys.properties";
//        String filePath = "D:\\Intekhab\\Weblogic\\user_projects\\domains\\base_domain\\SRVConfig\\CPCS_MDM_Keys.properties";
        try (FileInputStream fis = new FileInputStream(filePath)) {
            properties.load(fis);
        }
        String encodedKey = properties.getProperty(keyName);
        return (keyName.contains("PUBLIC_KEY")) ? getPublicKey(encodedKey) : getPrivateKey(encodedKey);
    }

    private static SecretKey generateSecretKey(String randomNumber) {
        return new SecretKeySpec(randomNumber.getBytes(), "AES");
    }

    public static byte[] encryptAES(byte[] requestPaacket, SecretKey secretKey, IvParameterSpec iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, iv);
        return cipher.doFinal(requestPaacket);
    }

    public static byte[] decryptAES(byte[] encryptedPayload, SecretKey secretKey, IvParameterSpec iv) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, iv);
        return cipher.doFinal(encryptedPayload);
    }

    public static byte[] encryptRSA(byte[] encodedSecretKey, PublicKey publicKey) throws NoSuchAlgorithmException, NoSuchPaddingException, InvalidKeyException, InvalidAlgorithmParameterException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);
        return cipher.doFinal(encodedSecretKey);
    }

    public static byte[] decryptRSA(byte[] input, PrivateKey privateKey) throws InvalidKeyException, NoSuchAlgorithmException, NoSuchPaddingException, IllegalBlockSizeException, BadPaddingException {
        Cipher cipher = Cipher.getInstance("RSA/ECB/PKCS1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        return cipher.doFinal(input);
    }

    private static byte[] generateIv() {
        byte[] ivBytes = new byte[16];
        new SecureRandom().nextBytes(ivBytes);
        return ivBytes;
    }

    private static String getEncodedData(byte[] byteArray) {
        return Base64.getEncoder().encodeToString(byteArray);
    }

    private static byte[] getDecodedData(String data) {
        return Base64.getDecoder().decode(data);
    }

    private static PublicKey getPublicKey(String encodedPublicKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(getDecodedData(encodedPublicKey)));
    }

    private static PrivateKey getPrivateKey(String encodedPrivateKey) throws NoSuchAlgorithmException, InvalidKeySpecException {
        return KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(getDecodedData(encodedPrivateKey)));
    }

    private static String generate16DigitRandomNumber() {
        SecureRandom random = new SecureRandom();
        long min = 1_000_000_000_000_000L, max = 9_999_999_999_999_999L;
        long number = min + ((long) (random.nextDouble() * (max - min + 1)));
        return Long.toString(number);
    }
    
//  public static void main(String[] args) throws Exception {
////String requestPacket = "abcd";
////String finalRequestPacket = getEncryptedRequestPacket(requestPacket);
////System.out.println("getEncryptedRequestPacket : " + finalRequestPacket);
//String encryptedRresponsePacket = new String(Files.readAllBytes(Paths.get(
//        "D:\\Intekhab\\Weblogic\\user_projects\\domains\\base_domain\\CLB\\CPCS_Response_Encrypted.txt")));
//String getDecryptedResponsePacket = getDecryptedResponsePacket(encryptedRresponsePacket);
//System.out.println("\n\n\n getDecryptedResponsePacket : " + getDecryptedResponsePacket);
//}

}
