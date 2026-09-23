package com.ecom.payment.service;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.ecom.payment.dto.OrderRequest;
import com.ecom.payment.dto.PaymentVerificationRequest;
import com.razorpay.Order;
import com.razorpay.RazorpayClient;
import com.razorpay.RazorpayException;
import com.razorpay.Utils;

@Service
public class RazorpayService {

	@Value("${razorpay.key.id}")
	private String keyId;

	@Value("${razorpay.key.secret}")
	private String keySecret;

	public String createOrder(OrderRequest req) throws RazorpayException {
		RazorpayClient client = new RazorpayClient(keyId, keySecret);

		JSONObject options = new JSONObject();
		options.put("amount", req.amount().intValue() * 100); // Razorpay expects amount in paise (1 INR = 100 paise)
		options.put("currency", "INR");
		options.put("receipt", "txn_" + req.orderId());

		Order order = client.orders.create(options);
		return order.toString();
	}

	public boolean verifyPayment(PaymentVerificationRequest req) {
		try {
            String payload = req.razorpayOrderId() + "|" + req.razorpayPaymentId();
            return Utils.verifySignature(payload, req.razorpaySignature(), keySecret);
        } catch (Exception e) {
            return false;
        }
	}
}
