package com.bank.customerservice.event;

import com.bank.customerservice.model.customer.Customer;
import com.bank.customerservice.model.customer.CustomerType;
import org.apache.kafka.clients.producer.ProducerRecord;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.util.concurrent.SettableListenableFuture;

import java.time.LocalDateTime;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomerEventProducerTest {
    @Mock
    private KafkaTemplate<String, Customer> kafkaTemplate;
    @InjectMocks
    private CustomerEventProducer customerEventProducer;
    @Test
    void testPublishCustomerCreated() {
        Customer customer = Customer.builder()
                .id("1")
                .fullName("John Doe")
                .documentNumber("DOC123")
                .customerType(CustomerType.PERSONAL)
                .email("john@example.com")
                .phone("1234567890")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
        ProducerRecord<String, Customer> producerRecord = new ProducerRecord<>("customer-created",
                customer.getId(), customer);
        SettableListenableFuture<SendResult<String, Customer>> future = new SettableListenableFuture<>();
        future.set(new SendResult<>(producerRecord, null));
        when(kafkaTemplate.send(anyString(), anyString(), any(Customer.class))).thenReturn(future);
        customerEventProducer.publishCustomerCreated(customer);
        verify(kafkaTemplate, times(1)).send("customer-created", customer.getId(), customer);
    }
}