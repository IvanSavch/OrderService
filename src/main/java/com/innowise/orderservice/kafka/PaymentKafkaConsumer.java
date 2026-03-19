package com.innowise.orderservice.kafka;

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
    public void consumeOrder(ConsumerRecord<String, Payment> payment){
        Payment value = payment.value();
        if (value.getStatus().equals(Payment.Status.FAILED.name())){
            orderService.updateStatusById(value.getOrderId(), Order.OrderStatus.CANCELLED);
        }else {orderService.updateStatusById(value.getOrderId(), Order.OrderStatus.PAID);}
    }
}
