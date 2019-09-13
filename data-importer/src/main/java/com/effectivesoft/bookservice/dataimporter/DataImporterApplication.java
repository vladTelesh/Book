package com.effectivesoft.bookservice.dataimporter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.context.annotation.ComponentScan;

import java.io.File;
import java.text.ParseException;

@SpringBootApplication
@ComponentScan({"com.effectivesoft.bookservice.dataimporter", "com.effectivesoft.bookservice.core"})
public class DataImporterApplication implements CommandLineRunner {

    private static final String DEFAULT_CSV_FILE_NAME = "example.csv";

    @Value("${directory}")
    private String directory;
    private BookImportService service;

    DataImporterApplication(BookImportService service){
        this.service = service;
    }


    public static void main(String[] args) {
        ConfigurableApplicationContext context = SpringApplication.run(DataImporterApplication.class, args);
        context.close();
    }

    @Override
    public void run(String... args) throws ParseException {
        String fileName;
        if (args.length == 1) {
            fileName = args[0];
        } else {
            fileName = DEFAULT_CSV_FILE_NAME;
        }
        File file = new File("./books/example.csv");

        service.importBookFromCsv(file);
    }
}
