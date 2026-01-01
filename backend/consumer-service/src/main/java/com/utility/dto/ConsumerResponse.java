package com.utility.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class ConsumerResponse {

    private String id;
    private String name;
    private String username;
    private String email;
    private String phone;
    private String address;
}
