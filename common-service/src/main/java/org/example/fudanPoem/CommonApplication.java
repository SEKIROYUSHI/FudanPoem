package org.example.fudanPoem;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.example.fudanPoem")
public class CommonApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(CommonApplication.class, args);
    }
}
