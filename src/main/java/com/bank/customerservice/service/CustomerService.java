package com.bank.customerservice.service;

import com.bank.customerservice.model.Customer;
import com.bank.customerservice.model.CustomerType;
import com.bank.customerservice.repository.CustomerRepository;
import net.bytebuddy.asm.Advice;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

@Service
public class CustomerService {
    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Mono<Customer> createCustomer(Customer customer){
        return customerRepository.findByDocumentNumber(customer.getDocumentNumber())
                        .flatMap(existingCustomer -> Mono.error(new RuntimeException("Customer with this document number already exists")))
                        .cast(Customer.class)
                        .switchIfEmpty(
                                Mono.defer(() -> {
                                    customer.setCreatedAd(LocalDateTime.now());
                                    customer.setModifiedAd(null);
                                    customer.setStatus("ACTIVE");
                                    return customerRepository.save(customer);
                                })
                        );
    }
    public Flux<Customer> getAllCustomers(){
        return customerRepository.findAll();
    }
    public Mono<Customer> getCustomerByDocumentNumber(String Id){
        return customerRepository.findByDocumentNumber(Id);
    }
    public Flux<Customer> getCustomerByType(CustomerType type){
        return customerRepository.findByCustomerType(type);
    }
    public Mono<Customer> updateCustomer(String Id, Customer customer){
        return customerRepository.findByDocumentNumber(Id)
                .flatMap(existingCustomer -> {
                   Customer updateCustomer = Customer.builder()
                           .id(existingCustomer.getId())
                           .fullName(customer.getFullName())
                           .documentNumber(existingCustomer.getDocumentNumber())
                           .customerType(customer.getCustomerType())
                           .email(customer.getEmail())
                           .phone(customer.getPhone())
                           .createdAd(existingCustomer.getCreatedAd())
                           .modifiedAd(LocalDateTime.now())
                           .status(existingCustomer.getStatus())
                           .build();
                   return customerRepository.save(updateCustomer);
                });
    }
    public Mono<Customer> deleteCustomer(String id){
        return customerRepository.findByDocumentNumber(id)
                .flatMap(existingCustomer ->{
                    existingCustomer.setStatus("DELETED");
                    existingCustomer.setModifiedAd(LocalDateTime.now());
                    return customerRepository.save(existingCustomer);
                });
    }
}
