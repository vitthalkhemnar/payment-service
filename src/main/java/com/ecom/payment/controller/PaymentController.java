package com.ecom.payment.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.payment.dto.OrderRequest;
import com.ecom.payment.dto.PaymentVerificationRequest;
import com.ecom.payment.service.RazorpayService;
import com.razorpay.RazorpayException;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
@Tag(name = "Payment Processing", description = "Endpoints for Razorpay payment initialization and signature verification")
@SecurityRequirement(name = "bearerAuth")
public class PaymentController {

	private final RazorpayService razorpayService;

	@Operation(
		summary = "Create Razorpay payment order",
		description = "Initializes a payment order on Razorpay for the specified amount and returns the Razorpay order payload JSON."
	)
	@PostMapping("/order")
	public ResponseEntity<String> createOrder(@RequestBody OrderRequest request) throws RazorpayException {
		String orderResponse = razorpayService.createOrder(request);
		return ResponseEntity.ok(orderResponse);
	}

	@Operation(
		summary = "Verify Razorpay payment",
		description = "Verifies the cryptographic payment signature returned by the Razorpay checkout against the payment secret."
	)
	@PostMapping("/verify")
	public ResponseEntity<Boolean> verifyPayment(@RequestBody PaymentVerificationRequest request) {
		boolean isVerified = razorpayService.verifyPayment(request);

		return ResponseEntity.ok(isVerified);
	}
}
