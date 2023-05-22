package com.ru.gbox.models;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
public class ContentResponse implements Serializable {
    List<Content> contents;
    String curDir;
}
