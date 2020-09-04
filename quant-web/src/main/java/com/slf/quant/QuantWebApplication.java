package com.slf.quant;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

@EnableAutoConfiguration
@ComponentScan(basePackages = "com.slf.quant")
@MapperScan("com.slf.quant.dao")
public class QuantWebApplication
{
    public static void main(String[] args)
    {
        SpringApplication.run(QuantWebApplication.class, args);
    }
}
