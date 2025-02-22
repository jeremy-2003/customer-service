package com.bank.customerservice.repository;

import com.bank.customerservice.model.customer.Customer;
import com.bank.customerservice.model.customer.CustomerType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import reactor.core.publisher.Flux;
import reactor.test.StepVerifier;

import java.time.LocalDateTime;

@DataMongoTest
@ExtendWith(SpringExtension.class)
@ExtendWith(MockitoExtension.class)
class CustomerRepositoryTest {
    @Autowired
    private CustomerRepository customerRepository;
    @BeforeEach
    void setUp() {
        Customer customer1 = Customer.builder()
                .id("1")
                .fullName("John Doe")
                .documentNumber("87654321")
                .customerType(CustomerType.PERSONAL)
                .email("john@example.com")
                .phone("987654321")
                .status("ACTIVE")
                .createdAd(LocalDateTime.now())
                .modifiedAd(LocalDateTime.now())
                .isVip(true)
                .isPym(false)
                .build();

        Customer customer2 = Customer.builder()
                .id("2")
                .fullName("Jane Smith")
                .documentNumber("12345678")
                .customerType(CustomerType.BUSINESS)
                .email("jane@example.com")
                .phone("123456789")
                .status("ACTIVE")
                .createdAd(LocalDateTime.now())
                .modifiedAd(LocalDateTime.now())
                .isVip(false)
                .isPym(true)
                .build();

        customerRepository.deleteAll()
                .thenMany(Flux.just(customer1, customer2))
                .flatMap(customerRepository::save)
                .blockLast();
    }
    @Test
    void testFindByCustomerType() {
        StepVerifier.create(customerRepository.findByCustomerType(CustomerType.PERSONAL))
                .expectNextMatches(customer -> customer.getFullName().equals("John Doe"))
                .verifyComplete();
    }
    @Test
    void testFindByDocumentNumber() {
        StepVerifier.create(customerRepository.findByDocumentNumber("12345678"))
                .expectNextMatches(customer -> customer.getFullName().equals("Jane Smith"))
                .verifyComplete();
    }
    @Test
    void testFindById() {
        StepVerifier.create(customerRepository.findById("1"))
                .expectNextMatches(customer -> customer.getFullName().equals("John Doe"))
                .verifyComplete();
    }
}