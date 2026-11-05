package com.ecom.payment.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecom.payment.dto.OrderRequest;
import com.ecom.payment.dto.PaymentFailedEvent;
import com.ecom.payment.dto.PaymentSuccessEvent;
import com.ecom.payment.dto.PaymentVerificationRequest;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class RazorpayService {

	@Value("${razorpay.key.id}")
	private String keyId;

	@Value("${razorpay.key.secret}")
	private String keySecret;

	private final PaymentEventProducer paymentEventProducer;

	public String createOrder(OrderRequest req) throws RazorpayException {
		RazorpayClient client = new RazorpayClient(keyId, keySecret);

		JSONObject options = new JSONObject();
		options.put("amount", req.amount().movePointRight(2).intValue()); // Razorpay expects amount in paise (1 INR = 100 paise)
		options.put("currency", "INR");
		options.put("receipt", "txn_" + req.orderId());

		Order order = client.orders.create(options);
		return order.toString();
	}

	public boolean verifyPayment(PaymentVerificationRequest req) {
		Long orderId = resolveOrderId(req);
		try {
			String payload = req.razorpayOrderId() + "|" + req.razorpayPaymentId();
			boolean isValid = Utils.verifySignature(payload, req.razorpaySignature(), keySecret);

			if (isValid) {
				log.info("Payment signature verified successfully for order #{} (Razorpay Order: {})", orderId, req.razorpayOrderId());
				if (orderId != null) {
					paymentEventProducer.publishPaymentSuccess(new PaymentSuccessEvent(
							orderId,
							req.razorpayOrderId(),
							req.razorpayPaymentId(),
							"SUCCESS"
					));
				} else {
					log.warn("Payment verified but orderId could not be determined for razorpayOrderId {}", req.razorpayOrderId());
				}
				return true;
			} else {
				log.warn("Payment signature verification failed for order #{} (Razorpay Order: {})", orderId, req.razorpayOrderId());
				if (orderId != null) {
					paymentEventProducer.publishPaymentFailed(new PaymentFailedEvent(
							orderId,
							req.razorpayOrderId(),
							"Signature mismatch"
					));
				}
				return false;
			}
		} catch (Exception e) {
			log.error("Exception during payment verification for razorpayOrderId {}: {}", req.razorpayOrderId(), e.getMessage(), e);
			if (orderId != null) {
				paymentEventProducer.publishPaymentFailed(new PaymentFailedEvent(
						orderId,
						req.razorpayOrderId(),
						e.getMessage()
				));
			}
			return false;
		}
	}

	public Long resolveOrderId(PaymentVerificationRequest req) {
		if (req.orderId() != null) {
			return req.orderId();
		}
		try {
			if (req.razorpayOrderId() != null && !req.razorpayOrderId().isBlank()) {
				RazorpayClient client = new RazorpayClient(keyId, keySecret);
				Order rOrder = client.orders.fetch(req.razorpayOrderId());
				if (rOrder != null && rOrder.has("receipt")) {
					String receipt = rOrder.get("receipt");
					if (receipt != null && receipt.startsWith("txn_")) {
						return Long.parseLong(receipt.substring(4));
					} else if (receipt != null) {
						return Long.parseLong(receipt);
					}
				}
			}
		} catch (Exception e) {
			log.warn("Could not extract orderId from Razorpay order {}: {}", req.razorpayOrderId(), e.getMessage());
		}
		return null;
	}
}
