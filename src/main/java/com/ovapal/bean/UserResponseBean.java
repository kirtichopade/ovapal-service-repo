package com.ovapal.bean;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserResponseBean {
    private Long userId;
    private String name;
    private String email;
    // password is deliberately excluded from response
    private Integer age;
} 