package com.payment.gateway.exception;

import java.time.Instant;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ErrorResponse {
	
	private String message;
	private int status;
	private Instant timestamp;
	private String title;
	
}
