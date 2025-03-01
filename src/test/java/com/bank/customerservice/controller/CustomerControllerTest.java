package com.bank.customerservice.controller;
import com.bank.customerservice.model.customer.Customer;
import com.bank.customerservice.model.customer.CustomerType;
import com.bank.customerservice.service.CustomerService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
@ExtendWith(MockitoExtension.class)
class CustomerControllerTest {
    @Mock
    private CustomerService customerService;
    @InjectMocks
    private CustomerController customerController;
    private WebTestClient webTestClient;
    private Customer customer;
    @BeforeEach
    void setUp() {
        webTestClient = WebTestClient.bindToController(customerController).build();
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
        when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(Mono.just(customer));
        webTestClient.post()
                .uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.status").isEqualTo(201)
                .jsonPath("$.message").isEqualTo("Customer successfully created")
                .jsonPath("$.data.id").isEqualTo(customer.getId());
    }
    @Test
    void createCustomer_Error() {
        when(customerService.createCustomer(any(Customer.class)))
                .thenReturn(Mono.error(new RuntimeException("Customer with this document number already exists")));
        webTestClient.post()
                .uri("/api/customers")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .jsonPath("$.status").isEqualTo(400)
                .jsonPath("$.message").isEqualTo("Customer with this document number already exists")
                .jsonPath("$.data").isEqualTo(null);
    }
    @Test
    void getAllCustomers_Success() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerService.getAllCustomers())
                .thenReturn(Flux.fromIterable(customers));
        webTestClient.get()
                .uri("/api/customers")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("Customers retrieved successfully")
                .jsonPath("$.data[0].id").isEqualTo(customer.getId());
    }
    @Test
    void getCustomerById_Success() {
        when(customerService.getCustomerById(anyString()))
                .thenReturn(Mono.just(customer));
        webTestClient.get()
                .uri("/api/customers/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("Customer details retrieved successfully")
                .jsonPath("$.data.id").isEqualTo(customer.getId());
    }
    @Test
    void getCustomerById_NotFound() {
        when(customerService.getCustomerById(anyString()))
                .thenReturn(Mono.empty());
        webTestClient.get()
                .uri("/api/customers/{id}", "1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found")
                .jsonPath("$.data").isEqualTo(null);
    }
    @Test
    void getCustomerByType_Success() {
        List<Customer> customers = Arrays.asList(customer);
        when(customerService.getCustomerByType(any(CustomerType.class)))
                .thenReturn(Flux.fromIterable(customers));
        webTestClient.get()
                .uri("/api/customers/type/{type}", CustomerType.PERSONAL)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("Customers retrieved successfully")
                .jsonPath("$.data[0].id").isEqualTo(customer.getId());
    }
    @Test
    void updateCustomer_Success() {
        when(customerService.updateCustomer(anyString(), any(Customer.class)))
                .thenReturn(Mono.just(customer));
        webTestClient.put()
                .uri("/api/customers/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("Customer successfully update")
                .jsonPath("$.data.id").isEqualTo(customer.getId());
    }
    @Test
    void updateCustomer_NotFound() {
        when(customerService.updateCustomer(anyString(), any(Customer.class)))
                .thenReturn(Mono.empty());
        webTestClient.put()
                .uri("/api/customers/{id}", "1")
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(customer)
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found")
                .jsonPath("$.data").isEqualTo(null);
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
                .status("DELETED")
                .modifiedAd(LocalDateTime.now())
                .build();
        when(customerService.deleteCustomer(anyString()))
                .thenReturn(Mono.just(deletedCustomer));
        webTestClient.delete()
                .uri("/api/customers/{id}", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("Customer successfully deleted (soft delete)")
                .jsonPath("$.data.status").isEqualTo("DELETED");
    }
    @Test
    void deleteCustomer_NotFound() {
        when(customerService.deleteCustomer(anyString()))
                .thenReturn(Mono.empty());
        webTestClient.delete()
                .uri("/api/customers/{id}", "1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found")
                .jsonPath("$.data").isEqualTo(null);
    }
    @Test
    void updateVipPymStatus_Success() {
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

        Customer updatedCustomer = Customer.builder()
                .id(businessCustomer.getId())
                .fullName(businessCustomer.getFullName())
                .documentNumber(businessCustomer.getDocumentNumber())
                .customerType(businessCustomer.getCustomerType())
                .email(businessCustomer.getEmail())
                .phone(businessCustomer.getPhone())
                .createdAt(businessCustomer.getCreatedAt())
                .status(businessCustomer.getStatus())
                .isPym(false)
                .isVip(true)
                .build();

        when(customerService.updateVipPymStatus(anyString(), any(Boolean.class)))
                .thenReturn(Mono.just(updatedCustomer));

        webTestClient.put()
                .uri("/api/customers/{customerId}/vip-pym/status?isVipPym=true", "1")
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.status").isEqualTo(200)
                .jsonPath("$.message").isEqualTo("Customer successfully update")
                .jsonPath("$.data.vip").isEqualTo(updatedCustomer.isVip());
    }
    @Test
    void updateVipPymStatus_NotFound() {
        when(customerService.updateVipPymStatus(anyString(), any(Boolean.class)))
                .thenReturn(Mono.empty());
        webTestClient.put()
                .uri("/api/customers/{customerId}/vip-pym/status?isVipPym=true", "1")
                .exchange()
                .expectStatus().isNotFound()
                .expectBody()
                .jsonPath("$.status").isEqualTo(404)
                .jsonPath("$.message").isEqualTo("Customer not found")
                .jsonPath("$.data").isEqualTo(null);
    }
}