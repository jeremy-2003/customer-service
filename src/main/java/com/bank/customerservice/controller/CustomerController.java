package com.bank.customerservice.controller;

import com.bank.customerservice.dto.BaseResponse;
import com.bank.customerservice.model.Customer;
import com.bank.customerservice.model.CustomerType;
import com.bank.customerservice.service.CustomerService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/customers")
public class CustomerController {
    private final CustomerService customerService;

    public CustomerController(CustomerService customerService){
        this.customerService = customerService;
    }

    @PostMapping
    public Mono<ResponseEntity<BaseResponse<Customer>>> createCustomer(@RequestBody Customer customer){
        return customerService.createCustomer(customer)
                .map(savedCustomer -> ResponseEntity
                        .created(URI.create("/api/customer/" + savedCustomer.getId()))
                        .body(BaseResponse.<Customer>builder()
                                .status(HttpStatus.CREATED.value())
                                .message("Customer successfully created")
                                .data(savedCustomer)
                                .build()))
                .onErrorResume(e -> Mono.just(ResponseEntity
                        .status(HttpStatus.BAD_REQUEST)
                        .body(BaseResponse.<Customer>builder()
                                .status(HttpStatus.BAD_REQUEST.value())
                                .message(e.getMessage())
                                .data(null)
                                .build())));
    }
    @GetMapping
    public Mono<ResponseEntity<BaseResponse<List<Customer>>>> getAllCustomers(){
        return customerService.getAllCustomers()
                        .collectList()
                        .map(customers -> ResponseEntity.ok(
                                BaseResponse.<List<Customer>>builder()
                                    .status(HttpStatus.OK.value())
                                    .message("Customers retrieved successfully")
                                    .data(customers)
                                    .build()
                        ));
    }
    @GetMapping("/{id}")
    public Mono<ResponseEntity<BaseResponse<Customer>>> getCustomerById(@PathVariable String id){
        return customerService.getCustomerById(id)
                .map(customer -> ResponseEntity.ok(
                        BaseResponse.<Customer>builder()
                                .status(HttpStatus.OK.value())
                                .message("Customer details retrieved successfully")
                                .data(customer)
                                .build()))
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.<Customer>builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("Customer not found")
                                .data(null)
                                .build())));
    }
    @GetMapping("/type/{type}")
    public Mono<ResponseEntity<BaseResponse<List<Customer>>>> getCustomerByType(@PathVariable CustomerType type){
        return customerService.getCustomerByType(type)
                .collectList()
                .map(customers -> ResponseEntity.ok(
                        BaseResponse.<List<Customer>>builder()
                                .status(HttpStatus.OK.value())
                                .message("Customers retrieved successfully")
                                .data(customers)
                                .build()
                ));
    }
    @PutMapping("/{id}")
    public Mono<ResponseEntity<BaseResponse<Customer>>> updateCustomer(@PathVariable String id, @RequestBody Customer customer){
        return customerService.updateCustomer(id, customer)
                .map(updatedCustomer -> ResponseEntity.ok(
                        BaseResponse.<Customer>builder()
                                .status(HttpStatus.OK.value())
                                .message("Customer successfully update")
                                .data(updatedCustomer)
                                .build()))
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.<Customer>builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("Customer not found")
                                .data(null)
                                .build())));
    }
    @DeleteMapping("/{id}")
    public Mono<ResponseEntity<BaseResponse<Customer>>> deleteCustomer(@PathVariable String id){
        return customerService.deleteCustomer(id)
                .map(updatedCustomer -> ResponseEntity.ok(
                        BaseResponse.<Customer>builder()
                                .status(HttpStatus.OK.value())
                                .message("Customer successfully deleted (soft delete)")
                                .data(updatedCustomer)
                                .build()))
                .switchIfEmpty(Mono.just(ResponseEntity
                        .status(HttpStatus.NOT_FOUND)
                        .body(BaseResponse.<Customer>builder()
                                .status(HttpStatus.NOT_FOUND.value())
                                .message("Customer not found")
                                .data(null)
                                .build())));
    }
}
