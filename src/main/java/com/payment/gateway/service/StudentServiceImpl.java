package com.payment.gateway.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public class StudentServiceImpl implements StudentService {

	

	@Override
	public Map<String, Object> createPayment() throws JsonProcessingException {
		try {
			String applicationid = "001";
			String mprn = "123456";
			int amount = 25;
			String amt = String.valueOf(amount);
//			String passwordold = "0wvgd620X4nUObJsNrO/PVUS2vovQm4Z6IGsOVSEu/k="; // your base64 encoded AES key
			String password = "MU(a&_)fpn2p7R!d";
			String sname = "Justin";
			String smobno = "8526330797";
			String semailid = "justinaravinth2@gmail.com";
			String ppCode = "FIRSTPUC";

			// Encrypt values
			String encryptedMprn = encryptForUcoBank(mprn, password);
			String encryptedAmount = encryptForUcoBank(amt, password);

			System.out.println("Encrypted MPRN old: " + encryptedMprn);

			System.out.println("Encrypted Amount old: " + encryptedAmount);

			// Prepare URL parameters
			String params = "MCODE=MCPUCAPPFC" + "&MPRN=" + encryptedMprn + "&MTAMT=" + encryptedAmount + "&SRN=001"
					+ "&SNAME=" + sname + "&SMOBNO=" + smobno + "&SEMAILID=" + semailid + "&PPCODE=" + ppCode;
			
			String urlString = "https://uatucoebanking.in/corp/CustomSmartPayInwardController"
			        + "?MCODE=MCPUCAPPFC"
			        + "&MPRN=" + encryptedMprn
			        + "&MTAMT=" + encryptedAmount
			        + "&SRN=" + applicationid
			        + "&SNAME=" + sname
			        + "&SMOBNO=" + smobno
			        + "&SEMAILID=" +semailid
			        + "&PPCODE=" + ppCode;

			System.out.println("Full URL: " + urlString);
			
			System.out.println("url: "+params);

			// Call API
			URL url = new URL("https://uatucoebanking.in/corp/CustomSmartPayInwardController");
			HttpURLConnection conn = (HttpURLConnection) url.openConnection();
			conn.setRequestMethod("POST");
			conn.setDoOutput(true);
			conn.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

			OutputStream os = conn.getOutputStream();
			os.write(params.getBytes());
			os.flush();
			os.close();

			int responseCode = conn.getResponseCode();
			System.out.println("Response Code : " + responseCode);
			// Read API response
			BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
			String inputLine;
			StringBuilder responseContent = new StringBuilder();

			while ((inputLine = in.readLine()) != null) {
				responseContent.append(inputLine);
			}
			in.close();

			System.out.println("API Response: " + responseContent.toString());

			conn.disconnect();

			// Prepare response map
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Success");
			response.put("apiResponse", responseContent.toString());
			return response;

		} catch (Exception e) {
			e.printStackTrace();
			Map<String, Object> response = new HashMap<>();
			response.put("message", "Error");
			response.put("error", e.getMessage());
			return response;
		}
	}

	// Encrypt method using symmetric encryption (AES)
	public String encryptBySymmetricKey(String textToEncrypt, String decryptedSek) {
		try {
			// Decode the secret key (AES key) from Base64 string
			byte[] sekByte = Base64.getDecoder().decode(decryptedSek);
			SecretKey aesKey = new SecretKeySpec(sekByte, "AES");

			// Initialize the AES cipher in encryption mode with PKCS5Padding
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);

			// Encrypt the text (the name or any string) as bytes
			byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

			// Convert encrypted bytes to HEX string
			StringBuilder hexString = new StringBuilder();
			for (byte b : encryptedBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString().toUpperCase(); // final HEX string in UPPERCASE
		} catch (Exception e) {
			e.printStackTrace();
			return "Encryption error: " + e.getMessage();
		}
	}

	public String encryptBySymmetricKeynew(String textToEncrypt, String plainTextKey) {
		try {
			// Convert plain text key to byte array (since it's 16 chars => 128-bit AES key)
			byte[] keyBytes = plainTextKey.getBytes(StandardCharsets.UTF_8);
			SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

			// Initialize cipher
			Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
			cipher.init(Cipher.ENCRYPT_MODE, aesKey);

			// Encrypt
			byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

			// Convert to HEX string
			StringBuilder hexString = new StringBuilder();
			for (byte b : encryptedBytes) {
				String hex = Integer.toHexString(0xff & b);
				if (hex.length() == 1)
					hexString.append('0');
				hexString.append(hex);
			}
			return hexString.toString().toUpperCase();

		} catch (Exception e) {
			e.printStackTrace();
			return "Encryption error: " + e.getMessage();
		}
	}
	
	public String encryptUcoFormat(String textToEncrypt, String plainTextKey) {
	    try {
	        byte[] keyBytes = plainTextKey.getBytes(StandardCharsets.UTF_8);
	        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

	        byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

	        // Step 2: Base64 encode
	        String base64Encoded = Base64.getEncoder().encodeToString(encryptedBytes);

	        // Step 3: Convert Base64 string to HEX
	        StringBuilder hexString = new StringBuilder();
	        for (byte b : base64Encoded.getBytes(StandardCharsets.UTF_8)) {
	            String hex = Integer.toHexString(0xff & b);
	            if (hex.length() == 1)
	                hexString.append('0');
	            hexString.append(hex);
	        }
	        return hexString.toString().toUpperCase();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Encryption error: " + e.getMessage();
	    }
	}
	
//	public static String encryptForUcoBank(String textToEncrypt, String plainTextKey) {
//        try {
//            // Step 1: Prepare key (16 char -> 128-bit key)
//            byte[] keyBytes = plainTextKey.getBytes(StandardCharsets.UTF_8);
//            SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");
//
//            // Step 2: Prepare cipher (AES/ECB/PKCS5Padding)
//            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
//            cipher.init(Cipher.ENCRYPT_MODE, aesKey);
//
//            // Step 3: Encrypt
//            byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));
//
//            // Step 4: Base64 encode
//            String base64Encoded = Base64.getEncoder().encodeToString(encryptedBytes);
//
//            // Step 5: Convert Base64 string to HEX
//            StringBuilder hexString = new StringBuilder();
//            for (byte b : base64Encoded.getBytes(StandardCharsets.UTF_8)) {
//                String hex = Integer.toHexString(0xff & b);
//                if (hex.length() == 1)
//                    hexString.append('0');
//                hexString.append(hex);
//            }
//
//            return hexString.toString().toUpperCase();
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return "Encryption error: " + e.getMessage();
//        }
//    }

	public static String encryptForUcoBank(String textToEncrypt, String plainTextKey) {
	    try {
	        // Step 1: Prepare key (16 char -> 128-bit key)
	        byte[] keyBytes = plainTextKey.getBytes(StandardCharsets.UTF_8);
	        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

	        // Step 2: Prepare cipher (AES/ECB/PKCS5Padding)
	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, aesKey);

	        // Step 3: Encrypt
	        byte[] encryptedBytes = cipher.doFinal(textToEncrypt.getBytes(StandardCharsets.UTF_8));

	        // Step 4: Base64 encode the encrypted bytes
	        String base64Encoded = Base64.getEncoder().encodeToString(encryptedBytes);

	        // Step 5: Convert Base64 string into HEX string
	        StringBuilder hexString = new StringBuilder();
	        for (char ch : base64Encoded.toCharArray()) {
	            String hex = Integer.toHexString(ch);
	            if (hex.length() == 1)
	                hexString.append('0');
	            hexString.append(hex);
	        }

	        return hexString.toString().toUpperCase();

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Encryption error: " + e.getMessage();
	    }
	}

}
