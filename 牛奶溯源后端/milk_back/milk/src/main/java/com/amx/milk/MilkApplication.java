package com.amx.milk;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@MapperScan("com.amx.milk.dao")
@SpringBootApplication
public class MilkApplication {

    public static void main(String[] args) {
        SpringApplication.run(MilkApplication.class, args);
    }

}
