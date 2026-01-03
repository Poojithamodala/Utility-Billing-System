package com.utility.dto;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerRegistrationRequestResponse {

    private String id;
    private String name;
    private String email;
    private String phone;
    private String address;
    private RequestStatus status;
    private LocalDateTime createdAt;
}