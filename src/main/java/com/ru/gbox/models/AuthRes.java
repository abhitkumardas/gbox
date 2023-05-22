package com.ru.gbox.models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
public class AuthRes implements Serializable {
    private String token;
    private Date expiresIn;
}
