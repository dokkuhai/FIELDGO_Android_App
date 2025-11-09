package com.group6.fieldgo.model;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class UserProfileResponse {
    private String name;
    private String email;
    private String phone;
}
