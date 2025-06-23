package com.payment.gateway.service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.JsonProcessingException;

@Service
public interface StudentService {

	Map<String, Object> createPayment() throws JsonProcessingException;

	Map<String, Object> decryptAndSave(String encdata);

}
