package com.bank.customerservice.service;
import com.bank.customerservice.event.CustomerEventProducer;
import com.bank.customerservice.model.customer.Customer;
import com.bank.customerservice.model.customer.CustomerType;
import com.bank.customerservice.repository.CustomerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;
import java.time.LocalDateTime;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
class CustomerServiceTest {
    @Mock
    private CustomerRepository customerRepository;
    @Mock
    private CustomerEventProducer eventProducer;
    @InjectMocks
    private CustomerService customerService;
    private Customer customer;
    @BeforeEach
    void setUp() {
        customer = Customer.builder()
                .id("1")
                .fullName("John Doe")
                .documentNumber("DOC123")
                .customerType(CustomerType.PERSONAL)
                .email("john@example.com")
                .phone("1234567890")
                .status("ACTIVE")
                .createdAt(LocalDateTime.now())
                .build();
    }
    @Test
    void createCustomer_Success() {
        when(customerRepository.findByDocumentNumber(any()))
                .thenReturn(Mono.empty());
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Mono.just(customer));
        doNothing().when(eventProducer).publishCustomerCreated(any(Customer.class));
        StepVerifier.create(customerService.createCustomer(customer))
                .expectNext(customer)
                .verifyComplete();
        verify(customerRepository).findByDocumentNumber(customer.getDocumentNumber());
        verify(customerRepository).save(any(Customer.class));
        verify(eventProducer).publishCustomerCreated(any(Customer.class));
    }
    @Test
    void createCustomer_DuplicateDocument() {
        when(customerRepository.findByDocumentNumber(any()))
                .thenReturn(Mono.just(customer));
        StepVerifier.create(customerService.createCustomer(customer))
                .expectError(RuntimeException.class)
                .verify();
        verify(customerRepository).findByDocumentNumber(customer.getDocumentNumber());
        verify(customerRepository, never()).save(any(Customer.class));
        verify(eventProducer, never()).publishCustomerCreated(any(Customer.class));
    }
    @Test
    void getAllCustomers_Success() {
        when(customerRepository.findAll())
                .thenReturn(Flux.just(customer));
        StepVerifier.create(customerService.getAllCustomers())
                .expectNext(customer)
                .verifyComplete();
        verify(customerRepository).findAll();
    }
    @Test
    void getCustomerById_Success() {
        when(customerRepository.findById("1"))
                .thenReturn(Mono.just(customer));
        StepVerifier.create(customerService.getCustomerById("1"))
                .expectNext(customer)
                .verifyComplete();
        verify(customerRepository).findById("1");
    }
    @Test
    void getCustomerByType_Success() {
        when(customerRepository.findByCustomerType(CustomerType.PERSONAL))
                .thenReturn(Flux.just(customer));
        StepVerifier.create(customerService.getCustomerByType(CustomerType.PERSONAL))
                .expectNext(customer)
                .verifyComplete();
        verify(customerRepository).findByCustomerType(CustomerType.PERSONAL);
    }
    @Test
    void updateCustomer_Success() {
        Customer updatedCustomer = Customer.builder()
                .id(customer.getId())
                .fullName("John Updated")
                .documentNumber(customer.getDocumentNumber())
                .customerType(customer.getCustomerType())
                .email("john.updated@example.com")
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .status(customer.getStatus())
                .build();
        when(customerRepository.findById("1"))
                .thenReturn(Mono.just(customer));
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Mono.just(updatedCustomer));
        StepVerifier.create(customerService.updateCustomer("1", updatedCustomer))
                .expectNext(updatedCustomer)
                .verifyComplete();
        verify(customerRepository).findById("1");
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void updateVipPymStatus_PersonalCustomer_Success() {
        Customer vipCustomer = Customer.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .documentNumber(customer.getDocumentNumber())
                .customerType(customer.getCustomerType())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .status(customer.getStatus())
                .isVip(true)
                .build();
        when(customerRepository.findById("1"))
                .thenReturn(Mono.just(customer));
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Mono.just(vipCustomer));
        StepVerifier.create(customerService.updateVipPymStatus("1", true))
                .expectNext(vipCustomer)
                .verifyComplete();
        verify(customerRepository).findById("1");
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void updateVipPymStatus_BusinessCustomer_Success() {
        Customer businessCustomer = Customer.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .documentNumber(customer.getDocumentNumber())
                .customerType(CustomerType.BUSINESS)
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .status(customer.getStatus())
                .build();
        Customer pymCustomer = Customer.builder()
                .id(businessCustomer.getId())
                .fullName(businessCustomer.getFullName())
                .documentNumber(businessCustomer.getDocumentNumber())
                .customerType(businessCustomer.getCustomerType())
                .email(businessCustomer.getEmail())
                .phone(businessCustomer.getPhone())
                .createdAt(businessCustomer.getCreatedAt())
                .status(businessCustomer.getStatus())
                .isPym(true)
                .build();
        when(customerRepository.findById("1"))
                .thenReturn(Mono.just(businessCustomer));
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Mono.just(pymCustomer));
        StepVerifier.create(customerService.updateVipPymStatus("1", true))
                .expectNext(pymCustomer)
                .verifyComplete();
        verify(customerRepository).findById("1");
        verify(customerRepository).save(any(Customer.class));
    }
    @Test
    void deleteCustomer_Success() {
        Customer deletedCustomer = Customer.builder()
                .id(customer.getId())
                .fullName(customer.getFullName())
                .documentNumber(customer.getDocumentNumber())
                .customerType(customer.getCustomerType())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .createdAt(customer.getCreatedAt())
                .modifiedAt(LocalDateTime.now())
                .status("DELETED")
                .build();
        when(customerRepository.findById("1"))
                .thenReturn(Mono.just(customer));
        when(customerRepository.save(any(Customer.class)))
                .thenReturn(Mono.just(deletedCustomer));
        StepVerifier.create(customerService.deleteCustomer("1"))
                .expectNext(deletedCustomer)
                .verifyComplete();
        verify(customerRepository).findById("1");
        verify(customerRepository).save(any(Customer.class));
    }
}