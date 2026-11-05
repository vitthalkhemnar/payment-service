package com.ecom.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record PaymentVerificationRequest(
	    String razorpayOrderId,
	    String razorpayPaymentId,
	    String razorpaySignature,
	    Long orderId
	) {
	public PaymentVerificationRequest(String razorpayOrderId, String razorpayPaymentId, String razorpaySignature) {
		this(razorpayOrderId, razorpayPaymentId, razorpaySignature, null);
	}
}
