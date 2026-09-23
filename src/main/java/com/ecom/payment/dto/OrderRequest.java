package com.ecom.payment.dto;

import java.math.BigDecimal;

public record OrderRequest(
		BigDecimal amount,
		Long orderId
	) {}
