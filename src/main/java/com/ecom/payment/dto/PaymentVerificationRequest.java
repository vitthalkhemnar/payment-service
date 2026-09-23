package com.ecom.payment.dto;

public record PaymentVerificationRequest(
	    String razorpayOrderId,
	    String razorpayPaymentId,
	    String razorpaySignature
	) {}