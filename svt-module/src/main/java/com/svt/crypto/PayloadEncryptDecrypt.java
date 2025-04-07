package com.svt.crypto;

import com.google.gson.Gson;
import java.security.GeneralSecurityException;
import java.util.HashMap;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class PayloadEncryptDecrypt {

	 static EncryptDecryptUtils utils = null;
	 
	    private static final Logger logger = LoggerFactory.getLogger(PayloadEncryptDecrypt.class);
	 
	    public static String encrypt(String req, String publicKey, String serviceType) {
	        try {
	            utils = new EncryptDecryptUtils();
	            byte[] plaintextKey = utils.generateSecretKey(16, "AES");
	            byte[] encodedEncryptedKey = utils.encodeToBase64(utils.encryptKey(plaintextKey, publicKey.getBytes(), "RSA", "RSA/ECB/PKCS1Padding"));
	            byte[] iv = utils.generateIv("AES");
	            byte[] encryptedContent = utils.encryptContent(req.getBytes(), iv, plaintextKey, "AES", "AES/CBC/PKCS5Padding");
	            byte[] encodedIvAndEncryptedContent = utils.encodeToBase64(utils.mergeTwoByteArrays(iv, encryptedContent));
	            String payload=buildPayload(encodedEncryptedKey, encodedIvAndEncryptedContent, serviceType);
	            return payload;
	        } catch (GeneralSecurityException e) {
	        	logger.info("The Exception in PayloadEncryptDecrypt "+ e);
	            return "<ErrorMsgReq>" + e.getMessage() + "</ErrorMsgReq>";
	        }
	    }
	 
	    public static String decrypt(String encryptedKey, String encodedIv, String encryptedContent, String privateKey) {
	        byte[] decodedIv;
	        byte[] encryptedDecodedContent;
	        try {
	            utils = new EncryptDecryptUtils();
	            byte[] decodedKey = utils.decodeFromBase64(encryptedKey.getBytes());
	            byte[] decryptedKey = utils.decryptKey(decodedKey, privateKey.getBytes(), "RSA", "RSA/ECB/PKCS1Padding");
	            byte[] decodedContent = utils.decodeFromBase64(encryptedContent.getBytes());
	            if ("".equalsIgnoreCase(encodedIv)) {
	                decodedIv = utils.extractBytes(decodedContent, 0, 16);
	                encryptedDecodedContent = utils.extractBytes(decodedContent, 16, decodedContent.length);
	            } else {
	                decodedIv = utils.decodeFromBase64(encodedIv.getBytes());
	                encryptedDecodedContent = utils.decodeFromBase64(encryptedContent.getBytes());
	            }
	            byte[] decryptedContent = utils.decryptContent(encryptedDecodedContent, decodedIv, decryptedKey, "AES", "AES/CBC/PKCS5Padding");
	            return new String(decryptedContent);
	        } catch (GeneralSecurityException e) {
	        	logger.info("The Exception in PayloadEncryptDecrypt "+ e);
	            return "<ErrorMsgRes>" + e.getMessage() + "</ErrorMsgRes>";
	        }
	    }
	    public static String buildPayload(byte[] encodedEncryptedKey, byte[] encodedIvAndEncryptedContent,String serviceType){
	        Map<String, String> requestMap = new HashMap<>();
	        requestMap.put("requestId", "");
	        requestMap.put("service", serviceType);
	        requestMap.put("encryptedKey", new String(encodedEncryptedKey));
	        requestMap.put("encryptedData", new String(encodedIvAndEncryptedContent));
	        requestMap.put("iv", "");
	        requestMap.put("encryption", "true");
	        requestMap.put("oaepHashingAlgorithm", "NONE");
	        requestMap.put("clientInfo", "");
	        requestMap.put("optionalParam", "");
	        return new Gson().toJson(requestMap);
	    }
	}
