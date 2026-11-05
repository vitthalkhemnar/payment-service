package com.ecom.payment.dto;

public record PaymentFailedEvent(
		Long orderId,
		String razorpayOrderId,
		String reason
) {}
