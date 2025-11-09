package com.group6.fieldgo.model;

import lombok.Data;
import lombok.Getter;

@Data
@Getter
public class ApiResponse<T> {
    private boolean success;
    private String message;
    private T data;
}
