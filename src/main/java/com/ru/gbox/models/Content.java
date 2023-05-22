package com.ru.gbox.models;

import lombok.Data;

import java.io.Serializable;
import java.util.Date;

@Data
public class Content implements Serializable {
    private String name;
    private String type;
    private boolean isDir;
    private Long size;
    private Date lastModifiedAt;
}
