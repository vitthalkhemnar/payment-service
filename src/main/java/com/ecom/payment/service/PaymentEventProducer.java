package com.ecom.payment.service;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

import com.ecom.payment.config.KafkaTopicConfig;
import com.ecom.payment.dto.PaymentFailedEvent;
import com.ecom.payment.dto.PaymentSuccessEvent;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentEventProducer {

	private final KafkaTemplate<String, Object> kafkaTemplate;

	public void publishPaymentSuccess(PaymentSuccessEvent event) {
		log.info("Publishing PaymentSuccessEvent for order: {}", event.orderId());
		try {
			kafkaTemplate.send(KafkaTopicConfig.PAYMENT_SUCCESS_TOPIC, String.valueOf(event.orderId()), event)
					.whenComplete((result, ex) -> {
						if (ex != null) {
							log.error("Failed to deliver PaymentSuccessEvent for order #{}: {}", event.orderId(), ex.getMessage());
						} else {
							log.info("Successfully published PaymentSuccessEvent for order #{} to partition {} at offset {}",
									event.orderId(),
									result.getRecordMetadata().partition(),
									result.getRecordMetadata().offset());
						}
					});
		} catch (Exception e) {
			log.error("Exception initiating Kafka send for PaymentSuccessEvent #{}: {}", event.orderId(), e.getMessage());
		}
	}

	public void publishPaymentFailed(PaymentFailedEvent event) {
		log.info("Publishing PaymentFailedEvent for order: {}", event.orderId());
		try {
			kafkaTemplate.send(KafkaTopicConfig.PAYMENT_FAILED_TOPIC, String.valueOf(event.orderId()), event)
					.whenComplete((result, ex) -> {
						if (ex != null) {
							log.error("Failed to deliver PaymentFailedEvent for order #{}: {}", event.orderId(), ex.getMessage());
						} else {
							log.info("Successfully published PaymentFailedEvent for order #{} to partition {} at offset {}",
									event.orderId(),
									result.getRecordMetadata().partition(),
									result.getRecordMetadata().offset());
						}
					});
		} catch (Exception e) {
			log.error("Exception initiating Kafka send for PaymentFailedEvent #{}: {}", event.orderId(), e.getMessage());
		}
	}
}
