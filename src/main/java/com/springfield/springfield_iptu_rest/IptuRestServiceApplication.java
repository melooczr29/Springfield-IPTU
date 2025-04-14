package com.springfield.springfield_iptu_rest;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@SpringBootApplication
@EnableFeignClients
public class IptuRestServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(IptuRestServiceApplication.class, args);
    }
}