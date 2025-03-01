package com.bank.customerservice.service;

import com.bank.customerservice.event.CustomerEventProducer;
import com.bank.customerservice.model.customer.Customer;
import com.bank.customerservice.model.customer.CustomerType;
import com.bank.customerservice.repository.CustomerRepository;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;
    private final CustomerEventProducer eventProducer;
    public CustomerService(CustomerRepository customerRepository, CustomerEventProducer eventProducer) {
        this.customerRepository = customerRepository;
        this.eventProducer = eventProducer;
    }

    public Mono<Customer> createCustomer(Customer customer) {
        return customerRepository.findByDocumentNumber(customer.getDocumentNumber())
                        .flatMap(existingCustomer ->
                                Mono.error(new RuntimeException("Customer with this document number already exists")))
                        .cast(Customer.class)
                        .switchIfEmpty(
                                Mono.defer(() -> {
                                    customer.setCreatedAt(LocalDateTime.now());
                                    customer.setModifiedAt(null);
                                    customer.setStatus("ACTIVE");
                                    return customerRepository.save(customer);
                                })
                                .doOnSuccess(eventProducer::publishCustomerCreated));
    }

    public Flux<Customer> getAllCustomers() {
        return customerRepository.findAll();
    }
    public Mono<Customer> getCustomerById(String Id) {
        return customerRepository.findById(Id);
    }
    public Flux<Customer> getCustomerByType(CustomerType type) {
        return customerRepository.findByCustomerType(type);
    }
    public Mono<Customer> updateCustomer(String id, Customer customer) {
        return customerRepository.findById(id)
                .flatMap(existingCustomer -> {
                    Customer updateCustomer = Customer.builder()
                           .id(existingCustomer.getId())
                           .fullName(customer.getFullName())
                           .documentNumber(existingCustomer.getDocumentNumber())
                           .customerType(customer.getCustomerType())
                           .email(customer.getEmail())
                           .phone(customer.getPhone())
                           .createdAt(existingCustomer.getCreatedAt())
                           .modifiedAt(LocalDateTime.now())
                           .status(existingCustomer.getStatus())
                           .build();
                    return customerRepository.save(updateCustomer);
                });
    }
    public Mono<Customer> updateVipPymStatus(String idCustomer, boolean isVipPym) {
        return customerRepository.findById(idCustomer)
                .flatMap(existUser -> {
                    if (existUser.getCustomerType() == CustomerType.BUSINESS) {
                        existUser.setPym(isVipPym);
                    } else {
                        existUser.setVip(isVipPym);
                    }
                    return customerRepository.save(existUser);
                });
    }
    public Mono<Customer> deleteCustomer(String id) {
        return customerRepository.findById(id)
                .flatMap(existingCustomer -> {
                    existingCustomer.setStatus("DELETED");
                    existingCustomer.setModifiedAt(LocalDateTime.now());
                    return customerRepository.save(existingCustomer);
                });
    }
}
