package com.fof.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class ErrorResponse {

    private boolean success;

    private String error;

    private LocalDateTime timestamp;
}