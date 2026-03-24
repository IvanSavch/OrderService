package com.innowise.orderservice.kafka;

import com.innowise.orderservice.exception.InvalidStatusException;
import com.innowise.orderservice.exception.PaymentNotFoundException;
import com.innowise.orderservice.model.Payment;
import com.innowise.orderservice.model.entity.Order;
import com.innowise.orderservice.service.OrderService;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class PaymentKafkaConsumer {
    private final OrderService orderService;

    public PaymentKafkaConsumer(OrderService orderService) {
        this.orderService = orderService;
    }

    @KafkaListener(topics = "CREATE_PAYMENT")
    public void consumePayment(ConsumerRecord<String, Payment> payment) {
        Payment value = payment.value();
        if (value == null) {
            throw new PaymentNotFoundException("Payment not found");
        }
        if (value.getStatus().equals(Payment.Status.FAILED.name())) {
            orderService.updateStatusById(value.getOrderId(), Order.OrderStatus.CANCELLED);
        } else if (value.getStatus().equals(Payment.Status.SUCCESS.name())) {
            orderService.updateStatusById(value.getOrderId(), Order.OrderStatus.PAID);
        } else {
            throw new InvalidStatusException("Invalid status from payment service");
        }
    }
}

