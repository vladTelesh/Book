package com.effectivesoft.bookservice.rest.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;

import javax.mail.internet.MimeMessage;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MailSender implements Runnable {
    private JavaMailSender javaMailSender;
    private String link;
    private String code;
    private String to;

    private static final Logger logger = LoggerFactory.getLogger(MailSender.class);

    MailSender(String to, String code, String link, JavaMailSender javaMailSender) {
        this.link = link;
        this.javaMailSender = javaMailSender;
        this.to = to;
        this.code = code;
    }

    @Override
    public void run() {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        try {
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "utf-8");
            mimeMessage.setContent(readTemplate(), "text/html");
            mimeMessageHelper.setTo(to);
            mimeMessageHelper.setSubject("Confirm your account!");
            javaMailSender.send(mimeMessage);
        } catch (Exception e) {
            logger.error(e.getMessage());
        }
    }

    private String readTemplate() {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            BufferedReader bufferedReader = new BufferedReader(new FileReader("D:/Projects/es-book-service/rest/src/main/resources/template/email_template.html"));
            String string;
            while ((string = bufferedReader.readLine()) != null) {
                contentBuilder.append(string);
            }
            bufferedReader.close();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        return contentBuilder.toString().replace("${confirmation_link}", link + "/confirm/" + code);
    }
}
