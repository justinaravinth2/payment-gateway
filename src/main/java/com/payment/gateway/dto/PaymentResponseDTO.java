package com.payment.gateway.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PaymentResponseDTO {
	
	private String MerchantRefNo;
	
	private String UcoPaymentRefNo;
	
	
	private String Amount;
	
	private String TxnStatus;
	
	private String BankMessage;

}
