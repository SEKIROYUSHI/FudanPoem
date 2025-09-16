package org.example.fudanPoem;
import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan("org.example.fudanPoem")
@MapperScan("org.example.fudanPoem.mapper")
public class ChatApplication {
    public static void main(String[] args) {
       org.springframework.boot.SpringApplication.run(ChatApplication.class, args);
    }
}