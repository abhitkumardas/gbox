package com.ru.gbox.models;

import lombok.Data;

import java.io.Serializable;

@Data
public class SignUpReq implements Serializable {
    private String fullName;
    private String email;
    private String mobile;
    private String password;

}
