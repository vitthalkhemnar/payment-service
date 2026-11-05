package com.ecom.payment.dto;

public record PaymentSuccessEvent(
		Long orderId,
		String razorpayOrderId,
		String razorpayPaymentId,
		String status
) {}
