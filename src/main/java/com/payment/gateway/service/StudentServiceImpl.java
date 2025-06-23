package com.payment.gateway.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;
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
			String mprn = "68568686865565^PG35695454646548^300.00^SUCCESS@S30315238|DCRD|NA|UCO Bank|NA|SUCCESS|SUCCESS";
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
	
	
	public static String decryptForUcoBank(String hexEncodedBase64, String plainTextKey) {
	    try {
	        // Step 1: Convert hex back to Base64
	        StringBuilder base64Builder = new StringBuilder();
	        for (int i = 0; i < hexEncodedBase64.length(); i += 2) {
	            String hexByte = hexEncodedBase64.substring(i, i + 2);
	            int decimal = Integer.parseInt(hexByte, 16);
	            base64Builder.append((char) decimal);
	        }
	        String base64Encoded = base64Builder.toString();

	        // Step 2: Base64 decode
	        byte[] encryptedBytes = Base64.getDecoder().decode(base64Encoded);

	        // Step 3: AES Decryption
	        byte[] keyBytes = plainTextKey.getBytes(StandardCharsets.UTF_8);
	        SecretKey aesKey = new SecretKeySpec(keyBytes, "AES");

	        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, aesKey);
	        byte[] decryptedBytes = cipher.doFinal(encryptedBytes);

	        // Step 4: Return the decrypted text
	        return new String(decryptedBytes, StandardCharsets.UTF_8);

	    } catch (Exception e) {
	        e.printStackTrace();
	        return "Decryption error: " + e.getMessage();
	    }
	}



	@Override
	public Map<String, Object> decryptAndSave(String encdata) {
	    Map<String, Object> result = new HashMap<>();
	    String plainTextKey = "MU(a&_)fpn2p7R!d"; // Your actual AES key

	    try {
	        String decryptedData = decryptForUcoBank(encdata, plainTextKey);
	        System.out.println("\nDecrypted raw data: " + decryptedData);

	        // Split the data by '@' to separate main fields from the transaction response
	        String[] atParts = decryptedData.split("@", 2);
	        String mainPart = atParts[0];
	        String txnResponse = (atParts.length > 1) ? atParts[1] : ""; // Will be after the '@' if present

	        // Now split the main part by '^'
	        String[] parts = mainPart.split("\\^");
	        System.out.println("\nNumber of Parts (main): " + parts.length);
	        for (int i = 0; i < parts.length; i++) {
	            System.out.println("Part[" + i + "]: " + parts[i]);
	        }

	        if (parts.length >= 4) {
	            String merchantRefNo = parts[0];
	            String ucoRefNo = parts[1];
	            String amount = parts[2];
	            String txnStatus = parts[3];

	            // Printing
	            System.out.println("\nMERCHANT REF NO: " + merchantRefNo);
	            System.out.println("UCO PAYMENT SOLUTION REF NO: " + ucoRefNo);
	            System.out.println("AMOUNT: " + amount);
	            System.out.println("TXN STATUS: " + txnStatus);
	            if (!txnResponse.isEmpty()) {
	                System.out.println("Transaction Response : " + txnResponse);
	            }

	            // Putting into result
	            result.put("merchantRefNo", merchantRefNo);
	            result.put("ucoRefNo", ucoRefNo);
	            result.put("amount", amount);
	            result.put("txnStatus", txnStatus);
	            if (!txnResponse.isEmpty()) {
	                result.put("txnResponse", txnResponse);
	            }
	        } else {
	            result.put("error", "Invalid response format. Not enough sections found.");
	        }

	    } catch (Exception e) {
	        e.printStackTrace();
	        result.put("error", e.getMessage());
	    }

	    return result;
	}

}
