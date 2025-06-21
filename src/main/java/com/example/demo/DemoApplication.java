package com.example.demo;

import com.example.demo.entities.Product;
import com.example.demo.repositories.ProductRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.context.annotation.Bean;

@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class DemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(DemoApplication.class, args);
    }

    @Bean
    public CommandLineRunner start(ProductRepository repository) {
        return args -> {
            Product p1 = Product.builder()
                    .name("Adam")
                    .price(9.99)
                    .qte(5.0)
                    .build();

            Product p2 = Product.builder()
                    .name("Eve")
                    .price(14.99)
                    .qte(3.0)
                    .build();

            Product p3 = Product.builder()
                    .name("John")
                    .price(7.49)
                    .qte(10.0)
                    .build();

            repository.saveAll(java.util.List.of(p1, p2, p3));

            repository.findAll().forEach(System.out::println);
        };
    }
}
