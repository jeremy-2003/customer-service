package com.bank.customerservice.repository;

import com.bank.customerservice.model.Customer;
import com.bank.customerservice.model.CustomerType;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface CustomerRepository extends ReactiveMongoRepository<Customer, String> {
    Flux<Customer> findByCustomerType(CustomerType customerType);
    Mono<Customer> findByDocumentNumber(String documentNumber);
}
