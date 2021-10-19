package ru.netcracker.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
public class BackendApplication {


    public static void main(String[] args) {
        SpringApplication.run(BackendApplication.class, args);
        /*BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String pass1 = encoder.encode("lol");
        String pass2 = encoder.encode("123");
        System.out.println(pass1);
        System.out.println(pass2);*/
    }

}
