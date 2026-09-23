package com.ecom.payment.service;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.ecom.payment.dto.OrderRequest;
import com.ecom.payment.dto.PaymentVerificationRequest;
import com.razorpay.RazorpayException;

import lombok.RequiredArgsConstructor;

@CrossOrigin("*")
@RestController
@RequiredArgsConstructor
@RequestMapping("/payment")
public class PaymentController {

	private final RazorpayService razorpayService;

	@PostMapping("/order")
	public ResponseEntity<?> createOrder(@RequestBody OrderRequest request) throws RazorpayException {
		String orderResponse = razorpayService.createOrder(request);
		return ResponseEntity.ok(orderResponse);
	}

	@PostMapping("/verify")
	public ResponseEntity<?> verifyPayment(@RequestBody PaymentVerificationRequest request) {
		boolean isVerified = razorpayService.verifyPayment(request);

		return ResponseEntity.ok(isVerified);
	}
}
