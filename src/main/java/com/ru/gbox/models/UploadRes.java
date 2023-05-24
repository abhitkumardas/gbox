package com.ru.gbox.models;

import lombok.Builder;
import lombok.Data;

import java.io.Serializable;

@Data
public class UploadRes implements Serializable {
    private int status;
    private String fileName;
}
