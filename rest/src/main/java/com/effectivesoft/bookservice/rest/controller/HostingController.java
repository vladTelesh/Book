package com.effectivesoft.bookservice.rest.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;

import java.io.*;

@RestController
@RequestMapping(value = "/api/v1/hosting", produces = MediaType.APPLICATION_JSON_VALUE)
public class HostingController {
    @Value("${image.directory}")
    String imageDirectory;

    private static final Logger logger = LoggerFactory.getLogger(HostingController.class);

    @GetMapping(value = "/images/{type}/{itemId}/{fileName}")
    public byte[] readImage(@PathVariable("itemId") String itemId,
                            @PathVariable("fileName") String fileName,
                            @PathVariable("type") String type) {
        File file = new File(imageDirectory + type + "\\" + itemId + "\\" + fileName);
        byte[] byteArray = new byte[(int) file.length()];

        try (FileInputStream fis = new FileInputStream(file)) {
            fis.read(byteArray);
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return byteArray;
    }
}
