package com.example.gbsports.request;

import lombok.Data;

@Data
public class ResetMKRequest {
    private String token;
    private String newPassword;
}
