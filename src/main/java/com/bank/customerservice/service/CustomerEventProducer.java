package com.bank.customerservice.service;

import com.bank.customerservice.model.Customer;
import com.bank.customerservice.model.CustomerType;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.common.protocol.types.Field;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;
@Slf4j
@Service
public class CustomerEventProducer {
    private final KafkaTemplate<String, Customer> kafkaTemplate;

    public CustomerEventProducer(KafkaTemplate<String, Customer> kafkaTemplate){
        this.kafkaTemplate = kafkaTemplate;
    }
    public void publishCustomerCreated(Customer customer){
        kafkaTemplate.send("customer-created", customer.getId(), customer)
                .addCallback(
                        result -> log.info("Message sent successfully"),
                        ex -> log.error("Failed to send message", ex));
    }
}