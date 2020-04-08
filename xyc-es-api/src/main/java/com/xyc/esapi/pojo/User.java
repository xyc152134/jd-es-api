package com.xyc.esapi.pojo;

import lombok.Data;

@Data
public class User {
    public  String name;
    public  int age;

    public User(String name, int age) {
        this.name = name;
        this.age = age;
    }
}
