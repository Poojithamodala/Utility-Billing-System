package com.utility.model;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.utility.dto.RequestStatus;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Document(collection = "consumer_requests")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ConsumerRegistrationRequest {

    @Id
    private String id;

    private String name;
    private String email;
    private String phone;
    private String address;

    private RequestStatus status; // PENDING, APPROVED, REJECTED

    private LocalDateTime createdAt;
    private LocalDateTime reviewedAt;
    
    private String rejectionReason;
}
