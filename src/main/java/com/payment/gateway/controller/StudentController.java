package com.payment.gateway.controller;

import java.util.HashMap;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.payment.gateway.common.CommonConstant;
import com.payment.gateway.common.UserConstants;
import com.payment.gateway.dto.ResponseDTO;
import com.payment.gateway.service.StudentService;

@RestController
@RequestMapping("/api/auth")
public class StudentController extends BaseController {

	@Autowired
	StudentService studentService;

	public static final Logger LOGGER = LoggerFactory.getLogger(StudentController.class);

	@PostMapping("/createPayment")
	public ResponseEntity<ResponseDTO> createEInvoice() {
		String methodName = "createEInvoice()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> irnResponseDTO = studentService.createPayment();
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Payment Successfully");
			responseObjectsMap.put("irnResponseDTO", irnResponseDTO);
			responseDTO = createServiceResponse(irnResponseDTO);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}

	@PostMapping("/decryptAndSave")
    public ResponseEntity<ResponseDTO> decryptAndSave(
            @RequestParam("encdata") String encdata) {
		String methodName = "createEInvoice()";
		LOGGER.debug(CommonConstant.STARTING_METHOD, methodName);
		String errorMsg = null;
		Map<String, Object> responseObjectsMap = new HashMap<>();
		ResponseDTO responseDTO = null;
		try {
			Map<String, Object> irnResponseDTO = studentService.decryptAndSave(encdata);
			responseObjectsMap.put(CommonConstant.STRING_MESSAGE, "Payment Successfully");
			responseObjectsMap.put("irnResponseDTO", irnResponseDTO);
			responseDTO = createServiceResponse(irnResponseDTO);
		} catch (Exception e) {
			errorMsg = e.getMessage();
			LOGGER.error(UserConstants.ERROR_MSG_METHOD_NAME, methodName, errorMsg);
		}
		LOGGER.debug(CommonConstant.ENDING_METHOD, methodName);
		return ResponseEntity.ok().body(responseDTO);
	}
	
}
