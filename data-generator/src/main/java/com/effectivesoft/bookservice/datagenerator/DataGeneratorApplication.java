package com.effectivesoft.bookservice.datagenerator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.context.ConfigurableApplicationContext;

@SpringBootApplication(exclude={DataSourceAutoConfiguration.class})
public class DataGeneratorApplication implements CommandLineRunner {

    private static final String DEFAULT_CSV_FILE_NAME = "example.csv";

    private final BookCsvGenerator generator;

    public DataGeneratorApplication(@Autowired BookCsvGenerator generator) {
        this.generator = generator;
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DataGeneratorApplication.class, args);
        context.close();
    }

    @Override
    public void run(String... args) {
        generator.generateBooks(DEFAULT_CSV_FILE_NAME);
    }
}