package com.group6.fieldgo.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@AllArgsConstructor
public class UpdateProfileRequest {
    private String name;
    private String phone;

}
