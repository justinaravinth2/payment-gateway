package com.payment.gateway.service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
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
			String mprn = "00114062025";
			String amount = "25.00";
			String password = "0wvgd620X4nUObJsNrO/PVUS2vovQm4Z6IGsOVSEu/k="; // your base64 encoded AES key
			String sname = "Justin";
			String smobno = "8526330797";
			String semailid = "justinaravinth2@gmail.com";
			String ppCode = "FIRSTPUC";

			// Encrypt values
			String encryptedMprn = encryptBySymmetricKey(mprn, password);
			String encryptedAmount = encryptBySymmetricKey(amount, password);

			// Prepare URL parameters
			String params = "MCODE=XYZ" + "&MPRN=" + URLEncoder.encode(encryptedMprn, "UTF-8") + "&MTAMT="
					+ URLEncoder.encode(encryptedAmount, "UTF-8") + "&SRN=20123" + "&SNAME="
					+ URLEncoder.encode(sname, "UTF-8") + "&SMOBNO=" + smobno + "&SEMAILID="
					+ URLEncoder.encode(semailid, "UTF-8") + "&PPCODE=" + URLEncoder.encode(ppCode, "UTF-8");

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

			// Return the encrypted bytes as a Base64-encoded string
			return Base64.getEncoder().encodeToString(encryptedBytes);
		} catch (Exception e) {
			e.printStackTrace();
			return "Encryption error: " + e.getMessage();
		}
	}

}
