package com.bank.customerservice.model.customer;

import lombok.*;
import nonapi.io.github.classgraph.json.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "customers")
public class Customer {
    @Id
    private String id;
    private String fullName;
    private String documentNumber;
    private CustomerType customerType;
    private String email;
    private String phone;
    private LocalDateTime createdAt;
    private LocalDateTime modifiedAt;
    private String status;

    //Only for special profiles
    private boolean isVip;
    private boolean isPym;
}
